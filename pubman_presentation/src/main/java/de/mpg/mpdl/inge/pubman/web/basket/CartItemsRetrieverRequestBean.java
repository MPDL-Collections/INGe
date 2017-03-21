package de.mpg.mpdl.inge.pubman.web.basket;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;

/**
 * This bean is the implementation of the BaseListRetrieverRequestBean for the basket list. It uses
 * the PubItemSessionBean as cooresponding BasePaginatorListSessionBean.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "CartItemsRetrieverRequestBean")
@SuppressWarnings("serial")
public class CartItemsRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(CartItemsRetrieverRequestBean.class);

  public static final String MESSAGE_NO_ITEM_FOR_DELETION_SELECTED =
      "deleteItemsFromBasket_NoItemSelected";

  private int numberOfRecords;

  public CartItemsRetrieverRequestBean() {
    // refreshAlways is needed due to workarround (latest-version problem, filter only retrieves
    // latest versions and therefore
    // number of items in the basket could change -> message is displayed to the user.
    super((PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean"), true);
  }

  @Override
  public int getTotalNumberOfRecords() {
    return numberOfRecords;
  }

  @Override
  public String getType() {
    return "CartItems";
  }

  @Override
  public void init() {
    // no init needed
  }

  @Override
  public void readOutParameters() {
    // no additional parameters needed
  }

  /**
   * Retrieves the list of item baskets.
   */
  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();

    try {
      PubItemStorageSessionBean pssb =
          (PubItemStorageSessionBean) FacesTools.findBean("PubItemStorageSessionBean");

      List<ItemRO> idList = new ArrayList<ItemRO>();
      for (ItemRO id : pssb.getStoredPubItems().values()) {
        idList.add(id);
      }

      if (idList.size() > 0) {
        checkSortCriterias(sc);

        // define the filter criteria
        FilterTaskParamVO filter = new FilterTaskParamVO();

        Filter f1 = filter.new ItemRefVersionFilter(idList);
        filter.getFilterList().add(0, f1);

        Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
        filter.getFilterList().add(f10);
        if (limit > 0) {
          Filter f8 = filter.new LimitFilter(String.valueOf(limit));
          filter.getFilterList().add(f8);
        }
        Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
        filter.getFilterList().add(f9);

        String xmlItemList = "";
        if (getLoginHelper().getESciDocUserHandle() != null) {
          xmlItemList =
              ServiceLocator.getItemHandler(getLoginHelper().getESciDocUserHandle()).retrieveItems(
                  filter.toMap());
        } else {
          xmlItemList = ServiceLocator.getItemHandler().retrieveItems(filter.toMap());
        }

        System.out.println(filter.toMap());

        ItemVOListWrapper pubItemList =
            XmlTransformingService.transformSearchRetrieveResponseToItemList(xmlItemList);

        numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords());
        returnList =
            CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList
                .getItemVOList());
      } else {
        numberOfRecords = 0;
      }

      pssb.setDiffDisplayNumber(pssb.getStoredPubItemsSize() - numberOfRecords);
      if (pssb.getDiffDisplayNumber() > 0) {

        error(pssb.getDiffDisplayNumber() + " " + getMessage("basket_ItemsChanged"));
      }



    } catch (Exception e) {
      error("Error in retrieving items");
      logger.error("Error while retrieving items for basket", e);
    }
    return returnList;
  }

  /**
   * Called from JSF when selected items in the list should be removed from the basket.
   * 
   * @return
   */
  public String deleteSelected() {
    PubItemStorageSessionBean pssb =
        (PubItemStorageSessionBean) FacesTools.findBean("PubItemStorageSessionBean");
    int countSelected = 0;

    for (PubItemVOPresentation pubItem : getBasePaginatorListSessionBean().getCurrentPartList()) {
      if (pubItem.getSelected()) {
        countSelected++;
        pssb.getStoredPubItems().remove(pubItem.getVersion().getObjectIdAndVersion());
      }
    }
    if (countSelected == 0) {
      error(getMessage(CartItemsRetrieverRequestBean.MESSAGE_NO_ITEM_FOR_DELETION_SELECTED));
    }

    getBasePaginatorListSessionBean().redirect();

    return "";
  }

  @Override
  public String getListPageName() {
    return "CartItemsPage.jsp";
  }

  /**
   * Checks if the selected sorting criteria is currently available. If not (empty string), it
   * displays a warning message to the user.
   * 
   * @param sc The sorting criteria to be checked
   */
  protected void checkSortCriterias(SORT_CRITERIA sc) {
    if (sc.getSortPath() == null || sc.getSortPath().equals("")) {
      error(getMessage("depositorWS_sortingNotSupported").replace("$1",
          getLabel("ENUM_CRITERIA_" + sc.name())));
      // getBasePaginatorListSessionBean().redirect();
    }
  }

  /**
   * Called when the export format list should be updated. Workaround. Method needs to be called
   * over this bean and not directly in the ExportItems bean, because it has to be called first in
   * order to save the selections in the list.
   */
  public void updateExportOptions() {
    ((ExportItems) FacesTools.findBean("ExportItems")).updateExportFormats();
  }
}
