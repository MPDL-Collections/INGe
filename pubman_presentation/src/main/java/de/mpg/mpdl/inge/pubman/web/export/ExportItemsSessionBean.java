/*
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

package de.mpg.mpdl.inge.pubman.web.export;

import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Superclass for keeping the attributes used d�ring the session by ExportItems.
 * 
 * @author: Galina Stancheva, created 02.08.2007
 * @version: $Revision$ $LastChangedDate$ Revised by StG: 28.09.2007 - Comments for the get- and
 *           set-methods are missing! ToDo StG.
 */
@ManagedBean(name = "ExportItemsSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class ExportItemsSessionBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ExportItemsSessionBean.class);

  public String exportDisplayData = "No export data available";

  private String message = null;

  private String exportFormatType = "LAYOUT";
  private String exportFormatName = "APA";
  private final ExportFormatVO curExportFormatVO = new ExportFormatVO();
  private final FileFormatVO curFileFormatVO = new FileFormatVO();

  private boolean enableFileFormats = true;
  private boolean enableExport = true;
  private boolean enableCslAutosuggest = false;

  // email properties
  private File attExportFile = null;
  private String attExportFileName = "ExportFile";
  private String exportEmailTxt = ExportItems.MESSAGE_EXPORT_EMAIL_TEXT;
  private String exportEmailSubject = ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT;
  private String exportEmailReplyToAddr = "";
  private String emailRecipients = "";
  private String emailCCRecipients = "";
  private String emailBCCRecipients = "";
  private String emailSenderProp = "";
  private String emailServernameProp = "";
  private String emailWithAuthProp = "";
  private String emailAuthUserProp = "";
  private String emailAuthPwdProp = "";
  private String citationStyleName = "";

  private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_SERVERNAME = "inge.email.mailservername";
  private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_SENDER = "inge.email.sender";
  private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_WITHAUTHENTICATION = "inge.email.withauthentication";
  private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHUSER = "inge.email.authenticationuser";
  private final String PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHPWD = "inge.email.authenticationpwd";

  public ExportItemsSessionBean() {
    this.init();
  }

  public void init() {
    if (this.exportFormatType.equals("LAYOUT")) {
      this.curExportFormatVO.setFormatType(ExportFormatVO.FormatType.LAYOUT);
      // default format for STRUCTURED is pdf
      this.curFileFormatVO.setName(FileFormatVO.PDF_NAME);
      this.curFileFormatVO.setMimeType(FileFormatVO.PDF_MIMETYPE);
    } else {
      this.curExportFormatVO.setFormatType(ExportFormatVO.FormatType.STRUCTURED);
      // default format for STRUCTURED is TEXT
      this.curFileFormatVO.setName(FileFormatVO.TEXT_NAME);
      this.curFileFormatVO.setMimeType(FileFormatVO.TEXT_MIMETYPE);
    }
    this.curExportFormatVO.setName(this.exportFormatName);
    this.curExportFormatVO.setSelectedFileFormat(this.curFileFormatVO);

    try {
      this.emailSenderProp = PropertyReader.getProperty(this.PROPERTY_PREFIX_FOR_EMAILSERVICE_SENDER);
      this.emailServernameProp = PropertyReader.getProperty(this.PROPERTY_PREFIX_FOR_EMAILSERVICE_SERVERNAME);
      this.emailWithAuthProp = PropertyReader.getProperty(this.PROPERTY_PREFIX_FOR_EMAILSERVICE_WITHAUTHENTICATION);
      this.emailAuthUserProp = PropertyReader.getProperty(this.PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHUSER);
      this.emailAuthPwdProp = PropertyReader.getProperty(this.PROPERTY_PREFIX_FOR_EMAILSERVICE_AUTHPWD);
    } catch (final Exception e) {
      ExportItemsSessionBean.logger.warn("Propertyfile not readable for emailserver  properties'");
    }
  }

  public ExportFormatVO getCurExportFormatVO() {
    return this.curExportFormatVO;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getExportFormatType() {
    return this.exportFormatType;
  }

  public void setExportFormatType(String exportFormatType) {
    this.exportFormatType = exportFormatType;
  }

  public String getExportFormatName() {
    return this.curExportFormatVO.getName();
  }

  public void setExportFormatName(String exportFormatName) {
    this.exportFormatName = exportFormatName;
    this.curExportFormatVO.setName(exportFormatName);

    if ("APA".equalsIgnoreCase(exportFormatName) //
        || "AJP".equalsIgnoreCase(exportFormatName) //
        || "JUS".equalsIgnoreCase(exportFormatName) //
        || "APA(CJK)".equalsIgnoreCase(exportFormatName)) {
      this.curExportFormatVO.setFormatType(FormatType.LAYOUT);
      this.exportFormatType = FormatType.LAYOUT.toString();
      this.setEnableFileFormats(true);
      this.setEnableCslAutosuggest(false);
    } else if ("CSL".equalsIgnoreCase(exportFormatName)) {
      this.curExportFormatVO.setFormatType(FormatType.LAYOUT);;
      this.exportFormatType = FormatType.LAYOUT.toString();
      this.setEnableFileFormats(true);
      this.setEnableCslAutosuggest(true);
    } else {
      this.curExportFormatVO.setFormatType(FormatType.STRUCTURED);
      this.exportFormatType = FormatType.STRUCTURED.toString();
      this.setEnableFileFormats(false);
      this.setEnableCslAutosuggest(false);
    }
  }

  public String getFileFormat() {
    return this.curExportFormatVO.getSelectedFileFormat().getName();
  }

  public void setFileFormat(String fileFormat) {
    if (fileFormat == null || fileFormat.trim().equals("") || this.getExportFormatName().equalsIgnoreCase("ENDNOTE")
        || this.getExportFormatName().equalsIgnoreCase("BIBTEX")) {
      fileFormat = FileFormatVO.TEXT_NAME;
    }

    this.curFileFormatVO.setName(fileFormat);
    this.curFileFormatVO.setMimeType(FileFormatVO.getMimeTypeByName(fileFormat));
    this.curExportFormatVO.setSelectedFileFormat(this.curFileFormatVO);

    ExportItemsSessionBean.logger.debug("setFileFormat.....:" + this.curExportFormatVO.getSelectedFileFormat().getName() + ";"
        + this.curExportFormatVO.getSelectedFileFormat().getMimeType());

  }

  // ////////////////////////////////////////////////////////////////////////////////////////7
  // next methods are used by EMAIL-ing

  public void setAttExportFile(File attFile) {
    this.attExportFile = new File(attFile.toURI());
  }

  public File getAttExportFile() {
    return this.attExportFile;
  }

  public void setCitationStyleName(String citationStyleName) {
    this.citationStyleName = citationStyleName;
  }

  public String getCitationStyleName() {
    return this.citationStyleName;
  }

  public void setConeCitationStyleId(String citationStyleId) {
    this.curExportFormatVO.setId(citationStyleId);
  }

  public String getConeCitationStyleId() {
    return this.curExportFormatVO.getId();
  }

  public void setAttExportFileName(String fileName) {
    this.attExportFileName = fileName;
  }

  public String getAttExportFileName() {
    return this.attExportFileName;
  }

  public void setExportEmailSubject(String exportEmailSubject) {
    this.exportEmailSubject = exportEmailSubject;
  }

  public String getExportEmailSubject() {
    return this.exportEmailSubject;
  }

  public void setExportEmailTxt(String exportEmailTxt) {
    this.exportEmailTxt = exportEmailTxt;
  }

  public String getExportEmailTxt() {
    return this.exportEmailTxt;
  }

  public void setExportEmailReplyToAddr(String exportEmailReplyToAddr) {
    this.exportEmailReplyToAddr = exportEmailReplyToAddr;
  }

  public String getExportEmailReplyToAddr() {
    return this.exportEmailReplyToAddr;
  }

  public void setEmailRecipients(String emailRecipients) {
    this.emailRecipients = emailRecipients;
  }

  public String getEmailRecipients() {
    return this.emailRecipients;
  }

  public void setEmailSenderProp(String emailSender) {
    this.emailSenderProp = emailSender;
  }

  public String getEmailSenderProp() {
    return this.emailSenderProp;
  }

  public void setEmailServernameProp(String name) {}

  public String getEmailServernameProp() {
    return this.emailServernameProp;
  }

  public void setEmailWithAuthProp(String trueorfalse) {
    this.emailWithAuthProp = trueorfalse;
  }

  public String getEmailWithAuthProp() {
    return this.emailWithAuthProp;
  }

  public void setEmailAuthUserProp(String user) {
    this.emailAuthUserProp = user;
  }

  public String getEmailAuthUserProp() {
    return this.emailAuthUserProp;
  }

  public void setEmailAuthPwdProp(String user) {
    this.emailAuthPwdProp = user;
  }

  public String getEmailAuthPwdProp() {
    return this.emailAuthPwdProp;
  }

  public boolean getEnableCslAutosuggest() {
    return this.enableCslAutosuggest;
  }

  public void setEnableCslAutosuggest(boolean enableCslAutosuggest) {
    this.enableCslAutosuggest = enableCslAutosuggest;
  }

  public void setExportDisplayData(String data) {
    this.exportDisplayData = data;
  }

  public String getExportDisplayData() {
    return this.exportDisplayData;
  }

  public boolean getEnableFileFormats() {
    return this.enableFileFormats;
  }

  public void setEnableFileFormats(boolean enableFileFormats) {
    this.enableFileFormats = enableFileFormats;
  }

  public boolean getEnableExport() {
    return this.enableExport;
  }

  public void setEnableExport(boolean enableExport) {
    this.enableExport = enableExport;
  }

  public String getEmailCCRecipients() {
    return this.emailCCRecipients;
  }

  public void setEmailCCRecipients(String emailCCRecipients) {
    this.emailCCRecipients = emailCCRecipients;
  }

  public String getEmailBCCRecipients() {
    return this.emailBCCRecipients;
  }

  public void setEmailBCCRecipients(String emailBCCRecipients) {
    this.emailBCCRecipients = emailBCCRecipients;
  }

}
