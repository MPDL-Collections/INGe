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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.common.xmltransforming;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.common.TestBase;
import test.common.xmltransforming.component.TransformAffiliationTest;
import test.common.xmltransforming.component.TransformExportFormatTest;
import test.common.xmltransforming.component.TransformFileUrlTest;
import test.common.xmltransforming.component.TransformInvalidPubItemTest;
import test.common.xmltransforming.component.TransformLockTest;
import test.common.xmltransforming.component.TransformParamTest;
import test.common.xmltransforming.component.TransformPubCollectionTest;
import test.common.xmltransforming.component.TransformPubItemResultTest;
import test.common.xmltransforming.component.TransformPubItemTest;
import test.common.xmltransforming.component.TransformPubItemVersionListTest;
import test.common.xmltransforming.component.TransformRelationTest;

/**
 * Component test suite for XmlTransforming.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TransformAffiliationTest.class,
                     TransformExportFormatTest.class,
                     TransformFileUrlTest.class,
                     TransformInvalidPubItemTest.class,
                     TransformLockTest.class,
                     TransformParamTest.class,
                     TransformPubCollectionTest.class,
                     TransformPubItemResultTest.class,
                     TransformPubItemTest.class,
                     TransformRelationTest.class,
                     TransformPubItemVersionListTest.class
                    })
public class XmlTransformingTest extends TestBase
{

}
