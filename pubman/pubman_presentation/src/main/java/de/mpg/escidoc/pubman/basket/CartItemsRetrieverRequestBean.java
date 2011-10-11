package de.mpg.escidoc.pubman.basket;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.ServiceLocator;


/**
 * This bean is the implementation of the BaseListRetrieverRequestBean for the basket list.
 * It uses the PubItemSessionBean as cooresponding BasePaginatorListSessionBean.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CartItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, SORT_CRITERIA>
{
    public static final String BEAN_NAME = "CartItemsRetrieverRequestBean";
    private int numberOfRecords;
    
    public static final String MESSAGE_NO_ITEM_FOR_DELETION_SELECTED = "deleteItemsFromBasket_NoItemSelected";
    
    Logger logger = Logger.getLogger(CartItemsRetrieverRequestBean.class);

    public CartItemsRetrieverRequestBean()
    {
        //refreshAlways is needed due to workarround (latest-version problem, filter only retrieves latest versions and therefore 
        //number of items in the basket could change -> message is displayed to the user.
        super((PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class), true);
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public String getType()
    {
        return "CartItems";
    }

    @Override
    public void init()
    {
        // no init needed
    }

    @Override
    public void readOutParameters()
    {
        // no additional parameters needed
    }

    /**
     * Retrieves the list of item baskets.
     */
    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
        List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
        
        try
        {
            PubItemStorageSessionBean pssb =
                (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
            
            LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            InitialContext initialContext = new InitialContext();
            XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
      
            
            List<ItemRO> idList = new ArrayList<ItemRO>();
            for (ItemRO id : pssb.getStoredPubItems().values())
            {
                idList.add(id);
            }
            
            if (idList.size() > 0)
            {
                checkSortCriterias(sc);
                
                // define the filter criteria
                FilterTaskParamVO filter = new FilterTaskParamVO();
                
                Filter f1 = filter.new ItemRefFilter(idList);
                filter.getFilterList().add(0, f1);
                
                Filter f10 = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
                filter.getFilterList().add(f10);
                Filter f8 = filter.new LimitFilter(String.valueOf(limit));
                filter.getFilterList().add(f8);
                Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
                filter.getFilterList().add(f9);
                
                String xmlItemList = "";
                if (loginHelper.getESciDocUserHandle() != null)
                {
                    xmlItemList = ServiceLocator
                      .getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(filter.toMap());
                }
                else
                {
                    xmlItemList = ServiceLocator.getItemHandler().retrieveItems(filter.toMap());
                }
        
                ItemVOListWrapper pubItemList = xmlTransforming.transformSearchRetrieveResponseToItemList(xmlItemList);
                
                numberOfRecords = Integer.parseInt(pubItemList.getNumberOfRecords());
                returnList = CommonUtils.convertToPubItemVOPresentationList((List<PubItemVO>) pubItemList.getItemVOList());
            }
            else
            {
                numberOfRecords = 0;
            }
            
            pssb.setDiffDisplayNumber(pssb.getStoredPubItemsSize() - numberOfRecords);
            if (pssb.getDiffDisplayNumber()>0)
            {
                error(pssb.getDiffDisplayNumber() + " " + getMessage("basket_ItemsChanged"));
            }
            
                      

        }
        catch (Exception e)
        {
            error("Error in retrieving items");
            logger.error("Error while retrieving items for basket", e);
        }
        return returnList;

    }

   

    /**
     * Called from JSF when selected items in the list should be removed from the basket.
     * @return
     */
    public String deleteSelected()
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
        int countSelected = 0;
        
        for (PubItemVOPresentation pubItem : getBasePaginatorListSessionBean().getCurrentPartList())
        {
            if (pubItem.getSelected())
            {
                countSelected++;
                pssb.getStoredPubItems().remove(pubItem.getVersion().getObjectIdAndVersion());
            }
        }
        if (countSelected == 0)
        {
            error(getMessage(CartItemsRetrieverRequestBean.MESSAGE_NO_ITEM_FOR_DELETION_SELECTED));
        }
       
        getBasePaginatorListSessionBean().redirect();
       
        return "";
    }

    @Override
    public String getListPageName()
    {
        return "CartItemsPage.jsp";
    }
    
    /**
     * Checks if the selected sorting criteria is currently available.
     * If not (empty string), it displays a warning message to the user.
     * @param sc The sorting criteria to be checked
     */
    protected void checkSortCriterias(SORT_CRITERIA sc)
    {
        if  (sc.getSortPath() == null || sc.getSortPath().equals(""))
        {
            error(getMessage("depositorWS_sortingNotSupported").replace("$1", getLabel("ENUM_CRITERIA_" + sc.name())));
            //getBasePaginatorListSessionBean().redirect();
        }
        
    }
    
    
    /**
     * Called when the export format list should be updated. Workaround.
     * Method needs to be called over this bean and not directly in the ExportItems bean,
     * because it has to be called first in order to save the selections in the list.
     */
    public void updateExportOptions()
    {
        ExportItems exportItemsBean = (ExportItems) getRequestBean(ExportItems.class);
        exportItemsBean.updateExportFormats();
        
    }

}
