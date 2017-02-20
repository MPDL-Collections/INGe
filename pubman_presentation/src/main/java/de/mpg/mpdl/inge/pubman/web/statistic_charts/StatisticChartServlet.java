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
package de.mpg.mpdl.inge.pubman.web.statistic_charts;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.encoders.KeypointPNGEncoderAdapter;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.mpdl.inge.pubman.PubItemSimpleStatistics;
import de.mpg.mpdl.inge.pubman.statistics.SimpleStatistics;

/**
 * 
 * Servlet that delivers image files in PNG format which incorporate statistic charts built from
 * data from eSciDoc statistic service.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class StatisticChartServlet extends HttpServlet {

  private static final String CONTENT_TYPE = "image/png";

  private static final String numberOfMonthsParameterName = "months";

  private static final String idParameterName = "id";

  private static final String typeParameterName = "type";

  private static final String languageParameterName = "lang";

  private String id;

  private int numberOfMonths;

  private String type;

  private String language;

  @EJB
  private PubItemSimpleStatistics pubItemStatistic;

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  public synchronized void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String numberOfMonthsString = request.getParameter(numberOfMonthsParameterName);
    if (numberOfMonthsString == null) {
      numberOfMonths = 12;
    } else {
      numberOfMonths = Integer.parseInt(numberOfMonthsString);
    }

    String lang = request.getParameter(languageParameterName);
    if (lang == null) {
      this.language = "en";
    } else {
      this.language = lang;
    }


    id = request.getParameter(idParameterName);
    type = request.getParameter(typeParameterName);

    try {

      CategoryDataset dataset = createDataset();
      JFreeChart chart = createChart(dataset);
      BufferedImage img = chart.createBufferedImage(630, 250);
      byte[] image = new KeypointPNGEncoderAdapter().encode(img);

      response.setContentType(CONTENT_TYPE);
      ServletOutputStream out = response.getOutputStream();
      out.write(image);
      out.flush();
      out.close();


    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }


  }


  /**
   * Retrieves statistic data from the framework and creates the dataset for the visualisation and
   * the given months. If there was no request in a month, a dataset with value "0" is added.
   * 
   * @return The dataset.
   */
  private CategoryDataset createDataset() throws Exception {

    // create the dataset...



    // retrieve the statistic reports
    List<StatisticReportRecordVO> reportListAllUsers = new ArrayList<StatisticReportRecordVO>();
    List<StatisticReportRecordVO> reportListAnonymousUsers =
        new ArrayList<StatisticReportRecordVO>();

    if (type.equals("item")) {
      reportListAllUsers =
          pubItemStatistic.getStatisticReportRecord(
              SimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ALL_USERS, id, null);
      reportListAnonymousUsers =
          pubItemStatistic.getStatisticReportRecord(
              SimpleStatistics.REPORTDEFINITION_NUMBER_OF_ITEM_RETRIEVALS_ANONYMOUS, id, null);
    } else if (type.equals("file")) {
      reportListAllUsers =
          pubItemStatistic.getStatisticReportRecord(
              SimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS, id, null);
      reportListAnonymousUsers =
          pubItemStatistic.getStatisticReportRecord(
              SimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, id, null);
    }



    // Organize report records in map with month/year as key
    Map<String, StatisticReportRecordVOPresentation> mapAllUserRequests =
        new HashMap<String, StatisticReportRecordVOPresentation>();
    for (StatisticReportRecordVO reportRec : reportListAllUsers) {
      // sortingListAllUsers.add(new StatisticReportRecordVOPresentation(reportRec));
      StatisticReportRecordVOPresentation repRecPres =
          new StatisticReportRecordVOPresentation(reportRec);
      mapAllUserRequests.put(repRecPres.getMonth() + "/" + repRecPres.getYear(), repRecPres);

    }

    Map<String, StatisticReportRecordVOPresentation> mapAnonymousUserRequests =
        new HashMap<String, StatisticReportRecordVOPresentation>();
    for (StatisticReportRecordVO reportRec : reportListAnonymousUsers) {

      StatisticReportRecordVOPresentation repRecPres =
          new StatisticReportRecordVOPresentation(reportRec);
      mapAnonymousUserRequests.put(repRecPres.getMonth() + "/" + repRecPres.getYear(), repRecPres);

    }



    // Create the dataset with 2 series for anonmyous and logged-in users.
    String loggedInUsersSeries = "Logged-in Users";
    String anonymousUsersSeries = "Anonymous Users";

    if (this.language.equals("de")) {
      loggedInUsersSeries = "Eingeloggte Nutzer";
      anonymousUsersSeries = "Anonyme Nutzer";
    }


    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -(numberOfMonths - 1));


    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (int i = 0; i < numberOfMonths; i++) {
      String xLabel = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);
      int allUserRequests = 0;
      int anonymousUserrequests = 0;

      if (mapAllUserRequests.get(xLabel) != null) {
        allUserRequests = mapAllUserRequests.get(xLabel).getRequests();
      }

      if (mapAnonymousUserRequests.get(xLabel) != null) {
        anonymousUserrequests = mapAnonymousUserRequests.get(xLabel).getRequests();
      }


      dataset.addValue(allUserRequests - anonymousUserrequests, loggedInUsersSeries, xLabel);
      // logger.info("added value " + (allUserRequests-anonymousUserrequests) + " for " +
      // loggedInUsersSeries);
      dataset.addValue(anonymousUserrequests, anonymousUsersSeries, xLabel);
      // logger.info("added value " + (anonymousUserrequests) + " for " + anonymousUsersSeries);

      cal.add(Calendar.MONTH, +1);
    }



    return dataset;

  }


  /**
   * Creates the statistic chart.
   * 
   * @param dataset the dataset.
   * 
   * @return The chart.
   */
  private JFreeChart createChart(CategoryDataset dataset) {


    // create the chart
    JFreeChart chart = ChartFactory.createStackedBarChart(null, // chart title
        "", // domain axis label
        "", // range axis label
        dataset, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        false, // tooltips?
        false // URLs?
        );


    // set the background color for the chart
    chart.setBackgroundPaint(Color.white);


    // get a reference to the plot for further customisation
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(new Color(0xf5, 0xf5, 0xf5));
    plot.setDomainGridlinePaint(Color.gray);
    plot.setDomainGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.gray);


    // set the range axis to display integers only
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setLowerBound(0);
    // disable bar outlines...
    StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());
    renderer.setDrawBarOutline(false);

    // set up gradient paints for series
    /*
     * GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0,
     * 64)); GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, new Color(64,
     * 0, 0));
     */
    Color series1Color = new Color(0xfa, 0x80, 0x72);
    Color series2Color = new Color(0x64, 0x95, 0xed);
    renderer.setSeriesPaint(1, series1Color);
    renderer.setSeriesPaint(0, series2Color);

    // remove shadow
    renderer.setShadowVisible(false);
    //


    // Labels in bars
    /*
     * renderer.setSeriesItemLabelsVisible(0, true); renderer.setSeriesItemLabelGenerator(0, new
     * StandardCategoryItemLabelGenerator()); renderer.setSeriesItemLabelPaint(0, Color.white);
     * renderer.setSeriesItemLabelsVisible(1, true); renderer.setSeriesItemLabelGenerator(1, new
     * StandardCategoryItemLabelGenerator()); renderer.setSeriesItemLabelPaint(1, Color.white);
     */

    // setCategorySummary(dataset);


    // rotate labels on x-axis
    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions
        .createUpRotationLabelPositions(Math.PI / 6.0));

    return chart;


  }



}
