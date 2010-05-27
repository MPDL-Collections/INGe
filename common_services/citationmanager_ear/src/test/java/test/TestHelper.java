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

package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.citationmanager.ProcessCitationStyles;
import de.mpg.escidoc.services.citationmanager.utils.ResourceUtil;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Helper class for all test classes.
 *
 * @author Johannes M&uuml;ller (initial) 
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 */
public class TestHelper
{

	private static Logger logger = Logger.getLogger(TestHelper.class);
	
	public static final String ITEMS_LIMIT = "10"; 
	public static final String CONTENT_MODEL = "escidoc:persistent4"; 
//	public static final String USER_NAME = "citman_user"; 
//	public static final String USER_PASSWD = "citman_user";
	public static final String CONTEXT_TITLE = "Citation Style Testing Context";
    private static final String PROPERTY_USERNAME_ADMIN = "framework.admin.username";
    private static final String PROPERTY_PASSWORD_ADMIN = "framework.admin.password";
	
	/**
     * Retrieve resource based on a path relative to the classpath.
     * @param fileName The path of the resource.
     * @return The file defined by The given path.
     * @throws FileNotFoundException File not there.
     */
    public final File findFileInClasspath(final String fileName) throws FileNotFoundException
    {
        URL url = getClass().getClassLoader().getResource(fileName);
        if (url == null)
        {
            throw new FileNotFoundException(fileName);
        }
        return new File(url.getFile());
    }

    /**
     * Reads contents from text file and returns it as String.
     *
     * @param fileName Name of input file
     * @return Entire contents of filename as a String
     */
    public static String readFile(final String fileName)
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            try
            {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
                BufferedReader br = new BufferedReader(isr);
                fileBuffer = new StringBuffer();
                while ((line = br.readLine()) != null)
                {
                    fileBuffer.append(line + "\n");
                }
                isr.close();
                fileString = fileBuffer.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return fileString;
    }

    
    public static byte[] readBinFile(final String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	if (!isFileNameNull)
    	{
    		try {
    			int b;                // the byte read from the file
    			BufferedInputStream is = new BufferedInputStream(new FileInputStream(fileName));
    			BufferedOutputStream os = new BufferedOutputStream(baos);
    			while ((b = is.read( )) != -1) {
    				os.write(b);
    			}
    			is.close( );
    			os.close( );
    		}
    		catch (IOException e)
    		{
    			return null;
    		}
    	}
    	return baos.toByteArray();
    }
    
    public static void writeBinFile(byte[] content, String fileName)
    {
    	boolean isFileNameNull = (fileName == null);
    	boolean isEmptyContent = (content.length == 0);
    	if (!isFileNameNull && !isEmptyContent)
    	{
    		try {
    	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
    	        for (byte b : content)
    	            bos.write(b);
    	        bos.close( );
    		}
    		catch (IOException e)
    		{
    		}
    	}
    }
    
    public static Properties getTestProperties(String csName) throws FileNotFoundException, IOException 
    {
    	InputStream is = ResourceUtil.getResourceAsStream(
    			ResourceUtil.getPathToCitationStyles()
    			+ ResourceUtil.CITATIONSTYLES_DIRECTORY 
    			+ csName
    			+ "/test.properties" 
    	); 
    	Properties props = new Properties();
    	try {
			props.load(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return props;
    }
    
    public static String cleanCit(String str) {
    	if (str!=null && !str.trim().equals("")) 
    		str = str.replaceAll("[\\s\t\r\n]+", " ");
    	return str;
    }

 
   
    /**
     * Get itemList from the current Framework instance
     * @param fileName
     * @throws IOException 
     * @throws URISyntaxException 
     * @throws ServiceException 
     */    
    public static String getTestItemListFromFramework() throws IOException, ServiceException, URISyntaxException
    {
    	return getItemListFromFrameworkBase(PropertyReader.getProperty(PROPERTY_USERNAME_ADMIN), PropertyReader.getProperty(PROPERTY_PASSWORD_ADMIN), 
    		"<param>" +
	    		"<filter name=\"/properties/content-model/id\">" + CONTENT_MODEL + "</filter>" +
//	    		"<filter name=\"/properties/context/title\">" + CONTEXT_TITLE + "</filter>" +
	    		"<limit>" + ITEMS_LIMIT + "</limit>" +
	//    		"<filter name=\"/properties/public-status\">pending</filter>" +
    		"</param>"
    	);	
    }
    
    public static String getItemListFromFrameworkBase(String USER, String PASSWD, String filter) throws IOException, ServiceException, URISyntaxException
    {
    	logger.info("Retrieve USER, PASSWD:" + USER + ", " + PASSWD);
    	String userHandle = loginUser(USER, PASSWD); 
    	logger.info("Retrieve filter:" + filter);
    	// see here for filters: https://zim02.gwdg.de/repos/common/trunk/common_services/common_logic/src/main/java/de/mpg/escidoc/services/common/xmltransforming/JiBXFilterTaskParamVOMarshaller.java
    	ItemHandler ch = ServiceLocator.getItemHandler(userHandle);
    	return ch.retrieveItems(filter);
    }
    
    
    
    protected static String loginUser(String userid, String password) throws HttpException, IOException, ServiceException, URISyntaxException
    {
       String frameworkUrl = ServiceLocator.getFrameworkUrl();
        int delim1 = frameworkUrl.indexOf("//");
        int delim2 = frameworkUrl.indexOf(":", delim1);
        
        String host;
        int port;
        
        if (delim2 > 0)
        {
            host = frameworkUrl.substring(delim1 + 2, delim2);
            port = Integer.parseInt(frameworkUrl.substring(delim2 + 1));
        }
        else
        {
            host = frameworkUrl.substring(delim1 + 2);
            port = 80;
        }

    	HttpClient client = new HttpClient();

    	client.getHostConfiguration().setHost( host, port, "http");
    	client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

    	PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
    	login.addParameter("j_username", userid);
    	login.addParameter("j_password", password);

    	client.executeMethod(login);

    	login.releaseConnection();
    	CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
    	Cookie[] logoncookies = cookiespec.match(
    			host, port, "/", false, 
    			client.getState().getCookies());

    	Cookie sessionCookie = logoncookies[0];

    	PostMethod postMethod = new PostMethod("/aa/login");
    	postMethod.addParameter("target", frameworkUrl);
    	client.getState().addCookie(sessionCookie);
    	client.executeMethod(postMethod);

    	if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
    	{
    		throw new HttpException("Wrong status code: " + login.getStatusCode());
    	}

    	String userHandle = null;
    	Header headers[] = postMethod.getResponseHeaders();
    	for (int i = 0; i < headers.length; ++i)
    	{
    		if ("Location".equals(headers[i].getName()))
    		{
    			String location = headers[i].getValue();
    			int index = location.indexOf('=');
    			userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
    			//System.out.println("location: "+location);
    			//System.out.println("handle: "+userHandle);
    		}
    	}

    	if (userHandle == null)
    	{
    		throw new ServiceException("User not logged in.");
    	}
    	return userHandle;
    }    

    

}
