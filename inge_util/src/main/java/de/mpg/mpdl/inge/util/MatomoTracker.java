package de.mpg.mpdl.inge.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

/**
 * Tracking direct calls to REST services in Matomo
 * 
 * @author walter
 *
 */
public class MatomoTracker {

  private static final Logger logger = Logger.getLogger(MatomoTracker.class);
  private static final String HOST_URL = PropertyReader.getProperty(PropertyReader.INGE_MATOMO_ANALYTICS_BASE_URI) + "/piwik.php";

  public final static String AUTH_TOKEN = "token_auth";
  public final static String REC = "rec";
  public final static String SITE_ID = "idsite";
  public final static String SITE_URL = "url";
  public final static String DOWNLOAD_URL = "download";
  public final static String USER_IP = "cip";
  public final static String User_ID = "_id";
  public final static int TIMEOUT = 1; // timeout in sedc

  public MatomoTracker() {

  }

  /**
   * trackUrl tracks a call to a REST service in Matomo
   * 
   * @param parameterMap - you can add all parameters Matomo offers
   *        (https://developer.matomo.org/api-reference/tracking-api) necessary parameters are:
   *        token_auth, rec, idsite, url, cip
   */
  public static void trackUrl(HashMap<String, String> parameterMap) {

    RequestConfig config = RequestConfig.custom().setConnectTimeout(TIMEOUT * 1000).setConnectionRequestTimeout(TIMEOUT * 1000)
        .setSocketTimeout(TIMEOUT * 1000).build();
    CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    HttpGet getMethod = new HttpGet(HOST_URL + getUrlEncodedQueryString(parameterMap));
    try {
      try {
        client.execute(getMethod);
      } catch (IOException e) {
        logger.error("Could not execute statistics call", e);
      }
    } finally {
      getMethod.releaseConnection();
    }
  }


  /**
   * transforms a parameter HashMap into a URL encoded query String
   * 
   * @param parameterMap
   * @return a query String which is URL encoded
   */
  private static String getUrlEncodedQueryString(HashMap<String, String> parameterMap) {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, String> parameter : parameterMap.entrySet()) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      try {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(parameter.getKey());
        sb2.append("=");
        sb2.append(URLEncoder.encode(parameter.getValue().toString(), StandardCharsets.UTF_8.toString()));
        sb.append(sb2);
      } catch (UnsupportedEncodingException e) {
        System.err.println(e.getMessage());
      }
    }

    return sb.toString();
  }
}
