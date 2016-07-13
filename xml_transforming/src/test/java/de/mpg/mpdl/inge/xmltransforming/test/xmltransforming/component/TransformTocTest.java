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
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur F�rderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.xmltransforming.test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.xmltransforming.test.xmltransforming.XmlTransformingTestBase;
import de.mpg.mpdl.inge.model.valueobjects.TocDivVO;
import de.mpg.mpdl.inge.model.valueobjects.TocItemVO;
import de.mpg.mpdl.inge.model.valueobjects.TocPtrVO;
import de.mpg.mpdl.inge.model.valueobjects.TocVO;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for statistic transformings.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class TransformTocTest extends XmlTransformingTestBase {
  Logger logger = Logger.getLogger(this.getClass());

  private static XmlTransforming xmlTransforming = new XmlTransformingBean();
  private static String TEST_FILE_ROOT = "xmltransforming/component/transformTocTest/";
  private static String TOCITEM_SAMPLE_FILE = TEST_FILE_ROOT + "tocItem_sample.xml";
  private static String TOC_SAMPLE_FILE = TEST_FILE_ROOT + "toc_sample.xml";

  private static String TOC_ID = "meins";
  private static String TOC_TYPE = "LOGICAL";
  private static String TOC_LABEL = "Table of Content";
  private static String TOC_XML_BASE = "http://localhost:8080";

  private static String DIV_ID_ROOT = "rootNode";
  private static String DIV_LABEL_ROOT = "root title";
  private static String DIV_TYPE_ROOT = "monograph";

  private static String PTR_ID_1_1 = "rootNodePtr";
  private static String PTR_USE_1_1 = "DEFAULT";
  private static String PTR_LOCTYPE_1_1 = "URL";
  private static String PTR_LINKREF_1_1 = "/ir/container/escidoc:10";
  private static String PTR_LINKTYPE_1_1 = "simple";
  private static String PTR_LINKTITLE_1_1 = "[the containers title]";

  private static String DIV_ID_1 = "container11";
  private static int DIV_ORDER_1 = 1;
  private static String DIV_ORDERLABEL_1 = "1.";
  private static String DIV_LABEL_1 = "title1.1";
  private static String DIV_TYPE_1 = "chapter";



  /**
   * 
   * Test of {@link XmlTransforming#transformToTocItemVO(String)}
   * 
   * @throws Exception
   */
  @Test
  public void transformToTocItemVOwithNestedDivsRoundtrip() throws Exception {
    logger.info("### transformToTocItemVOwithNestedDivsRoundtrip ###");

    String tocXml = readFile(TOCITEM_SAMPLE_FILE);

    TocItemVO tocItemVO = xmlTransforming.transformToTocItemVO(tocXml);
    TocVO tocVO = tocItemVO.getTocVO();

    compareTocs(tocVO);

    String tocXmlNew = xmlTransforming.transformToTocItem(tocItemVO);
    TocItemVO tocItemNew = xmlTransforming.transformToTocItemVO(tocXmlNew);

    compareTocs(tocItemNew.getTocVO());

  }


  /**
   * 
   * Test of {@link XmlTransforming#transformToTocVO(String)}
   * 
   * @throws Exception
   */
  @Test
  public void transformToTocVOwithNestedDivs() throws Exception {
    logger.info("### transformToTocVOwithNestedDivs ###");

    String tocXML = readFile(TOC_SAMPLE_FILE);

    TocVO tocVO = xmlTransforming.transformToTocVO(tocXML);

    compareTocs(tocVO);


  }



  private void compareTocs(TocVO tocVO) {
    assertEquals(TOC_ID, tocVO.getTocId());
    assertEquals(TOC_LABEL, tocVO.getTocLabel());
    assertEquals(TOC_TYPE, tocVO.getTocType());
    assertEquals(TOC_XML_BASE, tocVO.getTocBase());

    TocDivVO tocDivVO = tocVO.getTocDiv();
    assertNotNull("TocDiv is null", tocDivVO);
    assertEquals(DIV_ID_ROOT, tocDivVO.getId());
    assertEquals(DIV_LABEL_ROOT, tocDivVO.getLabel());
    assertEquals(DIV_TYPE_ROOT, tocDivVO.getType());


    List<TocDivVO> tocDivList1 = tocDivVO.getTocDivList();
    assertEquals(2, tocDivList1.size());

    List<TocPtrVO> tocPtrList1 = tocDivVO.getTocPtrList();
    assertEquals(1, tocPtrList1.size());

    TocPtrVO tocPtrVO1_1 = tocPtrList1.get(0);
    assertEquals(PTR_ID_1_1, tocPtrVO1_1.getId());
    assertEquals(PTR_LOCTYPE_1_1, tocPtrVO1_1.getLoctype());
    assertEquals(PTR_USE_1_1, tocPtrVO1_1.getUse());
    assertEquals(PTR_LINKREF_1_1, tocPtrVO1_1.getLinkRef());
    assertEquals(PTR_LINKTITLE_1_1, tocPtrVO1_1.getLinkTitle());
    assertEquals(PTR_LINKTYPE_1_1, tocPtrVO1_1.getLinkType());

    TocDivVO tocDivVO1 = tocDivList1.get(0);
    assertEquals(DIV_ID_1, tocDivVO1.getId());
    assertEquals(DIV_LABEL_1, tocDivVO1.getLabel());
    assertEquals(DIV_ORDER_1, tocDivVO1.getOrder());
    assertEquals(DIV_ORDERLABEL_1, tocDivVO1.getOrderLabel());
    assertEquals(DIV_TYPE_1, tocDivVO1.getType());

    List<TocDivVO> tocDivList1_1 = tocDivVO1.getTocDivList();
    assertEquals(2, tocDivList1_1.size());

    List<TocPtrVO> tocPtrList1_1 = tocDivVO1.getTocPtrList();
    assertEquals(1, tocPtrList1_1.size());
  }


}
