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
package de.mpg.mpdl.inge.dataacquisition;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.rmi.AccessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.util.ProxyHelper;

/**
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 */
public class DataHandlerService {
  private static final Logger logger = Logger.getLogger(DataHandlerService.class);

  private static final String REGEX = "GETID";
  private static final String PROTOCOL = "oai-pmh";

  private DataSourceHandlerService sourceHandler;

  private String contentType;
  private String fileEnding;
  private String contentCategorie;
  private String visibility;
  private FileVO componentVO;
  private URL itemUrl;

  private DataSourceVO currentSource = null;

  public DataHandlerService() {
    this.sourceHandler = new DataSourceHandlerService();
  }

  public byte[] doFetchMetaData(String service, String serviceId,
      TransformerFactory.FORMAT targetFormat) throws DataaquisitionException {
    byte[] fetchedData = null;

    try {
      this.currentSource = this.sourceHandler.getSourceByName(service);
      String identifier = Util.trimIdentifier(this.currentSource, serviceId);
      String textualData = this.fetchTextualData(identifier, targetFormat);
      if (textualData != null) {
        fetchedData = textualData.getBytes();
      }
    } catch (Exception e) {
      throw new DataaquisitionException(e);
    }

    return fetchedData;
  }

  public byte[] doFetchFullText(String service, String serviceId,
      TransformerFactory.FORMAT[] targetFormats) throws DataaquisitionException {
    byte[] fetchedData = null;

    try {
      this.currentSource = this.sourceHandler.getSourceByName(service);
      String identifier = Util.trimIdentifier(this.currentSource, serviceId);
      fetchedData = this.fetchFileData(identifier, targetFormats);
    } catch (Exception e) {
      throw new DataaquisitionException(e);
    }

    return fetchedData;
  }

  private String fetchTextualData(String identifier, TransformerFactory.FORMAT targetFormat)
      throws DataaquisitionException, TransformationException, UnsupportedEncodingException,
      MalformedURLException {
    MetadataVO metaDataVO = Util.getMdObjectToFetch(this.currentSource, targetFormat);

    String decoded =
        URLDecoder.decode(metaDataVO.getMdUrl().toString(), this.currentSource.getEncoding());
    metaDataVO.setMdUrl(new URL(decoded));
    metaDataVO.setMdUrl(new URL(metaDataVO.getMdUrl().toString().replaceAll(REGEX, identifier)));

    this.currentSource = this.sourceHandler.updateMdEntry(this.currentSource, metaDataVO);

    // Select harvesting method
    String item = null;
    boolean supportedProtocol = false;
    if (this.currentSource.getHarvestProtocol().equalsIgnoreCase(PROTOCOL)) {
      item = fetchOAIRecord(metaDataVO, targetFormat.getEncoding());
      // Check the record for error codes
      ProtocolHandler protocolHandler = new ProtocolHandler();
      protocolHandler.checkOAIRecord(item);
      supportedProtocol = true;
    }

    if (!supportedProtocol) {
      logger.warn(
          "Harvesting protocol " + this.currentSource.getHarvestProtocol() + " not supported.");
      throw new DataaquisitionException(
          "Harvesting protocol " + this.currentSource.getHarvestProtocol() + " not supported.");
    }

    String fetchedItem = item;
    String itemAfterTransformaton = item;

    // Transform the itemXML if necessary
    if (item != null && !targetFormat.getName().equalsIgnoreCase(metaDataVO.getName())) {

      Transformer transformer = TransformerCache
          .getTransformer(TransformerFactory.getFormat(metaDataVO.getName()), targetFormat);
      StringWriter wr = new StringWriter();

      transformer.transform(
          new TransformerStreamSource(
              new ByteArrayInputStream(item.getBytes(targetFormat.getEncoding()))),
          new TransformerStreamResult(wr));

      itemAfterTransformaton = wr.toString();

      if (this.currentSource.getItemUrl() != null) {
        this.itemUrl = 
            new URL(this.currentSource.getItemUrl().toString().replace("GETID", identifier));
      }

      try {
        Transformer componentTransformer =
            TransformerCache.getTransformer(TransformerFactory.getFormat(metaDataVO.getName()),
                TransformerFactory.FORMAT.ESCIDOC_COMPONENT_XML);
        if (componentTransformer != null) {
          wr = new StringWriter();

          componentTransformer.transform(
              new TransformerStreamSource(new ByteArrayInputStream(fetchedItem.getBytes())),
              new TransformerStreamResult(wr));
          byte[] componentBytes = wr.toString().getBytes();

          if (componentBytes != null) {
            String componentXml = new String(componentBytes);
            this.componentVO = XmlTransformingService.transformToFileVO(componentXml);
          }
        }
      } catch (Exception e) {
        logger.info("No component was created from external this.sources metadata");
      }
    }

    this.contentType = targetFormat.getType();

    return itemAfterTransformaton;
  }


