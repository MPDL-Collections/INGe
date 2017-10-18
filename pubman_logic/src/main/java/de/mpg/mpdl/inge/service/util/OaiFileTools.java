package de.mpg.mpdl.inge.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;

public class OaiFileTools {

  private static Logger logger = Logger.getLogger(OaiFileTools.class);

  private final static String OAI_FILESYSTEM_ROOT_PATH = PropertyReader
      .getProperty("inge.filestorage.oai.filesystem_path");

  public static String createFile(InputStream fileInputStream, String fileName)
      throws IngeTechnicalException {
    logger.info("Trying to create File [" + fileName + "]");
    Path filePath = FileSystems.getDefault().getPath(OAI_FILESYSTEM_ROOT_PATH + "/" + fileName);
    try {
      CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING,};
      Files.copy(fileInputStream, filePath, options);
    } catch (IOException e) {
      logger.error("An error occoured, when trying to create file [" + fileName + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to create file [" + fileName
          + "]", e);
    }

    return filePath.toString();
  }

  public static void deleteFile(String fileName) throws IngeTechnicalException {
    logger.info("Trying to delete File [" + fileName + "]");
    Path path = FileSystems.getDefault().getPath(OAI_FILESYSTEM_ROOT_PATH + fileName);
    try {
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (IOException e) {
      logger
          .error("An error occoured, when trying to delete the file [" + path.toString() + "]", e);
      throw new IngeTechnicalException("An error occoured, when trying to delete the file ["
          + path.toString() + "]", e);
    }
  }
}
