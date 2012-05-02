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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.createItem;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;

/**
 * Fragment class for CreateItem.
 * 
 * @author: Thomas Diebäcker, created 11.10.2007
 * @author: $Author$ last modification
 * @version: $Revision$ $LastChangedDate$ 
 */
public class CreateItem extends FacesBean
{
    
    public enum SubmissionMethod
    {
        FULL_SUBMISSION, MULTIPLE_IMPORT, EASY_SUBMISSION
    }
    
    public static final String BEAN_NAME = "CreateItem";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(CreateItem.class);
    
    private String target = EditItem.LOAD_EDITITEM;
    
    private SubmissionMethod method = SubmissionMethod.FULL_SUBMISSION;
    
    // Faces navigation string
    public final static String LOAD_CREATEITEM = "loadCreateItem";

    /**
     * Public constructor.
     */
    public CreateItem()
    {
        this.init();
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
        super.init();

    }
    
    public String confirmSelection()
    {
        return target;
    }

    /**
     * Starts a new submission.
     *
     * @return string, identifying the page that should be navigated to after this methodcall
     */
    public String newSubmission()
    {
        
        target = EditItem.LOAD_EDITITEM;
        method = SubmissionMethod.FULL_SUBMISSION;
        String genreBundle = "Genre_ARTICLE";
        
        String navigateTo = "";
        if (logger.isDebugEnabled())
        {
            logger.debug("New Submission");
        }
        
        // first clear the EditItemSessionBean
        this.getEditItemSessionBean().initEmptyComponents();
        
        // set the current submission method for edit item to full submission (for GUI purpose)
        this.getEditItemSessionBean().setCurrentSubmission(EditItemSessionBean.SUBMISSION_METHOD_FULL_SUBMISSION);
        
        
        // if there is only one context for this user we can skip the CreateItem-Dialog and
        // create the new item directly
        if (this.getContextListSessionBean().getDepositorContextList().size() == 0)
        {
            logger.warn("The user does not have privileges for any context.");
            return null;
        }
        
        if (this.getContextListSessionBean().getDepositorContextList().size() == 1 && this.getContextListSessionBean().getOpenContextsAvailable())
        {
            ContextVO contextVO = this.getContextListSessionBean().getDepositorContextList().get(0);
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has only privileges for one context (ID: "
                        + contextVO.getReference().getObjectId() + ")");
            }
            navigateTo = this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
                    contextVO.getReference());
            
            // re-init the edit item bean to make sure that all data is removed
            if(this.getItemControllerSessionBean().getCurrentPubItem() != null)
            {
                if (!contextVO.getAdminDescriptor().getAllowedGenres().contains(MdsPublicationVO.Genre.ARTICLE))
                {
                    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setGenre(contextVO.getAdminDescriptor().getAllowedGenres().get(0));
                }
                else
                {
                    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setGenre(MdsPublicationVO.Genre.ARTICLE);
                }
                this.getEditItemSessionBean().setGenreBundle(genreBundle);
                this.getEditItem().setItem(null);
                this.getEditItem().getGenreSelect().resetValue();
                this.getEditItem().init();
            }
            return navigateTo;
        }
        else
        {
            // more than one context exists for this user; let him choose the right one
            if (logger.isDebugEnabled())
            {
                logger.debug("The user has privileges for "
                        + this.getContextListSessionBean().getDepositorContextList().size() + " different contexts.");
            }
            navigateTo = this.getItemControllerSessionBean().createNewPubItem(CreateItem.LOAD_CREATEITEM,
                    this.getContextListSessionBean().getDepositorContextList().get(0).getReference());
            
            // re-init the edit item bean to make sure that all data is removed
            if(this.getItemControllerSessionBean().getCurrentPubItem() != null)
            {
                this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setGenre(Genre.ARTICLE);
                this.getEditItemSessionBean().setGenreBundle(genreBundle);
                this.getEditItem().setItem(null);
                this.getEditItem().setIdentifierIterator(new UIXIterator());
                this.getEditItem().init();
            }
            return navigateTo;
        }
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getContextListSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }

    /**
     * Returns the ContextListSessionBean.
     *
     * @return a reference to the scoped data bean (ContextListSessionBean)
     */
    protected ContextListSessionBean getSessionBean()
    {
        return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    }
    
    /**
     * Returns the ItemListSessionBean.
     * @return a reference to the scoped data bean (ItemListSessionBean)
     */
    protected EditItemSessionBean getEditItemSessionBean()
    {
        return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
    }
    
    /**
     * Returns the EditItem.
     * @return a reference to the scoped data bean (EditItem)
     */
    protected EditItem getEditItem()
    {
        return (EditItem)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                EditItem.BEAN_NAME);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

    public List<PubContextVOPresentation> getCurrentCollectionList() {
        return getSessionBean().getDepositorContextList();
    }

    public boolean getMultiple()
    {
        return (getMethod() == SubmissionMethod.MULTIPLE_IMPORT);
    }
    
    /**
     * @return the target
     */
    public String getTarget()
    {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target)
    {
        this.target = target;
    }

    /**
     * @return the method
     */
    public SubmissionMethod getMethod()
    {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(SubmissionMethod method)
    {
        this.method = method;
    }

}
