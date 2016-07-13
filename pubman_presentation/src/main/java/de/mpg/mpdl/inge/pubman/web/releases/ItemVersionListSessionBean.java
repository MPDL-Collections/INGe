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

package de.mpg.mpdl.inge.pubman.web.releases;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO.EventType;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.EventLogEntryVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.VersionHistoryVOPresentation;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;

/**
 * Keeps all attributes that are used for the whole session by the ReleaseHistory, the Item Log and
 * the Revision History.
 * 
 * @author: Tobias Schraut, created 18.10.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ItemVersionListSessionBean extends FacesBean {
  public static final String BEAN_NAME = "ItemVersionListSessionBean";
  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(ItemVersionListSessionBean.class);

  private List<VersionHistoryVOPresentation> versionList =
      new ArrayList<VersionHistoryVOPresentation>();

  private List<EventLogEntryVOPresentation> releaseList =
      new ArrayList<EventLogEntryVOPresentation>();

  private List<EventLogEntryVOPresentation> eventLogList =
      new ArrayList<EventLogEntryVOPresentation>();


  /**
   * Public constructor.
   */
  public ItemVersionListSessionBean() {
    this.init();
  }

  /**
   * Returns a reference to the scoped data bean (the ItemControllerSessionBean).
   * 
   * @return a reference to the scoped data bean
   */
  protected ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class);
  }

  public List<VersionHistoryVOPresentation> getVersionList() {
    return versionList;
  }

  public void setVersionList(List<VersionHistoryVOPresentation> versionList) {
    this.versionList = versionList;
  }

  public void initVersionLists(List<VersionHistoryEntryVO> vList) {
    this.versionList = new ArrayList<VersionHistoryVOPresentation>();

    for (VersionHistoryEntryVO vEntry : vList) {
      this.versionList.add(new VersionHistoryVOPresentation(vEntry));
    }


    this.releaseList = new ArrayList<EventLogEntryVOPresentation>();

    this.eventLogList = new ArrayList<EventLogEntryVOPresentation>();

    for (VersionHistoryVOPresentation vEntry : versionList) {



      List<EventLogEntryVO> eventList = vEntry.getEvents();
      for (EventLogEntryVO eEntry : eventList) {
        // if state=released add to release list
        if (eEntry.getType() == EventType.RELEASE) {
          releaseList.add(new EventLogEntryVOPresentation(eEntry, vEntry));
        }

        // add all eventlog-entries to eventloglist
        eventLogList.add(new EventLogEntryVOPresentation(eEntry, vEntry));
      }



    }


  }

  public void resetVersionLists() {
    this.versionList = null;

    this.releaseList = null;

    this.eventLogList = null;
  }



  public List<EventLogEntryVOPresentation> getEventLogList() {
    return eventLogList;
  }

  public void setEventLogList(List<EventLogEntryVOPresentation> eventLogList) {
    this.eventLogList = eventLogList;
  }



  public void setReleaseList(List<EventLogEntryVOPresentation> releaseList) {
    this.releaseList = releaseList;
  }

  public List<EventLogEntryVOPresentation> getReleaseList() {
    return releaseList;
  }


}
