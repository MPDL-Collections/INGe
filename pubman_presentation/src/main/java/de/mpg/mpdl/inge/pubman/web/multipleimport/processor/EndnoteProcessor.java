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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class EndnoteProcessor extends FormatProcessor {

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
  @Override
  public boolean hasNext() {
    if (!this.init) {
      this.initialize();
    }
    return (this.items != null && this.counter < this.items.length);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#next()
   */
  @Override
  public String next() throws NoSuchElementException {
    if (!this.init) {
      this.initialize();
    }
    if (this.items != null && this.counter < this.items.length) {
      this.counter++;
      return this.items[this.counter - 1];
    } else {
      throw new NoSuchElementException("No more entries left");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    throw new RuntimeException("Method not implemented");
  }

  private void initialize() {
    this.init = true;
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int read;
    final byte[] buffer = new byte[2048];
    try {
      final InputStream is = new FileInputStream(this.getSourceFile());
      while ((read = is.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, read);
      }

      is.close();
      this.originalData = byteArrayOutputStream.toByteArray();

      String inputString = new String(this.originalData, this.encoding);

      // replace first empty lines and BOM
      inputString = Pattern.compile("^.*?%", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(inputString).replaceFirst("%");

      final BufferedReader reader = new BufferedReader(new StringReader(inputString));

      String buff;
      boolean firstItem = true;
      int count = 0;
      StringBuffer sb = null;
      final List<String> l = new ArrayList<String>();

      while ((buff = reader.readLine()) != null) {

        if (buff.trim().equals("")) {
          count++;
        } else {
          // first item handling
          if (firstItem) {
            firstItem = false;
            sb = new StringBuffer();
          }
          // new item
          else if (count >= 1 && buff.startsWith("%0")) {
            l.add(sb.toString().trim());
            count = 0;
            sb = new StringBuffer();
          }
          sb.append(buff).append("\n");
        }

      }
      // add last item
      if (sb != null) {
        l.add(sb.toString().trim());
      }

      reader.close();

      this.items = l.toArray(new String[l.size()]);

      this.length = this.items.length;

      this.counter = 0;

    } catch (final Exception e) {
      throw new RuntimeException("Error reading input stream", e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.inge.pubman.multipleimport.processor.FormatProcessor#getLength()
   */
  @Override
  public int getLength() {
    return this.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.inge.pubman.multipleimport.processor.FormatProcessor#getDataAsBase64()
   */
  @Override
  public String getDataAsBase64() {
    if (this.originalData == null) {
      return null;
    }

    return Base64.getEncoder().encodeToString(this.originalData);
  }
}
