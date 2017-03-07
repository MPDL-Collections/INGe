package de.mpg.mpdl.inge.transformation.transformations.reports;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.Configurable;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;

/**
 * The Report Transformation Interface.
 * 
 * @author gergana (initial creation)
 * 
 */
@TransformationModule
public class ReportTransformationInterface implements Transformation, Configurable {
  private static final Logger logger = Logger.getLogger(ReportTransformationInterface.class);

  private static final Format JUS_REPORT_SNIPPET_FORMAT = new Format("jus_report_snippet",
      "application/xml", "UTF-8");
  private static final Format JUS_OUT_FORMAT_INDESIGN = new Format("jus_out", "application/xml",
      "UTF-8");
  private static final Format JUS_OUT_FORMAT_HTML = new Format("jus_out", "text/html", "UTF-8");

  private ReportTransformation transformer;

  public ReportTransformationInterface() {
    this.transformer = new ReportTransformation();
  }

  @Override
  public Format[] getSourceFormats() throws RuntimeException {
    return new Format[] {JUS_REPORT_SNIPPET_FORMAT};
  }

  @Override
  public Format[] getSourceFormats(Format trg) throws RuntimeException {
    if (trg != null && (trg.matches(JUS_OUT_FORMAT_INDESIGN) || trg.matches(JUS_OUT_FORMAT_HTML))) {
      return new Format[] {JUS_REPORT_SNIPPET_FORMAT};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public Format[] getTargetFormats(Format src) throws RuntimeException {
    if (src != null && src.matches(JUS_REPORT_SNIPPET_FORMAT)) {
      return new Format[] {JUS_OUT_FORMAT_INDESIGN, JUS_OUT_FORMAT_HTML};
    } else {
      return new Format[] {};
    }
  }

  @Override
  public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding,
      String trgFormatName, String trgType, String trgEncoding, String service)
      throws TransformationNotSupportedException, RuntimeException {
    Format source = new Format(srcFormatName, srcType, srcEncoding);
    Format target = new Format(trgFormatName, trgType, trgEncoding);

    return this.transform(src, source, target, service);
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service)
      throws TransformationNotSupportedException {
    logger.warn("Transformation without parameter institutsId is not supported: \n"
        + srcFormat.getName() + ", " + srcFormat.getType() + ", " + srcFormat.getEncoding() + "\n"
        + trgFormat.getName() + ", " + trgFormat.getType() + ", " + trgFormat.getEncoding());
    throw new TransformationNotSupportedException();
  }

  @Override
  public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service,
      Map<String, String> configuration) throws TransformationNotSupportedException,
      RuntimeException {
    byte[] result = null;
    boolean supported = false;
    String transformedXml = null;

    String srcFormatName = srcFormat.getName();

    if (srcFormat.getName().toLowerCase().startsWith("jus")) {
      try {
        if (configuration != null) {
          transformedXml =
              this.transformer.reportTransform(srcFormatName, trgFormat, new String(src, "UTF-8"),
                  configuration);
        } else {
          logger.warn("Transformation without parameter institutsId is not supported: \n"
              + srcFormat.getName() + ", " + srcFormat.getType() + ", " + srcFormat.getEncoding()
              + "\n" + trgFormat.getName() + ", " + trgFormat.getType() + ", "
              + trgFormat.getEncoding());
          throw new TransformationNotSupportedException();
        }
        result = transformedXml.getBytes("UTF-8");
      } catch (Exception e) {
        logger.warn("An error occurred during transformation with jusXslt.", e);
      }
      supported = true;
    }

    if (!supported) {
      logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", "
          + srcFormat.getType() + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName()
          + ", " + trgFormat.getType() + ", " + trgFormat.getEncoding());
      throw new TransformationNotSupportedException();
    }

    return result;
  }

  @Override
  public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception {
    return null;
  }

  @Override
  public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key)
      throws Exception {
    return null;
  }
}
