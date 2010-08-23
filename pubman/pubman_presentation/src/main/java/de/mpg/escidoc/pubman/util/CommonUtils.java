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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.contextList.PubContextVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Provides different utilities for all kinds of stuff.
 * 
 * @author: Thomas Diebäcker, created 25.04.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 07.08.2007
 */
public class CommonUtils extends InternationalizedImpl
{
    private static Logger logger = Logger.getLogger(CommonUtils.class);
    private static final String NO_ITEM_SET = "-";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm";

    //HTML escaped characters mapping
    private static final String[] PROBLEMATIC_CHARACTERS =
    {
        "&", ">", "<", "\"", "\'", "\r\n", "\n", "\r", "\t"
    };
    private static final String[] ESCAPED_CHARACTERS =
    {
        "&amp;", "&gt;", "&lt;", "&quot;", "&apos;", "<br/>", "<br/>", "<br/>" , "&#160;&#160;"
    };
    


    /**
     * Converts a Set to an Array of SelectItems (an empty SelectItem is included at the beginning).
     * This method is used to convert Enums into SelectItems for dropDownLists.
     *
     * @param set the Set to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Set set)
    {
        return convertToOptions(set, true);
    }

    /**
     * Converts a Set to an Array of SelectItems. This method is used to convert Enums into SelectItems for dropDownLists.
     * @param set the Set to be converted
     * @param includeEmptyOption if TRUE an empty SelectItem is added at the beginning of the list
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Set set, boolean includeEmptyOption)
    {
        List<SelectItem> options = new ArrayList<SelectItem>();

        if (includeEmptyOption)
        {
            options.add(new SelectItem("", NO_ITEM_SET));
        }

        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            options.add(new SelectItem(iter.next()));
        }

        return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
    }

    /**
     * Converts an Array of Objects to an Array of SelectItems (an empty SelectItem is included at the beginning). This method
     * is used to convert Objects into SelectItems for dropDownLists.
     * @param objects the Array of Objects to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Object[] objects)
    {
        return convertToOptions(objects, true);
    }

    /**
     * Converts an Array of Objects to an Array of SelectItems. This method is used to convert Objects into SelectItems for
     * dropDownLists.
     * @param objects the Array of Objects to be converted
     * @return an Array of SelectItems
     */
    public static SelectItem[] convertToOptions(Object[] objects, boolean includeEmptyOption)
    {
        List<SelectItem> options = new ArrayList<SelectItem>();

        if (includeEmptyOption)
        {
            options.add(new SelectItem("", NO_ITEM_SET));
        }

        for (int i = 0; i < objects.length; i++)
        {
            options.add(new SelectItem(objects[i]));
        }

        return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
    }
    
