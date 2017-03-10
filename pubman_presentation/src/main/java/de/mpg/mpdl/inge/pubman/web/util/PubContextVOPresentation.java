package de.mpg.mpdl.inge.pubman.web.util;

import java.text.Collator;
import java.util.Locale;

import javax.faces.context.FacesContext;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.web.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmission;
import de.mpg.mpdl.inge.pubman.web.easySubmission.EasySubmissionSessionBean;

/**
 * Wrapper class for contexts to be used in the presentation.
 * 
 * @author franke
 * @author $Author$
 * @version: $Revision$ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
@SuppressWarnings("serial")
public class PubContextVOPresentation extends ContextVO implements
    Comparable<PubContextVOPresentation> {

  private boolean selected = false;

  public PubContextVOPresentation(ContextVO item) {
    super(item);
  }

  public boolean getSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String selectForEasySubmission() {
    this.selected = true;

    // deselect all other contexts
    if (this.getContextListSessionBean().getDepositorContextList() != null) {
      for (int i = 0; i < this.getContextListSessionBean().getDepositorContextList().size(); i++) {
        this.getContextListSessionBean().getDepositorContextList().get(i).setSelected(false);
      }
    }

    this.getItemControllerSessionBean().createNewPubItem(EasySubmission.LOAD_EASYSUBMISSION,
        getReference());
    this.getEasySubmissionSessionBean()
        .setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);

    if (this.getEasySubmissionSessionBean().getCurrentSubmissionMethod() == EasySubmissionSessionBean.SUBMISSION_METHOD_FETCH_IMPORT) {
      return "loadNewFetchMetadata";
    } else {
      return "loadNewEasySubmission";
    }
  }

  @Override
  public int compareTo(PubContextVOPresentation compareObject) {
    Collator collator = Collator.getInstance(Locale.getDefault());
    collator.setStrength(Collator.SECONDARY);
    return collator.compare(this.getName(), compareObject.getName());
  }

  /**
   * Return any bean stored in session scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  private synchronized Object getSessionBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }
    Object result =
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
    if (result == null) {
      try {
        Object newBean = cls.newInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, newBean);
        return newBean;
      } catch (Exception e) {
        throw new RuntimeException("Error creating new bean of type " + cls, e);
      }
    } else {
      return result;
    }
  }

  private ContextListSessionBean getContextListSessionBean() {
    return ((ContextListSessionBean) getSessionBean(ContextListSessionBean.class));
  }

  private ItemControllerSessionBean getItemControllerSessionBean() {
    return ((ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class));
  }

  private EasySubmissionSessionBean getEasySubmissionSessionBean() {
    return ((EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class));
  }
}
