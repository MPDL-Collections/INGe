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

package de.mpg.escidoc.pubman.search.bean.criterion;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;


/**
 * person criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:15:07
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class PersonCriterion extends Criterion {
  // creator role for the search criterion
  private List<CreatorRole> creatorRole;
  private String identifier;

  /**
   * constructor.
   */
  public PersonCriterion() {
    super();
  }

  public List<CreatorRole> getCreatorRole() {
    return creatorRole;
  }

  public void setCreatorRole(List<CreatorRole> creatorRole) {
    this.creatorRole = creatorRole;
  }

  public String getIdentifier() {
    System.out.println(identifier);
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  private String getRolesAsStringList() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < creatorRole.size(); i++) {
      buffer.append(creatorRole.get(i).getUri());
      if (i != creatorRole.size() - 1) {
        buffer.append(" OR ");
      }
    }
    return buffer.toString();
  }

  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    if (isSearchStringEmpty()) {
      return criterions;
    } else {
      if (identifier == null || "".equals(identifier)) {
        MetadataSearchCriterion criterion =
            new MetadataSearchCriterion(CriterionType.PERSON, getSearchString());
        criterions.add(criterion);
      } else {
        MetadataSearchCriterion criterion =
            new MetadataSearchCriterion(CriterionType.PERSON_IDENTIFIER, identifier);
        criterions.add(criterion);
      }
      if (creatorRole.size() != 0) {
        MetadataSearchCriterion criterion1 =
            new MetadataSearchCriterion(CriterionType.PERSON_ROLE, getRolesAsStringList(),
                LogicalOperator.AND);
        criterions.add(criterion1);
      }
    }
    return criterions;
  }
}
