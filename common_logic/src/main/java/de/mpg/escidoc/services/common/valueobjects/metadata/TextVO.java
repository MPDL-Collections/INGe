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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.IgnoreForCleanup;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * This class combines a text value with an optional language attribute.
 * 
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:58
 */
public class TextVO extends ValueObject implements Cloneable {
  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */
  private static final long serialVersionUID = 1L;

  @IgnoreForCleanup
  private String language;

  private String value;
  private String type;

  /**
   * Creates a new instance with the given value.
   */
  public TextVO(String value) {
    super();
    this.value = value;
  }

  /**
   * Creates a new instance.
   */
  public TextVO() {
    super();
  }

  /**
   * Creates a new instance with the given value and language.
   * 
   * @param value The text value
   * @param language The text language
   */
  public TextVO(String value, String language) {
    this.value = value;
    this.language = language;
  }

  /**
   * Creates a new instance with the given value and language.
   * 
   * @param value The text value
   * @param language The text language
   * @param type The type of the text
   */
  public TextVO(String value, String language, String type) {
    this.value = value;
    this.language = language;
    this.type = type;
  }

  /**
   * Delivers the language of the text.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Delivers the value of the text.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the language of the text.
   * 
   * @param newVal newVal
   */
  public void setLanguage(String newVal) {
    language = newVal;
  }

  /**
   * Sets the value of the text.
   * 
   * @param newVal newVal
   */
  public void setValue(String newVal) {
    value = newVal;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    TextVO vo = new TextVO();
    vo.setLanguage(getLanguage());
    vo.setValue(getValue());
    vo.setType(getType());
    return vo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals()
   */
  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof TextVO)) {
      return false;
    }
    TextVO vo = (TextVO) o;
    return equals(getLanguage(), vo.getLanguage()) && equals(getValue(), vo.getValue())
        && equals(getType(), vo.getType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return value;
  }
}