    public static SelectItem[] getLanguageOptions()
    {
        ApplicationBean appBean = (ApplicationBean)getApplicationBean(ApplicationBean.class);
        String locale = Locale.getDefault().getLanguage();
        
        //if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja") || locale.equals("es")))
        if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja")))
        {
             locale = "en";
        }
         
        if(appBean.getLanguageSelectItems().get(locale)!=null && appBean.getLanguageSelectItems().get(locale).length>0)
        {
            return appBean.getLanguageSelectItems().get(locale);
        }
        else
        {
            SelectItem[] languageSelectItems = retrieveLanguageOptions(locale);
            appBean.getLanguageSelectItems().put(locale, languageSelectItems);
            return languageSelectItems;
        }
        
    }
    /**
     * Returns all Languages from Cone Service, with "de","en","es" and "ja" at the first positions.
     * @return all Languages from Cone Service, with "de","en","es" and "ja" at the first positions
     */
    public static SelectItem[] retrieveLanguageOptions(String locale)
    {
       Map<String, String> coneLanguagesIso639_1 = null;
       Map<String, String> coneLanguagesIso639_3 = null;
        try {
            coneLanguagesIso639_1 = CommonUtils.getConeLanguages("iso639-1", locale);
            coneLanguagesIso639_3 = CommonUtils.getConeLanguages("iso639-3", locale);
            
        }
        catch(Exception e) {
            return new SelectItem[0];
        }
            
//        SelectItem[] options = new SelectItem[coneLanguagesIso639_1.size() + 5];
        SelectItem[] options = new SelectItem[coneLanguagesIso639_1.size() + 4];
        options[0] = new SelectItem("", NO_ITEM_SET);
        if (locale.equals("de"))
        {
            options[1] = new SelectItem("eng", "eng - Englisch");  
            options[2] = new SelectItem("deu", "deu - Deutsch");  
            options[3] = new SelectItem("jpn", "jpn - Japanisch"); 
//            options[4] = new SelectItem("spa", "spa - Spanisch"); 
        }
        else if (locale.equals("en"))
        {
            options[1] = new SelectItem("eng", "eng - English");  
            options[2] = new SelectItem("deu", "deu - German");  
            options[3] = new SelectItem("jpn", "jpn - Japanese"); 
//            options[4] = new SelectItem("spa", "spa - Spanish"); 
        }
        else if (locale.equals("fr"))
        {
            options[1] = new SelectItem("eng", "eng - Anglais");  
            options[2] = new SelectItem("deu", "deu - Allemand");  
            options[3] = new SelectItem("jpn", "jpn - Japonais"); 
//            options[4] = new SelectItem("spa", "spa - Espagnol"); 
        }
        else if (locale.equals("ja"))
        {
            options[1] = new SelectItem("eng", "eng - 英語");
            options[2] = new SelectItem("deu", "deu - ドイツ語");
            options[3] = new SelectItem("jpn", "jpn - 日本語");
//            options[4] = new SelectItem("spa", "spa - スペイン語"); 
        }
//        else if (locale.equals("es"))
//        {
//            options[1] = new SelectItem("eng", "eng - Inglés");
//            options[2] = new SelectItem("deu", "deu - Alemán");
//            options[3] = new SelectItem("jpn", "jpn - Japonés");
//            options[4] = new SelectItem("spa", "spa - Español"); 
//        }
        else
        {
            logger.error("Language not supported: " + locale);
            // Using english as default
            options[1] = new SelectItem("eng", "eng - English");  
            options[2] = new SelectItem("deu", "deu - German");  
            options[3] = new SelectItem("jpn", "jpn - Japanese");
//            options[4] = new SelectItem("spa", "spa - Spanish"); 
        }
//        options[5] = new SelectItem("", NO_ITEM_SET);
        if (coneLanguagesIso639_1.size() > 0)
        {
            options[4] = new SelectItem("", NO_ITEM_SET);
        }

        int i = 0;
        List<String> langLabels = new ArrayList<String>(coneLanguagesIso639_1.keySet());
        Collections.sort(langLabels);
        
        for (String label : langLabels)
        {
            String iso639Code = coneLanguagesIso639_3.get(label);
            options[i + 4] = new SelectItem(iso639Code, iso639Code + " - " + label);
//            options[i + 5] = new SelectItem(iso639Code, iso639Code + " - " + label);
            i++;
        }

        return options;
    }
    
    public static Map<String, String> getConeLanguages(String type, String locale)
    {
        Map<String, String> langMap = new HashMap<String, String>();
        
        
        //if (!(locale.equals("en") || locale.equals("de") || locale.equals("fr") || locale.equals("ja") || locale.equals("es")))
        if (!(locale.equals("en") || locale.equals("de") || locale.equals("fr") || locale.equals("ja")))
        {
            locale = "en";
        }
        
        try
        {
            URL coneUrl = new URL (PropertyReader.getProperty("escidoc.cone.service.url")+ type + "/all?format=options&lang="+locale);
            URLConnection conn = coneUrl.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            int responseCode = httpConn.getResponseCode();
            
            switch (responseCode)
            {
                case 200:
                    logger.debug("Cone Service responded with 200.");
                    break;
                default:
                    throw new RuntimeException("An error occurred while calling Cone Service: "
                            + responseCode + ": " + httpConn.getResponseMessage());
            }
            
            InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
            BufferedReader bReader = new BufferedReader(isReader);
            String line = "";
            while ((line = bReader.readLine()) != null)
            {
                String[] parts = line.split("\\|");
                if (parts.length == 2)
                {
                    langMap.put(parts[1], parts[0]);
                }
            }
            isReader.close();
            httpConn.disconnect();
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred while calling the Cone service.",e);
        }
        return langMap;
    }

