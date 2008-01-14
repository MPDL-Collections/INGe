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

package de.mpg.escidoc.pubman.revisions;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.sun.rave.web.ui.appbase.AbstractSessionBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.revisions.ui.RevisionListUI;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;

/**
 * Keeps all attributes that are used for the whole session by the RevisionList.
 * @author:  Thomas Diebäcker, created 22.10.2007
 * @version: $Revision: 1599 $ $LastChangedDate: 2007-11-21 20:51:24 +0100 (Wed, 21 Nov 2007) $
 */
public class RevisionListSessionBean extends AbstractSessionBean
{
    public static final String BEAN_NAME = "revisions$RevisionListSessionBean";
    private static Logger logger = Logger.getLogger(RevisionListSessionBean.class);

    private List<RelationVOWrapper> relationVOWrapperList = new ArrayList<RelationVOWrapper>();
    private RevisionListUI revisionListUI = null;
    private PubItemVO pubItemVO = null;
    private String revisionDescription = new String();

    /**
     * Public constructor.
     */
    public RevisionListSessionBean()
    {        
    }

    /**
     * Retrieves all RevisionWrappers for the current item.
     * @return the list of RelationVOWrappers
     */
    private List<RelationVOWrapper> retrieveRevisions(PubItemVO pubItemVO)
    {
        List<RelationVOWrapper> allRevisions = new ArrayList<RelationVOWrapper>();
        
        try
        {
            allRevisions = this.getItemControllerSessionBean().retrieveRevisions(pubItemVO); 
        }
        catch (Exception e)
        {
            logger.error("Could not create revision list." + "\n" + e.toString());
        }

        return allRevisions;
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getBean(ItemControllerSessionBean.BEAN_NAME);
    }

    public RevisionListUI getRevisionListUI()
    {
        return revisionListUI;
    }

    public void setRevisisonListUI(RevisionListUI revisisonListUI)
    {
        this.revisionListUI = revisisonListUI;
    }

    public List<RelationVOWrapper> getRelationVOWrapperList()
    {
        // lazy initialize relation list if pubItem is set
        if ((this.relationVOWrapperList == null || this.relationVOWrapperList.size() == 0) && this.pubItemVO != null)
        {
            this.relationVOWrapperList = this.retrieveRevisions(this.pubItemVO);
        }
        
        return relationVOWrapperList;
    }

    public void setRelationVOWrapperList(List<RelationVOWrapper> relationVOWrapperList)
    {
        this.relationVOWrapperList = relationVOWrapperList;
    }

    public PubItemVO getPubItemVO()
    {
        return pubItemVO;
    }

    public void setPubItemVO(PubItemVO pubItemVO)
    {
        // re-init the lists as this is a new PubItem
        this.setRelationVOWrapperList(null);
        this.setRevisisonListUI(null);
        this.setRevisionDescription(null);
        
        this.pubItemVO = pubItemVO;
    }

    public String getRevisionDescription()
    {
        return revisionDescription;
    }

    public void setRevisionDescription(String revisionDescription)
    {
        this.revisionDescription = revisionDescription;
    }
}
