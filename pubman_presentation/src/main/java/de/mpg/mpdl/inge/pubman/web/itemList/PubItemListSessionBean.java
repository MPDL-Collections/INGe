/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.pubman.web.itemList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.basket.PubItemStorageSessionBean;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BasePaginatorListSessionBean;
import de.mpg.mpdl.inge.pubman.web.export.ExportItems;
import de.mpg.mpdl.inge.pubman.web.export.ExportItemsSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubItemVOPresentation;

/**
 * This session bean implements the BasePaginatorListSessionBean for sortable lists of PubItems.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "PubItemListSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class PubItemListSessionBean extends
    BasePaginatorListSessionBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static final Logger logger = Logger.getLogger(PubItemListSessionBean.class);

  public static final int MAXIMUM_CART_ITEMS = 2800;

  /**
   * An enumeration that contains the index for the search service and the sorting filter for the
   * eSciDoc ItemHandler for the offered sorting criterias. TODO Description
   * 
   * @author Markus Haarlaender (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   * 
   */
  public static enum SORT_CRITERIA {
    // Use dummy value "score" for default sorting
    RELEVANCE(null, "", OrderFilter.ORDER_DESCENDING), //
    TITLE("sort.escidoc.publication.title", "/sort/md-records/md-record/publication/title",
        OrderFilter.ORDER_ASCENDING), //
    GENRE("sort.escidoc.genre-without-uri sort.escidoc.publication.degree",
        "/sort/genre-without-uri /sort/md-records/md-record/publication/degree",
        OrderFilter.ORDER_ASCENDING), //
    DATE("sort.escidoc.any-dates", "/sort/any-dates", OrderFilter.ORDER_DESCENDING), //
    CREATOR("sort.escidoc.publication.compound.publication-creator-names",
        "/sort/md-records/md-record/publication/creator/person/family-name",
        OrderFilter.ORDER_ASCENDING), // TODO: Change back to sort.escidoc.complete-name when
                                      // complete name is filled!!
    PUBLISHING_INFO("sort.escidoc.publication.publishing-info.publisher",
        "/sort/md-records/md-record/publication/source/publishing-info/publisher",
        OrderFilter.ORDER_ASCENDING), //
    MODIFICATION_DATE("sort.escidoc.last-modification-date", "/sort/last-modification-date",
        OrderFilter.ORDER_DESCENDING), //
    EVENT_TITLE("sort.escidoc.publication.event.title",
        "/sort/md-records/md-record/publication/event/title", OrderFilter.ORDER_ASCENDING), //
    SOURCE_TITLE("", "/sort/md-records/md-record/publication/source/title",
        OrderFilter.ORDER_ASCENDING), //
    SOURCE_CREATOR("", "/sort/md-records/md-record/publication/source/creator/person/family-name",
        OrderFilter.ORDER_ASCENDING), //
    REVIEW_METHOD("", "/sort/md-records/md-record/publication/review-method",
        OrderFilter.ORDER_ASCENDING), //
    FILE("", "", OrderFilter.ORDER_ASCENDING), //
    CREATION_DATE("sort.escidoc.property.creation-date", "/sort/properties/creation-date",
        OrderFilter.ORDER_ASCENDING), //
    STATE("sort.escidoc.property.version.status", "/sort/properties/version/status",
        OrderFilter.ORDER_ASCENDING), //
    OWNER("sort.escidoc.property.created-by.title", "/properties/created-by/xLinkTitle",
        OrderFilter.ORDER_ASCENDING), //
    COLLECTION("sort.escidoc.context.objid", "/sort/properties/context/xLinkTitle",
        OrderFilter.ORDER_ASCENDING);

    /**
     * The search sorting index
     */
    private String index;

    /**
     * The path to the xml by which a list should be sorted
     */
    private String sortPath;

    /**
     * An additional attribute indicating the default sort order ("ascending" or "descending")
     */
    private String sortOrder;

    SORT_CRITERIA(String index, String sortPath, String sortOrder) {
      this.setIndex(index);
      this.setSortPath(sortPath);
      this.sortOrder = sortOrder;
    }

    /**
     * Sets the sorting search index
     * 
     * @param index
     */
    public void setIndex(String index) {
      this.index = index;
    }

    /**
     * Returns the sorting search index
     * 
     * @return
     */
    public String getIndex() {
      return this.index;
    }

    /**
     * Sets the path to the xml tag by which the list should be sorted. Used in filter of
     * ItemHandler
     * 
     * @param sortPath
     */
    public void setSortPath(String sortPath) {
      this.sortPath = sortPath;
    }

    /**
     * Sets the path to the xml tag by which the list should be sorted. Used in filter of
     * ItemHandler
     * 
     * @return
     */
    public String getSortPath() {
      return this.sortPath;
    }

    /**
     * Sets the sort order. "ascending" or "descending"
     * 
     * @param sortOrder
     */
    public void setSortOrder(String sortOrder) {
      this.sortOrder = sortOrder;
    }

    /**
     * Returns the sort order. "ascending" or "descending"
     * 
     * @param sortOrder
     */
    public String getSortOrder() {
      return this.sortOrder;
    }
  }

  /**
   * The HTTP GET parameter name for the sorting criteria.
   */
  public static String parameterSelectedSortBy = "sortBy";

  /**
   * The HTTP GET parameter name for the sorting order
   */
  public static String parameterSelectedSortOrder = "sortOrder";

  /**
   * A list containing the menu entries of the sorting criteria menu.
   */
  private List<SelectItem> sortBySelectItems;

  /**
   * The currently selected sorting criteria.
   */
  private String selectedSortBy;

  /**
   * The currently selected sort order
   */
  private String selectedSortOrder;

  /**
   * A string indicating the currently selected submenu of a PubItem list.
   */
  private String subMenu = "VIEW";

  /**
   * A string indicating the currently selected list type of a Pub Item list.
   */
  private String listType = "BIB";

  /**
   * A map containing the references of the currently selected pub items of one page. Used to reset
   * selections after a redirect.
   */
  private final Map<String, ItemRO> selectedItemRefs = new HashMap<String, ItemRO>();

  /**
   * A integer telling about the current items' position in the list
   */
  private int itemPosition = 0;

  public PubItemListSessionBean() {}

  /**
   * Called by JSF when the items should be sorted by their state. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByState() {
    try {
      this.setSelectedSortBy("STATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their title. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByTitle() {
    try {
      this.setSelectedSortBy("TITLE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their genre. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByGenre() {
    try {
      this.setSelectedSortBy("GENRE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their date. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByDate() {
    try {
      this.setSelectedSortBy("DATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their creators. Redirects to the same page
   * with updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByCreator() {
    try {
      this.setSelectedSortBy("CREATOR");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their files. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByFile() {
    try {
      this.setSelectedSortBy("FILE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the items should be sorted by their genre. Redirects to the same page with
   * updated GET parameter for sorting.
   * 
   * @return
   */
  public void changeToSortByCreationDate() {
    try {
      this.setSelectedSortBy("CREATION_DATE");
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the sort order should be changed from "ascending" to "descending" or vice
   * versa.
   * 
   * @return
   */
  public void changeSortOrder() {
    if (this.selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING)) {
      this.setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
    } else {
      this.setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
    }
    try {
      this.setSelectedSortOrder(this.selectedSortOrder);
      this.setCurrentPageNumber(1);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the sorting criteria should be changed.
   * 
   * @return
   */
  public void changeSortBy() {
    try {
      this.setCurrentPageNumber(1);
      this.setSelectedSortOrder(SORT_CRITERIA.valueOf(this.getSelectedSortBy()).getSortOrder());
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the VIEW part
   * 
   * @return
   */
  public void changeSubmenuToView() {
    try {
      this.setSubMenu("VIEW");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the submenu should be changed to the EXPORT part
   * 
   * @return
   */
  public void changeSubmenuToExport() {
    try {
      this.setSubMenu("EXPORT");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the FILTER part
   * 
   * @return
   */
  public void changeSubmenuToFilter() {
    try {
      this.setSubMenu("FILTER");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when submenu should be changed to the SORTING part
   * 
   * @return
   */
  public void changeSubmenuToSorting() {
    try {
      this.setSubMenu("SORTING");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the list type should be changed to bibliographic lists
   * 
   * @return
   */
  public void changeListTypeToBib() {
    try {
      this.setListType("BIB");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Called by JSF when the list type should be changed to grid lists
   * 
   * @return
   */
  public void changeListTypeToGrid() {
    try {
      this.setListType("GRID");
      this.setListUpdate(false);
      this.redirect();
    } catch (final Exception e) {
      this.error("Could not redirect");
    }
  }

  /**
   * Returns true if the current sort order is ascending, false if "descending
   * 
   * @return
   */
  public boolean getIsAscending() {
    return this.selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING);
  }

  /**
   * Sets the menu entries for the sorting criteria menu
   * 
   * @param sortBySelectItems
   */
  public void setSortBySelectItems(List<SelectItem> sortBySelectItems) {
    this.sortBySelectItems = sortBySelectItems;
  }

  /**
   * Returns the menu entries for the sorting criteria menu
   */
  public List<SelectItem> getSortBySelectItems() {
    this.sortBySelectItems = new ArrayList<SelectItem>();

    // the last three should not be in if not logged in
    if (!this.getLoginHelper().isLoggedIn()) {
      for (int i = 0; i < SORT_CRITERIA.values().length - 3; i++) {
        final SORT_CRITERIA sc = SORT_CRITERIA.values()[i];

        // only add if index/sorting path is available
        if ((this.getPageType().equals("SearchResult") && (sc.getIndex() == null || !sc.getIndex()
            .equals("")))
            || (!this.getPageType().equals("SearchResult") && !sc.getSortPath().equals(""))) {
          this.sortBySelectItems.add(new SelectItem(sc.name(), this.getLabel("ENUM_CRITERIA_"
              + sc.name())));
        }
      }
    } else {
      for (int i = 0; i < SORT_CRITERIA.values().length; i++) {
        final SORT_CRITERIA sc = SORT_CRITERIA.values()[i];
        // only add if index/sorting path is available
        if ((this.getPageType().equals("SearchResult") && (sc.getIndex() == null || !sc.getIndex()
            .equals("")))
            || (!this.getPageType().equals("SearchResult") && !sc.getSortPath().equals(""))) {
          this.sortBySelectItems.add(new SelectItem(sc.name(), this.getLabel("ENUM_CRITERIA_"
              + sc.name())));
        }
      }
    }

    return this.sortBySelectItems;
  }


  /**
   * Sets the current sorting criteria
   * 
   * @param selectedSortBy
   */
  public void setSelectedSortBy(String selectedSortBy) {
    this.selectedSortBy = selectedSortBy;
    this.getParameterMap().put(PubItemListSessionBean.parameterSelectedSortBy, selectedSortBy);
  }

  /**
   * Returns the currently selected sorting criteria
   * 
   * @return
   */
  public String getSelectedSortBy() {
    return this.selectedSortBy;
  }

  /**
   * Retu´rns the label in the selected language for the currrently selected sorting criteria
   * 
   * @return
   */
  public String getSelectedSortByLabel() {
    String returnString = "";
    if (!this.getSelectedSortBy().equals("all")) {
      returnString = this.getLabel("ENUM_CRITERIA_" + this.getSelectedSortBy());
    }

    return returnString;
  }

  /**
   * Returns the current sort order ("ascending" or "descending")
   */
  public String getSelectedSortOrder() {
    return this.selectedSortOrder;
  }

  /**
   * Sets the current sort order ("ascending" or "descending")
   * 
   * @param selectedSortOrder
   */
  public void setSelectedSortOrder(String selectedSortOrder) {
    this.selectedSortOrder = selectedSortOrder;
    this.getParameterMap()
        .put(PubItemListSessionBean.parameterSelectedSortOrder, selectedSortOrder);
  }

  /**
   * Reads out additional parmaeters from GET request for sorting criteria and sort order and sets
   * their default values if they are null
   */
  @Override
  protected void readOutParameters() {
    final String sortBy =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(PubItemListSessionBean.parameterSelectedSortBy);

    if (sortBy != null) {
      this.setSelectedSortBy(sortBy);
    } else if (this.getSelectedSortBy() != null) {
      // do nothing
    } else {
      // This is commented out due to PUBMAN-1907
      // if(getPageType().equals("SearchResult"))
      // {
      // setSelectedSortBy(SORT_CRITERIA.RELEVANCE.name());
      // }
      // else
      // {
      this.setSelectedSortBy(SORT_CRITERIA.MODIFICATION_DATE.name());
      // }
    }

    final String sortOrder =
        FacesTools.getExternalContext().getRequestParameterMap()
            .get(PubItemListSessionBean.parameterSelectedSortOrder);
    if (sortOrder != null) {
      this.setSelectedSortOrder(sortOrder);
    } else if (this.getSelectedSortOrder() != null) {
      // do nothing
    } else {
      this.setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
    }
  }

  /**
   * Returns the currently selected sorting criteria which is used as an additional filter
   */
  @Override
  public SORT_CRITERIA getSortCriteria() {
    final SORT_CRITERIA sc = SORT_CRITERIA.valueOf(this.getSelectedSortBy());
    sc.setSortOrder(this.getSelectedSortOrder());

    return sc;
  }

  /**
   * Sets the submenu
   * 
   * @param subMenu
   */
  public void setSubMenu(String subMenu) {
    this.subMenu = subMenu;
  }

  /**
   * Returns a string describing the curently selected submenu
   * 
   * @return
   */
  public String getSubMenu() {
    return this.subMenu;
  }

  /**
   * Resets the submenus, clears parameters from the map
   */
  @Override
  protected void pageTypeChanged() {
    if (this.getPageType().equals("MyItems") || this.getPageType().equals("MyTasks")) {
      this.subMenu = "FILTER";
    } else {
      this.subMenu = "VIEW";
    }
    this.getSelectedItemRefs().clear();
  }

  /**
   * Sets the list type ("BIB" or "GRID")
   * 
   * @param listType
   */
  public void setListType(String listType) {
    this.listType = listType;
  }

  /**
   * Returns the list type ("BIB" or "GRID")
   * 
   * @param listType
   */
  public String getListType() {
    return this.listType;
  }

  /**
   * Returns the currently selected pub items of the displayed list page
   * 
   * @return
   */
  public List<PubItemVOPresentation> getSelectedItems() {
    final List<PubItemVOPresentation> selectedPubItems = new ArrayList<PubItemVOPresentation>();
    for (final PubItemVOPresentation pubItem : this.getCurrentPartList()) {
      if (pubItem.getSelected()) {
        selectedPubItems.add(pubItem);
      }
    }

    return selectedPubItems;
  }

  /**
   * Adds the currently selected pub items to the basket and displays corresponding messages.
   * 
   * @return
   */
  public void addSelectedToCart() {
    final PubItemStorageSessionBean pubItemStorage =
        (PubItemStorageSessionBean) FacesTools.findBean("PubItemStorageSessionBean");
    final List<PubItemVOPresentation> selectedPubItems = this.getSelectedItems();

    int added = 0;
    int existing = 0;
    for (final PubItemVOPresentation pubItem : selectedPubItems) {

      if ((pubItemStorage.getStoredPubItemsSize()) < PubItemListSessionBean.MAXIMUM_CART_ITEMS) {
        if (!pubItemStorage.getStoredPubItems().containsKey(
            pubItem.getVersion().getObjectIdAndVersion())) {
          pubItemStorage.getStoredPubItems().put(pubItem.getVersion().getObjectIdAndVersion(),
              pubItem.getVersion());
          added++;
        } else {
          existing++;
        }
      } else {
        this.error(this.getMessage("basket_MaximumSizeReached") + " ("
            + PubItemListSessionBean.MAXIMUM_CART_ITEMS + ")");
        break;
      }
    }

    if (selectedPubItems.size() == 0) {
      this.error(this.getMessage("basket_NoItemsSelected"));
    }
    if (added > 0 || existing > 0) {
      this.info(this.getMessage("basket_MultipleAddedSuccessfully").replace("$1",
          String.valueOf(added)));
    }
    if (existing > 0) {
      this.info(this.getMessage("basket_MultipleAlreadyInBasket").replace("$1",
          String.valueOf(existing)));
    }

    this.redirect();
  }


  /**
   * Before any redirect, the references of the currently selected publication items are stored in
   * this session in order to reselct them after the redirect Thus, the selection is not lost.
   */
  @Override
  protected void beforeRedirect() {
    this.saveSelections();
  }

  /**
   * Saves the references of currently selected pub items into a map.
   */
  private void saveSelections() {
    for (final PubItemVOPresentation pubItem : this.getCurrentPartList()) {
      if (pubItem.getSelected()) {
        this.getSelectedItemRefs().put(pubItem.getVersion().getObjectIdAndVersion(),
            pubItem.getVersion());
      } else {
        this.getSelectedItemRefs().remove(pubItem.getVersion().getObjectIdAndVersion());
      }
    }
  }

  /**
   * Checks if items on the current page have to be selected (checked) after an redirect.
   */
  private void updateSelections() {
    if (!this.getSelectedItemRefs().isEmpty()) {
      for (final PubItemVOPresentation pubItem : this.getCurrentPartList()) {
        if (this.getSelectedItemRefs().containsKey(pubItem.getVersion().getObjectIdAndVersion())) {
          pubItem.setSelected(true);
        }
      }
      this.getSelectedItemRefs().clear();
    }
  }

  /*
   * @Override protected void saveState() { //saveSelections(); }
   */

  /**
   * Updates the checkboxes of the items on the page after a new list is displayed.
   */
  @Override
  protected void listUpdated() {
    this.updateSelections();
  }

  /**
   * Exports the selected items and displays the results.
   * 
   * @return
   */
  public String exportSelectedDisplay() {
    return this.showDisplayExportData(this.getSelectedItems());
  }

  /**
   * Exports the selected items and shows the email page.
   * 
   * @return
   */
  public String exportSelectedEmail() {
    return this.showExportEmailPage(this.getSelectedItems());
  }

  /**
   * Exports the selected items and allows the user to download them .
   * 
   * @return
   */
  public void exportSelectedDownload() {
    this.downloadExportFile(this.getSelectedItems());
  }

  /**
   * Exports all items (without offset and limit filters) and displays them.
   * 
   * @return
   */
  public String exportAllDisplay() {
    return this.showDisplayExportData(this.retrieveAll());
  }

  /**
   * Exports all items (without offset and limit filters) and and shows the email page.
   * 
   * @return
   */
  public String exportAllEmail() {
    return this.showExportEmailPage(this.retrieveAll());
  }

  /**
   * Exports all items (without offset and limit filters) and allows the user to download them .
   * 
   * @return
   */
  public void exportAllDownload() {
    this.downloadExportFile(this.retrieveAll());
  }

  /**
   * Retrieves all pub items (without offset and limit filters) and returns them in a list
   * 
   * @return
   */
  private List<PubItemVOPresentation> retrieveAll() {
    final List<PubItemVOPresentation> itemList =
        this.getPaginatorListRetriever().retrieveList(0, 0, this.getSortCriteria());
    return itemList;
  }

  /**
   * Exports the given items and displays them
   * 
   * @param pubItemList
   * @return
   */
  public String showDisplayExportData(List<PubItemVOPresentation> pubItemList) {
    this.saveSelections();

    final ItemControllerSessionBean icsb =
        (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
    String displayExportData = this.getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
    final ExportItemsSessionBean sb =
        (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");


    // set the currently selected items in the FacesBean
    // this.setSelectedItemsAndCurrentItem();
    if (pubItemList.size() != 0) {
      // save selected file format on the web interface
      final String selectedFileFormat = sb.getFileFormat();
      // for the display export data the file format should be always HTML
      sb.setFileFormat(FileFormatVO.HTML_STYLED_NAME);
      final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
      try {
        displayExportData =
            new String(icsb.retrieveExportData(curExportFormat,
                CommonUtils.convertToPubItemVOList(pubItemList)));
      } catch (final TechnicalException e) {
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
      if (curExportFormat.getFormatType() == ExportFormatVO.FormatType.STRUCTURED) {
        displayExportData = "<pre>" + displayExportData + "</pre>";
      }
      sb.setExportDisplayData(displayExportData);
      // restore selected file format on the interface
      sb.setFileFormat(selectedFileFormat);
      // return "dialog:showDisplayExportItemsPage";
      return "showDisplayExportItemsPage";
    } else {
      this.error(this.getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
      sb.setExportDisplayData(displayExportData);
      this.redirect();
      return "";
    }
  }

  /**
   * Exports the given pub items and shows the email page.
   * 
   * @param pubItemList
   * @return
   */
  public String showExportEmailPage(List<PubItemVOPresentation> pubItemList) {
    this.saveSelections();

    final ItemControllerSessionBean icsb =
        (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
    // this.setSelectedItemsAndCurrentItem();
    final ExportItemsSessionBean sb =
        (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");

    if (pubItemList.size() != 0) {
      // gets the export format VO that holds the data.
      final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
      byte[] exportFileData;
      try {
        exportFileData =
            icsb.retrieveExportData(curExportFormat,
                CommonUtils.convertToPubItemVOList(pubItemList));
      } catch (final TechnicalException e) {
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e);
        return ErrorPage.LOAD_ERRORPAGE;
      }
      if ((exportFileData == null) || (new String(exportFileData)).trim().equals("")) {
        this.error(this.getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
        this.redirect();
      }
      // YEAR + MONTH + DAY_OF_MONTH
      final Calendar rightNow = Calendar.getInstance();
      final String date =
          rightNow.get(Calendar.YEAR) + "-" + rightNow.get(Calendar.DAY_OF_MONTH) + "-"
              + rightNow.get(Calendar.MONTH) + "_";
      // create an attachment temp file from the byte[] stream
      File exportAttFile;
      try {
        exportAttFile =
            File.createTempFile("eSciDoc_Export_" + curExportFormat.getName() + "_" + date, "."
                + FileFormatVO.getExtensionByName(sb.getFileFormat()));
        final FileOutputStream fos = new FileOutputStream(exportAttFile);
        fos.write(exportFileData);
        fos.close();
      } catch (final IOException e1) {
        ((ErrorPage) FacesTools.findBean("ErrorPage")).setException(e1);
        return ErrorPage.LOAD_ERRORPAGE;
      }
      sb.setExportEmailTxt(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
      sb.setAttExportFileName(exportAttFile.getName());
      sb.setAttExportFile(exportAttFile);
      sb.setExportEmailSubject(this.getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT)
          + ": " + exportAttFile.getName());
      // hier call set the values on the exportEmailView - attachment file, subject, ....
      return "displayExportEmailPage";
    } else {
      this.error(this.getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
    }

    return "";
  }

  /**
   * Exports the given pub items and allows the user to download them
   * 
   * @param pubItemList
   * @return
   */
  public void downloadExportFile(List<PubItemVOPresentation> pubItemList) {
    this.saveSelections();

    final ItemControllerSessionBean icsb =
        (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
    // set the currently selected items in the FacesBean
    // this.setSelectedItemsAndCurrentItem();
    final ExportItemsSessionBean sb =
        (ExportItemsSessionBean) FacesTools.findBean("ExportItemsSessionBean");
    if (pubItemList.size() != 0) {
      // export format and file format.
      final ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
      byte[] exportFileData = null;
      try {
        exportFileData =
            icsb.retrieveExportData(curExportFormat,
                CommonUtils.convertToPubItemVOList(pubItemList));
      } catch (final TechnicalException e) {
        throw new RuntimeException("Cannot retrieve export data", e);
      }
      final String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
      FacesTools.getResponse().setContentType(contentType);
      final String fileName =
          "export_" + curExportFormat.getName().toLowerCase() + "."
              + FileFormatVO.getExtensionByName(sb.getFileFormat());
      FacesTools.getResponse().setHeader("Content-disposition", "attachment; filename=" + fileName);
      try {
        final OutputStream out = FacesTools.getResponse().getOutputStream();
        out.write(exportFileData);
        out.close();
      } catch (final Exception e) {
        throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
      }
      FacesTools.getCurrentInstance().responseComplete();
    } else {
      this.error(this.getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
    }
  }

  /**
   * Returns a map that contains references of the selected pub items of the last page
   * 
   * @return
   */
  public Map<String, ItemRO> getSelectedItemRefs() {
    return this.selectedItemRefs;
  }

  public boolean getDisplaySortOrder() {
    if (SORT_CRITERIA.RELEVANCE.name().equals(this.getSelectedSortBy())) {
      return false;
    }

    return true;
  }

  /**
   * redirects to the next list item and updates the currentPartList if needed
   */
  public void nextListItem() {
    final PubItemVOPresentation currentItem =
        this.getItemControllerSessionBean().getCurrentPubItem();
    int positionFirstPartListItem;
    try {
      for (int i = 0; i < this.getCurrentPartList().size(); i++) {
        if (this.getCurrentPartList().get(i).getVersion().getObjectId()
            .equals(currentItem.getVersion().getObjectId())) {
          // Case: not the last item of a part-list --> get next Item without any pagechange
          if ((i + 1) < this.getCurrentPartList().size()) {
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem + i + 1);
            FacesTools.getExternalContext()
                .redirect(this.getCurrentPartList().get(i + 1).getLink());
            return;
          }
          // Case: last item of a part-list, but not the last of the whole list --> Get first item
          // of next page
          else if ((i + 1) >= this.getCurrentPartList().size()
              && this.getCurrentPageNumber() < this.getPaginatorPageSize()) {
            this.setCurrentPageNumber(this.getCurrentPageNumber() + 1);
            this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem);
            FacesTools.getExternalContext().redirect(this.getCurrentPartList().get(0).getLink());
            return;
          }
          // Case: last item of the whole list (also of the part-list) --> get first item of the
          // first page
          else {
            this.setCurrentPageNumber(1);
            this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem);
            FacesTools.getExternalContext().redirect(this.getCurrentPartList().get(0).getLink());
            return;
          }
        }
      }
    } catch (final IOException e) {
      PubItemListSessionBean.logger.warn(
          "IO-Exception while retrieving ExternalContext for nextItem", e);
    } catch (final Exception e) {
      PubItemListSessionBean.logger.warn("Exception while getting link to nextListItem", e);
    }
  }

  /**
   * redirects to the previous list item and updates the currentPartList if needed
   */
  public void previousListItem() {
    final PubItemVOPresentation currentItem =
        this.getItemControllerSessionBean().getCurrentPubItem();
    int positionFirstPartListItem;
    try {
      for (int i = 0; i < this.getCurrentPartList().size(); i++) {
        if (this.getCurrentPartList().get(i).getVersion().getObjectId()
            .equals(currentItem.getVersion().getObjectId())) {
          // Case: not the first item of a part-list --> Go one item back without pagechange
          if ((i - 1) >= 0) {
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem + i - 1);
            FacesTools.getExternalContext()
                .redirect(this.getCurrentPartList().get(i - 1).getLink());
            return;
          }
          // Case: first item of a part-list, but not the first of the whole list --> Get last item
          // of previous page
          else if ((i - 1) < 0 && this.getCurrentPageNumber() > 1) {
            this.setCurrentPageNumber(this.getCurrentPageNumber() - 1);
            this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem + this.getCurrentPartList().size()
                - 1);
            FacesTools.getExternalContext().redirect(
                this.getCurrentPartList().get(this.getCurrentPartList().size() - 1).getLink());
            return;
          }
          // Case: first item of the whole list (also of the part-list) --> Get last item of last
          // page
          else {
            this.setCurrentPageNumber(this.getPaginatorPageSize());
            this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
            positionFirstPartListItem =
                ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
            this.setListItemPosition(positionFirstPartListItem + this.getCurrentPartList().size()
                - 1);
            FacesTools.getExternalContext().redirect(
                this.getCurrentPartList().get(this.getCurrentPartList().size() - 1).getLink());
            return;
          }
        }
      }
    } catch (final IOException e) {
      PubItemListSessionBean.logger.warn(
          "IO-Exception while retrieving ExternalContext for previousItem", e);
    } catch (final Exception e) {
      PubItemListSessionBean.logger.warn("Exception while getting link to previousListItem", e);
    }
  }

  /**
   * checks if an item is the last item of the whole list
   * 
   * @return
   */
  public boolean getHasNextListItem() {
    final PubItemVOPresentation currentItem =
        this.getItemControllerSessionBean().getCurrentPubItem();
    if (this.getCurrentPartList() != null) {
      for (int i = 0; i < this.getCurrentPartList().size(); i++) {
        if (this.getCurrentPartList().get(i).getVersion().getObjectId()
            .equals(currentItem.getVersion().getObjectId())) {
          if ((i + 1) >= this.getCurrentPartList().size()
              && this.getCurrentPageNumber() >= this.getPaginatorPageSize()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * checks if an item is the last item of the whole list
   * 
   * @return
   */
  public boolean getHasPreviousListItem() {
    final PubItemVOPresentation currentItem =
        this.getItemControllerSessionBean().getCurrentPubItem();
    if (this.getCurrentPartList() != null) {
      for (int i = 0; i < this.getCurrentPartList().size(); i++) {
        if (this.getCurrentPartList().get(i).getVersion().getObjectId()
            .equals(currentItem.getVersion().getObjectId())) {
          if ((i - 1) < 0 && this.getCurrentPageNumber() <= 1) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * redirects to the first item of the whole list and updates the currentPartList if needed
   */
  public void firstListItem() {
    try {
      this.setCurrentPageNumber(1);
      this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
      final int positionFirstPartListItem =
          ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
      this.setListItemPosition(positionFirstPartListItem);
      FacesTools.getExternalContext().redirect(this.getCurrentPartList().get(0).getLink());
    } catch (final Exception e) {
      PubItemListSessionBean.logger.debug("Exception while getting link to firstListItem");
      e.printStackTrace();
    }
  }

  /**
   * redirects to the last item of the whole list and updates the currentPartList if needed
   */
  public void lastListItem() {
    try {
      this.setCurrentPageNumber(this.getPaginatorPageSize());
      this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
      final int positionFirstPartListItem =
          ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
      this.setListItemPosition(positionFirstPartListItem + this.getCurrentPartList().size() - 1);
      FacesTools.getExternalContext().redirect(
          this.getCurrentPartList().get(this.getCurrentPartList().size() - 1).getLink());
    } catch (final Exception e) {
      PubItemListSessionBean.logger.debug("Exception while getting link to firstListItem");
      e.printStackTrace();
    }
  }

  public int getListItemPosition() {
    final PubItemVOPresentation currentItem =
        this.getItemControllerSessionBean().getCurrentPubItem();
    final int positionFirstPartListItem =
        ((this.getCurrentPageNumber() - 1) * this.getElementsPerPage()) + 1;
    for (int i = 0; i < this.getCurrentPartList().size(); i++) {
      if (this.getCurrentPartList().get(i).getVersion().getObjectId()
          .equals(currentItem.getVersion().getObjectId())) {
        this.itemPosition = positionFirstPartListItem + i;
      }
    }
    return this.itemPosition;
  }

  public void setListItemPosition(int newItemPosition) {
    if (newItemPosition > 0 && newItemPosition <= this.getTotalNumberOfElements()) {
      this.itemPosition = newItemPosition;
    } else {
      this.error(this.getMessage("ViewItemFull_browse_to_item_not_in_range"));
    }
  }

  public void doListItemPosition() {
    try {
      this.setCurrentPageNumber((int) Math.ceil((double) this.itemPosition
          / (double) this.getElementsPerPage()));
      this.update(this.getCurrentPageNumber(), this.getElementsPerPage());
      final int positionInPartList = (this.itemPosition - 1) % this.getElementsPerPage();
      FacesTools.getExternalContext().redirect(
          this.getCurrentPartList().get(positionInPartList).getLink());
    } catch (final IOException e) {
      PubItemListSessionBean.logger.debug("Problem reading new itemPosition");
      e.printStackTrace();
    } catch (final Exception e) {
      PubItemListSessionBean.logger.debug("Problem on setting new position in list");
      e.printStackTrace();
    }
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }
}
