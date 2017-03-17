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

package de.mpg.mpdl.inge.pubman.web.home;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 * Fragment class for the corresponding Home-JSP.
 * 
 * @author: Thomas Diebäcker, created 08.02.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@SuppressWarnings("serial")
public class Home extends FacesBean {
  public static final String BEAN_NAME = "Home";

  public static final String LOAD_HOME = "loadHome";

  public Home() {}

  // /**
  // * Callback method that is called whenever a page containing this page fragment is navigated to,
  // * either directly via a URL, or indirectly via page navigation.
  // */
  // public void init() {
  // // Perform initializations inherited from our superclass
  // //super.init();
  // }

  // public List<PubItemVOPresentation> getLatest() throws Exception {
  // // SearchRetrieverRequestBean srrb =
  // // (SearchRetrieverRequestBean)ictx.lookup(SearchRetrieverRequestBean.BEAN_NAME);
  // String cqlQuery =
  // "escidoc.content-model.objid="
  // + PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
  // SearchQuery cql = new PlainCqlQuery(cqlQuery);
  // cql.setMaximumRecords("4");
  // cql.setSortKeysAndOrder("sort.escidoc.last-modification-date", SortingOrder.DESCENDING);
  // ItemContainerSearchResult icsr = SearchService.searchForItemContainer(cql);
  // List<PubItemVOPresentation> list = SearchRetrieverRequestBean.extractItemsOfSearchResult(icsr);
  // return list;
  // }
}
