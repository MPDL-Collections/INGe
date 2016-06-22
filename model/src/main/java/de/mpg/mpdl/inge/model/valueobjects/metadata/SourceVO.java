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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * Some items are published as part of a bundle, e.g. a journal, a book, a series or a database. The
 * source container includes descriptive elements of the superordinate element.
 * 
 * @revised by MuJ: 27.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 22-Okt-2007 14:35:53
 */
@JsonInclude(value = Include.NON_NULL)
public class SourceVO extends ValueObject implements Cloneable {
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
  private java.util.List<AlternativeTitleVO> alternativeTitles =
      new java.util.ArrayList<AlternativeTitleVO>();
  private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
  private String volume;
  private String issue;
  private Date datePublishedInPrint;
  private String startPage;
  private String endPage;
  private String sequenceNumber;
  private PublishingInfoVO publishingInfo;
  private java.util.List<IdentifierVO> identifiers = new java.util.ArrayList<IdentifierVO>();
  private java.util.List<SourceVO> sources = new java.util.ArrayList<SourceVO>();
  private Genre genre;
  private String totalNumberOfPages;

  /**
   * The possible genres for an source.
   */
  public enum Genre {
    BOOK("http://purl.org/eprint/type/Book"), PROCEEDINGS(
        "http://purl.org/escidoc/metadata/ves/publication-types/proceedings"), JOURNAL(
        "http://purl.org/escidoc/metadata/ves/publication-types/journal"), ISSUE(
        "http://purl.org/escidoc/metadata/ves/publication-types/issue"), SERIES(
        "http://purl.org/escidoc/metadata/ves/publication-types/series"),

    // JUS
    NEWSPAPER("http://purl.org/escidoc/metadata/ves/publication-types/newspaper"), ENCYCLOPEDIA(
        "http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia"), MULTI_VOLUME(
        "http://purl.org/escidoc/metadata/ves/publication-types/multi-volume"), COMMENTARY(
        "http://purl.org/escidoc/metadata/ves/publication-types/commentary"), HANDBOOK(
        "http://purl.org/escidoc/metadata/ves/publication-types/handbook"), COLLECTED_EDITION(
        "http://purl.org/escidoc/metadata/ves/publication-types/collected-edition"), FESTSCHRIFT(
        "http://purl.org/escidoc/metadata/ves/publication-types/festschrift");

    private String uri;

    private Genre(String uri) {
      this.uri = uri;
    }

    public String getUri() {
      return uri;
    }
  }

  /**
   * The possible genres for an source.
   */
  public enum AlternativeTitleType {
    ABBREVIATION("http://purl.org/escidoc/metadata/terms/0.1/ABBREVIATION"), HTML(
        "http://purl.org/escidoc/metadata/terms/0.1/HTML"), LATEX(
        "http://purl.org/escidoc/metadata/terms/0.1/LATEX"), MATHML(
        "http://purl.org/escidoc/metadata/terms/0.1/MATHML"), SUBTITLE(
        "http://purl.org/escidoc/metadata/terms/0.1/SUBTITLE"), OTHER(
        "http://purl.org/escidoc/metadata/terms/0.1/OTHER");

    private String uri;

    private AlternativeTitleType(String uri) {
      this.uri = uri;
    }

    public String getUri() {
      return uri;
    }
  }

  /**
   * Creates a new instance.
   */
  public SourceVO() {
    super();
  }

  /**
   * Creates a new instance with the given title.
   * 
   * @param title
   */
  public SourceVO(String title) {
    super();
    this.title = title;
  }

  /**
   * Delivers the title of the source, e.g. the title of the journal or the book.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the source, e.g. the title of the journal or the book.
   * 
   * @param newVal
   */
  public void setTitle(String newVal) {
    title = newVal;
  }

  /**
   * Delivers the list of alternative titles of the source. The source may have one or several other
   * forms of the title.
   */
  public java.util.List<AlternativeTitleVO> getAlternativeTitles() {
    return alternativeTitles;
  }

  /**
   * Delivers the volume of the source in which the described item was published in.
   */
  public String getVolume() {
    return volume;
  }

  /**
   * Sets the volume of the source in which the described item was published in.
   * 
   * @param newVal
   */
  public void setVolume(String newVal) {
    volume = newVal;
  }

  /**
   * Delivers the issue of the source in which the described item was published in.
   */
  public String getIssue() {
    return issue;
  }

  /**
   * Sets the issue of the source in which the described item was published in.
   * 
   * @param newVal
   */
  public void setIssue(String newVal) {
    issue = newVal;
  }

