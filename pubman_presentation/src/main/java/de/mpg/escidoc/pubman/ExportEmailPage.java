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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.pubman;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;

/**
 * ExportEmailPage.java Backing bean for the ExportEmailPage.jsp
 * 
 * @author: Galina Stancheva, created 07.10.2007
 * @version: $Revision$ $LastChangedDate$ 
 */
public class ExportEmailPage extends BreadcrumbPage
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ExportEmailPage.class);

    /**
     * Public constructor
     */
    public ExportEmailPage()
    {
        this.init();
    }


    /**
     * Callback method that is called whenever a page is navigated to, either directly via a URL, or indirectly via page
     * navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
 
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        
        sb.setNavigationStringToGoBack(SearchRetrieverRequestBean.LOAD_SEARCHRESULTLIST);    
        sb.setExportEmailTxt(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
        sb.setEnableExport(false);
    }

    /*
     * Handle messages in fragments from here to please JSF life cycle.
     * Used to remove the last shown msg
     * @author: Michael Franke
     */
    @Override
    public void prerender()
    {
        super.prerender();
//        fragment.handleMessage();

    }
    @Override
    public boolean isItemSpecific() {
        return false;
    }

}