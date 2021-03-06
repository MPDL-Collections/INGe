package de.mpg.mpdl.inge.transformation;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public class TransformerCacheTest {

  @Before
  public void setUp() {
    TransformerCache.clear();
  }

  @Test
  public void testTransformerCache() {
    Transformer t1 = null, t2 = null, t3 = null, t4 = null;
    try {
      t1 = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V2_XML, FORMAT.ESCIDOC_ITEMLIST_V1_XML);

      assertTrue(TransformerCache.getTransformerCacheSize() == 1);
      assertTrue(t1 != null);

      t2 = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V3_XML, FORMAT.ESCIDOC_ITEMLIST_V1_XML);
      assertTrue(TransformerCache.getTransformerCacheSize() == 2);
      assertTrue(t2 != null);

      t3 = TransformerFactory.newTransformer(FORMAT.ESCIDOC_ITEMLIST_V2_XML, FORMAT.ESCIDOC_ITEMLIST_V1_XML);
      assertTrue(TransformerCache.getTransformerCacheSize() == 2);
      assertTrue(t3 != null && t1.getClass() == t3.getClass());

    } catch (TransformationException e) {
      Assert.fail();
    }

    try {
      t4 = TransformerFactory.newTransformer(FORMAT.BIBTEX_STRING, FORMAT.BMC_XML);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof TransformationException);;
    }
    assertTrue(t4 == null);
    assertTrue(TransformerCache.getTransformerCacheSize() == 2);
  }

  @Test
  public void testGetAllSourceFormatsFor() {

    assertTrue(Arrays.asList(TransformerFactory.getAllSourceFormatsFor(FORMAT.ESCIDOC_ITEM_V3_XML))
        .containsAll(Arrays.asList(TransformerFactoryTest.sourceForESCIDOC_ITEM_V3_XML)));
  }

  @Test
  public void testGetAllTargetFormatsFor() {


    assertTrue(Arrays.asList(TransformerFactory.getAllTargetFormatsFor(FORMAT.ESCIDOC_ITEM_V3_XML))
        .containsAll(Arrays.asList(TransformerFactoryTest.targetForESCIDOC_ITEM_V3_XML)));


  }

  @Test
  public void testIsTransformationExisting() {
    // formats used by import
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.BIBTEX_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.ENDNOTE_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.RIS_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.WOS_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.MAB_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.EDOC_XML, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.MARC_21_STRING, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.MARC_XML, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.BMC_XML, FORMAT.ESCIDOC_ITEM_V3_XML));
    assertTrue(TransformerCache.isTransformationExisting(FORMAT.ZFN_TEI_XML, FORMAT.ESCIDOC_ITEM_V3_XML));

    assertTrue("Is " + TransformerCache.getTransformerCacheSize() + " expected 10", TransformerCache.getTransformerCacheSize() == 10);
  }

}
