/*
* CDDL HEADER START
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

package test.exportmanager;

 
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;


/**
 * JUnit test class for Export Manager component   
 * @author Author: Vlad Makarenko (initial creation) 
 * @author $Author$ (last modification) 
 * @version $Revision$ $LastChangedDate$
 */
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test; 


import de.mpg.escidoc.services.exportmanager.ExportManagerException;
import de.mpg.escidoc.services.exportmanager.ExportHandler;
import de.mpg.escidoc.services.exportmanager.Export.ArchiveFormats;

import test.TestHelper;

import javax.naming.InitialContext;



public class ExportIntegrationTest 
{
		private static Logger logger = Logger.getLogger(ExportIntegrationTest.class);
		
		private static ExportHandler export;
	    private static String pubManItemList;
	    private static String facesItemList;

	    
        long start = 0;

	    /**
	     * Init  Export bean.
	     * @throws Exception Any Exception.
	     */
	    @BeforeClass
	    public static final void getExport() throws Exception
	    {
	        InitialContext ctx = new InitialContext();
	        export = (ExportHandler) ctx.lookup(ExportHandler.SERVICE_NAME);
	    }
	    
	    
	    /**
	     * Get test item list from XML 
	     * @throws Exception
	     */
	    @BeforeClass
	    public static final void getItemList() throws Exception
	    {
	    	pubManItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_PUBMAN, TestHelper.ITEMS_LIMIT);
			assertFalse("PubMan item list from framework is empty", pubManItemList == null || pubManItemList.trim().equals("") );
			logger.info("PubMan item list from framework:\n" + pubManItemList);
			
//			FileOutputStream fos = new FileOutputStream("target/pubManItemList.xml");
//			fos.write(pubManItemList.getBytes());				
//			fos.close();	        
			
			facesItemList = TestHelper.getItemListFromFramework(TestHelper.CONTENT_MODEL_FACES, "2");
			assertFalse("Faces item list from framework is empty", facesItemList == null || facesItemList.trim().equals("") );
			logger.info("Faces item list from framework:\n" + facesItemList);
			
//			FileOutputStream fos = new FileOutputStream("target/FacesItemList.xml");
//			fos.write(facesItemList.getBytes());				
//			fos.close();	        
	    }

	    
	    
	    /**
	     * Test explainExport XML file
	     * @throws Exception Any exception.
	     */
	    @Test
	    public final void testExplainExportXML() throws Exception
	    {
	    	String result = export.explainFormatsXML(); 
	        assertFalse("explain formats file is null", result == null || result.trim().equals("") );
	        logger.info("explain formats: " + result);
	    }
	    
	    
	    
	    /**
	     * Test generate output into archive.
	     * @throws Exception Any exception.
	     */
	    @Test 
	    public final void testExportsToArchives() throws Exception
	    {
	    	logger.info("heapMaxSize = " + Runtime.getRuntime().maxMemory());

	    	logger.info("Exports to the archive file:");    
	    	File f; 
	    	for (ArchiveFormats af : ArchiveFormats.values())
	    	{
	    		String afString = af.toString();
	    		start = -System.currentTimeMillis();
	    		f = export.getOutputFile("CSV", null, afString, facesItemList); 
	    		start += System.currentTimeMillis();
	    		assertFalse(afString + " generation failed", f == null || f.length() == 0);
	    		logger.info(afString + " generation is OK (" + (start) + "ms), " +
	    				"file name:" + f.getCanonicalPath() + 
	    				", file size:" + f.length() 
	    		);
	    		f.delete();
	    	}
	    	logger.info("End of the exports to the archive files.");    

	    	logger.info("Exports to the byte[]:");    
	    	byte [] ba;
	    	FileOutputStream fos;
	    	for (ArchiveFormats af : ArchiveFormats.values())
	    	{
	    		String afString = af.toString();
	    		start = -System.currentTimeMillis();
	    		ba = export.getOutput("CSV", null, afString, facesItemList); 
	    		start += System.currentTimeMillis();
	    		assertFalse(afString + " generation failed", ba == null || ba.length == 0);
	    		logger.info(afString + " generation is OK (" + (start) + "ms), " +
	    				"byte array size:" + ba.length 
	    		);
	    		
//				afString = afString.equals(ArchiveFormats.gzip.toString()) ? "tar.gz" : afString; 
//				fos = new FileOutputStream("target/output." + afString);
//				fos.write(ba);				
//				fos.close();
				
	    	}
	    	logger.info("End of the exports to the byte[].");    


	    }	    
	    
	    /**
	     * Test Exports with a item list XML.
	     * @throws Exception Any exception.
	     */	    
	    @Test 
	    public final void testExports() throws Exception
	    {
	    	
	    	byte[] result; 
	    	for ( String ef : new String[] { "ENDNOTE", "BIBTEX", "APA" })
	    	{
		    	logger.info("start " + ef + " export ");
		        start = -System.currentTimeMillis();
		    	result = export.getOutput(
		    			ef,
		    			ef.equals("APA") ? "escidoc_snippet" : null,	
		    			null, 
		    			pubManItemList
		    	);
		    	start += System.currentTimeMillis();
		    	assertFalse(ef + " export failed", result == null || result.length == 0);
		    	logger.info(ef + " export (" + start + "ms):\n" + new String(result));
	    		
	    	}
	    	
	    }

	     
    

}
