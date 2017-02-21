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

import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;

/**
 * BackingBean for EditItemPage.jsp. This one is empty because all code is implemented in the
 * BackingBean of the fragment.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 14.08.2007
 */
@SuppressWarnings("serial")
public class EditItemPage extends FacesBean {
  public static final String BEAN_NAME = "EditItemPage";

  // private static final Logger logger = Logger.getLogger(EditItemPage.class);

  // The referring GUI Tool Page
  // public final static String GT_EDIT_ITEM_PAGE = "GTEditItemPage.jsp";

  /**
   * Public constructor.
   */
  public EditItemPage() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    // Perform initializations inherited from our superclass
    // super.init();
    checkForLogin();
    // redirect to the referring GUI Tool page if the application has been started as GUI Tool
  }

  // /**
  // * Redirects to the referring GUI Tool page.
  // *
  // * @author Tobias Schraut
  // * @return a navigation string
  // */
  // protected String redirectToGUITool() {
  // FacesContext fc = FacesContext.getCurrentInstance();
  // try {
  // fc.getExternalContext().redirect(GT_EDIT_ITEM_PAGE);
  // } catch (IOException e) {
  // logger.error("Could not redirect to GUI Tool Search result list page." + "\n" + e.toString());
  // }
  // return "";
  // }

  /*
   * @Override protected Method getDefaultAction() throws NoSuchMethodException { return
   * Navigation.class.getMethod("newSubmission", null); }
   * 
   * @Override public boolean isItemSpecific() { return true; }
   */
}
