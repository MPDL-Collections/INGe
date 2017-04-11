package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.inge_validation.exception.ConeException;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationConeCacheConfigException;
import de.mpg.mpdl.inge.inge_validation.util.Properties;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;

public class ConeCache {
  // Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse initialisiert wird
  private static final class InstanceHolder {
    // Die Initialisierung von Klassenvariablen geschieht nur einmal und wird vom ClassLoader
    // implizit synchronisiert
    static final ConeCache INSTANCE = new ConeCache();
  }

  private static final Logger LOG = Logger.getLogger(ConeCache.class);

  private static final String ISO639_3_IDENTIFIER_QUERY =
      "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String ISO639_3_TITLE_QUERY = "iso639-3/query?format=rdf&q=*&mode=full&n=0";
  private static final String DDC_TITLE_QUERY = "ddc/query?format=rdf&q=*&mode=full&n=0";
  private static final String MIME_TYPES_TITLE_QUERY =
      "escidocmimetypes/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIPKS_TITLE_QUERY = "mpipks/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIRG_TITLE_QUERY = "mpirg/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIS_GROUPS_TITLE_QUERY =
      "/mpis-groups/query?format=rdf&q=*&mode=full&n=0";
  private static final String MPIS_PROJECTS_TITLE_QUERY =
      "mpis-projects/query?format=rdf&q=*&mode=full&n=0";

  private static final String IDENTIFIER = "dc:identifier";
  private static final String TITLE = "dc:title";

  private final ConeSet iso639_3_Identifier = ConeSet.ISO639_3_IDENTIFIER;
  private final ConeSet iso639_3_Title = ConeSet.ISO639_3_TITLE;
  private final ConeSet ddcTitle = ConeSet.DDC_TITLE;
  private final ConeSet mimeTypesTitle = ConeSet.MIME_TYPES_TITLE;
  private final ConeSet mpipksTitle = ConeSet.MPIPKS_TITLE;
  private final ConeSet mpirgTitle = ConeSet.MPIRG_TITLE;
  private final ConeSet mpisGroupsTitle = ConeSet.MPIS_GROUPS_TITLE;
  private final ConeSet mpisProjectTitle = ConeSet.MPIS_PROJECTS_TITLE;

  private final String coneServiceUrl;

  private ConeCache() {
    this.coneServiceUrl = PropertyReader.getProperty(Properties.ESCIDOC_CONE_SERVICE_URL);
    ConeCache.LOG.info("ConeServiceUrl: " + this.coneServiceUrl);
    if (this.coneServiceUrl == null) {
      ConeCache.LOG.error("Property <" + Properties.ESCIDOC_CONE_SERVICE_URL + "> not set");
      throw new IllegalArgumentException();
    }

    try {
      ConeCache.LOG.info("Starting refresh of validation database <- Constructor.");
      this.refreshCache();
      ConeCache.LOG.info("Finished refresh of validation database <- Constructor.");
    } catch (final ValidationConeCacheConfigException e) {
      ConeCache.LOG.error(e);
      throw new IllegalStateException();
    }
  }

  public static ConeCache getInstance() {
    return ConeCache.InstanceHolder.INSTANCE;
  }

  public void refreshCache() throws ValidationConeCacheConfigException {
    ConeCache.LOG.info("*** Start Refresh-Cycle ***");
    this.refresh(this.iso639_3_Identifier, new ConeHandler(ConeCache.IDENTIFIER),
        this.coneServiceUrl + ConeCache.ISO639_3_IDENTIFIER_QUERY);
    this.refresh(this.iso639_3_Title, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.ISO639_3_TITLE_QUERY);
    this.refresh(this.ddcTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.DDC_TITLE_QUERY);
    this.refresh(this.mimeTypesTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.MIME_TYPES_TITLE_QUERY);
    this.refresh(this.mpipksTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.MPIPKS_TITLE_QUERY);
    this.refresh(this.mpirgTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.MPIRG_TITLE_QUERY);
    this.refresh(this.mpisGroupsTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.MPIS_GROUPS_TITLE_QUERY);
    this.refresh(this.mpisProjectTitle, new ConeHandler(ConeCache.TITLE), this.coneServiceUrl
        + ConeCache.MPIS_PROJECTS_TITLE_QUERY);
    ConeCache.LOG.info("*** Ende Refresh-Cycle ***");
  }

