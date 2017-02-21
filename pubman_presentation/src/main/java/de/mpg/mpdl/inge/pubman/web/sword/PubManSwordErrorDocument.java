package de.mpg.mpdl.inge.pubman.web.sword;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

/**
 * This class implements the SWORD error document.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PubManSwordErrorDocument {

  private String href;
  private String summary = "";
  private int status;
  private swordError errorDesc;

  // [415] Wrong metadata format
  private static String ErrorContent = "http://purl.org/net/sword/error/ErrorContent";
  // [400] Error in request
  private static String ErrorBadRequest = "http://purl.org/net/sword/error/ErrorBadRequest";
  // [412]
  private static String MediationNotAllowed = "http://purl.org/net/sword/error/MediationNotAllowed";
  // [412]
  private static String ChecksumMismatch = "http://purl.org/escidoc/sword/error/ChecksumMismatch";
  // [400]
  private static String ValidationFailure = "http://purl.org/escidoc/sword/error/ValidationFailure";
  // [400] User not recognized
  private static String AuthentificationFailure =
      "http://purl.org/escidoc/sword/error/AuthentificationFailure";
  // [403] User has no depositing rights at all, or for the provided collection
  private static String AuthorisationFailure =
      "http://purl.org/escidoc/sword/error/AuthorisationFailure";
  // [500]
  private static String InternalError = "http://purl.org/escidoc/sword/error/InternalError";

  public PubManSwordErrorDocument() {}

  /**
   * Poosible errors during sword deposit.
   */
  public enum swordError {
    ErrorContent, ErrorBadRequest, MediationNotAllowed, ValidationFailure, AuthentificationFailure, AuthorisationFailure, InternalError, ChecksumMismatch
  }

  /**
   * Creation of the atom error document.
   * 
   * @return XML
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public String createErrorDoc() throws ParserConfigurationException, TransformerException {
    DocumentBuilder documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

    this.processError();
    Document document = documentBuilder.newDocument();
    Element error = document.createElementNS("http://purl.org/net/sword/", "error");
    error.setPrefix("sword");
    error.setAttribute("href", this.href);
    Element title = document.createElementNS("http://www.w3.org/2005/Atom", "title");
    title.setTextContent("Error Document");
    title.setPrefix("atom");
    Element updated = document.createElementNS("http://www.w3.org/2005/Atom", "updated");
    updated.setTextContent(this.getCurrentTimeAsString());
    updated.setPrefix("atom");
    Element summary = document.createElementNS("http://www.w3.org/2005/Atom", "summary");
    summary.setTextContent(this.summary);
    summary.setPrefix("atom");
    Element generator = document.createElementNS("http://www.w3.org/2005/Atom", "generator");
    generator.setPrefix("atom");
    PubManSwordServer server = new PubManSwordServer();
    generator.setTextContent(server.getBaseURL());
    Element treatment = document.createElementNS("http://purl.org/net/sword/", "treatment");
    treatment.setTextContent("Deposit failed");
    treatment.setPrefix("sword");

    error.appendChild(title);
    error.appendChild(updated);
    error.appendChild(summary);
    error.appendChild(generator);
    error.appendChild(treatment);

    document.appendChild(error);

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


  /**
   * Error classifiaction.
   */
  public void processError() {
    if (this.errorDesc.equals(swordError.AuthentificationFailure)) {
      this.setStatus(400);
      this.setHref(PubManSwordErrorDocument.AuthentificationFailure);
    }
    if (this.errorDesc.equals(swordError.AuthorisationFailure)) {
      this.setStatus(403);
      this.setHref(PubManSwordErrorDocument.AuthorisationFailure);
    }
    if (this.errorDesc.equals(swordError.ValidationFailure)) {
      this.setStatus(400);
      this.setHref(PubManSwordErrorDocument.ValidationFailure);
    }
    if (this.errorDesc.equals(swordError.MediationNotAllowed)) {
      this.setStatus(412);
      this.setHref(PubManSwordErrorDocument.MediationNotAllowed);
    }
    if (this.errorDesc.equals(swordError.ChecksumMismatch)) {
      this.setStatus(412);
      this.setHref(PubManSwordErrorDocument.ChecksumMismatch);
    }
    if (this.errorDesc.equals(swordError.ErrorBadRequest)) {
      this.setStatus(400);
      this.setHref(PubManSwordErrorDocument.ErrorBadRequest);
    }
    if (this.errorDesc.equals(swordError.ErrorContent)) {
      this.setStatus(415);
      this.setHref(PubManSwordErrorDocument.ErrorContent);
    }
    if (this.errorDesc.equals(swordError.InternalError)) {
      this.setStatus(500);
      this.setHref(PubManSwordErrorDocument.InternalError);
    }
  }

  // public PubManSwordErrorDocument getErrorStatus (PubManSwordErrorDocument errorDoc)
  // {
  // if (errorDoc.errorDesc.equals(swordError.AuthentificationFailure))
  // {
  // errorDoc.setStatus(400);
  // errorDoc.setHref(this.AuthentificationFailure);
  // }
  // if (errorDoc.errorDesc.equals(swordError.AuthorisationFailure))
  // {
  // errorDoc.setStatus(403);
  // errorDoc.setHref(this.AuthorisationFailure);
  // }
  // if (errorDoc.errorDesc.equals(swordError.ValidationFailure))
  // {
  // errorDoc.setStatus(400);
  // errorDoc.setHref(this.ValidationFailure);
  // }
  // if (errorDoc.errorDesc.equals(swordError.MediationNotAllowed))
  // {
  // errorDoc.setStatus(412);
  // errorDoc.setHref(this.MediationNotAllowed);
  // }
  // if (errorDoc.errorDesc.equals(swordError.ErrorBadRequest))
  // {
  // errorDoc.setStatus(400);
  // errorDoc.setHref(this.ErrorBadRequest);
  // }
  // if (errorDoc.errorDesc.equals(swordError.ErrorContent))
  // {
  // errorDoc.setStatus(415);
  // errorDoc.setHref(this.ErrorContent);
  // }
  // if (errorDoc.errorDesc.equals(swordError.InternalError))
  // {
  // errorDoc.setStatus(500);
  // errorDoc.setHref(this.InternalError);
  // }
  // return errorDoc;
  // }

  private String getCurrentTimeAsString() {
    Date date;
    String dateS;

    date = new Date(System.currentTimeMillis());
    dateS = date.toLocaleString();

    return dateS;
  }

  public String getHref() {
    return this.href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(String summary) {
    this.summary += summary;
  }

  public void resetSummary() {
    this.summary = "";
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public swordError getErrorDesc() {
    return this.errorDesc;
  }

  public void setErrorDesc(swordError errorDesc) {
    this.errorDesc = errorDesc;
  }
}
