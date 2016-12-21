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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.depositing.DepositingException;
import de.mpg.mpdl.inge.pubman.exceptions.PubManException;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;
import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

/**
 * Main class to provide SWORD Server functionality.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PubManSwordServer {
  private Logger log = Logger.getLogger(PubManSwordServer.class);
  private AccountUserVO currentUser;
  private String verbose = "";

  /**
   * Process the deposit.
   * 
   * @param deposit
   * @param collection
   * @return DepositResponse
   * @throws Exception
   * @throws ItemInvalidException
   * @throws ValidationException
   * @throws NamingException
   * @throws SWORDContentTypeException
   * @throws URISyntaxException
   * @throws TechnicalException
   * @throws PubManException
   * @throws SecurityException
   * @throws DepositingException
   * @throws AuthorizationException
   */
  public DepositResponse doDeposit(Deposit deposit, String collection) throws ItemInvalidException,
      ContentStreamNotFoundException, NamingException, ValidationException,
      SWORDContentTypeException, AuthorizationException, DepositingException, SecurityException,
      PubManException, TechnicalException, URISyntaxException {
    SwordUtil util = new SwordUtil();
    PubItemVO depositItem = null;
    DepositResponse dr = new DepositResponse(Deposit.ACCEPTED);
    boolean valid = false;

    this.setVerbose("Start depositing process ... ");

    // Create item
    util.setCurrentDeposit(deposit);
    depositItem = util.readZipFile(deposit.getFile(), this.currentUser);
    this.setVerbose("Escidoc Publication Item successfully created.");
    ContextRO context = new ContextRO();
    context.setObjectId(collection);
    depositItem.setContext(context);

    // Validate Item
    util.getItemControllerSessionBean().setCurrentPubItem(new PubItemVOPresentation(depositItem));
    ValidationReportVO validationReport = util.validateItem(depositItem);
    if (validationReport.isValid()) {
      this.setVerbose("Escidoc Publication Item successfully validated.");
      valid = true;
    } else {
      this.setVerbose("Following validation error(s) occurred: " + validationReport);
      valid = false;
      throw new ItemInvalidException(validationReport);
    }

    // Deposit item
    if (!deposit.isNoOp() && valid) {
      depositItem = util.doDeposit(this.currentUser, depositItem);
      if (depositItem.getVersion().getState().equals(State.RELEASED)) {
        dr = new DepositResponse(Deposit.CREATED);
        this.setVerbose("Escidoc Publication Item successfully deposited " + "(state: "
            + depositItem.getPublicStatus() + ").");
      } else {
        dr = new DepositResponse(Deposit.ACCEPTED);
        this.setVerbose("Escidoc Publication Item successfully deposited " + "(state: "
            + depositItem.getPublicStatus() + ").");
      }
    } else {
      if (valid) {
        this.setVerbose("Escidoc Publication Item not deposited due to X_NO_OP=true.");
      } else {
        this.setVerbose("Escidoc Publication Item not deposited due to validation errors.");
      }
    }

    SWORDEntry se = util.createResponseAtom(depositItem, deposit, valid);
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
  public String doServiceDocument(ServiceDocumentRequest sdr) throws SWORDAuthenticationException,
      ParserConfigurationException, TransformerException {
    SwordUtil util = new SwordUtil();
    List<PubContextVOPresentation> contextList = null;
    ContextListSessionBean contextListBean =
        (ContextListSessionBean) FacesBean.getSessionBean(ContextListSessionBean.class);
    contextListBean.init();
    contextList = contextListBean.getDepositorContextList();
    DocumentBuilder documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

    // Create and return the PubMan ServiceDocument
    Document document = documentBuilder.newDocument();
    Element service = document.createElementNS("http://www.w3.org/2007/app", "service");
    Element version = document.createElementNS("http://purl.org/net/sword/", "version");
    version.setPrefix("sword");
    version.setTextContent("1.3");
    Element workspace = document.createElement("workspace");
    Element wsTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
    wsTitle.setPrefix("atom");
    wsTitle.setTextContent("PubMan SWORD Workspace");
    workspace.appendChild(wsTitle);

    // Add all collections to workspace
    for (int i = 0; i < contextList.size(); i++) {
      PubContextVOPresentation pubContext = contextList.get(i);

      Element collection = document.createElement("collection");
      collection.setAttribute("href", pubContext.getReference().getObjectId());
      Element colTitle = document.createElementNS("http://www.w3.org/2005/Atom", "title");
      colTitle.setPrefix("atom");
      colTitle.setTextContent(pubContext.getName());
      Element abst = document.createElementNS("http://purl.org/dc/terms/", "abstract");
      abst.setPrefix("dcterms");
      abst.setTextContent(pubContext.getDescription());
      Element med = document.createElementNS("http://purl.org/net/sword/", "mediation");
      med.setPrefix("sword");
      med.setTextContent("false");
      Element policy = document.createElementNS("http://purl.org/net/sword/", "collectionPolicy");
      policy.setPrefix("sword");
      policy.setTextContent(util.getWorkflowAsString(pubContext));
      // static value
      Element treat = document.createElementNS("http://purl.org/net/sword/", "treatment");
      treat.setPrefix("sword");
      treat.setTextContent(util.getTreatmentText());

      collection.appendChild(colTitle);
      collection.appendChild(abst);
      collection.appendChild(med);
      collection.appendChild(policy);
      collection.appendChild(treat);

      // static value
      for (int x = 0; x < util.Packaging.length; x++) {
        Element format = document.createElementNS("http://purl.org/net/sword/", "acceptPackaging");
        format.setPrefix("sword");
        format.setTextContent(util.Packaging[x]);
        collection.appendChild(format);
      }

      // collection.appendChild(format1);
      // collection.appendChild(format2);
      // collection.appendChild(format3);
      // collection.appendChild(format4);

      workspace.appendChild(collection);
    }

    service.appendChild(version);
    service.appendChild(workspace);

    document.appendChild(service);

    // Transform to xml
    Transformer transformer =
        TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
            .newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(document);
    transformer.transform(source, result);
    String xmlString = result.getWriter().toString();

    return xmlString;
  }

  public AccountUserVO getCurrentUser() {
    return this.currentUser;
  }

  public void setCurrentUser(AccountUserVO currentUser) {
    this.currentUser = currentUser;
  }

  public String getVerbose() {
    return this.verbose;
  }

  public void setVerbose(String verbose) {
    this.verbose += verbose + "\n";
  }

  public String getBaseURL() {
    try {
      return PropertyReader.getProperty("escidoc.pubman.instance.url");
    } catch (Exception e) {
      this.log.warn("Base URL could not be read from property file.", e);
    }
    return "";
  }

  public String getCoreserviceURL() {
    try {
      return PropertyReader.getProperty("escidoc.framework_access.framework.url");
    } catch (Exception e) {
      this.log.warn("Coreservice URL could not be read from property file.", e);
    }
    return "";
  }
}
