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

package de.mpg.mpdl.inge.pubman.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 * @author franke
 * 
 */
@ManagedBean(name = "UserAccountSuggest")
@SuppressWarnings("serial")
public class UserAccountSuggest extends FacesBean {
  private List<AccountUserVO> userAccountList;

  public UserAccountSuggest() throws Exception {
    Map<String, String> parameters = getExternalContext().getRequestParameterMap();
    String query = parameters.get("q");

    if (getLoginHelper().getESciDocUserHandle() != null) {
      if (query != null) {
        String queryString = "";
        for (String snippet : query.split(" ")) {
          if (!"".equals(queryString)) {
            queryString += " and ";
          }
          queryString +=
              "(\"/properties/name\"=\"%" + snippet + "%\"  or \"/properties/login-name\"=\"%"
                  + snippet + "%\")";
        }

        FilterTaskParamVO filter = new FilterTaskParamVO();
        Filter f1 = filter.new CqlFilter(queryString);
        filter.getFilterList().add(f1);
        Filter f3 = filter.new LimitFilter("50");
        filter.getFilterList().add(f3);

        UserAccountHandler uag =
            ServiceLocator.getUserAccountHandler(getLoginHelper().getESciDocUserHandle());
        String xmlUserList = uag.retrieveUserAccounts(filter.toMap());
        SearchRetrieveResponseVO resp =
            XmlTransformingService.transformToSearchRetrieveResponseAccountUser(xmlUserList);

        this.userAccountList = new ArrayList<AccountUserVO>();

        if (resp.getRecords() != null) {
          for (SearchRetrieveRecordVO rec : resp.getRecords()) {
            if (rec != null) {
              getUserAccountList().add((AccountUserVO) rec.getData());
            }
          }
        }
      }
    }
  }

  public List<AccountUserVO> getUserAccountList() {
    return this.userAccountList;
  }

  public void setUserAccountList(List<AccountUserVO> userAccountList) {
    this.userAccountList = userAccountList;
  }

  public int getUserAccountListSize() {
    if (this.userAccountList != null) {
      return this.userAccountList.size();
    }

    return 0;
  }
}
