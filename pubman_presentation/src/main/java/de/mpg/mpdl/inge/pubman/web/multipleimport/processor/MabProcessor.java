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

package de.mpg.mpdl.inge.pubman.web.multipleimport.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.apache.axis.encoding.Base64;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class MabProcessor extends FormatProcessor {

  private boolean init = false;
  private String[] items = null;
  private int counter = -1;
  private int length = -1;
  private byte[] originalData = null;

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {
    if (!init) {
      initialize();
    }
    return (this.items != null && this.counter < this.length);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  public String next() throws NoSuchElementException {
    if (!init) {
      initialize();
    }
    if (this.items != null && this.counter < this.length) {
      this.counter++;
      return items[counter - 1];
    } else {
      throw new NoSuchElementException("No more entries left");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    throw new RuntimeException("Method not implemented");
  }

  private void initialize() {
    init = true;

    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(getSourceFile()));
      String line = null;
      ArrayList<String> itemList = new ArrayList<String>();
      StringWriter stringWriter = new StringWriter();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int lineBreakCount = 0;

      while ((line = bufferedReader.readLine()) != null) {
        stringWriter.write(line);
        stringWriter.write("\n");

        byteArrayOutputStream.write(line.getBytes(getEncoding()));
        byteArrayOutputStream.write("\n".getBytes(getEncoding()));

        if ("".equals(line) && lineBreakCount == 6) {
          itemList.add(stringWriter.toString().trim());
          stringWriter = new StringWriter();
        } else if ("".equals(line)) {
          lineBreakCount++;
        } else {
          lineBreakCount = 0;
        }
      }

      bufferedReader.close();

      if (!"".equals(stringWriter.toString().trim())) {
        itemList.add(stringWriter.toString().trim());
      }

      this.originalData = byteArrayOutputStream.toByteArray();

      this.items = itemList.toArray(new String[] {});

      this.length = this.items.length;

      counter = 0;


    } catch (Exception e) {
      throw new RuntimeException("Error reading input stream", e);
    }
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public String getDataAsBase64() {
    if (this.originalData == null) {
      return null;
    }

    return Base64.encode(this.originalData);
  }
}
