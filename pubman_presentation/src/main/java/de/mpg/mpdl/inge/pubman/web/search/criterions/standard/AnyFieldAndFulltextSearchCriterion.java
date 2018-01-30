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
package de.mpg.mpdl.inge.pubman.web.search.criterions.standard;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class AnyFieldAndFulltextSearchCriterion extends StandardSearchCriterion {



  @Override
  public QueryBuilder toElasticSearchQuery() {

    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    qb.should(QueryBuilders.simpleQueryStringQuery(getSearchString()));
    
    HasChildQueryBuilder childQueryBuilder = JoinQueryBuilders.hasChildQuery("file",
        QueryBuilders.matchQuery(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT, getSearchString()), ScoreMode.Avg);

    HighlightBuilder hb = new HighlightBuilder().field(PubItemServiceDbImpl.INDEX_FULLTEXT_CONTENT).preTags("<span class=\"searchHit\">").postTags("</span>");
    childQueryBuilder.innerHit(new InnerHitBuilder().setHighlightBuilder(hb));
    qb.should(childQueryBuilder);
    return qb;
  }

  @Override
  public String[] getCqlIndexes(Index indexName) {

    switch (indexName) {
      case ESCIDOC_ALL:
        return new String[] {"escidoc.metadata", "escidoc.fulltext"};
      case ITEM_CONTAINER_ADMIN:
        return new String[] {"\"/metadata\"", "\"/fulltext\""};
    }
    return null;

  }

  // TODO: Add fulltext index
  @Override
  public String[] getElasticIndexes() {
    return new String[] {"_all"};

  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }


  /*
   * @Override public SearchCriterion getSearchCriterion() { return SearchCriterion.ANYFULLTEXT; }
   */


}
