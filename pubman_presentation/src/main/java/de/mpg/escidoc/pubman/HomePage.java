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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.desktop.Search;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * BackingBean for HomePage.jsp.
 *
 * @author: Thomas Diebäcker, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class HomePage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(HomePage.class);
    public static final String BEAN_NAME = "HomePage";
    
    /**
     * Public constructor.
     */
    public HomePage()
    {
        this.init();
        
    }
    
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (parameters.containsKey("expired"))
        {
            error(getMessage("LoginErrorPage_loggedOffFromSystem"));
        }
        else if (parameters.containsKey("logout"))
        {
            info(getMessage("LogoutMessage"));
        }
        // Perform initializations inherited from our superclass
        super.init();

    }

   
    
    /**
     * Reads the blog URL from the properties file.
     * Needed for blogintegration on homepage
     * @return blodUrl as String
     */
    public String getBlogBaseUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.blog.baseUrl");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.blog.baseUrl' from properties file.", e);
        }

        return url;
    }
    
    /**
     * Reads the policy URL from the properties file.
     * @return policyUrl as String
     */
    public String getPolicyUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.policy.url");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.policy.url' from properties file.", e);
        }

        return url;
    }
    
    /**
     * Reads the contact URL from the properties file.
     * @return contactUrl as String
     */
    public String getContactUrl()
    {
        String url = "";
        try
        {
            url = PropertyReader.getProperty("escidoc.pubman.contact.url");
        }
        catch (Exception e)
        {
            HomePage.logger.error("Could not read property: 'escidoc.pubman.contact.url' from properties file.", e);
        }

        return url;
    }
    
    public boolean isDepositor()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        return loginHelper.getAccountUser().isDepositor();
    }
    
    public boolean isModerator()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        return loginHelper.getAccountUser().isModerator();
    }

	@Override
	public boolean isItemSpecific() 
	{
		return false;
	}
	
	
}
