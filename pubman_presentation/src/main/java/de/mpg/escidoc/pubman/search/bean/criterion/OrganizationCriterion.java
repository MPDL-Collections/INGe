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

import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;

/**
 * organization criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:45:25
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class OrganizationCriterion extends Criterion {
  /**
   * constructor.
   */

  AffiliationVOPresentation affiliation = null;
  private boolean includePredecessorsAndSuccessors = false;

  public OrganizationCriterion() {
    super();
    AffiliationVO affiliationVO = new AffiliationVO();
    affiliationVO.setReference(new AffiliationRO());
    affiliation = new AffiliationVOPresentation(affiliationVO);

  }

  /**
   * {@inheritDoc}
   */
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    if (getAffiliation() != null && getAffiliation().getReference().getObjectId() != null
        && !"".equals(getAffiliation().getReference().getObjectId())) {
      MetadataSearchCriterion criterion =
          new MetadataSearchCriterion(CriterionType.CREATOR_ORGANIZATION_IDS_WITH_PATH,
              getAffiliation().getReference().getObjectId());
      criterions.add(criterion);
    } else if (isSearchStringEmpty() != true) {
      MetadataSearchCriterion criterion =
          new MetadataSearchCriterion(CriterionType.CREATOR_ORGANIZATION, getSearchString());
      criterions.add(criterion);
    }
    return criterions;
  }

  /**
   * @return the affiliation
   */
  public AffiliationVOPresentation getAffiliation() {
    return affiliation;
  }

  public String getAffiliationName() {
    if (affiliation == null) {
      return "";
    } else {
      return affiliation.getName();
    }
  }

  public boolean getAffiliationEmpty() {
    return (affiliation == null);
  }

  /**
   * @param affiliation the affiliation to set
   */
  public void setAffiliation(AffiliationVOPresentation affiliation) {
    this.affiliation = affiliation;
  }

  public void setIncludePredecessorsAndSuccessors(boolean include) {
    this.includePredecessorsAndSuccessors = include;
  }

  public boolean getIncludePredecessorsAndSuccessors() {
    return this.includePredecessorsAndSuccessors;
  }
}
