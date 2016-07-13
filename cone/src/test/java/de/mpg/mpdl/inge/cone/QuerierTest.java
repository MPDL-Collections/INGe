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

package de.mpg.mpdl.inge.cone;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mpg.mpdl.inge.cone.Querier;
import de.mpg.mpdl.inge.cone.Querier.ModeType;
import de.mpg.mpdl.inge.cone.QuerierFactory;
import de.mpg.mpdl.inge.cone.util.Describable;
import de.mpg.mpdl.inge.cone.util.Pair;
import de.mpg.mpdl.inge.cone.util.TreeFragment;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Test class to check Querier implementation
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class QuerierTest {
  private static final Logger logger = Logger.getLogger(QuerierTest.class);

  private Querier querier;

  /**
   * Initialize the querier before each test.
   */
  @Before
  public void getQuerier(boolean loggedIn) {
    querier = QuerierFactory.newQuerier(loggedIn);
  }

  /**
   * Initialize the querier before each test.
   */
  @After
  public void releaseQuerier() throws Exception {
    querier.release();
  }

  @Test
  public void testAllIdsMethod() throws Exception {
    List<String> ids = querier.getAllIds("journals");
    assertNotNull("No resources for model 'journals'", ids);
    assertTrue("No resources for model 'journals'", ids.size() > 0);
  }

  @Test
  public void testQueryMethod1() throws Exception {
    List<? extends Describable> results = querier.query("journals", "of", ModeType.FAST);
    assertNotNull("No results for query 'of'", results);
    assertTrue("No results for query 'of'", results.size() > 0);
    assertTrue(
        "Retrieved more results than allowed (" + results.size() + " > "
            + Integer.parseInt(PropertyReader.getProperty("escidoc.cone.maximum.results")) + ")",
        results.size() <= Integer.parseInt(PropertyReader
            .getProperty("escidoc.cone.maximum.results")));

    logger.debug("Query returned " + results.size() + " hits");

    for (Describable pair : results) {
      assertTrue("Result does not contain query string 'of': " + ((Pair) pair).getValue(),
          ((Pair) pair).getValue().toString().contains("of"));
    }
  }

  @Test
  public void testQueryMethod2() throws Exception {
    List<? extends Describable> results = querier.query("journals", "of", null, ModeType.FAST, 10);
    assertNotNull("No results for query 'of'", results);
    assertTrue("No results for query 'of'", results.size() > 0);
    assertTrue("Retrieved more results than allowed (" + results.size() + " > 10)",
        results.size() <= 10);

    logger.debug("Query returned " + results.size() + " hits");

    for (Describable pair : results) {
      assertTrue("Result does not contain query string 'of': " + ((Pair) pair).getValue(),
          ((Pair) pair).getValue().toString().contains("of"));
    }
  }

  @Test
  public void testDetailMethod() throws Exception {
    List<String> ids = querier.getAllIds("journals");
    TreeFragment result = querier.details("journals", ids.get(0));

    assertNotNull("No details for '" + ids.get(0) + "'", result);
    assertNotNull("No id set", result.getSubject());
    assertNotNull("No title found", result.get("http://purl.org/dc/elements/1.1/title"));
  }
}
