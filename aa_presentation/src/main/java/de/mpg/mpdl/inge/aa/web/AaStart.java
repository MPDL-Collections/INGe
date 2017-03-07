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

package de.mpg.mpdl.inge.aa.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.aa.Config;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class AaStart extends HttpServlet {

  private static final Pattern authPattern = Pattern.compile("(\\?|&)auth=[^&]*(&auth=[^&]*)*");
  private static final String DEFAULT_ENCODING = "UTF-8";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * This servlet is expecting 2 parameters:
   * 
   * - tan: A random string generated by the client system to check whether the result is valid or
   * not. - target: The URL the user should be redirected to after authentication.
   * 
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    request.setCharacterEncoding(DEFAULT_ENCODING);
    response.setCharacterEncoding(DEFAULT_ENCODING);

    String from = request.getParameter("from");
    String tan = request.getParameter("tan");

    // TODO: Make this generic
    String handle = request.getParameter("eSciDocUserHandle");

    Matcher matcher = authPattern.matcher(from);
    from = matcher.replaceAll("");

    String target = request.getParameter("target");

    if (target == null) {
      target = Config.getProperty("escidoc.aa.default.target");
    }

    String separator = "?";
    if (target.contains("?")) {
      separator = "&";
    }
    target +=
        separator + "target=" + URLEncoder.encode(from, "ISO-8859-1") + "&tan="
            + URLEncoder.encode(tan, "ISO-8859-1");

    if (handle != null) {
      target += "&eSciDocUserHandle=" + URLEncoder.encode(handle, "ISO-8859-1");
    }

    response.sendRedirect(target);

  }

}
