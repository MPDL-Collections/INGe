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

package de.mpg.escidoc.pubman.desktop;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.ViewItemRevisionsPage;
import de.mpg.escidoc.pubman.affiliation.AffiliationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.home.Home;
import de.mpg.escidoc.pubman.itemLog.ViewItemLog;
import de.mpg.escidoc.pubman.releases.ItemVersionListSessionBean;
import de.mpg.escidoc.pubman.releases.ReleaseHistory;
import de.mpg.escidoc.pubman.revisions.CreateRevision;
import de.mpg.escidoc.pubman.revisions.RelationListSessionBean;
import de.mpg.escidoc.pubman.search.AdvancedSearchEdit;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.NavigationRule;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * Navigation.java Backing Bean for the Navigation side bar of pubman. Additionally there is some internationalization
 * functionality (language switching).
 *
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 16.08.2007
 */
public class Navigation extends FacesBean
{
    private static Logger logger = Logger.getLogger(Navigation.class);

    public static final String BEAN_NAME = "Navigation";
    private List<NavigationRule> navRules;
    
    private boolean showExportMenuOption;
  
    /**
     * Public constructor.
     */
    public Navigation()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {

        // Perform initializations inherited from our superclass
        super.init();
        // initially sets the navigation rules for redirecting after changing the language
        navRules = new ArrayList<NavigationRule>();
        this.navRules.add(new NavigationRule("/faces/HomePage.jsp",
                Home.LOAD_HOME));
        this.navRules.add(new NavigationRule("/faces/DepositorWSPage.jsp",
                MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS));
        this.navRules.add(new NavigationRule("/faces/EditItemPage.jsp",
                EditItem.LOAD_EDITITEM));
        this.navRules.add(new NavigationRule("/faces/viewItemFullPage.jsp",
                ViewItemFull.LOAD_VIEWITEM));
        this.navRules.add(new NavigationRule("/faces/SearchResultListPage.jsp",
                SearchRetrieverRequestBean.LOAD_SEARCHRESULTLIST));
        this.navRules.add(new NavigationRule("/faces/AffiliationTreePage.jsp",
                AffiliationBean.LOAD_AFFILIATION_TREE));
        this.navRules.add(new NavigationRule("/faces/ViewItemRevisionsPage.jsp",
                ViewItemRevisionsPage.LOAD_VIEWREVISIONS));
        this.navRules.add(new NavigationRule("/faces/ViewItemReleaseHistoryPage.jsp",
                ReleaseHistory.LOAD_RELEASE_HISTORY));
        this.navRules.add(new NavigationRule("/faces/AdvancedSearchPage.jsp",
                AdvancedSearchEdit.LOAD_SEARCHPAGE));
        this.navRules.add(new NavigationRule("/faces/EasySubmissionPage.jsp",
                EasySubmission.LOAD_EASYSUBMISSION));
    }

    /**
     * loads the home page.
     *
     * @return String navigation string (JSF navigation) to load the home page.
     */
    public String loadHome()
    {
        return Home.LOAD_HOME;
    }

    /**
     * loads the help page.
     *
     * @return String navigation string (JSF navigation) to load the help page.
     */
    public String loadHelp()
    {
        return "loadHelp";
    }

    /**
     * Changes the language within the application. Some classes have to be treated especially.
     *
     * @return String navigation string (JSF navigation) to reload the page the user has been when changing the language
     */
    public String changeLanguage()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        // initialize the nav string with null. if it won't be changed the page would just be reloaded
        String navigationString = "";

        ViewItemFull viewItem;
        EditItem editItem;
        CreateRevision createRevision;
        ReleaseHistory releaseHistory;
        ViewItemLog viewItemLog;

        // special re-initializaion for pages with dynamic page elements which
        // must be re-inited

        //logger.debug("Resolving current page URI: " + request.getRequestURI());
        String requestURI = request.getRequestURI();
        
