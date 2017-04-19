package de.mpg.mpdl.inge.pubman.web.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemResultVO;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Search result
 * list. It executes the Search whenever the page is called with a GET cql parameter and a valid cql
 * query. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "SearchRetrieverRequestBean")
@SuppressWarnings("serial")
public class SearchRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(SearchRetrieverRequestBean.class);

  /**
   * The HTTP-GET parameter name for the cql query
   */
  public static String parameterCqlQuery = "cql";

  /**
   * The HTTP-GET parameter name for the query
   */
  public static String parameterQuery = "q";

  /**
   * The HTTP-GET parameter name for the elastic search query query
   */
  public static String parameterElasticSearchQuery = "esq";

  /**
   * The HTTP-GET parameter name for the search type (advanced, simple, ...)
   */
  public static String parameterSearchType = "searchType";

  /**
   * The current cqlQuery
   */
  private String cqlQuery;

  /**
   * The current internal pubman query;
   */
  private String queryString;

  private String elasticSearchQuery;

  /**
   * The total number of records from the search request
   */
  private int numberOfRecords;

  /**
   * The type of the search (simple, advanced, ...)
   */
  private String searchType;

  public static final String LOAD_SEARCHRESULTLIST = "showSearchResults";



  public SearchRetrieverRequestBean() {
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), false);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return this.numberOfRecords;
  }

  @Override
  public String getType() {
    return "SearchResult";
  }

  /**
   * Reads out the qql query and the search type from HTTP-GET parameeters. If cql is null, an error
   * message is shown. If search type is null, an default value is set
   */
  @Override
  public void readOutParameters() {
    final HttpServletRequest request = FacesTools.getRequest();

    // the following procedure is necessary because of the strange decoding in tomcat, when you
    // fetch the
    // parameters with the getParameter method. Japanese characters are decoded to a ISO format and
    // this
    // messes up the characters. Therefore we take the complete query string, which is not decoded,
    // extract the cql parameter
    // and decode it with UrlDecode.
    Map<String, String> paramMap = null;
    try {
      paramMap = CommonUtils.getDecodedUrlParameterMap(request.getQueryString());
    } catch (final UnsupportedEncodingException e) {
      SearchRetrieverRequestBean.logger.error("Error during reading GET parameters.", e);
    }



    final String query = paramMap.get(SearchRetrieverRequestBean.parameterQuery);

    if (query != null) {
      this.setQueryString(query);
    }

    paramMap.get(SearchRetrieverRequestBean.parameterCqlQuery);

    final String elasticSearchQuery =
        paramMap.get(SearchRetrieverRequestBean.parameterElasticSearchQuery);

    if ((elasticSearchQuery == null || elasticSearchQuery.equals(""))) {
      this.setElasticSearchQuery("");
      FacesBean
          .error("You have to call this page with a parameter \"esq\" and a elastic search query!");

    } else {
      this.setElasticSearchQuery(elasticSearchQuery);
    }



    final String searchType = paramMap.get(SearchRetrieverRequestBean.parameterSearchType);
    if (searchType == null) {
      this.setSearchType("simple");
    } else {
      this.setSearchType(searchType);
    }

  }

  /*
   * public List<PubItemVOPresentation> retrieveListGenericSearch(int offset, int limit,
   * SORT_CRITERIA sc) { List<PubItemVOPresentation> pubItemList = null; //new
   * ArrayList<PubItemVOPresentation>(); //checkSortCriterias(sc); try {
   * 
   * 
   * 
   * PlainCqlQuery query = new PlainCqlQuery(getCqlQuery()); query.setStartRecord(new
   * PositiveInteger(String.valueOf(offset+1))); query.setMaximumRecords(new
   * NonNegativeInteger(String.valueOf(limit)));
   * 
   * if(sc.getIndex()!=null) { query.setSortKeys(sc.getIndex()); }
   * 
   * if(sc.getIndex() == null || !sc.getIndex().equals("")) { if
   * (sc.getSortOrder().equals(OrderFilter.ORDER_DESCENDING)) {
   * 
   * query.setSortOrder(SortingOrder.DESCENDING); }
   * 
   * else { query.setSortOrder(SortingOrder.ASCENDING); } } ItemContainerSearchResult result =
   * this.searchService.searchForItemContainer(query);
   * 
   * pubItemList = extractItemsOfSearchResult(result); this.numberOfRecords =
   * Integer.parseInt(result.getTotalNumberOfResults().toString()); } catch (Exception e) {
   * error("Error in search!"); logger.error("Error during search. ", e); }
   * 
   * return pubItemList; }
   */

  /*
   * public List<PubItemVOPresentation> retrieveListAdminSearch(int offset, int limit, SORT_CRITERIA
   * sc) { List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
   * LoginHelper loginHelper = (LoginHelper) FacesTools.findBean(LoginHelper.class); try {
   * ItemHandler itemHandler = null;
   * 
   * if(loginHelper.getESciDocUserHandle()!=null) { itemHandler =
   * ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()); } else { itemHandler =
   * ServiceLocator.getItemHandler(); }
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * checkSortCriterias(sc);
   * 
   * FilterTaskParamVO filter = new FilterTaskParamVO(); Filter f1 = filter.new
   * CqlFilter(getCqlQuery()); filter.getFilterList().add(f1); Filter f2 = filter.new
   * OrderFilter(sc.getSortPath(), sc.getSortOrder()); filter.getFilterList().add(f2); Filter f3 =
   * filter.new LimitFilter(String.valueOf(limit)); filter.getFilterList().add(f3); Filter f4 =
   * filter.new OffsetFilter(String.valueOf(offset)); filter.getFilterList().add(f4);
   * 
   * 
   * String xmlItemList = itemHandler.retrieveItems(filter.toMap());
   * 
   * 
   * ItemVOListWrapper pubItemList =
   * xmlTransforming.transformSearchRetrieveResponseToItemList(xmlItemList);
   * 
   * numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords()); returnList =
   * CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList.getItemVOList());
   * } catch (Exception e) { logger.error("Error in retrieving items", e);
   * error("Error in retrieving items"); numberOfRecords = 0; } return returnList; } /*
   * 
   * 
   * /** Calls the search service and requests the items for the current cql query.
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {

    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    // checkSortCriterias(sc);
    try {



      final QueryBuilder qb = QueryBuilders.wrapperQuery(this.elasticSearchQuery);
      final SearchRetrieveRequestVO<QueryBuilder> query =
          new SearchRetrieveRequestVO<QueryBuilder>(qb, limit, offset, null);
      final SearchRetrieveResponseVO result =
          ApplicationBean.INSTANCE.getPubItemService().search(query, null);
      this.numberOfRecords = result.getNumberOfRecords();

      pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);



      /*
       * PlainCqlQuery query = new PlainCqlQuery(getCqlQuery()); query.setStartRecord(new
       * PositiveInteger(String.valueOf(offset + 1))); query.setMaximumRecords(new
       * NonNegativeInteger(String.valueOf(limit)));
       * 
       * if (sc.getIndex() != null) { if ("admin".equals(getSearchType())) {
       * query.setSortKeys(sc.getSortPath()); } else { query.setSortKeys(sc.getIndex()); }
       * 
       * }
       * 
       * if (sc.getIndex() == null || !sc.getIndex().equals("")) { if
       * (sc.getSortOrder().equals(OrderFilter.ORDER_DESCENDING)) {
       * 
       * query.setSortOrder(SortingOrder.DESCENDING); }
       * 
       * else { query.setSortOrder(SortingOrder.ASCENDING); } } ItemContainerSearchResult result =
       * null;
       * 
       * if ("admin".equals(getSearchType())) { result =
       * SearchService.searchForItemContainerAdmin(query, getLoginHelper() .getESciDocUserHandle());
       * } else { result = SearchService.searchForItemContainer(query); }
       * 
       * 
       * 
       * 
       * pubItemList = extractItemsOfSearchResult(result); this.numberOfRecords =
       * Integer.parseInt(result.getTotalNumberOfResults().toString());
       */
    } catch (final Exception e) {
      FacesBean.error("Error in search!");
      SearchRetrieverRequestBean.logger.error("Error during search. ", e);
    }

    return pubItemList;
  }

  /**
   * Sets the current cql query
   * 
   * @param cqlQuery
   */
  public void setCqlQuery(String cqlQuery) {
    this.cqlQuery = cqlQuery;
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(SearchRetrieverRequestBean.parameterCqlQuery, cqlQuery);
  }

  /**
   * Returns the current cql query
   * 
   * @return
   */
  public String getCqlQuery() {
    return this.cqlQuery;
  }

  /**
   * Returns the current cql query without blanks
   * 
   * @return
   */
  public String getNormalizedCqlQuery() {
    final String ret = this.cqlQuery;
    if (ret != null) {
      return URLEncoder.encode(ret);
    }

    return "";
  }

  /**
   * @return link to the rss feed for the current search
   * @throws PubManVersionNotAvailableException
   */
  public String getRssFeedLink() throws PubManVersionNotAvailableException {
    return "<link href='"
        + ((ApplicationBean) FacesTools.findBean("ApplicationBean")).getPubmanInstanceUrl()
        + "/syndication/feed/rss_2.0/search?q=" + this.getNormalizedCqlQuery()
        + "' rel='alternate' type='application/rss+xml' title='Current Search | rss 2.0' />";
  }

  /**
   * @return link to the atom feed for the current search
   * @throws PubManVersionNotAvailableException
   */
  public String getAtomFeedLink() throws PubManVersionNotAvailableException {
    return "<link href='"
        + ((ApplicationBean) FacesTools.findBean("ApplicationBean")).getPubmanInstanceUrl()
        + "/syndication/feed/atom_1.0/search?q=" + this.getNormalizedCqlQuery()
        + "' rel='alternate' type='application/atom+xml' title='Current Search | atom 1.0' />";
  }

  /**
   * Helper method that transforms the result of the search into a list of PubItemVOPresentation
   * objects.
   * 
   * @param result
   * @return
   */
  public static ArrayList<PubItemVOPresentation> extractItemsOfSearchResult(
      ItemContainerSearchResult result) {

    final List<SearchResultElement> results = result.getResultList();

    final ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    for (int i = 0; i < results.size(); i++) {
      // check if we have found an item
      if (results.get(i) instanceof ItemResultVO) {
        // cast to PubItemResultVO
        final ItemResultVO item = (ItemResultVO) results.get(i);
        final PubItemResultVO pubItemResult =
            new PubItemResultVO(item, item.getSearchHitList(), item.getScore());
        final PubItemVOPresentation pubItemPres = new PubItemVOPresentation(pubItemResult);
        pubItemList.add(pubItemPres);
      }
    }

    return pubItemList;
  }


  public static ArrayList<PubItemVOPresentation> extractItemsOfSearchResult(
      SearchRetrieveResponseVO result) {

    final List<SearchRetrieveRecordVO> results = result.getRecords();

    final ArrayList<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    for (int i = 0; i < results.size(); i++) {
      // check if we have found an item

      final SearchRetrieveRecordVO record = results.get(i);
      final PubItemVOPresentation pubItemPres =
          new PubItemVOPresentation((PubItemVO) record.getData());
      pubItemList.add(pubItemPres);

    }

    return pubItemList;
  }

  @Override
  public String getListPageName() {
    return "SearchResultListPage.jsp";
  }

  /**
   * Sets the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to display
   * search type specific elements.
   * 
   * @param searchType
   */
  public void setSearchType(String searchType) {
    this.searchType = searchType;
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(SearchRetrieverRequestBean.parameterSearchType, searchType);
  }

  /**
   * Returns the search type (e.g. advanced, simple, ...) Can be used in the jspf in order to
   * display search type specific elements
   * 
   * @return
   */
  public String getSearchType() {
    return this.searchType;
  }

  /**
   * Checks if the selected sorting criteria is currently available. If not (empty string), it
   * displays a warning message to the user.
   * 
   * @param sc The sorting criteria to be checked
   */
  protected void checkSortCriterias(SORT_CRITERIA sc) {
    if (sc.getIndex() == null || sc.getIndex().equals("")) {
      FacesBean.error(this.getMessage("depositorWS_sortingNotSupported").replace("$1",
          this.getLabel("ENUM_CRITERIA_" + sc.name())));
    }
  }

  public String getQueryString() {
    return this.queryString;
  }

  public String getUrlEncodedQueryString() {
    try {
      if (this.queryString != null) {
        return URLEncoder.encode(this.queryString, "UTF-8");
      }
    } catch (final UnsupportedEncodingException e) {
      SearchRetrieverRequestBean.logger.error("Could not encode query string", e);
    }

    return "";
  }

  public void setQueryString(String query) {
    this.queryString = query;
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(SearchRetrieverRequestBean.parameterQuery, query);
  }

  public String getElasticSearchQuery() {
    return this.elasticSearchQuery;
  }

  public void setElasticSearchQuery(String elasticSearchQuery) {
    this.elasticSearchQuery = elasticSearchQuery;
    this.getBasePaginatorListSessionBean().getParameterMap()
        .put(SearchRetrieverRequestBean.parameterElasticSearchQuery, elasticSearchQuery);
  }

}
