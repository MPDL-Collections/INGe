/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubContextVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.RelationVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Provides different utilities for all kinds of stuff.
 * 
 * @author: Thomas Diebäcker, created 25.04.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 07.08.2007
 */
public class CommonUtils {
  private static final Logger logger = Logger.getLogger(CommonUtils.class);

  private static final String NO_ITEM_SET = "-";
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm";

  // HTML escaped characters mapping
  private static final String[] PROBLEMATIC_CHARACTERS =
      {"&", ">", "<", "\"", "\'", "\r\n", "\n", "\r", "\t"};
  private static final String[] ESCAPED_CHARACTERS =
      {"&amp;", "&gt;", "&lt;", "&quot;", "&apos;", "<br/>", "<br/>", "<br/>", "&#160;&#160;"};

  /**
   * Converts a Set to an Array of SelectItems (an empty SelectItem is included at the beginning).
   * This method is used to convert Enums into SelectItems for dropDownLists.
   * 
   * @param set the Set to be converted
   * @return an Array of SelectItems
   */
  public static SelectItem[] convertToOptions(Set<?> set) {
    return convertToOptions(set, true);
  }

  /**
   * Converts a Set to an Array of SelectItems. This method is used to convert Enums into
   * SelectItems for dropDownLists.
   * 
   * @param set the Set to be converted
   * @param includeEmptyOption if TRUE an empty SelectItem is added at the beginning of the list
   * @return an Array of SelectItems
   */
  public static SelectItem[] convertToOptions(Set<?> set, boolean includeEmptyOption) {
    List<SelectItem> options = new ArrayList<SelectItem>();

    if (includeEmptyOption) {
      options.add(new SelectItem("", NO_ITEM_SET));
    }

    Iterator<?> iter = set.iterator();
    while (iter.hasNext()) {
      options.add(new SelectItem(iter.next()));
    }

    return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
  }

  /**
   * Converts an Array of Objects to an Array of SelectItems (an empty SelectItem is included at the
   * beginning). This method is used to convert Objects into SelectItems for dropDownLists.
   * 
   * @param objects the Array of Objects to be converted
   * @return an Array of SelectItems
   */
  public static SelectItem[] convertToOptions(Object[] objects) {
    return convertToOptions(objects, true);
  }

  /**
   * Converts an Array of Objects to an Array of SelectItems. This method is used to convert Objects
   * into SelectItems for dropDownLists.
   * 
   * @param objects the Array of Objects to be converted
   * @return an Array of SelectItems
   */
  public static SelectItem[] convertToOptions(Object[] objects, boolean includeEmptyOption) {
    List<SelectItem> options = new ArrayList<SelectItem>();

    if (includeEmptyOption) {
      options.add(new SelectItem("", NO_ITEM_SET));
    }

    for (int i = 0; i < objects.length; i++) {
      options.add(new SelectItem(objects[i]));
    }

    return (SelectItem[]) options.toArray(new SelectItem[options.size()]);
  }

  public static SelectItem[] getLanguageOptions() {
    ApplicationBean applicationBean = ((ApplicationBean) FacesTools.findBean("ApplicationBean"));

    String locale = Locale.getDefault().getLanguage();

    if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja"))) {
      locale = "en";
    }

