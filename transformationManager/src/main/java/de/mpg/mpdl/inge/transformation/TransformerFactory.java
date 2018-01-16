package de.mpg.mpdl.inge.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.transformers.IdentityTransformer;

public class TransformerFactory {
  private static Logger logger = Logger.getLogger(TransformerFactory.class);

  public static final String ARXIV = "arXiv";
  public static final String BIBTEX = "BibTex";
  public static final String BMC_FULLTEXT_HTML = "Bmc_Fulltext_Html";
  public static final String BMC_FULLTEXT_XML = "Bmc_Fulltext_Xml";
  public static final String BMC_OAIPMH_XML = "Bmc_Oaipmh_Xml";
  public static final String BMC_XML = "Bmc_Xml";
  public static final String COINS = "Coins";
  public static final String DC_XML = "Dc_Xml";
  public static final String DOI_XML = "Doi_Xml";
  public static final String EDOC_XML = "Edoc_Xml";
  public static final String ENDNOTE = "Endnote";
  public static final String ENDNOTE_XML = "Endnote_Xml";
  public static final String ESCIDOC_COMPONENT_XML = "eSciDoc_Component_Xml";
  public static final String ESCIDOC_ITEMLIST_V1_XML = "eSciDoc_Itemlist_V1_Xml";
  public static final String ESCIDOC_ITEMLIST_V2_XML = "eSciDoc_Itemlist_V2_Xml";
  public static final String ESCIDOC_ITEMLIST_XML = "eSciDoc_Itemlist_Xml";
  public static final String ESCIDOC_ITEM_V1_XML = "eSciDoc_Item_V1_Xml";
  public static final String ESCIDOC_ITEM_V2_XML = "eSciDoc_Item_V2_Xml";
  public static final String ESCIDOC_ITEM_VO = "eSciDoc_Item_Vo";
  public static final String ESCIDOC_ITEM_XML = "eSciDoc_Item_Xml";
  public static final String HTML_METATAGS_DC_XML = "Html_Metatags_Dc_Xml";
  public static final String HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML = "Html_Metatags_Highwirepress_Cit_Xml";
  public static final String JUS_HTML_XML = "Jus_Html_Xml";
  public static final String JUS_INDESIGN_XML = "Jus_Indesign_Xml";
  public static final String JUS_SNIPPET_XML = "Jus_Snippet_Xml";
  public static final String MAB = "Mab";
  public static final String MAB_XML = "Mab_Xml";
  public static final String MARC_21 = "Marc21";
  public static final String MARC_XML = "Marc_Xml";
  public static final String MODS_XML = "Mods_Xml";
  public static final String OAI_DC = "Oai_Dc";
  public static final String PEER_TEI_XML = "Peer_TeiI_Xml";
  public static final String PMC_OAIPMH_XML = "Pmc_Oaipmh_Xml";
  public static final String RIS = "Ris";
  public static final String RIS_XML = "Ris_Xml";
  public static final String SPIRES_XML = "Spires_Xml";
  public static final String WOS = "Wos";
  public static final String WOS_XML = "Wos_Xml";
  public static final String ZFN_TEI_XML = "Zfn_Tei_Xml";
  public static final String ZIM_XML = "Zim_Xml";

  private static final String UTF_8 = "UTF-8";

