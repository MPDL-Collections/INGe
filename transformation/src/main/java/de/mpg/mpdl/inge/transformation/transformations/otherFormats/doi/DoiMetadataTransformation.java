package de.mpg.mpdl.inge.transformation.transformations.otherFormats.doi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.transformations.LocalUriResolver;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Transformation class to transform escidoc item xml to doi metadata xml
 * 
 * @author walter
 * 
 */
@TransformationModule
public class DoiMetadataTransformation implements Transformation {
  public static final Format ESCIDOC_ITEM_FORMAT =
      new Format("escidoc", "application/xml", "UTF-8");
  public static final Format DOI_ITEM_FORMAT = new Format("doi", "application/xml", "UTF-8");

  private static final String STYLESHEET_PROPERTY_NAME =
      "escidoc.transformation.doi.stylesheet.filename";

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    return new Format[] {ESCIDOC_ITEM_FORMAT};
  }

  @Override
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    if (DOI_ITEM_FORMAT.equals(trg)) {
      return new Format[] {ESCIDOC_ITEM_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    if (ESCIDOC_ITEM_FORMAT.equals(src)) {
      return new Format[] {DOI_ITEM_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException, RuntimeException {
    StringWriter result = new StringWriter();
    try {
      System.out.print("Started xslt transformation...");
      TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

      String xslPath = PropertyReader.getProperty(STYLESHEET_PROPERTY_NAME);
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
        xslPath = "transformations/otherFormats/xslt/escidoc2doi.xsl";
      }

      factory.setURIResolver(new LocalUriResolver(xslDir));
      InputStream stylesheet =
          ResourceUtil.getResourceAsStream(xslPath,
              DoiMetadataTransformation.class.getClassLoader());
      Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

      if (!trgFormat.matches(DOI_ITEM_FORMAT)) {
        throw new TransformationNotSupportedException("The requested target format ("
            + trgFormat.toString() + ") is not supported");
      }

      transformer.setOutputProperty(OutputKeys.ENCODING, trgFormat.getEncoding());
      transformer.transform(new StreamSource(new ByteArrayInputStream(src)), new StreamResult(
          result));

      return result.toString().getBytes(trgFormat.getEncoding());
    } catch (Exception e) {
      throw new RuntimeException("Error parsing edoc xml", e);
    }
  }

  @Override
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationNotSupportedException, RuntimeException {
    Format srcFormat = new Format(srcFormatName, srcType, srcEncoding);
    Format trgFormat = new Format(trgFormatName, trgType, trgEncoding);

    return transform(src, srcFormat, trgFormat, service);
  }
}
