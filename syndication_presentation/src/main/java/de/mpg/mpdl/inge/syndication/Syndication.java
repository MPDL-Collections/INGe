/*
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

/**
 * eSciDoc Syndication manager for RSS/ATOM feeds generation.
 * 
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification) $Revision$ $LastChangedDate$
 */

package de.mpg.mpdl.inge.syndication;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;

public class Syndication implements SyndicationHandler {

  private Logger logger = Logger.getLogger(Syndication.class);

  private static final String FEEDS_DEFINITION_PROPERTY = "escidoc.syndication.feeds.xml.path";
  private static String FEEDS_DEFINITION_FILE;
  private static final String FEEDS_DEFINITION_DIGESTER_RULES_FILE =
      "resources/feeds-digester-rules.xml";


  /* Explain XML variable */
  private static String explainXML;

  /* Placeholder for the feed definitions */
  private static Feeds feeds;

  /**
   * Constructor always loads feed collection class
   * 
   * @throws IOException
   * @throws SyndicationException
   */
  public Syndication() throws IOException, SyndicationException {
    try {
      FEEDS_DEFINITION_FILE = PropertyReader.getProperty(FEEDS_DEFINITION_PROPERTY);
    } catch (Exception e) {
      throw new SyndicationException(e);
    }

    explainXML =
        ResourceUtil.getResourceAsString(FEEDS_DEFINITION_FILE, Syndication.class.getClassLoader());
    feeds = Feeds.readFeedsFromXml(FEEDS_DEFINITION_DIGESTER_RULES_FILE, FEEDS_DEFINITION_FILE);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.syndication.SyndicationHandler#explainFeedsXML()
   */
  public String explainFeedsXML() {
    return explainXML;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.syndication.SyndicationHandler#getFeedList()
   */
  public String[] getFeedList() {
    List fs = feeds.getFeeds();
    String[] fl = new String[(int) fs.size()];
    int i = 0;
    for (Feed f : (List<Feed>) fs) {
      fl[i++] = f.getUri();
    }
    return fl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.syndication.SyndicationHandler#getFeedFormatList(java.lang.String)
   */
  public String[] getFeedFormatList(String uri) {
    String ft = feeds.matchFeedByUri(uri).getFeedTypes();
    return ft.split(",");
  }


  /**
   * Getter of the <code>feeds</code>.
   * 
   * @return <code>feeds</code>
   */
  public Feeds getFeeds() {
    return this.feeds;
  }



  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.syndication.SyndicationHandler#getFeed(java.lang.String)
   */
  public byte[] getFeed(String uri) throws SyndicationException, IOException, URISyntaxException,
      FeedException {
    Writer writer = new StringWriter();
    getFeed(uri, writer);
    return writer.toString().getBytes("UTF-8");
  }

  /**
   * Returns the generated Feed as <code>java.io.Writer</code>
   * 
   * @param uri is Feed url
   * @param writer is output <code>java.io.Writer</code>
   * @throws SyndicationException
   * @throws IOException
   * @throws URISyntaxException
   * @throws FeedException
   */
  public void getFeed(String uri, Writer writer) throws SyndicationException, IOException,
      URISyntaxException, FeedException {
    Utils.checkName(uri, "Url is not defined");
    Utils.checkCondition(writer == null, "Empty writer");

    Feed f = feeds.matchFeedByUri(uri);
    if (f == null)
      throw new SyndicationException("The feed for uri: " + uri + "has not been found");

    // make clone of the feed in order to hold instant feeds unchanged
    Feed cf = (Feed) f.clone();

    cf.populateEntries(uri);

    SyndFeedOutput output = new SyndFeedOutput();

    output.output(cf, writer);

  }

  public String getFeedRelLink(String uri) throws Exception {
    Utils.checkName(uri, "Url is not defined");

    Feed f = feeds.matchFeedByUri(uri);
    if (f == null)
      throw new SyndicationException("The feed for uri: " + uri + "has not been found");

    // make clone of the feed in order to hold instant feeds unchanged
    Feed cf = (Feed) f.clone();

    return cf.generateRelLink(uri);

  }


}
