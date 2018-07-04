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

package de.mpg.mpdl.inge.pubman.web.util.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.pubman.web.util.ServletTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.util.MatomoTracker;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * A servlet for retrieving and redirecting the content objects urls. /pubman/item/escidoc:12345 for
 * items and /pubman/item/escidoc:12345/component/escidoc:23456/name.txt for components.
 * 
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class RedirectServlet extends HttpServlet {
  private static final String INSTANCE_CONTEXT_PATH = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    final String id = req.getPathInfo().substring(1);
    final boolean download = ("download".equals(req.getParameter("mode")));
    final boolean tme = ("tme".equals(req.getParameter("mode")));

    final String userHandle = req.getParameter(LoginHelper.PARAMETERNAME_USERHANDLE);


    // no component -> ViewItemOverviewPage
    if (!id.contains("/component/")) {
      final StringBuffer redirectUrl = new StringBuffer();
      final LoginHelper loginHelper = (LoginHelper) ServletTools.findSessionBean(req, "LoginHelper");

      if (loginHelper != null && loginHelper.isDetailedMode()) {
        redirectUrl.append(RedirectServlet.INSTANCE_CONTEXT_PATH + "/faces/ViewItemFullPage.jsp?itemId=" + id);
      } else {
        redirectUrl.append(RedirectServlet.INSTANCE_CONTEXT_PATH + "/faces/ViewItemOverviewPage.jsp?itemId=" + id);
      }
      if (userHandle != null) {
        redirectUrl.append("&" + LoginHelper.PARAMETERNAME_USERHANDLE + "=" + userHandle);
      }
      resp.sendRedirect(redirectUrl.toString());
      return;
    }

    // is component
    if (id.contains("/component/")) {
      final String[] pieces = id.split("/");
      if (pieces.length != 4) {
        resp.sendError(404, "File not found");
      }

      final StringBuffer redirectUrl = new StringBuffer("/rest/items/");
      redirectUrl.append(pieces[0]);
      redirectUrl.append("/component/");
      redirectUrl.append(pieces[2]);

      // open component or download it
      if (req.getParameter("mode") == null || download) {
        redirectUrl.append("/content");
      }

      // view technical metadata
      if (tme) {
        redirectUrl.append("/metadata");
      }
      HashMap<String, String> matomoParameterMap = new HashMap<String, String>();
      matomoParameterMap.put(MatomoTracker.SITE_ID, PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_SITE_ID));
      matomoParameterMap.put(MatomoTracker.REC, Integer.toString(1)); // fixed value that needs to be sent as it is
      matomoParameterMap.put(MatomoTracker.SITE_URL,
          PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + req.getRequestURI());
      matomoParameterMap.put(MatomoTracker.AUTH_TOKEN, PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_AUTH_TOKEN));
      matomoParameterMap.put(MatomoTracker.USER_IP, req.getRemoteAddr());
      MatomoTracker.trackUrl(matomoParameterMap);

      resp.sendRedirect(redirectUrl.toString());
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // No post action
    return;
  }
}
