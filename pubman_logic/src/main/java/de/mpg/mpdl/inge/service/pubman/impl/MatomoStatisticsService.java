package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.util.PropertyReader;

public class MatomoStatisticsService {

  private static final Logger logger = Logger.getLogger(MatomoStatisticsService.class);
  private static final String INSTANCE_URI = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
  private static final String LEGACY_INSTANCE_URI = "http://pubman.mpdl.mpg.de";
  private static final String INSTANCE_CONTEXT_PATH = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
  private static final String ANALYTICS_BASE_URI = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_BASE_URI);
  private static final String ANALYTICS_SITE_ID = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_SITE_ID);
  private static final String ANALYTICS_TOKEN = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_AUTH_TOKEN);

  private static final String PURE_OVERVIEW = INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/faces/ViewItemOverviewPage.jsp";
  private static final String PURE_FULLPAGE = INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/faces/ViewItemFullPage.jsp";
  private static final String LEGACY_OVERVIEW = LEGACY_INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/faces/viewItemOverviewPage.jsp";
  private static final String LEGACY_FULLPAGE = LEGACY_INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/faces/viewItemFullPage.jsp";

  private static final String PURE_ITEM = INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/item/";
  private static final String LEGACY_ITEM = LEGACY_INSTANCE_URI + INSTANCE_CONTEXT_PATH + "/item/";
  private static final String PURE_FILE = "/component/";
  private static final String DATE_RANGE = "2000-01-01,today";


  static ObjectMapper om = new ObjectMapper();


  public static Map<String, Integer> get124item(String id) throws Exception {
    Map<String, Integer> pageStatistics = new TreeMap<>();
    HttpResponse fullpage_response = Request.Get(prepareItemURL(id, "full", "month", "last12")).execute().returnResponse();
    JsonNode fullpage = om.readValue(fullpage_response.getEntity().getContent(), JsonNode.class);
    HttpResponse overview_response = Request.Get(prepareItemURL(id, "over", "month", "last12")).execute().returnResponse();
    JsonNode overview = om.readValue(overview_response.getEntity().getContent(), JsonNode.class);
    HttpResponse legacy_fullpage_response = Request.Get(prepareItemURL(id, "legacy_full", "month", "last12")).execute().returnResponse();
    JsonNode legacy_fullpage = om.readValue(legacy_fullpage_response.getEntity().getContent(), JsonNode.class);
    HttpResponse legacy_overview_response = Request.Get(prepareItemURL(id, "legacy_over", "month", "last12")).execute().returnResponse();
    JsonNode legacy_overview = om.readValue(legacy_overview_response.getEntity().getContent(), JsonNode.class);
    fullpage.fields().forEachRemaining(set -> {
      String key = set.getKey();
      int fullpage_visits = 0;
      int overview_visits = 0;
      int legacy_fullpage_visits = 0;
      int legacy_overview_visits = 0;

      if (legacy_fullpage.get(key).get(0) != null) {
        legacy_fullpage_visits = legacy_fullpage.get(key).get(0).get("nb_visits").asInt();
      }
      if (legacy_overview.get(key).get(0) != null) {
        legacy_overview_visits = legacy_overview.get(key).get(0).get("nb_visits").asInt();
      }
      if (overview.get(key).get(0) != null) {
        overview_visits = overview.get(key).get(0).get("nb_visits").asInt();
      }
      if (set.getValue().get(0) != null) {
        fullpage_visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      pageStatistics.put(key, legacy_fullpage_visits + legacy_overview_visits + overview_visits + fullpage_visits);
    });
    return pageStatistics;

  }

  public static Map<String, Integer> get124file(String id, String file_id, String name) throws Exception {
    Map<String, Integer> fileStatistics = new TreeMap<>();
    HttpResponse file_response = Request.Get(prepareFileURL(id, "pure", file_id, name, "month", "last12")).execute().returnResponse();
    JsonNode downloads = om.readValue(file_response.getEntity().getContent(), JsonNode.class);
    HttpResponse legacy_file_response =
        Request.Get(prepareFileURL(id, "legacy", file_id, name, "month", "last12")).execute().returnResponse();
    JsonNode legacy_downloads = om.readValue(legacy_file_response.getEntity().getContent(), JsonNode.class);
    downloads.fields().forEachRemaining(set -> {
      String key = set.getKey();
      int legacy_visits = 0;
      int visits = 0;
      if (legacy_downloads.get(key).get(0) != null) {
        legacy_visits = legacy_downloads.get(key).get(0).get("nb_visits").asInt();
      }
      if (set.getValue().get(0) != null) {
        visits = set.getValue().get(0).get("nb_visits").asInt();
      }

      fileStatistics.put(key, legacy_visits + visits);
    });
    return fileStatistics;
  }

  public static int getTotal4Item(String id) throws Exception {
    HttpResponse full_response = Request.Get(prepareItemURL(id, "full", "range", DATE_RANGE)).execute().returnResponse();
    HttpResponse over_response = Request.Get(prepareItemURL(id, "over", "range", DATE_RANGE)).execute().returnResponse();
    HttpResponse legacy_full_response = Request.Get(prepareItemURL(id, "legacy_full", "range", DATE_RANGE)).execute().returnResponse();
    HttpResponse legacy_over_response = Request.Get(prepareItemURL(id, "legacy_over", "range", DATE_RANGE)).execute().returnResponse();

    JsonNode fullpage = om.readValue(full_response.getEntity().getContent(), JsonNode.class);
    JsonNode overview = om.readValue(over_response.getEntity().getContent(), JsonNode.class);
    JsonNode legacy_fullpage = om.readValue(legacy_full_response.getEntity().getContent(), JsonNode.class);
    JsonNode legacy_overview = om.readValue(legacy_over_response.getEntity().getContent(), JsonNode.class);
    int total = 0, full = 0, over = 0, legacy_full = 0, legacy_over = 0;
    if (fullpage.get(0) != null) {
      full = fullpage.get(0).get("nb_visits").asInt();
    }
    if (overview.get(0) != null) {
      over = overview.get(0).get("nb_visits").asInt();
    }
    if (legacy_fullpage.get(0) != null) {
      legacy_full = legacy_fullpage.get(0).get("nb_visits").asInt();
    }
    if (legacy_overview.get(0) != null) {
      legacy_over = legacy_overview.get(0).get("nb_visits").asInt();
    }
    total = full + over + legacy_full + legacy_over;

    return total;

  }

  public static int getTotal4File(String id, String file_id, String name) throws IOException {
    HttpResponse file_response = Request.Get(prepareFileURL(id, "pure", file_id, name, "range", DATE_RANGE)).execute().returnResponse();
    JsonNode file_downloads = om.readValue(file_response.getEntity().getContent(), JsonNode.class);
    HttpResponse legacy_file_response =
        Request.Get(prepareFileURL(id, "legacy", file_id, name, "range", DATE_RANGE)).execute().returnResponse();
    JsonNode legacy_file_downloads = om.readValue(legacy_file_response.getEntity().getContent(), JsonNode.class);
    int total = 0;
    int legacy_total = 0;
    if (file_downloads.get(0) != null) {
      total = file_downloads.get(0).get("nb_visits").asInt();
    }
    if (legacy_file_downloads.get(0) != null) {
      legacy_total = legacy_file_downloads.get(0).get("nb_visits").asInt();
    }
    return legacy_total + total;
  }

  private static URI prepareItemURL(String id, String what, String period, String date) {
    URIBuilder builder;
    String legacy_id;
    try {
      builder = new URIBuilder(new URI(ANALYTICS_BASE_URI));
      switch (what) {
        case "legacy_full":
          legacy_id = id.replace("item", "escidoc").replaceAll("_", ":");
          builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", ANALYTICS_SITE_ID)
              .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
              .addParameter("format", "json").addParameter("showColumns", "nb_visits");
          builder.addParameter("pageUrl", LEGACY_FULLPAGE + "?itemId=" + legacy_id);
          break;
        case "legacy_over":
          legacy_id = id.replace("item", "escidoc").replaceAll("_", ":");
          builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", ANALYTICS_SITE_ID)
              .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
              .addParameter("format", "json").addParameter("showColumns", "nb_visits");
          builder.addParameter("pageUrl", LEGACY_OVERVIEW + "?itemId=" + legacy_id);
          break;
        case "full":
          builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", ANALYTICS_SITE_ID)
              .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
              .addParameter("format", "json").addParameter("showColumns", "nb_visits");
          builder.addParameter("pageUrl", PURE_FULLPAGE + "?itemId=" + id);
          break;
        case "over":
          builder.addParameter("module", "API").addParameter("method", "Actions.getPageUrl").addParameter("idSite", ANALYTICS_SITE_ID)
              .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
              .addParameter("format", "json").addParameter("showColumns", "nb_visits");
          builder.addParameter("pageUrl", PURE_OVERVIEW + "?itemId=" + id);
          break;
      }

      URI analytics_URI = builder.build();
      //      System.out.println("Requesting item stats 4 " + analytics_URI.toString());
      return analytics_URI;

    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  private static URI prepareFileURL(String id, String what, String file_id, String name, String period, String date) {
    URIBuilder builder;
    String legacy_id, legacy_file_id;
    try {
      builder = new URIBuilder(new URI(ANALYTICS_BASE_URI));
      if (what.equalsIgnoreCase("legacy")) {
        legacy_id = id.replace("item", "escidoc").replaceAll("_", ":");
        legacy_file_id = file_id.replace("file", "escidoc").replaceAll("_", ":");
        builder.addParameter("module", "API").addParameter("method", "Actions.getDownload").addParameter("idSite", ANALYTICS_SITE_ID)
            .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
            .addParameter("format", "json").addParameter("showColumns", "nb_visits");
        builder.addParameter("downloadUrl", LEGACY_ITEM + legacy_id + PURE_FILE + legacy_file_id + "/" + name);
      } else {
        builder.addParameter("module", "API").addParameter("method", "Actions.getDownload").addParameter("idSite", ANALYTICS_SITE_ID)
            .addParameter("period", period).addParameter("date", date).addParameter("token_auth", ANALYTICS_TOKEN)
            .addParameter("format", "json").addParameter("showColumns", "nb_visits");
        builder.addParameter("downloadUrl", PURE_ITEM + id + PURE_FILE + file_id + "/" + name);
      }

      URI analytics_URI = builder.build();
      //      System.out.println("Requesting file stats 4 " + analytics_URI.toString());
      return analytics_URI;
    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfItemOrFileRequests(String objectId) throws Exception {
    int requests = 0;
    // objectId = objectId.replaceAll("_", ":");
    // objectId = objectId.replace("item", "escidoc");
    requests = getTotal4Item(objectId);

    return String.valueOf(requests);
  }

  /**
   * {@inheritDoc}
   */
  public static String getNumberOfFileDownloads(String objectId, String file_id, String name) throws Exception {
    int requests = 0;
    // objectId = objectId.replaceAll("_", ":");
    // objectId = objectId.replace("item", "escidoc");
    // file_id = file_id.replaceAll("_", ":");
    // file_id = file_id.replace("file", "escidoc");
    requests = getTotal4File(objectId, file_id, name);

    return String.valueOf(requests);
  }

}
