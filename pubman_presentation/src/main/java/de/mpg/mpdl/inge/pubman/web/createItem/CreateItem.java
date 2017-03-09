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

package de.mpg.mpdl.inge.pubman.web.createItem;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItem;
import de.mpg.mpdl.inge.pubman.web.editItem.EditItemSessionBean;

/**
 * Fragment class for CreateItem.
 * 
 * @author: Thomas Diebäcker, created 11.10.2007
 * @author: $Author$ last modification
 * @version: $Revision$ $LastChangedDate$
 */
@SuppressWarnings("serial")
public class CreateItem extends FacesBean {
  public static final String BEAN_NAME = "CreateItem";

  private static final Logger logger = Logger.getLogger(CreateItem.class);

  public final static String LOAD_CREATEITEM = "loadCreateItem";

  public enum SubmissionMethod {
    FULL_SUBMISSION, MULTIPLE_IMPORT, EASY_SUBMISSION
  }

  private SubmissionMethod method = SubmissionMethod.FULL_SUBMISSION;
  private String target = EditItem.LOAD_EDITITEM;

  public CreateItem() {}

  public String confirmSelection() {
    return this.target;
  }

  public String newSubmission() {
    this.target = EditItem.LOAD_EDITITEM;
    this.method = SubmissionMethod.FULL_SUBMISSION;

    String genreBundle = "Genre_ARTICLE";
    String navigateTo = "";

    // first clear the EditItemSessionBean
    this.getEditItemSessionBean().initEmptyComponents();

    // set the current submission method for edit item to full submission (for GUI purpose)
    this.getEditItemSessionBean().setCurrentSubmission(
        EditItemSessionBean.SUBMISSION_METHOD_FULL_SUBMISSION);

    // if there is only one context for this user we can skip the CreateItem-Dialog and
    // create the new item directly
    if (this.getContextListSessionBean().getDepositorContextList().size() == 0) {
      logger.warn("The user does not have privileges for any context.");
      return null;
    }

    if (this.getContextListSessionBean().getDepositorContextList().size() == 1
        && this.getContextListSessionBean().getOpenContextsAvailable()) {
      ContextVO contextVO = this.getContextListSessionBean().getDepositorContextList().get(0);
      navigateTo =
          this.getItemControllerSessionBean().createNewPubItem(EditItem.LOAD_EDITITEM,
              contextVO.getReference());

      // re-init the edit item bean to make sure that all data is removed
      if (this.getItemControllerSessionBean().getCurrentPubItem() != null) {
        if (!contextVO.getAdminDescriptor().getAllowedGenres()
            .contains(MdsPublicationVO.Genre.ARTICLE)) {
          this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
              .setGenre(contextVO.getAdminDescriptor().getAllowedGenres().get(0));
        } else {
          this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
              .setGenre(MdsPublicationVO.Genre.ARTICLE);
        }

        this.getEditItemSessionBean().setGenreBundle(genreBundle);
        this.getEditItem().setItem(null);
        this.getEditItem().getGenreSelect().resetValue();
        this.getEditItem().init();
      }
    } else {
      // more than one context exists for this user; let him choose the right one
      navigateTo =
          this.getItemControllerSessionBean().createNewPubItem(CreateItem.LOAD_CREATEITEM,
              this.getContextListSessionBean().getDepositorContextList().get(0).getReference());

      // re-init the edit item bean to make sure that all data is removed
      if (this.getItemControllerSessionBean().getCurrentPubItem() != null) {
        this.getItemControllerSessionBean().getCurrentPubItem().getMetadata()
            .setGenre(Genre.ARTICLE);
        this.getEditItemSessionBean().setGenreBundle(genreBundle);
        this.getEditItem().setItem(null);
        this.getEditItem().init();
      }
    }

    return navigateTo;
  }

  private ContextListSessionBean getContextListSessionBean() {
    return (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return (EditItemSessionBean) getSessionBean(EditItemSessionBean.class);
  }

  protected EditItem getEditItem() {
    return (EditItem) FacesContext.getCurrentInstance().getApplication().getVariableResolver()
        .resolveVariable(FacesContext.getCurrentInstance(), EditItem.BEAN_NAME);
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  public boolean getMultiple() {
    return (getMethod() == SubmissionMethod.MULTIPLE_IMPORT);
  }

  /**
   * @return the target
   */
  public String getTarget() {
    return target;
  }

  /**
   * @param target the target to set
   */
  public void setTarget(String target) {
    this.target = target;
  }

  /**
   * @return the method
   */
  public SubmissionMethod getMethod() {
    return method;
  }

  /**
   * @param method the method to set
   */
  public void setMethod(SubmissionMethod method) {
    this.method = method;
  }
}
