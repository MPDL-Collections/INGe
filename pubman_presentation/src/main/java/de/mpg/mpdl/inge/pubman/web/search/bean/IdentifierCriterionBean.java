package de.mpg.mpdl.inge.pubman.web.search.bean;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.IdentifierCriterion;

/**
 * POJO bean to deal with one IdentifierCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class IdentifierCriterionBean extends CriterionBean {
  private IdentifierCriterion identifierCriterionVO;

  // collapsed by default
  protected boolean collapsed = true;


  public IdentifierCriterionBean() {
    // ensure the parentVO is never null;
    this(new IdentifierCriterion());
  }

  public IdentifierCriterionBean(IdentifierCriterion identifierCriterionVO) {
    setIdentifierCriterionVO(identifierCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return identifierCriterionVO;
  }

  public IdentifierCriterion getIdentifierCriterionVO() {
    return identifierCriterionVO;
  }

  public void setIdentifierCriterionVO(IdentifierCriterion identifierCriterionVO) {
    this.identifierCriterionVO = identifierCriterionVO;
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    identifierCriterionVO.setSearchString("");
  }
}
