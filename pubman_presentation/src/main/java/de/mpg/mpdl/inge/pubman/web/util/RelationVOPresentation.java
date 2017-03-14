package de.mpg.mpdl.inge.pubman.web.util;

import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

@SuppressWarnings("serial")
public class RelationVOPresentation extends RelationVO {

  private PubItemVO sourceItem;

  private PubItemVO targetItem;



  public PubItemVO getTargetItem() {
    return targetItem;
  }

  public void setTargetItem(PubItemVO targetItem) {
    this.targetItem = targetItem;
  }

  public RelationVOPresentation(RelationVO relation) {
    super(relation);
  }

  public PubItemVO getSourceItem() {
    return sourceItem;
  }

  public void setSourceItem(PubItemVO item) {
    this.sourceItem = item;
  }

}
