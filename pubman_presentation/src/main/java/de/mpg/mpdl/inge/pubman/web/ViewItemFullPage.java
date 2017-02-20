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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.appbase.BreadcrumbPage;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemSessionBean;

/**
 * Backing bean for ViewItemFullPage.jsp (for viewing items in a full context).
 * 
 * @author Tobias Schraut, created 03.09.2007
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class ViewItemFullPage extends BreadcrumbPage {
  private static final Logger logger = Logger.getLogger(ViewItemFullPage.class);

  // // The referring GUI Tool Page
  // public static final String GT_VIEW_ITEM_FULL_PAGE = "GTViewItemFullPage.jsp";

  /**
   * Public constructor.
   */
  public ViewItemFullPage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  @Override
  public void init() {
    // Perform initializations inherited from our superclass
    super.init();

    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    String userHandle = request.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);

    if (logger.isDebugEnabled()) {
      logger.debug("UserHandle: " + userHandle);
    }

    /*
     * LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class); if(loginHelper ==
     * null) { loginHelper = new LoginHelper(); //NBU: Moved inside loginHelper check as no needed
     * to re-insert Login once the user is logged-in try { loginHelper.insertLogin(); } catch
     * (Exception e) { logger.error("Could not login." + "\n" + e.toString()); } }
     */
  }

  // /**
  // * Redirets to the referring GUI Tool page.
  // *
  // * @return a navigation string
  // */
  // protected String redirectToGUITool() {
  // FacesContext fc = FacesContext.getCurrentInstance();
  // try {
  // this.getViewItemSessionBean().setHasBeenRedirected(true);
  // fc.getExternalContext().redirect(GT_VIEW_ITEM_FULL_PAGE);
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
    return true;
  }

}
