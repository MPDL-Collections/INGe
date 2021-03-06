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

package de.mpg.mpdl.inge.transformation.util.creators;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Description
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class BibTeXMixedSurnameAndGivennameFormat extends AuthorFormat {

  @Override
  public String getPattern() {
    return "";
  }

  @Override
  public List<Author> getAuthors(String authorsString) throws Exception {
    if (authorsString == null || !(authorsString.contains(" and ") && authorsString.contains(", "))) {
      return null;
    }
    String[] authors = authorsString.split(" +and +");
    List<String> surnameFirst = new ArrayList<String>();
    List<String> givennameFirst = new ArrayList<String>();
    for (String author : authors) {
      if (author.contains(", ")) {
        surnameFirst.add(author);
      } else {
        givennameFirst.add(author);
      }
    }

    List<Author> authorListSurnnameFirst = getAuthorListLeadingSurname(surnameFirst.toArray(new String[surnameFirst.size()]), ",");
    List<Author> authorListGivennameFirst = getAuthorListNormalFormat(givennameFirst.toArray(new String[givennameFirst.size()]));

    if (authorListSurnnameFirst == null || authorListGivennameFirst == null) {
      return null;
    }

    List<Author> result;
    result = authorListSurnnameFirst;
    result.addAll(authorListGivennameFirst);
    return result;
  }

  @Override
  public int getSignificance() {
    return 3;
  }

  @Override
  public String getDescription() {
    return "Vorname Nachname and Nachname, Vorname... (or vice versa)";
  }

  @Override
  public String getName() {
    return "BibTeXMixedSurnameAndGivennameFormat";
  }

  @Override
  public String getWarning() {
    return null;
  }

}