  public enum FORMAT
  {
    ARXIV_OAIPMH_XML(TransformerFactory.ARXIV, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    BIBTEX_STRING(TransformerFactory.BIBTEX, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    BMC_FULLTEXT_HTML(TransformerFactory.BMC_FULLTEXT_HTML, FileFormatVO.HTML_PLAIN_MIMETYPE, TransformerFactory.UTF_8), //
    BMC_FULLTEXT_XML(TransformerFactory.BMC_FULLTEXT_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    BMC_OAIPMH_XML(TransformerFactory.BMC_OAIPMH_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    BMC_XML(TransformerFactory.BMC_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    COINS_STRING(TransformerFactory.COINS, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    DC_XML(TransformerFactory.DC_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    DOI_METADATA_XML(TransformerFactory.DOI_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    EDOC_XML(TransformerFactory.EDOC_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ENDNOTE_STRING(TransformerFactory.ENDNOTE, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    ENDNOTE_XML(TransformerFactory.ENDNOTE_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_COMPONENT_XML(TransformerFactory.ESCIDOC_COMPONENT_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEMLIST_V1_XML(TransformerFactory.ESCIDOC_ITEMLIST_V1_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEMLIST_V2_XML(TransformerFactory.ESCIDOC_ITEMLIST_V2_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEMLIST_V3_XML(TransformerFactory.ESCIDOC_ITEMLIST_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEM_V1_XML(TransformerFactory.ESCIDOC_ITEM_V1_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEM_V2_XML(TransformerFactory.ESCIDOC_ITEM_V2_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEM_V3_XML(TransformerFactory.ESCIDOC_ITEM_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ESCIDOC_ITEM_VO(TransformerFactory.ESCIDOC_ITEM_VO, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    HTML_METATAGS_DC_XML(TransformerFactory.HTML_METATAGS_DC_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML(TransformerFactory.HTML_METATAGS_HIGHWIRE_PRESS_CIT_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    JUS_HTML_XML(TransformerFactory.JUS_HTML_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    JUS_INDESIGN_XML(TransformerFactory.JUS_INDESIGN_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    JUS_SNIPPET_XML(TransformerFactory.JUS_SNIPPET_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    MAB_STRING(TransformerFactory.MAB, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    MAB_XML(TransformerFactory.MAB_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    MARC_21_STRING(TransformerFactory.MARC_21, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    MARC_XML(TransformerFactory.MARC_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    MODS_XML(TransformerFactory.MODS_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    OAI_DC(TransformerFactory.OAI_DC, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    PEER_TEI_XML(TransformerFactory.PEER_TEI_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    PMC_OAIPMH_XML(TransformerFactory.PMC_OAIPMH_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    RIS_STRING(TransformerFactory.RIS, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    RIS_XML(TransformerFactory.RIS_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    SPIRES_XML(TransformerFactory.SPIRES_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    WOS_STRING(TransformerFactory.WOS, FileFormatVO.TXT_MIMETYPE, TransformerFactory.UTF_8), //
    WOS_XML(TransformerFactory.WOS_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ZFN_TEI_XML(TransformerFactory.ZFN_TEI_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8), //
    ZIM_XML(TransformerFactory.ZIM_XML, FileFormatVO.XML_MIMETYPE, TransformerFactory.UTF_8);

  private final String name;
  private final String type;
  private final String encoding;

  FORMAT(String name, String type, String encoding) {
      this.name = name;
      this.type = type;
      this.encoding = encoding;
    }

  public String getName() {
    return this.name;
  }

  public String getType() {
    return this.type;
  }

  public String getEncoding() {
    return this.encoding;
  }

  }

  public static FORMAT getFormat(String formatName) {
    for (FORMAT format : FORMAT.values()) {
      if (format.getName().equals(formatName)) {
        return format;
      }
    }

    throw new IllegalArgumentException("Format " + formatName + " unknown");
  }

  public static FORMAT getInternalFormat() {
    return FORMAT.ESCIDOC_ITEM_V3_XML;
  }

  public static Transformer newInstance(FORMAT sourceFormat, FORMAT targetFormat) throws TransformationException {
    if (sourceFormat.equals(targetFormat)) {
      return new IdentityTransformer();
    }

    List<TransformerEdge> transformerEdges = new ArrayList<TransformerEdge>();

    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm.targetFormat()));
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        transformerEdges.add(new TransformerEdge(transformerClass, tm.sourceFormat(), tm.targetFormat()));
      }
    }

    DijkstraAlgorithm da = new DijkstraAlgorithm(Arrays.asList(FORMAT.values()), transformerEdges);
    da.execute(sourceFormat);

    List<TransformerEdge> edges = da.getPath(targetFormat);

    if (edges == null || edges.size() == 0) {
      throw new TransformationException("No transformation chain found for " + sourceFormat + " --> " + targetFormat);
    }

    if (edges.size() == 1) {
      try {
        TransformerEdge edge = edges.get(0);
        Transformer t = edge.getTransformerClass().newInstance();
        t.setSourceFormat(edge.getSourceFormat());
        t.setTargetFormat(edge.getTargetFormat());

        return t;
      } catch (Exception e) {
        throw new TransformationException("Could not initialize transformer.", e);
      }
    }

    try {
      ChainTransformer chainTransformer = new ChainTransformer();
      List<ChainableTransformer> tList = new ArrayList<ChainableTransformer>();
      chainTransformer.setTransformerChain(tList);

      for (TransformerEdge edge : edges) {
        Transformer ct = edge.getTransformerClass().newInstance();
        ct.setSourceFormat(edge.getSourceFormat());
        ct.setTargetFormat(edge.getTargetFormat());
        tList.add((ChainableTransformer) ct);
      }

      return chainTransformer;
    } catch (Exception e) {
      throw new TransformationException("Could not initialize transformer.", e);
    }
  }

  public static FORMAT[] getAllTargetFormatsFor(FORMAT sourceFormat) {
    Set<FORMAT> targetFormats = new HashSet<FORMAT>();
    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      if (tm.sourceFormat() == sourceFormat) {
        if (logger.isDebugEnabled())
          logger.debug("Adding <" + tm.targetFormat());
        targetFormats.add(tm.targetFormat());
      }
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;

      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        if (tm.sourceFormat() == sourceFormat) {
          if (logger.isDebugEnabled())
            logger.debug("Adding <" + tm.targetFormat());
          targetFormats.add(tm.targetFormat());
        }
      }
    }

    return (FORMAT[]) targetFormats.toArray(new FORMAT[targetFormats.size()]);
  }

  public static FORMAT[] getAllSourceFormatsFor(FORMAT targetFormat) {
    Set<FORMAT> sourceFormats = new HashSet<FORMAT>();
    Reflections refl = new Reflections("de.mpg.mpdl.inge.transformation.transformers");

    Set<Class<?>> transformerModuleClasses = refl.getTypesAnnotatedWith(TransformerModule.class);
    Set<Class<?>> transformerModulesClasses = refl.getTypesAnnotatedWith(TransformerModules.class);

    for (Class<?> t : transformerModuleClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;
      TransformerModule tm = transformerClass.getAnnotation(TransformerModule.class);

      if (tm.targetFormat() == targetFormat) {
        if (logger.isDebugEnabled())
          logger.debug("Adding <" + tm.sourceFormat());
        sourceFormats.add(tm.sourceFormat());
      }
    }

    for (Class<?> t : transformerModulesClasses) {
      Class<Transformer> transformerClass = (Class<Transformer>) t;

      TransformerModules tms = transformerClass.getAnnotation(TransformerModules.class);
      for (TransformerModule tm : tms.value()) {
        if (tm.targetFormat() == targetFormat) {
          if (logger.isDebugEnabled())
            logger.debug("Adding <" + tm.sourceFormat());
          sourceFormats.add(tm.sourceFormat());
        }
      }
    }

    return (FORMAT[]) sourceFormats.toArray(new FORMAT[sourceFormats.size()]);
  }
}
