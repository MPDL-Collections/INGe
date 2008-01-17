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

package test.common.xmltransforming.component;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.PubItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.PubItemRefFilter;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO.State;

/**
 * Test class for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 613 $ $LastChangedDate: 2007-11-07 17:45:28 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformParamTest extends TestBase
{
    private static XmlTransforming xmlTransforming;
    private Logger logger = Logger.getLogger(getClass());

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        XMLUnit.setIgnoreWhitespace(true);
        xmlTransforming = (XmlTransforming) getService(XmlTransforming.SERVICE_NAME);
    }
    
    /**
     * Test for {@link XmlTransforming#transformToTaskParam(TaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToTaskParam() throws Exception
    {
        logger.info("### testTransformToTaskParam ###");
        String expectedXML = "<param last-modification-date=\"1967-08-06T12:34:56.000Z\"/>";

        GregorianCalendar cal = new GregorianCalendar(1967, Calendar.AUGUST, 06, 12, 34, 56);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        Date date = cal.getTime();
        TaskParamVO taskVO = new TaskParamVO(date);

        String xmlparam = xmlTransforming.transformToTaskParam(taskVO);
        logger.debug("TaskParam: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamCreatorAndState() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamCreatorAndState ###");
        String expectedXML = "<param>" + "<filter name=\"created-by\">escidoc:user1</filter>"
                + "<filter name=\"latest-version-status\">pending</filter>" + "</param>";

        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter f1 = filter.new OwnerFilter(new AccountUserRO("escidoc:user1"));
        Filter f2 = filter.new PubItemStatusFilter(State.PENDING);
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("OwnerFilter + PubItemStatusFilter: " + xmlparam + "\n" + "Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithState() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithState ###");
        String expectedXML = "<param>" + "   <filter name=\"latest-version-status\">pending</filter>"
                + "   <filter name=\"latest-version-status\">submitted</filter>" + "</param>";

        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter f1 = filter.new PubItemStatusFilter(State.PENDING);
        Filter f2 = filter.new PubItemStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(f1);
        filter.getFilterList().add(f2);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("PubItemStatusFilter + PubItemStatusFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithCreator() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithCreator ###");
        String expectedXML = "<param>" + "   <filter name=\"created-by\">escidoc:user1</filter>" + "</param>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();

        Filter filter = filterParam.new OwnerFilter(new AccountUserRO("escidoc:user1"));
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("OwnerFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithIdList() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithIdList ###");
        String expectedXML = "<param>" + "   <filter name=\"items\">" + "       <id>escidoc:1</id>"
                + "       <id>escidoc:2</id>" + "       <id>escidoc:3</id>" + "   </filter>" + "</param>";

        FilterTaskParamVO filter = new FilterTaskParamVO();
        PubItemRefFilter f1 = filter.new PubItemRefFilter();
        f1.getIdList().add(new PubItemRO("escidoc:1"));
        f1.getIdList().add(new PubItemRO("escidoc:2"));
        f1.getIdList().add(new PubItemRO("escidoc:3"));
        filter.getFilterList().add(f1);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filter);
        logger.debug("PubItemRefFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithRole() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithRole ###");
        String expectedXML = "<param>" + "   <filter name=\"role\">Depositor</filter>"
                + "   <filter name=\"user\">objectId4711</filter>" + "</param>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        Filter filter = filterParam.new RoleFilter("Depositor", new AccountUserRO("objectId4711"));
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("RoleFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

    /**
     * Test for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
     * 
     * @throws Exception
     */
    @Test
    public void old_transformToFilterTaskParamWithType() throws Exception
    {
        logger.info("### old_transformToFilterTaskParamWithType ###");
        String expectedXML = "<param>"
                + "   <filter name=\"content-type\">/ctm/content-type/escidoc:persistent4</filter>" + "</param>";

        FilterTaskParamVO filterParam = new FilterTaskParamVO();
        Filter filter = filterParam.new FrameworkItemTypeFilter("/ctm/content-type/escidoc:persistent4");
        filterParam.getFilterList().add(filter);

        String xmlparam = xmlTransforming.transformToFilterTaskParam(filterParam);
        logger.debug("FrameworkItemTypeFilter: " + xmlparam);
        logger.debug("Expected: " + expectedXML);

        Diff myDiff = new Diff(expectedXML, xmlparam);
        assertTrue("XML similar " + myDiff.toString(), myDiff.similar());
        assertTrue("XML identical " + myDiff.toString(), myDiff.identical());
    }

}
