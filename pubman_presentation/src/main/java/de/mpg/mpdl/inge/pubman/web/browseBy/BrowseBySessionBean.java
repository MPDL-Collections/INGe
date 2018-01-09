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

package de.mpg.mpdl.inge.pubman.web.browseBy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.LinkVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * 
 * Session Bean for Browse By
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "BrowseBySessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class BrowseBySessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(BrowseBySessionBean.class);

  public static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ".toCharArray();

  private List<LinkVO> searchResults;
  private String currentCharacter = "A";
  private String dateMode = "published";
  private String dateType = "published";
  private String pubContentModel = "";
  private String query = "q";
  private String selectedValue = "persons";
  private String[] characters = null;
  private boolean showChars = true;
  private final int maxDisplay = 100;
  // private int yearStart;

  private Map<String, Long> yearMap = new TreeMap<>();

  public BrowseBySessionBean() {
    try {
      this.pubContentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
    } catch (final Exception e) {
      BrowseBySessionBean.logger.warn("Could not read property content model.", e);
    }
  }

  public void clear() {
    this.currentCharacter = "A";
    this.selectedValue = "persons";
    this.showChars = true;
    this.query = "q";
    this.dateType = "published";
  }

  public List<String> getControlledVocabs() {
    final List<String> vocabs = new ArrayList<String>();
    try {
      final String vocabsStr = PropertyReader.getProperty("inge.cone.subjectVocab");
      if (vocabsStr != null && vocabsStr.trim().length() > 0) {
        final String[] vocabsArr = vocabsStr.split(";");
        for (int i = 0; i < vocabsArr.length; i++) {
          vocabs.add(vocabsArr[i].trim());
        }
      }
    } catch (final Exception e) {
      BrowseBySessionBean.logger.error("Could not read Property: 'inge.cone.subjectVocab'", e);
    }
    return vocabs;
  }

  public List<LinkVO> getSearchResults() {
    return this.searchResults;
  }

  public void setSearchResults(List<LinkVO> searchResults) {
    this.searchResults = searchResults;
  }

  public String getCurrentCharacter() {
    return this.currentCharacter;
  }

  public void setCurrentCharacter(String currentCharacter) {
    this.currentCharacter = currentCharacter;
  }

  public String getSelectedValue() {
    return this.selectedValue;
  }

  public void setSelectedValue(String selectedValue) {
    this.selectedValue = selectedValue;
  }

  public int getMaxDisplay() {
    return this.maxDisplay;
  }

  /**
   * This method checks weather the searchResult from CoNE has to be devided into character,
   * according to the value of 'maxDisplay'
   * 
   * @return
   */
  public boolean isShowChars() {
    return this.showChars;
  }

  public void setShowChars() {
    if (!this.selectedValue.equals("year")) {
      final List<LinkVO> all = this.getConeAll();
      if (all.size() > this.getMaxDisplay()) {
        final SortedSet<Character> characters = new TreeSet<Character>();

        for (int i = 0; i < BrowseBySessionBean.CHARACTERS.length; i++) {
          characters.add(BrowseBySessionBean.CHARACTERS[i]);
        }

        // for (LinkVO linkVO : all)
        // {
        // Character chr = new Character(linkVO.getLabel().toUpperCase().charAt(0));
        // if (!characters.contains(chr))
        // {
        // logger.debug("new character: " + linkVO.getLabel());
        // characters.add(chr);
        // }
        // }

        this.characters = new String[characters.size()];
        int counter = 0;

        for (final Iterator<Character> iterator = characters.iterator(); iterator.hasNext();) {
          final Character character = iterator.next();
          this.characters[counter] = character.toString();
          counter++;
        }

        this.showChars = true;
      } else {
        this.showChars = false;
      }
    }
  }

  /**
   * Call the cone service to retrieve all browse by values.
   * 
   * @param type, type of the cone request (persons, subjects, journals)
   * @return
   */
  public List<LinkVO> getConeAll() {
    final List<LinkVO> links = new ArrayList<LinkVO>();

    try {
      final URL coneUrl = new URL(PropertyReader.getProperty("inge.cone.service.url") + this.selectedValue + "/all?format=options&lang=en");
      final URLConnection conn = coneUrl.openConnection();
      final HttpURLConnection httpConn = (HttpURLConnection) conn;
      final int responseCode = httpConn.getResponseCode();

      switch (responseCode) {
        case 200:
          BrowseBySessionBean.logger.debug("Cone Service responded with 200.");
          break;
        default:
          throw new RuntimeException(
              "An error occurred while calling Cone Service: " + responseCode + ": " + httpConn.getResponseMessage());
      }

      final InputStreamReader isReader = new InputStreamReader(coneUrl.openStream(), "UTF-8");
      final BufferedReader bReader = new BufferedReader(isReader);
      String line = "";
      while ((line = bReader.readLine()) != null) {
        final String[] parts = line.split("\\|");
        if (parts.length == 2) {
          final LinkVO link = new LinkVO(parts[1], parts[0]);
          links.add(link);
        }
      }

      isReader.close();
      httpConn.disconnect();
    } catch (final Exception e) {
      BrowseBySessionBean.logger.warn("An error occurred while calling the Cone service.", e);
      return null;
    }

    return links;
  }



  private void fillDateMap(String... indexes) {

    try {

      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO();
      yearMap.clear();

      for (String index : indexes) {
        AggregationBuilder aggBuilder =
            AggregationBuilders.dateHistogram(index).field(index).dateHistogramInterval(DateHistogramInterval.YEAR).minDocCount(1);
        srr.getAggregationBuilders().add(aggBuilder);
      }

      srr.setLimit(0);

      SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, null);


      for (String index : indexes) {
        Histogram dh = resp.getOriginalResponse().getAggregations().get(index);

        for (Histogram.Bucket entry : dh.getBuckets()) {
          String year = entry.getKeyAsString().substring(0, 4);
          if (yearMap.containsKey(year)) {
            yearMap.put(year, yearMap.get(year) + entry.getDocCount());
          } else {
            yearMap.put(year, entry.getDocCount());
          }
        }


      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }


  /**
   * Searches the rep for the oldest year.
   */
  public void setYearPublished() {

    fillDateMap(new String[] {PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT,
        PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE});
  }

  public void setYearStartAny() {

    fillDateMap(new String[] {PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_IN_PRINT,
        PubItemServiceDbImpl.INDEX_METADATA_DATE_PUBLISHED_ONLINE, PubItemServiceDbImpl.INDEX_METADATA_DATE_ACCEPTED,
        PubItemServiceDbImpl.INDEX_METADATA_DATE_SUBMITTED, PubItemServiceDbImpl.INDEX_METADATA_DATE_MODIFIED,
        PubItemServiceDbImpl.INDEX_METADATA_DATE_CREATED,});
  }


  public String getQuery() {
    return this.query;
  }

  public void setQuery(String query) {
    this.query = query;
  }



  public String getDateType() {
    return this.dateType;
  }

  public void setDateType(String dateType) {
    this.dateType = dateType;
  }

  public String getPubContentModel() {
    return this.pubContentModel;
  }

  public String[] getCharacters() {
    return this.characters;
  }

  public void setCharacters(String[] characters) {
    this.characters = characters;
  }

  public String getDateMode() {
    return this.dateMode;
  }

  public void setDateMode(String dateMode) {
    this.dateMode = dateMode;
  }

  public Map<String, Long> getYearMap() {
    return yearMap;
  }

  public void setYearMap(Map<String, Long> yearMap) {
    this.yearMap = yearMap;
  }


}
