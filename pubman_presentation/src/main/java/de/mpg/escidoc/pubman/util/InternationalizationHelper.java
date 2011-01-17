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
package de.mpg.escidoc.pubman.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.comparator.PubItemVOComparator;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Class for Internationalization settings.
 *
 * @author: Tobias Schraut, created 04.07.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 20.08.2007
 */
public class InternationalizationHelper
{

    public static final String BEAN_NAME = "InternationalizationHelper";
    private static Logger logger = Logger.getLogger(InternationalizationHelper.class);
    public static final String LABEL_BUNDLE = "Label";
    public static final String MESSAGES_BUNDLE = "Messages";
    public static final String HELP_PAGE_DE = "help/eSciDoc_help_de.html";
    public static final String HELP_PAGE_EN = "help/eSciDoc_help_en.html";
    private String selectedHelpPage;
    private String locale = "en";
    private String homeContent = "n/a";
    
    public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	private String context = null;
    
    public List<String> test = new ArrayList<String>();
    
    // entry when no item in the comboBox is selected
    private SelectItem NO_ITEM_SET = null;
    
    /**
     * enum for select items.
     */
    public enum SelectMultipleItems
    {
        SELECT_ITEMS, SELECT_ALL, DESELECT_ALL, SELECT_VISIBLE
    }
    
    /**
     * Enum for component visibility in the advanced search
     * @author endres
     *
     */
    public enum SelectComponentAvailability
    {
        SELECT_HAS_FILES, SELECT_HAS_LOCATORS, SELECT_HAS_COMPONENTS, SELECT_HAS_NO_COMPONENTS
    }
    
   /**
    * Enum for component acccessability in the advanced search
    * @author endres
    *
    */
    public enum SelectComponentVisibility
    {
        SELECT_COMPONENT_PRIVATE, SELECT_COMPONENT_PUBLIC, SELECT_COMPONENT_RESTRICTED
    }
    
    Locale userLocale;

    public InternationalizationHelper()
    {
        userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        Iterator<Locale> supportedLocales = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        
        boolean found = false;
        while (supportedLocales.hasNext())
        {
            Locale supportedLocale = supportedLocales.next();
            if (supportedLocale.getLanguage().equals(userLocale.getLanguage()))
            {
                found = true;
                break;
            }
        }
        if (!found)
        {
            userLocale = new Locale("en");
        }
        
        if (userLocale.getLanguage().equals("de"))
        {
            selectedHelpPage = HELP_PAGE_DE;
        }
        else
        {
            selectedHelpPage = HELP_PAGE_EN;
        }
        locale = userLocale.getLanguage();
        NO_ITEM_SET = new SelectItem("", getLabel("EditItem_NO_ITEM_SET"));
    }

    // Getters and Setters
    public String getSelectedLabelBundle()
    {
        return LABEL_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedMessagesBundle()
    {
        return MESSAGES_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedHelpPage()
    {
        return selectedHelpPage;
    }
    
    public void changeLanguage(ValueChangeEvent event)
    {
        FacesContext fc = FacesContext.getCurrentInstance(); 
        if (event.getOldValue() != null && !event.getOldValue().equals(event.getNewValue()))
        {
            Locale locale = null;
            String language = event.getNewValue().toString();
            String country = language.toUpperCase();
            this.locale = language;
            try
            {
                locale = new Locale(language, country);
                fc.getViewRoot().setLocale(locale);
                Locale.setDefault(locale);
                userLocale = locale;
                homeContent="n/a";
                
                logger.debug("New locale: " + language + "_" + country + " : " + locale);
            }
            catch (Exception e)
            {
                logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
            }
            if (language.equals("de"))
            {
                selectedHelpPage = HELP_PAGE_DE;
            }
            else
            {
                selectedHelpPage = HELP_PAGE_EN;
            }
        }
    }

    public void toggleLocale(ActionEvent event)
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        
        //
        // toggle the locale
        Locale locale = null;
        Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
        String language = (String) map.get("language");
        String country = (String) map.get("country");
        this.locale = language;
        try
        {
            locale = new Locale(language, country);
            fc.getViewRoot().setLocale(locale);
            Locale.setDefault(locale);
            userLocale = locale;
            logger.debug("New locale: " + language + "_" + country + " : " + locale);
        }
        catch (Exception e)
        {
            logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
        }
        if (language.equals("de"))
        {
            selectedHelpPage = HELP_PAGE_DE;
        }
        else
        {
            selectedHelpPage = HELP_PAGE_EN;
        }
    }

    public Locale getUserLocale()
    {
        return userLocale;
    }

    public void setUserLocale(final Locale userLocale)
    {
        this.userLocale = userLocale;
    }

    public List<String> getTest()
    {
        if (test.isEmpty())
        {
            test.add("AAA");
            test.add("BBB");
        }
        return test;
    }

    public void setTest(List<String> test)
    {
        this.test = test;
    }
    
    /**
     * Turn the values of an enum to an array of SelectItem.
     *
     * @param includeNoItemSelectedEntry Decide if a SelectItem with null value should be inserted.
     * @param values The values of an enum.
     * @return An array of SelectItem.
     */
    public SelectItem[] getSelectItemsForEnum(final boolean includeNoItemSelectedEntry, final Object[] values)
    {
        Object[] valuesWithoutNull = removeNullValues(values);
        
        SelectItem[] selectItems = new SelectItem[valuesWithoutNull.length];

        for (int i = 0; i < valuesWithoutNull.length; i++)
        {
            if (valuesWithoutNull[i] != null)
            {
                 SelectItem selectItem = new SelectItem(valuesWithoutNull[i].toString(), getLabel(convertEnumToString(valuesWithoutNull[i])));
                 selectItems[i] = selectItem;
            }
           
        }

        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }

        return selectItems;
    }
    