  /**
   * Delivers the page where the described item starts.
   */
  public String getStartPage() {
    return startPage;
  }

  /**
   * Sets the page where the described item starts.
   * 
   * @param newVal
   */
  public void setStartPage(String newVal) {
    startPage = newVal;
  }

  /**
   * Delivers the page where the described item ends.
   */
  public String getEndPage() {
    return endPage;
  }

  /**
   * Sets the page where the described item ends.
   * 
   * @param newVal
   */
  public void setEndPage(String newVal) {
    endPage = newVal;
  }

  /**
   * Delivers the sequence number, i. e. the number of the described item within the source.
   */
  public String getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * Sets the sequence number, i. e. the number of the described item within the source.
   * 
   * @param newVal
   */
  public void setSequenceNumber(String newVal) {
    sequenceNumber = newVal;
  }

  /**
   * Delivers the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   */
  public PublishingInfoVO getPublishingInfo() {
    return publishingInfo;
  }

  /**
   * Sets the publishing info, i. e. the institution which published the item and additional
   * information, e.g. the publisher name and place of a book or the university where an theses has
   * been created.
   * 
   * @param newVal
   */
  public void setPublishingInfo(PublishingInfoVO newVal) {
    publishingInfo = newVal;
  }

  /**
   * Delivers the list of creators of the source, e.g. the editor of a book or a book series.
   */
  public java.util.List<CreatorVO> getCreators() {
    return creators;
  }

  /**
   * Delivers the list of sources, i. e. bundles in which the source has been published, e.g. a
   * series.
   */
  public java.util.List<SourceVO> getSources() {
    return sources;
  }

  /**
   * Delivers the genre of the source.
   */
  public Genre getGenre() {
    return genre;
  }

  /**
   * Sets the genre of the source.
   * 
   * @param newVal
   */
  public void setGenre(Genre newVal) {
    genre = newVal;
  }

  /**
   * Delivers the list of external Identifier of the source, e.g. ISSN, ISBN, URI.
   */
  public java.util.List<IdentifierVO> getIdentifiers() {
    return identifiers;
  }

  public String getTotalNumberOfPages() {
    return totalNumberOfPages;
  }

  public void setTotalNumberOfPages(String totalNumberOfPages) {
    this.totalNumberOfPages = totalNumberOfPages;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public Object clone() {
    SourceVO vo = new SourceVO();
    if (getTitle() != null) {
      vo.setTitle(getTitle());
    }
    vo.setVolume(getVolume());
    vo.setIssue(getIssue());
    if (getPublishingInfo() != null) {
      vo.setPublishingInfo((PublishingInfoVO) getPublishingInfo().clone());
    }
    vo.setSequenceNumber(getSequenceNumber());
    vo.setTotalNumberOfPages(getTotalNumberOfPages());
    vo.setStartPage(getStartPage());
    vo.setEndPage(getEndPage());
    for (AlternativeTitleVO title : getAlternativeTitles()) {
      vo.getAlternativeTitles().add((AlternativeTitleVO) title.clone());
    }
    for (CreatorVO creator : getCreators()) {
      vo.getCreators().add((CreatorVO) creator.clone());
    }
    for (IdentifierVO identifier : getIdentifiers()) {
      vo.getIdentifiers().add((IdentifierVO) identifier.clone());
    }
    for (SourceVO source : getSources()) {
      vo.getSources().add((SourceVO) source.clone());
    }

    // added by DiT, 27.11.2007
    vo.setGenre(this.getGenre());

    return vo;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof SourceVO)) {
      return false;
    }
    SourceVO vo = (SourceVO) o;
    return equals(getTitle(), vo.getTitle()) && equals(getVolume(), vo.getVolume())
        && equals(getIssue(), vo.getIssue()) && equals(getPublishingInfo(), vo.getPublishingInfo())
        && equals(getSequenceNumber(), vo.getSequenceNumber())
        && equals(getStartPage(), vo.getStartPage()) && equals(getEndPage(), vo.getEndPage())
        && equals(getAlternativeTitles(), vo.getAlternativeTitles())
        && equals(getCreators(), vo.getCreators()) && equals(getIdentifiers(), vo.getIdentifiers())
        && equals(getSources(), vo.getSources())
        // added by DiT, 27.11.2007
        && equals(this.getGenre(), vo.getGenre())
        && equals(this.getTotalNumberOfPages(), vo.getTotalNumberOfPages());
  }

  public void setDatePublishedInPrint(Date datePublishedInPrint) {
    this.datePublishedInPrint = datePublishedInPrint;
  }

  public Date getDatePublishedInPrint() {
    return datePublishedInPrint;
  }
}
