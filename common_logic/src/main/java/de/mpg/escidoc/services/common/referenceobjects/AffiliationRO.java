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

package de.mpg.escidoc.services.common.referenceobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class for Affiliation references.
 * 
 * @revised by MuJ: 27.08.2007
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @updated 04-Sep-2007 11:43:18
 */
@JsonInclude(value = Include.NON_NULL)
public class AffiliationRO extends ReferenceObject {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */
  private static final long serialVersionUID = 1L;

  private String form;

  /**
   * Creates a new instance.
   */
  public AffiliationRO() {
    super();
  }

  /**
   * Creates a new instance with the given objectId.
   * 
   * @param objectId The id of the object.
   */
  public AffiliationRO(String objectId) {
    super(objectId);
  }

  /**
   * @return the form
   */
  public String getForm() {
    return form;
  }

  /**
   * @param form the form to set
   */
  public void setForm(String form) {
    this.form = form;
  }

  /**
   * 
   * @param href the xlink:href to be set
   */
  private void setHref(String href) {
    if (href == null) {
      return;
    }
    if (href.contains("/")) {
      href = href.substring(href.lastIndexOf("/") + 1);
    }
    this.setObjectId(href);
  }
}
