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

package de.mpg.mpdl.inge.pubman.web.search.bean.criterion;

import java.util.ArrayList;

import de.mpg.mpdl.inge.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;

/**
 * identifier criterion vo for the advanced search.
 * 
 * @created 15-Mai-2007 15:46:47
 * @author NiH
 * @version 1.0 Revised by NiH: 13.09.2007
 */
public class IdentifierCriterion extends Criterion {
  /**
   * constructor.
   */
  public IdentifierCriterion() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>();
    criterions.add(new MetadataSearchCriterion(MetadataSearchCriterion.CriterionType.IDENTIFIER,
        getSearchString()));
    criterions.add(new MetadataSearchCriterion(CriterionType.LATEST_RELEASE_OBJID,
        getSearchString(), MetadataSearchCriterion.LogicalOperator.OR));
    return criterions;
  }
}
