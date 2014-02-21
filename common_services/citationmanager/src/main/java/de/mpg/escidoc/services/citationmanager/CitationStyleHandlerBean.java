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

package de.mpg.escidoc.services.citationmanager;

import java.io.IOException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleExecutor;



/**
 * EJB implementation of interface {@link CitationStyleHandler}. It will use an external package.
 * It can be considered as a wrapper of the external package.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * Revised by StG: 24.08.2007
 */
@Stateless
@Local
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CitationStyleHandlerBean implements CitationStyleHandler
{ 
    /**
     * Logger for this class.
     */
    private static Logger logger = Logger.getLogger(CitationStyleHandlerBean.class);
    CitationStyleExecutor cse = new CitationStyleExecutor();
    
    
    /**
     * {@inheritDoc}
     * @throws IOException 
     * @throws IllegalArgumentException 
     */
     public String explainStyles() throws CitationStyleManagerException, IllegalArgumentException, IOException
     {           
    	 return cse.explainStyles();
     }

     /**
      * {@inheritDoc}
      */
    public byte[] getOutput(String citationStyle, String outputFormat, String itemList)
        throws JRException, CitationStyleManagerException, IOException
    {
        logger.debug("CitationStyleHandlerBean getOutput with citationStyle: " + citationStyle);
        return cse.getOutput(citationStyle, outputFormat, itemList);
    }

    /**
     * {@inheritDoc}
     */
	public boolean isCitationStyle(String citationStyle)
			throws CitationStyleManagerException {
		return cse.isCitationStyle(citationStyle);
	}

    /**
     * {@inheritDoc}
     */
	public String[] getStyles() throws CitationStyleManagerException {
		return cse.getStyles();
	}
	
    /**
     * {@inheritDoc}
     */
	public String[] getOutputFormats(String cs) throws CitationStyleManagerException {
		return cse.getOutputFormats(cs);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getMimeType(String cs, String ouf) throws CitationStyleManagerException {
		return cse.getMimeType(cs, ouf);
	}
	
}
