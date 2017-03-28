package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.DateCriterion.DateType;

/**
 * POJO bean to deal with one DateCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class DateCriterionBean extends CriterionBean {
  private DateCriterion dateCriterionVO;

  // selection fields for the DateCriterionVO.DateType enum
  // ACCEPTED, CREATED, MODIFIED, PUBLISHED_ONLINE, PUBLISHED_PRINT, SUBMITTED
  private boolean searchAccepted, searchCreated, searchModified;
  private boolean searchPublishedOnline, searchPublishedPrint, searchSubmitted;
  private boolean searchEventStart, searchEventEnd;

  public DateCriterionBean() {
    // ensure the parentVO is never null;
    this(new DateCriterion());
  }

  public DateCriterionBean(DateCriterion dateCriterionVO) {
    setDateCriterionVO(dateCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return dateCriterionVO;
  }

  public DateCriterion getDateCriterionVO() {
    return dateCriterionVO;
  }

  public void setDateCriterionVO(DateCriterion dateCriterionVO) {
    this.dateCriterionVO = dateCriterionVO;
    if (dateCriterionVO.getDateType() == null) {
      dateCriterionVO.setDateType(new ArrayList<DateType>());
    }

    for (DateType date : dateCriterionVO.getDateType()) {
      if (DateType.ACCEPTED.equals(date))
        searchAccepted = true;
      else if (DateType.CREATED.equals(date))
        searchCreated = true;
      else if (DateType.MODIFIED.equals(date))
        searchModified = true;
      else if (DateType.PUBLISHED_ONLINE.equals(date))
        searchPublishedOnline = true;
      else if (DateType.PUBLISHED_PRINT.equals(date))
        searchPublishedPrint = true;
      else if (DateType.SUBMITTED.equals(date))
        searchSubmitted = true;
      else if (DateType.EVENT_START.equals(date))
        searchEventStart = true;
      else if (DateType.EVENT_END.equals(date))
        searchEventEnd = true;

    }
  }


  /**
   * Action navigation call to select all DateType enums
   * 
   * @return null
   */
  public void selectAll() {
    dateCriterionVO.getDateType().clear();

    setSearchAccepted(true);
    setSearchCreated(true);
    setSearchModified(true);
    setSearchPublishedOnline(true);
    setSearchPublishedPrint(true);
    setSearchSubmitted(true);
    setSearchEventStart(true);
    setSearchEventEnd(true);
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    setSearchAccepted(false);
    setSearchCreated(false);
    setSearchModified(false);
    setSearchPublishedOnline(false);
    setSearchPublishedPrint(false);
    setSearchSubmitted(false);
    setSearchEventStart(false);
    setSearchEventEnd(false);

    dateCriterionVO.getDateType().clear();
    dateCriterionVO.setSearchString("");
    dateCriterionVO.setFrom("");
    dateCriterionVO.setTo("");
  }

  public boolean isSearchAccepted() {
    return searchAccepted;
  }

  public void setSearchAccepted(boolean searchAccepted) {
    this.searchAccepted = searchAccepted;
    if (searchAccepted == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.ACCEPTED)) {
        dateCriterionVO.getDateType().add(DateType.ACCEPTED);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.ACCEPTED);
    }
  }

  public boolean isSearchCreated() {
    return searchCreated;
  }

  public void setSearchCreated(boolean searchCreated) {
    this.searchCreated = searchCreated;
    if (searchCreated == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.CREATED)) {
        dateCriterionVO.getDateType().add(DateType.CREATED);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.CREATED);
    }
  }

  public boolean isSearchModified() {
    return searchModified;
  }

  public void setSearchModified(boolean searchModified) {
    this.searchModified = searchModified;
    if (searchModified == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.MODIFIED)) {
        dateCriterionVO.getDateType().add(DateType.MODIFIED);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.MODIFIED);
    }
  }

  public boolean isSearchPublishedOnline() {
    return searchPublishedOnline;
  }

  public void setSearchPublishedOnline(boolean searchPublishedOnline) {
    this.searchPublishedOnline = searchPublishedOnline;
    if (searchPublishedOnline == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.PUBLISHED_ONLINE)) {
        dateCriterionVO.getDateType().add(DateType.PUBLISHED_ONLINE);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.PUBLISHED_ONLINE);
    }
  }

  public boolean isSearchPublishedPrint() {
    return searchPublishedPrint;
  }

  public void setSearchPublishedPrint(boolean searchPublishedPrint) {
    this.searchPublishedPrint = searchPublishedPrint;
    if (searchPublishedPrint == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.PUBLISHED_PRINT)) {
        dateCriterionVO.getDateType().add(DateType.PUBLISHED_PRINT);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.PUBLISHED_PRINT);
    }
  }

  public boolean isSearchSubmitted() {
    return searchSubmitted;
  }

  public void setSearchSubmitted(boolean searchSubmitted) {
    this.searchSubmitted = searchSubmitted;
    if (searchSubmitted == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.SUBMITTED)) {
        dateCriterionVO.getDateType().add(DateType.SUBMITTED);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.SUBMITTED);
    }
  }

  public boolean isSearchEventStart() {
    return searchEventStart;
  }

  public void setSearchEventStart(boolean searchEventStart) {
    this.searchEventStart = searchEventStart;
    if (searchEventStart == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.EVENT_START)) {
        dateCriterionVO.getDateType().add(DateType.EVENT_START);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.EVENT_START);
    }
  }

  public boolean isSearchEventEnd() {
    return searchEventEnd;
  }

  public void setSearchEventEnd(boolean searchEventEnd) {
    this.searchEventEnd = searchEventEnd;
    if (searchEventEnd == true) {
      if (!dateCriterionVO.getDateType().contains(DateType.EVENT_END)) {
        dateCriterionVO.getDateType().add(DateType.EVENT_END);
      }
    } else {
      dateCriterionVO.getDateType().remove(DateType.EVENT_END);
    }
  }

}
