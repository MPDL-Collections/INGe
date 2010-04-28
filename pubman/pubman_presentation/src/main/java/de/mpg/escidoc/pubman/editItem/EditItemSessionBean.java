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
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman.editItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorBean;
import de.mpg.escidoc.pubman.util.OrganizationVOPresentation;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Keeps all attributes that are used for the whole session by the EditItem.
 * 
 * @author: Tobias Schraut, created 26.02.2007
 * @version: $Revision$ $LastChangedDate: 2007-11-13 10:54:07 +0100 (Di, 13
 *           Nov 2007) $
 */
public class EditItemSessionBean extends FacesBean 
{
	public static final String BEAN_NAME = "EditItemSessionBean";
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(EditItemSessionBean.class);

	private List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
	
	private List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
	
	private List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();
	
	private String genreBundle = "Genre_ARTICLE";
	
	 /**The offset of the page where to jump back*/
    private String offset;
    
    /**The string with authors to parse for author copy&paste.*/
    private String creatorParseString;
    
    /**Checkbox if existing authors should be overwritten with the ones from author copy/paste*/
    private boolean overwriteCreators;
    
    /**
     * A creator bean that holds the data from the author copy&paste organizations
     */
    private CreatorBean authorCopyPasteOrganizationsCreatorBean;
   
    /**
     * Stores a string from a hidden input field (set by javascript) that indicates whether the author copy&paste elements are to be displayed or not.
     */
    private String showAuthorCopyPaste;
    
    public static final String SUBMISSION_METHOD_FULL_SUBMISSION = "FULL_SUBMISSION";
    public static final String SUBMISSION_METHOD_EASY_SUBMISSION = "EASY_SUBMISSION";
    public static final String SUBMISSION_METHOD_IMPORT = "IMPORT";
    
    /**
     * Flag for the GUI to detect if the edit item page is called for a submission or for an editing process
     */
    private String currentSubmission = "";

    /**
	 * Public constructor.
	 */
	public EditItemSessionBean() 
	{
		this.init();
	}

	/**
	 * This method is called when this bean is initially added to session scope.
	 * Typically, this occurs as a result of evaluating a value binding or
	 * method binding expression, which utilizes the managed bean facility to
	 * instantiate this bean and store it into session scope.
	 */
	public void init() 
	{
		// Perform initializations inherited from our superclass
		super.init();
		initAuthorCopyPasteCreatorBean();
	}
	
	/**
	 * This method clears the file and the locator list
	 */
	public void initEmptyComponents()
	{
		clean();
		
		// make sure that at least one locator and one file is stored in the  EditItemSessionBean
    	if(this.getFiles().size() < 1)
    	{
    	    FileVO newFile = new FileVO();
    	    newFile.getMetadataSets().add(new MdsFileVO());
    	    newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    		this.getFiles().add(new PubFileVOPresentation(this.getFiles().size(), newFile, false));
    	}
    	if(this.getLocators().size() < 1)
    	{
    		FileVO newLocator = new FileVO();
    		newLocator.getMetadataSets().add(new MdsFileVO());
    		newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
    		this.getLocators().add(new PubFileVOPresentation(0, newLocator, true));
    	}
    	
    	initAuthorCopyPasteCreatorBean();
	}

	/**
	 * 
	 */
	public void clean() {
		this.files.clear();
		this.locators.clear();
		this.creatorOrganizations.clear();
		this.genreBundle = "";
		this.offset="";
		this.showAuthorCopyPaste = "";
	}
	
	/**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeFileIndexes()
    {
    	if(this.files != null)
    	{
    		for(int i = 0; i < this.files.size(); i++)
        	{
        		this.files.get(i).setIndex(i);
        	}
    	}
    }
    
    
    
    /**
     * This method reorganizes the index property in PubFileVOPresentation after removing one element of the list.
     */
    public void reorganizeLocatorIndexes()
    {
    	if(this.locators != null)
    	{
    		for(int i = 0; i < this.locators.size(); i++)
        	{
        		this.locators.get(i).setIndex(i);
        	}
    	}
    }

	public List<PubFileVOPresentation> getFiles() 
	{
		return files;
	}

	public void setFiles(List<PubFileVOPresentation> files) 
	{
		this.files = files;
	}

	public List<PubFileVOPresentation> getLocators()
	{
		return locators;
	}

	public void setLocators(List<PubFileVOPresentation> locators)
	{
		this.locators = locators;
	}

	public String getGenreBundle() {
		return genreBundle;
	}

