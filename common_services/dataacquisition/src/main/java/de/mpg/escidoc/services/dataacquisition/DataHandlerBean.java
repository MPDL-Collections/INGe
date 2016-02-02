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
package de.mpg.escidoc.services.dataacquisition;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.AccessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.dataacquisition.exceptions.BadArgumentException;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.IdentifierNotRecognisedException;
import de.mpg.escidoc.services.dataacquisition.exceptions.SourceNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.exceptions.FormatNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * This class provides the ejb implementation of the {@link DataHandler} interface.
 * 
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 */
@Remote
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DataHandlerBean implements DataHandler {
  private final Logger logger = Logger.getLogger(DataHandlerBean.class);
  private final String fetchTypeTEXTUALDATA = "TEXTUALDATA";
  private final String fetchTypeFILEDATA = "FILEDATA";
  private final String fetchTypeESCIDOCTRANS = "ESCIDOCTRANS";
  private final String fetchTypeUNKNOWN = "UNKNOWN";
  private final String regex = "GETID";

  private DataSourceHandlerBean sourceHandler;
  private Util util;

  // Additional data info
  private String contentType;
  private String fileEnding;
  private String contentCategorie;
  private String visibility = "PRIVATE";
  private FileVO componentVO = null;
  private DataSourceVO currentSource = null;

  private URL itemUrl;
  private final String enc = "UTF-8";

  /**
   * public constructor for DataHandlerBean class.
   */
  public DataHandlerBean() {
    this.sourceHandler = new DataSourceHandlerBean();
    this.util = new Util();
  }

  /**
   * {@inheritDoc}
   */
  public byte[] doFetch(String sourceName, String identifier) throws SourceNotAvailableException,
      AccessException, IdentifierNotRecognisedException, FormatNotRecognisedException,
      RuntimeException, FormatNotAvailableException {
    this.currentSource = this.sourceHandler.getSourceByName(sourceName);
    MetadataVO md = this.sourceHandler.getDefaultMdFormatFromSource(this.currentSource);
    return this.doFetch(sourceName, identifier, md.getName(), md.getMdFormat(), md.getEncoding());
  }

  /**
   * {@inheritDoc}
   */
  public byte[] doFetch(String sourceName, String identifier, String trgFormatName,
      String trgFormatType, String trgFormatEncoding) throws SourceNotAvailableException,
      AccessException, IdentifierNotRecognisedException, FormatNotRecognisedException,
      RuntimeException, FormatNotAvailableException {
    byte[] fetchedData = null;
    this.setFileEnding(this.util.retrieveFileEndingFromCone(trgFormatType));

    try {
      if (sourceName.equalsIgnoreCase("escidoc")) {
        // necessary for escidoc sources
        sourceName = this.util.trimSourceName(sourceName, identifier);
        identifier = this.util.setEsciDocIdentifier(identifier);
        this.currentSource = this.sourceHandler.getSourceByName(sourceName);
      } else {
        this.currentSource = this.sourceHandler.getSourceByName(sourceName);
        identifier = this.util.trimIdentifier(this.currentSource, identifier);
      }

      String fetchType = this.getFetchingType(trgFormatName, trgFormatType, trgFormatEncoding);

      if (fetchType.equals(this.fetchTypeTEXTUALDATA)) {
        String textualData =
            this.fetchTextualData(identifier, trgFormatName, trgFormatType, trgFormatEncoding);
        if (textualData == null) {
          return null;
        }
        fetchedData = textualData.getBytes(this.enc);
      }
      if (fetchType.equals(this.fetchTypeFILEDATA)) {
        Format format = new Format(trgFormatName, trgFormatType, trgFormatEncoding);
        fetchedData = this.fetchData(identifier, new Format[] {format});
      }
      if (fetchType.equals(this.fetchTypeESCIDOCTRANS)) {
        fetchedData =
            this.fetchTextualData(identifier, "eSciDoc-publication-item", "application/xml",
                this.enc).getBytes(this.enc);
        TransformationBean transformer = new TransformationBean();
        fetchedData =
            transformer.transform(fetchedData, "eSciDoc-publication-item", "application/xml",
                this.enc, trgFormatName, trgFormatType, trgFormatEncoding, "escidoc");
        this.setContentType(trgFormatType);
      }
      if (fetchType.equals(this.fetchTypeUNKNOWN)) {
        throw new FormatNotRecognisedException();
      }
    } catch (AccessException e) {
      throw new AccessException(sourceName);
    } catch (IdentifierNotRecognisedException e) {
      throw new IdentifierNotRecognisedException(e);
    } catch (SourceNotAvailableException e) {
      throw new SourceNotAvailableException(e);
    } catch (FormatNotRecognisedException e) {
      throw new FormatNotRecognisedException(e);
    } catch (FormatNotAvailableException e) {
      throw new FormatNotAvailableException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return fetchedData;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] doFetch(String sourceName, String identifier, Format[] formats)
      throws SourceNotAvailableException, IdentifierNotRecognisedException,
      FormatNotRecognisedException, RuntimeException, FormatNotAvailableException {
    this.currentSource = this.sourceHandler.getSourceByName(sourceName);
    identifier = this.util.trimIdentifier(this.currentSource, identifier);
    return this.fetchData(identifier, formats);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] doFetch(String sourceName, String identifier, String[] formats)
      throws SourceNotAvailableException, IdentifierNotRecognisedException,
      FormatNotRecognisedException, RuntimeException, FormatNotAvailableException {
    if (sourceName.equalsIgnoreCase("escidoc")) {
      // necessary for escidoc sources
      sourceName = this.util.trimSourceName(sourceName, identifier);
      identifier = this.util.setEsciDocIdentifier(identifier);
    }
    this.currentSource = this.sourceHandler.getSourceByName(sourceName);
    identifier = this.util.trimIdentifier(this.currentSource, identifier);
    Format[] formatsF = new Format[formats.length];
    Format format;

    for (int i = 0; i < formats.length; i++) {
      format =
          new Format(formats[i], this.util.getDefaultMimeType(formats[i]),
              this.util.getDefaultEncoding(formats[i]));
      formatsF[i] = format;
    }

    return this.fetchData(identifier, formatsF);
  }

  /**
   * {@inheritDoc}
   */
  public byte[] doFetch(String sourceName, String identifier, String formatName)
      throws SourceNotAvailableException, IdentifierNotRecognisedException,
      FormatNotRecognisedException, RuntimeException, AccessException, FormatNotAvailableException {
    String type;
    String enc;

    // check if the format is in the name
    if (formatName.contains(new String("\u005F")) && !formatName.equals("oai_dc")) {
      String[] typeArr = formatName.split(new String("\u005F"));
      formatName = typeArr[0];
      type = typeArr[1];
      enc = "*";
    } else {
      type = this.util.getDefaultMimeType(formatName);
      enc = this.util.getDefaultEncoding(formatName);
    }
    return this.doFetch(sourceName, identifier, formatName, type, enc);
  }

  /**
   * {@inheritDoc}
   */
  public String explainSources() throws RuntimeException {
    String explainXML = "";
    try {
      String sourcesXmlPath = PropertyReader.getProperty("escidoc.import.sources.xml");
      logger.info("SourcesXml-Property: " + sourcesXmlPath);
      ClassLoader cl = this.getClass().getClassLoader();
      InputStream fileIn = cl.getResourceAsStream(sourcesXmlPath);
      BufferedReader br = new BufferedReader(new InputStreamReader(fileIn, this.enc));
      String line = null;
      while ((line = br.readLine()) != null) {
        explainXML += line + "\n";
      }

    } catch (IOException e) {
      this.logger.error("An error occurred while accessing sources.xml.", e);
      throw new RuntimeException(e);
    } catch (URISyntaxException e) {
      this.logger.error("An error occurred while accessing solution.porperties.", e);
      throw new RuntimeException(e);
    }
    return explainXML;
  }

  /**
   * Operation for fetching data of type TEXTUALDATA.
   * 
   * @param identifier
   * @param format
   * @return itemXML
   * @throws IdentifierNotRecognisedException
   * @throws SourceNotAvailableException
   * @throws AccessException
   * @throws FormatNotSupportedException
   */
  private String fetchTextualData(String identifier, String trgFormatName, String trgFormatType,
      String trgFormatEncoding) throws IdentifierNotRecognisedException, AccessException,
      SourceNotAvailableException, FormatNotAvailableException, FormatNotRecognisedException {
    String fetchedItem = null;
    String item = null;
    boolean supportedProtocol = false;
    ProtocolHandler protocolHandler = new ProtocolHandler();

    try {
      MetadataVO md =
          this.util.getMdObjectToFetch(this.currentSource, trgFormatName, trgFormatType,
              trgFormatEncoding);

      if (md == null) {
        return null;
      }
      String decoded =
          java.net.URLDecoder.decode(md.getMdUrl().toString(), this.currentSource.getEncoding());
      md.setMdUrl(new URL(decoded));
      md.setMdUrl(new URL(md.getMdUrl().toString().replaceAll(this.regex, identifier.trim())));
      this.currentSource = this.sourceHandler.updateMdEntry(this.currentSource, md);

      // Select harvesting method
      if (this.currentSource.getHarvestProtocol().equalsIgnoreCase("oai-pmh")) {
        this.logger.debug("Fetch OAI record from URL: " + md.getMdUrl());
        item = fetchOAIRecord(md);
        // Check the record for error codes
        protocolHandler.checkOAIRecord(item);
        supportedProtocol = true;
      }
      if (this.currentSource.getHarvestProtocol().equalsIgnoreCase("ejb")) {
        this.logger.debug("Fetch record via EJB.");
        item = this.fetchEjbRecord(md, identifier);
        supportedProtocol = true;
      }
      if (this.currentSource.getHarvestProtocol().equalsIgnoreCase("http")) {
        this.logger.debug("Fetch record via http.");
        item = this.fetchHttpRecord(md);
        supportedProtocol = true;
      }
      if (!supportedProtocol) {
        this.logger.warn("Harvesting protocol " + this.currentSource.getHarvestProtocol()
            + " not supported.");
        throw new RuntimeException();
      }
      fetchedItem = item;

      // Transform the itemXML if necessary
      if (item != null && !trgFormatName.trim().equalsIgnoreCase(md.getName().toLowerCase())) {
        TransformationBean transformer = new TransformationBean();

        // Transform item metadata
        Format srcFormat = new Format(md.getName(), md.getMdFormat(), "*");
        Format trgFormat = new Format(trgFormatName, trgFormatType, trgFormatEncoding);

        item =
            new String(transformer.transform(item.getBytes(this.enc), srcFormat, trgFormat,
                "escidoc"), this.enc);
        if (this.currentSource.getItemUrl() != null) {
          this.setItemUrl(new URL(this.currentSource.getItemUrl().toString()
              .replace("GETID", identifier)));
        }

        try {
          // Create component if supported
          String name = trgFormatName.replace("item", "component");
          Format trgFormatComponent = new Format(name, trgFormatType, trgFormatEncoding);
          if (transformer.checkTransformation(srcFormat, trgFormatComponent)) {
            byte[] componentBytes =
                transformer.transform(fetchedItem.getBytes(this.enc), srcFormat,
                    trgFormatComponent, "escidoc");

            if (componentBytes != null) {
              String componentXml = new String(componentBytes, this.enc);
              InitialContext initialContext = new InitialContext();
              XmlTransforming xmlTransforming =
                  (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
              this.componentVO = xmlTransforming.transformToFileVO(componentXml);
            }
          }
        } catch (Exception e) {
          this.logger.info("No component was created from external sources metadata");
        }
      }

      this.setContentType(trgFormatType);
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(this.currentSource.getName());
    } catch (IdentifierNotRecognisedException e) {
      this.logger.error("The Identifier " + identifier + "was not recognized by source "
          + this.currentSource.getName() + ".", e);
      throw new IdentifierNotRecognisedException(e);
    } catch (BadArgumentException e) {
      this.logger.error("The request contained illegal arguments", e);
      throw new RuntimeException(e);
    } catch (FormatNotRecognisedException e) {
      this.logger.error("The requested format was not recognised by the import source", e);
      throw new FormatNotRecognisedException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return item;
  }

  /**
   * fetch data from a given url.
   * 
   * @param url
   * @return byte[]
   * @throws SourceNotAvailableException
   * @throws RuntimeException
   * @throws AccessException
   */
  public byte[] fetchMetadatafromURL(URL url) throws SourceNotAvailableException, RuntimeException,
      AccessException {
    byte[] input = null;
    URLConnection conn = null;
    Date retryAfter = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);
    try {
      conn = ProxyHelper.openConnection(url);
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          String retryAfterHeader = conn.getHeaderField("Retry-After");
          if (retryAfterHeader != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            retryAfter = dateFormat.parse(retryAfterHeader);
            this.logger.debug("Source responded with 503, retry after " + retryAfter + ".");
            throw new SourceNotAvailableException(retryAfter);
          }
          break;
        case 302:
          String alternativeLocation = conn.getHeaderField("Location");
          return fetchMetadatafromURL(new URL(alternativeLocation));
        case 200:
          this.logger.info("Source responded with 200.");
          // Fetch file
          GetMethod method = new GetMethod(url.toString());
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpConn.disconnect();
          // Create zip file with fetched file
          ZipEntry ze = new ZipEntry("unapi");
          ze.setSize(input.length);
          ze.setTime(this.currentDate());
          CRC32 crc321 = new CRC32();
          crc321.update(input);
          ze.setCrc(crc321.getValue());
          zos.putNextEntry(ze);
          zos.write(input);
          zos.flush();
          zos.closeEntry();
          zos.close();
          this.setContentType("application/zip");
          this.setFileEnding(".zip");
          break;
        case 403:
          throw new AccessException("Access to url " + url + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage() + ".");
      }
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(url.toString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return baos.toByteArray();
  }

  /**
   * Operation for fetching data of type FILE.
   * 
   * @param importSource
   * @param identifier
   * @param listOfFormats
   * @return byte[] of the fetched file, zip file if more than one record was fetched
   * @throws RuntimeException
   * @throws SourceNotAvailableException
   */
  private byte[] fetchData(String identifier, Format[] formats) throws SourceNotAvailableException,
      RuntimeException, FormatNotAvailableException {
    byte[] in = null;
    FullTextVO fulltext = new FullTextVO();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    try {
      // Call fetch file for every given format
      for (int i = 0; i < formats.length; i++) {
        Format format = formats[i];
        fulltext =
            this.util.getFtObjectToFetch(this.currentSource, format.getName(), format.getType(),
                format.getEncoding());
        // Replace regex with identifier
        String decoded =
            java.net.URLDecoder.decode(fulltext.getFtUrl().toString(),
                this.currentSource.getEncoding());
        fulltext.setFtUrl(new URL(decoded));
        fulltext.setFtUrl(new URL(fulltext.getFtUrl().toString()
            .replaceAll(this.regex, identifier.trim())));
        this.logger.debug("Fetch file from URL: " + fulltext.getFtUrl());

        // escidoc file
        if (this.currentSource.getHarvestProtocol().equalsIgnoreCase("ejb")) {
          in = this.fetchEjbFile(fulltext, identifier);
        }
        // other file
        else {
          in = this.fetchFile(fulltext);
        }

        this.setFileProperties(fulltext);
        // If only one file => return it in fetched format
        if (formats.length == 1) {
          return in;
        }
        // If more than one file => add it to zip
        else {
          // If cone service is not available (we do not get a
          // fileEnding) we have
          // to make sure that the zip entries differ in name.
          String fileName = identifier;
          if (this.getFileEnding().equals("")) {
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
      }
      this.setContentType("application/zip");
      this.setFileEnding(".zip");
      zos.close();

    } catch (SourceNotAvailableException e) {
      this.logger.error("Import Source " + this.currentSource + " not available.", e);
      throw new SourceNotAvailableException(e);
    } catch (FormatNotAvailableException e) {
      throw new FormatNotAvailableException(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return baos.toByteArray();
  }

  /**
   * Handlers the http request to fetch a file from an external source.
   * 
   * @param importSource
   * @param fulltext
   * @return byte[] of the fetched file
   * @throws SourceNotAvailableException
   * @throws RuntimeException
   */
  private byte[] fetchFile(FullTextVO fulltext) throws SourceNotAvailableException,
      RuntimeException, AccessException, FormatNotAvailableException {
    URLConnection conn = null;
    byte[] input = null;
    try {
      conn = ProxyHelper.openConnection(fulltext.getFtUrl());
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          // request was not processed by source
          this.logger.warn("Import source " + this.currentSource.getName()
              + "did not provide data in format " + fulltext.getFtLabel());
          throw new FormatNotAvailableException(fulltext.getFtLabel());
        case 302:
          String alternativeLocation = conn.getHeaderField("Location");
          fulltext.setFtUrl(new URL(alternativeLocation));
          return fetchFile(fulltext);
        case 200:
          this.logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(fulltext.getFtUrl().toString());
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpConn.disconnect();
          break;
        case 403:
          throw new AccessException("Access to url " + this.currentSource.getName()
              + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(this.currentSource.getName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    // bmc needs transformation from xml to html
    if (this.currentSource.getName().equalsIgnoreCase("BioMed Central")
        && fulltext.getFtFormat().equalsIgnoreCase("text/html")) {
      Format from = new Format("bmc_fulltext_xml", "application/xml", "*");
      Format to = new Format("bmc_fulltext_html", "text/html", "*");
      TransformationBean transformer = new TransformationBean();

      try {
        input = transformer.transform(input, from, to, "escidoc");
      } catch (Exception e) {
        this.logger.error("Could not transform BMC fulltext", e);
      }
    } else if (this.currentSource.getName().equalsIgnoreCase("PubMedCentral")
        && fulltext.getFtFormat().equalsIgnoreCase("application/pdf")) {
      // pmc pdf is generated from oai-pmh-xml
      Format from = new Format("pmc_fulltext_xml", "application/xml", "*");
      Format to = new Format("pmc_fulltext_xslfo", "text/xsl", "*");
      TransformationBean transformer = new TransformationBean();
      byte[] xslFo = null;
      try {
        xslFo = transformer.transform(input, from, to, "escidoc");

      } catch (Exception e) {
        this.logger.error("Could not transform PMC fulltext", e);
      }
      // Step 1: Construct a FopFactory
      FopFactory fopFactory = FopFactory.newInstance();

      // Step 2: Set up output stream.
      ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();

      try {
        // Trying to load FOP-Configuration from the pubman.properties
        fopFactory.setUserConfig(new File(PropertyReader
            .getProperty("escidoc.dataacquisition.resources.fop.configuration")));
      } catch (Exception e) {
        try {
          logger.info(
              "FopFactory configuration couldn't be loaded from '"
                  + PropertyReader
                      .getProperty("escidoc.dataacquisition.resources.fop.configuration") + "'", e);

          // loading in-EAR configuration an fonts
          String dataaquisitionUrl =
              DataHandlerBean.class.getClassLoader().getResource("dataaquisition/").toString();
          logger.info("Trying to load FopFactory from: '" + dataaquisitionUrl
              + "apache-fop-config.xml'");
          fopFactory.setUserConfig(dataaquisitionUrl + "apache-fop-config.xml");
          fopFactory.setBaseURL(dataaquisitionUrl);
          fopFactory.getFontManager().setFontBaseURL(dataaquisitionUrl + "fonts/");
          if (logger.isDebugEnabled()) {
            logger.debug(fopFactory.getBaseURL());
            logger.debug(fopFactory.getFontManager().getFontBaseURL());
          }
        } catch (Exception exception) {
          logger.error("FopFactory configuration wasn't loaded correctly", exception);
        }
      }

      try {
        // Step 3: Construct fop with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputPdf);

        SortedMap map =
            (new org.apache.fop.tools.fontlist.FontListGenerator()).listFonts(fopFactory,
                MimeConstants.MIME_PDF, new org.apache.fop.fonts.FontEventAdapter(
                    new org.apache.fop.events.DefaultEventBroadcaster()));

        // Step 4: Setup JAXP using identity transformer
        TransformerFactory factory =
            TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        Transformer xmlTransformer = factory.newTransformer(); // identity transformer

        // Step 5: Setup input and output for XSLT transformation
        Source src = new StreamSource((InputStream) (new ByteArrayInputStream(xslFo)));

        // Resulting SAX events (the generated FO) must be piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Step 6: Start XSLT transformation and FOP processing
        xmlTransformer.transform(src, res);

        // setting pdf as result
        input = outputPdf.toByteArray();
      } catch (Exception e) {
        logger.error("Error when trying to transform xsl-FO to PDF (Apache-FOP): ", e);
      } finally {
        // Clean-up
        try {
          outputPdf.close();
        } catch (IOException e) {
          logger.error("Couldn't close outputPdf-Stream", e);
        }
      }
    }

    return input;
  }

  /**
   * Fetches an OAI record for given record identifier.
   * 
   * @param sourceURL
   * @return itemXML
   * @throws IdentifierNotRecognisedException
   * @throws SourceNotAvailableException
   * @throws RuntimeException
   */
  private String fetchOAIRecord(MetadataVO md) throws SourceNotAvailableException, AccessException,
      IdentifierNotRecognisedException, RuntimeException {
    String itemXML = "";
    URLConnection conn;
    Date retryAfter;
    InputStreamReader isReader;
    BufferedReader bReader;
    try {
      conn = ProxyHelper.openConnection(md.getMdUrl());
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          String retryAfterHeader = conn.getHeaderField("Retry-After");
          if (retryAfterHeader != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            retryAfter = dateFormat.parse(retryAfterHeader);
            this.logger.debug("Source responded with 503, retry after " + retryAfter + ".");
            throw new SourceNotAvailableException(retryAfter);
          } else {
            this.logger.debug("Source responded with 503, retry after "
                + this.currentSource.getRetryAfter() + ".");
            throw new SourceNotAvailableException(this.currentSource.getRetryAfter());
          }
        case 302:
          String alternativeLocation = conn.getHeaderField("Location");
          md.setMdUrl(new URL(alternativeLocation));
          this.currentSource = this.sourceHandler.updateMdEntry(this.currentSource, md);
          return fetchOAIRecord(md);
        case 200:
          this.logger.info("Source responded with 200");
          break;
        case 403:
          throw new AccessException("Access to url " + this.currentSource.getName()
              + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }

      // Get itemXML
      isReader = new InputStreamReader(md.getMdUrl().openStream(), this.enc);
      bReader = new BufferedReader(isReader);
      String line = "";
      while ((line = bReader.readLine()) != null) {
        itemXML += line + "\n";
      }
      httpConn.disconnect();
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(this.currentSource.getName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return itemXML;
  }

  /**
   * Fetches a eSciDoc Record from eSciDoc system.
   * 
   * @param identifier of the item
   * @return itemXML as String
   * @throws IdentifierNotRecognisedException
   * @throws RuntimeException
   * @throws URISyntaxException
   * @throws ServiceException
   * @throws MalformedURLException
   */
  private String fetchEjbRecord(MetadataVO md, String identifier)
      throws IdentifierNotRecognisedException, RuntimeException, ServiceException,
      URISyntaxException, MalformedURLException {
    String defaultUrl = ServiceLocator.getFrameworkUrl();

    try {
      if (this.currentSource.getName().equalsIgnoreCase("escidoc")) {
        return ServiceLocator.getItemHandler().retrieve(identifier);
      }
      if (this.currentSource.getName().equalsIgnoreCase("escidocdev")
          || this.currentSource.getName().equalsIgnoreCase("escidocqa")
          || this.currentSource.getName().equalsIgnoreCase("escidocprod")
          || this.currentSource.getName().equalsIgnoreCase("escidoctest")) {
        // return
        // ServiceLocator.getItemHandler(md.getMdUrl()).retrieve("escidoc:"
        // + identifier);

        String xml = ServiceLocator.getItemHandler(md.getMdUrl()).retrieve(identifier);
        return xml;
      }
    } catch (ItemNotFoundException e) {
      this.logger.error("Item with identifier " + identifier + " was not found.");
      throw new IdentifierNotRecognisedException(e);
    } catch (Exception e) {
      this.logger.error("An error occurred while retrieving the item " + identifier + ".");
      throw new RuntimeException(e);
    } finally {
      // reset ServiceLocator to standard url
      ServiceLocator.getItemHandler(new URL(defaultUrl));
    }

    return null;
  }

  /**
   * Fetches a eSciDoc Record from eSciDoc system.
   * 
   * @param identifier of the item
   * @return itemXML as String
   * @throws IdentifierNotRecognisedException
   * @throws RuntimeException
   */
  private byte[] fetchEjbFile(FullTextVO ft, String identifier)
      throws IdentifierNotRecognisedException, RuntimeException {
    String itemXML = "";
    String coreservice = "";
    URLConnection contentUrl = null;
    XmlTransforming xmlTransforming = new XmlTransformingBean();
    byte[] input = null;

    try {
      if (this.currentSource.getName().equalsIgnoreCase("escidoc")) {
        itemXML = ServiceLocator.getItemHandler().retrieve(identifier);
        coreservice = ServiceLocator.getFrameworkUrl();
      }
      if (this.currentSource.getName().equalsIgnoreCase("escidocdev")
          || this.currentSource.getName().equalsIgnoreCase("escidocqa")
          || this.currentSource.getName().equalsIgnoreCase("escidocprod")) {
        itemXML = ServiceLocator.getItemHandler(ft.getFtUrl().toString()).retrieve(identifier);
        coreservice = ft.getFtUrl().toString();
      }

      PubItemVO itemVO = xmlTransforming.transformToPubItem(itemXML);
      contentUrl =
          ProxyHelper.openConnection(new URL(coreservice + itemVO.getFiles().get(0).getContent()));
      HttpURLConnection httpConn = (HttpURLConnection) contentUrl;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          // request was not processed by source
          this.logger.warn("Import source " + this.currentSource.getName()
              + "did not provide file.");
          throw new FormatNotAvailableException(ft.getFtLabel());
        case 302:
          String alternativeLocation = contentUrl.getHeaderField("Location");
          ft.setFtUrl(new URL(alternativeLocation));
          return fetchEjbFile(ft, identifier);
        case 200:
          this.logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(coreservice + itemVO.getFiles().get(0).getContent());
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpConn.disconnect();
          break;
        case 403:
          throw new AccessException("Access to url " + this.currentSource.getName()
              + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }
    }

    catch (ItemNotFoundException e) {
      this.logger.error("Item with identifier " + identifier + " was not found.", e);
      throw new IdentifierNotRecognisedException(e);
    } catch (Exception e) {
      this.logger.error("An error occurred while retrieving the item " + identifier + ".", e);
      throw new RuntimeException(e);
    }

    return input;
  }

  /**
   * Fetches a record via http protocol.
   * 
   * @param importSource
   * @param md
   * @return
   * @throws IdentifierNotRecognisedException
   * @throws RuntimeException
   * @throws AccessException
   */
  private String fetchHttpRecord(MetadataVO md) throws IdentifierNotRecognisedException,
      RuntimeException, AccessException {
    String item = "";
    URLConnection conn;
    String charset = this.currentSource.getEncoding();
    InputStreamReader isReader;
    BufferedReader bReader;
    try {
      conn = ProxyHelper.openConnection(md.getMdUrl());
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          // request was not processed by source
          this.logger.warn("Import source " + this.currentSource.getName()
              + "did not provide file.");
          throw new FormatNotAvailableException(md.getMdLabel());
        case 302:
          String alternativeLocation = conn.getHeaderField("Location");
          md.setMdUrl(new URL(alternativeLocation));
          this.currentSource = this.sourceHandler.updateMdEntry(this.currentSource, md);
          return fetchHttpRecord(md);
        case 200:
          this.logger.info("Source responded with 200");
          break;
        case 403:
          throw new AccessException("Access to url " + this.currentSource.getName()
              + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }
      // Get itemXML
      isReader = new InputStreamReader(md.getMdUrl().openStream(), charset);
      bReader = new BufferedReader(isReader);
      String line = "";
      while ((line = bReader.readLine()) != null) {
        item += line + "\n";
      }
      httpConn.disconnect();
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(this.currentSource.getName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return item;
  }

  /**
   * Retrieves the content of a component from different escidoc instances.
   * 
   * @param identifier
   * @param url
   * @return content of a component as byte[]
   */
  public byte[] retrieveComponentContent(String identifier, String url) {
    String coreservice = "";
    URLConnection contentUrl;
    byte[] input = null;

    String sourceName = this.util.trimSourceName("escidoc", identifier);
    DataSourceVO source = this.sourceHandler.getSourceByName(sourceName);

    if (sourceName.equalsIgnoreCase("escidoc")) {
      try {
        coreservice = ServiceLocator.getFrameworkUrl();
      } catch (Exception e) {
        this.logger.error("Framework Access threw an exception.", e);
        return null;
      }
    }
    if (sourceName.equalsIgnoreCase("escidocdev") || sourceName.equalsIgnoreCase("escidocqa")
        || sourceName.equalsIgnoreCase("escidocprod") || sourceName.equalsIgnoreCase("escidoctest")) {
      // escidoc source has only one dummy ft record
      FullTextVO ft = source.getFtFormats().get(0);
      coreservice = ft.getFtUrl().toString();
    }

    try {
      contentUrl = ProxyHelper.openConnection(new URL(coreservice + url));
      HttpURLConnection httpConn = (HttpURLConnection) contentUrl;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          // request was not processed by source
          this.logger.warn("Component content could not be fetched.");
          throw new RuntimeException("Component content could not be fetched. (503)");
        case 200:
          this.logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(coreservice + url);
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpConn.disconnect();
          break;
        case 403:
          throw new AccessException("Access to component content is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }
    } catch (Exception e) {
      this.logger.error("An error occurred while retrieving the item " + identifier + ".", e);
      throw new RuntimeException(e);
    }

    return input;
  }

  /**
   * Fetches a file via http protocol.
   * 
   * @param importSource
   * @param ft
   * @return fetched file as byte[]
   * @throws IdentifierNotRecognisedException
   * @throws RuntimeException
   * @throws AccessException
   */
  private byte[] fetchHttpFile(FullTextVO ft) throws IdentifierNotRecognisedException,
      RuntimeException, AccessException {
    URLConnection conn;
    byte[] input = null;

    try {
      conn = ProxyHelper.openConnection(ft.getFtUrl());
      HttpURLConnection httpConn = (HttpURLConnection) conn;
      int responseCode = httpConn.getResponseCode();
      switch (responseCode) {
        case 503:
          // request was not processed by source
          this.logger.warn("Import source " + this.currentSource.getName()
              + "did not provide file.");
          throw new FormatNotAvailableException(ft.getFtLabel());
        case 302:
          String alternativeLocation = conn.getHeaderField("Location");
          ft.setFtUrl(new URL(alternativeLocation));
          return fetchHttpFile(ft);
        case 200:
          this.logger.info("Source responded with 200.");
          GetMethod method = new GetMethod(ft.getFtUrl().toString());
          HttpClient client = new HttpClient();
          ProxyHelper.executeMethod(client, method);
          input = method.getResponseBody();
          httpConn.disconnect();
          break;
        case 403:
          throw new AccessException("Access to url " + this.currentSource.getName()
              + " is restricted.");
        default:
          throw new RuntimeException("An error occurred during importing from external system: "
              + responseCode + ": " + httpConn.getResponseMessage());
      }
    } catch (AccessException e) {
      this.logger.error("Access denied.", e);
      throw new AccessException(this.currentSource.getName());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return input;
  }

  /**
   * Decide which kind of data has to be fetched.
   * 
   * @param source
   * @param format
   * @return type of data to be fetched {TEXTUALDATA, FILEDATA, ESCIDOCTRANS, UNKNOWN}
   */
  private String getFetchingType(String trgFormatName, String trgFormatType,
      String trgFormatEncoding) throws FormatNotAvailableException {
    // Native metadata format
    if (this.util.getMdObjectToFetch(this.currentSource, trgFormatName, trgFormatType,
        trgFormatEncoding) != null) {
      return this.fetchTypeTEXTUALDATA;
    }
    // Native Fulltext format
    if (this.util.getFtObjectToFetch(this.currentSource, trgFormatName, trgFormatType,
        trgFormatEncoding) != null) {
      return this.fetchTypeFILEDATA;
    }
    // Transformations via escidoc format
    if (this.util.checkEscidocTransform(trgFormatName, trgFormatType, trgFormatEncoding)) {
      return this.fetchTypeESCIDOCTRANS;
    }
    // Transformable formats
    TransformationBean transformer = new TransformationBean();
    Format[] trgFormats =
        transformer.getTargetFormats(new Format(trgFormatName, trgFormatType, trgFormatEncoding));
    if (trgFormats.length > 0) {
      return this.fetchTypeTEXTUALDATA;
    }

    return this.fetchTypeUNKNOWN;
  }

  /**
   * Sets the properties for a file.
   * 
   * @param fulltext
   */
  public void setFileProperties(FullTextVO fulltext) {
    this.setVisibility(fulltext.getVisibility());
    this.setContentCategorie(fulltext.getContentCategory());
    this.setContentType(fulltext.getFtFormat());
    this.setFileEnding(this.util.retrieveFileEndingFromCone(fulltext.getFtFormat()));
  }

  /**
   * method for retrieving the current sys date.
   * 
   * @return current date
   */
  public long currentDate() {
    Date today = new Date();
    return today.getTime();
  }

  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getFileEnding() {
    if (this.fileEnding == null) {
      return "";
    } else {
      return this.fileEnding;
    }
  }

  public void setFileEnding(String fileEnding) {
    this.fileEnding = fileEnding;
  }

  public String getContentCategory() {
    return this.contentCategorie;
  }

  public void setContentCategorie(String contentCategorie) {
    this.contentCategorie = contentCategorie;
  }

  public Visibility getVisibility() {
    if (this.visibility.equals("PUBLIC")) {
      return FileVO.Visibility.PUBLIC;
    } else {
      return FileVO.Visibility.PRIVATE;
    }
  }

  public void setVisibility(String visibility) {
    this.visibility = visibility;
  }

  public URL getItemUrl() {
    return this.itemUrl;
  }

  public void setItemUrl(URL itemUrl) {
    this.itemUrl = itemUrl;
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
    } else {
      FileVO file = new FileVO();
      MdsFileVO md = new MdsFileVO();
      md.setLicense(this.currentSource.getLicense());
      md.setRights(this.currentSource.getCopyright());
      file.setDefaultMetadata(md);
      return file;
    }
  }

  public void setComponentVO(FileVO componentVO) {
    this.componentVO = componentVO;
  }

  public DataSourceVO getCurrentSource() {
    return this.currentSource;
  }

  public void setCurrentSource(DataSourceVO currentSource) {
    this.currentSource = currentSource;
  }

}
