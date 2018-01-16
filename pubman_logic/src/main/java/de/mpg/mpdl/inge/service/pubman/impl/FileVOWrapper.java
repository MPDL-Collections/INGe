package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.OutputStream;

import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;

public class FileVOWrapper {

  private String fileId;

  private FileDbVO fileVO;

  private FileStorageInterface fileStorageInterface;


  protected FileVOWrapper(String fileId, FileDbVO fileVO, FileStorageInterface fileStorageInterface) {
    this.fileId = fileId;
    this.fileVO = fileVO;
    this.fileStorageInterface = fileStorageInterface;
  }

  public FileDbVO getFileVO() {
    return fileVO;
  }

  public void readFile(OutputStream outputStream) throws IngeTechnicalException {
    fileStorageInterface.readFile(fileId, outputStream);
  }


}
