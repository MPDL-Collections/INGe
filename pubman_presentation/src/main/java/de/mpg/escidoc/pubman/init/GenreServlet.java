/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.org/license.
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

package de.mpg.escidoc.pubman.init;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;

public class GenreServlet extends HttpServlet
{

    @Override
    public void init() throws ServletException
    {
        try
        {
        	/*
        	InputStream is = ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.pubman.genres.configuration"), GenreServlet.class.getClassLoader());
        	
        	if(is == null)
        	{
        		is = GenreServlet.class.getResourceAsStream(ResourceUtil.resolveFileName(PropertyReader.getProperty("escidoc.pubman.genres.configuration")));
        	}
        	*/
        	
        	//InputStream defaultIs = GenreServlet.class.getResourceAsStream("WEB-INF/classes/Genres.xml");

         
            //String dir = defaultFile.getAbsolutePath().substring(0, defaultFile.getAbsolutePath().lastIndexOf(File.separator));

        	
        	
        	InputStream file = ResourceUtil.getResourceAsStream(PropertyReader.getProperty("escidoc.pubman.genres.configuration"), GenreServlet.class.getClassLoader());
            //File defaultFile = ResourceUtil.getResourceAsFile("WEB-INF/classes/Genres.xml", GenreServlet.class.getClassLoader());
            //String dir = defaultFile.getAbsolutePath().substring(0, defaultFile.getAbsolutePath().lastIndexOf(File.separator));
        	
        	
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            String jbossHomeDir = System.getProperty("jboss.home.dir");
            DefaultHandler handler = new GenreHandler(jbossHomeDir + "/modules/pubman/main");

            parser.parse(file, handler);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

}
