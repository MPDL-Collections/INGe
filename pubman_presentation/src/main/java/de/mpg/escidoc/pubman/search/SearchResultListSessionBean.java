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

package de.mpg.escidoc.pubman.search;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;

/**
 * Keeps all attributes that are used for the whole session by the SearchResultList.
 * @author:  Thomas Diebäcker; Tobias Schraut, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public class SearchResultListSessionBean extends FacesBean
{
    private static final long serialVersionUID = 1L;
    
    public static final String BEAN_NAME = "SearchResultListSessionBean";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(SearchResultListSessionBean.class);

    private String searchString = new String();
    private boolean includeFiles = false;
    
    //Search criteria in case of advanced search
    private ArrayList<MetadataSearchCriterion> criteria;
    
    //Affiliation in case of affiliation search
    private AffiliationVO affiliation;
   
    
    private SearchType type;

    /**
     * Public constructor.
     */
    public SearchResultListSessionBean()
    {
        this.init();
        
    }

    /**
     * This method is called when this bean is initially added to session scope. Typically, this occurs as a result of
     * evaluating a value binding or method binding expression, which utilizes the managed bean facility to instantiate
     * this bean and store it into session scope.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    public String getSearchString()
    {
        return searchString;
    }

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public boolean getIncludeFiles()
    {
        return includeFiles;
    }

    public void setIncludeFiles(boolean includeFiles)
    {
        this.includeFiles = includeFiles;
    }

    public SearchType getType()
    {
        return type;
    }

    public void setType(SearchType type)
    {
        this.type = type;
    }
    
    public enum SearchType 
    {
        NORMAL_SEARCH, ADVANCED_SEARCH, AFFILIATION_SEARCH;
    }

    public ArrayList<MetadataSearchCriterion> getSearchCriteria()
    {
        return this.criteria;
    }

    public void setSearchCriteria(ArrayList<MetadataSearchCriterion> criteria )
    {
        this.criteria = criteria;
    }

    public AffiliationVO getAffiliation()
    {
        return affiliation;
    }

    public void setAffiliation(AffiliationVO affiliation)
    {
        this.affiliation = affiliation;
    }

}
