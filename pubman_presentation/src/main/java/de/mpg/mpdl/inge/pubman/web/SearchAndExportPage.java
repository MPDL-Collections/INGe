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
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.pubman.web.breadcrumb.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;

@ManagedBean(name = "SearchAndExportPage")
@SuppressWarnings("serial")
public class SearchAndExportPage extends BreadcrumbPage {
  private final SearchAndExportService saes = ApplicationBean.INSTANCE.getSearchAndExportService();

  private String esQuery =
      "{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"term\":{\"versionState\":{\"value\":\"RELEASED\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}}";
  private String limit = "5";
  private String offset = "1";
  private String sortingKey = "metadata.title.keyword";
  private SearchSortCriteria.SortOrder sortOption = SearchSortCriteria.SortOrder.ASC;

  public SearchAndExportPage() {}

  @Override
  public void init() {
    super.init();
  }

  @Override
  public boolean isItemSpecific() {
    return false;
  }

  public void searchAndExport() {
    final ExportItemsSessionBean sb = (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");
    final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();

    SearchAndExportResultVO searchAndExportResultVO = null;
    SearchAndExportRetrieveRequestVO saerrVO = null;
    QueryBuilder queryBuilder = (QueryBuilder) QueryBuilders.wrapperQuery(this.esQuery);
    ArrayList<SearchSortCriteria> _sortCriterias = new ArrayList<>();
    if (this.sortingKey != null) {
      SearchSortCriteria searchSortCriteria = new SearchSortCriteria(this.sortingKey, this.sortOption);
      _sortCriterias.add(searchSortCriteria);
    }
    int _limit = Integer.parseInt(this.limit);
    int _offset = Integer.parseInt(this.offset);

    try {
      saerrVO = new SearchAndExportRetrieveRequestVO(curExportFormat.getName(), curExportFormat.getFileFormat().getName(),
          curExportFormat.getId(), queryBuilder, _limit, _offset, _sortCriterias.toArray(new SearchSortCriteria[_sortCriterias.size()]));
      searchAndExportResultVO = saes.searchAndExportItems(saerrVO, this.getLoginHelper().getAuthenticationToken());
    } catch (final Exception e) {
      throw new RuntimeException("Cannot retrieve export data", e);
    }

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

    FacesTools.getCurrentInstance().responseComplete();
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

  public SearchSortCriteria.SortOrder getSortOption() {
    return this.sortOption;
  }

  public void setSortOption(SearchSortCriteria.SortOrder sortOption) {
    this.sortOption = sortOption;
  }


}