        if (requestURI.startsWith("/pubman"))
        {
            requestURI = requestURI.substring("/pubman".length());
            
        }
        logger.debug("Resolving current page URI: " + requestURI);
        for (int i = 0; i < navRules.size(); i++)
        {
            if (requestURI.equals(navRules.get(i).getRequestURL()))
            {
                navigationString = navRules.get(i).getNavigationString();
                break;
            }
        }
        if (navigationString.equals(EditItem.LOAD_EDITITEM))
        {
            editItem = (EditItem) getRequestBean(EditItem.class);
            editItem.init();
        }
        
        else if (navigationString.equals(ViewItemFull.LOAD_VIEWITEM))
        {
            viewItem = (ViewItemFull) getRequestBean(ViewItemFull.class);
            viewItem.init();
        }
        else if (navigationString.equals(ViewItemRevisionsPage.LOAD_VIEWREVISIONS))
        {
            createRevision = (CreateRevision) getRequestBean(CreateRevision.class);
            createRevision.init();
        }
        else if (navigationString.equals(ReleaseHistory.LOAD_RELEASE_HISTORY))
        {
            this.getItemVersionSessionBean().resetVersionLists();
            releaseHistory = (ReleaseHistory) getRequestBean(ReleaseHistory.class);
            releaseHistory.init();
        }
        else if (navigationString.equals(ViewItemLog.LOAD_ITEM_LOG))
        {
            this.getItemVersionSessionBean().resetVersionLists();
            viewItemLog = (ViewItemLog) getRequestBean(ViewItemLog.class);
            viewItemLog.init();
        }
        else if (navigationString.equals(EasySubmission.LOAD_EASYSUBMISSION))
        {
            EasySubmission easy = (EasySubmission) getRequestBean(EasySubmission.class);
           
            easy.init();
        }
        else
        {
            navigationString = null;
        }
        return navigationString;
    }

    /**
     * Starts a new submission.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String newSubmission()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }


        // if there is only one context for this user we can skip the
        // CreateItem-Dialog and create the new item directly
        if (this.getCollectionListSessionBean().getDepositorContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            return null;
        }
        if (this.getCollectionListSessionBean().getDepositorContextList().size() == 1)
        {
            ContextVO contextVO = this.getCollectionListSessionBean().getDepositorContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one context (ID: "
                        + contextVO.getReference().getObjectId() + ")");
            }

            return this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
                    contextVO.getReference());
        }
        else
        {
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getCollectionListSessionBean().getDepositorContextList().size()
                        + " different contexts.");
            }

            return CreateItem.LOAD_CREATEITEM;
        }
    }

    /**
     * Gets the parameters out of the faces context.
     *
     * @return The value
     */
    public static String getFacesParamValue(final String name)
    {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }


    /**
     * Returns the AffiliationSessionBean.
     *
     * @return a reference to the scoped data bean (AffiliationSessionBean)
     */
    protected AffiliationBean getAffiliationBean()
    {
        return (AffiliationBean) getSessionBean(AffiliationBean.class);
    }

    /**
     * Returns the ReleasesSessionBean.
     *
     * @return a reference to the scoped data bean (ReleasesSessionBean)
     */
    protected ItemVersionListSessionBean getItemVersionSessionBean()
    {
        return (ItemVersionListSessionBean) getSessionBean(ItemVersionListSessionBean.class);
    }

    /**
     * Returns the RevisionListSessionBean.
     *
     * @return a reference to the scoped data bean (RevisionListSessionBean)
     */
    protected RelationListSessionBean getRevisionListSessionBean()
    {
        return (RelationListSessionBean) getSessionBean(RelationListSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getCollectionListSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
     * @return a reference to the scoped data bean.
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
    }

    /**
     * Returns the AdvancedSearchEdit session bean.
     * @return AdvancedSearchEdit session bean.
     */
    protected AdvancedSearchEdit getAdvancedSearchEdit()
    {
        return (AdvancedSearchEdit) getSessionBean(AdvancedSearchEdit.class);
    }

    public void setShowExportMenuOption(boolean showExportMenuOption)
    {
        this.showExportMenuOption = showExportMenuOption;
    }

    public boolean getShowExportMenuOption()
    {
        return showExportMenuOption;
    }

}
