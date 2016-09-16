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

package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.IgnoreForCleanup;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * This class combines a abstract value with an optional language attribute.
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class SubjectVO extends ValueObject implements Cloneable {

  private static final long serialVersionUID = 1L;

  @IgnoreForCleanup
  private String language;

  private String value;
  private String type;

  /**
   * Creates a new instance with the given value.
   */
  public SubjectVO(String value) {
    super();
    this.value = value;
  }

  /**
   * Creates a new instance.
   */
  public SubjectVO() {
    super();
  }

  /**
   * Creates a new instance with the given value and language.
   * 
   * @param value The abstract value
   * @param language The abstract language
   */
  public SubjectVO(String value, String language) {
    this.value = value;
    this.language = language;
  }

  /**
   * Creates a new instance with the given value and language.
   * 
   * @param value The abstract value
   * @param language The abstract language
   * @param type The type of the abstract
   */
  public SubjectVO(String value, String language, String type) {
    this.value = value;
    this.language = language;
    this.type = type;
  }

  /**
   * Delivers the language of the abstract.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Delivers the value of the abstract.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the language of the abstract.
   * 
   * @param newVal newVal
   */
  public void setLanguage(String newVal) {
    language = newVal;
  }

  /**
   * Sets the value of the abstract.
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
    SubjectVO vo = new SubjectVO();
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
    if (o == null || !(o instanceof SubjectVO)) {
      return false;
    }
    SubjectVO vo = (SubjectVO) o;
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
