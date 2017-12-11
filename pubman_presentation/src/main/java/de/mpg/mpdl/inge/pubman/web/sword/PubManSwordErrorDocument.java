package de.mpg.mpdl.inge.pubman.web.sword;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class implements the SWORD error document.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class PubManSwordErrorDocument {
  // // [415] Wrong metadata format
  // private static final String ErrorContent = "http://purl.org/net/sword/error/ErrorContent";
  // // [400] Error in request
  // private static final String ErrorBadRequest =
  // "http://purl.org/net/sword/error/ErrorBadRequest";
  // // [412]
  // private static final String MediationNotAllowed =
  // "http://purl.org/net/sword/error/MediationNotAllowed";
  // // [412]
  // private static final String ChecksumMismatch =
  // "http://purl.org/escidoc/sword/error/ChecksumMismatch";
  // // [400]
  // private static final String ValidationFailure =
  // "http://purl.org/escidoc/sword/error/ValidationFailure";
  // // [400] User not recognized
  // private static final String AuthentificationFailure =
  // "http://purl.org/escidoc/sword/error/AuthentificationFailure";
  // // [403] User has no depositing rights at all, or for the provided collection
  // private static final String AuthorisationFailure =
  // "http://purl.org/escidoc/sword/error/AuthorisationFailure";
  // // [500]
  // private static final String InternalError =
  // "http://purl.org/escidoc/sword/error/InternalError";

  private String href;
  private String summary = "";
  private int status;
  private swordError errorDesc;

  public PubManSwordErrorDocument() {}

  /**
   * Poosible errors during sword deposit.
   */
  public enum swordError
  {
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
    final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    this.processError();
    final Document document = documentBuilder.newDocument();
    final Element error = document.createElementNS("http://purl.org/net/sword/", "error");
    error.setPrefix("sword");
    error.setAttribute("href", this.href);
    final Element title = document.createElementNS("http://www.w3.org/2005/Atom", "title");
    title.setTextContent("Error Document");
    title.setPrefix("atom");
    final Element updated = document.createElementNS("http://www.w3.org/2005/Atom", "updated");
    updated.setTextContent(this.getCurrentTimeAsString());
    updated.setPrefix("atom");
    final Element summary = document.createElementNS("http://www.w3.org/2005/Atom", "summary");
    summary.setTextContent(this.summary);
    summary.setPrefix("atom");
    final Element generator = document.createElementNS("http://www.w3.org/2005/Atom", "generator");
    generator.setPrefix("atom");
    final PubManSwordServer server = new PubManSwordServer();
    generator.setTextContent(server.getBaseURL());
    final Element treatment = document.createElementNS("http://purl.org/net/sword/", "treatment");
    treatment.setTextContent("Deposit failed");
    treatment.setPrefix("sword");

    error.appendChild(title);
    error.appendChild(updated);
    error.appendChild(summary);
    error.appendChild(generator);
    error.appendChild(treatment);

    document.appendChild(error);

    // Transform to xml
    final Transformer transformer = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    final StreamResult result = new StreamResult(new StringWriter());
    final DOMSource source = new DOMSource(document);
    transformer.transform(source, result);
    final String xmlString = result.getWriter().toString();

    return xmlString;
  }

  private void processError() {
    if (this.errorDesc.equals(swordError.AuthentificationFailure)) {
      this.setStatus(400);
      // this.setHref(PubManSwordErrorDocument.AuthentificationFailure);
    }

    if (this.errorDesc.equals(swordError.AuthorisationFailure)) {
      this.setStatus(403);
      // this.setHref(PubManSwordErrorDocument.AuthorisationFailure);
    }

    if (this.errorDesc.equals(swordError.ValidationFailure)) {
      this.setStatus(400);
      // this.setHref(PubManSwordErrorDocument.ValidationFailure);
    }

    if (this.errorDesc.equals(swordError.MediationNotAllowed)) {
      this.setStatus(412);
      // this.setHref(PubManSwordErrorDocument.MediationNotAllowed);
    }

    if (this.errorDesc.equals(swordError.ChecksumMismatch)) {
      this.setStatus(412);
      // this.setHref(PubManSwordErrorDocument.ChecksumMismatch);
    }

    if (this.errorDesc.equals(swordError.ErrorBadRequest)) {
      this.setStatus(400);
      // this.setHref(PubManSwordErrorDocument.ErrorBadRequest);
    }

    if (this.errorDesc.equals(swordError.ErrorContent)) {
      this.setStatus(415);
      // this.setHref(PubManSwordErrorDocument.ErrorContent);
    }

    if (this.errorDesc.equals(swordError.InternalError)) {
      this.setStatus(500);
      // this.setHref(PubManSwordErrorDocument.InternalError);
    }
  }

  private String getCurrentTimeAsString() {
    Date date;
    String dateS;

    date = new Date(System.currentTimeMillis());
    dateS = date.toLocaleString();

    return dateS;
  }

  // public String getHref() {
  // return this.href;
  // }

  // public void setHref(String href) {
  // this.href = href;
  // }

  // public String getSummary() {
  // return this.summary;
  // }

  public void setSummary(String summary) {
    this.summary += summary;
  }

  // public void resetSummary() {
  // this.summary = "";
  // }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  // public swordError getErrorDesc() {
  // return this.errorDesc;
  // }

  public void setErrorDesc(swordError errorDesc) {
    this.errorDesc = errorDesc;
  }
}
