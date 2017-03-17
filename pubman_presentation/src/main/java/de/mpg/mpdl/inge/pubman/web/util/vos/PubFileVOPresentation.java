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

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.pubman.SimpleStatisticsService;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmissionSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 * Presentation wrapper for {@link FileVO}.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class PubFileVOPresentation extends FacesBean {
  private static Properties properties;

  private FileVO file;
  private List<GrantVOPresentation> grantList = new ArrayList<GrantVOPresentation>();
  private String fileType;
  private boolean isLocator = false;
  private int index;

  public PubFileVOPresentation() {
    this.file = new FileVO();
    this.file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    this.init();
  }

  public PubFileVOPresentation(int fileIndex, boolean isLocator) {
    this.file = new FileVO();
    this.index = fileIndex;
    this.isLocator = isLocator;
    if (isLocator) {
      this.file.setStorage(FileVO.Storage.EXTERNAL_URL);
    } else {
      this.file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
    }
    this.init();
  }

  public PubFileVOPresentation(int fileIndex, FileVO file) {
    this.index = fileIndex;
    this.file = file;

    this.init();
  }

  public PubFileVOPresentation(int fileIndex, FileVO file, boolean isLocator) {
    this.index = fileIndex;
    this.file = file;
    this.isLocator = isLocator;
    this.init();
  }

  public void init() {
    setVisibility();
  }

  /**
   * get all available content categories as Map for this (server-) instance, depending on the
   * content_categories.properties definitions
   * 
   * @return Map filled with all content Categories
   */
  public static Map<String, String> getContentCategoryMap() {
    if (properties == null || properties.isEmpty()) {
      properties = loadContentCategoryProperties();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    Map<String, String> propertiesMap = new HashMap<String, String>((Map) properties);
    return propertiesMap;
  }

  /**
   * 
   * @param key for which the content category URI will be returned
   * @return URI depending on the key of the content category
   */
  public static String getContentCategoryUri(String key) {
    if (properties == null || properties.isEmpty()) {
      properties = loadContentCategoryProperties();
    }
    String value = properties.getProperty(key.toLowerCase());
    if (value != null) {
      return value;
    } else {
      error("There is no such content category defined (" + key + ")");
      Logger.getLogger(PubFileVOPresentation.class).warn(
          "WARNING: content-category \"" + key + "\" has not been defined valid in Genres.xml");
      return null;
    }
  }

  /**
   * get all available content categories as properties for this (server-) instance, depending on
   * the content_categories.properties definitions
   * 
   * @return Properties filled with all content Categories
   */
  private static Properties loadContentCategoryProperties() {
    properties = new Properties();
    URL contentCategoryURI = null;
    try {
      contentCategoryURI =
          PubFileVOPresentation.class.getClassLoader().getResource("content_categories.properties");
      if (contentCategoryURI != null) {
        Logger.getLogger(PubFileVOPresentation.class).info(
            "Content-category properties URI is " + contentCategoryURI.toString());
        InputStream in = contentCategoryURI.openStream();
        properties.load(in);
        properties.putAll(properties);
        in.close();

        Logger.getLogger(PubFileVOPresentation.class).info(
            "Content-category properties loaded from " + contentCategoryURI.toString());
      } else {
        Logger.getLogger(PubFileVOPresentation.class).debug(
            "Content-category properties file not found.");
      }
    } catch (Exception e) {
      Logger.getLogger(PubFileVOPresentation.class).warn(
          "WARNING: content-category properties not found: " + e.getMessage());
    }
    return properties;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public FileVO getFile() {
    return file;
  }

  public void setFile(FileVO file) {
    this.file = file;
  }

  public boolean getIsLocator() {
    return isLocator;
  }

  public void setLocator(boolean isLocator) {
    this.isLocator = isLocator;
  }

  public String getFileType() {
    return fileType;
  }

  /**
   * Returns an internationalized String for the file's content category.
   * 
   * @return The internationalized content-category.
   */
  public String getContentCategory() {
    if (this.file.getContentCategory() != null) {
      @SuppressWarnings({"unchecked", "rawtypes"})
      Map<String, String> propertiesMap = new HashMap<String, String>((Map) properties);
      for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
        if (entry.getValue().equals(this.file.getContentCategory())) {
          return getLabel("ENUM_CONTENTCATEGORY_" + entry.getKey().toLowerCase().replace("_", "-"));
        }
      }
    }

    return "";
  }

  /**
   * Returns an string according to XML conventions.
   * 
   * @return The content category of the file.
   */
  public String getContentCategoryAsXmlString() {
    return this.file.getContentCategory();
  }

  /**
   * Sets the content category of the file.
   * 
   * @param category The content category as a string according to XML conventions.
   */
  public void setContentCategoryAsXmlString(String category) {
    this.file.setContentCategory(category);
  }

  /**
   * Return the file size.
   * 
   * @return The number of bytes.
   */
  public int getSize() {
    if (this.file.getDefaultMetadata() != null) {
      return this.file.getDefaultMetadata().getSize();
    }

    return 0;
  }

  public String getDescription() {
    if (this.file.getDefaultMetadata() != null) {
      return this.file.getDefaultMetadata().getDescription();
    }

    return "";
  }

  public void setDescription(String description) {
    if (this.file.getDefaultMetadata() != null) {
      this.file.getDefaultMetadata().setDescription(description);
    } else {
      this.file.getMetadataSets().add(new MdsFileVO());
      this.file.getDefaultMetadata().setDescription(description);
    }
  }

  public String getVisibility() {
    String visibility = "";
    if (this.file.getVisibility() != null) {
      visibility = getLabel(getI18nHelper().convertEnumToString(this.file.getVisibility()));
    } else {
      this.file.setVisibility(FileVO.Visibility.PUBLIC);
      visibility = getLabel(getI18nHelper().convertEnumToString(this.file.getVisibility()));
    }

    return visibility;
  }

  private void setVisibility() {
    if (this.file.getVisibility() == null) {
      this.file.setVisibility(FileVO.Visibility.PUBLIC);
    }
  }

  public void setMimeType(String mimeType) {
    if (this.file.getDefaultMetadata() == null) {
      this.file.getMetadataSets().add(new MdsFileVO());
    }

    // set in properties
    this.file.setMimeType(mimeType);

    List<FormatVO> formats = this.file.getDefaultMetadata().getFormats();
    boolean found = false;
    for (FormatVO formatVO : formats) {
      if ("dcterms:IMT".equals(formatVO.getType())) {
        formatVO.setValue(mimeType);
        found = true;
        break;
      }
    }
    if (!found) {
      FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(mimeType);
      formats.add(formatVO);
    }
  }

  public String getMimeType() {
    if (this.file.getDefaultMetadata() == null) {
      return null;
    } else {

      List<FormatVO> formats = this.file.getDefaultMetadata().getFormats();
      for (FormatVO formatVO : formats) {
        if ("dcterms:IMT".equals(formatVO.getType())) {
          return formatVO.getValue();
        }
      }
    }
    return null;
  }

  public String getLocator() {
    String locator = "";
    if (getIsLocator()) {
      locator = this.file.getContent();
    }
    return locator;
  }

  public void setLocator(String locator) {
    this.file.setContent(locator.trim());
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String removeFile() {
    EditItemSessionBean editItemSessionBean =
        (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);

    editItemSessionBean.getFiles().remove(this.index);

    // ensure that at least one file component is visible
    if (editItemSessionBean.getFiles().size() == 0) {
      FileVO newFile = new FileVO();
      newFile.getMetadataSets().add(new MdsFileVO());
      newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
      editItemSessionBean.getFiles().add(0, new PubFileVOPresentation(0, newFile, false));
    }

    editItemSessionBean.reorganizeFileIndexes();

    return null;
  }

  public String removeLocatorEditItem() {
    EditItemSessionBean editItemSessionBean =
        (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);

    editItemSessionBean.getLocators().remove(this.index);

    // ensure that at least one locator component is visible
    if (editItemSessionBean.getLocators().size() == 0) {
      FileVO newLocator = new FileVO();
      newLocator.getMetadataSets().add(new MdsFileVO());
      newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
      editItemSessionBean.getLocators().add(0, new PubFileVOPresentation(0, newLocator, true));
    }

    editItemSessionBean.reorganizeLocatorIndexes();

    return "loadEditItem";
  }

  public String removeFileEasySubmission() {
    this.getEasySubmissionSessionBean().getFiles().remove(this.index);
    this.getEasySubmission().reorganizeFileIndexes();
    this.getEasySubmission().init();

    return "loadNewEasySubmission";
  }

  public String removeLocatorEasySubmission() {
    this.getEasySubmissionSessionBean().getLocators().remove(this.index);
    this.getEasySubmission().reorganizeLocatorIndexes();
    this.getEasySubmission().init();

    return "loadNewEasySubmission";
  }

  public String getNumberOfFileDownloadsPerFileAllUsers() throws Exception {
    String fileID = file.getReference().getObjectId();

    String result =
        SimpleStatisticsService.getNumberOfItemOrFileRequests(
            SimpleStatisticsService.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS, fileID,
            getLoginHelper().getAccountUser());
    return result;
  }

  public String getNumberOfFileDownloadsPerFileAnonymousUsers() throws Exception {
    String fileID = file.getReference().getObjectId();
    String result =
        SimpleStatisticsService.getNumberOfItemOrFileRequests(
            SimpleStatisticsService.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, fileID,
            getLoginHelper().getAccountUser());
    return result;
  }

  /**
   * This Method evaluates if the embargo date input filed has to be displayed or not (yes, if
   * visibility is set to private or restricted)
   * 
   * @return boolean flag if embargo date input field should be displayed or not
   */
  public boolean getShowEmbargoDate() {
    boolean showEmbargoDate = false;
    if (FileVO.Visibility.PRIVATE.equals(file.getVisibility())
        || FileVO.Visibility.AUDIENCE.equals(file.getVisibility())) {
      showEmbargoDate = true;
    } else {
      file.getDefaultMetadata().setEmbargoUntil(null);
      showEmbargoDate = false;
    }

    return showEmbargoDate;
  }

  /**
   * public String addGrant() { GrantVO newGrant = new GrantVO(); newGrant.setObjectRef("");
   * newGrant.setGrantType(GrantVOPresentation.GRANT_TYPE_USER_GROUP);
   * newGrant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
   * newGrant.setAssignedOn(this.file.getReference().getObjectId()); this.getGrantList().add( new
   * GrantVOPresentation(newGrant, this.getGrantList().size(), this.index)); return
   * AudienceBean.LOAD_AUDIENCEPAGE; }
   */

  /**
   * This method updates the file's visibility with the new one selected by the user
   * 
   * @param event The value change event
   */
  public void setUpdateVisibility(ValueChangeEvent event) {
    Visibility newVisibility = (Visibility) event.getNewValue();
    this.file.setVisibility(newVisibility);
  }

  public List<GrantVOPresentation> getGrantList() {
    // ensure that at least one grant is in the list (for presentation)
    /*
     * if(this.grantList.size() == 0) { this.grantList.add(new GrantVOPresentation(new Grant(),
     * this.grantList.size(), this.index)); }
     */
    return this.grantList;
  }

  public void setGrantList(List<GrantVOPresentation> grantList) {
    this.grantList = grantList;
  }

  protected EasySubmission getEasySubmission() {
    return (EasySubmission) getSessionBean(EasySubmission.class);
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
  }
}
