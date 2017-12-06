package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.PropertyReader;

@TransformerModule(sourceFormat = FORMAT.MAB_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.MAB_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class MabXmlToItemXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty("inge.transformation.mab.stylesheet.filename",
        "transformations/otherFormats/xslt/mabxml2escidoc.xsl");
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();

    if (FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.FALSE);
    } else if (FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("is-item-list", Boolean.TRUE);
    }
    map.put("localPrefix", PropertyReader.getProperty("inge.transformation.mab.content.url.prefix"));
    map.put("content-model",
        PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
    map.put("external-organization",
        PropertyReader.getProperty("inge.pubman.external.organisation.id"));

    return map;

  }


  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return null;
  }

}