  private byte[] fetchFileData(String identifier, TransformerFactory.FORMAT[] targetFormats)
      throws DataaquisitionException {
    byte[] in = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    try {
      // Call fetch file for every given format
      for (int i = 0; i < targetFormats.length; i++) {
        TransformerFactory.FORMAT format = targetFormats[i];
        FullTextVO fulltextVO = Util.getFtObjectToFetch(this.currentSource, format);
        // Replace regex with identifier
        String decoded = java.net.URLDecoder.decode(fulltextVO.getFtUrl().toString(),
            this.currentSource.getEncoding());
        fulltextVO.setFtUrl(new URL(decoded));
        fulltextVO.setFtUrl(
            new URL(fulltextVO.getFtUrl().toString().replaceAll(REGEX, identifier.trim())));

        in = this.fetchFile(fulltextVO);

        this.setFileProperties(fulltextVO);
        
        // If only one file => return it in fetched format
        if (targetFormats.length == 1) {
          return in;
        }

        // If more than one file => add it to zip
        // If cone service is not available (we do not get a fileEnding)
        // we have to make sure that the zip entries differ in name.
        String fileName = identifier;
        
        if ("".equals(this.getFileEnding())) {
          fileName = fileName + "_" + i;
        }
        
        ZipEntry ze = new ZipEntry(fileName + this.getFileEnding());
        ze.setSize(in.length);
        ze.setTime(this.currentDate());
        
        CRC32 crc321 = new CRC32();
        crc321.update(in);
        
        ze.setCrc(crc321.getValue());
        
        zos.putNextEntry(ze);
        zos.write(in);
        zos.flush();
        zos.closeEntry();
      }
      
      this.contentType = "application/zip";
      this.fileEnding = ".zip";
      
      zos.close();
    } catch (DataaquisitionException e) {
      logger.error("Import this.source " + this.currentSource + " not available.", e);
      throw new DataaquisitionException(
          "Import this.source " + this.currentSource + " not available.", e);
    } catch (IOException e) {
      throw new DataaquisitionException(e);
    }

    return baos.toByteArray();
  }

  private byte[] fetchFile(FullTextVO fulltext) throws DataaquisitionException {
    byte[] input = null;

    try {
      URLConnection con = ProxyHelper.openConnection(fulltext.getFtUrl());
      HttpURLConnection httpCon = (HttpURLConnection) con;
      
      int responseCode = httpCon.getResponseCode();
      
      switch (responseCode) {
        case 200:
          logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(fulltext.getFtUrl().toString());
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpCon.disconnect();
          break;
          
        case 302:
          String alternativeLocation = con.getHeaderField("Location");
          fulltext.setFtUrl(new URL(alternativeLocation));
          
          return fetchFile(fulltext);
          
        case 403:
          throw new DataaquisitionException(
              "Access to url " + this.currentSource.getName() + " is restricted.");

        case 503:
          // request was not processed by this.source
          logger.warn("Import this.source " + this.currentSource.getName()
              + "did not provide data in format " + fulltext.getFtLabel());
          throw new DataaquisitionException("Import this.source " + this.currentSource.getName()
              + "did not provide data in format " + fulltext.getFtLabel());

        default:
          throw new DataaquisitionException(
              "An error occurred during importing from external system: " + responseCode + ": "
                  + httpCon.getResponseMessage());
      }
    } catch (AccessException e) {
      logger.error("Access denied.", e);
      throw new DataaquisitionException(this.currentSource.getName());
    } catch (IOException e) {
      throw new DataaquisitionException(e);
    }

    return input;
  }

