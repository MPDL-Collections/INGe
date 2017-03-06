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

package de.mpg.mpdl.inge.pubman.web.appbase;

import java.io.Serializable;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.pubman.web.util.InternationalizationHelper;
import de.mpg.mpdl.inge.pubman.web.util.LoginHelper;

/**
 * The FacesBean provides common features for bean and facesMessage handling. Designed to replace
 * inheritance from FacesBean and others.
 * 
 * @author Mario Wagner
 * @version
 */
@SuppressWarnings("serial")
public class FacesBean implements Serializable {
  public static final String BEAN_NAME = FacesBean.class.getName();

  private static final Logger logger = Logger.getLogger(FacesBean.class);

  public FacesBean() {}

  /**
   * Return the <code>FacesContext</code> instance for the current request.
   * 
   * @return <code>FacesContext</code>
   */
  public static FacesContext getFacesContext() {
    return FacesContext.getCurrentInstance();
  }

  /**
   * Return the <code>ExternalContext</code> instance for the current request.
   * 
   * @return <code>ExternalContext</code>
   */
  public static ExternalContext getExternalContext() {
    return getFacesContext().getExternalContext();
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void info(String summary) {
    info(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void info(String summary, String detail) {
    info(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   * 
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public static void info(UIComponent component, String summary) {
    info(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void info(String summary, String detail, UIComponent component) {
    message(summary, detail, component, FacesMessage.SEVERITY_INFO);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void warn(String summary) {
    warn(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void warn(String summary, String detail) {
    warn(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   * 
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public static void warn(UIComponent component, String summary) {
    warn(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void warn(String summary, String detail, UIComponent component) {
    message(summary, detail, component, FacesMessage.SEVERITY_WARN);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void error(String summary) {
    error(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void error(String summary, String detail) {
    error(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   * 
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public static void error(UIComponent component, String summary) {
    error(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void error(String summary, String detail, UIComponent component) {
    message(summary, detail, component, FacesMessage.SEVERITY_ERROR);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void fatal(String summary) {
    fatal(summary, null, null);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void fatal(String summary, String detail) {
    fatal(summary, detail, null);
  }

  /**
   * Enqueue a <code>FacesMessage</code> associated with the specified component, containing the
   * specified summary text and a message severity level of <code>FacesMessage.SEVERITY_ERROR</code>
   * .
   * 
   * @param component associated <code>UIComponent</code>
   * @param summary summary text
   */
  public static void fatal(UIComponent component, String summary) {
    fatal(summary, null, component);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void fatal(String summary, String detail, UIComponent component) {
    message(summary, detail, component, FacesMessage.SEVERITY_FATAL);
  }

  /**
   * Enqueue a global <code>FacesMessage</code> (not associated with any particular component)
   * containing the specified summary text, a detailed description and a message severity level of
   * <code>FacesMessage.SEVERITY_ERROR</code>.
   * 
   * @param summary summary text
   */
  public static void message(String summary, String detail, UIComponent component, Severity severity) {
    FacesMessage fm = new FacesMessage(severity, summary, detail);

    if (component == null) {
      getFacesContext().addMessage(null, fm);
    } else {
      getFacesContext().addMessage(component.getId(), fm);
    }
  }

  public boolean getHasMessages() {
    return getFacesContext().getMessages().hasNext();
  }

  /**
   * Get the name of the actual bean.
   * 
   * @return The name of the actual bean.
   */
  public String getBeanName() {
    return BEAN_NAME;
  }

  public boolean getHasErrorMessages() {
    for (Iterator<FacesMessage> i = getFacesContext().getMessages(); i.hasNext();) {
      FacesMessage fm = i.next();

      logger
          .info("Message (" + fm.getSeverity() + "): " + fm.getSummary() + ":\n" + fm.getDetail());

      if (fm.getSeverity().equals(FacesMessage.SEVERITY_ERROR)
          || fm.getSeverity().equals(FacesMessage.SEVERITY_WARN)
          || fm.getSeverity().equals(FacesMessage.SEVERITY_FATAL)) {
        return true;
      }
    }

    return false;
  }

  public int getNumberOfMessages() {
    int number = 0;

    for (Iterator<FacesMessage> i = getFacesContext().getMessages(); i.hasNext();) {
      i.next();
      number++;
    }

    return number;
  }

  public String getIP() {
    HttpServletRequest requ = (HttpServletRequest) getExternalContext().getRequest();
    return requ.getRemoteAddr();
  }

  public String getSessionId() {
    HttpSession session = (HttpSession) getExternalContext().getSession(false);
    return session.getId();
  }

  public String getReferer() {
    HttpServletRequest requ = (HttpServletRequest) getExternalContext().getRequest();
    return requ.getHeader("Referer");
  }

  public String getUserAgent() {
    HttpServletRequest requ = (HttpServletRequest) getExternalContext().getRequest();
    return requ.getHeader("User-Agent");
  }

  protected void checkForLogin() {
    // if not logged in redirect to login page
    if (!getLoginHelper().isLoggedIn()) {
      info(getI18nHelper().getMessage("NotLoggedIn"));
    }
  }

  public LoginHelper getLoginHelper() {
    return (LoginHelper) getSessionBean(LoginHelper.class);
  }

  public InternationalizationHelper getI18nHelper() {
    return (InternationalizationHelper) getSessionBean(InternationalizationHelper.class);
  }

  public String getMessage(String placeholder) {
    return getI18nHelper().getMessage(placeholder);
  }

  public String getLabel(String placeholder) {
    return getI18nHelper().getLabel(placeholder);
  }

  /**
   * Return any bean stored in request scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  // TODO: was ist der Unterschied zu den anderen getXXXBeans
  public static Object getRequestBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }

    FacesContext context = FacesContext.getCurrentInstance();
    return cls
        .cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));
  }

  /**
   * Return any bean stored in session scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  // TODO: was ist der Unterschied zu den anderen getXXXBeans
  public static Object getSessionBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }

    FacesContext context = FacesContext.getCurrentInstance();
    Object bean =
        cls.cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));

    return bean;
  }

  /**
   * Return any bean stored in application scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   */
  // TODO: was ist der Unterschied zu den anderen getXXXBeans
  public static Object getApplicationBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
      if (FacesBean.class.getName().equals(name)) {
        logger.warn("Bean class " + cls.getName() + " appears to have no individual BEAN_NAME.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error getting bean name of " + cls, e);
    }

    FacesContext context = FacesContext.getCurrentInstance();

    return cls
        .cast(context.getApplication().evaluateExpressionGet(context, "#{" + name + "}", cls));
  }

  /**
   * Return any bean stored in request, session or application scope under the specified name.
   * 
   * @param cls The bean class.
   * @return the actual or new bean instance
   * 
   * @Deprecated Use getRequestBean(), getSessionBean() or getApplicationBean instead.
   */
  @Deprecated
  // TODO: was ist der Unterschied zu den anderen getXXXBeans
  public static synchronized Object getBean(final Class<?> cls) {
    String name = null;

    try {
      name = (String) cls.getField("BEAN_NAME").get(new String());
    } catch (IllegalAccessException iae) {
      throw new RuntimeException("Error getting bean name.", iae);
    } catch (NoSuchFieldException nsfe) {
      throw new RuntimeException("Property BEAN_NAME not defined in " + cls, nsfe);
    }

    Object bean =
        FacesContext.getCurrentInstance().getApplication().createValueBinding("#{" + name + "}")
            .getValue(FacesContext.getCurrentInstance());

    return bean;
  }
}