    public static String getConeLanguageName(String code, String locale) throws Exception
    {
        if (code != null && !"".equals(code.trim()))
        {
            //if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja") || locale.equals("es")))
            if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja")))
            {
                locale = "en";
            }
            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.service.url") + "iso639-3/resource/" + code + "?format=json&lang=" + locale);
            client.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();
            Pattern pattern = Pattern.compile("\"http_purl_org_dc_elements_1_1_title\" : \\[?\\s*\"(.+)\"");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find())
            {
                return matcher.group(1);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static String getConeLanguageCode(String name) throws Exception
    {
        if (name != null && !"".equals(name.trim()))
        {

            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.service.url")
                    + "iso639-3/query?q=\"" + URLEncoder.encode(name, "ISO-8859-1") + "\"&format=options");
            client.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();

            String[] parts = response.split("\\|");
            if (parts.length == 1 || parts.length > 2)
            {
                return null;
            }
            else
            {
                return parts[0];
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Returns the current value of a comboBox. Used in UIs.
     * @param comboBox the comboBox for which the value should be returned
     * @return the current value of the comboBox
     */
    public static String getUIValue(HtmlSelectOneMenu comboBox)
    {
        if (comboBox.getSubmittedValue() != null
                && comboBox.getSubmittedValue() instanceof String[]
                && ((String[]) comboBox.getSubmittedValue()).length > 0)
        {
            return ((String[]) comboBox.getSubmittedValue())[0];
        }

        return (String) comboBox.getValue();
    }
    
    /**
     * Returns the current value of a comboBox. Used in UIs.
     * @param comboBox the comboBox for which the value should be returned
     * @return the current value of the comboBox
     */
    public static String getUIValue(HtmlSelectOneRadio radioButton)
    {
        if (radioButton.getSubmittedValue() != null
                && radioButton.getSubmittedValue() instanceof String[]
                && ((String[]) radioButton.getSubmittedValue()).length > 0)
        {
            return ((String[]) radioButton.getSubmittedValue())[0];
        }

        return (String) radioButton.getValue();
    }

    /**
     * Returns the current value of a textfield. Used in UIs.
     * @param textField the textField for which the value should be returned
     * @return the current value of the textfield
     */
    public static String getUIValue(HtmlInputText textField)
    {
        if (textField.getSubmittedValue() != null
                && textField.getSubmittedValue() instanceof String
                && ((String) textField.getSubmittedValue()).length() > 0)
        {
            return ((String) textField.getSubmittedValue());
        }

        return (String) textField.getValue();
    }

    /**
     * Returns the current value of a textArea. Used in UIs.
     * @param textArea the textArea for which the value should be returned
     * @return the current value of the textArea
     */
    public static String getUIValue(HtmlInputTextarea textArea)
    {
        if (textArea.getSubmittedValue() != null
                && textArea.getSubmittedValue() instanceof String
                && ((String) textArea.getSubmittedValue()).length() > 0)
        {
            return ((String) textArea.getSubmittedValue());
        }

        return (String) textArea.getValue();
    }

    /**
     * Creates a unique id for GUI components.
     * @param uiComponent the uiComponent for which an id should be created
     * @return a unique id
     */
    public static String createUniqueId(UIComponent uiComponent)
    {
        UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();

        if (viewRoot == null)
        {
            viewRoot = new UIViewRoot();
            FacesContext.getCurrentInstance().setViewRoot(viewRoot);
        }

        String id = viewRoot.createUniqueId() + "_" + uiComponent.getClass().getSimpleName() + "_" + uiComponent.hashCode() + "_" + Calendar.getInstance().getTimeInMillis();

        return id;
    }

    /**
     * Formats a date with the default format.
     * @param date the date to be formated
     * @return a formated String
     */
    public static String format(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonUtils.DATE_FORMAT);
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }

    /**
     * Formats a date with the default format.
     * @param date the date to be formated
     * @return a formated String
     */
    public static String formatTimestamp(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonUtils.TIMESTAMP_FORMAT);
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }

    /**
     * Escapes problematic HTML characters ("less than", "greater than", ampersand, apostrophe and quotation mark).
     *
     * @param cdata A String that might contain problematic HTML characters.
     * @return The escaped string.
     */
    public static String htmlEscape(String cdata)
    {
        if (cdata == null)
        {
            return null;
        }
        // The escaping has to start with the ampersand (&amp;, '&') !
        for (int i = 0; i < PROBLEMATIC_CHARACTERS.length; i++)
        {
            cdata = cdata.replace(PROBLEMATIC_CHARACTERS[i], ESCAPED_CHARACTERS[i]);

        }
        return cdata;
    }

    /**
     * Escapes problematic Javascript characters ("'", "\n").
     *
     * @param cdata A String that might contain problematic Javascript characters.
     * @return The escaped string.
     */
    public static String javascriptEscape(String cdata)
    {
        if (cdata == null)
        {
            return null;
        }
        else
        {
            return cdata.replace("'", "\\'").replace("\n", "\\n").trim();
        }
    }

    /**
     * Changes all occurrences of oldPat to newPat.
     *
     * @param in A String that might contain problematic HTML characters.
     * @param oldPat the old pattern to be escaped.
     * @param newPat the new pattern to escape with.
     * 
     * @return The escaped string.
     * 
     * @deprecated I do not see any advantage over String.replace
     */
    @Deprecated
    private static String change(String in, String oldPat, String newPat)
    {
        if (in == null)
        {
            return null;
        }
        if (oldPat.length() == 0)
        {
            return in;
        }
        if (oldPat.length() == 1 && newPat.length() == 1)
        {
            return in.replace(oldPat.charAt(0), newPat.charAt(0));
        }
        if (in.indexOf(oldPat) < 0)
        {
            return in;
        }
        int lastIndex = 0;
        int newIndex = 0;
        StringBuffer newString = new StringBuffer();
        for (;;)
        {
            newIndex = in.indexOf(oldPat, lastIndex);
            if (newIndex != -1)
            {
                newString.append(in.substring(lastIndex, newIndex) + newPat);
                lastIndex = newIndex + oldPat.length();

            }
            else
            {
                newString.append(in.substring(lastIndex));
                break;
            }
        }
        return newString.toString();
    }

    /**
     * Converts an array of SelectItems to a SelectItemUI. This is used for items for comboboxes.
     * @param selectItems the array of SelectItems that should be converted
     * @return a UISelectItems which can be added to a HtmlSelectOneMenu with HtmlSelectOneMenu.getChildren.add()
     */
    public static List<UISelectItem> convertToSelectItemsUI(final SelectItem[] selectItems)
    {
        List<UISelectItem> uiSelectItems = new ArrayList<UISelectItem>();

        for (int i = 0; i < selectItems.length; i++)
        {
            UISelectItem uiSelectItem = new UISelectItem();
            uiSelectItem.setItemValue(selectItems[i].getValue());
            uiSelectItem.setItemLabel(selectItems[i].getLabel());
            uiSelectItems.add(uiSelectItem);
        }

//        UISelectItems items = new UISelectItems();
//        items.setValue(uiSelectItems);

        return uiSelectItems;
    }

    public static String convertToEnumString(String value)
    {
        return value.toUpperCase().replace("-", "_");
    }
    
    /**
     * generates an HTML OutputText element. The method also tests if the string that
     * should be placed into the element is empty.
     * If it is, a "&nbsp;" string is placed into.
     * @author Tobias Schraut
     * @param elementText the text that should placed into the html text element
     * @return HtmlOutputText the generated and prepared html text element
     */
    public static HtmlOutputText getTextElementConsideringEmpty(String elementText)
    {
        HtmlOutputText text = new HtmlOutputText();
        text.setId(CommonUtils.createUniqueId(text));
        if (elementText != null)
        {
            if (!elementText.trim().equals(""))
            {
                text.setEscape(false);
                elementText = elementText.replace("<", "&lt;");
                elementText = elementText.replace(">", "&gt;");
                elementText = elementText.replace("\n", "<br/>");
                text.setValue(elementText);
            }
            else
            {
                text.setEscape(false);
                text.setValue("&nbsp;");
            }
        }
        else
        {
            text.setEscape(false);
            text.setValue("&nbsp;");
        }
        return text;
    }

    /**
     * Converts a list of valueObjects to a list of ValueObjectWrappers.
     * @param valueObjectList the list of valueObjects
     * @return the list of ValueObjectWrappers
     */
    public static List<PubItemVOWrapper> convertToWrapperList(final List<PubItemVO> valueObjectList)
    {
        List <PubItemVOWrapper> wrapperList = new ArrayList<PubItemVOWrapper>();

        for (int i = 0; i < valueObjectList.size(); i++)
        {
            wrapperList.add(new PubItemVOWrapper(valueObjectList.get(i)));
        }

        return wrapperList;
    }

    /**
     * Converts a list of PubItemVOWrappers to a list of PubItemVOs.
     * @param wrapperList the list of PubItemVOWrappers
     * @return the list of PubItemVOs
     */
    public static List<PubItemVO> convertToPubItemList(List<PubItemVOWrapper> wrapperList)
    {
        List <PubItemVO> pubItemList = new ArrayList<PubItemVO>();

        for (int i = 0; i < wrapperList.size(); i++)
        {
            pubItemList.add(wrapperList.get(i).getValueObject());
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubItemVOPresentations to a list of PubItems.
     * @param list the list of PubItemVOPresentations
     * @return the list of PubItemVOs
     */
    public static ArrayList<PubItemVO> convertToPubItemVOList(List<PubItemVOPresentation> list)
    {
        ArrayList<PubItemVO> pubItemList = new ArrayList<PubItemVO>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new PubItemVO(list.get(i)));
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubCollectionVOPresentations to a list of PubCollections.
     * @param list the list of PubCollectionVOPresentations
     * @return the list of ContextVOs
     */
    public static ArrayList<ContextVO> convertToContextVOList(List<PubContextVOPresentation> list)
    {
        ArrayList<ContextVO> contextList = new ArrayList<ContextVO>();

        for (int i = 0; i < list.size(); i++)
        {
            contextList.add(new ContextVO(list.get(i)));
        }

        return contextList;
    }

    /**
     * Converts a list of PubItems to a list of PubItemVOPresentations.
     * @param list the list of PubItemVOs
     * @return the list of PubItemVOPresentations
     */
    public static List<PubItemVOPresentation> convertToPubItemVOPresentationList(List<? extends PubItemVO> list)
    {
        List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new PubItemVOPresentation(list.get(i)));
        }

        return pubItemList;
    }
    
    /**
     * Converts a list of PubItems to a list of PubItemVOPresentations.
     * @param list the list of PubItemVOs
     * @return the list of PubItemVOPresentations
     */
    public static List<PubFileVOPresentation> convertToPubFileVOPresentationList(List<? extends FileVO> list)
    {
        List<PubFileVOPresentation> pubFileList = new ArrayList<PubFileVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            pubFileList.add(new PubFileVOPresentation(i, list.get(i)));
        }

        return pubFileList;
    }

    /**
     * Converts a list of Relations to a list of RelationVOPresentation.
     * @param list the list of RelationVO
     * @return the list of RelationVOPresentation
     */
    public static List<RelationVOPresentation> convertToRelationVOPresentationList(List<RelationVO> list)
    {
        List<RelationVOPresentation> relationList = new ArrayList<RelationVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            relationList.add(new RelationVOPresentation(list.get(i)));
        }

        return relationList;
    }

