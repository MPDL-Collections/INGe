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

package de.mpg.mpdl.inge.cone.formatter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.cone.ConeException;
import de.mpg.mpdl.inge.cone.Describable;
import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.TreeFragment;
import de.mpg.mpdl.inge.cone.util.RdfHelper;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Servlet to answer calls from the JQuery Javascript API.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class HtmlFormatter extends AbstractFormatter {

  private static final Logger logger = Logger.getLogger(HtmlFormatter.class);
  private static final String ERROR_TRANSFORMING_RESULT = "Error transforming result";
  private static final String DEFAULT_ENCODING = "UTF-8";

  @Override
  public String getContentType() {
    return "text/html;charset=" + DEFAULT_ENCODING;
  }

  /**
   * Send explain output to the client.
   * 
   * @param response
   * 
   * @throws FileNotFoundException
   * @throws TransformerFactoryConfigurationError
   * @throws IOException
   */
  public void explain(HttpServletResponse response)
      throws FileNotFoundException, TransformerFactoryConfigurationError, IOException, URISyntaxException {
    response.setContentType("text/xml");

    InputStream source =
        ResourceUtil.getResourceAsStream(PropertyReader.getProperty("inge.cone.modelsxml.path"), HtmlFormatter.class.getClassLoader());
    InputStream template = ResourceUtil.getResourceAsStream("explain/html_explain.xsl", HtmlFormatter.class.getClassLoader());

    try {
      Transformer transformer =
          TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTransformer(new StreamSource(template));
      transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
      transformer.transform(new StreamSource(source), new StreamResult(response.getWriter()));
    } catch (Exception e) {
      logger.error(ERROR_TRANSFORMING_RESULT, e);
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Formats an List&lt;Pair&gt; into an HTML list.
   * 
   * @param pairs A list of key-value pairs
   * @return A String formatted as HTML
   */
  public String formatQuery(List<? extends Describable> pairs, Model model) throws ConeException {

    String result = RdfHelper.formatList(pairs, model);
    StringWriter writer = new StringWriter();
    try {

      TransformerFactory factory1 = new net.sf.saxon.TransformerFactoryImpl();

      Transformer transformer = factory1.newTransformer(
          new StreamSource(ResourceUtil.getResourceAsStream("xslt/html/resultlist-html.xsl", HtmlFormatter.class.getClassLoader())));
      transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
      transformer.transform(new StreamSource(new StringReader(result)), new StreamResult(writer));
    } catch (TransformerException | FileNotFoundException e) {
      throw new ConeException(e);
    }
    return writer.toString();
  }

  /**
   * Formats an Map of triples into RDF.
   * 
   * @param triples The map of triples
   * 
   * @return A String formatted in HTML.
   * 
   * @throws IOException Any i/o exception
   */
  public String formatDetails(String id, Model model, TreeFragment triples, String lang) throws ConeException {

    String result = RdfHelper.formatMap(id, triples, model);
    StringWriter writer = new StringWriter();
    try {
      URL xsltFile = null;

      xsltFile = HtmlFormatter.class.getClassLoader().getResource("xslt/html/" + model.getName() + "-html.xsl");

      if (xsltFile == null) {
        logger.debug("No HTML template for '" + model.getName() + "' found, using generic template.");
        // xsltFile = ResourceUtil.getResourceAsStream("xslt/html/generic-html.xsl");
        xsltFile = HtmlFormatter.class.getClassLoader().getResource("xslt/html/generic-html.xsl");
      }


      TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

      Transformer transformer = factory.newTransformer(new StreamSource(xsltFile.toExternalForm()));
      transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
      String exportFormat = "APA";
      if ("ja".equals(lang)) {
        exportFormat = "APA(CJK)";
      }

      for (Object key : PropertyReader.getProperties().keySet()) {
        transformer.setParameter(key.toString(), PropertyReader.getProperty(key.toString()));
      }

      // TODO: an neue SearchAndExport Schnittstelle anpassen
      transformer.setParameter("citation-link",
          PropertyReader.getProperty("inge.pubman.instance.url")
              + "/search/SearchAndExport?cqlQuery=escidoc.publication.creator.person.identifier=\""
              + PropertyReader.getProperty("inge.cone.service.url") + id + "\"&exportFormat=" + exportFormat
              + "&outputFormat=snippet&language=all&sortKeys=escidoc.any-dates&sortOrder=descending");
      transformer.setParameter("item-link", PropertyReader.getProperty("inge.pubman.instance.url")
          + PropertyReader.getProperty("inge.pubman.instance.context.path") + PropertyReader.getProperty("inge.pubman.item.pattern"));
      transformer.setParameter("lang", lang);
      transformer.setParameter("subjectTagNamespace", model.getRdfAboutTag().getNamespaceURI());
      transformer.setParameter("subjectTagLocalName", model.getRdfAboutTag().getLocalPart());
      transformer.setParameter("subjectTagPrefix", model.getRdfAboutTag().getPrefix());

      transformer.transform(new StreamSource(new StringReader(result)), new StreamResult(writer));
    } catch (Exception e) {
      throw new ConeException(e);
    }
    return writer.toString();
  }

}
