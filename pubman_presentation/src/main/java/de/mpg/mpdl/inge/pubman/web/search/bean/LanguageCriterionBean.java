/**
 * 
 */
package de.mpg.mpdl.inge.pubman.web.search.bean;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.LanguageCriterion;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;

/**
 * @author endres
 * 
 */
@SuppressWarnings("serial")
public class LanguageCriterionBean extends CriterionBean {
  private LanguageCriterion languageCriterionVO = new LanguageCriterion();

  private String languageProposal = "";

  // collapsed by default
  protected boolean collapsed = true;

  public LanguageCriterionBean() {

  }

  public LanguageCriterionBean(LanguageCriterion languageCriterionVO) {
    setLanguageCriterionVO(languageCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return languageCriterionVO;
  }

  public LanguageCriterion getLanguageCriterionVO() {
    return languageCriterionVO;
  }

  public void setLanguageCriterionVO(LanguageCriterion languageCriterionVO) {
    this.languageCriterionVO = languageCriterionVO;
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    languageCriterionVO.setSearchString("");
    setLanguageProposal("");
  }

  public SelectItem[] getLanguageOptions() {
    return CommonUtils.getLanguageOptions();
  }

  /**
   * @return the languageProposal
   */
  public String getLanguageProposal() {
    return languageProposal;
  }

  /**
   * @param languageProposal the languageProposal to set
   */
  public void setLanguageProposal(String languageProposal) {
    this.languageProposal = languageProposal;
  }

  public void valueChanged(ValueChangeEvent event) {
    String newVal = "";
    if (event != null && event.getNewValue() != null) {
      newVal = event.getNewValue().toString();
    }
    languageProposal = newVal;
    languageCriterionVO.setSearchString(newVal);
  }

  public String getAlternativeValue() throws Exception {
    String locale = getI18nHelper().getLocale();
    return CommonUtils.getConeLanguageName(languageCriterionVO.getSearchString(), locale);
  }
}
