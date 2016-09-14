package de.mpg.mpdl.inge.cone;

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


import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import de.mpg.mpdl.inge.cone.ModelList.Model;
import de.mpg.mpdl.inge.cone.ModelList.Predicate;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * A representation of a tree-like structure built of s-p-o triples.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TreeFragment extends LinkedHashMap<String, List<LocalizedTripleObject>> implements
    LocalizedTripleObject {
  private static final String REGEX_PREDICATE_REPLACE = ":/\\-\\.# ";
  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("([\\S]+)(([/#])| )([^/# ]+)");
  private String subject;
  private String language;

  /**
   * Default constructor.
   */
  public TreeFragment() {}

  /**
   * Constructor with given subject.
   * 
   * @param subject The subject.
   */
  public TreeFragment(String subject) {
    this.subject = subject;
  }

  /**
   * Constructor with given subject and language.
   * 
   * @param subject The subject.
   * @param language The language.
   */
  public TreeFragment(String subject, String language) {
    this.subject = subject;
    this.language = language;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  // Add predicates of other if this predicate does not exist yet, otherwise overwrite it.
  public void merge(TreeFragment other, boolean overwrite) {
    Set<String> removedPredicates = new HashSet<String>();

    for (String predicateName : other.keySet()) {
      if (get(predicateName) != null) {
        for (LocalizedTripleObject otherObject : other.get(predicateName)) {
          if (overwrite
              && !removedPredicates.contains(predicateName)
              && (!(otherObject instanceof LocalizedString) || !""
                  .equals(((LocalizedString) otherObject).getValue()))) {
            for (int i = 0; i < get(predicateName).size(); i++) {
              LocalizedTripleObject myObject = get(predicateName).get(i);
              if ((myObject.getLanguage() == null && otherObject.getLanguage() == null)
                  || myObject.getLanguage().equals(otherObject.getLanguage())) {
                get(predicateName).remove(myObject);
                i--;
                removedPredicates.add(predicateName);
              }
            }
          }
          get(predicateName).add(otherObject);
        }
      } else {
        put(predicateName, other.get(predicateName));
      }
    }
  }

  public boolean exists() {
    return (this.keySet() != null && keySet().size() > 0);
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasValue() {
    return (subject != null && !"".equals(subject));
    // for (String element : this.keySet())
    // {
    // List<LocalizedTripleObject> list = this.get(element);
    // for (LocalizedTripleObject object : list)
    // {
    // if (object.hasValue())
    // {
    // return true;
    // }
    // }
    // }
    // return false;
  }

  /**
   * {@inheritDoc}
   */
  public String toRdf(Model model) {
    if (size() == 0) {
      try {
        return StringEscapeUtils.escapeXml10(PropertyReader.getProperty("escidoc.cone.service.url")
            + subject);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      StringWriter result = new StringWriter();
      Map<String, String> namespaces = new HashMap<String, String>();
      ModelList modelList;
      try {
        modelList = ModelList.getInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      int counter = 0;

      result.append("<"
          + (model.getRdfAboutTag().getPrefix() != null ? model.getRdfAboutTag().getPrefix() + ":"
              : "") + model.getRdfAboutTag().getLocalPart());

      if (!subject.startsWith("genid:")) {
        try {
          result.append(" rdf:about=\"");
          result.append(PropertyReader.getProperty("escidoc.cone.service.url") + subject);
          result.append("\"");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      if (language != null && !"".equals(language)) {
        result.append(" xml:lang=\"");
        result.append(language);
        result.append("\"");
      }
      for (String predicate : keySet()) {
        Matcher matcher = NAMESPACE_PATTERN.matcher(predicate);
        if (matcher.find()) {
          String namespace = matcher.group(1) + (matcher.group(3) == null ? "" : matcher.group(3));
          if (!namespaces.containsKey(namespace)) {
            String prefix;
            if (modelList.getDefaultNamepaces().containsKey(namespace)) {
              prefix = modelList.getDefaultNamepaces().get(namespace);
            } else {
              counter++;
              prefix = "ns" + counter;
            }
            namespaces.put(namespace, prefix);
            result.append(" xmlns:" + prefix + "=\"" + namespace + "\"");
          }
        }
      }
      result.append(">\n");
      for (String predicate : keySet()) {
        Matcher matcher = NAMESPACE_PATTERN.matcher(predicate);
        String namespace = null;
        String tagName = null;
        String prefix = null;
        if (matcher.find()) {
          namespace = matcher.group(1) + (matcher.group(3) == null ? "" : matcher.group(3));
          prefix = namespaces.get(namespace);
          tagName = matcher.group(4);
        } else {
          int lastColon = predicate.lastIndexOf(":");
          tagName = predicate.substring(lastColon + 1);
        }
        List<LocalizedTripleObject> values = get(predicate);
        for (LocalizedTripleObject value : values) {
          result.append("<");
          if (namespace != null) {
            result.append(prefix);
            result.append(":");
          }
          result.append(tagName);
          if (value.getLanguage() != null && !"".equals(value.getLanguage())) {
            result.append(" xml:lang=\"");
            result.append(value.getLanguage());
            result.append("\"");
          }


          Predicate p = model.getPredicate(predicate);

          // display links to other resources as rdf:resource attribute, if includeResource is false

          if (p != null && p.getResourceModel() != null && !p.isIncludeResource()) {
            String url = value.toString();
            if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp:"))) {
              try {
                if (value.toString().startsWith("/")) {
                  url =
                      PropertyReader.getProperty("escidoc.cone.service.url")
                          + url.substring(0, url.length() - 1);
                } else {
                  url = PropertyReader.getProperty("escidoc.cone.service.url") + url;
                }
              } catch (Exception e) {
                throw new RuntimeException(e);
              }

            }

            result.append(" rdf:resource=\"" + url + "\"/>");
          }

          else {

            result.append(">");
            result.append(value.toRdf(model));

            result.append("</");
            if (namespace != null) {
              result.append(prefix);
              result.append(":");
            }
            result.append(tagName);
            result.append(">\n");
          }


        }
      }
      result.append("</"
          + (model.getRdfAboutTag().getPrefix() != null ? model.getRdfAboutTag().getPrefix() + ":"
              : "") + model.getRdfAboutTag().getLocalPart() + ">\n");
      return result.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  public String toJson() {
    if (size() == 0) {
      try {
        return "\"" + PropertyReader.getProperty("escidoc.cone.service.url")
            + subject.replace("\"", "\\\"") + "\"";
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      StringWriter writer = new StringWriter();
      writer.append("{\n");
      if (!subject.startsWith("genid:")) {
        writer.append("\"id\" : \"");
        try {
          writer.append(PropertyReader.getProperty("escidoc.cone.service.url")
              + subject.replace("\"", "\\\""));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        writer.append("\",\n");
      }
      for (Iterator<String> iterator = keySet().iterator(); iterator.hasNext();) {
        String key = iterator.next();
        writer.append("\"");
        writer.append(key.replaceAll("[" + REGEX_PREDICATE_REPLACE + "]+", "_").replace("\"",
            "\\\""));
        writer.append("\" : ");
        if (get(key).size() == 1) {
          writer.append(get(key).get(0).toJson());
        } else {
          writer.append("[\n");
          for (Iterator<LocalizedTripleObject> iterator2 = get(key).iterator(); iterator2.hasNext();) {
            LocalizedTripleObject object = (LocalizedTripleObject) iterator2.next();
            writer.append(object.toJson());
            if (iterator2.hasNext()) {
              writer.append(",");
            }
            writer.append("\n");
          }
          writer.append("]");
        }
        if (iterator.hasNext()) {
          writer.append(",\n");
        }
      }
      writer.append("\n}\n");
      return writer.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    if (subject == null) {
      return null;
    }
    try {
      return subject;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String toString2() {

    return super.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (!(o instanceof TreeFragment)) {
      return false;
    } else if (language == null && ((TreeFragment) o).getLanguage() != null) {
      return false;
    } else if (language != null && !language.equals(((TreeFragment) o).getLanguage())) {
      return false;
    } else if (subject == null && ((TreeFragment) o).getSubject() != null) {
      return false;
    } else if (subject != null && !subject.equals(((TreeFragment) o).getSubject())) {
      return false;
    } else {
      return super.equals(o);
    }
  }
}
