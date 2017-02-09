/**
 * 
 */
package de.mpg.mpdl.inge.model.valueobjects.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * @author gerga
 * 
 *         JUS-specific VO
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class LegalCaseVO extends ValueObject implements Cloneable {

  /**
   * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
   * 'de.mpg.mpdl.inge.model.valueobjects.ItemVO; local class incompatible: stream classdesc
   * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286'
   * that occur after JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to
   * be compiled twice for testing (once for the Application Server, once for the local test).
   * 
   * @author Johannes Mueller
   */

  private static final long serialVersionUID = 1L;


  private String title;
  private String courtName;
  private String identifier;
  private String datePublished;

  public String getTitle() {
    return title;
  }

  public void setTitle(String newVal) {
    this.title = newVal;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String newVal) {
    this.identifier = newVal;
  }

  public String getDatePublished() {
    return datePublished;
  }

  public void setDatePublished(String newVal) {
    this.datePublished = newVal;
  }

  public String getCourtName() {
    return courtName;
  }

  public void setCourtName(String newVal) {
    this.courtName = newVal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    LegalCaseVO clone = new LegalCaseVO();
    if (getTitle() != null) {

      clone.setTitle(getTitle());
    }
    if (getIdentifier() != null) {

      clone.setIdentifier(getIdentifier());
    }
    if (getDatePublished() != null) {

      clone.setDatePublished(getDatePublished());
    }
    if (getCourtName() != null) {

      clone.setCourtName(getCourtName());
    }
    return clone;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((courtName == null) ? 0 : courtName.hashCode());
    result = prime * result + ((datePublished == null) ? 0 : datePublished.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    LegalCaseVO other = (LegalCaseVO) obj;

    if (courtName == null) {
      if (other.courtName != null)
        return false;
    } else if (!courtName.equals(other.courtName))
      return false;

    if (datePublished == null) {
      if (other.datePublished != null)
        return false;
    } else if (!datePublished.equals(other.datePublished))
      return false;

    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;

    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;

    return true;
  }

}
