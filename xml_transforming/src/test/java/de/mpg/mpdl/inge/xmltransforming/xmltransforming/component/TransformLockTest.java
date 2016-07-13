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

package de.mpg.mpdl.inge.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.xmltransforming.TestBase;
import de.mpg.mpdl.inge.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.LockVO;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.XmlTransformingBean;

/**
 * Test class for {@link XmlTransforming} methods for LockVo transformation.
 * 
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
public class TransformLockTest extends TestBase {
  private static XmlTransforming xmlTransforming = new XmlTransformingBean();
  private Logger logger = Logger.getLogger(getClass());

  /**
   * Test for {@link XmlTransforming#transformToLockVO(String)}.
   * 
   * @throws TechnicalException
   * @throws Exception
   */
  @Ignore("Not implemenmted yet")
  @Test
  public void testTransformToLockVO() throws TechnicalException {
    logger.info("### testTransformToLockVO ###");
    LockVO lock = xmlTransforming.transformToLockVO("lockInformation xml");
    assertNotNull("Transforming of LockVO not implemented yet.", lock);
  }
}
