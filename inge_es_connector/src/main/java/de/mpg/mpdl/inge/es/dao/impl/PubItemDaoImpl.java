package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class PubItemDaoImpl extends ElasticSearchGenericDAOImpl<PubItemVO> implements PubItemDaoEs {

  private static final String indexName = PropertyReader.getProperty("inge.index.item.name");
  private static final String indexType = PropertyReader.getProperty("inge.index.item.type");
  private static final Class<PubItemVO> typeParameterClass = PubItemVO.class;


  public PubItemDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
