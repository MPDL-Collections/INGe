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

package de.mpg.mpdl.inge.validation.util;


/**
 * Identifier class for XSLT transformer cache.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class CacheTriple extends CacheTuple {

  private String validationPoint;

  /**
   * Constructor.
   * 
   * @param schemaName Context.
   * @param contentModel Content-Model.
   * @param validationPoint Validation Point.
   */
  public CacheTriple(final String schemaName, final String contentModel,
      final String validationPoint) {
    super(schemaName, contentModel);
    this.validationPoint = validationPoint;
    this.hash = (schemaName + contentModel + validationPoint).length();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object other) {
    if (this.schemaName != null && this.contentModel != null && this.validationPoint != null
        && other instanceof CacheTriple) {
      return (this.schemaName.equals(((CacheTriple) other).schemaName)
          && this.contentModel.equals(((CacheTriple) other).contentModel) && this.validationPoint
            .equals(((CacheTriple) other).validationPoint));
    } else {
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "[" + schemaName + "|" + contentModel + "|" + validationPoint + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return hash;
  }
}
