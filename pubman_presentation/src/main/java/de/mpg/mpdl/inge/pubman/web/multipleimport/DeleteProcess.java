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

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;

/**
 * A {@link Thread} that deletes asynchronously all imported items of an import.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class DeleteProcess extends Thread {
  private final ImportLog log;
  private AccountUserVO user;

  /**
   * Constructor taking an {@link ImportLog}. Reopens the log again and checks user data.
   * 
   * @param log The {@link ImportLog} whose items should be deleted
   */
  public DeleteProcess(ImportLog log) {
    this.log = log;
    this.log.reopen();
    this.log.setPercentage(5);
    this.log.startItem("import_process_delete_items");
    this.log.addDetail(ErrorLevel.FINE, "import_process_initialize_delete_process");

    try {
      this.user = new AccountUserVO();
      this.user.setHandle(log.getUserHandle());
      this.user.setUserid(log.getUser());
    } catch (final Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_initialize_delete_process_error");
      this.log.addDetail(ErrorLevel.FATAL, e);
      this.log.close();
      throw new RuntimeException(e);
    }

    this.log.finishItem();
    this.log.setPercentage(5);
  }

  /**
   * First schedule all imported items for deletion, then delete them.
   */
  @Override
  public void run() {
    int itemCount = 0;
    for (final ImportLogItem item : this.log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        itemCount++;
        this.log.activateItem(item);
        this.log.addDetail(ErrorLevel.FINE, "import_process_schedule_delete");
        this.log.suspendItem();
      }
    }

    this.log.setPercentage(10);

    int counter = 0;
    for (final ImportLogItem item : this.log.getItems()) {
      if (item.getItemId() != null && !"".equals(item.getItemId())) {
        this.log.activateItem(item);
        this.log.addDetail(ErrorLevel.FINE, "import_process_delete_item");
        final ItemRO itemRO = new ItemRO(item.getItemId());
        try {
          PubItemService.INSTANCE.deletePubItem(itemRO, this.user);
          this.log.addDetail(ErrorLevel.FINE, "import_process_delete_successful");
          this.log.addDetail(ErrorLevel.FINE, "import_process_remove_identifier");
          item.setItemId(null);
          this.log.finishItem();
        } catch (final Exception e) {
          this.log.addDetail(ErrorLevel.WARNING, "import_process_delete_failed");
          this.log.addDetail(ErrorLevel.WARNING, e);
          this.log.finishItem();
        }
        counter++;
        this.log.setPercentage(85 * counter / itemCount + 10);
      }
    }

    this.log.startItem("import_process_delete_finished");
    this.log.finishItem();
    this.log.close();
    this.log.closeConnection();
  }
}