    /**
     * Converts a list of RelationVOPresentation to a list of Relations.
     * @param list the list of RelationVOPresentation
     * @return the list of RelationVO
     */
    public static ArrayList<RelationVO> convertToRelationVOList(List<RelationVOPresentation> list)
    {
        ArrayList<RelationVO> pubItemList = new ArrayList<RelationVO>();

        for (int i = 0; i < list.size(); i++)
        {
            pubItemList.add(new RelationVO(list.get(i)));
        }

        return pubItemList;
    }

    /**
     * Converts a list of PubCollections to a list of PubCollectionVOPresentations.
     * @param list the list of ContextVOs
     * @return the list of PubCollectionVOPresentations
     */
    public static List<PubContextVOPresentation> convertToPubCollectionVOPresentationList(List<ContextVO> list)
    {
        List<PubContextVOPresentation> contextList = new ArrayList<PubContextVOPresentation>();

        for (int i = 0; i < list.size(); i++)
        {
            contextList.add(new PubContextVOPresentation(list.get(i)));
        }

        return contextList;
    }

    /**
     * Converts a list of AffiliationVOs to a list of AffiliationVOPresentations.
     * @param list the list of AffiliationVOs
     * @return the list of AffiliationVOPresentations
     */
    public static List<AffiliationVOPresentation> convertToAffiliationVOPresentationList(List<AffiliationVO> list)
    {
        List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();
        
        for (int i = 0; i < list.size(); i++)
        {
            affiliationList.add(new AffiliationVOPresentation(list.get(i)));
        }
        AffiliationVOPresentation[] affiliationArray = affiliationList.toArray(new AffiliationVOPresentation[]{});
        Arrays.sort(affiliationArray);
        
        return Arrays.asList(affiliationArray);
    }

