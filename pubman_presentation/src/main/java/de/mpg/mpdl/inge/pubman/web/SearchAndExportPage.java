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
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
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

  private final int maxLimit = Integer.parseInt(PropertyReader.getProperty("inge.search.and.export.max.limit"));

  public SearchAndExportPage() {}

  // Wird bei jedem Aufruf des Beans ausgefuehrt -> SearchAndExportPage.jsp: <f:event type="preRenderView" listener="#{SearchAndExportPage.init}" />
  @Override
  public void init() {
    super.init();

    String oldQuery = this.esQuery;

    if (FacesTools.getCurrentInstance().getRenderResponse()) {
      final HttpServletRequest request = FacesTools.getRequest();
      Map<String, String> paramMap = null;
      try {
        paramMap = CommonUtils.getDecodedUrlParameterMap(request.getQueryString());
      } catch (final UnsupportedEncodingException e) {
        SearchAndExportPage.logger.error("Error during reading GET parameters.", e);
      }

      this.esQuery = paramMap.get(SearchRetrieverRequestBean.parameterElasticSearchQuery);
    }

    if (this.esQuery == null && oldQuery != null) {
      this.esQuery = oldQuery;
    } else if (this.esQuery == null) {
      this.esQuery = PropertyReader.getProperty("inge.search.and.export.default.query");
    }
  }

  // Wird nur 1x während der Lebenszeit des Beans aufgerufen
  @PostConstruct
  public void postConstruct() {
    this.limit = PropertyReader.getProperty("inge.search.and.export.maximum.records");
    this.offset = PropertyReader.getProperty("inge.search.and.export.start.record");
    this.sortOrder = PropertyReader.getProperty("inge.search.and.export.default.sort.order").equalsIgnoreCase("ascending")
        ? SearchSortCriteria.SortOrder.ASC
        : SearchSortCriteria.SortOrder.DESC;
    this.sortingKey = PropertyReader.getProperty("inge.search.and.export.default.sort.key");
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  public void searchAndExport() {
    final SearchAndExportRetrieveRequestVO saerrVO = parseInput();
    final SearchAndExportResultVO searchAndExportResultVO = doSearch(saerrVO);
    createResponse(searchAndExportResultVO);
    FacesTools.getCurrentInstance().responseComplete();
  }

  private void createResponse(SearchAndExportResultVO searchAndExportResultVO) {
    final String contentType = searchAndExportResultVO.getTargetMimetype();
    FacesTools.getResponse().setContentType(contentType);
    FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + searchAndExportResultVO.getFileName());
    try {
      final OutputStream out = FacesTools.getResponse().getOutputStream();
      out.write(searchAndExportResultVO.getResult());
      out.close();
    } catch (final Exception e) {
      throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
    }
  }

  private SearchAndExportResultVO doSearch(SearchAndExportRetrieveRequestVO saerrVO) {
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

      if (this.limit == null || this.limit.trim().length() == 0) {
        this.limit = PropertyReader.getProperty("inge.search.and.export.maximum.records");
      }

      int _limit = Integer.parseInt(this.limit);
      if (_limit > this.maxLimit) {
        _limit = this.maxLimit;
      }

      if (this.offset == null || this.offset.trim().length() == 0) {
        this.offset = PropertyReader.getProperty("inge.search.and.export.start.record");
      }

      int _offset = Integer.parseInt(this.offset) - 1;

      SearchAndExportRetrieveRequestVO saerrVO =
          new SearchAndExportRetrieveRequestVO(curExportFormat.getName(), curExportFormat.getFileFormat().getName(),
              curExportFormat.getId(), queryBuilder, _limit, _offset, sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));

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

}
