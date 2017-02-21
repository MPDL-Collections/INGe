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

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.appbase.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.desktop.Login;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemSessionBean;



/**
 * BackingBean for Workspaces Page (ReportWorkspacePage.jsp).
 * 
 */
@SuppressWarnings("serial")
public class ReportWorkspacePage extends BreadcrumbPage {
  private static Logger logger = Logger.getLogger(ReportWorkspacePage.class);
  public static final String BEAN_NAME = "ReportWorkspacePage";

  // The referring GUI Tool Page
  // public static final String GT_REPORT_WORKSPACE_PAGE = "GTReportWorkspacePage.jsp";

  /**
   * Public constructor.
   */
  public ReportWorkspacePage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    // Perform initializations inherited from our superclass
    super.init();

    checkLogin();
  }

  protected void checkLogin() {
    LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
    Login login = (Login) getSessionBean(Login.class);

    // if not logged in redirect to login page
    if (!loginHelper.isLoggedIn()) {
      try {
        login.loginLogout();
      } catch (Exception e) {
        logger.error("Error during redirection.", e);
        error("Could not redirect to login!");
      }

    }
  }

  // /**
  // * Redirets to the referring GUI Tool page.
  // *
  // * @return a navigation string
  // */
  // protected String redirectToGUITool() {
  // FacesContext fc = FacesContext.getCurrentInstance();
  // try {
  // fc.getExternalContext().redirect(GT_REPORT_WORKSPACE_PAGE);
  // } catch (IOException e) {
  // logger.error("Could not redirect to GUI Tool View item page." + "\n" + e.toString());
  // }
  // return "";
  // }


  /**
   * Returns the ViewItemSessionBean.
   * 
   * @return a reference to the scoped data bean (ViewItemSessionBean)
   */
  protected ViewItemSessionBean getViewItemSessionBean() {
    return (ViewItemSessionBean) getSessionBean(ViewItemSessionBean.class);
  }


  @Override
  public boolean isItemSpecific() {
    return false;
  }


}
