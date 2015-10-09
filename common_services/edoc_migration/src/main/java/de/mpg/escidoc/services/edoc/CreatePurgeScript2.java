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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.edoc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;


/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CreatePurgeScript2
{
    private static final Logger logger = Logger.getLogger(CreatePurgeScript.class);
    
    private static String CORESERVICES_URL;
    //private static final String IMPORT_CONTEXT = "escidoc:31013";
    //private static final String IMPORT_CONTEXT = "escidoc:54203";
    private static final String IMPORT_CONTEXT = "escidoc:57277";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        CORESERVICES_URL = PropertyReader.getProperty("escidoc.framework_access.framework.url");
        
        String userHandle = AdminHelper.loginUser(args[0], args[1]);
        
        logger.info("Querying core-services...");
        HttpClient httpClient = new HttpClient();
        String filter = "<param><filter name=\"http://escidoc.de/core/01/structural-relations/context\">" + IMPORT_CONTEXT + "</filter><filter name=\"/properties/public-status\">released</filter><order-by>http://escidoc.de/core/01/properties/creation-date</order-by><limit>0</limit></param>";

        PostMethod postMethod = new PostMethod(CORESERVICES_URL + "/ir/items/filter");
        postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
        postMethod.setRequestBody(filter);
        
        ProxyHelper.executeMethod(httpClient, postMethod);
        String response = postMethod.getResponseBodyAsString();
        logger.info("...done!");
        
        FileWriter xmlData = new FileWriter("xmlData.xml");
        xmlData.write(response);
        xmlData.close();
        //System.out.println(response);
        
        logger.info("Transforming result...");
        XSLTTransform transform = new XSLTTransform();
        File stylesheet = new File("src/main/resources/itemlist2purgescript.xslt");
        FileOutputStream outputStream = new FileOutputStream("purge.sh");
        transform.transform(response, stylesheet, outputStream);
        logger.info("...done!");
        
        logger.info("Finished!");
    }
    

}
