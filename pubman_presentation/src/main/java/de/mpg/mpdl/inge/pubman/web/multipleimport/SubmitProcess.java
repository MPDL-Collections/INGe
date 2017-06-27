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

package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.sql.Connection;

import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class SubmitProcess extends Thread {
  private final ImportLog importLog;
  private AccountUserVO user;
  private final boolean alsoRelease;
  private final String authenticationToken;
  private Connection connection = null;

  public SubmitProcess(ImportLog importLog, boolean alsoRelease, String authenticationToken,
      Connection connection) {
    this.importLog = importLog;
    this.alsoRelease = alsoRelease;
    this.authenticationToken = authenticationToken;
    this.connection = connection;

    try {
      this.importLog.reopen(connection);
      this.importLog.startItem("import_process_submit_items", connection);
      this.importLog.addDetail(ErrorLevel.FINE, "import_process_initialize_submit_process",
          connection);
      this.user = new AccountUserVO();
      this.user.setHandle(importLog.getUserHandle());
      this.user.setUserid(importLog.getUser());
    } catch (final Exception e) {
      this.importLog.addDetail(ErrorLevel.FATAL, "import_process_initialize_submit_process_error",
          connection);
      this.importLog.addDetail(ErrorLevel.FATAL, e, connection);
      this.importLog.close(connection);
      throw new RuntimeException(e);
    }

    this.importLog.finishItem(connection);
    this.importLog.setPercentage(ImportLog.PERCENTAGE_SUBMIT_START, connection);
  }

  @Override
  public void run() {
    try {
      int itemCount = 0;
      for (final ImportLogItem item : this.importLog.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          itemCount++;
          this.importLog.activateItem(item);
          this.importLog.addDetail(ErrorLevel.FINE, "import_process_schedule_submit",
              this.connection);
          this.importLog.suspendItem(this.connection);
        }
      }

      this.importLog.setPercentage(ImportLog.PERCENTAGE_SUBMIT_SUSPEND, this.connection);
      int counter = 0;

      for (final ImportLogItem item : this.importLog.getItems()) {
        if (item.getItemId() != null && !"".equals(item.getItemId())) {
          this.importLog.activateItem(item);

          try {
            final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();
            final ItemVO itemVO = pubItemService.get(item.getItemId(), this.authenticationToken);
            if (this.alsoRelease) {
              this.importLog.addDetail(ErrorLevel.FINE, "import_process_submit_release_item",
                  this.connection);
              pubItemService.releasePubItem(item.getItemId(), itemVO.getModificationDate(),
                  "Batch submit/release from import " + this.importLog.getMessage(),
                  this.authenticationToken);
              this.importLog.addDetail(ErrorLevel.FINE, "import_process_submit_release_successful",
                  this.connection);
            } else {
              this.importLog.addDetail(ErrorLevel.FINE, "import_process_submit_item",
                  this.connection);
              pubItemService.submitPubItem(item.getItemId(), itemVO.getModificationDate(),
                  "Batch submit from import " + this.importLog.getMessage(),
                  this.authenticationToken);
              this.importLog.addDetail(ErrorLevel.FINE, "import_process_submit_successful",
                  this.connection);
            }

            this.importLog.finishItem(this.connection);
          } catch (final Exception e) {
            this.importLog.addDetail(ErrorLevel.WARNING, "import_process_submit_failed",
                this.connection);
            this.importLog.addDetail(ErrorLevel.WARNING, e, this.connection);
            this.importLog.finishItem(this.connection);
          }

          counter++;
          this.importLog.setPercentage(ImportLog.PERCENTAGE_SUBMIT_END * counter / itemCount
              + ImportLog.PERCENTAGE_SUBMIT_SUSPEND, this.connection);
        }
      }

      this.importLog.startItem("import_process_submit_finished", this.connection);
      this.importLog.finishItem(this.connection);
      this.importLog.close(this.connection);
    } finally {
      DbTools.closeConnection(this.connection);
    }
  }
}
