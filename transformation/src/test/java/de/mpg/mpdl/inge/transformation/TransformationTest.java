package de.mpg.mpdl.inge.transformation;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.model.xmltransforming.util.XmlComparator;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingBean;
import de.mpg.mpdl.inge.transformation.TransformationBean;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;


public class TransformationTest {
  public static TransformationBean trans;
  private final Logger logger = Logger.getLogger(TransformationTest.class);


  /**
   * Initializes the {@link TransformationBean}.
   */
  @BeforeClass
  public static void initTransformation() {
    trans = new TransformationBean(true);
  }

  @Test
  public void explainTest() throws Exception {
    try {
      this.logger.info("ALL SOURCE FORMATS FOR ALL TRANSFORMATIONS");
      Format[] formats = trans.getSourceFormats();
      for (int i = 0; i < formats.length; i++) {
        this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
      }

      this.logger.info("-----OK");

      this.logger.info("ALL TARGET FORMATS FOR escidoc-publication-item:");
      formats =
          trans.getTargetFormats(new Format("eSciDoc-publication-item", "application/xml", "*"));
      for (int i = 0; i < formats.length; i++) {
        this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
      }
      this.logger.info("-----OK");

      this.logger.info("ALL SOURCE FORMATS FOR escidoc-publication-item:");
      formats =
          trans
              .getSourceFormats(new Format("eSciDoc-publication-item", "application/xml", "UTF-8"));
      for (int i = 0; i < formats.length; i++) {
        this.logger.info(formats[i].getName() + " (" + formats[i].getType() + ")");
      }
      this.logger.info("-----OK");
    } catch (Exception e) {
      this.logger.error("An error occurred during transformation", e);
      throw new Exception(e);
    }

    this.logger.info("--- Explain tests succeeded ---");
  }

  /*
   * test TEI2 to eSciDoc item transformation
   */
  @Test
  public void tei2escidoc() throws Exception {
    this.logger.info("---Transformation TEI to escidoc format ---");
    Format teiFormat = new Format("peer_tei", "application/xml", "UTF-8");
    Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");

    byte[] result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/tei/tei1.tei",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), teiFormat,
            escidocFormat, "escidoc");

