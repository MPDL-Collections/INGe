/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.mpdl.inge.cslmanager;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageManagerDefaultImpl;
import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageManagerInterface;
import de.mpg.mpdl.inge.cslmanager.CitationStyleLanguageUtils;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;

/**
 * Tests for {@link CitationStyleLanguageManagerInterface} implementations
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TestCitationStyleLanguageManager {
  private static final Logger logger = Logger.getLogger(TestCitationStyleLanguageManager.class);
  private static final String PATH_CITATION_STYLE = "citationStyleChicago.xml";
  private static final String PATH_ESCDOC_ITEM = "escidocItemXml.xml";
  private static final String EXPORT_FORMAT_NAME = "CSL";
  private String citationStyleXml = null;
  private String escidocItemXml = null;
  private ExportFormatVO exportFormat;

  /**
   * initializing TestCitationStyleLanguageManager before tests run
   * 
   * @throws Exception
   */
  @Before
  public void init() throws Exception {
    BasicConfigurator.configure();
    this.citationStyleXml =
        IOUtils.toString(TestCitationStyleLanguageManager.class.getClassLoader()
            .getResourceAsStream(PATH_CITATION_STYLE), "UTF-8");
    this.escidocItemXml =
        IOUtils.toString(TestCitationStyleLanguageManager.class.getClassLoader()
            .getResourceAsStream(PATH_ESCDOC_ITEM), "UTF-8");
    this.exportFormat = new ExportFormatVO(FormatType.LAYOUT, EXPORT_FORMAT_NAME, "html");
  }

  /**
   * automatically logging start and result of each Test
   */
  @Rule
  public TestWatcher testWatcher = new TestWatcher() {
    @Override
    protected void starting(Description descritption) {
      logger.info("\n--------------------\nStarting Test <" + descritption.getMethodName()
          + ">\n--------------------\n");
    }

    @Override
    protected void succeeded(Description descritption) {
      logger.info("\n--------------------\nTest <" + descritption.getMethodName()
          + "> succeeded\n--------------------\n");
    }

    @Override
    protected void failed(Throwable e, Description descritption) {
      logger.error("\n--------------------\nTest <" + descritption.getMethodName()
          + "> failed\n--------------------\n", e);
    }
  };

  /**
   * testing the result of {@link CitationStyleLanguageManagerDefaultImpl}
   * 
   * @throws Exception
   */
  @Test
  public void testDefaultImplementation() throws Exception {
    CitationStyleLanguageManagerInterface cslManager =
        new CitationStyleLanguageManagerDefaultImpl(this.citationStyleXml);
    String citationSnippet =
        IOUtils.toString(cslManager.getOutput(this.exportFormat, this.escidocItemXml), "UTF-8");
    assertEquals(
        "Walter, Matthias, Markus Haarländer, Franky S., - Testmann, G. Hoyden-Siedersleben, and J. C. Alonso. 2015. “CSL Test - Vortrag - Do Not Change!” Edited by Frank Demmig, Hideki ABE, Udo Stenzel, Shan Lu, Daniela Alic, Jana Wäldchen, and Collections, Max Planck Digital Library, Max Planck Gesellschaft. Translated by Martin Boosen. Directed by Michael Franke. <i>International Zoo Yearbook</i>. Habilitation Thesis presented at the EventTitel, EventOrt.",
        CitationStyleLanguageUtils.parseTagFromXml(citationSnippet, "bibliographicCitation",
            "http://purl.org/dc/terms/"));
  }
}
