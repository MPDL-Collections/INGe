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

package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.util.Date;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Class describing an import item. The parent is a {@link ImportLog}.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ImportLogItem extends ImportLog {
  private ImportLog parent;
  private String itemId;
  private PubItemVO itemVO;

  private static String link = null;

  /**
   * Constructor setting the parent import.
   * 
   * @param parent The parent import
   */
  public ImportLogItem(ImportLog parent) {
    this.setStartDate(new Date());
    this.setStatus(Status.PENDING);
    this.setErrorLevel(ErrorLevel.FINE);

    this.parent = parent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setErrorLevel(ErrorLevel errorLevel) {
    super.setErrorLevel(errorLevel);
    if (this.parent != null) {
      this.parent.setErrorLevel(errorLevel);
    }
  }

  /**
   * @return the parent
   */
  public ImportLog getParent() {
    return this.parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(ImportLog parent) {
    this.parent = parent;
  }

  /**
   * @return the itemId
   */
  public String getItemId() {
    return this.itemId;
  }

  /**
   * @param itemId the itemId to set
   */
  @Override
  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  /**
   * @return the itemVO
   */
  public PubItemVO getItemVO() {
    return this.itemVO;
  }

  /**
   * @param itemVO the itemVO to set
   */
  @Override
  public void setItemVO(PubItemVO itemVO) {
    this.itemVO = itemVO;
  }

  @Override
  protected String getRelevantString() {
    return this.getMessage();
  }


  /**
   * @return the itemLink
   */
  public String getLink() {
    if (ImportLogItem.link == null) {
      try {
        ImportLogItem.link =
            PropertyReader.getProperty("escidoc.pubman.instance.url")
                + PropertyReader.getProperty("escidoc.pubman.instance.context.path")
                + PropertyReader.getProperty("escidoc.pubman.item.pattern");
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }
    return ImportLogItem.link.replaceAll("\\$1", this.itemId);
  }

  /**
   * @return the itemLink
   */
  public String getDetailsLink() {
    return "ImportItemDetails.jsp?id=" + this.getStoredId();
  }


  // /**
  // * @return An XML representation of this item
  // */
  // @Override
  // public void toXML(Writer writer) throws Exception {
  // // StringWriter writer = new StringWriter();
  //
  // writer.write("<import-item ");
  // writer.write("status=\"");
  // writer.write(this.getStatus().toString());
  // writer.write("\" error-level=\"");
  // writer.write(this.getErrorLevel().toString());
  // writer.write("\">\n");
  //
  // writer.write("\t<message>");
  // writer.write(this.escape(this.getMessage()));
  // writer.write("</message>\n");
  //
  // if (this.getItemId() != null) {
  // writer.write("\t<escidoc-id>");
  // writer.write(this.getItemId());
  // writer.write("</escidoc-id>\n");
  // }
  //
  //
  // writer.write("\t<start-date>");
  // writer.write(this.getStartDateFormatted());
  // writer.write("</start-date>\n");
  //
  // if (this.getEndDate() != null) {
  // writer.write("\t<end-date>");
  // writer.write(this.getEndDateFormatted());
  // writer.write("</end-date>\n");
  // }
  //
  // writer.write("\t<items>\n");
  // for (final ImportLogItem item : this.getItems()) {
  // item.toXML(writer);
  // }
  // writer.write("\t</items>\n");
  //
  // writer.write("</import-item>\n");
  //
  // }


  /**
   * @return An XML representation of this item
   */
  /*
   * public void toXML(XMLStreamWriter writer) throws XMLStreamException {
   * 
   * 
   * writer.writeStartElement("import-item "); writer.writeAttribute("status",
   * getStatus().toString()); writer.writeAttribute("error-level", getErrorLevel().toString());
   * 
   * 
   * writer.writeStartElement("message"); writer.writeCharacters(getMessage());
   * writer.writeEndElement();
   * 
   * writer.writeStartElement("escidoc-id"); writer.writeCharacters(getItemId());
   * writer.writeEndElement();;
   * 
   * writer.writeStartElement("start-date"); writer.writeCharacters(getStartDateFormatted());
   * writer.writeEndElement();;
   * 
   * if (getEndDate() != null) { writer.writeStartElement("end-date");
   * writer.writeCharacters(getEndDateFormatted()); writer.writeEndElement(); }
   * 
   * writer.writeStartElement("items"); for (ImportLogItem item : getItems()) { item.toXML(writer);
   * } writer.writeEndElement();
   * 
   * writer.writeEndElement(); }
   */
}