	public void setGenreBundle(String genreBundle)
	{
		this.genreBundle = genreBundle;
	}

    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    public String getOffset()
    {
        return offset;
    }
    
    /**
     * (Re)-initializes the PersonOPrganisationManager that manages the author copy&paste organizations.
     */
    public void initAuthorCopyPasteCreatorBean()
    {
        CreatorVO newVO = new CreatorVO();
       
        /*
        newVO.setPerson(new PersonVO());
        OrganizationVO newPersonOrganization = new OrganizationVO();
        newPersonOrganization.setName(new TextVO());
        newPersonOrganization.setAddress("");
        newPersonOrganization.setIdentifier("");
        newVO.getPerson().getOrganizations().add(newPersonOrganization);
        */
        
        CreatorBean dummyCreatorBean = new CreatorBean(newVO);
        this.authorCopyPasteOrganizationsCreatorBean = dummyCreatorBean;
        setCreatorParseString("");
        setShowAuthorCopyPaste("");
    }

    public void initOrganizationsFromCreators(PubItemVO pubItem)
    {
        List<OrganizationVOPresentation> creatorOrganizations = new ArrayList<OrganizationVOPresentation>();
        int counter = 1;
        for (CreatorVO creator : pubItem.getMetadata().getCreators())
        {
            if (creator.getType() == CreatorType.PERSON)
            {
                for (OrganizationVO organization : creator.getPerson().getOrganizations())
                {
                    if (!creatorOrganizations.contains(organization))
                    {
                        OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation(organization);
                        if (!organizationPresentation.isEmpty() || (creatorOrganizations.isEmpty() && creator == pubItem.getMetadata().getCreators().get(pubItem.getMetadata().getCreators().size() - 1)))
                        {
	                        organizationPresentation.setNumber(counter);
	                        organizationPresentation.setList(creatorOrganizations);
	                        if (organizationPresentation.getName() ==  null)
	                        {
	                            organizationPresentation.setName(new TextVO());
	                        }
	                        creatorOrganizations.add(organizationPresentation);
	                        counter++;
                        }
                    }
                }
            }
        }
        this.creatorOrganizations = creatorOrganizations;
    }

    public int getOrganizationCount()
    {
        return getCreatorOrganizations().size();
    }

    /**
     * Sets the CreatorBean that manages the author copy&paste organizations.
     * @param authorCopyPasteOrganizationsCreatorBean
     */
    public void setAuthorCopyPasteOrganizationsCreatorBean(CreatorBean authorCopyPasteOrganizationsCreatorBean)
    {
        this.authorCopyPasteOrganizationsCreatorBean = authorCopyPasteOrganizationsCreatorBean;
    }

    /**
     * Returns the PersonOPrganisationManager that manages the author copy&paste organizations.
     * @return
     */
    public CreatorBean getAuthorCopyPasteOrganizationsCreatorBean()
    {
        return authorCopyPasteOrganizationsCreatorBean;
    }

    public void setCreatorParseString(String creatorParseString)
    {
        this.creatorParseString = creatorParseString;
    }

    public String getCreatorParseString()
    {
        return creatorParseString;
    }

    public void setOverwriteCreators(boolean overwriteCreators)
    {
        this.overwriteCreators = overwriteCreators;
    }

    public boolean getOverwriteCreators()
    {
        return overwriteCreators;
    }
    
    /**
     * Returns the content(set by javascript) from a hidden input field  that indicates whether the author copy&paste elements are to be displayed or not.
     */
    public  String getShowAuthorCopyPaste()
    {
        return showAuthorCopyPaste;
    }

    /**Sets the content from a hidden input field  that indicates whether the author copy&paste elements are to be displayed or not.
     */
    public void setShowAuthorCopyPaste(String showAuthorCopyPaste)
    {
        this.showAuthorCopyPaste = showAuthorCopyPaste;
    }

	public String getCurrentSubmission() {
		return currentSubmission;
	}

	public void setCurrentSubmission(String currentSubmission) {
		this.currentSubmission = currentSubmission;
	}

    /**
     * @return the creatorOrganizations
     */
    public List<OrganizationVOPresentation> getCreatorOrganizations()
    {
        return creatorOrganizations;
    }

    /**
     * @param creatorOrganizations the creatorOrganizations to set
     */
    public void setCreatorOrganizations(List<OrganizationVOPresentation> creatorOrganizations)
    {
        this.creatorOrganizations = creatorOrganizations;
    }
    
    public String readPastedOrganizations()
    {
    	return "";
    }
    
}
