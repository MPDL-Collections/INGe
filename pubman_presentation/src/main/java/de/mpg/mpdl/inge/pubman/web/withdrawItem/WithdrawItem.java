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

package de.mpg.mpdl.inge.pubman.web.withdrawItem;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.depositorWS.MyItemsRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Fragment class for editing PubItems. This class provides all functionality for editing, saving
 * and submitting a PubItem including methods for depending dynamic UI components.
 * 
 * @author: Thomas Diebäcker, created 10.01.2007
 * @author: $Author$
 * @version: $Revision$ $LastChangedDate$ Revised by FrM: 09.08.2007 * Checkstyled, commented,
 *           cleaned.
 */
@SuppressWarnings("serial")
public class WithdrawItem extends FacesBean {
  private static Logger logger = Logger.getLogger(WithdrawItem.class);
  // Faces navigation string
  public static final String LOAD_WITHDRAWITEM = "loadWithdrawItem";

  private String withdrawalComment;

  private String valMessage;
  private String creators;

  private String navigationStringToGoBack;

  /**
   * Public constructor.
   */
  public WithdrawItem() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation. Creators handling added by FrM.
   */
  public void init() {
    // Perform initializations inherited from our superclass
    //super.init();

    // Fill creators property.
    StringBuffer creators = new StringBuffer();
    for (CreatorVO creator : getPubItem().getMetadata().getCreators()) {
      if (creators.length() > 0) {
        creators.append("; ");
      }
      if (creator.getType() == CreatorVO.CreatorType.PERSON) {
        creators.append(creator.getPerson().getFamilyName());
        if (creator.getPerson().getGivenName() != null) {
          creators.append(", ");
          creators.append(creator.getPerson().getGivenName());
        }
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION) {
        String name =
            creator.getOrganization().getName() != null ? creator.getOrganization().getName() : "";
        creators.append(name);
      }
    }
    this.creators = creators.toString();

    if (logger.isDebugEnabled()) {
      if (this.getPubItem() != null && this.getPubItem().getVersion() != null) {
        logger.debug("Item that is being withdrawn: "
            + this.getPubItem().getVersion().getObjectId());
      } else {
        logger.error("NO ITEM GIVEN");
      }
    }
  }

  /**
   * Deliveres a reference to the currently edited item. This is a shortCut for the method in the
   * ItemController.
   * 
   * @return the item that is currently edited
   */
  public final PubItemVO getPubItem() {
    return (this.getItemControllerSessionBean().getCurrentPubItem());
  }

  /**
   * Saves the item.
   * 
   * TODO FrM: Revise this when the new item list is available.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public final String withdraw() {
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    String retVal;
    String navigateTo = getSessionBean().getNavigationStringToGoBack();
    if (navigateTo == null) {
      navigateTo = ViewItemFull.LOAD_VIEWITEM;
    }

    if (withdrawalComment == null || "".equals(withdrawalComment)) {
      error(getMessage(DepositorWSPage.NO_WITHDRAWAL_COMMENT_GIVEN));
      return null;
    }

    retVal =
        this.getItemControllerSessionBean().withdrawCurrentPubItem(navigateTo, withdrawalComment);

    // redirect to the view item page afterwards (if no error occured)
    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      try {
        fc.getExternalContext().redirect(
            request.getContextPath()
                + "/faces/viewItemFullPage.jsp?itemId="
                + this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
                    .getObjectId());
      } catch (IOException e) {
        logger.error("Could not redirect to View Item Page", e);
      }
    }

    if (!ErrorPage.LOAD_ERRORPAGE.equals(retVal)) {
      info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_WITHDRAWN));
    }

    PubItemListSessionBean pubItemListSessionBean =
        (PubItemListSessionBean) getSessionBean(PubItemListSessionBean.class);
    if (pubItemListSessionBean != null) {
      pubItemListSessionBean.update();
    }

    return retVal;
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public final String cancel() {
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    try {
      fc.getExternalContext().redirect(
          request.getContextPath() + "/faces/viewItemFullPage.jsp?itemId="
              + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
    } catch (IOException e) {
      logger.error("Could not redirect to View Item Page", e);
    }
    return MyItemsRetrieverRequestBean.LOAD_DEPOSITORWS;
  }

  /**
   * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
   * 
   * @return a reference to the scoped data bean
   */
  public final ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  /**
   * Returns the WithdrawItemSessionBean.
   * 
   * @return a reference to the scoped data bean (WithdrawItemSessionBean)
   */
  protected final WithdrawItemSessionBean getSessionBean() {
    return (WithdrawItemSessionBean) getSessionBean(WithdrawItemSessionBean.class);
  }

  public String getWithdrawalComment() {
    return withdrawalComment;
  }

  public void setWithdrawalComment(String withdrawalComment) {
    this.withdrawalComment = withdrawalComment;
  }

  public String getValMessage() {
    return valMessage;
  }

  public void setValMessage(String valMessage) {
    this.valMessage = valMessage;
  }

  public final String getNavigationStringToGoBack() {
    return navigationStringToGoBack;
  }

  public final void setNavigationStringToGoBack(final String navigationStringToGoBack) {
    this.navigationStringToGoBack = navigationStringToGoBack;
  }

  public String getCreators() {
    return creators;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

}
