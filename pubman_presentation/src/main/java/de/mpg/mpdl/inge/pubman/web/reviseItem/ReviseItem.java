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

package de.mpg.mpdl.inge.pubman.web.reviseItem;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.pubman.PubItemDepositing;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.ErrorPage;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.qaws.MyTasksRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;


/**
 * Backing bean for ReviseItem.jspf
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@SuppressWarnings("serial")
public class ReviseItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(ReviseItem.class);
  // Faces navigation string
  public static final String LOAD_REVISEITEM = "loadReviseItem";
  // public static final String JSP_NAME = "ReviseItemPage.jsp";

  private String reviseComment;

  // private String valMessage = null;
  private String creators;

  private String navigationStringToGoBack;

  public ReviseItem() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation. Creators handling added by FrM.
   */
  public final void init() {
    // Perform initializations inherited from our superclass
    // super.init();

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
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION
          && creator.getOrganization().getName() != null) {
        creators.append(creator.getOrganization().getName());
      }
    }
    this.creators = creators.toString();

    if (logger.isDebugEnabled()) {
      if (this.getPubItem() != null && this.getPubItem().getVersion() != null) {
        logger.debug("Item that is being revised: " + this.getPubItem().getVersion().getObjectId());
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
   * Submits the item.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public final String revise() {
    FacesContext fc = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
    String retVal;
    String navigateTo = ViewItemFull.LOAD_VIEWITEM;
    /*
     * String navigateTo = getSessionBean().getNavigationStringToGoBack();
     * 
     * if(navigateTo == null) { navigateTo = ViewItemFull.LOAD_VIEWITEM; }
     */
    logger.debug("Now revising, then go to " + navigateTo);

    retVal = this.getItemControllerSessionBean().reviseCurrentPubItem(reviseComment, navigateTo);

    if (retVal.compareTo(ErrorPage.LOAD_ERRORPAGE) != 0) {
      info(getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_REVISED));
    }


    if (ViewItemFull.LOAD_VIEWITEM.equals(retVal)) {
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



    return MyTasksRetrieverRequestBean.LOAD_QAWS;
  }



  /**
   * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
   * 
   * @return a reference to the scoped data bean
   */
  public final ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  // public String getValMessage() {
  // return valMessage;
  // }
  //
  // public void setValMessage(String valMessage) {
  // this.valMessage = valMessage;
  // }

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

  public boolean getIsStandardWorkflow() {
    return getItemControllerSessionBean().getCurrentWorkflow().equals(
        PubItemDepositing.WORKFLOW_STANDARD);
  }

  public boolean getIsSimpleWorkflow() {
    return getItemControllerSessionBean().getCurrentWorkflow().equals(
        PubItemDepositing.WORKFLOW_SIMPLE);
  }

  public String getReviseComment() {
    return reviseComment;
  }

  public void setReviseComment(String reviseComment) {
    this.reviseComment = reviseComment;
  }

}
