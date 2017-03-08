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

package de.mpg.mpdl.inge.citationmanager.transformation;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleHandler;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.citationmanager.impl.CitationStyleHandlerBean;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingBean;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.Util;
import de.mpg.mpdl.inge.transformation.Util.Styles;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.transformation.util.Format;

/**
 * Implements transformations for citation styles.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CitationTransformation {
  private final Logger logger = Logger.getLogger(CitationTransformation.class);

  private final String typeHTML = "text/html";
  private final String typeRTF1 = "text/richtext";
  private final String typeRTF2 = "application/rtf";
  private final String typeODT = "application/vnd.oasis.opendocument.text";
  private final String typePDF = "application/pdf";
  private final String typeSnippet = "snippet";

  /**
   * Public constructor.
   */
  public CitationTransformation() {}

  /**
   * Transformation to citation style in snippet format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param itemListBool - checks if the provided object is an itemList
   * @param service
   * @return transformed item as byte[]
   * @throws TransformationNotSupportedException
   * @throws RuntimeException
   */

  public byte[] transformEscidocItemToCitation(byte[] src, Format srcFormat, Format trgFormat,
      String service, boolean itemListBool) throws TransformationException, RuntimeException {

    byte[] citation = null;
    try {
      XmlTransforming xmlTransforming = new XmlTransformingBean();
      CitationStyleHandler citeHandler = new CitationStyleHandlerBean();
      String itemList = "";
      if (!itemListBool) {
        PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(src, "UTF-8"));
        List<PubItemVO> pubitemList = Arrays.asList(itemVO);
        itemList = xmlTransforming.transformToItemList(pubitemList);
      } else {
        itemList = new String(src, "UTF-8");
      }


      citation =
          citeHandler.getOutput(itemList, new ExportFormatVO(FormatType.LAYOUT, "snippet",
              trgFormat.getName().toUpperCase()));
    } catch (CitationStyleManagerException e) {
      throw new TransformationException(e);
    } catch (Exception e) {
      this.logger.error("An error occurred during a citation transformation.", e);
      throw new RuntimeException(e);
    }

    return citation;
  }

  /**
   * This method calls the transformation service for the transformation from citation snippet to a
   * given output format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param service
   * @return
   */
  public byte[] transformOutputFormat(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationException, RuntimeException {
    byte[] result = null;

    // Create input format
    Styles style = Util.getStyleInfo(trgFormat);
    String formatName = "snippet";
    if (style == Styles.APA || style == Styles.AJP) {
      formatName += "_" + style.toString();
    }
    Format input = new Format(formatName, "application/xml", "UTF-8");
    // Create output format
    Format output =
        new Format(this.getOutputFormat(trgFormat.getType()), trgFormat.getType(),
            trgFormat.getEncoding());

    StringWriter wr = new StringWriter();
    Transformer t =
        de.mpg.mpdl.inge.transformation.TransformerFactory.newInstance(input.toFORMAT(),
            output.toFORMAT());

    t.transform(new TransformerStreamSource(new ByteArrayInputStream(src)),
        new TransformerStreamResult(wr));
    return wr.toString().getBytes();
  }

  private String getOutputFormat(String type) {
    if (type.toLowerCase().equals(this.typeHTML)) {
      return "html";
    }
    if (type.toLowerCase().equals(this.typeODT)) {
      return "odt";
    }
    if (type.toLowerCase().equals(this.typePDF)) {
      return "pdf";
    }
    if (type.toLowerCase().equals(this.typeRTF1) || type.toLowerCase().equals(this.typeRTF2)) {
      return "rtf";
    }
    if (type.toLowerCase().equals(this.typeSnippet)) {
      return "snippet";
    }

    return null;
  }
}
