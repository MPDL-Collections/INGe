package de.mpg.mpdl.inge.transformation.transformers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import de.mpg.mpdl.inge.transformation.ChainableTransformer;
import de.mpg.mpdl.inge.transformation.SingleTransformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.TransformerModule;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.LocalUriResolver;

@TransformerModule(sourceFormat = FORMAT.MARC_XML, targetFormat = FORMAT.ESCIDOC_ITEM_V3_XML)
@TransformerModule(sourceFormat = FORMAT.MARC_XML, targetFormat = FORMAT.ESCIDOC_ITEMLIST_V3_XML)
public class MarcXmlToItemXml extends XslTransformer implements ChainableTransformer {


  @Override
  public Source getXsltSource() throws TransformationException {
    return getXmlSourceFromProperty("inge.transformation.marcxml2escidoc.stylesheet.filename",
        "transformations/commonPublicationFormats/xslt/marc_to_pubman.xsl");
  }

  @Override
  public Map<String, Object> getParameters() throws TransformationException {
    Map<String, Object> map = new HashMap<String, Object>();

    //    String ns_prefix_xsd_soap_common_srel =
    //        (PropertyReader.getProperty("xsd.soap.common.srel") != null) ? "{" + PropertyReader.getProperty("xsd.soap.common.srel") + "}"
    //            : "{http://escidoc.de/core/01/structural-relations/}";
    //
    //    map.put(ns_prefix_xsd_soap_common_srel + "context-URI", PropertyReader.getProperty("escidoc.framework_access.context.id.test"));
    //    map.put(ns_prefix_xsd_soap_common_srel + "content-model-URI",
    //        PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));

    if (FORMAT.ESCIDOC_ITEM_V3_XML.equals(getTargetFormat())) {
      map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item");
    } else if (FORMAT.ESCIDOC_ITEMLIST_V3_XML.equals(getTargetFormat())) {
      map.put("{http://www.editura.de/ns/2012/misc}target-format", "eSciDoc-publication-item-list");
    }

    return map;

  }


  @Override
  public URIResolver getURIResolver() {
    return new LocalUriResolver("transformations/commonPublicationFormats/xslt");
  }

  @Override
  public Map<String, String> getDefaultConfiguration() throws TransformationException {
    return SingleTransformer.getDefaultConfigurationFromProperty("inge.transformation.marcxml2escidoc.configuration.filename",
        "transformations/commonPublicationFormats/conf/marcxml2escidoc.properties");
  }

  @Override
  public List<String> getAllConfigurationValuesFor(String key) throws TransformationException {
    return getAllConfigurationValuesFromProperty("inge.transformation.marcxml2escidoc.configuration.filename",
        "transformations/commonPublicationFormats/conf/marcxml2escidoc.properties").get(key);
  }


}
