package de.mpg.mpdl.inge.pubman.web.depositorWS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.pubman.web.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the My Items
 * workspace. It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and
 * adds additional functionality for filtering the items by their state.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class MyItemsRetrieverRequestBean extends
    BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> {
  private static Logger logger = Logger.getLogger(MyItemsRetrieverRequestBean.class);
  public static String BEAN_NAME = "MyItemsRetrieverRequestBean";
  public static final String LOAD_DEPOSITORWS = "loadDepositorWS";

  /**
   * This workspace's user.
   */
  AccountUserVO userVO;

  /**
   * The GET parameter name for the item state.
   */
  protected static String parameterSelectedItemState = "itemState";

  /**
   * import filter.
   */
  private static String parameterSelectedImport = "import";

  /**
   * The total number of records
   */
  private int numberOfRecords;

  /**
   * The currently selected import tag.
   */
  private String selectedImport;

  /**
   * A list with menu entries for the import filter menu.
   */
  private List<SelectItem> importSelectItems;

  /**
   * The menu entries of the item state filtering menu
   */
  private List<SelectItem> itemStateSelectItems;

  /**
   * The currently selected item state.
   */
  private String selectedItemState;

  @EJB
  private XmlTransforming xmlTransforming;

  public MyItemsRetrieverRequestBean() {
    super((PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class), false);
    // logger.info("RenderResponse: "+FacesContext.getCurrentInstance().getRenderResponse());
    // logger.info("ResponseComplete: "+FacesContext.getCurrentInstance().getResponseComplete());
  }


  /**
   * Checks if the user is logged in. If not, redirects to the login page.
   */
  @Override
  public void init() {
    checkForLogin();
    // Init imports
    List<SelectItem> importSelectItems = new ArrayList<SelectItem>();
    importSelectItems.add(new SelectItem("all", getLabel("EditItem_NO_ITEM_SET")));
    LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

    if (!loginHelper.isLoggedIn())
      return;

    this.userVO = loginHelper.getAccountUser();

    try {
      Connection connection = ImportLog.getConnection();
      String sql = "select * from ESCIDOC_IMPORT_LOG where userid = ? order by STARTDATE desc";
      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1, this.userVO.getReference().getObjectId());

      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        SelectItem selectItem =
            new SelectItem(resultSet.getString("name") + " "
                + ImportLog.DATE_FORMAT.format(resultSet.getTimestamp("startdate")));
        importSelectItems.add(selectItem);
      }

      resultSet.close();
      statement.close();
    } catch (Exception e) {
      logger.error("Error getting imports from database", e);
      error("Error getting imports from database");
    }

    setImportSelectItems(importSelectItems);
  }

  @Override
  public int getTotalNumberOfRecords() {
    return numberOfRecords;
  }

  @Override
  public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc) {
    List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
    LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);

    // Return empty list if the user is not logged in, needed to avoid exceptions
    if (!loginHelper.isLoggedIn())
      return returnList;

    try {

      checkSortCriterias(sc);

      // define the filter criteria
      FilterTaskParamVO filter = new FilterTaskParamVO();

      Filter f1 = filter.new OwnerFilter(loginHelper.getAccountUser().getReference());
      filter.getFilterList().add(f1);
      Filter f2 =
          filter.new FrameworkItemTypeFilter(
              PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
      filter.getFilterList().add(f2);
      Filter latestVersionFilter = filter.new StandardFilter("/isLatestVersion", "true");
      filter.getFilterList().add(latestVersionFilter);

      if (selectedItemState.toLowerCase().equals("withdrawn")) {
        // use public status instead of version status here
        Filter f3 = filter.new ItemPublicStatusFilter(State.WITHDRAWN);
        filter.getFilterList().add(0, f3);
      } else if (selectedItemState.toLowerCase().equals("all")) {
        // all public status except withdrawn
        Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        Filter f5 = filter.new ItemPublicStatusFilter(State.PENDING);
        filter.getFilterList().add(0, f5);
        Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      } else {
        // the selected version status filter
        Filter f3 = filter.new ItemStatusFilter(State.valueOf(selectedItemState));
        filter.getFilterList().add(0, f3);

        // all public status except withdrawn
        Filter f4 = filter.new ItemPublicStatusFilter(State.IN_REVISION);
        filter.getFilterList().add(0, f4);
        Filter f5 = filter.new ItemPublicStatusFilter(State.PENDING);
        filter.getFilterList().add(0, f5);
        Filter f6 = filter.new ItemPublicStatusFilter(State.SUBMITTED);
        filter.getFilterList().add(0, f6);
        Filter f7 = filter.new ItemPublicStatusFilter(State.RELEASED);
        filter.getFilterList().add(0, f7);
      }

      if (!getSelectedImport().toLowerCase().equals("all")) {
        Filter f10 = filter.new LocalTagFilter(getSelectedImport());
        filter.getFilterList().add(f10);
      }

      Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
      filter.getFilterList().add(f10);

      Filter f8 = filter.new LimitFilter(String.valueOf(limit));
      filter.getFilterList().add(f8);
      Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
      filter.getFilterList().add(f9);

      String xmlItemList =
          ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(
              filter.toMap());


      ItemVOListWrapper pubItemList =
          xmlTransforming.transformSearchRetrieveResponseToItemList(xmlItemList);

      numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords());
      returnList =
          CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList
              .getItemVOList());
    } catch (Exception e) {
      logger.error("Error in retrieving items", e);
      error("Error in retrieving items");
      numberOfRecords = 0;
    }
    return returnList;

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
   * Sets the current item state filter
   * 
   * @param itemStateSelectItem
   */
  public void setItemStateSelectItems(List<SelectItem> itemStateSelectItem) {
    this.itemStateSelectItems = itemStateSelectItem;
  }

  /**
   * Sets and returns the menu entries of the item state filter menu.
   * 
   * @return
   */
  public List<SelectItem> getItemStateSelectItems() {
    itemStateSelectItems = new ArrayList<SelectItem>();
    itemStateSelectItems.add(new SelectItem("all", getLabel("ItemList_filterAllExceptWithdrawn")));
    itemStateSelectItems.add(new SelectItem(State.PENDING.name(), getLabel(getI18nHelper()
        .convertEnumToString(State.PENDING))));
    itemStateSelectItems.add(new SelectItem(State.SUBMITTED.name(), getLabel(getI18nHelper()
        .convertEnumToString(State.SUBMITTED))));
    itemStateSelectItems.add(new SelectItem(State.RELEASED.name(), getLabel(getI18nHelper()
        .convertEnumToString(State.RELEASED))));
    itemStateSelectItems.add(new SelectItem(State.WITHDRAWN.name(), getLabel(getI18nHelper()
        .convertEnumToString(State.WITHDRAWN))));
    itemStateSelectItems.add(new SelectItem(State.IN_REVISION.name(), getLabel(getI18nHelper()
        .convertEnumToString(State.IN_REVISION))));

    return itemStateSelectItems;
  }

  /**
   * Sets the selected item state filter
   * 
   * @param selectedItemState
   */
  public void setSelectedItemState(String selectedItemState) {
    this.selectedItemState = selectedItemState;
    getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedItemState,
        selectedItemState);
  }

  /**
   * Returns the currently selected item state filter
   * 
   * @return
   */
  public String getSelectedItemState() {
    return selectedItemState;
  }

  /**
   * @return the selectedImport
   */
  public String getSelectedImport() {
    return selectedImport;
  }

  /**
   * @param selectedImport the selectedImport to set
   */
  public void setSelectedImport(String selectedImport) {
    this.selectedImport = selectedImport;
    getBasePaginatorListSessionBean().getParameterMap()
        .put(parameterSelectedImport, selectedImport);
  }

  /**
   * Returns the label for the currently selected item state.
   * 
   * @return
   */
  public String getSelectedItemStateLabel() {
    String returnString = "";
    if (getSelectedItemState() != null && !getSelectedItemState().equals("all")) {
      returnString =
          getLabel(getI18nHelper().convertEnumToString(State.valueOf(getSelectedItemState())));
    }
    return returnString;

  }

  /**
   * Called by JSF whenever the item state menu is changed.
   * 
   * @return
   */
  public String changeItemState() {
    try {

      getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      getBasePaginatorListSessionBean().redirect();
    } catch (Exception e) {
      logger.error("Error during redirection.", e);
      error("Could not redirect");
    }
    return "";

  }

  /**
   * Called by JSF whenever the context filter menu is changed. Causes a redirect to the page with
   * updated import GET parameter.
   * 
   * @return
   */
  public String changeImport() {
    try {

      getBasePaginatorListSessionBean().setCurrentPageNumber(1);
      getBasePaginatorListSessionBean().redirect();
    } catch (Exception e) {
      error("Could not redirect");
    }
    return "";

  }

  /**
   * Reads out the item state parameter from the HTTP GET request and sets an default value if it is
   * null.
   */
  @Override
  public void readOutParameters() {
    String selectedItemState =
        getExternalContext().getRequestParameterMap().get(parameterSelectedItemState);
    if (selectedItemState == null) {
      setSelectedItemState("all");
    } else {
      setSelectedItemState(selectedItemState);
    }

    String selectedItem =
        getExternalContext().getRequestParameterMap().get(parameterSelectedImport);
    if (selectedItem == null) {
      setSelectedImport("all");
    } else {
      setSelectedImport(selectedItem);
    }
  }

  @Override
  public String getType() {
    return "MyItems";
  }

  @Override
  public String getListPageName() {
    return "DepositorWSPage.jsp";
  }

  /**
   * @return the importSelectItems
   */
  public List<SelectItem> getImportSelectItems() {
    return importSelectItems;
  }

  /**
   * @param importSelectItems the importSelectItems to set
   */
  public void setImportSelectItems(List<SelectItem> importSelectItems) {
    this.importSelectItems = importSelectItems;
  }

}
