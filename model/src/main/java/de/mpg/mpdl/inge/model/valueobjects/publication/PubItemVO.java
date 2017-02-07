package de.mpg.mpdl.inge.model.valueobjects.publication;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;


public class PubItemVO extends ItemVO {

  private static Logger logger = Logger.getLogger(PubItemVO.class);

  /**
   * Default constructor.
   */
  public PubItemVO() {
    try {
      // TODO remove content Model after migration
      this.setContentModel("");
    } catch (Exception e) {
      logger.error("Unable to set publication content model", e);
    }
  }

  /**
   * Clone constructor.
   * 
   * @param itemVO The item to be copied.
   */
  public PubItemVO(ItemVO itemVO) {
    super(itemVO);
  }

  /**
   * {@inheritDoc}
   * 
   * @author Tobias Schraut
   */
  @Override
  public Object clone() {
    return new PubItemVO(this);
  }


  public MdsPublicationVO getMetadata() {
    if (getMetadataSets() != null && getMetadataSets().size() > 0
        && getMetadataSets().get(0) instanceof MdsPublicationVO) {
      return (MdsPublicationVO) getMetadataSets().get(0);
    } else {
      return null;
    }
  }

  public void setMetadata(MdsPublicationVO mdsPublicationVO) {
    if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO) {
      getMetadataSets().set(0, mdsPublicationVO);
    } else if (getMetadataSets() != null) {
      getMetadataSets().add(mdsPublicationVO);
    }
  }

  public MdsYearbookVO getYearbookMetadata() {
    if (getMetadataSets() != null && getMetadataSets().size() > 0
        && getMetadataSets().get(0) instanceof MdsYearbookVO) {
      return (MdsYearbookVO) getMetadataSets().get(0);
    } else {
      return null;
    }
  }

  public void setYearbookMetadata(MdsYearbookVO mdsYearbookVO) {
    if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsYearbookVO) {
      getMetadataSets().set(0, mdsYearbookVO);
    } else if (getMetadataSets() != null) {
      getMetadataSets().add(mdsYearbookVO);
    }
  }
}