    private static Object[] removeNullValues(Object[] values) {
        
        List<Object> listWithoutNulls = new ArrayList<Object>();
        for(Object o : values)
        {
            if(o!=null)
            {
                listWithoutNulls.add(o);
            }
        }
        
        return listWithoutNulls.toArray();
    }

    /**
     * Adds an entry for NoItemSelected in front of the given array.
     * @param selectItems the array where the entry should be added
     * @return a new array with an entry for NoItemSelected
     */
    private SelectItem[] addNoItemSelectedEntry(final SelectItem[] selectItems)
    {
        SelectItem[] newSelectItems = new SelectItem[selectItems.length + 1];

        // add the entry for NoItemSelected in front of the array
        newSelectItems[0] = this.NO_ITEM_SET;
        for (int i = 0; i < selectItems.length; i++)
        {
            newSelectItems[i + 1] = selectItems[i];
        }

        return newSelectItems;
    }
    /**
     * TODO FrM: Check this
     * Converts an enum to a String for output.
     * @param enumObject the enum to convert
     * @return the converted String for output
     */
    public String convertEnumToString(final Object enumObject)
    {
        if (enumObject != null)
        {
            return "ENUM_" + enumObject.getClass().getSimpleName().toUpperCase() + "_" + enumObject;
        }
        else
        {
            return "ENUM_EMPTY";
        }
    }
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    }
    
    /**
     * Returns an array of SelectItems for the enum genre.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsGenre(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.Genre[] values = MdsPublicationVO.Genre.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum CreatorType.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for CreatorType
     */
    public SelectItem[] getSelectItemsCreatorType(final boolean includeNoItemSelectedEntry)
    {
        CreatorVO.CreatorType[] values = CreatorVO.CreatorType.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum CreatorRole.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for CreatorRole
     */
    public SelectItem[] getSelectItemsCreatorRole(final boolean includeNoItemSelectedEntry)
    {
        CreatorVO.CreatorRole[] values = CreatorVO.CreatorRole.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsGenre()
    {
        return this.getSelectItemsGenre(false);
    }
    
    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsDegreeType()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum DegreeType.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for DegreeType
     */
    public SelectItem[] getSelectItemsDegreeType(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.DegreeType[] values = MdsPublicationVO.DegreeType.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsReviewMethod()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum ReviewMethod.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for ReviewMethod
     */
    public SelectItem[] getSelectItemsReviewMethod(final boolean includeNoItemSelectedEntry)
    {
        MdsPublicationVO.ReviewMethod[] values = MdsPublicationVO.ReviewMethod.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Returns an array of SelectItems for the enum visibility.
     * @return array of SelectItems for visibility
     */
    public SelectItem[] getSelectItemsVisibility()
    {
        return this.getSelectItemsVisibility(false);
    }
    
    /**
     * Returns an array of SelectItems for the enum visibility.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for visibility
     */
    public SelectItem[] getSelectItemsVisibility(final boolean includeNoItemSelectedEntry)
    {
        FileVO.Visibility[] values = FileVO.Visibility.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Returns an array of SelectItems for the enum ContentCategory.
     * @return array of SelectItems for ContentCategory
     */
    public SelectItem[] getSelectItemsContentCategory()
    {
        return this.getSelectItemsContentCategory(false);
    }
    
    /**
     * Returns an array of SelectItems for a list of user groups
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for user groups
     */
    public SelectItem[] getSelectItemsUserGroups(final boolean includeNoItemSelectedEntry)
    {
        FileVO.Visibility[] values = FileVO.Visibility.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }


    /**
     * Returns an array of SelectItems for the enum ContentCategory.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for ReviewMethod
     */
    public SelectItem[] getSelectItemsContentCategory(final boolean includeNoItemSelectedEntry)
    {
        PubFileVOPresentation.ContentCategory[] values = PubFileVOPresentation.ContentCategory.values();

        SelectItem[] selectItems = new SelectItem[values.length];

        for (int i = 0; i < values.length; i++)
        {
            SelectItem selectItem = new SelectItem(values[i].getUri(), getLabel(convertEnumToString(values[i])));
            selectItems[i] = selectItem;
        }

        if (includeNoItemSelectedEntry)
        {
            selectItems = this.addNoItemSelectedEntry(selectItems);
        }
        
        return selectItems;
    }

    /**
     * Returns an array of SelectItems for the enum genre.
     * @return array of SelectItems for genre
     */
    public SelectItem[] getSelectItemsInvitationStatus()
    {
        return this.getSelectItemsGenre(false);
    }

    /**
     * Returns an array of SelectItems for the enum InvitationStatus.
     * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
     * @return array of SelectItems for InvitationStatus
     */
    public SelectItem[] getSelectItemsInvitationStatus(final boolean includeNoItemSelectedEntry)
    {
        EventVO.InvitationStatus[] values = EventVO.InvitationStatus.values();

        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }

    /**
     * Returns an array of SelectItems for the enum ItemState.
     * @return array of SelectItems for ItemState
     */
    public SelectItem[] getSelectItemsItemState()
    {
        PubItemVO.State[] values = PubItemVO.State.values();

        // TODO FrM: add an extra 'all', since it's not member of the enum
        // selectItems[0] = new SelectItem("all", getLabel("depositorWS_ItemState_all"));

        return getSelectItemsForEnum(false, values);
    }

    /**
     * Returns an array of SelectItems for the enum ItemListSortBy.
     * @return array of SelectItems for ItemListSortBy
     */
    public SelectItem[] getSelectItemsItemListSortBy()
    {
        PubItemVOComparator.Criteria[] values = PubItemVOComparator.Criteria.values();

        return getSelectItemsForEnum(false, values);
    }

    /**
     * Returns an array of SelectItems for the enum SelectMultipleItems.
     * @return array of SelectItems for SelectMultipleItems
     */
    public SelectItem[] getSelectItemsItemListSelectMultipleItems()
    {
        InternationalizationHelper.SelectMultipleItems[] values = InternationalizationHelper.SelectMultipleItems.values();

        return getSelectItemsForEnum(false, values);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    /**
     * Returns an array of SelectItems for the enum {@link SelectComponentAvailability}
     * @return array of SelectItems for SelectComponentAvailability
     */
    public SelectItem[] getSelectedItemsComponentAvailability(final boolean includeNoItemSelectedEntry)
    {
       InternationalizationHelper.SelectComponentAvailability[] values = InternationalizationHelper.SelectComponentAvailability.values();
       return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Returns an array of SelectItems for the enum {@link SelectComponentAccessability}
     * @return array of SelectItems for SelectComponentAccessability
     */
    public SelectItem[] getSelectedItemsComponentVisibility(final boolean includeNoItemSelectedEntry)
    {
        InternationalizationHelper.SelectComponentVisibility[] values = InternationalizationHelper.SelectComponentVisibility.values();
        return getSelectItemsForEnum(includeNoItemSelectedEntry, values);
    }
    
    /**
     * Part of Pubman's homepage van be drawn from an external html source.
     * The property escidoc.pubman.home.content.url has to be set with the url. If different languages should be supported,
     * The url source has to be provided with the different lcoale endings, e.g. ".de", ".en", ".ja".
     * @return
     */
    public String getHomeContent()
    {
    	if("n/a".equals(homeContent))
    	{
    		try {
    			String contentUrl = PropertyReader.getProperty("escidoc.pubman.home.content.url");
    			
    			if (contentUrl!=null && !contentUrl.equals(""))
    			{
    				//Try if there's a specific local version
    				homeContent = getContent(new URL(contentUrl + "." + getLocale()));
    				
    				//If not try the url without locale
    				if(homeContent==null)
    				{
    					homeContent = getContent(new URL(contentUrl));
    				}
    				
    				
    			}
    			else
    			{
    				homeContent=null;
    			}
    		
    		} catch (Exception e) {
    			logger.error("Could not retrieve content for home page", e);
    			homeContent=null;
    		}
    	}
    	
    	return homeContent;
    }
    
    private String getContent(URL url) throws Exception
    {
    	
    	HttpClient httpClient = new HttpClient();
    	GetMethod getMethod = new GetMethod(url.toExternalForm());
    	
    	httpClient.executeMethod(getMethod);
    	
    	if(getMethod.getStatusCode()==200)
    	{

            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                               getMethod.getResponseBodyAsStream()));

            String inputLine = "";
            String content = "";

            while (inputLine != null)
            {
                inputLine = in.readLine();
                if (inputLine != null)
                {
                    content += inputLine + "  ";
                }
            }
            
            in.close();
            
            return content;
    	}
    	else
    	{
    		return null;
    	}
    	
    	
    }
    
}
