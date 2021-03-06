package de.mpg.mpdl.inge.transformation.transformers;

import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.BMC_FULLTEXT_XML, targetFormat = FORMAT.BMC_FULLTEXT_HTML)
public class BmcFulltextXmlToBmcFulltextHtml extends XslTransformer implements ChainableTransformer {

  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty(PropertyReader.INGE_TRANSFORMATION_BMC_FULLTEXT_XML2BMC_FULLTEXT_HTML_STYLESHEET_FILENAME);
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    return null;
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }

}
