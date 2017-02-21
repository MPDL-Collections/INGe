package de.mpg.mpdl.inge.pubman.web.revisions;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.RelationVOPresentation;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Item Revisions
 * list. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class RevisionsRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {

  private int numberOfRecords;

  // Faces navigation string
  public final static String LOAD_REVISION_LIST = "loadRevisionList";

  public RevisionsRetrieverRequestBean() {
    super((RevisionItemListSessionBean) getSessionBean(RevisionItemListSessionBean.class), true);
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public int getTotalNumberOfRecords() {
    return numberOfRecords;
  }

  @Override
  public String getType() {
    return "RevisionList";
  }

  @Override
  public void readOutParameters() {
    // No parameters needed
  }

  /**
   * Retrieves the revisions and ignores limit and offset and sorting because there is no paginator
   * and no sorting mechanism for this list
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {

    // limit and offset is ignored because no paginator is used
    List<PubItemVO> pubItemVOList = new ArrayList<PubItemVO>();


    try {
      ItemControllerSessionBean icsb =
          (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
      // get Revisions
      List<RelationVOPresentation> relationVOList =
          icsb.retrieveRevisions(icsb.getCurrentPubItem());

      for (RelationVOPresentation relationVO : relationVOList) {
        PubItemVO sourceItem = relationVO.getSourceItem();

        if (sourceItem != null
            && sourceItem.getVersion().getState().toString()
                .equals(PubItemVO.State.RELEASED.toString())) {
          pubItemVOList.add(sourceItem);
        }

      }

      // get ParentItems

      List<RelationVOPresentation> relationVOList2 =
          icsb.retrieveParentsForRevision(icsb.getCurrentPubItem());

      for (RelationVOPresentation relationVO : relationVOList2) {
        PubItemVO targetItem = relationVO.getTargetItem();
        if (targetItem != null
            && targetItem.getVersion().getState().toString()
                .equals(PubItemVO.State.RELEASED.toString())) {
          pubItemVOList.add(targetItem);
        }

      }
    } catch (Exception e) {
      error("Error with retrieving revisions");
    }

    numberOfRecords = pubItemVOList.size();
    return CommonUtils.convertToPubItemVOPresentationList(pubItemVOList);
  }

  @Override
  public String getListPageName() {
    return "ViewItemRevisionsPage.jsp";
  }

}
