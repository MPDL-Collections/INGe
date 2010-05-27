/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.xmltransforming;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.TestBase;
import test.xmltransforming.component.TransformAffiliationTest;
import test.xmltransforming.component.TransformContainerTest;
import test.xmltransforming.component.TransformContextTest;
import test.xmltransforming.component.TransformExportFormatTest;
import test.xmltransforming.component.TransformFacesItemTest;
import test.xmltransforming.component.TransformGrantsTest;
import test.xmltransforming.component.TransformInvalidPubItemTest;
import test.xmltransforming.component.TransformLockTest;
import test.xmltransforming.component.TransformParamTest;
import test.xmltransforming.component.TransformPubItemTest;
import test.xmltransforming.component.TransformPubItemVersionListTest;
import test.xmltransforming.component.TransformRelationTest;
import test.xmltransforming.component.TransformSearchResultTest;
import test.xmltransforming.component.TransformStatisticReportTest;
import test.xmltransforming.component.TransformTocTest;

/**
 * Component test suite for XmlTransforming.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                     TransformAffiliationTest.class,
                     TransformContainerTest.class,
                     TransformExportFormatTest.class,
                     TransformInvalidPubItemTest.class,
                     TransformLockTest.class,
                     TransformGrantsTest.class,
                     TransformParamTest.class,
                     TransformContextTest.class,
                     TransformPubItemTest.class,
                     TransformRelationTest.class,
                     TransformPubItemVersionListTest.class,
                     TransformStatisticReportTest.class,
                     TransformTocTest.class,
                     TransformSearchResultTest.class,
                     TransformFacesItemTest.class
                    })
public class XmlTransformingTest extends TestBase
{

}