    // result = trans.transform(this.util.getResourceAsString("testFiles/tei/Springer-351-S2.tei")
    // .getBytes("UTF-8"), teiFormat, escidocComponentFormat, "escidoc");
    this.logger.debug(new String(result, "UTF-8"));

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");
  }

  // Transformation currently not in use
  public void bmcarticle2htmlTest() throws Exception {
    this.logger.info("---Transformation mnc article to html format ---");
    Format xml = new Format("bmc-fulltext-xml", "application/xml", "UTF-8");
    Format html = new Format("bmc-fulltext-html", "text/html", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/bmc_article.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), xml, html, "escidoc");
    this.logger.debug(new String(result, "UTF-8"));
  }

  @Test
  public void bmc2escidocTest() throws Exception {
    this.logger.info("---Transformation BMC to escidoc format ---");
    Format bmc = new Format("bmc", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
    Format escidocComponent =
        new Format("escidoc-publication-component", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/bmc.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), bmc, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    // System.out.println(new String(result, "UTF-8"));
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");

    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/bmc.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), bmc,
            escidocComponent, "escidoc");
    FileVO componentVO = xmlTransforming.transformToFileVO(new String(result, "UTF-8"));
    this.logger.info("FileVO successfully created.");
  }

  @Test
  public void arxiv2escidocTest() throws Exception {
    this.logger.info("---Transformation arXiv to escidoc format ---");
    Format arxivItem = new Format("arxiv", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
    Format escidocComponent =
        new Format("escidoc-publication-component", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/arxivItem.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), arxivItem, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");

    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/arxivItem.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), arxivItem,
            escidocComponent, "escidoc");
    System.out.println(new String(result, "UTF-8"));
    FileVO componentVO = xmlTransforming.transformToFileVO(new String(result, "UTF-8"));
    Assert.assertNotNull(componentVO);
    this.logger.info("FileVO successfully created. ");
  }

  @Test
  public void spires2escidocTest() throws Exception {
    this.logger.info("---Transformation spires to escidoc format ---");
    Format spires = new Format("spires", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/spires.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), spires, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");
  }

  @Test
  public void bibtex2escidocTest() throws Exception {
    this.logger.info("---Transformation BibTex to escidoc format ---");
    Format bibtex = new Format("BibTex", "text/plain", "*");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/bibtex/bib.bib",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), bibtex, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");
  }

  @Test
  public void escidoc2bibtexTest() throws Exception {
    this.logger.info("---Transformation escidoc to BibTex format ---");
    Format bibtex = new Format("BibTex", "text/plain", "*");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidocItem_newFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), escidoc, bibtex,
            "escidoc");
    this.logger.debug(new String(result, "UTF-8"));
  }

  @Test
  public void endnote2escidocTest() throws Exception {
    this.logger.info("---Transformation EndNote to escidoc format ---");
    Format endnote = new Format("EndNote", "text/plain", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/endnote/endnote.txt",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), endnote, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");
  }

  @Test
  public void escidoc2endnoteTest() throws Exception {
    this.logger.info("---Transformation escidoc to EndNote format ---");
    Format endnote = new Format("EndNote", "text/plain", "*");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidocItem_newFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), escidoc, endnote,
            "escidoc");
    this.logger.debug(new String(result, "UTF-8"));
  }

  @Test
  @Ignore
  public void edoc2escidoc() throws Exception {
    this.logger.info("---Transformation eDoc to eSciDoc format ---");
    Format edoc = new Format("eDoc", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");

    Map<String, String> configuration = new HashMap<String, String>();
    configuration.put("import-name", "OTHER");
    configuration.put("CoNE", "false");

    String result;
    byte[] resultBytes =
        trans
            .transform(
                ResourceUtil.getResourceAsBytes("testFiles/edoc/test.xml",
                    TransformationTest.class.getClassLoader()), edoc, escidoc, "escidoc",
                configuration);
    result = new String(resultBytes, "UTF-8");

    String compare =
        ResourceUtil.getResourceAsString("testFiles/edoc/result.xml",
            TransformationTest.class.getClassLoader());

    logger.info(resultBytes.length);

    XmlComparator xmlComparator = new XmlComparator(result, compare);

    if (!xmlComparator.equal()) {
      StringWriter stringWriter = new StringWriter();
      stringWriter.write("The result is not the expected. There is a difference at:\n");
      for (String error : xmlComparator.getErrors()) {
        stringWriter.write("- ");
        stringWriter.write(error);
        stringWriter.write("\n");
      }
      stringWriter.write("Result XML: ");
      stringWriter.write(result);
      stringWriter.write("\n");
      stringWriter.write("Expected XML: ");
      stringWriter.write(compare);
      stringWriter.write("\n");

      fail(stringWriter.toString());
    }

  }

  @Test
  @Ignore
  public void edoc2escidoc2() throws Exception {
    this.logger.info("---Transformation eDoc to eSciDoc format ---");
    Format edoc = new Format("eDoc", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");

    String result;

    Map<String, String> conf = trans.getConfiguration(edoc, escidoc);
    System.out.println(conf);

    assertTrue(conf.containsKey("CoNE"));
    assertTrue("true".equals(conf.get("CoNE")));

    conf.put("CoNE", "false");

    byte[] resultBytes =
        trans.transform(
            ResourceUtil.getResourceAsBytes("testFiles/edoc/test.xml",
                TransformationTest.class.getClassLoader()), edoc, escidoc, "escidoc", conf);
    result = new String(resultBytes, "UTF-8");

    String compare =
        ResourceUtil.getResourceAsString("testFiles/edoc/result.xml",
            TransformationTest.class.getClassLoader());

    logger.info(resultBytes.length);

    XmlComparator xmlComparator = new XmlComparator(result, compare);

    if (!xmlComparator.equal()) {
      StringWriter stringWriter = new StringWriter();
      stringWriter.write("The result is not the expected. There is a difference at:\n");
      for (String error : xmlComparator.getErrors()) {
        stringWriter.write("- ");
        stringWriter.write(error);
        stringWriter.write("\n");
      }
      stringWriter.write("Result XML: ");
      stringWriter.write(result);
      stringWriter.write("\n");
      stringWriter.write("Expected XML: ");
      stringWriter.write(compare);
      stringWriter.write("\n");
      stringWriter.close();

      fail(stringWriter.toString());

    }

  }

  @Test
  @Ignore
  // temporarily moved to the structuredexportmanager
  public void escidoc2edocTest() throws Exception {
    byte[] src =
        ResourceUtil.getResourceAsBytes("testFiles/escidoc/escidoc_xml_full.xml",
            TransformationTest.class.getClassLoader());
    Format escidoc = new Format("escidoc", "application/xml", "UTF-8");

    for (String format : new String[] {"edoc_export", "edoc_import"}) {
      this.logger.info("---Transformation eSciDoc to " + format + " format ---");

      Format edoc = new Format(format, "application/xml", "UTF-8");

      byte[] resultBytes = trans.transform(src, escidoc, edoc, "escidoc");
      Assert.assertNotNull(resultBytes);

      // String file = ResourceUtil.getResourceAsFile(".").getAbsolutePath() + "/edoc_test.xml";
      /*
       * String file = "target/"+ format + ".xml"; this.logger.info("output file: " + file);
       * 
       * FileOutputStream fos = new FileOutputStream(file); fos.write(resultBytes); fos.close();
       */
    }

  }

  @Test
  public void pmc2escidocTest() throws Exception {
    this.logger.info("---Transformation PMC to escidoc format ---");
    Format pmcItem = new Format("pmc", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
    Format escidocComponent =
        new Format("escidoc-publication-component", "application/xml", "UTF-8");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/pmc2.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), pmcItem, escidoc,
            "escidoc");

    PubItemVO itemVO = xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    this.logger.info("PubItemVO successfully created.");

    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/externalSources/pmc2.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), pmcItem,
            escidocComponent, "escidoc");
    FileVO componentVO = xmlTransforming.transformToFileVO(new String(result, "UTF-8"));
    Assert.assertNotNull(componentVO);
    this.logger.info("FileVO successfully created.");

  }

  @Test
  public void mods2oaidcTest() throws Exception {
    this.logger.info("---Transformation MODS to oai_dc format ---");

    Format mods = new Format("mods", "application/xml", "UTF-8");
    Format oai = new Format("oai_dc", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/mods/mods2.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), mods, oai, "escidoc");
    logger.debug("Result: " + new String(result, "UTF-8"));

    // String referenceItem =
    // this.normalizeString(this.util.getResourceAsString("testFiles/testResults/modsAsOaidc.xml"));
    // String actualItem = this.normalizeString(new String(result, "UTF-8"));
    // Assert.assertTrue(referenceItem.equals(actualItem));
    // this.logger.info("Transformation to oai_dc successful.");
  }

  @Test
  public void mods2marcTest() throws Exception {
    this.logger.info("---Transformation MODS to MARC format ---");

    Format mods = new Format("mods", "application/xml", "UTF-8");
    Format marc = new Format("marc21", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans
            .transform(
                ResourceUtil.getResourceAsString("testFiles/mods/mods.xml",
                    TransformationTest.class.getClassLoader()).getBytes("UTF-8"), mods, marc,
                "escidoc");
    this.logger.debug(new String(result, "UTF-8"));
  }

  @Test
  @Ignore
  // TODO: check, currently not needed
  public void mods2escidocTest() throws Exception {
    this.logger.info("---Transformation MODS to escidoc format ---");

    Format mods = new Format("mods", "application/xml", "UTF-8");
    Format escidoc = new Format("escidoc-publication-item-list", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/mods/mods.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), mods, escidoc,
            "escidoc");

    XmlTransformingBean xmlTransforming = new XmlTransformingBean();
    List<PubItemVO> itemList =
        (List<PubItemVO>) xmlTransforming.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemList);
    this.logger.info("PubItemVO successfully created.");
  }

  @Test
  public void escidoc2oaidcTest() throws Exception {
    this.logger.info("---Transformation escidoc to oai_dc format ---");

    Format escidoc = new Format("escidoc-publication-item", "application/xml", "UTF-8");
    Format oai = new Format("oai_dc", "application/xml", "UTF-8");

    byte[] result;
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidocItem_newFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), escidoc, oai,
            "escidoc");
    logger.debug("Result: " + new String(result, "UTF-8"));

    // String referenceItem =
    // this.normalizeString(this.util.getResourceAsString("testFiles/testResults/escidocAsOaidc.xml"));
    // String actualItem = this.normalizeString(new String(result, "UTF-8"));
    // Assert.assertTrue(referenceItem.equals(actualItem));
    // this.logger.info("Transformation to oai_dc successful.");
  }

  // This transformation is currently not in use
  public void snippetToOutputFormatTest() throws Exception {
    this.logger.info("snippet -> outputFormat");

    Format input1 = new Format("snippet", "application/xml", "UTF-8");
    Format input2 = new Format("snippet_APA", "application/xml", "UTF-8");
    Format input3 = new Format("snippet_AJP", "application/xml", "UTF-8");
    Format output1 = new Format("pdf", "application/pdf", "*");
    Format output2 = new Format("html", "text/html", "*");
    Format output3 = new Format("rtf", "application/rtf", "*");
    Format output4 = new Format("odt", "application/vnd.oasis.opendocument.text", "*");

    byte[] result;
    this.logger.info("APA");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/apa_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input2, output1,
            "escidoc");
    this.logger.info("APA - pdf: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/apa_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input2, output2,
            "escidoc");
    this.logger.info(new String(result, "UTF-8"));
    this.logger.info("APA - html: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/apa_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input2, output3,
            "escidoc");
    this.logger.info("APA - rtf: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/apa_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input2, output4,
            "escidoc");
    this.logger.info("APA - odt: OK");

    this.logger.info("AJP");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/ajp_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input3, output1,
            "escidoc");
    this.logger.info("AJP - pdf: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/ajp_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input3, output2,
            "escidoc");
    this.logger.info(new String(result, "UTF-8"));
    this.logger.info("AJP - html: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/ajp_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input3, output3,
            "escidoc");
    this.logger.info("AJP - rtf: OK");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/ajp_snippet_oldFormat.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), input3, output4,
            "escidoc");
    this.logger.info("AJP - odt: OK");
  }

  @Test
  public void eSciDocVer1toeSciDocVer2() throws TransformationNotSupportedException,
      RuntimeException, UnsupportedEncodingException, IOException {
    Format in_i = new Format("escidoc-publication-item-v1", "application/xml", "UTF-8");
    Format out_i = new Format("escidoc-publication-item-v2", "application/xml", "UTF-8");
    Format in_il = new Format("escidoc-publication-item-list-v1", "application/xml", "UTF-8");
    Format out_il = new Format("escidoc-publication-item-list-v2", "application/xml", "UTF-8");

    byte[] result;

    logger.info("escidoc-publication-item-v1 to escidoc-publication-item-v2");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-ver1.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_i,
            "escidoc");
    this.logger.info("OK");

    logger
        .info("escidoc-publication-item-v1 to escidoc-publication-item-v2, file with multiply items (Exception!)");
    try {
      result =
          trans.transform(
              ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver1.xml",
                  TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_i,
              "escidoc");
      Assert.fail("Exception should be thrown!");
    } catch (Exception e) {
      this.logger.info("OK");
    }

    logger.info("escidoc-publication-item-v1 to escidoc-publication-item-list-v2");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-ver1.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_il,
            "escidoc");
    this.logger.info("OK");

    logger
        .info("escidoc-publication-item-v1 to escidoc-publication-item-list-v2, file with multiply items");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver1.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_il,
            "escidoc");
    this.logger.info("OK");

    logger
        .info("escidoc-publication-item-v1 to escidoc-publication-item-list-v2, file with multiply items");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver1.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_il,
            "escidoc");
    this.logger.info("OK");

    logger
        .info("escidoc-publication-item-list-v1 to escidoc-publication-item-v2, 0 items (Exception!)");
    try {
      result =
          trans
              .transform(
                  "<escidocItemList:item-list xmlns:escidocItemList=\"http://www.escidoc.de/schemas/itemlist/0.8\"></escidocItemList:item-list>"
                      .getBytes("UTF-8"), in_il, out_i, "escidoc");
      Assert.fail("Exception should be thrown!");
    } catch (Exception e) {
      this.logger.info("OK");
    }
    logger
        .info("escidoc-publication-item-list-v1 to escidoc-publication-item-list-v2, file with multiply items");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-list-ver1.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_il, out_il,
            "escidoc");
    this.logger.info("OK");


    /*
     * this.logger.info("output file: " + ResourceUtil.getResourceAsFile(".").getAbsolutePath() +
     * "/testFiles/version2.xml"); FileOutputStream fos = new
     * FileOutputStream(ResourceUtil.getResourceAsFile(".").getAbsolutePath() +
     * "/testFiles/version2.xml"); fos.write(result); fos.close();
     */


  }

  @Test
  public void eSciDocVer2toeSciDocVer1() throws TransformationNotSupportedException,
      RuntimeException, UnsupportedEncodingException, IOException {
    Format in_i = new Format("escidoc-publication-item-v2", "application/xml", "UTF-8");
    Format out_i = new Format("escidoc-publication-item-v1", "application/xml", "UTF-8");
    Format in_il = new Format("escidoc-publication-item-list-v2", "application/xml", "UTF-8");
    Format out_il = new Format("escidoc-publication-item-list-v1", "application/xml", "UTF-8");

    byte[] result;

    /*
     * logger.info("escidoc-publication-item-v1 to escidoc-publication-item-v2"); result =
     * trans.transform
     * (this.util.getResourceAsString("testFiles/escidoc-item-ver1.xml").getBytes("UTF-8"), in_i,
     * out_i, "escidoc"); this.logger.info("OK");
     * 
     * logger.info(
     * "escidoc-publication-item-v1 to escidoc-publication-item-v2, file with multiply items (Exception!)"
     * ); try { result =
     * trans.transform(this.util.getResourceAsString("testFiles/escidoc-item-list-ver1.xml"
     * ).getBytes("UTF-8"), in_i, out_i, "escidoc"); Assert.fail("Exception should be thrown!"); }
     * catch (Exception e) { this.logger.info("OK"); }
     * 
     * logger.info("escidoc-publication-item-v1 to escidoc-publication-item-list-v2"); result =
     * trans
     * .transform(this.util.getResourceAsString("testFiles/escidoc-item-ver1.xml").getBytes("UTF-8"
     * ), in_i, out_il, "escidoc"); this.logger.info("OK");
     * 
     * logger.info(
     * "escidoc-publication-item-v1 to escidoc-publication-item-list-v2, file with multiply items");
     * result =
     * trans.transform(this.util.getResourceAsString("testFiles/escidoc-item-list-ver1.xml")
     * .getBytes("UTF-8"), in_i, out_il, "escidoc"); this.logger.info("OK");
     * 
     * logger.info(
     * "escidoc-publication-item-v1 to escidoc-publication-item-list-v2, file with multiply items");
     * result =
     * trans.transform(this.util.getResourceAsString("testFiles/escidoc-item-list-ver1.xml")
     * .getBytes("UTF-8"), in_i, out_il, "escidoc"); this.logger.info("OK");
     * 
     * logger.info(
     * "escidoc-publication-item-list-v1 to escidoc-publication-item-v2, 0 items (Exception!)"); try
     * { result = trans.transform(
     * "<escidocItemList:item-list xmlns:escidocItemList=\"http://www.escidoc.de/schemas/itemlist/0.8\"></escidocItemList:item-list>"
     * .getBytes("UTF-8"), in_il, out_i, "escidoc"); Assert.fail("Exception should be thrown!"); }
     * catch (Exception e) { this.logger.info("OK"); }
     */

    logger.info("escidoc-publication-item-v2 to escidoc-publication-item-list-v1");
    result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/escidoc/escidoc-item-ver2.xml",
                TransformationTest.class.getClassLoader()).getBytes("UTF-8"), in_i, out_il,
            "escidoc");
    this.logger.info("OK");

    /*
     * String file = ResourceUtil.getResourceAsFile(".").getAbsolutePath() +
     * "/testFiles/version1.xml"; this.logger.info("output file: " + file);
     * 
     * FileOutputStream fos = new FileOutputStream(file); fos.write(result); fos.close();
     */

  }

  private String normalizeString(String str) {
    return str.replace(" ", "").replace("\n", "");
  }
}
