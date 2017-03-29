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

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.AffiliationRefFilter;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pubman.web.affiliation.AffiliationBean;
import de.mpg.mpdl.inge.pubman.web.search.AffiliationDetail;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.util.AdminHelper;
import de.mpg.mpdl.inge.util.PropertyReader;

@SuppressWarnings("serial")
public class AffiliationVOPresentation extends AffiliationVO implements
    Comparable<AffiliationVOPresentation> {
  private static final Logger logger = Logger.getLogger(AffiliationVOPresentation.class);

  private static final int SHORTENED_NAME_STANDARD_LENGTH = 65;
  private static final int SHORTENED_LEVEL_LENGTH = 5;

  private AffiliationVOPresentation parent = null;

  private List<AffiliationVO> predecessors = new ArrayList<AffiliationVO>();
  private List<AffiliationVO> successors = null;
  private List<AffiliationVOPresentation> children = null;

  private String idPath;
  private String namePath;

  private boolean hasChildren = false;
  private boolean selectedForAuthor = false;

  public AffiliationVOPresentation(AffiliationVO affiliation) {
    super(affiliation);
    this.namePath = this.getDetails().getName();
    this.idPath = this.getReference().getObjectId();
    this.predecessors = this.getAffiliationVOfromRO(this.getPredecessorAffiliations());
    this.hasChildren = affiliation.getHasChildren();
  }

  public List<AffiliationVOPresentation> getChildren() throws Exception {
    if (this.children == null && this.isHasChildren()) {
      this.children =
          ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean"))
              .searchChildAffiliations(this);
    }

    return this.children;
  }

  public MdsOrganizationalUnitDetailsVO getDetails() {
    if (this.getMetadataSets().size() > 0
        && this.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return (MdsOrganizationalUnitDetailsVO) this.getMetadataSets().get(0);
    } else {
      return new MdsOrganizationalUnitDetailsVO();
    }
  }

  public boolean getMps() {
    try {
      final String rootAffiliationMPG =
          PropertyReader.getProperty("escidoc.pubman.root.organisation.id");

      return this.getReference().getObjectId().equals(rootAffiliationMPG);
    } catch (final Exception e) {
      AffiliationVOPresentation.logger.error("Error reading Properties", e);
      return false;
    }
  }

  public boolean getTopLevel() {
    return (this.parent == null);
  }

  // /**
  // * This returns a description of the affiliation in a html form.
  // *
  // * @return html description
  // */
  // public String getHtmlDescription() {
  // StringBuffer html = new StringBuffer();
  //
  // html.append("<html><head></head><body>");
  // html.append("<div class=\"affDetails\"><h1>"
  // + this.i18nHelper.getLabel("AffiliationTree_txtHeadlineDetails") + "</h1>");
  // html.append("<div class=\"formField\">");
  //
  // if (getDetails().getDescriptions().size() > 0
  // && !"".equals(getDetails().getDescriptions().get(0))) {
  // html.append("<div>");
  // html.append(getDetails().getDescriptions().get(0));
  // html.append("</div><br/>");
  // }
  //
  // for (IdentifierVO identifier : getDetails().getIdentifiers()) {
  // if (!identifier.getId().trim().equals("")) {
  // html.append("<span>, &nbsp;");
  // html.append(identifier.getId());
  // html.append("</span>");
  // }
  // }
  //
  // html.append("</div></div>");
  // html.append("</body></html>");
  //
  // return html.toString();
  // }

  public String startSearch() {
    ((AffiliationBean) FacesTools.findBean("AffiliationBean")).setSelectedAffiliation(this);
    ((AffiliationDetail) FacesTools.findBean("AffiliationDetail")).setAffiliationVO(this);
    return ((AffiliationBean) FacesTools.findBean("AffiliationBean")).startSearch();
  }

  public AffiliationVOPresentation getParent() {
    return this.parent;
  }

  public void setParent(AffiliationVOPresentation parent) {
    this.parent = parent;
  }

  /**
   * Returns the complete path to this affiliation as a string with the name of the affiliations.
   */
  public String getNamePath() {
    return this.namePath;
  }

  public void setNamePath(String path) {
    this.namePath = path;
  }

  /** Returns the complete path to this affiliation as a string with the ids of the affiliations */
  public String getIdPath() {
    return this.idPath;
  }

  public void setIdPath(String idPath) {
    this.idPath = idPath;
  }

  public String getSortOrder() {
    if ("closed".equals(this.getPublicStatus())) {
      return "3" + this.getName().toLowerCase();
    } else if (this.getMps() && "opened".equals(this.getPublicStatus())) {
      return "0" + this.getName().toLowerCase();
    } else if ("opened".equals(this.getPublicStatus())) {
      return "1" + this.getName().toLowerCase();
    } else if ("created".equals(this.getPublicStatus())) {
      return "2" + this.getName().toLowerCase();
    } else {
      return "9" + this.getName().toLowerCase();
    }
  }

  public String getName() {
    if (this.getMetadataSets().size() > 0
        && this.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      return ((MdsOrganizationalUnitDetailsVO) this.getMetadataSets().get(0)).getName();
    }

    return null;
  }

  public String getShortenedName() {
    AffiliationVOPresentation aff = this;
    int level = 0;

    while (!aff.getTopLevel()) {
      aff = aff.getParent();
      level++;
    }

    if (this.getMetadataSets().size() > 0
        && this.getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO) {
      if (((MdsOrganizationalUnitDetailsVO) this.getMetadataSets().get(0)).getName().length() > (AffiliationVOPresentation.SHORTENED_NAME_STANDARD_LENGTH - (level * AffiliationVOPresentation.SHORTENED_LEVEL_LENGTH))) {
        return ((MdsOrganizationalUnitDetailsVO) this.getMetadataSets().get(0))
            .getName()
            .substring(
                0,
                (AffiliationVOPresentation.SHORTENED_NAME_STANDARD_LENGTH - (level * AffiliationVOPresentation.SHORTENED_LEVEL_LENGTH)))
            + "...";
      } else {
        return ((MdsOrganizationalUnitDetailsVO) this.getMetadataSets().get(0)).getName();
      }
    }

    return null;
  }

  public List<String> getUris() {
    final List<IdentifierVO> identifiers = this.getDefaultMetadata().getIdentifiers();
    final List<String> uriList = new ArrayList<String>();

    for (final IdentifierVO identifier : identifiers) {
      if (identifier.getType() != null && identifier.getType().equals(IdentifierVO.IdType.URI)) {
        uriList.add(identifier.getId());
      }
    }

    return uriList;
  }

  public boolean getIsClosed() {
    return this.getPublicStatus().equals("closed");
  }

  @Override
  public int compareTo(AffiliationVOPresentation other) {
    return this.getSortOrder().compareTo(other.getSortOrder());
  }

  private List<AffiliationVO> getAffiliationVOfromRO(List<AffiliationRO> affiliations) { /*
                                                                                          * List<
                                                                                          * AffiliationVO
                                                                                          * >
                                                                                          * transformedAffs
                                                                                          * = new
                                                                                          * ArrayList
                                                                                          * <
                                                                                          * AffiliationVO
                                                                                          * >();
                                                                                          * InitialContext
                                                                                          * initialContext
                                                                                          * = null;
                                                                                          * XmlTransforming
                                                                                          * xmlTransforming
                                                                                          * = null;
                                                                                          * if(
                                                                                          * affiliations
                                                                                          * .size()
                                                                                          * == 0 ) {
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; } try
                                                                                          * {
                                                                                          * initialContext
                                                                                          * = new
                                                                                          * InitialContext
                                                                                          * ();
                                                                                          * xmlTransforming
                                                                                          * = (
                                                                                          * XmlTransforming
                                                                                          * )
                                                                                          * initialContext
                                                                                          * .lookup(
                                                                                          * XmlTransforming
                                                                                          * .
                                                                                          * SERVICE_NAME
                                                                                          * ); for(
                                                                                          * AffiliationRO
                                                                                          * affiliation
                                                                                          * :
                                                                                          * affiliations
                                                                                          * ) {
                                                                                          * OrganizationalUnitHandler
                                                                                          * ouHandler
                                                                                          * =
                                                                                          * ServiceLocator
                                                                                          * .
                                                                                          * getOrganizationalUnitHandler
                                                                                          * ();
                                                                                          * String
                                                                                          * ouXml =
                                                                                          * ouHandler
                                                                                          * .
                                                                                          * retrieve
                                                                                          * (
                                                                                          * affiliation
                                                                                          * .
                                                                                          * getObjectId
                                                                                          * ());
                                                                                          * AffiliationVO
                                                                                          * affVO =
                                                                                          * xmlTransforming
                                                                                          * .
                                                                                          * transformToAffiliation
                                                                                          * (ouXml);
                                                                                          * transformedAffs
                                                                                          * .
                                                                                          * add(affVO
                                                                                          * ); }
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; }
                                                                                          * catch
                                                                                          * (Exception
                                                                                          * e) {
                                                                                          * return
                                                                                          * transformedAffs
                                                                                          * ; }
                                                                                          */
    return this.retrieveAllOrganizationalUnits(affiliations);
  }

  /**
   * @Retrieves list of all contexts for which user has granted privileges @see
   *            LoginHelper.getUserGrants
   * @throws SecurityException
   * @throws TechnicalException
   */
  private List<AffiliationVO> retrieveAllOrganizationalUnits(List<AffiliationRO> affiliations) {

    List<AffiliationVO> transformedAffs = new ArrayList<AffiliationVO>();

    if (affiliations.size() == 0) {
      return transformedAffs;
    }
    try {
      final OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();

      if (affiliations.size() == 1) {

        final String ouXml = ouHandler.retrieve(affiliations.get(0).getObjectId());
        final AffiliationVO affVO = XmlTransformingService.transformToAffiliation(ouXml);
        transformedAffs.add(affVO);
        return transformedAffs;
      } else {
        final FilterTaskParamVO filter = new FilterTaskParamVO();

        final AffiliationRefFilter affiliationFilter = filter.new AffiliationRefFilter();
        filter.getFilterList().add(affiliationFilter);

        for (final AffiliationRO affiliation : affiliations) {
          affiliationFilter.getIdList().add(affiliation);
        }

        final String ouXml = ouHandler.retrieveOrganizationalUnits(filter.toMap());
        transformedAffs = XmlTransformingService.transformToAffiliationList(ouXml);

      }
    } catch (final Exception e) {
    }

    return transformedAffs;
  }

  /**
   * @return the predecessors
   */
  public List<AffiliationVO> getPredecessors() {
    return this.predecessors;
  }

  /**
   * @param predecessors the predecessors to set
   */
  public void setPredecessors(List<AffiliationVO> predecessors) {
    this.predecessors = predecessors;
  }

  /**
   * @return the successors
   */
  public List<AffiliationVO> getSuccessors() {
    this.fetchSuccessors();
    return this.successors;
  }

  /**
   * @return the selectedForAuthor
   */
  public boolean getSelectedForAuthor() {
    return this.selectedForAuthor;
  }

  /**
   * @param selectedForAuthor the selectedForAuthor to set
   */
  public void setSelectedForAuthor(boolean selectedForAuthor) {
    this.selectedForAuthor = selectedForAuthor;
  }

  private void fetchSuccessors() {
    if (this.successors == null) {
      try {
        // TODO tendres: This admin login is neccessary because of bug
        // http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=597
        // If the org tree structure is fetched via search, this is obsolete
        final String userHandle = AdminHelper.getAdminUserHandle();
        final OrganizationalUnitHandler ouHandler =
            ServiceLocator.getOrganizationalUnitHandler(userHandle);
        final String ouXml = ouHandler.retrieveSuccessors(this.reference.getObjectId());
        final Logger logger = Logger.getLogger(AffiliationVOPresentation.class);
        logger.debug(ouXml);
        final List<AffiliationRO> affROs =
            XmlTransformingService.transformToSuccessorAffiliationList(ouXml);
        this.successors = new ArrayList<AffiliationVO>();
        if (affROs != null && affROs.size() > 0) {
          final List<AffiliationVO> affVOs = this.getAffiliationVOfromRO(affROs);
          this.successors = affVOs;
        }
      } catch (final Exception e) {
        this.successors = new ArrayList<AffiliationVO>();
      }
    }
  }

  /**
   * Are predecessors available.
   * 
   * @return true if predecessors are available
   */
  public boolean getHasSuccessors() {
    this.fetchSuccessors();

    return (this.successors.size() != 0);
  }

  /**
   * @return the hasChildren
   */
  public boolean isHasChildren() {
    return this.hasChildren;
  }
}
