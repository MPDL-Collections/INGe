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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.exportmanager;

import java.io.File;
import java.io.IOException;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

/**
* This class provides the ejb implementation of the {@link ExportHandler} interface.
* 
* @author Vladislav Makarenko  (initial creation)
* @author $Author$ (last modification)
* @version $Revision$,  $LastChangedDate$
*/
 
@Stateless
@Local

public class ExportHandlerBean implements ExportHandler
{ 
	
	// private static final long serialVersionUID = 1L;
    
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(ExportHandlerBean.class);
    
    private Export exportService = new Export();

    
    /**
     * {@inheritDoc}
     */
	public String explainFormatsXML() throws ExportManagerException, IOException 
	{
        return exportService.explainFormatsXML();
	}

    /**
     * {@inheritDoc}
     * @throws IOException  
     */
	public File getOutputFile(String exportFormat,
			String outputFormat,
			String archiveFormat,
			String itemList)
			throws ExportManagerException, IOException  
	{
        return exportService.getOutputFile( exportFormat, outputFormat, archiveFormat, itemList );
	}

    /**
     * {@inheritDoc}  
     * @throws IOException 
     */
	public File generateArchiveFile(String exportFormat, String archiveFormat,
			byte[] exportOut, String itemListFiltered)
			throws ExportManagerException, IOException 
	{
		return exportService.generateArchiveFile(exportFormat, archiveFormat, exportOut, itemListFiltered);
	}
	
	/**
	 * {@inheritDoc}  
	 * @throws IOException 
	 */
	public File generateArchiveFile(String exportFormat, String archiveFormat,
			byte[] exportOut, String itemListFiltered, File license)
	throws ExportManagerException, IOException 
	{
		return exportService.generateArchiveFile(exportFormat, archiveFormat, exportOut, itemListFiltered, license);
	}


    /**
     * {@inheritDoc}  
     * @throws IOException 
     */
	public byte[] generateArchive(String exportFormat, String archiveFormat,
			byte[] export, String itemListFiltered)
			throws ExportManagerException, IOException {
		return exportService.generateArchive(exportFormat, archiveFormat, export, itemListFiltered);
	}
	
	/**
	 * {@inheritDoc}  
	 * @throws IOException 
	 */
	public byte[] generateArchive(String exportFormat, String archiveFormat,
			byte[] export, String itemListFiltered, File license)
	throws ExportManagerException, IOException {
		return exportService.generateArchive(exportFormat, archiveFormat, export, itemListFiltered, license);
	}
	
	/** 
	 * {@inheritDoc}  
	 * @throws IOException 
	 */
	public byte[] generateArchive(String archiveFormat,
			String itemListFiltered)
	throws ExportManagerException, IOException {
		return exportService.generateArchive(archiveFormat, itemListFiltered);
	}
	
	/** 
	 * {@inheritDoc}  
	 * @throws IOException 
	 */
	public byte[] generateArchive(String archiveFormat,
			String itemListFiltered, File license)
	throws ExportManagerException, IOException {
		return exportService.generateArchive(archiveFormat, itemListFiltered, license);
	}

    /** 
     * {@inheritDoc}  
     * @throws IOException 
     */
	public byte[] getOutput(String exportFormat, String outputFormat,
			String archiveFormat, String itemList)
			throws ExportManagerException, IOException {
		return exportService.getOutput(exportFormat, outputFormat, archiveFormat, itemList);
	}

	/**
	 * {@inheritDoc}  
	 * @throws IOException 
	 */
	public long calculateItemListFileSizes(String itemList)
			throws ExportManagerException {
		return exportService.calculateItemListFileSizes(itemList);
	}

	

}
