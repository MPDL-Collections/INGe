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

package de.mpg.mpdl.inge.pubman.web.itemLog;

import java.util.List;

import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.releases.ItemVersionListSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;

/**
 * Fragment class for viewItemLog.jspf
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@ManagedBean(name = "ViewItemLog")
@SuppressWarnings("serial")
public class ViewItemLog extends FacesBean {
  public static final String BEAN_NAME = "ViewItemLog";

  private static final Logger logger = Logger.getLogger(ViewItemLog.class);

  public static final String LOAD_ITEM_LOG = "loadViewItemLog";

  public ViewItemLog() {
    this.init();
  }

  /**
   * Callback method that is called whenever a page containing this page fragment is navigated to,
   * either directly via a URL, or indirectly via page navigation.
   */
  public void init() {
    if (this.getItemVersionListSessionBean().getVersionList() == null) {
      this.getItemVersionListSessionBean().initVersionLists(
          getVersionHistory(this.getItemControllerSessionBean().getCurrentPubItem().getVersion()
              .getObjectId()));
    }
  }

  /**
   * Retrieves all event logs for the current pubitem.
   * 
   * @param itemID the id of the item for which the releases should be retrieved
   * @return the list of EventLogEntryVOs
   */
  public List<VersionHistoryEntryVO> getVersionHistory(String itemID) {

    try {
      return this.getItemControllerSessionBean().retrieveVersionHistoryForItem(itemID);
    } catch (Exception e) {
      logger.error("Could not retrieve release list for Item " + itemID, e);
    }

    return null;
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return (ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean");
  }

  private ItemVersionListSessionBean getItemVersionListSessionBean() {
    return (ItemVersionListSessionBean) FacesTools.findBean("ItemVersionListSessionBean");
  }

  public String getDummy() {
    return "";
  }
}
