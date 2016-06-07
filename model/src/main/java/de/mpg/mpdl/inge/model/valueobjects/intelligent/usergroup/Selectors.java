package de.mpg.mpdl.inge.model.valueobjects.intelligent.usergroup;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.intelligent.IntelligentVO;

/**
 * 
 <create>discarded</create> <update>discarded</update>
 * 
 * 
 * Schema fragment(s) for this class:
 * 
 * <pre>
 * &lt;xs:element xmlns:ns="http://www.escidoc.de/schemas/usergroup/0.5" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="selectors">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="ns:selector" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Selectors extends IntelligentVO {
  private List<Selector> selectorList = new ArrayList<Selector>();

  /**
   * Get the list of 'selector' element items. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @return list
   */
  public List<Selector> getSelectors() {
    return selectorList;
  }

  /**
   * Set the list of 'selector' element items. <create>discarded</create> <update>discarded</update>
   * 
   * 
   * @param list
   */
  public void setSelectors(List<Selector> list) {
    selectorList = list;
  }
}
