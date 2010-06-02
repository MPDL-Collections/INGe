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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.affiliation.AffiliationTree;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation.ContentCategory;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.common.util.ProxyHelper;

/**
 * Bean for storing the information of files attached to items.
 * 
 * @author: Tobias Schraut, created 25.03.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class FileBean extends FacesBean
{
	private static Logger logger = Logger.getLogger(FileBean.class);
	private FileVO file;
	
	private State itemState;
	private List<SearchHitVO> searchHitList = new ArrayList<SearchHitVO>();
	private List<SearchHitBean> searchHits = new ArrayList<SearchHitBean>();
	private LoginHelper loginHelper;
	
	

    /**
     * Public constructor
     * @param file
     * @param position
     * @param itemState
     */
    public FileBean(FileVO file,  State itemState)
	{
        loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
		this.file = file;
		this.itemState = itemState;
	}
    
    /**
     * Second constructor (used if pubitem has fulltext search hits)
     * @param file
     * @param position
     * @param itemState
     * @param resultitem
     */
    public FileBean(FileVO file, State itemState, List<SearchHitVO> searchHitList)
	{
        loginHelper = (LoginHelper)getSessionBean(LoginHelper.class);
		this.file = file;
		this.itemState = itemState;
		this.searchHitList = searchHitList;
		initialize(file, itemState, searchHitList);
	}

    /**
     * Sets up some extra information concerning full text search hits
     * @param file
     * @param position
     * @param itemState
     * @param resultitem
     */
    protected void initialize(FileVO file, State itemState, List<SearchHitVO> searchHitList)
    {
        // set some html elements which cannot be completely constructed in the jsp
    	
    	String beforeSearchHitString;
    	String searchHitString;
    	String afterSearchHitString;
    	
    	// browse through the list of files and examine which of the files is the one the search result hits where found in
        for(int i = 0; i < searchHitList.size(); i++)
        {
            if(searchHitList.get(i).getType() == SearchHitType.FULLTEXT)
            {    
                if(searchHitList.get(i).getHitReference() != null)
                {
                    if(searchHitList.get(i).getHitReference().equals(this.file.getReference()))
                    {
                        for(int j = 0; j < searchHitList.get(i).getTextFragmentList().size(); j++)
                        {
                            int startPosition = 0;
                            int endPosition = 0;
                            
                            startPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
                            endPosition = searchHitList.get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;
                            
                            beforeSearchHitString ="..." + searchHitList.get(i).getTextFragmentList().get(j).getData().substring(0, startPosition);
                            searchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition);
                            afterSearchHitString = searchHitList.get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "...";
                            
                            this.searchHits.add(new SearchHitBean(beforeSearchHitString, searchHitString, afterSearchHitString));
                        }
                    }
                    
                }
                
            }
            
        }
    }
    
    
	
    
    /**
     * Prepares the file the user wants to download
     * 
     */
    public String downloadFile()
    {
        
        try
        {
            LoginHelper loginHelper = (LoginHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), "LoginHelper");
    
            
            String fileLocation = ServiceLocator.getFrameworkUrl() + this.file.getContent();
            String filename = this.file.getName(); // Filename suggested in browser Save As dialog
            filename = filename.replace(" ", "_"); // replace empty spaces because they cannot be procesed by the http-response (filename will be cutted after the first empty space)
            String contentType = this.file.getMimeType(); // For dialog, try
            System.out.println("MIME: " + contentType);
            
            // application/x-download
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            if(this.file.getDefaultMetadata() != null)
            {
            	response.setContentLength(this.file.getDefaultMetadata().getSize());
            }
            
            response.setContentType(contentType);
            System.out.println("MIME: " + response.getContentType());
    
            byte[] buffer = null;
            if (this.file.getDefaultMetadata() != null)
            {
                try
                {
                    GetMethod method = new GetMethod(fileLocation);
                    method.setFollowRedirects(false);
                    if (loginHelper.getESciDocUserHandle() != null)
                    {
                        // downloading by account user
                        addHandleToMethod(method, loginHelper.getESciDocUserHandle());
                    }
                    
                    // Execute the method with HttpClient.
                    HttpClient client = new HttpClient();
                    ProxyHelper.setProxy(client, fileLocation); //????
                    client.executeMethod(method);
                    OutputStream out = response.getOutputStream();
                    InputStream input = method.getResponseBodyAsStream();
                    try
                    {
                        if(this.file.getDefaultMetadata() != null)
                        {
                        	buffer = new byte[this.file.getDefaultMetadata().getSize()];
                            int numRead;
                            long numWritten = 0;
                            while ((numRead = input.read(buffer)) != -1) {
                                out.write(buffer, 0, numRead);
                                out.flush();
                                numWritten += numRead;
                            }
                            facesContext.responseComplete();
                        }
                    }
                    catch (IOException e1)
                    {
                        logger.debug("Download IO Error: " + e1.toString());
                    }
                    input.close();
                    out.close();
                }
                catch (FileNotFoundException e)
                {
                    logger.debug("File not found: " + e.toString());
                }
            }
        }
        catch (Exception e)
        {
            logger.debug("File Download Error: " + e.toString());
            System.out.println(e.toString());
        }
        return null;
    }
    
    /**
     * Adds a cookie named "escidocCookie" that holds the eScidoc user handle to the provided http method object.
     * @author Tobias Schraut
     * @param method The http method to add the cookie to.
     */
    private void addHandleToMethod(final HttpMethod method, String eSciDocUserHandle)
    {
        // Staging file resource is protected, access needs authentication and
        // authorization. Therefore, the eSciDoc user handle must be provided.
        // Put the handle in the cookie "escidocCookie"
        method.setRequestHeader("Cookie", "escidocCookie=" + eSciDocUserHandle);
    }

    /**
     * Returns the ItemControllerSessionBean.
     * 
     * @return a reference to the scoped data bean (ItemControllerSessionBean)
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        ItemControllerSessionBean itemControllerSessionBean = (ItemControllerSessionBean)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
        .resolveVariable(FacesContext.getCurrentInstance(), ItemControllerSessionBean.BEAN_NAME);
        return itemControllerSessionBean;
    }
   
    
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
        
    }
    
    public String getContentCategory() 
    {
    	String contentCategory = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
 
		if (this.file.getContentCategory() != null)
        {
        	
        	for(ContentCategory contcat : ContentCategory.values())
        	{
        		if(contcat.getUri().equals(this.file.getContentCategory()))
        		{
        			 contentCategory = internationalized.getLabel(this.i18nHelper.convertEnumToString(contcat));
        			 break;
        		}
        	}
        	/*
            contentCategory = internationalized.getLabel(this.i18nHelper.convertEnumToString(
            		PubFileVOPresentation.ContentCategory.valueOf(CommonUtils.convertToEnumString(this.file.getContentCategory()))));
            		*/
        }
    	
    	return contentCategory;
    }
    
    public String getVisibility()
    {
    	String visibility = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	if(this.file.getVisibility() != null)
    	{
    		visibility = internationalized.getLabel(this.i18nHelper.convertEnumToString(this.file.getVisibility()));
    	}
    	return visibility;
    }
    
    public boolean getItemWithdrawn()
    {
    	if(this.itemState.equals(State.WITHDRAWN))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public boolean getShowSearchHits()
    {
    	if(this.searchHits != null && this.searchHits.size() > 0)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }

    public FileVO getFile() {
		return file;
	}
    
    public String getFileName() {
        String fileName = "";
        if(file.getDefaultMetadata() != null && file.getDefaultMetadata().getTitle() != null)
        {
            fileName = file.getDefaultMetadata().getTitle().getValue();
        }
        return fileName;
    }
    
    public String getFileDescription()
    {
        String fileDescription = "";
        if(file.getDefaultMetadata() != null && file.getDefaultMetadata().getDescription() != null)
        {
            fileDescription = file.getDefaultMetadata().getDescription();
        }
        return fileDescription;
    }
    
    public String getFileLink() {
		return file.getContent();
	}
    
    public String getLocator() {
    	String locator = "";
		if(file.getDefaultMetadata() != null && file.getDefaultMetadata().getTitle() != null)
		{
			locator = file.getDefaultMetadata().getTitle().getValue();
		}
    	return locator;
	}
    
    public String getLocatorLink() {
		return file.getContent();
	}

	public void setFile(FileVO file) {
		this.file = file;
	}

	public String getFileSize() {
		String fileSize = "0";
		if(this.file.getDefaultMetadata() != null)
		{
			fileSize = computeFileSize(this.file.getDefaultMetadata().getSize());
		}
		return fileSize;
	}

	public List<SearchHitBean> getSearchHits() {
		return searchHits;
	}

	public void setSearchHits(List<SearchHitBean> searchHits) {
		this.searchHits = searchHits;
	}
	
	public boolean getLocatorIsLink()
	{
	    return(
	            (getFile().getStorage() == FileVO.Storage.EXTERNAL_URL) && (
	            getFile().getContent().startsWith("http://") || 
	            getFile().getContent().startsWith("https://") ||
	            getFile().getContent().startsWith("ftp://"))
	           );
	}
	
	public boolean getIsVisible()
	{
	    if(file.getVisibility().equals(FileVO.Visibility.PUBLIC))
	        return true;
	    else
	        return false;
	}

	/**
	 * This method generates a link to a refering thumbnail image out of a link to a creativecommons licence
	 * @return teh generated link to the refering thumbnail image
	 */
    public String getUrlToLicenceImage()
    {
    	String  urlToLicenceImage = "";
    	
    	try{
        	if(file.getDefaultMetadata() != null && file.getDefaultMetadata().getLicense() != null)
        	{
    	    	String licenceURL = file.getDefaultMetadata().getLicense().toLowerCase();
    	    	
    	    	if(licenceURL != null && !licenceURL.trim().equals("") && licenceURL.indexOf("creative") > -1 && licenceURL.indexOf("commons") > -1)
    	    	{
    		    	String[] splittedURL = licenceURL.split("\\/");
    		    	//Change for dettecting license url in a string
    		    	int start = 0;
    		    	for (int i = 0; i< splittedURL.length; i++)
    		    	{
    		    	    String part = splittedURL[i];
    		    	    if (part.startsWith("creativecommons"))
    		    	    {
    		    	        start = i;
    		    	    }
    		    	}
    		    	
    	            String address = splittedURL[start];
    	            String licenses = "l";
    	            String type = splittedURL[start+2];
    	            String version = splittedURL[start+3];
    	            String image = "80x15.png";
    		    	
    				urlToLicenceImage = "http://i." + address + "/" + licenses + "/" + type + "/" + version + "/" + image;
    	    	}
        	}
    	}
    	catch(Exception e)
    	{
    	    return "";
    	}
    	return urlToLicenceImage;
    }
	
    /**
     * This Method evaluates if the embargo date input filed has to be displayed or not (yes, if visibility is set to private or restricted)
     * @return boolean flag if embargo date input field should be displayed or not
     */
    public boolean getShowEmbargoDate()
    {
    	boolean showEmbargoDate = false;
    	if(file.getVisibility().equals(FileVO.Visibility.PRIVATE))
    	{
    		showEmbargoDate = true;
    	}
    	return showEmbargoDate;
    }
    
    public void setUpdateVisibility(ValueChangeEvent event)
    {
    	Visibility newVisibility = (Visibility) event.getNewValue();
    	file.setVisibility(newVisibility);
    }
    
    /**
     * Returns the checksum algorithm of the file as string.
     * @return
     */
    public String getChecksumAlgorithmAsString()
    {
        if (file.getChecksumAlgorithm() != null)
        {
            return file.getChecksumAlgorithm().toString();
        }
        return null;
        
    }
    
    /**
     * Sends back an html response of content type text/plain that includes the checksum as UTF-8 string.
     * @return
     */
    public String displayChecksum()
    {

        if (file.getChecksum()!=null && file.getChecksumAlgorithm()!=null)
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
            response.setContentLength(file.getChecksum().length());
            response.setContentType("text/plain");
            try
            {
                String filename = this.file.getName();
                if (filename!=null)
                {
                    filename = filename.replace(" ", "_");
                }
                else
                {
                    filename="";
                }
                
                response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8") + "." + getChecksumAlgorithmAsString().toLowerCase());
                
                OutputStream out = response.getOutputStream();
                out.write(file.getChecksum().getBytes("UTF-8"));
                out.flush();
                
                facesContext.responseComplete();
                out.close();
            }
            catch (Exception e)
            {
                error("Could not display checksum of file!");
               logger.error("Could not display checksum of file", e);
            }
      
           return "";
        }
        else
        {
            error("Could not display checksum of file!");
            logger.error("File checksum is null");
            return "";
        }
            
       
}
    
    /**
     * Current workaround for the detection if the current user belongs to the audience group of this file. Gets the audience grants of the file object via filter method.
     * Then it retrieves the user groups of the grants, takes the org unit id of its selector and checks if the user belongs to this org unit or if it is a child org unit of him.
     * @return
     */
    public boolean getIsAudience()
    {
        return file.getVisibility().equals(FileVO.Visibility.AUDIENCE) && loginHelper.isLoggedIn();
        
        /*
        boolean isAudience = false; 
        try
        {
            if (loginHelper.getAccountUser()!=null && loginHelper.getAccountUser().getHandle()!=null && !loginHelper.getAccountUser().getHandle().equals(""))
            {
                GrantList gl = GrantList.Factory.retrieveGrantsForObject(AdminHelper.getAdminUserHandle(), file.getReference().getObjectId(), Grant.CoreserviceRole.AUDIENCE.getRoleId());
                
               
                for(Grant g: gl.getGrants())
                {
                    logger.info("Grant for file: " + g.getAssignedOn() + " " + g.getGrantedTo() + " " + g.getGrantType() + " ");
                    if (g.getGrantType().equals("user-group"))
                    {
                        UserGroup ug = new UserGroup(g.getGrantedTo(), AdminHelper.getAdminUserHandle());
                        Selector sel = ug.getSelectors().getSelectors().get(0);
                       
                        for (AffiliationRO aff : loginHelper.getAccountUser().getAffiliations())
                        {
                            isAudience = isAudience || checkAffiliationId(aff, sel.getObjid());
                        }
                    }
                }
                
               
                
            }
        }
        
        catch (Exception e)
        {
            logger.error("Error while detecting audience grants of current user for file");
        }
        return isAudience;
        */
    }

    /**Helper method for checking audience rights
     * 
     * @param aff
     * @param objid
     * @return
     * @throws Exception
     */
    private static boolean checkAffiliationId(AffiliationRO aff, String objid) throws Exception
    {
        logger.info("Check affiliation id" + aff.getObjectId() + " with " +objid);
        if (aff.getObjectId().equals(objid))
        {
            return true;
        }
        else
        {
            AffiliationTree affTree = (AffiliationTree)getSessionBean(AffiliationTree.class);
            affTree.getAffiliationSelectItems();
            AffiliationVOPresentation affVO = affTree.getAffiliationMap().get(aff.getObjectId());
       
            if (affVO!=null && affVO.getChildren()!=null)
            {
                
                for (AffiliationVO childAffVO : affVO.getChildren())
                {
                    return checkAffiliationId(childAffVO.getReference(), objid);
                }
            }
                
        }
        return false;
        
    }
    
    /**
     * Generate a string for displaying file sizes.
     * Added by FrM to compute a better result for values < 1024.
     * 
     * @param size The size of an uploaded file.
     * @return A string representing the file size in a readable format.
     */
	public String computeFileSize(long size) {
		if (size < 1024)
		{
			return size + getLabel("ViewItemMedium_lblFileSizeB");
		}
		else if (size < 1024 * 1024)
		{
			return ((size - 1) / 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeKB");
		}
		else
		{
			return ((size - 1) / (1024 * 1024) + 1) + getLabel("ViewItemMedium_lblFileSizeMB");
		}
	}
    
}