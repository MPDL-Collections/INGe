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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.List;

import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDContentTypeException;
import org.purl.sword.base.SWORDEntry;
import org.purl.sword.base.ServiceDocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationServiceException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Main class to provide SWORD Server functionality.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PubManSwordServer {
  private static final Logger logger = Logger.getLogger(PubManSwordServer.class);

  private Principal currentUser;
  private String verbose = "";

  @Autowired
  private ItemValidatingService itemValidatingService;

  /**
   * Process the deposit.
   * 
   * @param deposit
   * @param collection
   * @return DepositResponse
   * @throws ContentStreamNotFoundException
   * @throws Exception
   * @throws ValidationServiceException
   * @throws NamingException
   * @throws SWORDContentTypeException
   * @throws IngeEsServiceException
   * @throws AuthenticationException
   * @throws PubItemAlreadyReleasedException
   * @throws PubItemNotFoundException
   * @throws PubCollectionNotFoundException
   * @throws PubItemStatusInvalidException
   * @throws PubItemLockedException
   * @throws PubItemMandatoryAttributesMissingException
   * @throws URISyntaxException
   * @throws TechnicalException
   * @throws PubManException
   * @throws SecurityException
   * @throws DepositingException
   * @throws AuthorizationException
   * @throws ValidationException
   */
  public DepositResponse doDeposit(Deposit deposit, String collection)
      throws SWORDContentTypeException, AuthenticationException, IngeTechnicalException, IngeApplicationException,
      de.mpg.mpdl.inge.service.exceptions.AuthorizationException, ValidationException, ValidationServiceException {

    final SwordUtil util = new SwordUtil();
    ItemVersionVO depositItem = null;
    DepositResponse dr = new DepositResponse(Deposit.ACCEPTED);

    this.setVerbose("Start depositing process ... ");

    // Create item
    util.setCurrentDeposit(deposit);
    depositItem = util.readZipFile(deposit.getFile(), this.currentUser);
    this.setVerbose("Escidoc Publication Item successfully created.");
    final ContextDbRO context = new ContextDbRO();
    context.setObjectId(collection);
    depositItem.getObject().setContext(context);

    util.getItemControllerSessionBean().setCurrentPubItem(new PubItemVOPresentation(depositItem));

    // Validate Item
    try {
      PubItemUtil.cleanUpItem(depositItem);
      this.itemValidatingService.validate(depositItem, ValidationPoint.STANDARD);
    } catch (final ValidationServiceException e) {
      this.setVerbose("Following validation error(s) occurred: " + e);
      throw e;
    } catch (final ValidationException e) {
      this.setVerbose("Following validation error(s) occurred: " + e.getReport());
      throw e;
    }

    this.setVerbose("Escidoc Publication Item successfully validated.");

    // Deposit item
    if (!deposit.isNoOp()) {
      depositItem = util.doDeposit(depositItem);
      if (ItemVersionRO.State.RELEASED.equals(depositItem.getVersionState())) {
        dr = new DepositResponse(Deposit.CREATED);
        this.setVerbose("Escidoc Publication Item successfully deposited " + "(state: " + depositItem.getObject().getPublicState() + ").");
      } else {
        dr = new DepositResponse(Deposit.ACCEPTED);
        this.setVerbose("Escidoc Publication Item successfully deposited " + "(state: " + depositItem.getObject().getPublicState() + ").");
      }
    } else {
      this.setVerbose("Escidoc Publication Item not deposited due to X_NO_OP=true.");
    }

    final SWORDEntry se = util.createResponseAtom(depositItem, deposit);
    if (deposit.isVerbose()) {
      se.setVerboseDescription(this.getVerbose());
    }
    dr.setEntry(se);

    return dr;
  }

  /**
   * Provides Service Document.
   * 
   * @param ServiceDocumentRequest
   * @return ServiceDocument
   * @throws SWORDAuthenticationException
   * @throws ParserConfigurationException
   * @throws TransformerException
   * @throws URISyntaxException
   * @throws IOException
   */
  public String doServiceDocument(ServiceDocumentRequest sdr)
      throws SWORDAuthenticationException, ParserConfigurationException, TransformerException {
    final SwordUtil util = new SwordUtil();
    List<PubContextVOPresentation> contextList = null;
    final ContextListSessionBean contextListBean = (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    contextListBean.init();
    contextList = contextListBean.getDepositorContextList();
    final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    // Create and return the PubMan ServiceDocument
    final Document document = documentBuilder.newDocument();
    final Element service = document.createElementNS("http://www.w3.org/2007/app", "service");
    final Element version = document.createElementNS("http://purl.org/net/sword/", "version");
    version.setPrefix("sword");
    version.setTextContent("1.3");
    final Element workspace = document.createElement("workspace");
    final Element wsTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
    wsTitle.setPrefix("atom");
    wsTitle.setTextContent("PubMan SWORD Workspace");
    workspace.appendChild(wsTitle);

    // Add all collections to workspace
    for (int i = 0; i < contextList.size(); i++) {
      final PubContextVOPresentation pubContext = contextList.get(i);

      final Element collection = document.createElement("collection");
      collection.setAttribute("href", pubContext.getObjectId());
      final Element colTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
      colTitle.setPrefix("atom");
      colTitle.setTextContent(pubContext.getName());
      final Element abst = document.createElementNS("http://purl.org/dc/terms/", "abstract");
      abst.setPrefix("dcterms");
      abst.setTextContent(pubContext.getDescription());
      final Element med = document.createElementNS("http://purl.org/net/sword/", "mediation");
      med.setPrefix("sword");
      med.setTextContent("false");
      final Element policy = document.createElementNS("http://purl.org/net/sword/", "collectionPolicy");
      policy.setPrefix("sword");
      policy.setTextContent(util.getWorkflowAsString(pubContext));
      // static value
      final Element treat = document.createElementNS("http://purl.org/net/sword/", "treatment");
      treat.setPrefix("sword");
      treat.setTextContent(util.getTreatmentText());

      collection.appendChild(colTitle);
      collection.appendChild(abst);
      collection.appendChild(med);
      collection.appendChild(policy);
      collection.appendChild(treat);

      // static value
      for (int x = 0; x < util.packaging.length; x++) {
        final Element format = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
        format.setPrefix("sword");
        format.setTextContent(util.packaging[x]);
        collection.appendChild(format);
      }

      workspace.appendChild(collection);
    }

    service.appendChild(version);
    service.appendChild(workspace);

    document.appendChild(service);

    // Transform to xml
    final Transformer transformer = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    final StreamResult result = new StreamResult(new StringWriter());
    final DOMSource source = new DOMSource(document);
    transformer.transform(source, result);
    final String xmlString = result.getWriter().toString();

    return xmlString;
  }

  public Principal getCurrentUser() {
    return this.currentUser;
  }

  public void setCurrentUser(Principal currentUser) {
    this.currentUser = currentUser;
  }

  private String getVerbose() {
    return this.verbose;
  }

  private void setVerbose(String verbose) {
    this.verbose += verbose + "\n";
  }

  public String getBaseURL() {
    try {
      return PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
    } catch (final Exception e) {
      PubManSwordServer.logger.warn("Base URL could not be read from property file.", e);
    }

    return "";
  }

  //  public String getCoreserviceURL() {
  //    try {
  //      return PropertyReader.getProperty(PropertyReader.ESCIDOC_FRAMEWORK_ACCESS_FRAMEWORK_URL);
  //    } catch (final Exception e) {
  //      PubManSwordServer.logger.warn("Coreservice URL could not be read from property file.", e);
  //    }
  //
  //    return "";
  //  }
}
