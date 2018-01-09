package de.mpg.mpdl.inge.pubman.web.acceptItem;

import java.io.IOException;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

@ManagedBean(name = "AcceptItem")
@SuppressWarnings("serial")
public class AcceptItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(AcceptItem.class);

  public static final String LOAD_ACCEPTITEM = "loadAcceptItem";

  private String acceptanceComment = null;
  private String creators;

  public AcceptItem() {
    this.init();
  }

  public void init() {
    final StringBuffer creators = new StringBuffer();
    for (final CreatorVO creator : this.getPubItem().getMetadata().getCreators()) {
      if (creators.length() > 0) {
        creators.append("; ");
      }

      if (creator.getType() == CreatorVO.CreatorType.PERSON) {
        creators.append(creator.getPerson().getFamilyName());
        if (creator.getPerson().getGivenName() != null) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION && creator.getOrganization().getName() != null) {
        creators.append(creator.getOrganization().getName());
      }
    }

    this.creators = creators.toString();
  }

  public String cancel() {
    try {
      FacesTools.getExternalContext().redirect(
          FacesTools.getRequest().getContextPath() + "/faces/ViewItemFullPage.jsp?itemId=" + this.getPubItem().getObjectId());
    } catch (final IOException e) {
      AcceptItem.logger.error("Could not redirect to View Item Page", e);
    }

    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  public boolean getHasAudienceFiles() {
    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if (file.getVisibility() != null && file.getVisibility().equals(Visibility.AUDIENCE)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks is the current item has at least one rights information field filled.
   * 
   * @return true if at least one rights information field filled
   */
  public boolean getHasRightsInformation() {
    for (final FileDbVO file : this.getPubItem().getFiles()) {
      if ((file.getMetadata().getCopyrightDate() != null && !"".equals(file.getMetadata().getCopyrightDate()))
          || (file.getMetadata().getLicense() != null && !"".equals(file.getMetadata().getLicense()))
          || (file.getMetadata().getRights() != null && !"".equals(file.getMetadata().getRights()))) {
        return true;
      }
    }

    return false;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  public ItemVersionVO getPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }

  public void setAcceptanceComment(String acceptanceComment) {
    this.acceptanceComment = acceptanceComment;
  }

  public String getAcceptanceComment() {
    return this.acceptanceComment;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public String getCreators() {
    return this.creators;
  }

  public String accept() {
    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;

    final String retVal = this.getItemControllerSessionBean().acceptCurrentPubItem(navigateTo, this.acceptanceComment);

    if (navigateTo.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_ACCEPTED));
      this.getPubItemListSessionBean().update();
    }

    return retVal;
  }
}
