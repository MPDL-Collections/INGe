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

package de.mpg.mpdl.inge.pubman.web.util;

/**
 * A Class for combining faces navigation strings(like 'loadDepsitorWS') and requested URLs (like
 * '/DepositorWSPage.jsp').
 * 
 * @author $Author$
 * @version $Revision$ $LastChangedDate$
 * @created 10-Jul-2007 16:55:03 Revised by ScT: 21.08.2007
 */
public class NavigationRule {
  // the URl to be requested
  private String requestURL;
  // the faces navigation string
  private String navigationString;

  public NavigationRule() {}

  /**
   * Public constructor with two parameters
   * 
   * @param requestedURL the URl to be requested
   * @param navString the faces navigation string
   */
  public NavigationRule(String requestedURL, String navString) {
    this.requestURL = requestedURL;
    this.navigationString = navString;
  }

  // Getters and Setters
  public String getNavigationString() {
    return this.navigationString;
  }

  public void setNavigationString(String navigationString) {
    this.navigationString = navigationString;
  }

  public String getRequestURL() {
    return this.requestURL;
  }

  public void setRequestURL(String requestURL) {
    this.requestURL = requestURL;
  }
}
