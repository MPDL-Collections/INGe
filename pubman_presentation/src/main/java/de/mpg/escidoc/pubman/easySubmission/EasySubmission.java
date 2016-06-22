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
package de.mpg.escidoc.pubman.easySubmission;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.ejb.EJB;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.editItem.bean.CreatorCollection;
import de.mpg.escidoc.pubman.editItem.bean.IdentifierCollection;
import de.mpg.escidoc.pubman.editItem.bean.SourceBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.GenreSpecificItemManager;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.dataacquisition.DataHandlerBean;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerBean;
import de.mpg.mpdl.inge.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.mpdl.inge.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.mpdl.inge.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving
 * and submitting a PubItem within the easy submission process.
 * 
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class EasySubmission extends FacesBean {
  public static final String BEAN_NAME = "EasySubmission";
  private static Logger logger = Logger.getLogger(EasySubmission.class);
  // Import Service
  private DataSourceHandlerBean dataSourceHandler = new DataSourceHandlerBean();
  // Transformation Service
  private Transformation transformer = null;
  // XML Transforming Service
  @EJB
  private XmlTransforming xmlTransforming;
  // Validation Service
  @EJB
  private ItemValidating itemValidating;
  private HtmlSelectOneRadio radioSelect;
  private HtmlSelectOneMenu dateSelect;
  // constants for the submission method
  public SelectItem SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL",
      getLabel("easy_submission_method_manual"));
  public SelectItem SUBMISSION_METHOD_FETCH_IMPORT = new SelectItem("FETCH_IMPORT",
      getLabel("easy_submission_method_fetch_import"));
  public SelectItem[] SUBMISSION_METHOD_OPTIONS = new SelectItem[] {this.SUBMISSION_METHOD_MANUAL,
      this.SUBMISSION_METHOD_FETCH_IMPORT};
  // constants for Date types
  public SelectItem DATE_CREATED = new SelectItem("DATE_CREATED",
      getLabel("easy_submission_lblDateCreated"));
  public SelectItem DATE_SUBMITTED = new SelectItem("DATE_SUBMITTED",
      getLabel("easy_submission_lblDateSubmitted"));
  public SelectItem DATE_ACCEPTED = new SelectItem("DATE_ACCEPTED",
      getLabel("easy_submission_lblDateAccepted"));
  public SelectItem DATE_PUBLISHED_IN_PRINT = new SelectItem("DATE_PUBLISHED_IN_PRINT",
      getLabel("easy_submission_lblDatePublishedInPrint"));
  public SelectItem DATE_PUBLISHED_ONLINE = new SelectItem("DATE_PUBLISHED_ONLINE",
      getLabel("easy_submission_lblDatePublishedOnline"));
  public SelectItem DATE_MODIFIED = new SelectItem("DATE_MODIFIED",
      getLabel("easy_submission_lblDateModified"));
  public SelectItem[] DATE_TYPE_OPTIONS = new SelectItem[] {this.DATE_CREATED, this.DATE_SUBMITTED,
      this.DATE_ACCEPTED, this.DATE_PUBLISHED_IN_PRINT, this.DATE_PUBLISHED_ONLINE,
      this.DATE_MODIFIED};
  public final String INTERNAL_MD_FORMAT = "eSciDoc-publication-item";
  // Faces navigation string
  public final static String LOAD_EASYSUBMISSION = "loadEasySubmission";
  private UploadedFile uploadedFile;
  /*
   * private HtmlAjaxRepeat fileIterator = new HtmlAjaxRepeat(); private HtmlAjaxRepeat
   * locatorIterator = new HtmlAjaxRepeat(); private HtmlAjaxRepeat creatorIterator = new
   * HtmlAjaxRepeat();
   */
  public SelectItem[] locatorVisibilities;
  private CreatorCollection creatorCollection;
  private IdentifierCollection identifierCollection;
  private String selectedDate;
  private boolean fromEasySubmission = false;
  // Import
  private Vector<DataSourceVO> dataSources = new Vector<DataSourceVO>();
  private HtmlSelectOneRadio radioSelectFulltext = new HtmlSelectOneRadio();
  // private HtmlSelectOneMenu sourceSelect = new HtmlSelectOneMenu();
  public SelectItem[] EXTERNAL_SERVICE_OPTIONS;
  public SelectItem[] FULLTEXT_OPTIONS;
  public SelectItem[] REFERENCE_OPTIONS;
  private String serviceID;
  private String creatorParseString;
  private boolean overwriteCreators;
  private HtmlMessages valMessage = new HtmlMessages();
  private boolean autosuggestJournals = false;
  private String suggestConeUrl = null;
  private String hiddenAlternativeTitlesField;
  private String hiddenIdsField;
  // private HtmlAjaxRepeat identifierIterator;
  private HtmlSelectOneMenu genreSelect = new HtmlSelectOneMenu();
  /** pub context name. */
  private String contextName = null;
  private String locatorUpload;

  // Dummy for language autosuggest
  private String alternativeLanguageName;


  /**
   * Public constructor.
   */
  public EasySubmission() {

    // InitialContext initialContext = new InitialContext();
    ApplicationBean appBean = (ApplicationBean) getApplicationBean(ApplicationBean.class);
    this.transformer = appBean.getTransformationService();

    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    super.init();
    SUBMISSION_METHOD_MANUAL = new SelectItem("MANUAL", getLabel("easy_submission_method_manual"));
    SUBMISSION_METHOD_FETCH_IMPORT =
        new SelectItem("FETCH_IMPORT", getLabel("easy_submission_method_fetch_import"));
    SUBMISSION_METHOD_OPTIONS =
        new SelectItem[] {this.SUBMISSION_METHOD_MANUAL, this.SUBMISSION_METHOD_FETCH_IMPORT};
    EasySubmissionSessionBean essb = this.getEasySubmissionSessionBean();
    this.locatorVisibilities = this.getI18nHelper().getSelectItemsVisibility(true);
    // if the user has reached Step 3, an item has already been created and must be set in the
    // EasySubmissionSessionBean for further manipulation
    if (essb.getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP2)
        || essb.getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP3)) {
      // this.getEasySubmissionSessionBean().setCurrentItem(this.getItemControllerSessionBean().getCurrentPubItem()
      // );
      // bindFiles();

      if (essb.getLocators() == null) {
        // add a locator

        FileVO newLocator = new FileVO();
        newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class).warn(
                "WARNING: no content-category has been defined in Genres.xml");
          }
        }
        newLocator.setContentCategory(contentCategory);
        newLocator.setVisibility(FileVO.Visibility.PUBLIC);
        newLocator.setDefaultMetadata(new MdsFileVO());
        this.getEasySubmissionSessionBean().getLocators()
            .add(new PubFileVOPresentation(0, newLocator, true));

        // add a file
        /*
         * FileVO newFile = new FileVO(); newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
         * newFile.setVisibility(FileVO.Visibility.PUBLIC); newFile.setDefaultMetadata(new
         * MdsFileVO()); newFile.getDefaultMetadata().setTitle(new TextVO());
         * this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile,
         * false));
         */
      }
      /*
       * if (essb.getFiles().size() < 1) { // add a file FileVO newFile = new FileVO();
       * newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
       * newFile.setVisibility(FileVO.Visibility.PUBLIC); newFile.setDefaultMetadata(new
       * MdsFileVO()); newFile.getDefaultMetadata().setTitle(new TextVO());
       * this.getEasySubmissionSessionBean().getFiles().add(new PubFileVOPresentation(0, newFile,
       * false)); }
       */
      if (essb.getLocators().size() < 1) {
        // add a locator
        FileVO newLocator = new FileVO();
        newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class).warn(
                "WARNING: no content-category has been defined in Genres.xml");
          }
        }
        newLocator.setContentCategory(contentCategory);
        newLocator.setVisibility(FileVO.Visibility.PUBLIC);
        newLocator.setDefaultMetadata(new MdsFileVO());
        this.getEasySubmissionSessionBean().getLocators()
            .add(new PubFileVOPresentation(0, newLocator, true));
      }


    }
    if (essb.getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP4)) {
      if (getItem().getMetadata() != null && getItem().getMetadata().getCreators() != null) {
        for (CreatorVO creatorVO : getItem().getMetadata().getCreators()) {
          if (creatorVO.getType() == CreatorType.PERSON && creatorVO.getPerson() == null) {
            creatorVO.setPerson(new PersonVO());
          } else if (creatorVO.getType() == CreatorType.ORGANIZATION
              && creatorVO.getOrganization() == null) {
            creatorVO.setOrganization(new OrganizationVO());
          }
        }
      }
      if (essb.getCreators().size() == 0) {
        essb.bindCreatorsToBean(getItem().getMetadata().getCreators());
      }
      if (essb.getCreatorOrganizations().size() == 0) {
        essb.initOrganizationsFromCreators();
      }
    }
    if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP5)) {
      this.identifierCollection =
          new IdentifierCollection(this.getItem().getMetadata().getIdentifiers());
      // this.eventTitleCollection = new TitleCollection(this.getItem().getMetadata().getEvent());
    }
    // Get informations about import sources if submission method = fetching import
    if ((this.getEasySubmissionSessionBean().getCurrentSubmissionStep()
        .equals(EasySubmissionSessionBean.ES_STEP2) || this.getEasySubmissionSessionBean()
        .getCurrentSubmissionStep().equals(EasySubmissionSessionBean.ES_STEP3))
        && this.getEasySubmissionSessionBean().getCurrentSubmissionMethod().equals("FETCH_IMPORT")) {
      // Call source initialization only once
      if (!this.getEasySubmissionSessionBean().isImportSourceRefresh()) {
        this.getEasySubmissionSessionBean().setImportSourceRefresh(true);
        this.setImportSourcesInfo();
      } else if (this.getServiceID() != null && this.getServiceID().toLowerCase().equals("escidoc")) {
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_ALL);
      }
    } else {
      this.getEasySubmissionSessionBean().setImportSourceRefresh(false);
    }
    this.setBibTexInfo();
    if (getItem() != null && getItem().getMetadata() != null && getSource() != null
        && getSource().getGenre() != null && getSource().getGenre().equals(SourceVO.Genre.JOURNAL)) {
      this.autosuggestJournals = true;
    }
    if (getItem() != null && getItem().getMetadata() != null
        && getItem().getMetadata().getGenre() == null) {
      getItem().getMetadata().setGenre(Genre.ARTICLE);
    }
  }

  public String selectSubmissionMethod() {
    String submittedValue = CommonUtils.getUIValue(this.radioSelect);
    // set the desired submission method in the session bean
    EasySubmissionSessionBean easySubmissionSessionBean =
        (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    easySubmissionSessionBean.setCurrentSubmissionMethod(submittedValue);
    // select the default context if only one exists
    ContextListSessionBean contextListSessionBean =
        (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    if (contextListSessionBean.getDepositorContextList() != null
        && contextListSessionBean.getDepositorContextList().size() == 1) {
      contextListSessionBean.getDepositorContextList().get(0).setSelected(false);
      contextListSessionBean.getDepositorContextList().get(0).selectForEasySubmission();
    }
    // set the current submission step to step2
    easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    return null;
  }

  public String newEasySubmission() {
    // initialize the collection list first
    this.getContextListSessionBean();
    EasySubmissionSessionBean easySubmissionSessionBean =
        (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    this.getItemControllerSessionBean().setCurrentPubItem(null);
    // clean the EasySubmissionSessionBean
    easySubmissionSessionBean.cleanup();
    // also make sure that the EditItemSessionBean is cleaned, too
    this.getEditItemSessionBean().getFiles().clear();
    this.getEditItemSessionBean().getLocators().clear();
    // deselect the selected context
    ContextListSessionBean contextListSessionBean =
        (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    if (contextListSessionBean.getDepositorContextList() != null) {
      for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++) {
        contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
      }
    }
    // set the current submission step to step2
    if (contextListSessionBean.getDepositorContextList() != null
        && contextListSessionBean.getDepositorContextList().size() > 1) {
      // create a dummy item in the first context to avoid an empty item
      contextListSessionBean.getDepositorContextList().get(0).selectForEasySubmission();
      easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
    }
    // Skip Collection selection for Import & Easy Sub if only one Collection
    else {
      contextListSessionBean.getDepositorContextList().get(0).selectForEasySubmission();
      easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      this.init();
    }
    // set method to manual
    easySubmissionSessionBean
        .setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_MANUAL);
    // set the current submission method for edit item to easy submission (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission(
        EditItemSessionBean.SUBMISSION_METHOD_EASY_SUBMISSION);
    return "loadNewEasySubmission";
  }

  public String newImport() {
    // initialize the collection list first
    this.getContextListSessionBean();
    EasySubmissionSessionBean easySubmissionSessionBean =
        (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    this.getItemControllerSessionBean().setCurrentPubItem(null);
    // clean the EasySubmissionSessionBean
    easySubmissionSessionBean.cleanup();
    // also make sure that the EditItemSessionBean is cleaned, too
    this.getEditItemSessionBean().getFiles().clear();
    this.getEditItemSessionBean().getLocators().clear();
    // deselect the selected context
    ContextListSessionBean contextListSessionBean =
        (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
    if (contextListSessionBean.getDepositorContextList() != null) {
      for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++) {
        contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
      }
    }
    // set method to import
    easySubmissionSessionBean
        .setCurrentSubmissionMethod(EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT);
    // set the current submission step to step2
    if (contextListSessionBean.getDepositorContextList() != null
        && contextListSessionBean.getDepositorContextList().size() > 1) {
      easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP2);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission(
          EditItemSessionBean.SUBMISSION_METHOD_IMPORT);
      return "loadNewFetchMetadata";
    }
    // Skip Collection selection for Import & Easy Sub if only one Collection
    else {
      contextListSessionBean.getDepositorContextList().get(0).selectForEasySubmission();
      easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
      // set the current submission method for edit item to import (for GUI purpose)
      this.getEditItemSessionBean().setCurrentSubmission(
          EditItemSessionBean.SUBMISSION_METHOD_IMPORT);
      this.init();
      return "loadNewFetchMetadata";
    }

  }

  /**
   * This method adds a file to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public String addFile() {
    // first try to upload the entered file
    upload(true);
    // then try to save the locator
    saveLocator();

    if (this.getEasySubmissionSessionBean().getFiles() != null
        && this.getEasySubmissionSessionBean().getFiles().size() > 0
        && this.getEasySubmissionSessionBean().getFiles()
            .get(this.getEasySubmissionSessionBean().getFiles().size() - 1).getFile()
            .getDefaultMetadata().getSize() > 0) {
      FileVO newFile = new FileVO();
      newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
      newFile.setVisibility(FileVO.Visibility.PUBLIC);
      newFile.setDefaultMetadata(new MdsFileVO());
      this.getEasySubmissionSessionBean()
          .getFiles()
          .add(
              new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(),
                  newFile, false));
    }
    return "loadNewEasySubmission";
  }

  /**
   * This method adds a locator to the list of files of the item
   * 
   * @return navigation string (null)
   */
  public String addLocator() {
    // first try to upload the entered file
    upload(true);
    // then try to save the locator
    saveLocator();
    if (this.getEasySubmissionSessionBean().getLocators() != null
        && this.getEasySubmissionSessionBean().getLocators()
            .get(this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile()
            .getContent() != null
        && !this.getEasySubmissionSessionBean().getLocators()
            .get(this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile()
            .getContent().trim().equals("")) {
      PubFileVOPresentation newLocator =
          new PubFileVOPresentation(this.getEasySubmissionSessionBean().getLocators().size(), true);
      String contentCategory = null;
      if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
        contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
      } else {
        Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
        if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
          contentCategory = contentCategoryMap.values().iterator().next();
        } else {
          error("There is no content category available.");
          Logger.getLogger(PubFileVOPresentation.class).warn(
              "WARNING: no content-category has been defined in Genres.xml");
        }
      }
      newLocator.getFile().setContentCategory(contentCategory);
      newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
      newLocator.getFile().setDefaultMetadata(new MdsFileVO());
      this.getEasySubmissionSessionBean().getLocators().add(newLocator);
    }
    return "loadNewEasySubmission";
  }

  private void bindFiles() {
    List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    for (int i = 0; i < this.getItemControllerSessionBean().getCurrentPubItem().getFiles().size(); i++) {
      PubFileVOPresentation filepres =
          new PubFileVOPresentation(i, this.getItemControllerSessionBean().getCurrentPubItem()
              .getFiles().get(i));
      files.add(filepres);
    }
    this.getEasySubmissionSessionBean().setFiles(files);
  }

  /**
   * This method binds the uploaded files to the files in the PubItem during the save process
   */
  private void bindUploadedFiles() {
    this.getItem().getFiles().clear();
    if (this.getFiles() != null && this.getFiles().size() > 0) {
      for (int i = 0; i < this.getFiles().size(); i++) {
        this.getItem().getFiles().add(this.getFiles().get(i).getFile());
      }
    }
    if (this.getLocators() != null && this.getLocators().size() > 0) {
      for (int i = 0; i < this.getLocators().size(); i++) {
        this.getItem().getFiles().add(this.getLocators().get(i).getFile());
      }
    }
  }

  public String saveValues() {
    return null;
  }

  public String saveLocator() {
    EasySubmissionSessionBean essb = this.getEasySubmissionSessionBean();
    // set the name if it is not filled
    logger.info("this.getLocators().size():" + this.getLocators().size());
    if (this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata()
        .getTitle() == null
        || this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata()
            .getTitle().trim().equals("")) {
      this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata()
          .setTitle(this.getLocators().get(this.getLocators().size() - 1).getFile().getContent());
    }
    // set a dummy file size for rendering purposes
    if (this.getLocators().get(this.getLocators().size() - 1).getFile().getContent() != null
        && !this.getLocators().get(this.getLocators().size() - 1).getFile().getContent().trim()
            .equals("")) {
      this.getLocators().get(this.getLocators().size() - 1).getFile().getDefaultMetadata()
          .setSize(11);
    }
    // Visibility PUBLIC is static default value for locators
    this.getLocators().get(this.getLocators().size() - 1).getFile()
        .setVisibility(Visibility.PUBLIC);
    // As default value set 'supplementary material'
    // this.locatorIterator = new HtmlAjaxRepeat();
    return "loadNewEasySubmission";
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeFileIndexes() {
    if (this.getEasySubmissionSessionBean().getFiles() != null) {
      for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++) {
        this.getEasySubmissionSessionBean().getFiles().get(i).setIndex(i);
      }
    }
  }

  /**
   * This method reorganizes the index property in PubFileVOPresentation after removing one element
   * of the list.
   */
  public void reorganizeLocatorIndexes() {
    if (this.getEasySubmissionSessionBean().getLocators() != null) {
      for (int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++) {
        this.getEasySubmissionSessionBean().getLocators().get(i).setIndex(i);
      }
    }
  }

  /**
   * Saves the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String save() {
    // bind the temporary uploaded files to the files in the current item
    bindUploadedFiles();
    parseAndSetAlternativeSourceTitlesAndIds();
    this.setFromEasySubmission(true);
    // info(getMessage("easy_submission_preview_hint"));
    if (validate("easy_submission_step_5", "validate") == null) {
      return null;
    }
    EditItem editItem = (EditItem) getRequestBean(EditItem.class);
    editItem.setFromEasySubmission(true);
    String returnValue =
        (this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false));
    if (returnValue != null && !"".equals(returnValue)) {
      getEasySubmissionSessionBean().cleanup();
    }
    PubItemListSessionBean pubItemListSessionBean =
        (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
    if (pubItemListSessionBean != null) {
      pubItemListSessionBean.update();
    }
    return returnValue;

    // /*
    // * FrM: Validation with validation point "default"
    // */
    // ValidationReportVO report = null;
    // try
    // {
    // PubItemVO itemVO = new PubItemVO(this.getItem());
    // report = this.itemValidating.validateItemObject(itemVO, "default");
    // }
    // catch (Exception e)
    // {
    // throw new RuntimeException("Validation error", e);
    // }
    // logger.debug("Validation Report: " + report);
    //
    // if (report.isValid() && !report.hasItems())
    // {
    //
    // if (logger.isDebugEnabled())
    // {
    // logger.debug("Saving item...");
    // }
    //
    // //String retVal =
    // this.getItemControllerSessionBean().saveCurrentPubItem(DepositorWS.LOAD_DEPOSITORWS,
    // false);
    // this.getItemListSessionBean().setListDirty(true);
    // String retVal =
    // this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
    //
    // if (retVal == null)
    // {
    // this.showValidationMessages(
    // this.getItemControllerSessionBean().getCurrentItemValidationReport());
    // }
    // else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
    // {
    // this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
    // }
    // return retVal;
    // }
    // else if (report.isValid())
    // {
    // // TODO FrM: Informative messages
    // this.getItemListSessionBean().setListDirty(true);
    // String retVal =
    // this.getItemControllerSessionBean().saveCurrentPubItem(ViewItemFull.LOAD_VIEWITEM, false);
    //
    // if (retVal == null)
    // {
    // this.showValidationMessages(
    // this.getItemControllerSessionBean().getCurrentItemValidationReport());
    // }
    // else if (retVal != null && retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0)
    // {
    // this.showMessage(DepositorWS.MESSAGE_SUCCESSFULLY_SAVED);
    // }
    // return retVal;
    // }
    // else
    // {
    // // Item is invalid, do not submit anything.
    // this.showValidationMessages(report);
    // return null;
    // }
  }

  /**
   * Displays validation messages.
   * 
   * @author Michael Franke
   * @param report The Validation report object.
   */
  private void showValidationMessages(ValidationReportVO report) {
    for (Iterator<ValidationReportItemVO> iter = report.getItems().iterator(); iter.hasNext();) {
      ValidationReportItemVO element = (ValidationReportItemVO) iter.next();
      if (element.isRestrictive()) {
        error(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
      } else {
        info(getMessage(element.getContent()).replaceAll("\\$1", element.getElement()));
      }
    }
    this.valMessage.setRendered(true);
  }

  /**
   * Uploads a file
   * 
   * @param event
   */
  public void fileUploaded(FileUploadEvent event) {
    uploadedFile = event.getFile();
    upload(true);
    /*
     * int indexUpload = this.getEasySubmissionSessionBean().getFiles().size() - 1; UploadedFile
     * file = (UploadedFile)event.getNewValue(); String contentURL; if (file != null) { contentURL =
     * uploadFile(file); if (contentURL != null && !contentURL.trim().equals("")) { FileVO fileVO =
     * this.getEasySubmissionSessionBean().getFiles().get(indexUpload).getFile();
     * fileVO.getDefaultMetadata().setSize((int)file.getLength());
     * fileVO.setName(file.getFilename()); fileVO.getDefaultMetadata().setTitle(new
     * TextVO(file.getFilename())); fileVO.setMimeType(file.getContentType()); FormatVO formatVO =
     * new FormatVO(); formatVO.setType("dcterms:IMT"); formatVO.setValue(file.getContentType());
     * fileVO.getDefaultMetadata().getFormats().add(formatVO); fileVO.setContent(contentURL); } }
     */
  }

  public void bibtexFileUploaded(FileUploadEvent event) {
    getEasySubmissionSessionBean().setUploadedBibtexFile(event.getFile());
  }

  /**
   * This method uploads a selected file and gives out error messages if needed
   * 
   * @param needMessages Flag to invoke error messages (set it to false if you invoke the validation
   *        service before or after)
   * @return String navigation string
   * @author schraut
   */
  public String upload(boolean needMessages) {
    if (uploadedFile != null) {
      UploadedFile file = uploadedFile;
      /*
       * for(UploadItem file : this.uploadedFile) {
       */
      StringBuffer errorMessage = new StringBuffer();
      int indexUpload = this.getFiles().size() - 1;
      // UploadItem file = this.uploadedFile;
      String contentURL;
      if (file != null) {
        // set the file name automatically if it is not filled by the user
        /*
         * if (this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue()
         * == null ||
         * this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue
         * ().trim() .equals("")) { this.getFiles().get(indexUpload).getFile().getDefaultMetadata()
         * .setTitle(new TextVO(file.getFilename())); } if
         * (this.getFiles().get(this.getFiles().size() - 1).getContentCategory() != null &&
         * !this.getFiles().get(this.getFiles().size() - 1).getContentCategory().trim().equals("")
         * && !this.getFiles().get(this.getFiles().size() -
         * 1).getContentCategory().trim().equals("-")) {
         */
        contentURL = uploadFile(file);
        String fixedFileName = CommonUtils.fixURLEncoding(file.getFileName());
        if (contentURL != null && !contentURL.trim().equals("")) {

          FileVO newFile = new FileVO();
          newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
          newFile.setVisibility(FileVO.Visibility.PUBLIC);
          newFile.setDefaultMetadata(new MdsFileVO());
          this.getEasySubmissionSessionBean()
              .getFiles()
              .add(
                  new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(),
                      newFile, false));

          newFile.getDefaultMetadata().setTitle(fixedFileName);
          newFile.setName(fixedFileName);
          newFile.getDefaultMetadata().setSize((int) file.getSize());
          // set the file name automatically if it is not filled by the user
          /*
           * if(this.getFiles().get(indexUpload).getFile().getName() == null ||
           * this.getFiles().get(indexUpload).getFile().getName().trim().equals("")) {
           * this.getFiles().get(indexUpload).getFile().setName(file.getFilename()); }
           */


          // newFile.setMimeType(file.getContentType());

          Tika tika = new Tika();
          /*
           * if(file.isTempFile()) {
           */
          try {
            InputStream fis = file.getInputstream();
            newFile.setMimeType(tika.detect(fis, fixedFileName));
            fis.close();
          } catch (IOException e) {
            logger.info("Error while trying to detect mimetype of file " + fixedFileName, e);
          }
          /*
           * } else { newFile.setMimeType(tika.detect(file.getFileName())); }
           */

          FormatVO formatVO = new FormatVO();
          formatVO.setType("dcterms:IMT");
          formatVO.setValue(newFile.getMimeType());
          // correct several PDF Mime type errors manually
          /*
           * if (file.getFileName() != null && (file.getFileName().endsWith(".pdf") ||
           * file.getFileName().endsWith(".PDF"))) { newFile.setMimeType("application/pdf");
           * formatVO.setValue("application/pdf"); }
           */
          newFile.getDefaultMetadata().getFormats().add(formatVO);
          newFile.setContent(contentURL);
        }
        this.init();
      }
      /*
       * else {
       * errorMessage.append(getMessage("ComponentContentCategoryNotProvidedEasySubmission")); } }
       * else { if
       * (this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue() !=
       * null &&
       * !this.getFiles().get(indexUpload).getFile().getDefaultMetadata().getTitle().getValue
       * ().trim() .equals("")) { errorMessage.append(getMessage("ComponentContentNotProvided")); if
       * (this.getFiles().get(indexUpload).getContentCategory() != null &&
       * !this.getFiles().get(indexUpload).getContentCategory().trim().equals("") &&
       * !this.getFiles().get(indexUpload).getContentCategory().trim().equals("-")) {
       * errorMessage.append(getMessage("ComponentContentCategoryNotProvidedEasySubmission")); } } }
       */
      if (errorMessage.length() > 0) {
        error(errorMessage.toString());
      }
      // }
    }
    return "loadNewEasySubmission";
  }

  /**
   * Uploads a file to the FIZ Framework and recieves and returns the location of the file in the FW
   * 
   * @param file
   * @return
   */
  public String uploadFile(UploadedFile file) {
    String contentURL = "";
    if (file != null && file.getSize() > 0) {
      try {
        // upload the file
        LoginHelper loginHelper = (LoginHelper) this.getBean(LoginHelper.class);
        URL url = this.uploadFile(file, file.getContentType(), loginHelper.getESciDocUserHandle());
        if (url != null) {
          contentURL = url.toString();
        }
      } catch (Exception e) {
        logger.error("Could not upload file." + "\n" + e.toString());
        ((ErrorPage) this.getBean(ErrorPage.class)).setException(e);
        // force JSF to load the ErrorPage
        try {
          FacesContext.getCurrentInstance().getExternalContext().redirect("ErrorPage.jsp");
        } catch (Exception ex) {
          logger.error(e.toString());
        }
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }
    return contentURL;
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param uploadedFile The file to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected URL uploadFile(UploadedFile uploadedFile, String mimetype, String userHandle)
      throws Exception {
    // Prepare the HttpMethod.
    String fwUrl = PropertyReader.getFrameworkUrl();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    /*
     * if(uploadedFile.isTempFile()) {
     */
    InputStream fis = uploadedFile.getInputstream();
    method.setRequestEntity(new InputStreamRequestEntity(fis));

    /*
     * } else { method.setRequestEntity(new InputStreamRequestEntity(new
     * ByteArrayInputStream(uploadedFile.getData()))); }
     */
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
    // Execute the method with HttpClient.
    HttpClient client = new HttpClient();
    ProxyHelper.setProxy(client, fwUrl);
    client.executeMethod(method);
    String response = method.getResponseBodyAsString();
    fis.close();
    return xmlTransforming.transformUploadResponseToFileURL(response);
  }

  /**
   * Uploads a file to the staging servlet and returns the corresponding URL.
   * 
   * @param InputStream to upload
   * @param mimetype The mimetype of the file
   * @param userHandle The userhandle to use for upload
   * @return The URL of the uploaded file.
   * @throws Exception If anything goes wrong...
   */
  protected URL uploadFile(InputStream in, String mimetype, String userHandle) throws Exception {
    // Prepare the HttpMethod.
    String fwUrl = PropertyReader.getFrameworkUrl();
    PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
    method.setRequestEntity(new InputStreamRequestEntity(in));
    method.setRequestHeader("Content-Type", mimetype);
    method.setRequestHeader("Cookie", "escidocCookie=" + userHandle);
    // Execute the method with HttpClient.
    HttpClient client = new HttpClient();
    client.executeMethod(method);
    String response = method.getResponseBodyAsString();
    return xmlTransforming.transformUploadResponseToFileURL(response);
  }

  public String uploadBibtexFile() {
    try {
      StringBuffer content = new StringBuffer();
      try {
        UploadedFile uploadedBibTexFile = getEasySubmissionSessionBean().getUploadedBibtexFile();
        InputStream fileIs = null;

        /*
         * if(uploadedBibTexFile.isTempFile()) { fileIs = new
         * FileInputStream(uploadedBibTexFile.getFile()); } else { fileIs = new
         * ByteArrayInputStream(uploadedBibTexFile.getData()); }
         */

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(uploadedBibTexFile.getInputstream()));
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line + "\n");
        }
      } catch (NullPointerException npe) {
        logger.error("Error reading bibtex file", npe);
        warn(getMessage("easy_submission_bibtex_empty_file"));
        return null;
      }
      // Transform from bibtex to escidoc pubItem
      Format source = new Format("eSciDoc-publication-item", "application/xml", "*");
      Format target = new Format("html-meta-tags-highwire-press-citation", "text/html", "UTF-8");
      byte[] result =
          this.transformer.transform(content.toString().getBytes("UTF-8"), source, target,
              "escidoc");
      PubItemVO itemVO = this.xmlTransforming.transformToPubItem(new String(result));
      itemVO.setContext(getItem().getContext());
      // Check if reference has to be uploaded as file
      if (this.getEasySubmissionSessionBean().getRadioSelectReferenceValue()
          .equals(this.getEasySubmissionSessionBean().getREFERENCE_FILE())) {
        LocatorUploadBean locatorBean = new LocatorUploadBean();
        Vector<FileVO> locators = locatorBean.getLocators(itemVO);
        // Check if item has locators
        if (locators != null && locators.size() > 0) {
          // Upload the locators as file
          for (int i = 0; i < locators.size(); i++) {
            // Add files to item
            FileVO uploadedLocator = locatorBean.uploadLocatorAsFile(locators.get(i));
            if (uploadedLocator != null) {
              // remove locator
              itemVO.getFiles().remove(i);
              // add file
              itemVO.getFiles().add(uploadedLocator);
            }
          }
        }
      }
      PubItemVOPresentation pubItemPres = new PubItemVOPresentation(itemVO);
      this.getItemControllerSessionBean().setCurrentPubItem(pubItemPres);
      this.setItem(pubItemPres);
    } catch (Exception e) {
      logger.error("Error reading bibtex file", e);
      error(getMessage("easy_submission_bibtex_error"));
      return null;
    }
    return "loadNewEasySubmission";
  }

  /**
   * Handles the import from an external ingestion sources.
   * 
   * @return navigation String
   */
  public String harvestData() {
    FileVO fileVO = new FileVO();
    Vector<FileVO> fileVOs = new Vector<FileVO>();
    String fetchedItem = null;
    String service = this.getEasySubmissionSessionBean().getCurrentExternalServiceType();
    PubItemVO itemVO = null;
    byte[] fetchedItemByte = null;
    DataHandlerBean dataHandler = new DataHandlerBean();
    // Fetch data from external system
    if (EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL.equals(this.getEasySubmissionSessionBean()
        .getImportMethod())) {
      if (getServiceID() == null || "".equals(getServiceID())) {
        warn(getMessage("easy_submission_external_service_no_id"));
        return null;
      }
      try {
        // Harvest metadata
        logger.debug("HarvestData: "
            + this.getEasySubmissionSessionBean().getCurrentExternalServiceType() + ": "
            + getServiceID());
        fetchedItemByte = dataHandler.doFetch(service, getServiceID(), this.INTERNAL_MD_FORMAT);
        fetchedItem = new String(fetchedItemByte, 0, fetchedItemByte.length, "UTF8");
        // Harvest full text
        if (this.getEasySubmissionSessionBean().isFulltext()
            && ((!this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                .equals(this.getEasySubmissionSessionBean().FULLTEXT_NONE)) && !fetchedItem
                .equals("")) && !service.equalsIgnoreCase("escidoc")) {
          DataSourceVO source = this.dataSourceHandler.getSourceByName(service);
          Vector<FullTextVO> ftFormats = source.getFtFormats();
          FullTextVO fulltext = new FullTextVO();
          Vector<String> formats = new Vector<String>();
          // Get DEFAULT full text version from source
          if (this.getEasySubmissionSessionBean().getRadioSelectFulltext()
              .equals(this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT)) {
            for (int x = 0; x < ftFormats.size(); x++) {
              fulltext = ftFormats.get(x);
              if (fulltext.isFtDefault()) {
                formats.add(fulltext.getName());
                break;
              }
            }
          }
          // Get ALL full text versions from source
          if (this.getEasySubmissionSessionBean().getRadioSelectFulltext()
              .equals(this.getEasySubmissionSessionBean().FULLTEXT_ALL)) {
            for (int x = 0; x < ftFormats.size(); x++) {
              fulltext = ftFormats.get(x);
              formats.add(fulltext.getName());
            }
          }
          String[] arrFormats = new String[formats.size()];
          byte[] ba =
              dataHandler.doFetch(this.getEasySubmissionSessionBean()
                  .getCurrentExternalServiceType(), getServiceID(), formats.toArray(arrFormats));
          LoginHelper loginHelper = (LoginHelper) this.getBean(LoginHelper.class);
          ByteArrayInputStream in = new ByteArrayInputStream(ba);
          URL fileURL =
              this.uploadFile(in, dataHandler.getContentType(), loginHelper.getESciDocUserHandle());
          if (fileURL != null && !fileURL.toString().trim().equals("")) {
            fileVO = dataHandler.getComponentVO();
            MdsFileVO fileMd = fileVO.getDefaultMetadata();
            fileVO.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            fileVO.setVisibility(dataHandler.getVisibility());
            fileVO.setDefaultMetadata(fileMd);
            fileVO.getDefaultMetadata().setTitle(
                this.replaceSlashes(getServiceID().trim() + dataHandler.getFileEnding()));
            fileVO.setMimeType(dataHandler.getContentType());
            fileVO
                .setName(this.replaceSlashes(getServiceID().trim() + dataHandler.getFileEnding()));
            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(dataHandler.getContentType());
            fileVO.getDefaultMetadata().getFormats().add(formatVO);
            fileVO.setContent(fileURL.toString());
            fileVO.getDefaultMetadata().setSize(ba.length);
            fileVO.getDefaultMetadata().setDescription(
                "File downloaded from " + service + " at " + CommonUtils.currentDate());
            fileVO.setContentCategory(dataHandler.getContentCategory());
            fileVOs.add(fileVO);
          }
        }
      } catch (AccessException inre) {
        logger.error("Error fetching from external import source", inre);
        error(getMessage("easy_submission_import_from_external_service_access_denied_error")
            + getServiceID());
        return null;
      } catch (IdentifierNotRecognisedException inre) {
        logger.error("Error fetching from external import source", inre);
        error(getMessage("easy_submission_import_from_external_service_identifier_error")
            + getServiceID());
        return null;
      } catch (SourceNotAvailableException anae) {
        logger.error("Import source currently not available", anae);
        long millis = anae.getRetryAfter().getTime() - (new Date()).getTime();
        if (millis < 1) {
          millis = 1;
        }
        error(getMessage("easy_submission_external_source_not_available_error").replace("$1",
            Math.ceil(millis / 1000) + ""));
        return null;
      } catch (FormatNotAvailableException e) {
        error(getMessage("formatNotAvailable_FromFetchingSource").replace("$1", e.getMessage())
            .replace("$2", service));
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_NONE);
      } catch (Exception e) {
        logger.error("Error fetching from external import source", e);
        error(getMessage("easy_submission_import_from_external_service_error"));
        return null;
      }
      // Generate item ValueObject
      if (fetchedItem != null && !fetchedItem.trim().equals("")) {
        try {
          itemVO = this.xmlTransforming.transformToPubItem(fetchedItem);
          // Upload fulltexts from other escidoc repositories to current repository
          if (this.getEasySubmissionSessionBean().isFulltext()
              && this.getEasySubmissionSessionBean().getRadioSelectFulltext() != null
              && this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                  .equals(this.getEasySubmissionSessionBean().FULLTEXT_ALL)
              && service.equalsIgnoreCase("escidoc")) {
            boolean hasFile = false;
            List<FileVO> fetchedFileList = itemVO.getFiles();
            for (int i = 0; i < fetchedFileList.size(); i++) {
              FileVO file = fetchedFileList.get(i);
              if (file.getStorage().equals(FileVO.Storage.INTERNAL_MANAGED)) {
                try {
                  FileVO newFile = new FileVO();
                  byte[] content =
                      dataHandler.retrieveComponentContent(this.getServiceID(), file.getContent());
                  LoginHelper loginHelper = (LoginHelper) this.getBean(LoginHelper.class);
                  ByteArrayInputStream in = new ByteArrayInputStream(content);
                  URL fileURL;
                  fileURL =
                      this.uploadFile(in, dataHandler.getContentType(),
                          loginHelper.getESciDocUserHandle());
                  if (fileURL != null && !fileURL.toString().trim().equals("")
                      && file.getVisibility().equals(FileVO.Visibility.PUBLIC)) {
                    hasFile = true;
                    newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
                    newFile.setVisibility(file.getVisibility());
                    newFile.setDefaultMetadata(new MdsFileVO());
                    newFile.getDefaultMetadata().setTitle(
                        this.replaceSlashes(file.getDefaultMetadata().getTitle()));
                    newFile.setMimeType(file.getMimeType());
                    newFile.setName(this.replaceSlashes(file.getName()));
                    FormatVO formatVO = new FormatVO();
                    formatVO.setType("dcterms:IMT");
                    formatVO.setValue(file.getMimeType());
                    newFile.getDefaultMetadata().getFormats().add(formatVO);
                    newFile.setContent(fileURL.toString());
                    newFile.getDefaultMetadata().setSize(content.length);
                    if (file.getDescription() != null) {
                      newFile.getDefaultMetadata().setDescription(
                          file.getDescription() + " File downloaded from " + service + " at "
                              + CommonUtils.currentDate());
                    } else {
                      newFile.getDefaultMetadata().setDescription(
                          "File downloaded from " + service + " at " + CommonUtils.currentDate());
                    }
                    newFile.setContentCategory(file.getContentCategory());
                    fileVOs.add(newFile);
                  }
                } catch (Exception e) {
                  logger.error("Error fetching file from coreservice", e);
                }
              } else if (file.getStorage().equals(FileVO.Storage.EXTERNAL_URL)
                  && file.getVisibility().equals(FileVO.Visibility.PUBLIC)) {
                // Locator is just added as is
                fileVOs.add(file);
              }
            }
            if (!hasFile) {
              info(getMessage("easy_submission_import_from_external_service_identifier_info"));
            }
          }
          itemVO.getFiles().clear();
          itemVO.setContext(getItem().getContext());
          if (dataHandler.getItemUrl() != null) {
            IdentifierVO id = new IdentifierVO();
            id.setType(IdType.URI);
            try {
              id.setId(java.net.URLDecoder.decode(dataHandler.getItemUrl().toString(), "UTF-8"));
              itemVO.getMetadata().getIdentifiers().add(id);
            } catch (UnsupportedEncodingException e) {
              logger.warn("Item URL could not be decoded");
            }
          }
          if (this.getEasySubmissionSessionBean().isFulltext()
              && !this.getEasySubmissionSessionBean().getRadioSelectFulltext()
                  .equals(this.getEasySubmissionSessionBean().FULLTEXT_NONE)) {
            for (int i = 0; i < fileVOs.size(); i++) {
              FileVO tmp = fileVOs.get(i);
              itemVO.getFiles().add(tmp);
            }
            fileVOs.clear();
          }
          this.getItemControllerSessionBean().getCurrentPubItem().setMetadata(itemVO.getMetadata());
          this.getItemControllerSessionBean().getCurrentPubItem().getFiles().clear();
          this.getItemControllerSessionBean().getCurrentPubItem().getFiles()
              .addAll(itemVO.getFiles());
          // Reset info for next call
          // this.setImportSourcesInfo(); Commented out because of browser back button problem.
          this.setBibTexInfo();
        } catch (TechnicalException e) {
          logger.warn("Error transforming item to pubItem.");
          error(getMessage("easy_submission_import_from_external_service_error"));
          return null;
        }
      } else {
        logger.warn("Empty fetched Item.");
        error(getMessage("easy_submission_import_from_external_service_error"));
        return null;
      }
    }
    // Fetch data from provided file
    else if (EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX.equals(this
        .getEasySubmissionSessionBean().getImportMethod())) {
      String uploadResult = uploadBibtexFile();
      if (uploadResult == null) {
        return null;
      }
    }
    this.getEditItemSessionBean().clean();
    return "loadEditItem";
  }

  public String cancel() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);
    try {
      FacesContext.getCurrentInstance().getExternalContext().redirect("faces/SubmissionPage.jsp");
    } catch (Exception e) {
      logger
          .error(
              "Cancel error: could not find context to redirect to SubmissionPage.jsp in Full Submssion",
              e);
    }
    return null;
  }

  public String loadStep1() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP1);
    return "loadNewEasySubmission";
  }

  public String loadStep2() {
    if (this.getContextListSessionBean().getDepositorContextList() != null
        && this.getContextListSessionBean().getDepositorContextList().size() > 1) {
      this.getEasySubmissionSessionBean().setCurrentSubmissionStep(
          EasySubmissionSessionBean.ES_STEP2);
    } else {
      try {
        FacesContext.getCurrentInstance().getExternalContext().redirect("faces/SubmissionPage.jsp");
      } catch (Exception e) {
        logger.error("could not find context to redirect to SubmissionPage.jsp in Easy Submssion",
            e);
      }
    }
    return "loadNewEasySubmission";
  }

  public String validateAndLoadStep3Manual() {
    // validate
    if (validate("easy_submission_step_4", "loadNewEasySubmission") == null) {
      return "";
    }
    return loadStep3Manual();
  }

  public String loadStep3Manual() {
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
    this.init();
    return "loadNewEasySubmission";
  }

  public String loadStep4Manual() {
    // parse hidden source information
    parseAndSetAlternativeSourceTitlesAndIds();
    // first try to upload the entered file
    // upload(false);
    PubItemVO item = this.getItemControllerSessionBean().getCurrentPubItem();

    // then try to save the locator
    saveLocator();
    // save the files and locators in the item in the ItemControllerSessionBean
    this.getItemControllerSessionBean().getCurrentPubItem().getFiles().clear();
    // first add the files
    for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++) {
      this.getItemControllerSessionBean().getCurrentPubItem().getFiles()
          .add(this.getEasySubmissionSessionBean().getFiles().get(i).getFile());
    }
    // then add the locators
    for (int i = 0; i < this.getEasySubmissionSessionBean().getLocators().size(); i++) {
      this.getItemControllerSessionBean().getCurrentPubItem().getFiles()
          .add(this.getEasySubmissionSessionBean().getLocators().get(i).getFile());
    }
    // add an empty file and an empty locator if necessary for display purposes
    if (this.getEasySubmissionSessionBean().getFiles() != null
        && this.getEasySubmissionSessionBean().getFiles().size() > 0) {
      if (this.getEasySubmissionSessionBean().getFiles()
          .get(this.getEasySubmissionSessionBean().getFiles().size() - 1).getFile()
          .getDefaultMetadata().getSize() > 0) {
        FileVO newFile = new FileVO();
        newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        newFile.setVisibility(FileVO.Visibility.PUBLIC);
        newFile.setDefaultMetadata(new MdsFileVO());
        this.getEasySubmissionSessionBean()
            .getFiles()
            .add(
                new PubFileVOPresentation(this.getEasySubmissionSessionBean().getFiles().size(),
                    newFile, false));
      }
    }
    if (this.getEasySubmissionSessionBean().getLocators() != null
        && this.getEasySubmissionSessionBean().getLocators().size() > 0) {
      if (this.getEasySubmissionSessionBean().getLocators()
          .get(this.getEasySubmissionSessionBean().getLocators().size() - 1).getFile()
          .getDefaultMetadata().getSize() > 0) {
        PubFileVOPresentation newLocator =
            new PubFileVOPresentation(this.getEasySubmissionSessionBean().getLocators().size(),
                true);
        String contentCategory = null;
        if (PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL") != null) {
          contentCategory = PubFileVOPresentation.getContentCategoryUri("SUPPLEMENTARY_MATERIAL");
        } else {
          Map<String, String> contentCategoryMap = PubFileVOPresentation.getContentCategoryMap();
          if (contentCategoryMap != null && !contentCategoryMap.entrySet().isEmpty()) {
            contentCategory = contentCategoryMap.values().iterator().next();
          } else {
            error("There is no content category available.");
            Logger.getLogger(PubFileVOPresentation.class).warn(
                "WARNING: no content-category has been defined in Genres.xml");
          }
        }
        newLocator.getFile().setContentCategory(contentCategory);
        newLocator.getFile().setVisibility(FileVO.Visibility.PUBLIC);
        newLocator.getFile().setDefaultMetadata(new MdsFileVO());
        this.getEasySubmissionSessionBean().getLocators().add(newLocator);
      }
    }
    // additionally map the dates if the user comes from Step5
    /*
     * if
     * (this.getEasySubmissionSessionBean().getCurrentSubmissionStep().equals(EasySubmissionSessionBean
     * .ES_STEP5)) { mapSelectedDate(); }
     */
    FacesContext fc = FacesContext.getCurrentInstance();
    // validate
    if (validate("easy_submission_step_3", "loadNewEasySubmission") == null) {
      return "";
    }
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP4);
    this.init();
    return "loadNewEasySubmission";
  }

  public String loadStep5Manual() {
    PubItemVO item = this.getItemControllerSessionBean().getCurrentPubItem();
    // validate
    if (validate("easy_submission_step_4", "loadNewEasySubmission") == null) {
      return "";
    }
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP5);
    this.init();
    return "loadNewEasySubmission";
  }

  public String loadPreview() {
    parseAndSetAlternativeSourceTitlesAndIds();
    // validate
    if (validate("easy_submission_step_5", "loadEditItem") == null) {
      return "";
    } else {
      // this.getEasySubmissionSessionBean().bindOrganizationsToCreators();
      // this.getEasySubmissionSessionBean().bindCreatorsToVO(
      // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getCreators());
      this.getEasySubmissionSessionBean().cleanup();
      this.getEditItemSessionBean().clean();
      return "loadEditItem";
    }
  }

  /**
   * Validates the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String validate(String validationPoint, String navigateTo) {
    try {
      // bind Organizations To Creators
      if (!this.getEasySubmissionSessionBean().bindOrganizationsToCreators()) {
        return null;
      }
      PubItemVO pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
      // write creators back to VO
      if (this.getEasySubmissionSessionBean().getCurrentSubmissionStep() == EasySubmissionSessionBean.ES_STEP4) {
        this.getEasySubmissionSessionBean().bindCreatorsToVO(pubItem.getMetadata().getCreators());
      }
      PubItemVO itemVO = new PubItemVO(pubItem);

      // cleanup item according to genre specific MD specification
      GenreSpecificItemManager itemManager =
          new GenreSpecificItemManager(itemVO, GenreSpecificItemManager.SUBMISSION_METHOD_EASY);
      try {
        itemVO = (PubItemVO) itemManager.cleanupItem();
      } catch (Exception e) {
        throw new RuntimeException("Error while cleaning up item genre specificly", e);
      }
      ValidationReportVO report = this.itemValidating.validateItemObject(itemVO, validationPoint);
      if (!report.isValid()) {
        for (ValidationReportItemVO item : report.getItems()) {
          if (item.isRestrictive()) {
            error(getMessage(item.getContent()));
          } else {
            warn(getMessage(item.getContent()));
          }
        }
        return null;
      }
    } catch (Exception e) {
      logger.error("Validation error", e);
    }
    return navigateTo;
  }

  /**
   * This method maps the entered date into the MD record of the item according to the selected type
   */
  private void mapSelectedDate() {
    String selectedDateType = CommonUtils.getUIValue(this.dateSelect);
    // first delete all previously entered dates
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateCreated("");
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateSubmitted("");
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateAccepted("");
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
        .setDatePublishedOnline("");
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setDateModified("");
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
        .setDatePublishedInPrint("");
    // map the selected date type to the referring metadata property
    if (selectedDateType.equals("DATE_CREATED")) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDateCreated(this.getEasySubmissionSessionBean().getSelectedDate());
    } else if (selectedDateType.equals("DATE_SUBMITTED")) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDateSubmitted(this.getEasySubmissionSessionBean().getSelectedDate());
    } else if (selectedDateType.equals("DATE_ACCEPTED")) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDateAccepted(this.getEasySubmissionSessionBean().getSelectedDate());
    } else if (selectedDateType.equals("DATE_PUBLISHED_ONLINE")) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDatePublishedOnline(this.getEasySubmissionSessionBean().getSelectedDate());
    } else if (selectedDateType.equals("DATE_MODIFIED")) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDateModified(this.getEasySubmissionSessionBean().getSelectedDate());
    } else {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
          .setDatePublishedInPrint(this.getEasySubmissionSessionBean().getSelectedDate());
    }
  }

  /**
   * Fill import source values dynamically from importsourceHandler
   */
  private void setImportSourcesInfo() {
    try {
      this.dataSources = this.dataSourceHandler.getSources(this.INTERNAL_MD_FORMAT);
      Vector<SelectItem> v_serviceOptions = new Vector<SelectItem>();
      Vector<FullTextVO> ftFormats = new Vector<FullTextVO>();
      String currentSource = "";
      for (int i = 0; i < this.dataSources.size(); i++) {
        DataSourceVO source = (DataSourceVO) this.dataSources.get(i);
        v_serviceOptions.add(new SelectItem(source.getName()));
        this.getEasySubmissionSessionBean().setCurrentExternalServiceType(source.getName());
        currentSource = source.getName();
        // Get full text informations from this source
        ftFormats = source.getFtFormats();
        for (int x = 0; x < ftFormats.size(); x++) {
          this.getEasySubmissionSessionBean().setFulltext(true);
          FullTextVO ft = ftFormats.get(x);
          if (ft.isFtDefault()) {
            this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
            this.getEasySubmissionSessionBean().setRadioSelectFulltext(
                this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT);
          }
        }
        if (ftFormats.size() <= 0) {
          this.getEasySubmissionSessionBean().setFulltext(false);
          this.getEasySubmissionSessionBean().setCurrentFTLabel("");
        }
      }
      this.EXTERNAL_SERVICE_OPTIONS = new SelectItem[v_serviceOptions.size()];
      v_serviceOptions.toArray(this.EXTERNAL_SERVICE_OPTIONS);
      this.getEasySubmissionSessionBean()
          .setEXTERNAL_SERVICE_OPTIONS(this.EXTERNAL_SERVICE_OPTIONS);
      if (currentSource.toLowerCase().equals("escidoc")) {
        this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
            new SelectItem[] {
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_ALL,
                    getLabel("easy_submission_lblFulltext_all")),
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_ALL);
      } else {
        if (ftFormats.size() > 1) {
          this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
              new SelectItem[] {
                  new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT, this
                      .getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_ALL,
                      getLabel("easy_submission_lblFulltext_all")),
                  new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                      getLabel("easy_submission_lblFulltext_none"))});
        } else
          this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
              new SelectItem[] {
                  new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT, this
                      .getEasySubmissionSessionBean().getCurrentFTLabel()),
                  new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                      getLabel("easy_submission_lblFulltext_none"))});
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Triggered when the selection of the external system is changed Updates full text selection
   * 
   * @return String navigation string
   */
  public void changeImportSource(String newImportSource) {

    DataSourceVO currentSource = null;
    currentSource = this.dataSourceHandler.getSourceByName(newImportSource);
    if (currentSource == null) {
      currentSource = new DataSourceVO();
    }
    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(currentSource.getName());
    Vector<FullTextVO> ftFormats = currentSource.getFtFormats();
    if (ftFormats != null && ftFormats.size() > 0) {
      for (int x = 0; x < ftFormats.size(); x++) {
        this.getEasySubmissionSessionBean().setFulltext(true);
        FullTextVO ft = ftFormats.get(x);
        if (ft.isFtDefault()) {
          this.getEasySubmissionSessionBean().setCurrentFTLabel(ft.getFtLabel());
          this.getEasySubmissionSessionBean().setRadioSelectFulltext(
              this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT);
        }
      }
    } else {
      this.getEasySubmissionSessionBean().setFulltext(false);
      this.getEasySubmissionSessionBean().setCurrentFTLabel("");
    }
    // This has to be set, because escidoc does not have a default fetching format for full texts
    if (currentSource.getName().toLowerCase().equals("escidoc")) {
      this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
          new SelectItem[] {
              new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_ALL,
                  getLabel("easy_submission_lblFulltext_all")),
              new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                  getLabel("easy_submission_lblFulltext_none"))});
      this.getEasySubmissionSessionBean().setRadioSelectFulltext(
          this.getEasySubmissionSessionBean().FULLTEXT_ALL);
    } else {
      if (ftFormats.size() > 1) {
        this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
            new SelectItem[] {
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT, this
                    .getEasySubmissionSessionBean().getCurrentFTLabel()),
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_ALL,
                    getLabel("easy_submission_lblFulltext_all")),
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT);
      }
      if (ftFormats.size() == 1) {
        this.getEasySubmissionSessionBean().setFULLTEXT_OPTIONS(
            new SelectItem[] {
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT, this
                    .getEasySubmissionSessionBean().getCurrentFTLabel()),
                new SelectItem(this.getEasySubmissionSessionBean().FULLTEXT_NONE,
                    getLabel("easy_submission_lblFulltext_none"))});
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_DEFAULT);
      }
      if (ftFormats.size() <= 0) {
        this.getEasySubmissionSessionBean().setRadioSelectFulltext(
            this.getEasySubmissionSessionBean().FULLTEXT_NONE);
        this.getEasySubmissionSessionBean().setFulltext(false);
      }
    }
    this.getEasySubmissionSessionBean().setCurrentExternalServiceType(newImportSource);

    // return "loadNewFetchMetadata";
  }

  public void changeImportSourceListener(ValueChangeEvent evt) {
    if (evt.getNewValue() != null) {
      changeImportSource((String) evt.getNewValue());
    }
  }

  private void setBibTexInfo() {
    // this.getEasySubmissionSessionBean().setREFERENCE_OPTIONS(
    // new SelectItem[] { new SelectItem(this.REFERENCE_FILE,
    // getLabel("easy_submission_lblReference_file")),
    // new SelectItem(this.REFERENCE_LOCATOR, getLabel("easy_submission_lblReference_locator")) });
  }

  /**
   * This method selects the import method 'fetch metadata from external systems'
   * 
   * @return String navigation string
   */
  public String selectImportExternal() {
    // this.sourceSelect.setSubmittedValue(this.getEasySubmissionSessionBean().getCurrentExternalServiceType());
    this.changeImportSource(this.getEasySubmissionSessionBean().getCurrentExternalServiceType());
    this.getEasySubmissionSessionBean().setImportMethod(
        EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL);
    return "loadNewEasySubmission";
  }

  /**
   * This method selects the import method 'Upload Bibtex file'
   * 
   * @return String navigation string
   */
  public String selectImportBibtex() {
    this.setBibTexInfo();
    // this.getEasySubmissionSessionBean().setFulltext(false);
    this.getEasySubmissionSessionBean().setImportMethod(
        EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX);
    return "loadNewEasySubmission";
  }

  /**
   * returns a flag which sets the fields of the import method 'fetch metadata from external
   * systems' to disabled or not
   * 
   * @return boolean the flag for disabling
   */
  public boolean getDisableExternalFields() {
    boolean disable = false;
    if (this.getEasySubmissionSessionBean().getImportMethod()
        .equals(EasySubmissionSessionBean.IMPORT_METHOD_BIBTEX)) {
      disable = true;
    }
    return disable;
  }

  /**
   * returns a flag which sets the fields of the import method 'Upload Bibtex file' to disabled or
   * not
   * 
   * @return boolean the flag for disabling
   */
  public boolean getDisableBibtexFields() {
    boolean disable = false;
    if (this.getEasySubmissionSessionBean().getImportMethod()
        .equals(EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL)) {
      disable = true;
    }
    return disable;
  }

  /**
   * Returns the CollectionListSessionBean.
   * 
   * @return a reference to the scoped data bean (CollectionListSessionBean)
   */
  protected ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
  }

  /**
   * Returns the EasySubmissionSessionBean.
   * 
   * @return a reference to the scoped data bean (EasySubmissionSessionBean)
   */
  protected EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
  }

  /**
   * Returns the EditItemSessionBean.
   * 
   * @return a reference to the scoped data bean (EditItemSessionBean)
   */
  protected EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
  }

  /**
   * Returns the ItemControllerSessionBean.
   * 
   * @return a reference to the scoped data bean (ItemControllerSessionBean)
   */
  protected ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  /**
   * localized creation of SelectItems for the genres available.
   * 
   * @return SelectItem[] with Strings representing genres.
   */
  public SelectItem[] getGenres() {
    List<MdsPublicationVO.Genre> allowedGenres = null;
    List<AdminDescriptorVO> adminDescriptors =
        this.getItemControllerSessionBean().getCurrentContext().getAdminDescriptors();
    for (AdminDescriptorVO adminDescriptorVO : adminDescriptors) {
      if (adminDescriptorVO instanceof PublicationAdminDescriptorVO) {
        allowedGenres = ((PublicationAdminDescriptorVO) adminDescriptorVO).getAllowedGenres();
        return this.getI18nHelper().getSelectItemsForEnum(false,
            allowedGenres.toArray(new MdsPublicationVO.Genre[] {}));
      }
    }
    return null;
  }

  /**
   * This method changes the Genre and sets the needed property file for genre specific Metadata
   * 
   * @return String null
   */
  public String changeGenre() {
    String newGenre = this.genreSelect.getSubmittedValue().toString();
    if (newGenre != null && newGenre.trim().equals("")) {
      newGenre = "ARTICLE";
      getItem().getMetadata().setGenre(Genre.ARTICLE);
    }
    this.getEasySubmissionSessionBean().setGenreBundle("Genre_" + newGenre);
    this.init();
    return null;
  }

  public SelectItem[] getSUBMISSION_METHOD_OPTIONS() {
    return this.SUBMISSION_METHOD_OPTIONS;
  }

  public void setSUBMISSION_METHOD_OPTIONS(SelectItem[] submission_method_options) {
    this.SUBMISSION_METHOD_OPTIONS = submission_method_options;
  }

  public SelectItem[] getDATE_TYPE_OPTIONS() {
    return this.DATE_TYPE_OPTIONS;
  }

  public void setDATE_TYPE_OPTIONS(SelectItem[] date_type_options) {
    this.DATE_TYPE_OPTIONS = date_type_options;
  }

  public SelectItem[] getEXTERNAL_SERVICE_OPTIONS() {
    return this.EXTERNAL_SERVICE_OPTIONS;
  }

  public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options) {
    this.EXTERNAL_SERVICE_OPTIONS = external_service_options;
  }

  public HtmlSelectOneRadio getRadioSelect() {
    return this.radioSelect;
  }

  public void setRadioSelect(HtmlSelectOneRadio radioSelect) {
    this.radioSelect = radioSelect;
  }

  /*
   * public PubItemVO getItem() { return this.getEasySubmissionSessionBean().getCurrentItem(); }
   * public void setItem(PubItemVO item) { this.getEasySubmissionSessionBean().setCurrentItem(item);
   * }
   */
  public PubItemVO getItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  public void setItem(PubItemVOPresentation item) {
    this.getItemControllerSessionBean().setCurrentPubItem(item);
  }

  public List<PubFileVOPresentation> getFiles() {
    return this.getEasySubmissionSessionBean().getFiles();
  }

  public List<PubFileVOPresentation> getLocators() {
    return this.getEasySubmissionSessionBean().getLocators();
  }

  public void setFiles(List<PubFileVOPresentation> files) {
    this.getEasySubmissionSessionBean().setFiles(files);
  }

  public void setLocators(List<PubFileVOPresentation> files) {
    this.getEasySubmissionSessionBean().setLocators(files);
  }

  public UploadedFile getUploadedFile() {
    return this.uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }

  /*
   * public HtmlAjaxRepeat getFileIterator() { return this.fileIterator; }
   * 
   * public void setFileIterator(HtmlAjaxRepeat fileIterator) { this.fileIterator = fileIterator; }
   * 
   * public HtmlAjaxRepeat getLocatorIterator() { return this.locatorIterator; }
   * 
   * public void setLocatorIterator(HtmlAjaxRepeat locatorIterator) { this.locatorIterator =
   * locatorIterator; }
   */

  public String getSelectedDate() {
    return this.selectedDate;
  }

  public void setSelectedDate(String selectedDate) {
    this.selectedDate = selectedDate;
  }

  public HtmlSelectOneMenu getDateSelect() {
    return this.dateSelect;
  }

  public void setDateSelect(HtmlSelectOneMenu dateSelect) {
    this.dateSelect = dateSelect;
  }

  public String getServiceID() {
    return this.serviceID;
  }

  public void setServiceID(String serviceID) {
    this.serviceID = serviceID;
  }


  /**
   * Returns all options for visibility.
   * 
   * @return all options for visibility
   */
  public SelectItem[] getVisibilities() {
    return this.getI18nHelper().getSelectItemsVisibility(false);
  }

  /**
   * Returns all options for visibility.
   * 
   * @return all options for visibility
   */
  public SelectItem[] getLocatorVisibilities() {
    // return ((ApplicationBean)
    // getApplicationBean(ApplicationBean.class)).getSelectItemsVisibility(true);
    return this.locatorVisibilities;
  }

  /**
   * Returns all options for publication language.
   * 
   * @return all options for publication language
   */
  public SelectItem[] getPublicationLanguages() {
    return CommonUtils.getLanguageOptions();
  }

  /**
   * returns the first language entry of the publication as String
   * 
   * @return String the first language entry of the publication as String
   */
  public String getPublicationLanguage() {
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages()
        .get(0);
  }

  public void setPublicationLanguage(String language) {
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().clear();
    if (language != null) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages()
          .add(language);
    } else {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLanguages().add("");
    }
  }

  /**
   * returns the value of the first abstract of the publication
   * 
   * @return String the value of the first abstract of the publication
   */
  public String getAbstract() {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts() == null
        || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts()
            .size() < 1) {
      AbstractVO newAbstract = new AbstractVO();
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts()
          .add(newAbstract);
    }
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts()
        .get(0).getValue();
  }

  public void setAbstract(String publicationAbstract) {
    AbstractVO newAbstract = new AbstractVO();
    newAbstract.setValue(publicationAbstract);
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts().clear();
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getAbstracts()
        .add(newAbstract);
  }

  /**
   * returns the value of the first subject of the publication
   * 
   * @return String the value of the first subject of the publication
   */
  public String getSubject() {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects() == null
        || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects()
            .size() < 1) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects()
          .add(new SubjectVO());
    }
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects()
        .get(0).getValue();
  }

  public void setSubject(String publicationSubject) {
    SubjectVO newSubject = new SubjectVO(publicationSubject);
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects().clear();
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSubjects()
        .add(newSubject);
  }

  public String getFreeKeywords() {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getFreeKeywords() == null) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setFreeKeywords("");
    }
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getFreeKeywords();
  }

  public void setFreeKeywords(String publicationSubject) {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getFreeKeywords() == null) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setFreeKeywords("");
    }
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getFreeKeywords();
  }

  /**
   * Returns all options for content category.
   * 
   * @return all options for content category.
   */
  public SelectItem[] getContentCategories() {
    return this.getI18nHelper().getSelectItemsContentCategory(true);
  }

  /**
   * Returns the number of files attached to the current item
   * 
   * @return int the number of files
   */
  public int getNumberOfFiles() {
    int fileNumber = 0;
    if (this.getEasySubmissionSessionBean().getFiles() != null) {
      /*
       * for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++) { if
       * (this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(
       * PubFileVOPresentation.FILE_TYPE_FILE)) { fileNumber++; } }
       */
      fileNumber = this.getEasySubmissionSessionBean().getFiles().size();
    }
    return fileNumber;
  }

  /**
   * Returns the number of files attached to the current item
   * 
   * @return int the number of files
   */
  public int getNumberOfLocators() {
    int locatorNumber = 0;
    if (this.getEasySubmissionSessionBean().getFiles() != null) {
      /*
       * for (int i = 0; i < this.getEasySubmissionSessionBean().getFiles().size(); i++) { if
       * (this.getEasySubmissionSessionBean().getFiles().get(i).getFileType().equals(
       * PubFileVOPresentation.FILE_TYPE_LOCATOR)) { locatorNumber++; } }
       */
      locatorNumber = this.getEasySubmissionSessionBean().getLocators().size();
    }
    return locatorNumber;
  }

  /**
   * This method examines if the user has already selected a context for creating an item. If yes,
   * the 'Next' button will be enabled, otherwise disabled
   * 
   * @return boolean Flag if the 'Next' button should be enabled or disabled
   */
  public boolean getDisableNextButton() {
    boolean disableButton = true;
    int countSelectedContexts = 0;
    // examine if a context for creating the item has been selected
    if (this.getContextListSessionBean().getDepositorContextList() != null) {
      for (int i = 0; i < this.getContextListSessionBean().getDepositorContextList().size(); i++) {
        if (this.getContextListSessionBean().getDepositorContextList().get(i).getSelected() == true) {
          countSelectedContexts++;
        }
      }
    }
    if (countSelectedContexts > 0) {
      disableButton = false;
    }
    return disableButton;
  }

  public String getSourceTitle() {
    String sourceTitle = "";
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources() == null
        || this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources()
            .size() < 1) {
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources()
          .add(new SourceVO());
    }
    // return the title value oif the first source
    sourceTitle =
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
            .getTitle();
    return sourceTitle;
  }

  public void setSourceTitle(String title) {
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
        .setTitle(title);
  }

  public String getSourcePublisher() {
    // Create new Publishing Info if not available yet
    SourceVO source =
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0);
    PublishingInfoVO pubVO;
    if (source.getPublishingInfo() == null) {
      pubVO = new PublishingInfoVO();
      source.setPublishingInfo(pubVO);
    } else {
      pubVO = source.getPublishingInfo();
    }
    return pubVO.getPublisher();
  }

  public void setSourcePublisher(String publisher) {
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
        .getPublishingInfo().setPublisher(publisher);
  }

  public String getSourcePublisherPlace() {
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources()
        .get(0).getPublishingInfo().getPlace();
  }

  public void setSourcePublisherPlace(String place) {
    this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
        .getPublishingInfo().setPlace(place);
  }

  public String getSourceIdentifier() {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
        .getIdentifiers().size() == 0) {
      IdentifierVO identifier = new IdentifierVO();
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
          .getIdentifiers().add(identifier);
    }
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources()
        .get(0).getIdentifiers().get(0).getId();
  }

  public void setSourceIdentifier(String id) {
    PubItemVO pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
    pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setId(id);
    if (!id.trim().equals("")) {
      pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setType(IdType.OTHER);
    }
  }

  /* JUS BEGIN */
  public String getLegalCaseTitle() {
    return null;// return
                // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().getTitle();
  }

  public void setLegalCaseTitle(String legalCaseTitle) {
    // logger.info("legalCAse Title " + legalCaseTitle);
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().setTitle(legalCaseTitle);
  }

  public String getLegalCaseIdentifier() {
    return null; // return
                 // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().getIdentifier();
  }

  public void setLegalCaseIdentifier(String legalCaseIdentifier) {
    // logger.info("legalCAse ID " + legalCaseIdentifier);
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().setIdentifier(legalCaseIdentifier);
  }

  public String getLegalCaseCourtName() {
    // if
    // (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().getCourtName()
    // ==
    // null){
    // LegalCaseVO legalCase = new LegalCaseVO();
    // logger.info("create new legal case  " );
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().setLegalCase(legalCase);
    // }
    // return
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().getCourtName();
    return null;
  }

  public void setLegalCaseCourtName(String legalCaseCourtName) {
    // logger.info("legalCAse Court  " + legalCaseCourtName);
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().setCourtName(legalCaseCourtName);
  }

  public String getLegalCaseDatePublished() {
    return null; // return
                 // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().getDatePublished();
  }

  public void setLegalCaseDatePublished(String legalCaseDatePublished) {
    // logger.info("legalCAse Date Pub " + legalCaseDatePublished);
    // this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getLegalCase().setDatePublished(legalCaseDatePublished);
  }

  // source identifier
  public void setSourceIdentifierType(String typeString) {
    logger.debug("typeString " + typeString);
    if (typeString != null) {
      PubItemVO pubItem = this.getItemControllerSessionBean().getCurrentPubItem();
      pubItem.getMetadata().getSources().get(0).getIdentifiers().get(0).setTypeString(typeString);
    }

  }

  public String getSourceIdentifierType() {
    if (this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
        .getIdentifiers().size() == 0) {
      IdentifierVO identifier = new IdentifierVO();
      this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources().get(0)
          .getIdentifiers().add(identifier);
    }
    return this.getItemControllerSessionBean().getCurrentPubItem().getMetadata().getSources()
        .get(0).getIdentifiers().get(0).getTypeString();
  }

  /* JUS END */
  /**
   * localized creation of SelectItems for the source genres available
   * 
   * @return SelectItem[] with Strings representing source genres
   */
  public SelectItem[] getSourceGenreOptions() {
    InternationalizationHelper i18nHelper =
        (InternationalizationHelper) FacesContext
            .getCurrentInstance()
            .getApplication()
            .getVariableResolver()
            .resolveVariable(FacesContext.getCurrentInstance(),
                InternationalizationHelper.BEAN_NAME);
    ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

    ApplicationBean appBean = (ApplicationBean) getApplicationBean(ApplicationBean.class);
    Map<String, String> excludedSourceGenres = appBean.getExcludedSourceGenreMap();
    List<SelectItem> sourceGenres = new ArrayList<SelectItem>();
    sourceGenres.add(new SelectItem("", bundleLabel.getString("EditItem_NO_ITEM_SET")));
    for (SourceVO.Genre value : SourceVO.Genre.values()) {
      sourceGenres.add(new SelectItem(value, bundleLabel.getString("ENUM_GENRE_" + value.name())));
    }

    String uri = "";
    int i = 0;
    while (i < sourceGenres.size()) {
      if (sourceGenres.get(i).getValue() != null && !("").equals(sourceGenres.get(i).getValue())) {
        uri = ((SourceVO.Genre) sourceGenres.get(i).getValue()).getUri();
      }

      if (excludedSourceGenres.containsValue(uri)) {
        sourceGenres.remove(i);
      } else {
        i++;
      }
    }
    return sourceGenres.toArray(new SelectItem[sourceGenres.size()]);
  }

  public SourceVO getSource() {
    SourceVO source = null;
    if (this.getItem().getMetadata().getSources() != null
        && this.getItem().getMetadata().getSources().size() > 0) {
      source = this.getItem().getMetadata().getSources().get(0);
    }
    return source;
  }

  public void setSource(SourceVO source) {
    if (this.getItem().getMetadata().getSources() != null
        && this.getItem().getMetadata().getSources().size() > 0) {
      this.getItem().getMetadata().getSources().set(0, source);
    }
  }

  public HtmlMessages getValMessage() {
    return this.valMessage;
  }

  public void setValMessage(HtmlMessages valMessage) {
    this.valMessage = valMessage;
  }

  public boolean getFromEasySubmission() {
    return this.fromEasySubmission;
  }

  public void setFromEasySubmission(boolean fromEasySubmission) {
    this.fromEasySubmission = fromEasySubmission;
  }

  public void setCreatorParseString(String creatorParseString) {
    this.getEasySubmissionSessionBean().setCreatorParseString(creatorParseString);
  }

  public String getCreatorParseString() {
    return this.getEasySubmissionSessionBean().getCreatorParseString();
  }

  public String addCreatorString() {
    try {
      getEasySubmissionSessionBean().parseCreatorString(getCreatorParseString(), null,
          getEasySubmissionSessionBean().getOverwriteCreators());
      setCreatorParseString("");
      getEasySubmissionSessionBean().initAuthorCopyPasteCreatorBean();
      return "loadNewEasySubmission";
    } catch (Exception e) {
      error(getMessage("ErrorParsingCreatorString"));
      return "loadNewEasySubmission";
    }
  }

  /*
   * public HtmlSelectOneMenu getSourceSelect() { return this.sourceSelect; }
   * 
   * public void setSourceSelect(HtmlSelectOneMenu sourceSelect) { this.sourceSelect = sourceSelect;
   * }
   */

  public SelectItem[] getFULLTEXT_OPTIONS() {
    return this.FULLTEXT_OPTIONS;
  }

  public void setFULLTEXT_OPTIONS(SelectItem[] fulltext_options) {
    this.FULLTEXT_OPTIONS = fulltext_options;
  }

  public HtmlSelectOneRadio getRadioSelectFulltext() {
    return this.radioSelectFulltext;
  }

  public void setRadioSelectFulltext(HtmlSelectOneRadio radioSelectFulltext) {
    this.radioSelectFulltext = radioSelectFulltext;
  }

  public SelectItem[] getREFERENCE_OPTIONS() {
    return this.REFERENCE_OPTIONS;
  }

  public void setREFERENCE_OPTIONS(SelectItem[] reference_options) {
    this.REFERENCE_OPTIONS = reference_options;
  }

  /*
   * public void chooseSourceGenre(ValueChangeEvent event) { String sourceGenre =
   * event.getNewValue().toString(); //System.out.println(sourceGenre);
   * if(sourceGenre.equals(SourceVO.Genre.JOURNAL.toString())) { this.setAutosuggestJournals(true);
   * } }
   */
  /*
   * public String chooseSourceGenre() { if (this.getSource().getGenre() != null &&
   * this.getSource().getGenre().equals(SourceVO.Genre.JOURNAL)) { this.autosuggestJournals = true;
   * } else { this.autosuggestJournals = false; } return ""; }
   */

  /**
   * This method returns the URL to the cone autosuggest service read from the properties
   * 
   * @author Tobias Schraut
   * @return String the URL to the cone autosuggest service
   * @throws Exception
   */
  public String getSuggestConeUrl() throws Exception {
    if (suggestConeUrl == null) {
      suggestConeUrl = PropertyReader.getProperty("escidoc.cone.service.url");
    }
    return suggestConeUrl;
  }

  /*
   * 
   * public void setAutosuggestJournals(boolean autosuggestJournals) { this.autosuggestJournals =
   * autosuggestJournals; }
   * 
   * public boolean isAutosuggestJournals() { return autosuggestJournals; }
   */

  /**
   * Returns all options for degreeType.
   * 
   * @return all options for degreeType
   */
  public SelectItem[] getDegreeTypes() {
    return this.getI18nHelper().getSelectItemsDegreeType(true);
  }

  /*
   * public HtmlAjaxRepeat getCreatorIterator() { return creatorIterator; }
   * 
   * public void setCreatorIterator(HtmlAjaxRepeat creatorIterator) { this.creatorIterator =
   * creatorIterator; }
   */

  public void setOverwriteCreators(boolean overwriteCreators) {
    this.overwriteCreators = overwriteCreators;
  }

  public boolean getOverwriteCreators() {
    return overwriteCreators;
  }

  public void setHiddenAlternativeTitlesField(String hiddenAlternativeTitlesField) {
    this.hiddenAlternativeTitlesField = hiddenAlternativeTitlesField;
  }

  public String getHiddenAlternativeTitlesField() {
    return hiddenAlternativeTitlesField;
  }

  public void setHiddenIdsField(String hiddenIdsField) {
    this.hiddenIdsField = hiddenIdsField;
  }

  public String getHiddenIdsField() {
    return hiddenIdsField;
  }

  /**
   * Takes the text from the hidden input fields, splits it using the delimiter and adds them to the
   * item. Format of alternative titles: alt title 1 ||##|| alt title 2 ||##|| alt title 3 Format of
   * ids: URN|urn:221441 ||##|| URL|http://www.xwdc.de ||##|| ESCIDOC|escidoc:21431
   * 
   * @return
   */
  public String parseAndSetAlternativeSourceTitlesAndIds() {
    if (getHiddenAlternativeTitlesField() != null
        && !getHiddenAlternativeTitlesField().trim().equals("")) {
      SourceVO source = getSource();
      source.getAlternativeTitles().clear();
      source.getAlternativeTitles().addAll(
          SourceBean.parseAlternativeTitles(getHiddenAlternativeTitlesField()));
    }
    if (getHiddenIdsField() != null && !getHiddenIdsField().trim().equals("")) {
      List<IdentifierVO> identifiers = getSource().getIdentifiers();
      identifiers.clear();
      identifiers.addAll(SourceBean.parseIdentifiers(getHiddenIdsField()));
    }
    return "";
  }

  public void setIdentifierCollection(IdentifierCollection identifierCollection) {
    this.identifierCollection = identifierCollection;
  }

  public IdentifierCollection getIdentifierCollection() {
    return identifierCollection;
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @return true if invitationstatus in VO is set, else false
   */
  public boolean getInvited() {
    boolean retVal = false;
    // Changed by FrM: Check for event
    if (this.getItem().getMetadata().getEvent() != null
        && this.getItem().getMetadata().getEvent().getInvitationStatus() != null
        && this.getItem().getMetadata().getEvent().getInvitationStatus()
            .equals(EventVO.InvitationStatus.INVITED)) {
      retVal = true;
    }
    return retVal;
  }

  /**
   * Invitationstatus of event has to be converted as it's an enum that is supposed to be shown in a
   * checkbox.
   * 
   * @param invited the value of the checkbox
   */
  public void setInvited(boolean invited) {
    if (invited) {
      this.getItem().getMetadata().getEvent().setInvitationStatus(EventVO.InvitationStatus.INVITED);
    } else {
      this.getItem().getMetadata().getEvent().setInvitationStatus(null);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Invitationstatus in VO has been set to: '"
          + this.getItem().getMetadata().getEvent().getInvitationStatus() + "'");
    }
  }

  /*
   * public void setIdentifierIterator(HtmlAjaxRepeat identifierIterator) { this.identifierIterator
   * = identifierIterator; }
   * 
   * 
   * public HtmlAjaxRepeat getIdentifierIterator() { return identifierIterator; }
   */

  public HtmlSelectOneMenu getGenreSelect() {
    return genreSelect;
  }

  public void setGenreSelect(HtmlSelectOneMenu genreSelect) {
    this.genreSelect = genreSelect;
  }

  public String getContextName() {
    if (this.contextName == null) {
      try {
        ContextVO context =
            this.getItemControllerSessionBean().retrieveContext(
                this.getItem().getContext().getObjectId());
        this.contextName = context.getName();
        return this.contextName;
      } catch (Exception e) {
        logger.error("Could not retrieve the requested context." + "\n" + e.toString());
        ((ErrorPage) getSessionBean(ErrorPage.class)).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
    }
    return this.contextName;
  }

  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  /**
   * Uploads a file from a given locator.
   */
  public void uploadLocator() {
    LocatorUploadBean locatorBean = new LocatorUploadBean();
    boolean check = locatorBean.checkLocator(this.getLocatorUpload());
    if (check) {
      locatorBean.locatorUploaded();
    }
    if (locatorBean.getError() != null) {
      // if (check)
      // {
      // //Reset locator if it was already added to list
      // locatorBean.removeLocator();
      // List <PubFileVOPresentation> list = this.getLocators();
      // list.get(indexUpload).setLocator(locatorValue);
      // this.setLocators(list);
      // }
      error(getMessage("errorLocatorMain").replace("$1", locatorBean.getError()));
    } else {
      setLocatorUpload("");
    }
  }

  /**
   * This method replaces forward and backslases in a given String (e.g. in a filename) with an
   * underscore
   * 
   * @param fileName
   * @return String the cleaned String
   */
  private String replaceSlashes(String fileName) {
    String newFileName = "";
    if (fileName != null) {
      // replace forward slahes
      newFileName = fileName.replaceAll("\\/", "_");
      // replace back slashes
      newFileName = newFileName.replaceAll("\\\\", "_");
    }
    return newFileName;
  }

  public String getLocatorUpload() {
    return locatorUpload;
  }

  public void setLocatorUpload(String locatorUpload) {
    this.locatorUpload = locatorUpload;
  }

  /**
   * @return the alternativeLanguageName
   */
  public String getAlternativeLanguageName() {
    return alternativeLanguageName;
  }

  /**
   * @param alternativeLanguageName the alternativeLanguageName to set
   */
  public void setAlternativeLanguageName(String alternativeLanguageName) {
    this.alternativeLanguageName = alternativeLanguageName;
  }

  public void clearBibtexFile(ActionEvent evt) {
    getEasySubmissionSessionBean().setUploadedBibtexFile(null);
  }


}
