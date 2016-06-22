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
 * Wissenschaft e.V. All rights reserved.
 */

package de.mpg.escidoc.pubman.util;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;

/**
 * TODO Description
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EventLogEntryVOPresentation extends EventLogEntryVO {
  private VersionHistoryEntryVO versionHistoryVO;
  private InternationalizationHelper i18nHelper;


  public EventLogEntryVOPresentation(EventLogEntryVO eventLogVO,
      VersionHistoryEntryVO versionHistoryVO) {
    this.setComment(eventLogVO.getComment());
    this.setDate(eventLogVO.getDate());
    this.setType(eventLogVO.getType());
    this.versionHistoryVO = versionHistoryVO;
    i18nHelper =
        (InternationalizationHelper) FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().get(InternationalizationHelper.BEAN_NAME);

  }

  public String getCurrentTypeLabel() {
    ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    if (getType() != null) {
      switch (getType()) {
        case CREATE:
          return labelBundle.getString("ViewItemLog_lblCreate");
        case RELEASE:
          return labelBundle.getString("ViewItemLog_lblRelease");
        case SUBMIT:
          return labelBundle.getString("ViewItemLog_lblSubmit");
        case UPDATE:
          return labelBundle.getString("ViewItemLog_lblUpdate");
        case WITHDRAW:
          return labelBundle.getString("ViewItemLog_lblWithdraw");
        case IN_REVISION:
          return labelBundle.getString("ViewItemLog_lblInRevision");
        case ASSIGN_VERSION_PID:
          return labelBundle.getString("ViewItemLog_lblAssignVersionPid");

      }
    }
    return "";
  }

  /** Returns all comments except for update, because update-comments are not possible */
  public String getFormattedComment() {
    if (getType() == EventLogEntryVO.EventType.UPDATE) {
      return "Item updated";
    } else
      return getComment();

  }

  public String getFormattedDate() {
    return CommonUtils.formatTimestamp(getDate());
  }

  public VersionHistoryEntryVO getVersionHistoryVO() {
    return versionHistoryVO;
  }

  public void setVersionHistoryVO(VersionHistoryEntryVO versionHistoryVO) {
    this.versionHistoryVO = versionHistoryVO;
  }
}
