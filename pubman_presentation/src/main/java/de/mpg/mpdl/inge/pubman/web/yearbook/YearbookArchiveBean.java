package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO.State;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.search.SearchRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.util.PubItemVOPresentation;
import de.mpg.mpdl.inge.search.Search;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Bean for archived Yearbook-Items
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class YearbookArchiveBean extends FacesBean {
  private final String MAXIMUM_RECORDS = "5000";
  public static final String BEAN_NAME = "YearbookArchiveBean";
  private static final Logger logger = Logger.getLogger(YearbookArchiveBean.class);

  // private YearbookItemSessionBean yearbookItemSessionBean;
  private LoginHelper loginHelper;
  private XmlTransforming xmlTransforming;
  // private PubItemVO activeYearbookItem;
  // private MdsYearbookVO yearbookMetadata;
  private List<PubItemVO> archivedYearbooks;
  private PubItemVO selectedYearbook;
  private String yearbookId;

  public YearbookArchiveBean() throws Exception {
    // this.yearbookItemSessionBean =
    // (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
    this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
    ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
    InitialContext initialContext = new InitialContext();
    this.xmlTransforming =
        (XmlTransforming) initialContext
            .lookup("java:global/pubman_ear/common_logic/XmlTransformingBean");
    // this.activeYearbookItem = this.yearbookItemSessionBean.getYearbookItem();
    this.archivedYearbooks = new ArrayList<PubItemVO>();
    HashMap<String, String[]> filterParams = new HashMap<String, String[]>();
    String orgId = loginHelper.getAccountUsersAffiliations().get(0).getReference().getObjectId();
    filterParams.put("operation", new String[] {"searchRetrieve"});
    filterParams.put("version", new String[] {"1.1"});
    filterParams.put(
        "query",
        new String[] {"\"/properties/context/id\"="
            + PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")
            + " and \"/md-records/md-record/yearbook/creator/organization/identifier\"=" + orgId});
    filterParams.put("maximumRecords", new String[] {this.MAXIMUM_RECORDS});
    String xmlItemList = itemHandler.retrieveItems(filterParams);
    SearchRetrieveResponseVO result =
        xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
    // check if years have to be excluded from selection
    if (result.getNumberOfRecords() > 0) {
      PubItemVO recordPubItem = null;
      for (SearchRetrieveRecordVO yearbookRecord : result.getRecords()) {
        recordPubItem = (PubItemVO) yearbookRecord.getData();
        if (recordPubItem != null && recordPubItem.getYearbookMetadata() != null) {
          if (State.RELEASED.equals(recordPubItem.getVersion().getState())) {
            this.archivedYearbooks.add(recordPubItem);
          }
        }
      }
    }
    if (this.getArchivedYearbooks() != null && this.getArchivedYearbooks().size() < 1) {
      info(getMessage("Yearbook_noArchivedItems"));
    }
  }

  /**
   * @return the archivedYearbooks (List<PubItemVO>)
   */
  public List<PubItemVO> getArchivedYearbooks() {
    return archivedYearbooks;
  }

  /**
   * @return the yearbook-ID for item to be displayed in the detailed view
   */
  public String getYearbookId() {
    return yearbookId;
  }

  /**
   * @param yearbookId (String) the yearbook-ID for item to be displayed in the detailed view
   */
  public void setYearbookId(String yearbookId) {
    this.yearbookId = yearbookId;
  }

  /**
   * @return the yearbook for the detailed view
   */
  public PubItemVO getSelectedYearbook() {
    return selectedYearbook;
  }

  /**
   * @param selectedYearbook (PubItemVO) the yearbook for the detailed view
   */
  public void setSelectedYearbook(PubItemVO selectedYearbook) {
    this.selectedYearbook = selectedYearbook;
  }

  /**
   * @return all Members of the choosen yearbook
   */
  public List<PubItemVOPresentation> retrieveAllMembers() throws Exception {
    InitialContext initialContext = new InitialContext();
    Search searchService =
        (Search) initialContext.lookup("java:global/pubman_ear/search/SearchBean");
    List<PubItemVOPresentation> pubItemList = new ArrayList<PubItemVOPresentation>();
    MetadataSearchQuery mdQuery =
        YearbookCandidatesRetrieverRequestBean.getMemberQuery(this.getSelectedYearbook());
    ItemContainerSearchResult result = searchService.searchForItemContainer(mdQuery);
    pubItemList = SearchRetrieverRequestBean.extractItemsOfSearchResult(result);
    return pubItemList;
  }

  public String viewItem() {
    for (PubItemVO archivedYearbook : this.getArchivedYearbooks()) {
      if (this.getYearbookId().equals(archivedYearbook.getVersion().getObjectId())) {
        this.setSelectedYearbook(archivedYearbook);
      }
    }
    return "loadYearbookArchiveItemViewPage";
  }

  /**
   * Return any bean stored in session scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  public static synchronized Object getSessionBean(final Class<?> cls) {

    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }
    Object result =
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);

    logger.debug("Getting bean " + name + ": " + result);

    if (result == null) {
      try {
        logger.debug("Creating new session bean: " + name);
        Object newBean = cls.newInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, newBean);
        return newBean;
      } catch (Exception e) {
        throw new RuntimeException("Error creating new bean of type " + cls, e);
      }
    } else {
      return result;
    }
  }
}
