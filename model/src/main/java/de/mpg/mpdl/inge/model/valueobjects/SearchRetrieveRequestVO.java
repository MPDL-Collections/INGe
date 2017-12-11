package de.mpg.mpdl.inge.model.valueobjects;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

public class SearchRetrieveRequestVO extends ValueObject {

  private QueryBuilder queryBuilder;

  private List<AggregationBuilder> aggregationBuilders = new ArrayList<>();

  // use -1 for limit set by property (currently 10000)
  private int limit = -1;

  private int offset = 0;

  private SearchSortCriteria[] sortKeys;

  public SearchRetrieveRequestVO() {

  }

  public SearchRetrieveRequestVO(AggregationBuilder aggBuilder) {
    this.aggregationBuilders.add(aggBuilder);
  }



  public SearchRetrieveRequestVO(QueryBuilder queryBuilder, int limit, int offset, SearchSortCriteria... sortKeys) {
    super();
    this.setQueryBuilder(queryBuilder);
    this.limit = limit;
    this.offset = offset;
    this.sortKeys = sortKeys;
  }


  public SearchRetrieveRequestVO(QueryBuilder queryBuilder, SearchSortCriteria... sortKeys) {
    this(queryBuilder, -1, 0, sortKeys);
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public SearchSortCriteria[] getSortKeys() {
    return sortKeys;
  }

  public void setSortKeys(SearchSortCriteria[] sortKeys) {
    this.sortKeys = sortKeys;
  }

  public QueryBuilder getQueryBuilder() {
    return queryBuilder;
  }

  public void setQueryBuilder(QueryBuilder queryBuilder) {
    this.queryBuilder = queryBuilder;
  }

  public List<AggregationBuilder> getAggregationBuilders() {
    return aggregationBuilders;
  }

  public void setAggregationBuilders(List<AggregationBuilder> aggregationBuilders) {
    this.aggregationBuilders = aggregationBuilders;
  }



}
