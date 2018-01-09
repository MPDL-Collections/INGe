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

package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.utils.XsltHelper;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.xmltransforming.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManStylesheetNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.exceptions.PubManVersionNotAvailableException;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.CreatorVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.SourceVOPresentation;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * ApplicationBean which stores all application wide values.
 * 
 * @author: Thomas Diebäcker, created 09.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by DiT: 09.08.2007
 */
@ManagedBean(name = "ApplicationBean")
@ApplicationScoped
@SuppressWarnings("serial")
public class ApplicationBean extends FacesBean {

  /** system type enum */
  public enum SystemType
  {
    /** profile for developer machines */
    Workstation,
    /** profile for the development test server */
    Dev_Server,
    /** profile for the demo server */
    Test_Server,
    /** profile for the qa server */
    QA_Server,
    /** profile for the production server */
    Production_Server
  }

  private static final Logger logger = Logger.getLogger(ApplicationBean.class);

  private static final String PROPERTY_FILENAME = "solution.properties";
  private static final String ALTERNATE_STYLESHEET = "alternate stylesheet";

  public static ApplicationBean INSTANCE;

  private Map<String, SelectItem[]> languageSelectItems;
  private Map<String, String> contentCategoryMap;
  private Map<String, String> creatorRoleMap;
  private Map<String, String> excludedSourceGenreMap;

  private Set<AffiliationDbVO> ouList = new HashSet<AffiliationDbVO>();

  private String additionalLogoCss;
  private String appContext = "";
  private String appTitle = null;
  private String commonPresentationUrl;
  private String cookieVersion;
  private String cslEditorInstanceUrl;
  private String footerSnippet;
  private String instanceContextPath;
  private String logoUrl;
  private String pubmanBlogFeedUrl;
  private String pubmanInstanceUrl;
  private String pubmanStyleTags;
  private String shortVersion;
  private String version = null;

  private boolean handlesActivated;

  @ManagedProperty("#{contextServiceDbImpl}")
  private ContextService contextService;

  @ManagedProperty("#{restDataSource}")
  private DataSource dataSource;

  @ManagedProperty("#{fileServiceFSImpl}")
  private FileService fileService;

  @ManagedProperty("#{itemValidatingService}")
  private ItemValidatingService itemValidatingService;

  @ManagedProperty("#{organizationServiceDbImpl}")
  private OrganizationService organizationService;

  @ManagedProperty("#{pubItemServiceDbImpl}")
  private PubItemService pubItemService;

  @ManagedProperty("#{userAccountServiceImpl}")
  private UserAccountService userAccountService;

  @ManagedProperty("#{yearbookServiceDbImpl}")
  private YearbookService yearbookService;

  public ApplicationBean() {
    this.languageSelectItems = new HashMap<String, SelectItem[]>();
    this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
    this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
    this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();

    ApplicationBean.INSTANCE = this;

    this.loadProperties();
  }