    /**
     * Converts a list of valueObjects to a list of ValueObjectWrappers.
     * @param valueObjectList the list of valueObjects
     * @return the list of ValueObjectWrappers
     */
    public static List<PubContextVOWrapper> convertToPubCollectionVOWrapperList(List<ContextVO> valueObjectList)
    {
        List <PubContextVOWrapper> wrapperList = new ArrayList<PubContextVOWrapper>();

        for (int i = 0; i < valueObjectList.size(); i++)
        {
            wrapperList.add(new PubContextVOWrapper(valueObjectList.get(i)));
        }

        return wrapperList;
    }

    /**
     * Searches the given list for the item with the given ID.
     * @param itemList the list to be searched
     * @param itemID the itemID that is searched for
     * @return the pubItem with the given ID or null if the item cannot be found in the given list
     */
    public static PubItemVOPresentation getItemByID(final List<PubItemVOPresentation> itemList, final String itemID)
    {
        for (int i = 0; i < itemList.size(); i++)
        {
            if (itemList.get(i).getVersion().getObjectId().equals(itemID))
            {
                return itemList.get(i);
            }
        }

        logger.warn("Item with ID: " + itemID + " cannot be found in the list.");
        return null;
    }

    /**
     * Limits a string to the given length (on word basis).
     * @param string the string to be limited
     * @param length the maximum length of the string
     * @return the limited String
     */
    public static String limitString(final String string, final int length)
    {
        String limitedString = new String();
        String[] splittedString = string.split(" ");

        if (splittedString != null && splittedString.length > 0)
        {
            limitedString = splittedString[0];

            for (int i = 1; i < splittedString.length; i++)
            {
                String newLimitedString = limitedString + " " + splittedString[i];
                if (newLimitedString.length() <= length)
                {
                    limitedString = newLimitedString;
                }
                else
                {
                    return limitedString.concat("...");
                }
            }
        }

        return limitedString;
    }

    public static String currentDate() {
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      return sdf.format(cal.getTime());

    }
    

    public static boolean getisUriValidUrl(IdentifierVO id)
    {
        boolean valid = false;
        try
        {
            if (id.getType()== null){return false;}
            if (id.getType().equals(IdType.URI) || id.getType().equals(IdType.CONE))
            {
                new URL (id.getId());
                valid = true;
            }
        }
        catch (MalformedURLException e)
        {
            logger.warn("URI: " + id.getId() + "is no valid URL");
            return false;
        }
        return valid;
    }
    
    public static Map<String, String> getDecodedUrlParameterMap(String query) throws UnsupportedEncodingException
    {
        String[] parameters = query.split("&");
        Map<String, String> parameterMap = new HashMap<String, String>();
        for( String param : parameters )
        {
            String[] keyValueParts = param.split("=");
            if (keyValueParts.length==1)
            {
                keyValueParts = new String[]{keyValueParts[0],""};
            }
            parameterMap.put(keyValueParts[0], URLDecoder.decode(keyValueParts[1], "UTF-8"));
            
        }
        return parameterMap;
    }
    
}
