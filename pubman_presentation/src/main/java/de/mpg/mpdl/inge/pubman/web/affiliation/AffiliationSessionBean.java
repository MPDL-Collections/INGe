/*
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

package de.mpg.mpdl.inge.pubman.web.affiliation;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.AffiliationVOPresentation;

/**
 * Keeps all attributes that are used for the whole session by the Affiliation components.
 * 
 * @author: Hugo Niedermaier, Basics by Thomas Diebäcker, created 30.05.2007
 * @version: $Revision$ $LastChangedDate$ Revised by NiH: 13.08.2007
 */
public class AffiliationSessionBean extends FacesBean {
  static final long serialVersionUID = 1L;

  // NiH: store different modes for the affiliation selection
  // to distinguish between the actions add and select in the edit item mask
  private boolean add = false;
  // to distinguish between the use case browse by affiliation and add/select in edit item
  private boolean browseByAffiliation = false;

  public static final String BEAN_NAME = "AffiliationSessionBean";

  // instance of the Affiliation Tree
  // private TreeModel treeAffiliation = new ChildPropertyTreeModel();
  // flag to control the dynamic creation of the tree from outside
  private boolean wasInit = false;
  private List<AffiliationVOPresentation> currentAffiliationList =
      new ArrayList<AffiliationVOPresentation>();


  // NiH: list of OrganizationVO's selected in EditItem page
  protected List<OrganizationVO> organizationParentVO = new ArrayList<OrganizationVO>();

  protected String organizationParentValueBinding = new String();
  protected int indexComponent;

  public AffiliationSessionBean() {}

  // /**
  // * This method is called when this bean is initially added to session scope. Typically, this
  // * occurs as a result of evaluating a value binding or method binding expression, which utilizes
  // * the managed bean facility to instantiate this bean and store it into session scope.
  // */
  // public void init() {
  // // Perform initializations inherited from our superclass
  // //super.init();
  // }

  public List<AffiliationVOPresentation> getCurrentAffiliationList() {
    return currentAffiliationList;
  }

  public void setCurrentAffiliationList(List<AffiliationVOPresentation> list) {
    this.currentAffiliationList = list;
  }

  public boolean isBrowseByAffiliation() {
    return browseByAffiliation;
  }

  public void setBrowseByAffiliation(boolean browseByAffiliation) {
    this.browseByAffiliation = browseByAffiliation;
  }

  /**
   * NiH: returns the list of OrganizationVO's selected in EditItem page
   * 
   * @return List<OrganizationVO>
   */
  public List<OrganizationVO> getOrganizationParentVO() {
    return organizationParentVO;
  }

  public void setOrganizationParentVO(List<OrganizationVO> organizationParentVO) {
    this.organizationParentVO = organizationParentVO;
  }

  public String getOrganizationParentValueBinding() {
    return organizationParentValueBinding;
  }

  public void setOrganizationParentValueBinding(String organizationParentValueBinding) {
    this.organizationParentValueBinding = organizationParentValueBinding;
  }

  public int getIndexComponent() {
    return indexComponent;
  }

  public void setIndexComponent(int indexComponent) {
    this.indexComponent = indexComponent;
  }

  public boolean isAdd() {
    return add;
  }

  public void setAdd(boolean add) {
    this.add = add;
  }

  /*
   * public TreeModel getTreeAffiliation() { return treeAffiliation; }
   * 
   * public void setTreeAffiliation(TreeModel treeAffiliation) { this.treeAffiliation =
   * treeAffiliation; }
   */
  public boolean isWasInit() {
    return wasInit;
  }

  public void setWasInit(boolean wasInit) {
    this.wasInit = wasInit;
  }

}
