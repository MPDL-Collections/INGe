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
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur F�rderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.model.valueobjects.statistics;

import java.util.List;

import de.mpg.mpdl.inge.model.referenceobjects.AllowedRolesRO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * VO class representing report-definition
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class StatisticReportDefinitionVO extends ValueObject {

  private String objectId;

  private String sql;

  private String name;

  private String scopeId;

  private List<AllowedRolesRO> allowedRoles;

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getScopeID() {
    return scopeId;
  }

  public void setScopeID(String scopeID) {
    this.scopeId = scopeID;
  }

  public void setAllowedRoles(List<AllowedRolesRO> allowedRoles) {
    this.allowedRoles = allowedRoles;
  }

  public List<AllowedRolesRO> getAllowedRoles() {
    return allowedRoles;
  }

}