  private void refresh(ConeSet coneSet, ConeHandler handler, String queryUrl)
      throws ValidationConeCacheConfigException {
    ConeCache.LOG.info("*** Start refresh: " + queryUrl);
    try {
      final Set<String> result = this.fill(handler, queryUrl);
      if (0 == result.size()) {
        ConeCache.LOG.warn("    " + "Size: " + result.size() + " " + queryUrl);
      } else {
        ConeCache.LOG.info("    " + "Size: " + result.size() + " " + queryUrl);
      }
      if (!result.isEmpty()) {
        synchronized (coneSet.set()) {
          coneSet.set().clear();
          coneSet.set().addAll(result);
        }
      }
    } catch (IOException | ParserConfigurationException | SAXException | ConeException e) {
      ConeCache.LOG.warn("Could not refresh Cone Set with Url: " + queryUrl);
      if (coneSet.set().isEmpty()) {
        ConeCache.LOG.error("Cone Set is empty: Url: " + queryUrl);
        throw new ValidationConeCacheConfigException(e);
      }
    }
    ConeCache.LOG.info("*** Ende refresh: " + queryUrl);
  }

  private Set<String> fill(ConeHandler handler, String queryUrl)
      throws ParserConfigurationException, SAXException, ConeException, IOException {
    final HttpClient client = new HttpClient();
    final GetMethod method = new GetMethod(queryUrl);

    ProxyHelper.executeMethod(client, method);

    if (method.getStatusCode() == 200) {
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(method.getResponseBodyAsStream(), handler);
      return handler.getResult();
    } else {
      ConeCache.LOG.error("Could not load CONE attributes:" + method.getStatusCode());
      throw new ConeException("Could not load CONE attributes: " + method.getStatusCode());
    }
  }

  public Set<String> getDdcTitleSet() {
    if (this.ddcTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE ddcTitleSet is empty.");
    }

    return this.ddcTitle.set();
  }

  public Set<String> getIso639_3_IdentifierSet() {
    if (this.iso639_3_Identifier.set().isEmpty()) {
      ConeCache.LOG.error("CONE iso639_3_IdentifierSet is empty.");
    }

    return this.iso639_3_Identifier.set();
  }

  public Set<String> getIso639_3_TitleSet() {
    if (this.iso639_3_Title.set().isEmpty()) {
      ConeCache.LOG.error("CONE iso639_3_TitleSet is empty.");
    }

    return this.iso639_3_Title.set();
  }

  public Set<String> getMimeTypesTitleSet() {
    if (this.mimeTypesTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE mimeTypesTitleSet is empty.");
    }

    return this.mimeTypesTitle.set();
  }

  public Set<String> getMpipksTitleSet() {
    if (this.mpipksTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE mpipksTitleSet is empty.");
    }

    return this.mpipksTitle.set();
  }

  public Set<String> getMpirgTitleSet() {
    if (this.mpirgTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE mpirgTitleSet is empty.");
    }

    return this.mpirgTitle.set();
  }

  public Set<String> getMpisGroupsTitleSet() {
    if (this.mpisGroupsTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE mpisGroupsTitleSet is empty.");
    }

    return this.mpisGroupsTitle.set();
  }

  public Set<String> getMpisProjectTitleSet() {
    if (this.mpisProjectTitle.set().isEmpty()) {
      ConeCache.LOG.error("CONE mpisProjectTitleSet is empty.");
    }

    return this.mpisProjectTitle.set();
  }
}