  /**
   * Fetches an OAI record for given record identifier.
   * 
   * @param this.sourceURL
   * @return itemXML
   * @throws DataaquisitionException
   */
  private String fetchOAIRecord(MetadataVO md, String encoding) throws DataaquisitionException {
    String itemXML = null;

    try {
      URLConnection con = ProxyHelper.openConnection(md.getMdUrl());
      HttpURLConnection httpCon = (HttpURLConnection) con;
      
      int responseCode = httpCon.getResponseCode();
      
      switch (responseCode) {
        case 200:
          logger.info("Source responded with 200");
          break;
          
        case 302:
          String alternativeLocation = con.getHeaderField("Location");
          md.setMdUrl(new URL(alternativeLocation));
          this.currentSource = this.sourceHandler.updateMdEntry(this.currentSource, md);
          return fetchOAIRecord(md, encoding);
          
        case 403:
          throw new AccessException(
              "Access to url " + this.currentSource.getName() + " is restricted.");
          
        case 503:
          String retryAfterHeader = con.getHeaderField("Retry-After");
          if (retryAfterHeader != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            Date retryAfter = dateFormat.parse(retryAfterHeader);
            logger.debug("Source responded with 503, retry after " + retryAfter + ".");
            throw new DataaquisitionException(
                "Source responded with 503, retry after " + retryAfter + ".");
          } else {
            logger.debug("Source responded with 503, retry after "
                + this.currentSource.getRetryAfter() + ".");
            throw new DataaquisitionException("Source responded with 503, retry after "
                + this.currentSource.getRetryAfter() + ".");
          }
          
        default:
          throw new DataaquisitionException(
              "An error occurred during importing from external system: " + responseCode + ": "
                  + httpCon.getResponseMessage());
      }

      // Get itemXML
      InputStreamReader isReader = new InputStreamReader(md.getMdUrl().openStream(), encoding);
      BufferedReader bReader = new BufferedReader(isReader);
      
      String line = "";
      while ((line = bReader.readLine()) != null) {
        itemXML += line + "\n";
      }
      
      httpCon.disconnect();
    } catch (AccessException e) {
      logger.error("Access denied.", e);
      throw new DataaquisitionException("Access denied " + this.currentSource.getName(), e);
    } catch (Exception e) {
      throw new DataaquisitionException(e);
    }

    return itemXML;
  }

  private void setFileProperties(FullTextVO fulltext) {
    this.visibility = fulltext.getVisibility();
    this.contentCategorie = fulltext.getContentCategory();
    this.contentType = fulltext.getFtFormat();
    this.fileEnding = Util.retrieveFileEndingFromCone(fulltext.getFtFormat());
  }

  private long currentDate() {
    Date today = new Date();
    return today.getTime();
  }

  public String getContentType() {
    return this.contentType;
  }

  public String getFileEnding() {
    if (this.fileEnding == null) {
      return "";
    } else {
      return this.fileEnding;
    }
  }

  public String getContentCategory() {
    return this.contentCategorie;
  }

  public Visibility getVisibility() {
    if (FileVO.Visibility.PUBLIC.name().equals(this.visibility)) {
      return FileVO.Visibility.PUBLIC;
    }

    return FileVO.Visibility.PRIVATE;
  }

  public URL getItemUrl() {
    return this.itemUrl;
  }

  public FileVO getComponentVO() {
    if (this.componentVO != null) {
      if (this.componentVO.getDefaultMetadata().getRights() == null
          || this.componentVO.getDefaultMetadata().getRights().equals("")) {
        this.componentVO.getDefaultMetadata().setRights(this.currentSource.getCopyright());
      }

      if (this.componentVO.getDefaultMetadata().getLicense() == null
          || this.componentVO.getDefaultMetadata().getLicense().equals("")) {
        this.componentVO.getDefaultMetadata().setLicense(this.currentSource.getLicense());
      }

      return this.componentVO;
    }

    FileVO file = new FileVO();
    MdsFileVO md = new MdsFileVO();
    md.setLicense(this.currentSource.getLicense());
    md.setRights(this.currentSource.getCopyright());
    file.setDefaultMetadata(md);

    return file;
  }
}
