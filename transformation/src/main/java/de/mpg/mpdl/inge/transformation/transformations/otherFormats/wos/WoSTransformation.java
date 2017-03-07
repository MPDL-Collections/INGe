package de.mpg.mpdl.inge.transformation.transformations.otherFormats.wos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.Configurable;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.transformations.LocalUriResolver;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

@TransformationModule
public class WoSTransformation implements Transformation, Configurable {
  private static final Format ESCIDOC_ITEM_LIST_FORMAT = new Format(
      "eSciDoc-publication-item-list", "application/xml", "*");
  private static final Format ESCIDOC_ITEM_FORMAT = new Format("eSciDoc-publication-item",
      "application/xml", "*");
  private static final Format WOS_FORMAT = new Format("WoS", "text/plain", "UTF-8");

  private Map<String, List<String>> properties = null;
  private Map<String, String> configuration = null;

  public WoSTransformation() {}

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    return new Format[] {WOS_FORMAT};
  }

  @Override
  public Format[] getSourceFormats(Format targetFormat) throws RuntimeException {
    if (targetFormat != null
        && (targetFormat.matches(ESCIDOC_ITEM_FORMAT) || targetFormat
            .matches(ESCIDOC_ITEM_LIST_FORMAT))) {
      return new Format[] {WOS_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public Format[] getTargetFormats(Format sourceFormat) throws RuntimeException {
    if (WOS_FORMAT.equals(sourceFormat)) {
      return new Format[] {ESCIDOC_ITEM_LIST_FORMAT, ESCIDOC_ITEM_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    return transform(src, srcFormat, trgFormat, service, null);
  }

  @Override
  public byte[] transform(byte[] arg0, String arg1, String arg2, String arg3, String arg4,
      String arg5, String arg6, String arg7) throws TransformationNotSupportedException,
      RuntimeException {
    return transform(arg0, new Format(arg1, arg2, arg3), new Format(arg4, arg5, arg6), arg7);
  }

  @Override
  public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception {
    if (this.configuration == null) {
      init();
    }

    return this.configuration;
  }

  private void init() throws IOException, FileNotFoundException, URISyntaxException {
    configuration = new LinkedHashMap<String, String>();
    properties = new HashMap<String, List<String>>();
    Properties props = new Properties();
    props.load(ResourceUtil.getResourceAsStream(
        PropertyReader.getProperty("escidoc.transformation.wos.configuration.filename"),
        WoSTransformation.class.getClassLoader()));
    for (Object key : props.keySet()) {
      if (!"configuration".equals(key.toString())) {
        String[] values = props.getProperty(key.toString()).split(",");
        properties.put(key.toString(), Arrays.asList(values));
      } else {
        String[] confValues = props.getProperty("configuration").split(",");
        for (String field : confValues) {
          String[] fieldArr = field.split("=", 2);
          configuration.put(fieldArr[0], fieldArr[1] == null ? "" : fieldArr[1]);
        }
      }
    }
  }

  @Override
  public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key)
      throws Exception {
    if (this.properties == null) {
      init();
    }

    return this.properties.get(key);
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service,
      Map<String, String> configuration) throws TransformationNotSupportedException,
      RuntimeException {
    String output = "";
    try {

      StringWriter result = new StringWriter();

      if (srcFormat.matches(WOS_FORMAT)) {
        // FileInputStream(ResourceUtil.getResourceAsFile("transformations/otherFormats/xslt/wosxml2escidoc.xsl")));
        String wosSource = new String(src, "UTF-8");
        WoSImport wos = new WoSImport();
        output = wos.transformWoS2XML(wosSource);
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

        String xslPath =
            PropertyReader.getProperty("escidoc.transformation.wos.stylesheet.filename");
        String xslDir;
        if (xslPath != null) {
          xslPath = xslPath.replace('\\', '/');
          if (xslPath.contains("/")) {
            xslDir = xslPath.substring(0, xslPath.lastIndexOf("/"));
          } else {
            xslDir = ".";
          }
        } else {
          xslDir = ".";
          xslPath = "transformations/otherFormats/xslt/wosxml2escidoc.xsl";
        }

        factory.setURIResolver(new LocalUriResolver(xslDir));
        InputStream stylesheet =
            ResourceUtil.getResourceAsStream(xslPath, WoSTransformation.class.getClassLoader());
        Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

        if (trgFormat.matches(ESCIDOC_ITEM_LIST_FORMAT)) {
          transformer.setParameter("is-item-list", Boolean.TRUE);
        } else if (trgFormat.matches(ESCIDOC_ITEM_FORMAT)) {
          transformer.setParameter("is-item-list", Boolean.FALSE);
        } else {
          throw new TransformationNotSupportedException("The requested target format ("
              + trgFormat.toString() + ") is not supported");
        }

        if (configuration != null) {
          for (String key : configuration.keySet()) {
            System.out.println("ADD PARAM " + key + " WITH VALUE " + configuration.get(key));
            transformer.setParameter(key, configuration.get(key));
          }
        }

        transformer.setParameter("content-model",
            PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
        transformer.setParameter("external-organization",
            PropertyReader.getProperty("escidoc.pubman.external.organisation.id"));
        transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
        transformer.transform(new StreamSource(new StringReader(output)), new StreamResult(result));
      }

      return result.toString().getBytes("UTF-8");
    } catch (Exception e) {
      throw new RuntimeException("Error getting file content", e);
    }
  }
}
