/*
roject* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test;


import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JUnit test class for Structured Export component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author$ (last modification) 
 * @version $Revision$ $LastChangedDate$
 */
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test; 

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportHandler;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExportManagerException;
 

import test.TestHelper;

public class StructuredExportTest 
{
		private StructuredExportHandler export = new StructuredExport();
 
	    private Logger logger = Logger.getLogger(StructuredExportTest.class);
	    
	    private static HashMap<String, String> itemLists;
	    
	    public static final Map<String, String> ITEM_LISTS_FILE_MAMES =   
	    	new HashMap<String, String>()   
	    	{  
				{  
		    		//put("BIBTEX", "src/test/resources/item_thesis.xml"); 
                    put("BIBTEX", "src/test/resources/publicationItems/metadataV2/item_book.xml");
                    put("ENDNOTE", "src/test/resources/publicationItems/metadataV2/item_book.xml");
                    put("BIBTEX", "src/test/resources/publicationItems/metadataV2/item_thesis.xml");
                    put("ENDNOTE", "src/test/resources/publicationItems/metadataV2/item_thesis.xml");
//		    		put("ENDNOTE", "src/test/resources/test.xml");  
//		    		put("BIBTEX", "src/test/resources/item_test_bibtex.xml");  
//		    		put("BIBTEX", "src/test/resources/escidoc.xml");  
//		    		put("XML", "src/test/resources/escidoc-item-ver2.xml");  
//		    		put("CSV", "src/test/resources/faces_item-list.xml");  
//		    		put("BAD_ITEM_LIST", "src/test/resources/item_publication_bad.xml");  
		    	}  
	    	};

	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    
	    public static final void getItemLists() throws Exception
	    {
	    	itemLists = new HashMap<String, String>();
	    	
//	    	String itemList = TestHelper.getItemListFromFramework();
//    		assertFalse("item list from framework is empty", itemList == null || itemList.trim().equals("") );
//    		logger.info("item list from framework:\n" + itemList);
    		
	    	for ( String key : ITEM_LISTS_FILE_MAMES.keySet() )
	    	{
	    		String itemList =  ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(key));
	    		assertNotNull("Item list xml is not found", itemList);
	    		itemLists.put(key, itemList);
	    	}
	    	
//	    	FileOutputStream fos = new FileOutputStream("fwItemList.xml");
//	    	fos.write(fwItemList.getBytes());
//	    	fos.close();
	    	
	    }

	    
	    /**
	     * Get EndNote output test 
	     * @throws Exception
	     */
//	    @Before
//	    @Ignore
//	    public final void getStructuredTestOutput() throws Exception
//	    {
//	    	endNoteTestOutput = new String(TestHelper.readBinFile("src/test/resources/EndNoteTestOutput.txt"));
//	    	assertNotNull("EndNote output is not found", endNoteTestOutput);
//	    }

 
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    @Ignore
	    public final void testExplainExport() throws Exception
	    {
	    	String result = export.explainFormats();
	        assertNotNull("explain formats file is null", result);
	        logger.info("explain formats: " + result);
	    }
	    
	    /**
	     * Test list of export formats
	     * @throws Exception Any exception.
	     */
	    @Test
	    @Ignore
	    public final void testFormatList() throws Exception
	    {
	    	String[] fl = export.getFormatsList();
	    	assertTrue("The list of export formats is empty", fl.length>0);
	    	for (String f : fl)
	    		logger.info("Export format: " + f);
	    }
	    
   
	    
	    /**
	     * Test service with a item list XML.
	     * @throws Exception Any exception.
	     */
	    @Test
	    @Ignore
	    public final void testStructuredExports() throws Exception
	    {
	    	long start;
//	    	String[] fl = export.getFormatsList();
	    	for (String f : ITEM_LISTS_FILE_MAMES.keySet()) 
	    	{
	    		logger.info("Export format: " + f);
	    		logger.info("Number of items to proceed: " + TestHelper.ITEMS_LIMIT);
	    		String itemList = ResourceUtil.getResourceAsString(ITEM_LISTS_FILE_MAMES.get(f));    		
	    		//logger.info("Test item list:\n" + itemList);
	    		
	    		XmlTransforming xmlTransforming = new XmlTransformingBean();
                PubItemVO itemVO = xmlTransforming.transformToPubItem(itemList);
                List<PubItemVO> pubitemList = Arrays.asList(itemVO);
                itemList = xmlTransforming.transformToItemList(pubitemList);
	    		
		    	start = System.currentTimeMillis();
		    	byte[] result = export.getOutput(itemList, f);
	    		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
		    	logger.info("---------------------------------------------------");
		    	assertFalse(f + " output is empty", result == null || result.length==0 );
		    	logger.info(f + " export result:\n" + new String(result) );
		    	TestHelper.writeBinFile(result, "target/" + f + "_result.txt");
	    	}
	    	
	    }
	    
	    @Test
	    public void doExportTest() throws Exception
	    {
            String itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml");            
            XmlTransforming xmlTransforming = new XmlTransformingBean();
            PubItemVO itemVO = xmlTransforming.transformToPubItem(itemList);
            List<PubItemVO> pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            byte[] result = export.getOutput(itemList, "BIBTEX");
            assertNotNull(result);
            logger.info("BIBTEX (Book)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_book.xml");            
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "ENDNOTE");
            assertNotNull(result);
            logger.info("ENDNOTE (Book)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml");            
            xmlTransforming = new XmlTransformingBean();
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "BIBTEX");
            assertNotNull(result);
            logger.info("BIBTEX (Thesis)");
            logger.info(new String(result));
            
            itemList = ResourceUtil.getResourceAsString("publicationItems/metadataV2/item_thesis.xml");            
            itemVO = xmlTransforming.transformToPubItem(itemList);
            pubitemList = Arrays.asList(itemVO);
            itemList = xmlTransforming.transformToItemList(pubitemList);
            result = export.getOutput(itemList, "ENDNOTE");
            assertNotNull(result);
            logger.info("ENDNOTE (Thesis)");
            logger.info(new String(result));
	    }
	    
	    
	    /**
	     * Test service with a non-valid item list XML.
	     * @throws Exception 
	     * @throws Exception Any exception.
	     */
	    @Test(expected = StructuredExportManagerException.class)
	    @Ignore
	    public final void testBadItemsListEndNoteExport() throws Exception
	    {
	    	byte[] result = export.getOutput(itemLists.get("BAD_ITEM_LIST"), "ENDNOTE");
	    }
	    

}
