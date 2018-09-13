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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONException;
import org.json.JSONObject;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.service.util.JsonUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "SearchAndExportPage")
@SessionScoped
@SuppressWarnings("serial")
public class SearchAndExportPage extends BreadcrumbPage {
  private static final Logger logger = Logger.getLogger(SearchAndExportPage.class);

  private final SearchAndExportService saes = ApplicationBean.INSTANCE.getSearchAndExportService();

  private SearchSortCriteria.SortOrder sortOrder;

  private String esQuery;
  private String sortingKey;
  private String limit;
  private String offset;

  private final int maxLimit = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_MAX_LIMIT));

  public SearchAndExportPage() {}

  // Wird bei jedem Aufruf des Beans ausgefuehrt -> SearchAndExportPage.jsp: <f:event type="preRenderView" listener="#{SearchAndExportPage.init}" />
  @Override
  public void init() {
    super.init();

    String oldQuery = this.esQuery;

    if (FacesTools.getCurrentInstance().getRenderResponse()) {
      final HttpServletRequest request = FacesTools.getRequest();

      try {
        if (request.getQueryString() != null) {
          final String decodedQuery = URLDecoder.decode(request.getQueryString(), "UTF-8");
          this.esQuery = decodedQuery.substring(decodedQuery.indexOf(SearchRetrieverRequestBean.parameterElasticSearchQuery)
              + SearchRetrieverRequestBean.parameterElasticSearchQuery.length() + 1);
          this.esQuery = JsonUtil.prettifyJsonString(this.esQuery);
        }
      } catch (final Exception e) {
        SearchAndExportPage.logger.error("Error during reading GET parameters.", e);
      }

    }

    if (this.esQuery == null && oldQuery != null) {
      this.esQuery = oldQuery;
    } else if (this.esQuery == null) {
      this.esQuery = PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_DEFAULT_QUERY);
    }
  }

  // Wird nur 1x während der Lebenszeit des Beans aufgerufen
  @PostConstruct
  public void postConstruct() {
    this.limit = PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_MAXIMUM_RECORDS);
    this.offset = PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_START_RECORD);
    this.sortOrder = PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_DEFAULT_SORT_ORDER).equalsIgnoreCase("ascending")
        ? SearchSortCriteria.SortOrder.ASC
        : SearchSortCriteria.SortOrder.DESC;
    this.sortingKey = PropertyReader.getProperty(PropertyReader.INGE_SEARCH_AND_EXPORT_DEFAULT_SORT_KEY);
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  public void searchAndExport() {
    final SearchAndExportRetrieveRequestVO saerrVO = parseInput();
    final SearchAndExportResultVO searchAndExportResultVO = search(saerrVO);
    createExportResponse(searchAndExportResultVO);
    FacesTools.getCurrentInstance().responseComplete();
  }

  private void createExportResponse(SearchAndExportResultVO searchAndExportResultVO) {
    final String contentType = searchAndExportResultVO.getTargetMimetype();
    FacesTools.getResponse().setContentType(contentType);
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + searchAndExportResultVO.getFileName());
    FacesTools.getResponse().setIntHeader("x-total-number-of-results", searchAndExportResultVO.getTotalNumberOfRecords());
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(searchAndExportResultVO.getResult());
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
    }
  }

  public void curl() {
    String curl = getCurl();
    createCurlResponse(curl);
    FacesTools.getCurrentInstance().responseComplete();
  }

  private void createCurlResponse(String curl) {
    FacesTools.getResponse().setContentType(FileFormatVO.TXT_MIMETYPE);
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=curl.txt");
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(curl.getBytes(StandardCharsets.UTF_8));
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put curl in HttpResponse body:", e);
    }
  }

  private SearchAndExportResultVO search(SearchAndExportRetrieveRequestVO saerrVO) {
    SearchAndExportResultVO searchAndExportResultVO = null;

    try {
      searchAndExportResultVO = saes.searchAndExportItems(saerrVO, this.getLoginHelper().getAuthenticationToken());
    } catch (final Exception e) {
      throw new RuntimeException("Cannot retrieve export data", e);
    }

    return searchAndExportResultVO;
  }

  private SearchAndExportRetrieveRequestVO parseInput() {
    try {
      final ExportItemsSessionBean sb = (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");
      final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();

      QueryBuilder queryBuilder = (QueryBuilder) QueryBuilders.wrapperQuery(this.esQuery);

      ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
      if (this.sortingKey != null && this.sortingKey.length() > 0) {
        SearchSortCriteria searchSortCriteria = new SearchSortCriteria(this.sortingKey, this.sortOrder);
        sortCriterias.add(searchSortCriteria);
      }

      int _limit = Integer.parseInt(this.limit);
      int _offset = Integer.parseInt(this.offset);

      SearchRetrieveRequestVO srrVO = new SearchRetrieveRequestVO();
      srrVO.setQueryBuilder(queryBuilder);
      srrVO.setSortKeys(sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));
      srrVO.setLimit(_limit);
      srrVO.setOffset(_offset);

      SearchAndExportRetrieveRequestVO saerrVO = new SearchAndExportRetrieveRequestVO(srrVO, curExportFormat);

      return saerrVO;
    } catch (final Exception e) {
      throw new RuntimeException("Cannot parse input", e);
    }
  }

  public String getEsQuery() {
    return this.esQuery;
  }

  public void setEsQuery(String esQuery) {
    this.esQuery = esQuery;
  }

  public String getLimit() {
    return this.limit;
  }

  public void setLimit(String limit) {
    this.limit = limit;
  }

  public String getOffset() {
    return this.offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getSortingKey() {
    return this.sortingKey;
  }

  public void setSortingKey(String sortingKey) {
    this.sortingKey = sortingKey;
  }

  public SelectItem[] getSortOptions() {
    final SelectItem ascending = new SelectItem(SearchSortCriteria.SortOrder.ASC, "ascending");
    final SelectItem descending = new SelectItem(SearchSortCriteria.SortOrder.DESC, "descending");

    final SelectItem[] sortOptions = new SelectItem[] { //
        ascending, //
        descending};

    return sortOptions;
  }

  public SearchSortCriteria.SortOrder getSortOrder() {
    return this.sortOrder;
  }

  public void setSortOrder(SearchSortCriteria.SortOrder sortOption) {
    this.sortOrder = sortOption;
  }

  public int getMaxLimit() {
    return this.maxLimit;
  }

  public String getAtomFeedLink() throws PubManVersionNotAvailableException, UnsupportedEncodingException {
    if (this.esQuery == null) {
      return null;
    }

    return ApplicationBean.INSTANCE.getPubmanInstanceUrl() + "/rest/feed/search?q="
        + URLEncoder.encode(this.esQuery, StandardCharsets.UTF_8.toString());
  }

  private String getCurl() {
    if (this.esQuery == null) {
      return null;
    }

    SearchAndExportRetrieveRequestVO saerrVO = parseInput();

    try {
      StringBuilder sb = new StringBuilder();
      sb.append("curl -X POST ");
      sb.append("\"");
      sb.append(ApplicationBean.INSTANCE.getPubmanInstanceUrl());
      sb.append("/rest/items/search");
      sb.append(getParameterString(saerrVO));
      sb.append("\" ");
      sb.append("-H 'Cache-Control: no-cache' -H 'Content-Type: application/json' ");
      sb.append("-d '");
      sb.append(getSearchString(saerrVO));
      sb.append("'");

      return sb.toString();
    } catch (PubManVersionNotAvailableException e) {
      throw new RuntimeException("Cannot get curl", e);
    }
  }

  private String getParameterString(SearchAndExportRetrieveRequestVO saerrVO) {
    StringBuilder sb = new StringBuilder();

    sb.append("?format=");
    sb.append(saerrVO.getExportFormat().getFormat());

    if (saerrVO.getExportFormat().getCitationName() != null)
      sb.append("&citation=");
    sb.append(saerrVO.getExportFormat().getCitationName());

    if (saerrVO.getExportFormat().getId() != null) {
      sb.append("&cslConeId=");
      sb.append(saerrVO.getExportFormat().getId());
    }

    return sb.toString();
  }

  private String getSearchString(SearchAndExportRetrieveRequestVO saerrVO) {
    StringBuilder sb = new StringBuilder();

    sb.append("{");

    sb.append("\"query\" : ");
    sb.append(this.esQuery);

    if (this.sortingKey != null && !this.sortingKey.isEmpty()) {
      sb.append(",");
      sb.append("\"sort\" : ");
      sb.append("[{\"" + this.sortingKey + "\" : {\"order\" : \"" + this.sortOrder.name() + "\"}}]");
    }

    sb.append(",");
    sb.append("\"size\" : ");
    sb.append("\"");
    sb.append(saerrVO.getSearchRetrieveRequestVO().getLimit());
    sb.append("\"");
    sb.append(",");

    sb.append("\"from\" : ");
    sb.append("\"");
    sb.append(saerrVO.getSearchRetrieveRequestVO().getOffset());
    sb.append("\"");

    sb.append("}");

    return sb.toString();
  }

  public void updatePage() {
    // reloads page
  }

  public void validateOffset(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    int offset_;
    String msg;

    // Test auf Zahl
    try {
      offset_ = Integer.parseInt((String) value);
    } catch (NumberFormatException ex) {
      msg = getMessage("searchAndExport_error_number");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    // Test auf positive Zahl
    if (offset_ < 0) {
      msg = getMessage("searchAndExport_error_number_GE_0");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
  }

  public void validateLimit(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    int limit_;
    String msg;

    // Test auf Zahl
    try {
      limit_ = Integer.parseInt((String) value);
    } catch (NumberFormatException ex) {
      msg = getMessage("searchAndExport_error_number");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }

    // Test auf korrektes Intervall
    if (limit_ <= 0 || limit_ > this.maxLimit) {
      msg = getMessage("searchAndExport_error_numberBetween_GT_LE").replace("$1", ("0")).replace("$2", "" + this.maxLimit);
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
  }

  public void validateQuery(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String msg;

    try {
      new JSONObject((String) value);
    } catch (JSONException e) {
      msg = getMessage("searchAndExport_error_validateQuery");
      throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
  }

}
