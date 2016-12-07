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

package de.mpg.mpdl.inge.transformation.transformations.commonPublicationFormats;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingBean;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportHandler;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportHandlerBean;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.transformations.commonPublicationFormats.endnote.EndNoteTransformation;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;

/**
 * Implements transformations for common publication.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CommonTransformation {
  private final Logger logger = Logger.getLogger(CommonTransformation.class);

  /**
   * Transformation from esciDoc to bibtex format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param service
   * @return bibtex as byte[]
   * @throws TransformationNotSupportedException
   * @throws RuntimeException
   */
  public byte[] transformEscidocToBibtex(byte[] src, Format srcFormat, Format trgFormat,
      String service, boolean list) throws TransformationNotSupportedException, RuntimeException {
    byte[] bib = null;
    try {
      XmlTransforming xmlTransforming = new XmlTransformingBean();
      StructuredExportHandler structExport = new StructuredExportHandlerBean();
      String itemList = "";
      if (!list) {
        PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(src, "UTF-8"));
        List<PubItemVO> pubitemList = Arrays.asList(itemVO);
        itemList = xmlTransforming.transformToItemList(pubitemList);
      } else {
        itemList = new String(src, "UTF-8");
      }
      bib = structExport.getOutput(itemList, "BIBTEX");

    } catch (Exception e) {
      this.logger.error("An error occurred during a common publication transformation.", e);
      throw new RuntimeException(e);
    }
    return bib;
  }

  /**
   * Transformation from eSciDoc to endnote format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param service
   * @return endnote as byte[]
   * @throws RuntimeException
   */
  public byte[] transformEscidocToEndnote(byte[] src, Format srcFormat, Format trgFormat,
      String service, boolean list) throws RuntimeException {
    byte[] endnote = null;
    try {
      XmlTransforming xmlTransforming = new XmlTransformingBean();
      StructuredExportHandler structExport = new StructuredExportHandlerBean();
      String itemList = "";
      if (!list) {
        PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(src, "UTF-8"));
        List<PubItemVO> pubitemList = Arrays.asList(itemVO);
        itemList = xmlTransforming.transformToItemList(pubitemList);
      } else {
        itemList = new String(src, "UTF-8");
      }
      endnote = structExport.getOutput(itemList, "ENDNOTE");
    } catch (Exception e) {
      this.logger.error("An error occurred during a common publication transformation.", e);
      throw new RuntimeException(e);
    }
    return endnote;
  }

  /**
   * Transformation bibtex to eSciDoc format.
   * 
   * @param src
   * @param srcFormat
   * @param trgFormat
   * @param service
   * @return bibtex as byte[]
   * @throws RuntimeException
   */
  public byte[] transformBibtexToEscidoc(byte[] src, Format srcFormat, Format trgFormat,
      String service, Map<String, String> configuration) throws RuntimeException {
    byte[] escidoc = null;

    BibtexInterface bibtexTrans = BibtexFactory.getBibtexImplementation(configuration);
    try {
      escidoc = bibtexTrans.getBibtex(new String(src)).getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return escidoc;
  }

}
