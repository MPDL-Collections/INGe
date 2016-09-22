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

package de.mpg.mpdl.inge.pubman.exporting;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleHandler;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.ItemExporting;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportHandler;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportManagerException;
import de.mpg.mpdl.inge.structuredexportmanager.StructuredExportXSLTNotFoundException;
import de.mpg.mpdl.inge.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.xmltransforming.exceptions.TechnicalException;
import net.sf.jasperreports.engine.JRException;

/**
 * This class provides the ejb implementation of the {@link ItemExporting} interface.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate: 2014-08-20 10:31:05 +0200 (Mi, 20 Aug 2014) $ Revised by
 *          StG: 24.08.2007
 */
@Remote(ItemExporting.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ItemExportingBean implements ItemExporting {
  private static Logger logger = Logger.getLogger(ItemExportingBean.class);

  /**
   * A XmlTransforming instance.
   */
  @EJB
  private XmlTransforming xmlTransforming;

  /**
   * A CitationStyleHandler instance.
   */
  @EJB
  private CitationStyleHandler citationStyleHandler;

  /**
   * A EndnodeExportHandler instance.
   */
  @EJB
  private StructuredExportHandler structuredExportHandler;

  private java.lang.String structuredFormat = "ENDNOTE";

  /**
   * {@inheritDoc}
   */
  public List<ExportFormatVO> explainExportFormats() throws TechnicalException {
    String layoutFormats;
    try {
      layoutFormats = citationStyleHandler.explainStyles();

    } catch (CitationStyleManagerException e) {
      throw new TechnicalException(e);
    } 
    List<ExportFormatVO> result = null;
    result = xmlTransforming.transformToExportFormatVOList(layoutFormats);
    appendStructuredFormat(result);
    return result;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getOutput(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList)
      throws TechnicalException {
    if (exportFormat == null)
      logger.debug(">>>  getOutput with ExportFormatVO NULL!");
    else if (logger.isDebugEnabled()) {
      logger.debug(">>>  getOutput in Format " + exportFormat.getName() + " "
          + exportFormat.getSelectedFileFormat().getName());
    }

    String itemList = xmlTransforming.transformToItemList(pubItemVOList);

    byte[] exportData = null;
    try {
      /*
       * if (exportFormat.getFormatType() == FormatType.LAYOUT_CSL) { if (logger.isDebugEnabled())
       * logger.debug(">>> start cslManager " + itemList); exportData =
       * csl_manager.getOutput(exportFormat, itemList); } else {
       */
      exportData = getOutput(itemList, exportFormat);
      // }

    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("getOutput result: " + new String(exportData));
    }

    return exportData;
  }

  /**
   * Output wrapper for structuredExportHandler.getOutput and citationStyleHandler.getOutput.
   * Parameters should be controlled in the colling methods!
   * 
   * @param exportFormat - export format
   * @param formatType - export format type
   * @param outputFormat - output format type
   * @param itemList - xml item list in item-list.xsd schema
   * @return generated export
   * @throws TechnicalException
   * @throws StructuredExportXSLTNotFoundException
   * @throws StructuredExportManagerException
   * @throws IOException
   * @throws JRException
   * @throws CitationStyleManagerException
   */
  private byte[] getOutput(String itemList, ExportFormatVO exportFormat) throws TechnicalException,
      StructuredExportXSLTNotFoundException, StructuredExportManagerException, IOException,
      JRException, CitationStyleManagerException {

    byte[] exportData = null;

    // structured export
    if (exportFormat.getFormatType() == FormatType.LAYOUT) {
      if (logger.isDebugEnabled())
        logger.debug(">>> start citationStyleHandler " + itemList);

      exportData = citationStyleHandler.getOutput(itemList, exportFormat);
    } else if (exportFormat.getFormatType() == FormatType.STRUCTURED) {
      if (logger.isDebugEnabled())
        logger.debug(">>> start structuredExportHandler " + itemList);
      exportData = structuredExportHandler.getOutput(itemList, exportFormat.getName());
    } else
      // no export format found!!!
      throw new TechnicalException("format Type: " + exportFormat.getFormatType()
          + " is not supported");

    return exportData;
  }

  /**
   * {@inheritDoc}
   */
  public String explainExportFormatsXML() throws TechnicalException {
    String structuredFormats;
    try {
      structuredFormats = structuredExportHandler.explainFormats();
    } catch (StructuredExportManagerException e) {
      throw new TechnicalException(e);
    } catch (IOException e) {
      throw new TechnicalException(e);
    }
    return structuredFormats;
  }

  /**
   * Appends an export structured format to a list of ExportFormatVOs and returns it.
   * 
   * @param listExportFormatVO the list of export formats to which a structured export format should
   *        be added.
   */
  private List<ExportFormatVO> appendStructuredFormat(List<ExportFormatVO> listExportFormatVO) {

    ExportFormatVO exportFormat = new ExportFormatVO();
    exportFormat.setName(structuredFormat);
    FileFormatVO fileFormat = new FileFormatVO();
    fileFormat.setName(FileFormatVO.TEXT_NAME);
    fileFormat.setMimeType(FileFormatVO.TEXT_MIMETYPE);
    exportFormat.setSelectedFileFormat(fileFormat);
    exportFormat.setFormatType(ExportFormatVO.FormatType.STRUCTURED);

    listExportFormatVO.add(exportFormat);

    return listExportFormatVO;
  }

}
