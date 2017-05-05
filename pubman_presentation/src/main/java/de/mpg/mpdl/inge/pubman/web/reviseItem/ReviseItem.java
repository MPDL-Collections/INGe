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

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PublicationAdminDescriptorVO;
import de.mpg.mpdl.inge.pubman.web.DepositorWSPage;
import de.mpg.mpdl.inge.pubman.web.itemList.PubItemListSessionBean;
import de.mpg.mpdl.inge.pubman.web.qaws.MyTasksRetrieverRequestBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;

/**
 * Backing bean for ReviseItem.jspf
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ReviseItem")
@SuppressWarnings("serial")
public class ReviseItem extends FacesBean {
  private static final Logger logger = Logger.getLogger(ReviseItem.class);

  public static final String LOAD_REVISEITEM = "loadReviseItem";

  private String reviseComment;
  private String creators;

  public ReviseItem() {
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
      } else if (creator.getType() == CreatorVO.CreatorType.ORGANIZATION
          && creator.getOrganization().getName() != null) {
        creators.append(creator.getOrganization().getName());
      }
    }

    this.creators = creators.toString();
  }

  /**
   * Deliveres a reference to the currently edited item. This is a shortCut for the method in the
   * ItemController.
   * 
   * @return the item that is currently edited
   */
  public PubItemVO getPubItem() {
    return this.getItemControllerSessionBean().getCurrentPubItem();
  }

  public String revise() {
    final String navigateTo = ViewItemFull.LOAD_VIEWITEM;

    final String retVal =
        this.getItemControllerSessionBean().reviseCurrentPubItem(this.reviseComment, navigateTo);

    if (ViewItemFull.LOAD_VIEWITEM.equals(retVal)) {
      this.info(this.getMessage(DepositorWSPage.MESSAGE_SUCCESSFULLY_REVISED));
      this.getPubItemListSessionBean().update();

      try {
        FacesTools.getExternalContext().redirect(FacesTools.getRequest().getContextPath()
            + "/faces/ViewItemFullPage.jsp?itemId="
            + this.getItemControllerSessionBean().getCurrentPubItem().getVersion().getObjectId());
      } catch (final IOException e) {
        ReviseItem.logger.error("Could not redirect to View Item Page", e);
      }
    }

    return retVal;
  }

  /**
   * Cancels the editing.
   * 
   * @return string, identifying the page that should be navigated to after this methodcall
   */
  public String cancel() {
    return MyTasksRetrieverRequestBean.LOAD_QAWS;
  }

  // public String getNavigationStringToGoBack() {
  // return this.navigationStringToGoBack;
  // }
  //
  // public void setNavigationStringToGoBack(final String navigationStringToGoBack) {
  // this.navigationStringToGoBack = navigationStringToGoBack;
  // }

  public String getCreators() {
    return this.creators;
  }

  public void setCreators(String creators) {
    this.creators = creators;
  }

  public boolean getIsStandardWorkflow() {
    return PublicationAdminDescriptorVO.Workflow.STANDARD == this.getItemControllerSessionBean()
        .getCurrentWorkflow();
  }

  public boolean getIsSimpleWorkflow() {
    return PublicationAdminDescriptorVO.Workflow.SIMPLE == this.getItemControllerSessionBean()
        .getCurrentWorkflow();
  }

  public String getReviseComment() {
    return this.reviseComment;
  }

  public void setReviseComment(String reviseComment) {
    this.reviseComment = reviseComment;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private PubItemListSessionBean getPubItemListSessionBean() {
    return (PubItemListSessionBean) FacesTools.findBean("PubItemListSessionBean");
  }
}