  /**
   * Provides the URLs of the pubman stylsheets.
   * 
   * @return the escidoc instance
   */
  private String buildPubmanStyleTags() throws PubManStylesheetNotAvailableException {
    final StringBuffer styleTags = new StringBuffer();
    String StylesheetStandard = "";
    String StylesheetContrast = "";
    String StylesheetClassic = "";
    String StylesheetSpecial = "";

    // First append the standard PubMan Stylesheet
    try {
      if ("true".equals(PropertyReader.getProperty("inge.pubman.stylesheet.standard.apply"))) {
        if (PropertyReader.getProperty("inge.pubman.stylesheet.standard.type").equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
          styleTags.append("<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.standard.url")
              + "\" id=\"Standard\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.standard.type") + "\"/>");
        } else {
          StylesheetStandard = "<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.standard.url")
              + "\" id=\"Standard\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblPubMan") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.standard.type") + "\"/>";
        }
      }

      // Then append the high contrast Stylesheet

      if ("true".equals(PropertyReader.getProperty("inge.pubman.stylesheet.contrast.apply"))) {
        if (PropertyReader.getProperty("inge.pubman.stylesheet.contrast.type").equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
          styleTags.append("<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.contrast.url")
              + "\" id=\"HighContrast\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast")
              + "\" rel=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.contrast.type") + "\"/>");
        } else {
          StylesheetContrast = "<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.contrast.url")
              + "\" id=\"HighContrast\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblHighContrast")
              + "\" rel=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.contrast.type") + "\"/>";
        }
      }

      // Then append the classic Stylesheet

      if ("true".equals(PropertyReader.getProperty("inge.pubman.stylesheet.classic.apply"))) {
        if (PropertyReader.getProperty("inge.pubman.stylesheet.classic.type").equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
          styleTags.append("<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.classic.url")
              + "\" id=\"Classic\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.classic.type") + "\"/>");
        } else {
          StylesheetClassic = "<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.classic.url")
              + "\" id=\"Classic\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblClassic") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.classic.type") + "\"/>";
        }
      }

      // Then append the special Stylesheet

      if ("true".equals(PropertyReader.getProperty("inge.pubman.stylesheet.special.apply"))) {
        if (PropertyReader.getProperty("inge.pubman.stylesheet.special.type").equals(ApplicationBean.ALTERNATE_STYLESHEET)) {
          styleTags.append("<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.special.url")
              + "\" id=\"Special\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.special.type") + "\"/>");
        } else {
          StylesheetSpecial = "<link href=\"" + PropertyReader.getProperty("inge.pubman.stylesheet.special.url")
              + "\" id=\"Special\" type=\"text/css\" title=\""
              + ResourceBundle.getBundle(InternationalizationHelper.LABEL_BUNDLE + "_en").getString("styleTheme_lblSpecial") + "\" rel=\""
              + PropertyReader.getProperty("inge.pubman.stylesheet.special.type") + "\"/>";
        }
      }
    } catch (final Exception e) {
      throw new PubManStylesheetNotAvailableException(e);
    }

    // then append the stylesheet String variables (no matter if empty) to ensure that the
    // stylesheet with the standard rel tag is the last entry in the list.

    styleTags.append(StylesheetStandard);
    styleTags.append(StylesheetContrast);
    styleTags.append(StylesheetClassic);
    styleTags.append(StylesheetSpecial);

    // Last Step: add Favicon information if it should be applied

    if ("true".equals(PropertyReader.getProperty("inge.pubman.favicon.apply"))) {
      styleTags.append(
          "<link rel=\"shortcut icon\" type=\"image/png\" href=\"" + PropertyReader.getProperty("inge.pubman.favicon.url") + "\"/>");
    }

    return styleTags.toString();
  }

  /**
   * Provides the escidoc instance string.
   * 
   * @return the escidoc instance
   * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
   */
  private SystemType fetchSystemTypeFromProperty() throws PubManVersionNotAvailableException {
    final String sysType = PropertyReader.getProperty("inge.systemtype");

    if (sysType.equals("workstation")) {
      return SystemType.Workstation;
    } else if (sysType.equals("dev")) {
      return SystemType.Dev_Server;
    } else if (sysType.equals("qa")) {
      return SystemType.QA_Server;
    } else if (sysType.equals("test")) {
      return SystemType.Test_Server;
    } else if (sysType.equals("production")) {
      return SystemType.Production_Server;
    } else {
      throw new PubManVersionNotAvailableException("SystemType Property unsupported!");
    }
  }

  public String getAdditionalLogoCss() {
    return this.additionalLogoCss;
  }

  public String getAppContext() {
    return this.appContext;
  }

  /**
   * Returns the title and version of the application, shown in the header.
   * 
   * @return applicationtitle, including version
   */
  public String getAppTitle() {
    return this.appTitle;
  }

  public synchronized Map<String, String> getContentCategoryMap() {
    if (this.contentCategoryMap == null) {
      this.contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
    }

    return this.contentCategoryMap;
  }

  public ContextService getContextService() {
    return this.contextService;
  }

  /**
   * This method returns the cookie version for PubMan hold in the pubman.properties
   * 
   * @return String cookie version for PubMan
   * @throws PubManVersionNotAvailableException
   */
  public String getCookieVersion() throws PubManVersionNotAvailableException {
    return this.cookieVersion;
  }

  public synchronized Map<String, String> getCreatorRoleMap() {
    if (this.creatorRoleMap == null) {
      this.creatorRoleMap = CreatorVOPresentation.getCreatorRoleMap();
    }

    return this.creatorRoleMap;
  }

  public String getCslEditorInstanceUrl() {
    return this.cslEditorInstanceUrl;
  }

  public DataSource getDataSource() {
    return this.dataSource;
  }

  public synchronized Map<String, String> getExcludedSourceGenreMap() {
    if (this.excludedSourceGenreMap == null) {
      this.excludedSourceGenreMap = SourceVOPresentation.getExcludedSourceGenreMap();
    }

    return this.excludedSourceGenreMap;
  }

  public FileService getFileService() {
    return this.fileService;
  }

  public String getFooterSnippet() {
    return this.footerSnippet;
  }

  public String getInstanceContextPath() {
    return this.instanceContextPath;
  }

  public ItemValidatingService getItemValidatingService() {
    return this.itemValidatingService;
  }

  public Map<String, SelectItem[]> getLanguageSelectItems() {
    return this.languageSelectItems;
  }

  public String getLogoUrl() {
    return this.logoUrl;
  }

  public OrganizationService getOrganizationService() {
    return this.organizationService;
  }

  public Set<AffiliationDbVO> getOuList() {
    return this.ouList;
  }

  public PubItemService getPubItemService() {
    return this.pubItemService;
  }

  /**
   * Provides the url for the pubman blog feed.
   * 
   * @return the escidoc instance
   * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
   */
  public String getPubmanBlogFeedUrl() {
    return this.pubmanBlogFeedUrl;
  }

  /**
   * Provides the escidoc instance string.
   * 
   * @return the escidoc instance
   * @throws PubManVersionNotAvailableException if escidoc instance can not be retrieved.
   */
  public String getPubmanInstanceUrl() throws PubManVersionNotAvailableException {
    return this.pubmanInstanceUrl;
  }

  public String getPubmanStyleTags() {
    return this.pubmanStyleTags;
  }

  public String getReloadResourceBundlesAndProperties() throws Exception {
    ResourceBundle.clearCache();
    PropertyReader.forceReloadProperties();
    this.loadProperties();
    this.languageSelectItems.clear();
    this.ouList.clear();

    // Renew Journal CitationStyles for JUS Exports
    XsltHelper.getJournalsXML();

    return "... Resource bundles and properties reloaded, language selection menu reset, Journal citation styles from CoNE reloaded.";
  }

  /**
   * Provides the escidoc version string without build date.
   * 
   * @return the escidoc version without build date
   * @throws PubManVersionNotAvailableException if escidoc version can not be retrieved.
   */
  public String getShortVersion() {

    return this.shortVersion;
  }

  public UserAccountService getUserAccountService() {
    return this.userAccountService;
  }

  public YearbookService getYearbookService() {
    return this.yearbookService;
  }

  public boolean isHandlesActivated() {
    return this.handlesActivated;
  }

  private void loadProperties() {
    try {
      final Properties solProperties = CommonUtils.getProperties(ApplicationBean.PROPERTY_FILENAME);
      this.version = solProperties.getProperty("inge.pubman.version");
      this.shortVersion = "";
      int whereToCut;
      try {
        this.shortVersion = solProperties.getProperty("inge.pubman.version");
        // get the position of the first blank before the word 'build'
        whereToCut = this.shortVersion.indexOf(" ");
        this.shortVersion = this.shortVersion.substring(0, whereToCut + 1);
      } catch (final Exception e) {
        ApplicationBean.logger.warn("The version of the application cannot be retrieved.");
      }

      this.appTitle = this.getLabel("Pubman_browserTitle");
      // hide the version information if system type is production
      if (!this.fetchSystemTypeFromProperty().equals(SystemType.Production_Server) && this.version != null) {
        this.appTitle += " " + this.version;
      }

      this.pubmanInstanceUrl = PropertyReader.getProperty("inge.pubman.instance.url");

      this.commonPresentationUrl = PropertyReader.getProperty("inge.pubman.common.presentation.url");
      if (this.commonPresentationUrl == null) {
        this.commonPresentationUrl = "";
      }

      this.pubmanBlogFeedUrl = PropertyReader.getProperty("inge.pubman.blog.news");
      if (this.pubmanBlogFeedUrl == null) {
        this.pubmanBlogFeedUrl = "";
      }

      try {
        this.pubmanStyleTags = this.buildPubmanStyleTags();
      } catch (final Exception e) {
        ApplicationBean.logger.error("Error while building style tags", e);
      }

      this.cookieVersion = PropertyReader.getProperty("inge.pubman.cookie.version");
      if (this.cookieVersion == null) {
        this.cookieVersion = "";
      }

      this.instanceContextPath = PropertyReader.getProperty("inge.pubman.instance.context.path");

      this.appContext = PropertyReader.getProperty("inge.pubman.instance.context.path") + "/faces/";

      this.logoUrl = PropertyReader.getProperty("inge.pubman.logo.url");

      this.additionalLogoCss = PropertyReader.getProperty("inge.pubman.logo.css");
      if (this.additionalLogoCss == null) {
        this.additionalLogoCss = "";
      }

      try {
        this.handlesActivated = Boolean.parseBoolean(PropertyReader.getProperty("inge.handles.activated"));
      } catch (final Exception e) {
        ApplicationBean.logger.error("Error reading property 'inge.handles.activated'", e);
        this.handlesActivated = false;
      }

      final String footerFileName = PropertyReader.getProperty("inge.pubman.footer.fileName");
      try {
        if (footerFileName != null && !footerFileName.isEmpty()) {
          this.footerSnippet = ResourceUtil.getResourceAsString(footerFileName, this.getClass().getClassLoader());
        }
      } catch (final Exception e) {
        ApplicationBean.logger.error("Error while reading footer file: " + footerFileName);
      }

      this.cslEditorInstanceUrl = PropertyReader.getProperty("inge.pubman.csl_editor.instance");
    } catch (final Exception e) {
      ApplicationBean.logger.error("Error while reading properties", e);
    }
  }

  public void setAppContext(String appContext) {
    this.appContext = appContext;
  }

  public void setContextService(ContextService contextService) {
    this.contextService = contextService;
  }

  public void setCslEditorInstanceUrl(String cslEditorInstanceUrl) {
    this.cslEditorInstanceUrl = cslEditorInstanceUrl;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  public void setFooterSnippet(String footerSnippet) {
    this.footerSnippet = footerSnippet;
  }

  public void setInstanceContextPath(String newInstanceContextPath) {
    this.instanceContextPath = newInstanceContextPath;
  }

  public void setItemValidatingService(ItemValidatingService itemValidatingService) {
    this.itemValidatingService = itemValidatingService;
  }

  public void setLanguageSelectItems(Map<String, SelectItem[]> languageSelectItems) {
    this.languageSelectItems = languageSelectItems;
  }

  public void setOrganizationService(OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  public void setOuList(Set<AffiliationDbVO> ouList) {
    this.ouList = ouList;
  }

  public void setPubItemService(PubItemService pubItemService) {
    this.pubItemService = pubItemService;
  }

  public void setUserAccountService(UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  public void setYearbookService(YearbookService yearbookService) {
    this.yearbookService = yearbookService;
  }

}
