package de.mpg.escidoc.services.citationmanager.utils;

import java.util.regex.Pattern;

import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import net.sf.jasperreports.engine.util.JRStringUtil;

/*
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


/**
 * Utils class.
 * 
 * @author vmakarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class Utils {
  /**
   * Returns true if val is not null && not empty String
   * 
   * @param val
   * @return first not null && not empty String
   */
  public static boolean checkVal(String val) {
    return (val != null && !val.trim().equals(""));
  }

  /**
   * Returns true if val is not null && Length >0
   * 
   * @param val
   * @return first not null && Length >0
   */
  public static boolean checkLen(String val) {
    return (val != null && val.length() > 0);
  }

  /**
   * Throws ExportManagerException true if cond is true
   * 
   * @param cond
   * @param message
   * @throws ExportManagerException
   */
  public static void checkCondition(boolean cond, String message)
      throws CitationStyleManagerException {
    if (cond)
      throw new CitationStyleManagerException(message);
  }

  /*
   * public static void checkPcs(ProcessCitationStyles pcs) throws CitationStyleManagerException {
   * Utils.checkCondition(pcs==null, "Empty ProcessCitationStyles instance"); }
   */

  public static void checkName(String name) throws CitationStyleManagerException {
    Utils.checkCondition(!checkVal(name), "Empty name");
  }

  public static void checkName(String name, String message) throws CitationStyleManagerException {
    Utils.checkCondition(!checkVal(name), message);
  }

  /**
   * Paramaters' overwriting
   * 
   * @param p1 is high parameter
   * @param p2 is low parameter
   * @return An overwritten parameter
   */
  public static String overParams(String p1, String p2) {
    return coalesce(new String[] {p1, p2});
  }

  /**
   * Paramaters' overwriting
   * 
   * @param p1 is high parameter
   * @param p2 is low parameter
   * @return An overwritten parameter
   */
  public static int overParams(int p1, int p2) {
    return coalesceInt(new int[] {p1, p2});
  }

  /**
   * Returns first not null && not empty String from String[]
   * 
   * @param values
   * @return first not null && not empty String
   */
  public static String coalesce(String[] values) {
    for (String val : values)
      if (checkLen(val))
        return val;
    return null;
  }

  /**
   * Returns first not 0 int from int[]
   * 
   * @param values
   * @return first not 0 value
   */
  public static int coalesceInt(int[] values) {
    for (int val : values)
      if (val != 0)
        return val;
    return 0;
  }


  /**
   * Find <code>name</code> in <code>a</code> String[]
   * 
   * @return <code>true</code> if <code>name</code> has been found
   */
  public static boolean findInList(String[] a, String name) {
    for (String s : a) {
      if (s.equals(name))
        return true;
    }
    return false;
  }

  /**
   * @param str
   * @param count
   * @return
   */
  public static String xmlEncode(String str, int count) {
    if (str != null && str.length() > 0)
      for (int i = 1; i <= count; i++)
        str = JRStringUtil.xmlEncode(str);
    return str;
  }

  /**
   * @param str
   * @return
   */
  public static String xmlEncode(String str) {
    return xmlEncode(str, 1);
  }

  public static String replaceAllTotal(String what, String expr, String replacement) {
    return Pattern.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(what)
        .replaceAll(replacement);
  }

  public static String replaceAllTotal(String what, Pattern p, String replacement) {
    return p.matcher(what).replaceAll(replacement);
  }


  /**
   * Joins the elements of the provided array into a single String containing the provided list of
   * elements. Separator will be put between the not null/empty elements
   * 
   * @param arr is the list of the elements.
   * @param delimiter
   * @return joined string
   */
  public static String join(String[] arr, String delimiter) {
    if (arr == null || arr.length == 0)
      return null;
    StringBuffer sb = new StringBuffer();
    if (delimiter == null)
      delimiter = "";
    for (int i = 0, n = arr.length; i < n; i++) {
      if (arr[i] == null || arr[i].trim().equals(""))
        continue;
      sb.append(arr[i]);
      if (i < n - 1)
        sb.append(delimiter);
    }
    String str = sb.toString().replaceAll(Pattern.quote(delimiter) + "$", "");

    return str;
  }
}
