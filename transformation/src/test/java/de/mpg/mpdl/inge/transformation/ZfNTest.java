package de.mpg.mpdl.inge.transformation;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class ZfNTest {
  public static TransformationService trans;

  /**
   * Initializes the {@link TransformationService}.
   */
  @BeforeClass
  public static void initTransformation() {
    trans = new TransformationService(true);
  }

  /*
   * test ZfN TEI to eSciDoc item transformation Will not work as junit test due to xslt path
   * property
   */
  @Test
  public void zfn2escidoc() throws Exception {
    System.out.println("---Transformation ZfN to escidoc format ---");
    Format teiFormat = new Format("zfn_tei", "application/xml", "UTF-8");
    Format escidocFormat = new Format("eSciDoc-publication-item", "application/xml", "UTF-8");

    byte[] result =
        trans.transform(
            ResourceUtil.getResourceAsString("testFiles/zfn/ZNC-1988-43c-0979_b.header.tei.xml",
                ZfNTest.class.getClassLoader()).getBytes("UTF-8"), teiFormat, escidocFormat,
            "escidoc");

    System.out.println(new String(result, "UTF-8"));

    PubItemVO itemVO = XmlTransformingService.transformToPubItem(new String(result, "UTF-8"));
    Assert.assertNotNull(itemVO);
    System.out.println("PubItemVO successfully created.");
  }
}
