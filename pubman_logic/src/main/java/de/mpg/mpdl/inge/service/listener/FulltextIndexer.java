package de.mpg.mpdl.inge.service.listener;



import java.io.ByteArrayOutputStream;

import javax.jms.ObjectMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.filestorage.FileStorageInterface;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@Component
public class FulltextIndexer {

  private final static Logger logger = LogManager.getLogger(FulltextIndexer.class);

  @Autowired
  PubItemDaoEs pubItemDao;

  @Autowired
  @Qualifier("fileSystemServiceBean")
  private FileStorageInterface fsi;


  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-fulltext")
  public void receiveMessage(ObjectMessage msg) {
    try {
      ItemVersionVO item = (ItemVersionVO) msg.getObject();

      //Delete all fulltexts for this item
      pubItemDao.deleteByQuery(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_FULLTEXT_ITEM_ID, item.getObjectIdAndVersion()));

      if (item.getFiles() != null) {
        for (FileDbVO fileVO : item.getFiles()) {
          if (Storage.INTERNAL_MANAGED.equals(fileVO.getStorage()) && Visibility.PUBLIC.equals(fileVO.getVisibility())) {
            logger.info("Index fulltext for: " + item.getObjectIdAndVersion() + " - " + fileVO.getObjectId());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fsi.readFile(fileVO.getLocalFileIdentifier(), bos);
            bos.flush();
            bos.close();
            pubItemDao.createFulltext(item.getObjectIdAndVersion(), fileVO.getObjectId(), bos.toByteArray());
            logger.info("Finished fulltext indexing for: " + item.getObjectIdAndVersion() + " - " + fileVO.getObjectId());
          }
        }
      }

    } catch (Exception e) {
      logger.error("Error while indexing fulltext", e);
    }
  }



}
