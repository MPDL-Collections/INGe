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

package test.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.common.datagathering.DataGatheringTest;
import test.common.emailhandling.EmailHandlingTest;
import test.common.encoding.EncodingTest;
import test.common.referenceobjects.ReferenceObjectTest;
import test.common.valueobjects.ValueObjectTest;
import test.common.valueobjects.comparator.ComparatorTest;
import test.common.xmltransforming.XmlTransformingTest;

/**
 * Component test suite for common_logic.
 *
 * @author Peter Broszeit (initial creation)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @revised by MuJ: 06.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DataGatheringTest.class
                    ,ReferenceObjectTest.class
                    ,ValueObjectTest.class                                        
                    ,ComparatorTest.class
                    ,XmlTransformingTest.class
                    ,EncodingTest.class
                    ,EmailHandlingTest.class
                    })
public class TestCommonLogic
{
}
