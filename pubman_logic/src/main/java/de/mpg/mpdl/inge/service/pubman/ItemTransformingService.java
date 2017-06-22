package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

public interface ItemTransformingService {

  public byte[] getOutputForExport(ExportFormatVO exportFormat, List<PubItemVO> pubItemVOList)
      throws TechnicalException;

  public byte[] getOutputForExport(ExportFormatVO exportFormat, String itemList)
      throws IngeTechnicalException;

  public FORMAT[] getAllSourceFormatsFor(FORMAT target);

  public FORMAT[] getAllTargetFormatsFor(FORMAT source);

  public String transformFromTo(FORMAT source, FORMAT target, String xml)
      throws TransformationException;

  public boolean isTransformationExisting(FORMAT sourceFormat, FORMAT targetFormat);


}
