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
package de.mpg.mpdl.inge.dataacquisition.valueobjects;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * The attributes of an dataacquisition source.
 * 
 * @author kleinfe1
 */
public class DataSourceVO {
  // General source
  private String name;
  private String description;
  private URL url;
  private URL itemUrl;
  private String type;
  private String encoding;
  private String harvestProtocol;
  private Date retryAfter;
  private int timeout;
  private int numberOfTries;
  private String status;
  private String license;

  private String copyright;
  // Identifier prefix
  private List<String> identifier;
  private String sourceOAIIdentifier;

  private List<String> identifierExample;

  // Full text informations
  private List<FullTextVO> ftFormats;
  private List<MetadataVO> mdFormats;

  public DataSourceVO() {}

  public List<FullTextVO> getFtFormats() {
    return this.ftFormats;
  }

  public void setFtFormats(List<FullTextVO> ftFormats) {
    this.ftFormats = ftFormats;
  }

  public List<MetadataVO> getMdFormats() {
    return this.mdFormats;
  }

  public void setMdFormats(List<MetadataVO> mdFormats) {
    this.mdFormats = mdFormats;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public URL getUrl() {
    return this.url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  public String getHarvestProtocol() {
    return this.harvestProtocol;
  }

  public void setHarvestProtocol(String harvestProtocol) {
    this.harvestProtocol = harvestProtocol;
  }

  public Date getRetryAfter() {
    return this.retryAfter;
  }

  public void setRetryAfter(Date retryAfter) {
    this.retryAfter = retryAfter;
  }

  public int getTimeout() {
    return this.timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getNumberOfTries() {
    return this.numberOfTries;
  }

  public void setNumberOfTries(int numberOfTries) {
    this.numberOfTries = numberOfTries;
  }

  public String getEncoding() {
    return this.encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(List<String> identifier) {
    this.identifier = identifier;
  }

  public URL getItemUrl() {
    return this.itemUrl;
  }

  public void setItemUrl(URL itemUrl) {
    this.itemUrl = itemUrl;
  }

  public String getSourceOAIIdentifier() {
    return this.sourceOAIIdentifier;
  }

  public void setSourceOAIIdentifier(String sourceOAIIdentifier) {
    this.sourceOAIIdentifier = sourceOAIIdentifier;
  }

  public void setIdentifierExample(List<String> identifierExample) {
    this.identifierExample = identifierExample;
  }

  public List<String> getIdentifierExample() {
    return this.identifierExample;
  }

  public String getLicense() {
    return this.license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public String getCopyright() {
    return this.copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }
}
