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
package de.mpg.mpdl.inge.pubman.web.search.criterions.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;

@SuppressWarnings("serial")
public abstract class EnumListSearchCriterion<T extends Enum<T>> extends SearchCriterionBase {


  private Map<T, Boolean> enumMap = new LinkedHashMap<T, Boolean>();

  private final Class<T> enumClass;

  public EnumListSearchCriterion(Class<T> clazz) {
    this.enumClass = clazz;
    this.initEnumMap();
  }


  public Map<T, Boolean> initEnumMap() {

    for (final T v : this.enumClass.getEnumConstants()) {
      this.enumMap.put(v, false);
    }
    return this.enumMap;

  }



  public List<T> getEnumList() {
    final List<T> list = new ArrayList<T>();
    for (final T t : this.enumMap.keySet()) {
      list.add(t);
    }
    return list;
  }



  //  @Override
  //  public String toCqlString(Index indexName) {
  //
  //    final StringBuffer sb = new StringBuffer();
  //    boolean enumSelected = false;
  //    boolean enumDeselected = false;
  //
  //
  //
  //    // List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
  //
  //    // returnList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
  //    sb.append("(");
  //
  //    int i = 0;
  //    for (final Entry<T, Boolean> entry : this.enumMap.entrySet()) {
  //      if (entry.getValue() && i > 0) {
  //        sb.append(" OR ");
  //        // /returnList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
  //      }
  //
  //      if (entry.getValue()) {
  //
  //
  //        enumSelected = true;
  //        // ComponentVisibilitySearchCriterion gc = new ComponentVisibilitySearchCriterion();
  //        // gc.setSearchString(entry.getKey().name().toLowerCase());
  //        sb.append(this.getSearchValue(entry.getKey()));
  //        i++;
  //
  //
  //      } else {
  //        enumDeselected = true;
  //        // allGenres = false;
  //      }
  //
  //    }
  //
  //    // returnList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
  //    sb.append(")");
  //
  //    if ((enumSelected && enumDeselected)) {
  //      return sb.toString();
  //    }
  //
  //    return null;
  //  }

  @Override
  public String toQueryString() {
    final StringBuffer sb = new StringBuffer();
    sb.append(this.getSearchCriterion() + "=\"");

    boolean allChecked = true;
    int i = 0;
    for (final Entry<T, Boolean> entry : this.getEnumMap().entrySet()) {
      if (entry.getValue()) {
        if (i > 0) {
          sb.append("|");
        }
        sb.append(entry.getKey().name());
        i++;
      } else {
        allChecked = false;
      }
    }

    sb.append("\"");
    if (!allChecked) {
      return sb.toString();
    }

    return null;
  }

  @Override
  public void parseQueryStringContent(String content) {

    for (final Entry<T, Boolean> e : this.getEnumMap().entrySet()) {
      e.setValue(false);
    }


    // Split by '|', which have no backslash before and no other '|' after
    final String[] enumParts = content.split("(?<!\\\\)\\|(?!\\|)");
    for (final String part : enumParts) {

      final T v = Enum.valueOf(this.enumClass, part);
      if (v == null) {
        throw new RuntimeException("Invalid visibility: " + part);
      }
      this.getEnumMap().put(v, true);
    }

  }

  /**
   * List is empty if either all genres or degrees are selected or all are deselected
   */
  @Override
  public boolean isEmpty(QueryType queryType) {

    boolean anySelected = false;
    boolean anyDeselected = false;
    for (final Entry<T, Boolean> entry : this.getEnumMap().entrySet()) {
      if (entry.getValue()) {
        anySelected = true;
      } else {
        anyDeselected = true;
      }
    }



    return !(anySelected && anyDeselected);
  }



  public Map<T, Boolean> getEnumMap() {
    return this.enumMap;
  }

  public void setEnumMap(Map<T, Boolean> enumMap) {
    this.enumMap = enumMap;
  }

  public abstract String getSearchValue(T enumConstant);



}
