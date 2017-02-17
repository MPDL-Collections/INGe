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

package de.mpg.mpdl.inge.pubman.exceptions;

import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.pubman.depositing.DepositingException;

/**
 * Exception class used for item which are locked.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 19.09.2007
 */
public class PubItemLockedException extends DepositingException {
  /**
   * The reference of the pubitem that is locked.
   */
  private ItemRO pubItemRef;

  /**
   * Creates a new instance with the given pubItemRef and cause.
   * 
   * @param pubItemRef The reference of the pubitem that is locked.
   * @param cause The throwable which caused this exception.
   */
  public PubItemLockedException(ItemRO pubItemRef, Throwable cause) {
    super(pubItemRef, cause);
  }

  /**
   * @return The reference of the pubitem that is locked.
   */
  public ItemRO getPubItemRef() {
    return pubItemRef;
  }
}
