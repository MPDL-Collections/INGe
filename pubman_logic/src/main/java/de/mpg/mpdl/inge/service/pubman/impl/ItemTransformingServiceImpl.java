package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO.FILE_FORMAT;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerStreamSource;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.CitationTransformer;
import de.mpg.mpdl.inge.util.PropertyReader;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.event.TransformerReceiver;

@Service
@Primary
public class ItemTransformingServiceImpl implements ItemTransformingService {

  private static Logger logger = Logger.getLogger(ItemTransformingServiceImpl.class);

  private static final String TRANSFORMATION_ITEM_LIST_2_SNIPPET = "itemList2snippet.xsl";

  //  // Mapping the format names of a ExportVO object to the enums used in transformationManager
  //  private static Map<String, TransformerFactory.FORMAT> map;
  //  static {
  //    map = new HashMap<String, TransformerFactory.FORMAT>();
  //    map.put(TransformerFactory.MARC_XML, TransformerFactory.FORMAT.MARC_XML);
  //    map.put(TransformerFactory.ENDNOTE, TransformerFactory.FORMAT.ENDNOTE_STRING);
  //    map.put(TransformerFactory.BIBTEX, TransformerFactory.FORMAT.BIBTEX_STRING);
  //    map.put(TransformerFactory.ESCIDOC_ITEM_XML, TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML);
  //    map.put(TransformerFactory.EDOC_XML, TransformerFactory.FORMAT.EDOC_XML);
  //  }



  private byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> itemList,
      SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException {
    try {


      if (searchResult == null) {
        searchResult = new SearchRetrieveResponseVO<>();
        searchResult.setNumberOfRecords(itemList.size());
        List<SearchRetrieveRecordVO<ItemVersionVO>> recordList = new ArrayList<>();
        for (ItemVersionVO item : itemList) {
          SearchRetrieveRecordVO<ItemVersionVO> srr = new SearchRetrieveRecordVO<>();
          srr.setData(new ItemVersionVO(item));
          srr.setPersistenceId(item.getObjectIdAndVersion());
          recordList.add(srr);
        }
        searchResult.setRecords(recordList);
      }


      if (exportFormat.getFormat().equals(TransformerFactory.FORMAT.JSON.getName())) {
        return MapperFactory.getObjectMapper().writeValueAsBytes(searchResult);
      } else {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Transformer trans = TransformerCache.getTransformer(TransformerFactory.FORMAT.SEARCH_RESULT_VO,
            TransformerFactory.getFormat(exportFormat.getFormat()));
        trans.getConfiguration().put(CitationTransformer.CONFIGURATION_CITATION, exportFormat.getCitationName());
        trans.getConfiguration().put(CitationTransformer.CONFIGURATION_CSL_ID, exportFormat.getId());
        trans.transform(new TransformerVoSource(searchResult), new TransformerStreamResult(bos));

        return bos.toByteArray();
      }


    } catch (Exception e) {
      logger.warn("Exception occured when transforming from <" + TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML + "> to <"
          + exportFormat.getFormat());
      //              + map.get(exportFormat.getName()));
      throw new IngeTechnicalException(e);
    }

  }



  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, SearchRetrieveResponseVO<ItemVersionVO> srr) throws IngeTechnicalException {
    return getOutputForExport(exportFormat, null, srr);

  }

  @Override
  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<ItemVersionVO> pubItemVOList) throws IngeTechnicalException {
    return getOutputForExport(exportFormat, pubItemVOList, null);
  }



  @Override
  public TransformerFactory.FORMAT[] getAllSourceFormatsFor(TransformerFactory.FORMAT target) {
    return TransformerCache.getAllSourceFormatsFor(target);
  }

  @Override
  public TransformerFactory.FORMAT[] getAllTargetFormatsFor(TransformerFactory.FORMAT source) {
    return TransformerCache.getAllTargetFormatsFor(source);
  }

  @Override
  public String transformFromTo(TransformerFactory.FORMAT source, TransformerFactory.FORMAT target, String itemXml,
      Map<String, String> configuration) throws TransformationException {
    StringWriter wr = new StringWriter();

    final Transformer t = TransformerCache.getTransformer(source, target);

    if (configuration != null && !configuration.isEmpty()) {
      if (t.getConfiguration() == null || t.getConfiguration().isEmpty()) {
        t.setConfiguration(configuration);
      } else {
        Map<String, String> map = t.getConfiguration();
        map.putAll(configuration);
        t.setConfiguration(map);
      }
    }

    try {
      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))), new TransformerStreamResult(wr));
    } catch (UnsupportedEncodingException e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }

  public String transformPubItemTo(TransformerFactory.FORMAT target, PubItemVO item) throws TransformationException {
    StringWriter wr = new StringWriter();
    try {
      String itemXml = XmlTransformingService.transformToItem(item);

      final Transformer t = TransformerCache.getTransformer(TransformerFactory.getInternalFormat(), target);


      t.transform(new TransformerStreamSource(new ByteArrayInputStream(itemXml.getBytes("UTF-8"))), new TransformerStreamResult(wr));
    } catch (Exception e) {
      throw new TransformationException(e);
    }

    return wr.toString();
  }

  @Override
  public boolean isTransformationExisting(TransformerFactory.FORMAT sourceFormat, TransformerFactory.FORMAT targetFormat) {
    return TransformerCache.isTransformationExisting(sourceFormat, targetFormat);
  }
}
