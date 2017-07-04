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

package de.mpg.mpdl.inge.pubman.web.util.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.multipleimport.BaseImportLog;
import de.mpg.mpdl.inge.pubman.web.multipleimport.DbTools;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLogItem;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ImportSurveyor extends Thread {
  private static final Logger logger = Logger.getLogger(ImportSurveyor.class);

  private boolean signal = false;
  private long interval;

  public ImportSurveyor() {
    this.setName("ImportSurveyor");

    try {
      this.interval =
          Long.parseLong(PropertyReader.getProperty("escidoc.import.surveyor.interval"));
    } catch (final Exception e) {
      throw new RuntimeException("Error initializing import surveyor");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    do {
      try {
        Thread.sleep(this.interval * 60 * 1000);
      } catch (final InterruptedException e) {
        ImportSurveyor.logger.info("Import surveyor interrupted");
        return;
      }

      if (this.signal) {
        ImportSurveyor.logger.info("Import surveyor interrupted");
        return;
      }

      ImportSurveyor.logger.info("Import surveyor checks logs...");

      Connection connection = null;
      ResultSet rs = null;
      PreparedStatement ps = null;

      // Searches for Import-Items which are in status "pending" OR "rollback" AND which have NOT
      // been changed in the last 60 Minutes
      final String query =
          "select id from import_log where (status = 'PENDING' or status = 'ROLLBACK') "
              + "and id not in (select parent from import_log_item "
              + "               where localtimestamp - interval '60 minutes' < startdate)";

      try {
        connection = DbTools.getNewConnection();
        ps = connection.prepareStatement(query);
        rs = ps.executeQuery();
        while (rs.next()) {
          final int id = rs.getInt("id");
          ImportSurveyor.logger.warn("Unfinished import detected (" + id
              + "). Finishing it with status FATAL.");
          final ImportLog log = ImportLog.getImportLog(id, true, connection);

          for (final ImportLogItem item : log.getItems()) {
            if (item.getEndDate() == null) {
              log.activateItem(item);
              log.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_terminate_item",
                  connection);
              log.finishItem(connection);
            }
          }

          log.startItem("import_process_aborted_unexpectedly", connection);
          log.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_failed", connection);
          log.finishItem(connection);
          log.close(connection);
        }
      } catch (final Exception e) {
        ImportSurveyor.logger.error("Error checking database for unfinished imports", e);
      } finally {
        DbTools.closeResultSet(rs);
        DbTools.closePreparedStatement(ps);
        DbTools.closeConnection(connection);
      }
    } while (!this.signal);

    ImportSurveyor.logger.info("Import surveyor interrupted");
  }

  /**
   * Signals this thread to finish itself.
   */
  public void terminate() {
    ImportSurveyor.logger.info("Import surveyor signalled to terminate.");
    this.signal = true;
  }
}