    if (applicationBean.getLanguageSelectItems().get(locale) != null
        && applicationBean.getLanguageSelectItems().get(locale).length > 0) {
      return applicationBean.getLanguageSelectItems().get(locale);
    } else {
      SelectItem[] languageSelectItems = retrieveLanguageOptions(locale);
      applicationBean.getLanguageSelectItems().put(locale, languageSelectItems);
      return languageSelectItems;
    }
  }

  /**
   * Returns all Languages from Cone Service, with "de","en" and "ja" at the first positions.
   * 
   * @return all Languages from Cone Service, with "de","en" and "ja" at the first positions
   */
  private static SelectItem[] retrieveLanguageOptions(String locale) {
    Map<String, String> result = new LinkedHashMap<String, String>();

    try {
      HttpClient httpClient = new HttpClient();
      GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.service.url")
          + "iso639-2/query?format=options&n=0&dc:relation=*&lang=" + locale);
      httpClient.executeMethod(getMethod);

      if (getMethod.getStatusCode() == 200) {
        String line;
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), "UTF-8"));
        while ((line = reader.readLine()) != null) {
          String[] pieces = line.split("\\|");
          result.put(pieces[0], pieces[1]);
        }
      } else {
        logger.error(
            "Error while retrieving languages from CoNE. Status code " + getMethod.getStatusCode());
      }
    } catch (Exception e) {
      return new SelectItem[0];
    }

    SelectItem[] options = new SelectItem[result.size() + 5];
    options[0] = new SelectItem("", NO_ITEM_SET);

    if (locale.equals("de")) {
      options[1] = new SelectItem("eng", "eng - Englisch");
      options[2] = new SelectItem("deu", "deu - Deutsch");
      options[3] = new SelectItem("jpn", "jpn - Japanisch");
    } else if (locale.equals("en")) {
      options[1] = new SelectItem("eng", "eng - English");
      options[2] = new SelectItem("deu", "deu - German");
      options[3] = new SelectItem("jpn", "jpn - Japanese");
    } else if (locale.equals("fr")) {
      options[1] = new SelectItem("eng", "eng - Anglais");
      options[2] = new SelectItem("deu", "deu - Allemand");
      options[3] = new SelectItem("jpn", "jpn - Japonais");
    } else if (locale.equals("ja")) {
      options[1] = new SelectItem("eng", "eng - 英語");
      options[2] = new SelectItem("deu", "deu - ドイツ語");
      options[3] = new SelectItem("jpn", "jpn - 日本語");
    } else {
      logger.error("Language not supported: " + locale);
      // Using english as default
      options[1] = new SelectItem("eng", "eng - English");
      options[2] = new SelectItem("deu", "deu - German");
      options[3] = new SelectItem("jpn", "jpn - Japanese");
    }

    if (result.size() > 0) {
      options[4] = new SelectItem("", NO_ITEM_SET);
    }

    int i = 0;
    for (String key : result.keySet()) {
      String value = result.get(key);
      if (!key.equals(value.split(" - ")[0])) {
        key = value.split(" - ")[0].split(" / ")[1];
      }
      options[i + 5] = new SelectItem(key, value);
      i++;
    }

    return options;
  }

  public static String getConeLanguageName(String code, String locale) throws Exception {
    if (code != null && !"".equals(code.trim())) {
      if (!(locale.equals("en") || locale.equals("de") || locale.equals("ja"))) {
        locale = "en";
      }

      // check if there was a problem splitting the cone-autosuggest in javascript
      if (code.contains(" ")) {
        code = code.trim().split(" ")[0];
      }

      HttpClient client = new HttpClient();
      GetMethod getMethod = new GetMethod(
          PropertyReader.getProperty("escidoc.cone.service.url") + "iso639-3/resource/"
              + URLEncoder.encode(code, "UTF-8") + "?format=json&lang=" + locale);
      client.executeMethod(getMethod);
      String response = getMethod.getResponseBodyAsString();

      Pattern pattern =
          Pattern.compile("\"http_purl_org_dc_elements_1_1_title\" : \\[?\\s*\"(.+)\"");
      Matcher matcher = pattern.matcher(response);

      if (matcher.find()) {
        return matcher.group(1);
      }

      if ("en".equals(locale)) {
        return null;
      }

      return getConeLanguageName(code, "en");
    }

    return null;
  }

  /**
   * Returns the current value of a comboBox. Used in UIs.
   * 
   * @param comboBox the comboBox for which the value should be returned
   * @return the current value of the comboBox
   */
  public static String getUIValue(HtmlSelectOneRadio radioButton) {
    if (radioButton.getSubmittedValue() != null
        && radioButton.getSubmittedValue() instanceof String[]
        && ((String[]) radioButton.getSubmittedValue()).length > 0) {
      return ((String[]) radioButton.getSubmittedValue())[0];
    }

    return (String) radioButton.getValue();
  }

  /**
   * Formats a date with the default format.
   * 
   * @param date the date to be formated
   * @return a formated String
   */
  public static String format(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonUtils.DATE_FORMAT);

    return simpleDateFormat.format(date);
  }

  /**
   * Formats a date with the default format.
   * 
   * @param date the date to be formated
   * @return a formated String
   */
  public static String formatTimestamp(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonUtils.TIMESTAMP_FORMAT);

    return simpleDateFormat.format(date);
  }

  /**
   * Escapes problematic HTML characters ("less than", "greater than", ampersand, apostrophe and
   * quotation mark).
   * 
   * @param cdata A String that might contain problematic HTML characters.
   * @return The escaped string.
   */
  public static String htmlEscape(String cdata) {
    if (cdata == null) {
      return null;
    }

    // The escaping has to start with the ampersand (&amp;, '&') !
    for (int i = 0; i < PROBLEMATIC_CHARACTERS.length; i++) {
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
  public static String javascriptEscape(String cdata) {
    if (cdata == null) {
      return null;
    }

    return cdata.replace("'", "\\'").replace("\n", "\\n").trim();
  }

  /**
   * Converts a list of PubItemVOPresentations to a list of PubItems.
   * 
   * @param list the list of PubItemVOPresentations
   * @return the list of PubItemVOs
   */
  public static ArrayList<PubItemVO> convertToPubItemVOList(List<PubItemVOPresentation> list) {
    ArrayList<PubItemVO> pubItemList = new ArrayList<PubItemVO>();

    for (int i = 0; i < list.size(); i++) {
      pubItemList.add(new PubItemVO(list.get(i)));
    }

    return pubItemList;
  }

  /**
   * Converts a list of PubItems to a list of PubItemVOPresentations.
   * 
   * @param list the list of PubItemVOs
   * @return the list of PubItemVOPresentations
   */
  public static List<PubItemVOPresentation> convertToPubItemVOPresentationList(
      List<? extends PubItemVO> list) {
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();

    for (int i = 0; i < list.size(); i++) {
      pubItemList.add(new PubItemVOPresentation(list.get(i)));
    }

    return pubItemList;
  }

  /**
   * Converts a list of PubItems to a list of PubItemVOPresentations.
   * 
   * @param list the list of PubItemVOs
   * @return the list of PubItemVOPresentations
   */
  public static List<PubFileVOPresentation> convertToPubFileVOPresentationList(
      List<? extends FileVO> list) {
    List<PubFileVOPresentation> pubFileList = new ArrayList<PubFileVOPresentation>();

    for (int i = 0; i < list.size(); i++) {
      pubFileList.add(new PubFileVOPresentation(i, list.get(i)));
    }

    return pubFileList;
  }

  /**
   * Converts a list of Relations to a list of RelationVOPresentation.
   * 
   * @param list the list of RelationVO
   * @return the list of RelationVOPresentation
   */
  public static List<RelationVOPresentation> convertToRelationVOPresentationList(
      List<RelationVO> list) {
    List<RelationVOPresentation> relationList = new ArrayList<RelationVOPresentation>();

    for (int i = 0; i < list.size(); i++) {
      relationList.add(new RelationVOPresentation(list.get(i)));
    }

    return relationList;
  }

  /**
   * Converts a list of PubCollections to a list of PubCollectionVOPresentations.
   * 
   * @param list the list of ContextVOs
   * @return the list of PubCollectionVOPresentations
   */
  public static List<PubContextVOPresentation> convertToPubCollectionVOPresentationList(
      List<ContextVO> list) {
    List<PubContextVOPresentation> contextList = new ArrayList<PubContextVOPresentation>();

    for (int i = 0; i < list.size(); i++) {
      contextList.add(new PubContextVOPresentation(list.get(i)));
    }

    return contextList;
  }

  /**
   * Converts a list of AffiliationVOs to a list of AffiliationVOPresentations.
   * 
   * @param list the list of AffiliationVOs
   * @return the list of AffiliationVOPresentations
   */
  public static List<AffiliationVOPresentation> convertToAffiliationVOPresentationList(
      List<AffiliationVO> list) {
    List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();

    for (int i = 0; i < list.size(); i++) {
      affiliationList.add(new AffiliationVOPresentation(list.get(i)));
    }
    AffiliationVOPresentation[] affiliationArray =
        affiliationList.toArray(new AffiliationVOPresentation[] {});
    Arrays.sort(affiliationArray);

    return Arrays.asList(affiliationArray);
  }

  public static String currentDate() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    return sdf.format(cal.getTime());
  }


  public static boolean getIsUriValidUrl(IdentifierVO id) {
    boolean valid = false;
    try {
      if (id.getType() == null) {
        return false;
      }
      if (id.getType().equals(IdType.URI) || id.getType().equals(IdType.CONE)) {
        new URL(id.getId());
        valid = true;
      }
    } catch (MalformedURLException e) {
      logger.warn("URI: " + id.getId() + "is no valid URL");
      return false;
    }

    return valid;
  }

  public static Map<String, String> getDecodedUrlParameterMap(String query)
      throws UnsupportedEncodingException {
    logger.info("query: " + query);
    String[] parameters = query.split("&");
    Map<String, String> parameterMap = new HashMap<String, String>();
    for (String param : parameters) {
      String[] keyValueParts = param.split("=");
      if (keyValueParts.length == 1) {
        keyValueParts = new String[] {keyValueParts[0], ""};
      }
      parameterMap.put(keyValueParts[0], URLDecoder.decode(keyValueParts[1], "UTF-8"));
    }

    return parameterMap;
  }

  /**
   * Transforms broken ISO-8859-1 strings into correct UTF-8 strings.
   * 
   * @param brokenValue
   * @return hopefully fixed string.
   */
  public static String fixURLEncoding(String input) {
    if (input != null) {
      try {
        String utf8 = new String(input.getBytes("ISO-8859-1"), "UTF-8");
        if (utf8.equals(input) || utf8.contains("�") || utf8.length() == input.length()) {
          return input;
        } else {
          return utf8;
        }
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    return null;
  }

  public static String getGenericItemLink(String objectId, int version) throws Exception {
    if (objectId != null) {
      return PropertyReader.getProperty("escidoc.pubman.instance.url")
          + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
          + PropertyReader.getProperty("escidoc.pubman.item.pattern").replaceAll("\\$1",
              objectId + (version != 0 ? ":" + version : ""));
    }

    return null;
  }

  public static String getGenericItemLink(String objectId) throws Exception {
    return getGenericItemLink(objectId, 0);
  }
}
