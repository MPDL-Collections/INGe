/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.sword;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.ServiceDocumentRequest;
import org.w3.atom.Author;
import org.w3.atom.Content;
import org.w3.atom.Generator;
import org.w3.atom.Source;
import org.w3.atom.Summary;
import org.w3.atom.Title;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.exceptions.DepositingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemLockedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemMandatoryAttributesMissingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.exceptions.PubManException;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.transformation.util.Format;

import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * This class provides helper method for the SWORD Server implementation.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class SwordUtil extends FacesBean {
  // public static final String BEAN_NAME = "SwordUtil";
  private static final Logger logger = Logger.getLogger(SwordUtil.class);

  private static final String LOGIN_URL = "/aa/login";
  private static final String LOGOUT_URL = "/aa/logout/clear.jsp";
  private static final int NUMBER_OF_URL_TOKENS = 2;

  // private static final String acceptedFormat = "application/zip";
  private static final String itemPath = "/pubman/item/";
  private static final String mdFormatBibTex = "BibTex";
  private static final String mdFormatEndnote = "EndNote";
  private static final String mdFormatEscidoc =
      "http://purl.org/escidoc/metadata/schemas/0.1/publication";
  private static final String mdFormatPeerTEI = "http://purl.org/net/sword-types/tei/peer";
  private static final String treatmentText =
      "Zip archives recognised as content packages are opened and the individual files contained in them are stored.";
  private static final String[] fileEndings = {".xml", ".bib", ".tei", ".enl"};

  private Deposit currentDeposit;
  private String depositXml = "";
  private List<String> filenames = new ArrayList<String>();

  /**
   * Accepted packagings.
   */
  public String[] packaging = {mdFormatEscidoc, mdFormatBibTex, mdFormatEndnote, mdFormatPeerTEI};

  public SwordUtil() {
    this.init();
  }

  /**
   * Initialisation method.
   */
  public void init() {
    this.filenames.clear();
  }

  /**
   * Logs in a user.
   * 
   * @return AccountUserVO
   */
  public AccountUserVO checkUser(ServiceDocumentRequest sdr) {
    AccountUserVO userVO = null;

    // Forward http authentification
    if (sdr.getUsername() != null && sdr.getPassword() != null) {
      String username = sdr.getUsername();
      String pwd = sdr.getPassword();
      try {
        String handle = AdminHelper.loginUser(username, pwd);
        getLoginHelper().setESciDocUserHandle(handle);
        userVO = getLoginHelper().getAccountUser();
      } catch (Exception e) {
        logger.error(e);
        return null;
      }
    }

    return userVO;
  }

  /**
   * Checks if a user has depositing rights for a collection.
   * 
   * @param collection
   * @param user
   * @return true if the user has depositing rights, else false
   */
  public boolean checkCollection(String collection, AccountUserVO user) {
    List<PubContextVOPresentation> contextList = null;
    ContextListSessionBean contextListBean =
        (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    contextListBean.init();
    contextList = contextListBean.getDepositorContextList();
    for (int i = 0; i < contextList.size(); i++) {
      String context = contextList.get(i).getReference().getObjectId();
      if (context.toLowerCase().equals(collection.toLowerCase().trim())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Logs in the given user with the given password.
   * 
   * @param userid The id of the user to log in.
   * @param password The password of the user to log in.
   * @return The handle for the logged in user.
   * @throws HttpException
   * @throws IOException
   * @throws ServiceException
   * @throws URISyntaxException
   */
  @Deprecated
  public String loginUser(String userid, String password) throws HttpException, IOException,
      ServiceException, URISyntaxException {
    String frameworkUrl = PropertyReader.getFrameworkUrl();
    StringTokenizer tokens = new StringTokenizer(frameworkUrl, "//");
    if (tokens.countTokens() != NUMBER_OF_URL_TOKENS) {
      throw new IOException(
          "Url in the config file is in the wrong format, needs to be http://<host>:<port>");
    }
    tokens.nextToken();
    StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");

    if (hostPort.countTokens() != NUMBER_OF_URL_TOKENS) {
      throw new IOException(
          "Url in the config file is in the wrong format, needs to be http://<host>:<port>");
    }
    String host = hostPort.nextToken();
    int port = Integer.parseInt(hostPort.nextToken());

    HttpClient client = new HttpClient();

    client.getHostConfiguration().setHost(host, port, "http");
    client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

    PostMethod login = new PostMethod(frameworkUrl + "/aa/j_spring_security_check");
    login.addParameter("j_username", userid);
    login.addParameter("j_password", password);

    client.executeMethod(login);

    login.releaseConnection();
    CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
    Cookie[] logoncookies =
        cookiespec.match(host, port, "/", false, client.getState().getCookies());

    Cookie sessionCookie = logoncookies[0];

    PostMethod postMethod = new PostMethod(LOGIN_URL);
    postMethod.addParameter("target", frameworkUrl);
    client.getState().addCookie(sessionCookie);
    client.executeMethod(postMethod);

    if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode()) {
      throw new HttpException("Wrong status code: " + login.getStatusCode());
    }

    String userHandle = null;
    Header[] headers = postMethod.getResponseHeaders();
    for (int i = 0; i < headers.length; ++i) {
      if ("Location".equals(headers[i].getName())) {
        String location = headers[i].getValue();
        int index = location.indexOf('=');
        userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
      }
    }
    if (userHandle == null) {
      throw new ServiceException("User not logged in.");
    }
    return userHandle;
  }

  /**
   * @param fc
   * @throws IOException
   * @throws ServiceException
   * @throws URISyntaxException
   */
  public void logoutUser() throws IOException, ServiceException, URISyntaxException {
    FacesContext
        .getCurrentInstance()
        .getExternalContext()
        .redirect(
            PropertyReader.getFrameworkUrl()
                + LOGOUT_URL
                + "?target="
                + URLEncoder.encode(PropertyReader.getProperty("escidoc.pubman.instance.url")
                    + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                    + "?logout=true", "UTF-8"));
  }

  /**
   * Creates a Account User.
   * 
   * @param user
   * @param pwd
   * @return AccountUserVO
   */
  public AccountUserVO getAccountUser(String user, String pwd) {
    try {
      String handle = AdminHelper.loginUser(user, pwd);
      getLoginHelper().fetchAccountUser(handle);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return getLoginHelper().getAccountUser();
  }

  /**
   * This method takes a zip file and reads out the entries.
   * 
   * @param in
   * @throws TechnicalException
   * @throws NamingException
   * @throws SWORDContentTypeException
   */
  public PubItemVO readZipFile(InputStream in, AccountUserVO user)
      throws ContentStreamNotFoundException, SWORDContentTypeException {
    String item = null;
    List<FileVO> attachements = new ArrayList<FileVO>();
    PubItemVO pubItem = null;
    final int bufLength = 1024;
    char[] buffer = new char[bufLength];
    int readReturn;
    int count = 0;

    try {
      ZipEntry zipentry;
      ZipInputStream zipinputstream = new ZipInputStream(in);

      while ((zipentry = zipinputstream.getNextEntry()) != null) {
        count++;
        logger.debug("Processing zip entry file: " + zipentry.getName());

        String name = URLDecoder.decode(zipentry.getName(), "UTF-8");
        name = name.replaceAll("/", "_slsh_");
        this.filenames.add(name);
        boolean metadata = false;

        // check if the file is a metadata file
        for (int i = 0; i < fileEndings.length; i++) {

          String ending = fileEndings[i];
          if (name.endsWith(ending)) {
            metadata = true;
            // Retrieve the metadata

            StringWriter sw = new StringWriter();
            Reader reader = new BufferedReader(new InputStreamReader(zipinputstream, "UTF-8"));

            while ((readReturn = reader.read(buffer)) != -1) {
              sw.write(buffer, 0, readReturn);
            }

            item = new String(sw.toString());
            this.depositXml = item;
            pubItem = createItem(item, user);

            // if not escidoc format, add as component
            if (!this.currentDeposit.getFormatNamespace().equals(mdFormatEscidoc)) {
              attachements.add(convertToFileAndAdd(
                  new ByteArrayInputStream(item.getBytes("UTF-8")), name, user, zipentry));
            }
          }
        }

        if (!metadata) {
          attachements.add(convertToFileAndAdd(zipinputstream, name, user, zipentry));
        }

        zipinputstream.closeEntry();
      }
      zipinputstream.close();


      // Now add the components to the Pub Item (if they do not exist. If they exist, use the
      // existing component metadata and just change the content)
      for (FileVO newFile : attachements) {
        boolean existing = false;
        for (FileVO existingFile : pubItem.getFiles()) {
          if (existingFile.getName().replaceAll("/", "_slsh_").equals(newFile.getName())) {
            // file already exists, replace content
            existingFile.setContent(newFile.getContent());
            existingFile.getDefaultMetadata().setSize(newFile.getDefaultMetadata().getSize());
            existing = true;
          }
        }

        if (!existing) {
          pubItem.getFiles().add(newFile);
        }
      }

      // If peer format, add additional copyright information to component. They are read from the
      // TEI metadata.
      if (this.currentDeposit.getFormatNamespace().equals(mdFormatPeerTEI)) {
        // Copyright information are imported from metadata file
        // InitialContext initialContext = new InitialContext();

        StringWriter wr = new StringWriter();
        Transformer t =
            de.mpg.mpdl.inge.transformation.TransformerFactory.newInstance(FORMAT.PEER_TEI_XML,
                FORMAT.ESCIDOC_COMPONENT_XML);

        t.transform(new TransformerStreamSource(
            new ByteArrayInputStream(this.depositXml.getBytes())), new TransformerStreamResult(wr));

        String fileXml = wr.toString();

        try {
          FileVO transformdedFileVO = XmlTransformingService.transformToFileVO(fileXml);
          for (FileVO pubItemFile : pubItem.getFiles()) {
            pubItemFile.getDefaultMetadata().setRights(
                transformdedFileVO.getDefaultMetadata().getRights());
            pubItemFile.getDefaultMetadata().setCopyrightDate(
                transformdedFileVO.getDefaultMetadata().getCopyrightDate());
          }
        } catch (TechnicalException e) {
          logger.error("File Xml could not be transformed into FileVO. ", e);
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (count == 0) {
      logger.info("No zip file was provided.");
      throw new SWORDContentTypeException();
    }

    return pubItem;
  }

  /**
   * @param item
   * @param files
   * @return
   * @throws NamingException
   * @throws TechnicalException
   * @throws SWORDContentTypeException
   */
  private PubItemVO createItem(String item, AccountUserVO user) throws ItemInvalidException,
      ContentStreamNotFoundException, Exception {
    PubItemVO itemVO = null;

    if (item == null) {
      throw new ContentStreamNotFoundException();
    }

    try {
      // Format escidocFormat = new Format("escidoc-publication-item", "application/xml", "UTF-8");
      Format trgFormat = null;
      Boolean transform = false;

      // Transform from tei to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(mdFormatPeerTEI)) {
        trgFormat = new Format("peer_tei", "application/xml", "UTF-8");
        transform = true;
      }

      // Transform from bibtex to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(mdFormatBibTex)) {
        trgFormat = new Format("bibtex", "text/plain", "*");
        transform = true;
      }

      // Transform from endnote to escidoc-publication-item
      if (this.currentDeposit.getFormatNamespace().equalsIgnoreCase(mdFormatEndnote)) {
        trgFormat = new Format("endnote", "text/plain", "UTF-8");
        transform = true;
      }

      if (transform) {

        StringWriter wr = new StringWriter();
        Transformer t =
            de.mpg.mpdl.inge.transformation.TransformerFactory.newInstance(
                FORMAT.ESCIDOC_ITEM_V3_XML, trgFormat.toFORMAT());

        t.transform(new TransformerStreamSource(new ByteArrayInputStream(item.getBytes("UTF-8"))),
            new TransformerStreamResult(wr));

        item = wr.toString();
      }

      // Create item
      itemVO = XmlTransformingService.transformToPubItem(item);

      // Set Version to null in order to force PubItemPubItemService to create a new item.
      itemVO.setVersion(null);

      logger.debug("Item successfully created.");
    } catch (Exception e) {
      logger.error("Transformation to PubItem failed.", e);
      ValidationReportItemVO itemReport = new ValidationReportItemVO();
      itemReport.setContent("Error transforming item into eSciDoc Publication Item.");
      ValidationReportVO report = new ValidationReportVO();
      report.getItems().add(itemReport);
      throw new ItemInvalidException(report);
    }

    return itemVO;
  }

  /**
   * @param user
   * @param item
   * @return Saved pubItem
   * @throws NamingException
   * @throws AuthorizationException
   * @throws SecurityException
   * @throws TechnicalException
   * @throws URISyntaxException
   * @throws NamingException
   * @throws PubItemStatusInvalidException
   * @throws ItemInvalidException
   * @throws PubItemAlreadyReleasedException
   * @throws PubItemNotFoundException
   * @throws PubCollectionNotFoundException
   * @throws PubItemLockedException
   * @throws PubItemMandatoryAttributesMissingException
   * @throws ValidationException
   * @throws PubManException
   * @throws DepositingException
   */
  public PubItemVO doDeposit(AccountUserVO user, PubItemVO item) throws NamingException,
      PubItemStatusInvalidException, AuthorizationException,
      PubItemMandatoryAttributesMissingException, PubItemLockedException,
      PubCollectionNotFoundException, PubItemNotFoundException, PubItemAlreadyReleasedException,
      SecurityException, TechnicalException {

    PubItemVO depositedItem = null;
    String method = this.getMethod(item);

    if (method == null) {
      throw new PubItemStatusInvalidException(null, null);
    }

    if (method.equals("SAVE_SUBMIT") || method.equals("SUBMIT")) {
      depositedItem = PubItemService.savePubItem(item, user);
      depositedItem = PubItemService.submitPubItem(depositedItem, "", user);
    }

    if (method.equals("RELEASE")) {
      depositedItem = PubItemService.savePubItem(item, user);
      depositedItem = PubItemService.submitPubItem(depositedItem, "", user);
      depositedItem =
          PubItemService.releasePubItem(depositedItem.getVersion(),
              depositedItem.getModificationDate(), "", user);
    }

    return depositedItem;
  }

  /**
   * Returns the Workflow of the current context.
   */
  public PublicationAdminDescriptorVO.Workflow getWorkflow() {
    if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD)) {
      return Workflow.STANDARD;
    }

    if ((getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE)) {
      return Workflow.SIMPLE;
    }

    return null;
  }

  public String getMethod(PubItemVO item) {
    boolean isWorkflowStandard = false;
    boolean isWorkflowSimple = true;

    boolean isStatePending = true;
    boolean isStateSubmitted = false;
    boolean isStateInRevision = false;

    if (item != null && item.getVersion() != null && item.getVersion().getState() != null) {
      isStatePending = item.getVersion().getState().equals(State.PENDING);
      isStateSubmitted = item.getVersion().getState().equals(State.SUBMITTED);
      isStateInRevision = item.getVersion().getState().equals(State.IN_REVISION);
    }

    isWorkflowStandard =
        getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD;
    isWorkflowSimple =
        getItemControllerSessionBean().getCurrentContext().getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE;

    boolean isModerator = getLoginHelper().getAccountUser().isModerator(item.getContext());
    boolean isOwner = true;
    if (item.getOwner() != null) {
      isOwner =
          (getLoginHelper().getAccountUser().getReference() != null ? getLoginHelper()
              .getAccountUser().getReference().getObjectId().equals(item.getOwner().getObjectId())
              : false);
    }

    if ((isStatePending || isStateSubmitted) && isWorkflowSimple && isOwner) {
      return "RELEASE";
    }

    if ((isStatePending || isStateInRevision) && isWorkflowStandard && isOwner) {
      return "SAVE_SUBMIT";
    }

    if (((isStatePending || isStateInRevision) && isOwner) || (isStateSubmitted && isModerator)) {
      return "SUBMIT";
    }

    return null;
  }

  /**
   * Returns the Workflow for a given context.
   * 
   * @param pubContext
   * @return workflow type as string
   */
  public String getWorkflowAsString(PubContextVOPresentation pubContext) {
    boolean isWorkflowStandard =
        pubContext.getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.STANDARD;
    boolean isWorkflowSimple =
        pubContext.getAdminDescriptor().getWorkflow() == PublicationAdminDescriptorVO.Workflow.SIMPLE;

    if (isWorkflowStandard) {
      return "Standard Workflow";
    }

    if (isWorkflowSimple) {
      return "Simple Workflow";
    } else {
      return "";
    }
  }

  /**
   * Converts a byte[] into a FileVO.
   * 
   * @param file
   * @param name
   * @param user
   * @return FileVO
   * @throws Exception
   */
  private FileVO convertToFileAndAdd(InputStream zipinputstream, String name, AccountUserVO user,
      ZipEntry zipEntry) throws Exception {
    MdsFileVO mdSet = new MdsFileVO();
    FileVO fileVO = new FileVO();
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String mimeType = fileNameMap.getContentTypeFor(name);

    // Hack: FileNameMap class does not know tei, bibtex and endnote
    if (name.endsWith(".tei")) {
      mimeType = "application/xml";
    }
    if (name.endsWith(".bib") || name.endsWith(".enl")) {
      mimeType = "text/plain";
    }

    URL fileURL = this.uploadFile(zipinputstream, mimeType, user.getHandle(), zipEntry);

    if (fileURL != null && !fileURL.toString().trim().equals("")) {
      if (this.currentDeposit.getContentDisposition() != null
          && !this.currentDeposit.getContentDisposition().equals("")) {
        name = this.currentDeposit.getContentDisposition();
      }

      fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
      fileVO.setVisibility(FileVO.Visibility.PUBLIC);
      fileVO.setDefaultMetadata(mdSet);
      fileVO.getDefaultMetadata().setTitle(name);
      fileVO.setMimeType(mimeType);
      fileVO.setName(name);

      FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(mimeType);
      fileVO.getDefaultMetadata().getFormats().add(formatVO);
      fileVO.setContent(fileURL.toString());
      fileVO.getDefaultMetadata().setSize((int) zipEntry.getSize());
      // This is the provided metadata file which we store as a component
      if (!name.endsWith(".pdf")) {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class).warn(
                "WARNING: no content-category has been defined in Genres.xml");
          }
        }
        fileVO.setContentCategory(contentCategory);
      } else {
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("PUBLISHER_VERSION") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("PUBLISHER_VERSION");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class).warn(
                "WARNING: no content-category has been defined in Genres.xml");
          }
        }
        fileVO.setContentCategory(contentCategory);
      }
    }

    return fileVO;
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param InputStream to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected URL uploadFile(InputStream in, String mimetype, String userHandle, ZipEntry zipEntry)
      throws Exception {
    String fwUrl = PropertyReader.getFrameworkUrl();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    method.setRequestEntity(new InputStreamRequestEntity(in, -1));
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);

    HttpClient client = new HttpClient();
    client.executeMethod(method);
    String response = method.getResponseBodyAsString();

    return XmlTransformingService.transformUploadResponseToFileURL(response);
  }

  public SWORDEntry createResponseAtom(PubItemVO item, Deposit deposit) {
    SWORDEntry se = new SWORDEntry();
    PubManSwordServer server = new PubManSwordServer();

    // This info can only be filled if item was successfully created
    if (item != null) {
      Title title = new Title();
      title.setContent(item.getMetadata().getTitle());
      se.setTitle(title);

      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      TimeZone utc = TimeZone.getTimeZone("UTC");
      sdf.setTimeZone(utc);
      String milliFormat = sdf.format(new Date());
      se.setUpdated(milliFormat);
    }

    Summary s = new Summary();
    String filename = "";
    for (int i = 0; i < this.filenames.size(); i++) {
      if (filename.equals("")) {
        filename = filenames.get(i);
      } else {
        filename = filename + " ," + this.filenames.get(i);
      }
    }
    s.setContent(filename);
    se.setSummary(s);

    Content content = new Content();
    content.setSource("");

    // // Only set content if item was deposited
    if (!deposit.isNoOp() && item != null) {
      content.setSource(server.getCoreserviceURL() + "/ir/item/" + item.getVersion().getObjectId());
      se.setId(server.getBaseURL() + itemPath + item.getVersion().getObjectId());
    }
    se.setContent(content);

    Source source = new Source();
    Generator generator = new Generator();
    generator.setContent(server.getBaseURL());
    source.setGenerator(generator);
    se.setSource(source);
    se.setTreatment(treatmentText);
    se.setNoOp(deposit.isNoOp());

    Author author = new Author();
    author.setName(deposit.getUsername());
    se.addAuthors(author);

    return se;
  }

  public void validateItem(PubItemVO item) throws NamingException, ValidationException,
      ItemInvalidException {
    ItemValidatingService.validateItemObject(item, ValidationPoint.STANDARD);
  }

  public boolean checkMetadatFormat(String format) {
    for (int i = 0; i < this.packaging.length; i++) {
      String pack = this.packaging[i];
      if (format.equalsIgnoreCase(pack)) {
        return true;
      }
    }

    return false;
  }

  // public String getAcceptedFormat() {
  // return acceptedFormat;
  // }

  public String getTreatmentText() {
    return treatmentText;
  }

  // public Deposit getCurrentDeposit() {
  // return this.currentDeposit;
  // }

  public void setCurrentDeposit(Deposit currentDeposit) {
    this.currentDeposit = currentDeposit;
  }

  public ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }
}
