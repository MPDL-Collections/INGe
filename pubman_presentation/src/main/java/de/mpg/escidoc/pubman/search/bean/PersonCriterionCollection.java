package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.PersonCriterion;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * Bean to handle the PersonCriterionCollection on a single jsp. A PersonCriterionCollection is
 * represented by a List<PersonCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class PersonCriterionCollection {
  public static final String BEAN_NAME = "PersonCriterionCollection";

  private List<PersonCriterion> parentVO;
  private PersonCriterionManager personCriterionManager;

  /**
   * CTOR to create a new ArrayList<PersonCriterionVO> starting with one empty new PersonCriterionVO
   */
  public PersonCriterionCollection() {
    // ensure the parentVO is never null;
    List<PersonCriterion> ctorList = new ArrayList<PersonCriterion>();
    ctorList.add(new PersonCriterion());
    setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<PersonCriterionVO>
   * 
   * @param parentVO
   */
  public PersonCriterionCollection(List<PersonCriterion> parentVO) {
    setParentVO(parentVO);
  }

  public List<PersonCriterion> getParentVO() {
    return parentVO;
  }

  public void setParentVO(List<PersonCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    personCriterionManager = new PersonCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type PersonCriterionBean
   * 
   * @author Mario Wagner
   */
  public class PersonCriterionManager extends DataModelManager<PersonCriterionBean> {
    List<PersonCriterion> parentVO;

    public PersonCriterionManager(List<PersonCriterion> parentVO) {
      setParentVO(parentVO);
    }

    public PersonCriterionBean createNewObject() {
      PersonCriterion newVO = new PersonCriterion();
      newVO.setCreatorRole(new ArrayList<CreatorRole>());
      // create a new wrapper pojo
      PersonCriterionBean personCriterionBean = new PersonCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      parentVO.add(newVO);
      return personCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      parentVO.remove(i);
    }

    public List<PersonCriterionBean> getDataListFromVO() {
      if (parentVO == null)
        return null;
      // we have to wrap all VO's in a nice PersonCriterionBean
      List<PersonCriterionBean> beanList = new ArrayList<PersonCriterionBean>();
      for (PersonCriterion personCriterionVO : parentVO) {
        beanList.add(new PersonCriterionBean(personCriterionVO));
      }
      return beanList;
    }

    public void setParentVO(List<PersonCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice PersonCriterionBean
      List<PersonCriterionBean> beanList = new ArrayList<PersonCriterionBean>();
      for (PersonCriterion personCriterionVO : parentVO) {
        beanList.add(new PersonCriterionBean(personCriterionVO));
      }
      setObjectList(beanList);
    }

    public int getSize() {
      return getObjectDM().getRowCount();
    }
  }


  public PersonCriterionManager getPersonCriterionManager() {
    return personCriterionManager;
  }

  public void setPersonCriterionManager(PersonCriterionManager personCriterionManager) {
    this.personCriterionManager = personCriterionManager;
  }

  public void clearAllForms() {
    for (PersonCriterionBean pcb : personCriterionManager.getObjectList()) {
      pcb.clearCriterion();
    }
  }

  public List<PersonCriterion> getFilledCriterion() {
    List<PersonCriterion> returnList = new ArrayList<PersonCriterion>();
    for (PersonCriterion vo : parentVO) {
      if (vo.getCreatorRole().size() > 0
          || (vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
