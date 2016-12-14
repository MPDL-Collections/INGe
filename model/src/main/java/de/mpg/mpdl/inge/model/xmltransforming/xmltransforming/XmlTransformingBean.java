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

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.xmltransforming.util.FileVOCreationDateComparator;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.MarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.AccountUserVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.AffiliationPathVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.AffiliationROListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.AffiliationVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ContextVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.EventVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ExportFormatVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.GrantVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.MemberListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.StatisticReportWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.SuccessorROListWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.URLWrapper;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.wrappers.UserAttributesWrapper;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationPathVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationResultVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemResultVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.LockVO;
import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.PidTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.valueobjects.RelationVO.RelationType;
import de.mpg.mpdl.inge.model.valueobjects.ResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchHitVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.TocVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.valueobjects.ValueObject;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.SearchResultElement;
import de.mpg.mpdl.inge.model.valueobjects.interfaces.Searchable;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsYearbookVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.AggregationDefinitionVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.mpdl.inge.model.valueobjects.statistics.StatisticReportRecordVO;

/**
 * EJB implementation of interface {@link XmlTransforming}.
 * 
 * @author Johannes Mueller (initial creation)
 * @version $Revision$ $LastChangedDate$Author: mfranke $
 * @revised by MuJ: 21.08.2007
 */
@Stateless
@Remote
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class XmlTransformingBean implements XmlTransforming {
  /**
   * Logger for this class.
   */
  private static final Logger logger = Logger.getLogger(XmlTransformingBean.class);

  /**
   * {@inheritDoc}
   */
  public AccountUserVO transformToAccountUser(String user) throws TechnicalException,
      UnmarshallingException {
    logger.debug("transformToAccountUser(String) - String user=" + user);
    if (user == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAccountUser:user is null");
    }
    AccountUserVO userVO = null;
    try {
      // unmarshal AccountUserVO from String
      IBindingFactory bfact = BindingDirectory.getFactory(AccountUserVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(user);
      userVO = (AccountUserVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(user, e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    return userVO;
  }

  public String transformToAccountUser(AccountUserVO accountUserVO) throws TechnicalException {
    logger.debug("transformToCAccountUser(AccountUserVO)");
    if (accountUserVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAccountUser:accountUserVO is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(AccountUserVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(accountUserVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(accountUserVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToAccountUser(accountUserVO) - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }

  /**
   * {@inheritDoc}
   */
  public List<AffiliationVO> transformToAffiliationList(String organizationalUnitList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAffiliationList(String) - String oranizationalUnitList="
        + organizationalUnitList);
    if (organizationalUnitList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliationList:organizationalUnitList is null");
    }
    AffiliationVOListWrapper affiliationVOListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_input", AffiliationVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(organizationalUnitList);
      affiliationVOListWrapper = (AffiliationVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(organizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationVO> affiliationList = affiliationVOListWrapper.getAffiliationVOList();
    return affiliationList;
  }

  /**
   * {@inheritDoc}
   */
  public List<AffiliationRO> transformToParentAffiliationList(String parentOrganizationalUnitList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToParentAffiliationList(String) - String parentOrganizationalUnitList="
        + parentOrganizationalUnitList);
    if (parentOrganizationalUnitList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliationList:organizationalUnitList is null");
    }
    AffiliationROListWrapper affiliationROListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_input", AffiliationROListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(parentOrganizationalUnitList);
      affiliationROListWrapper = (AffiliationROListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(parentOrganizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationRO> affiliationList = affiliationROListWrapper.getAffiliationROList();
    return affiliationList;
  }

  /**
   * {@inheritDoc}
   */
  public List<AffiliationRO> transformToSuccessorAffiliationList(
      String successorOrganizationalUnitList) throws TechnicalException, UnmarshallingException {
    logger
        .debug("transformToSuccessorAffiliationList(String) - String successorOrganizationalUnitList="
            + successorOrganizationalUnitList);
    if (successorOrganizationalUnitList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliationList:organizationalUnitList is null");
    }
    SuccessorROListWrapper successorROListWrapper;
    try {
      // unmarshal AffiliationVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_input", SuccessorROListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(successorOrganizationalUnitList);
      successorROListWrapper = (SuccessorROListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(successorOrganizationalUnitList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationVO>
    List<AffiliationRO> affiliationList = successorROListWrapper.getAffiliationROList();
    return affiliationList;
  }

  /**
   * {@inheritDoc}
   */
  public List<AffiliationPathVO> transformToAffiliationPathList(String pathList)
      throws TechnicalException, UnmarshallingException {
    logger.debug("transformToAffiliationPathList(String) - String pathList=" + pathList);
    if (pathList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliationPathList:pathList is null");
    }
    List<AffiliationPathVO> resultList;
    AffiliationPathVOListWrapper affiliationPathVOListWrapper = null;
    try {
      // unmarshal AffiliationPathVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_input", AffiliationPathVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(pathList);
      affiliationPathVOListWrapper =
          (AffiliationPathVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(pathList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<AffiliationPathVO>
    resultList = affiliationPathVOListWrapper.getAffiliationPathVOList();
    return resultList;
  }

  /**
   * {@inheritDoc}
   */
  public final List<ExportFormatVO> transformToExportFormatVOList(String formatList)
      throws TechnicalException {
    logger.debug("transformToExportFormatVOList(String) - String formatList=" + formatList);
    if (formatList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToExportFormatVOList:formatList is null");
    }
    ExportFormatVOListWrapper exportFormatVOListWrapper = null;
    try {
      // unmarshall ExportFormatVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("ExportFormatVOListWrapper", ExportFormatVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(formatList);
      exportFormatVOListWrapper = (ExportFormatVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(formatList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ExportFormatVO>
    List<ExportFormatVO> exportFormatVOList = exportFormatVOListWrapper.getExportFormatVOList();
    return exportFormatVOList;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToExportParams(ExportFormatVO exportFormat) throws TechnicalException,
      MarshallingException {
    // TODO FrM: Implement
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public final List<GrantVO> transformToGrantVOList(String formatList) throws TechnicalException {
    logger.debug("transformToGrantVOList(String) - String formatList=" + formatList);
    if (formatList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToGrantVOList:formatList is null");
    }
    GrantVOListWrapper grantVOListWrapper = null;
    try {
      // unmarshall GrantVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("GrantVOListWrapper", GrantVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(formatList);
      grantVOListWrapper = (GrantVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause(), e);
      throw new UnmarshallingException(formatList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<GrantVO>
    List<GrantVO> grantVOList = grantVOListWrapper.getGrantVOList();
    return grantVOList;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToFilterTaskParam(FilterTaskParamVO filterTaskParamVO)
      throws TechnicalException, MarshallingException {
    logger.debug("transformToFilterTaskParam(FilterTaskParamVO)");
    if (filterTaskParamVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToFilterTaskParam:filterTaskParamVO is null");
    }
    StringWriter sw = null;
    try {
      // marshal XML from FilterTaskParamVO
      IBindingFactory bfact = BindingDirectory.getFactory(FilterTaskParamVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(filterTaskParamVO);
    } catch (JiBXException e) {
      throw new MarshallingException(filterTaskParamVO.getClass().getSimpleName(), e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    String filterTaskParam = null;
    if (sw != null) {
      filterTaskParam = sw.toString().trim();
      if (logger.isDebugEnabled()) {
        logger
            .debug("transformToFilterTaskParam(FilterTaskParamVO) - result: String filterTaskParam="
                + filterTaskParam);
      }
      return filterTaskParam;
    }
    throw new TechnicalException("Marshalling result is null");
  }

  /**
   * {@inheritDoc}
   */
  public LockVO transformToLockVO(String lockInformation) throws TechnicalException,
      UnmarshallingException {
    logger.debug("transformToLockVO(String) - String lockInformation=" + lockInformation);
    if (lockInformation == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToLockVO:lockInformation is null");
    }
    // TODO MuJ: Implement if needed.
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public AffiliationVO transformToAffiliation(String organizationalUnit) throws TechnicalException,
      UnmarshallingException {
    logger
        .debug("transformToAffiliation(String) - String organizationalUnit=" + organizationalUnit);
    if (organizationalUnit == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliation:organizationalUnit is null");
    }
    AffiliationVO affiliationVO = null;
    try {
      // unmarshal AffiliationVO from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_input", AffiliationVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(organizationalUnit);
      affiliationVO = (AffiliationVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(organizationalUnit, e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    } catch (java.lang.reflect.UndeclaredThrowableException e) {
      throw new UnmarshallingException(organizationalUnit, new TechnicalException(
          "An UndeclaredThrowableException occured in " + getClass().getSimpleName()
              + ":transformToAffiliation", e.getUndeclaredThrowable()));
    }
    return affiliationVO;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToOrganizationalUnit(AffiliationVO affiliationVO)
      throws TechnicalException, MarshallingException {
    logger.debug("transformToOrganizationalUnit(AffiliationVO)");
    if (affiliationVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToOrganizationalUnit:affiliationVO is null");
    }
    String utf8ou = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("AffiliationVO_output", AffiliationVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(affiliationVO, "UTF-8", null, sw);
      utf8ou = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(affiliationVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger
          .debug("transformToOrganizationalUnit(AffiliationVO) - result: String utf8ou=" + utf8ou);
    }
    return utf8ou;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToItem(ItemVO itemVO) throws TechnicalException {
    logger.debug("transformToItem(PubItemVO)");
    if (itemVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToItem:pubItemVO is null");
    }
    String utf8item = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(pubItemVO);
      utf8item = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(itemVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItem(ItemVO) - result: String utf8item=" + utf8item);
    }
    return utf8item;
  }

  /**
   * {@inheritDoc}
   */
  public ContextVO transformToContext(String context) throws TechnicalException {
    logger.debug("transformToPubCollection(String) - String context=" + context);
    if (context == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubContext:context is null");
    }
    ContextVO contextVO = null;
    try {
      // unmarshal ContextVO from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ContextVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(context);
      contextVO = (ContextVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(context, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(context, e);
    }
    return contextVO;
  }

  /**
   * {@inheritDoc}
   */
  public List<ContextVO> transformToContextList(String contextList) throws TechnicalException {
    logger.debug("transformToPubCollectionList(String) - String contextList=" + contextList);
    if (contextList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubCollectionList:contextList is null");
    }

    logger.debug("transformed contextList =" + contextList);

    SearchRetrieveResponseVO response = null;
    try {
      // unmarshal ContextVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory
              .getFactory("PubItemVO_PubCollectionVO_input", ContextVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(contextList);
      response = (SearchRetrieveResponseVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(contextList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    List<ContextVO> ctxList = new ArrayList<ContextVO>();

    if (response.getRecords() != null) {
      for (SearchRetrieveRecordVO s : response.getRecords()) {
        ctxList.add((ContextVO) s.getData());
      }
    }
    return ctxList;
  }

  /**
   * {@inheritDoc}
   */
  public ItemVO transformToItem(String item) throws TechnicalException {
    logger.debug("transformToPubItem(String) - String item=" + item);
    if (item == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubItem:item is null");
    }
    ItemVO itemVO = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(item);
      itemVO = (ItemVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error("Error transforming item", e);
      throw new UnmarshallingException(item, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    Collections.sort(itemVO.getFiles(), new FileVOCreationDateComparator());
    return itemVO;
  }

  /**
   * {@inheritDoc}
   */
  public List<? extends ItemVO> transformToItemList(String itemListXml) throws TechnicalException {
    logger.debug("transformToPubItemList(String) - String itemList=\n" + itemListXml);
    if (itemListXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubItemList:itemList is null");
    }
    ItemVOListWrapper itemVOListWrapper = null;
    try {
      // unmarshal ItemVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(itemListXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      itemVOListWrapper = (ItemVOListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(itemListXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ItemVO>
    List<? extends ItemVO> itemList = itemVOListWrapper.getItemVOList();

    return itemList;
  }

  /**
   * {@inheritDoc}
   */
  public ItemVOListWrapper transformSearchRetrieveResponseToItemList(String itemListXml)
      throws TechnicalException {
    logger.debug("transformSearchRetrieveResponseToItemList(String) - String itemList=\n"
        + itemListXml);
    if (itemListXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformSearchRetrieveResponseToItemList:itemList is null");
    }
    SearchRetrieveResponseVO response = this.transformToSearchRetrieveResponse(itemListXml);
    List<SearchRetrieveRecordVO> records = response.getRecords();

    ItemVOListWrapper pubItemList = new ItemVOListWrapper();

    pubItemList.setNumberOfRecords(response.getNumberOfRecords() + "");
    List<PubItemVO> list = new ArrayList<PubItemVO>();
    pubItemList.setItemVOList(list);

    if (records == null) {
      return pubItemList;
    }

    for (SearchRetrieveRecordVO record : records) {
      list.add((PubItemVO) record.getData());
    }

    return pubItemList;
  }

  /**
   * {@inheritDoc}
   */
  public List<ContextVO> transformSearchRetrieveResponseToContextList(String contextListXml)
      throws TechnicalException {
    logger.debug("transformSearchRetrieveResponseToContextList(String) - String contextList=\n"
        + contextListXml);
    if (contextListXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformSearchRetrieveResponseToContextList:contextList is null");
    }

    logger.debug("transformed contextList =" + contextListXml);

    SearchRetrieveResponseVO response = this.transformToSearchRetrieveResponse(contextListXml);
    List<SearchRetrieveRecordVO> records = response.getRecords();

    List<ContextVO> pubContextList = new ArrayList<ContextVO>();

    if (records == null) {
      return pubContextList;
    }

    for (SearchRetrieveRecordVO record : records) {
      pubContextList.add((ContextVO) record.getData());
    }

    return pubContextList;
  }

  /**
   * {@inheritDoc}
   */
  @Deprecated
  public ItemResultVO transformToItemResultVO(String searchResultItem) throws TechnicalException {
    if (searchResultItem == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToItemResultVO:searchResultItem is null");
    }

    SearchResultElement searchResultElement = transformToSearchResult(searchResultItem);
    if (!(searchResultElement instanceof ItemResultVO)) {
      throw new TechnicalException("XML not in the right format");
    }

    return (ItemResultVO) searchResultElement;
  }

  /**
   * {@inheritDoc}
   */
  public SearchResultElement transformToSearchResult(String searchResultXml)
      throws TechnicalException {
    if (searchResultXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToSearchResult:searchResultXml is null");
    }
    SearchResultVO searchResultVO = null;
    try {
      // unmarshall PubItemResultVO from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("SearchResultVO_input", SearchResultVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchResultXml);
      searchResultVO = (SearchResultVO) uctx.unmarshalDocument(sr, "UTF-8");
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchResultXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    return convertToVO(searchResultVO);
  }

  /**
   * Converts a {@link SearchResultVO} into an instantiation of {@link SearchResultElement}. May be
   * a - {@link ItemResultVO} - {@link ContainerResultVO} - {@link AffiliationResultVO}
   * 
   * @param searchResultVO The original VO.
   * 
   * @return The new VO.
   */
  private SearchResultElement convertToVO(SearchResultVO searchResultVO) throws TechnicalException {
    Searchable searchable = searchResultVO.getResultVO();
    List<SearchHitVO> searchHits = searchResultVO.getSearchHitList();

    if (searchable instanceof ItemVO) {
      ItemResultVO itemResultVO = new ItemResultVO((ItemVO) searchable);
      itemResultVO.getSearchHitList().addAll(searchHits);
      itemResultVO.setScore(searchResultVO.getScore());
      return itemResultVO;
    } else if (searchable instanceof AffiliationVO) {
      AffiliationResultVO affiliationResultVO = new AffiliationResultVO((AffiliationVO) searchable);
      affiliationResultVO.getSearchHitList().addAll(searchHits);
      affiliationResultVO.setScore(searchResultVO.getScore());

      return affiliationResultVO;
    }
    throw new TechnicalException("Search result is of unknown type");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String transformToItemList(List<? extends ItemVO> itemVOList) throws TechnicalException {
    logger.debug("transformToItemList(List<ItemVO>)");
    if (itemVOList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToItemList:pubItemVOList is null");
    }
    // wrap the item list into the according wrapper class
    ItemVOListWrapper listWrapper = new ItemVOListWrapper();
    listWrapper.setItemVOList(itemVOList);

    return transformToItemList(listWrapper);
  }

  public String transformToItemList(ItemVOListWrapper itemListWrapper) throws TechnicalException {
    // transform the wrapper class into XML
    String utf8itemList = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", ItemVOListWrapper.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(itemListWrapper, "UTF-8", null, sw);
      utf8itemList = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(itemListWrapper.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToItemList(List<ItemVO>) - result: String utf8itemList=\n"
          + utf8itemList);
    }
    return utf8itemList;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToTaskParam(TaskParamVO taskParamVO) throws TechnicalException,
      MarshallingException {
    logger.debug("transformToTaskParam(TaskParamVO)");
    if (taskParamVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToTaskParam:taskParamVO is null");
    }
    StringWriter sw = null;
    try {
      // marshal XML from TaskParamVO
      IBindingFactory bfact = BindingDirectory.getFactory(TaskParamVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(taskParamVO);
    } catch (JiBXException e) {
      throw new MarshallingException(taskParamVO.getClass().getSimpleName(), e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    String taskParam = null;
    if (sw != null) {
      taskParam = sw.toString().trim();
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToTaskParam(TaskParamVO) - result: String taskParam=" + taskParam);
    }
    return taskParam;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToPidTaskParam(PidTaskParamVO pidTaskParamVO) throws TechnicalException,
      MarshallingException {
    logger.debug("transformToPidTaskParam(PidTaskParamVO)");
    if (pidTaskParamVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPidTaskParam:pidTaskParamVO is null");
    }
    StringWriter sw = null;
    try {
      // marshal XML from PidTaskParamVO
      IBindingFactory bfact = BindingDirectory.getFactory(PidTaskParamVO.class);
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(pidTaskParamVO);
    } catch (JiBXException e) {
      throw new MarshallingException(pidTaskParamVO.getClass().getSimpleName(), e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    String pidTaskParam = null;
    if (sw != null) {
      pidTaskParam = sw.toString().trim();
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToPidTaskParam(PidTaskParamVO) - result: String pidTaskParam="
          + pidTaskParam);
    }
    return pidTaskParam;
  }

  /**
   * {@inheritDoc}
   */
  public URL transformUploadResponseToFileURL(String uploadResponse) throws TechnicalException,
      UnmarshallingException, URISyntaxException {
    logger.debug("transformUploadResponseToFileURL(String) - String uploadResponse="
        + uploadResponse);
    if (uploadResponse == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformUploadResponseToFileURL:uploadResponse is null");
    }
    URLWrapper urlWrapper = null;
    URL url = null;
    try {
      // unmarshal the url String from upload response into a URLWrapper object
      IBindingFactory bfact = BindingDirectory.getFactory(URLWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(uploadResponse);
      urlWrapper = (URLWrapper) uctx.unmarshalDocument(sr, null);
      // extract the string from the wrapper and transform it to a URL

      logger.debug("URL: " + PropertyReader.getFrameworkUrl() + ":" + urlWrapper.getUrlString());

      url = new URL(PropertyReader.getFrameworkUrl() + urlWrapper.getUrlString());
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e, e.getRootCause());
      throw new UnmarshallingException(uploadResponse, e);
    } catch (MalformedURLException e) {
      throw new TechnicalException(e);
    } catch (java.lang.ClassCastException e) {
      throw new TechnicalException(e);
    }
    return url;
  }

  /**
   * {@inheritDoc}
   */
  public List<VersionHistoryEntryVO> transformToEventVOList(String versionList)
      throws TechnicalException {
    logger.debug("transformToPubItemVersionVOList(String) - String versionList=\n" + versionList);
    if (versionList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubItemVersionVOList:versionList is null");
    }
    EventVOListWrapper eventVOListWrapper = null;
    try {
      // unmarshal EventVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", EventVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(versionList);
      eventVOListWrapper = (EventVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(versionList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<VersionHistoryEntryVO>
    List<VersionHistoryEntryVO> eventList = eventVOListWrapper.getEventVOList();
    return eventList;
  }

  /**
   * Return the child of the node selected by the xPath.
   * 
   * @param node The node.
   * @param xPath The xPath.
   * @return The child of the node selected by the xPath.
   * @throws TransformerException If anything fails.
   */
  private static Node selectSingleNode(final Node node, final String xpathExpression)
      throws TransformerException {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (Node) xPath.evaluate(xpathExpression, node, XPathConstants.NODE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Return the list of children of the node selected by the xPath.
   * 
   * @param node The node.
   * @param xPath The xPath.
   * @return The list of children of the node selected by the xPath.
   * @throws TransformerException If anything fails.
   */
  public static NodeList selectNodeList(final Node node, final String xpathExpression)
      throws TransformerException {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xPath = factory.newXPath();
    try {
      return (NodeList) xPath.evaluate(xpathExpression, node, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



  /**
   * {@inheritDoc}
   */
  public List<RelationVO> transformToRelationVOList(String relationsXml)
      throws UnmarshallingException {
    logger.debug("transformToRelationVOList(String) - String relationsXml=\n" + relationsXml);
    if (relationsXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToRelationVOList:relationsXml is null");
    }
    List<RelationVO> relations = new ArrayList<RelationVO>();

    try {
      Document relationsDoc = getDocument(relationsXml, true);
      NodeList subjects =
          selectNodeList(
              relationsDoc,
              "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']");
      logger.debug("subjects length:" + subjects.getLength());
      for (int i = 1; i <= subjects.getLength(); i++) {
        String descriptionXpath =
            "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']["
                + i + "]";
        logger.debug("descriptionXpath: " + descriptionXpath);
        String subject = getAttributeValue(relationsDoc, descriptionXpath, "rdf:about");
        String subjectObjectId = subject.substring(subject.lastIndexOf('/') + 1);
        ItemRO subjectRef = new ItemRO(subjectObjectId);

        NodeList subjectRelations =
            selectNodeList(
                relationsDoc,
                "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']["
                    + i + "]/*");
        for (int j = 1; j <= subjectRelations.getLength(); j++) {
          String relationXpath =
              "//*[local-name() = 'Description' and namespace-uri() = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#']["
                  + i + "]/*[" + j + "]";
          logger.debug("relationXpath: " + relationXpath);
          Node relation = selectSingleNode(relationsDoc, relationXpath);
          if (relation.getLocalName() == "isRevisionOf") {
            String predicate =
                relation.getAttributes().getNamedItem("rdf:resource").getTextContent();
            String predicateObjectId = predicate.substring(predicate.lastIndexOf('/') + 1);
            ItemRO predicateRef = new ItemRO(predicateObjectId);

            // add relation to list
            logger.debug(subjectObjectId + " isRevisionOf " + predicateObjectId);
            RelationVO relationVO = new RelationVO();
            relationVO.setSourceItemRef(subjectRef);
            relationVO.settype(RelationType.ISREVISIONOF);
            relationVO.setTargetItemRef(predicateRef);
            relations.add(relationVO);
          }
          if (relation.getLocalName() == "member") {
            // using ItemRefs as workarund here, although it can be containers
            String object = relation.getAttributes().getNamedItem("rdf:resource").getTextContent();
            String objectObjectId = object.substring(object.lastIndexOf('/') + 1);
            ItemRO objectRef = new ItemRO(objectObjectId);

            // add relation to list
            logger.debug(subjectObjectId + " hasMember " + objectObjectId);
            RelationVO relationVO = new RelationVO();
            relationVO.setSourceItemRef(subjectRef);
            relationVO.settype(RelationType.HASMEMBER);
            relationVO.setTargetItemRef(objectRef);
            relations.add(relationVO);
          }
        }
      }
    } catch (Exception e) {
      throw new UnmarshallingException(relationsXml, e);
    }

    return relations;
  }

  /**
   * Parse the given xml String into a Document.
   * 
   * @param xml The xml String.
   * @param namespaceAwareness namespace awareness (default is false)
   * @return The Document.
   * @throws Exception If anything fails.
   */
  protected static Document getDocument(final String xml, final boolean namespaceAwareness)
      throws Exception {
    if (xml == null) {
      throw new IllegalArgumentException(":getDocument:xml is null");
    }
    String charset = "UTF-8";
    Document result = null;
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setNamespaceAware(namespaceAwareness);
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    result = docBuilder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
    result.getDocumentElement().normalize();
    return result;
  }

  /**
   * Return the text value of the selected attribute.
   * 
   * @param node The node.
   * @param xPath The xpath to select the node containint the attribute,
   * @param attributeName The name of the attribute.
   * @return The text value of the selected attribute.
   * @throws Exception If anything fails.
   */
  public static String getAttributeValue(final Node node, final String xPath,
      final String attributeName) throws Exception {
    if (node == null) {
      throw new IllegalArgumentException(":getAttributeValue:node is null");
    }
    if (xPath == null) {
      throw new IllegalArgumentException(":getAttributeValue:xPath is null");
    }
    if (attributeName == null) {
      throw new IllegalArgumentException(":getAttributeValue:attributeName is null");
    }
    String result = null;
    Node attribute = selectSingleNode(node, xPath);
    if (attribute.hasAttributes()) {
      result = attribute.getAttributes().getNamedItem(attributeName).getTextContent();
    }
    return result;
  }

  public PubItemVO transformToPubItem(String itemXml) throws TechnicalException {
    ItemVO itemVO = transformToItem(itemXml);
    if (itemVO.getMetadataSets().size() > 0
        && itemVO.getMetadataSets().get(0) instanceof MdsPublicationVO
        || itemVO.getMetadataSets().get(0) instanceof MdsYearbookVO) {
      return new PubItemVO(itemVO);
    } else {
      logger.warn("Cannot transform item xml to PubItemVO");
      return null;
    }
  }

  public List<PubItemVO> transformToPubItemList(String itemList) throws TechnicalException {
    List<? extends ItemVO> list = transformToItemList(itemList);
    List<PubItemVO> newList = new ArrayList<PubItemVO>();
    for (ItemVO itemVO : list) {
      PubItemVO pubItemVO = new PubItemVO(itemVO);
      newList.add(pubItemVO);
    }
    return newList;
  }

  public ItemVOListWrapper transformToItemListWrapper(String itemListXml) throws TechnicalException {
    logger.debug("transformToPubItemListWrapper(String) - String itemList=\n" + itemListXml);
    if (itemListXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPubItemList:itemList is null");
    }
    ItemVOListWrapper itemVOListWrapper = null;
    try {
      // unmarshal ItemVOListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ItemVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(itemListXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      itemVOListWrapper = (ItemVOListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(itemListXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    return itemVOListWrapper;
  }


  /**
   * {@inheritDoc}
   */
  public List<? extends ValueObject> transformToMemberList(String memberList)
      throws TechnicalException {
    logger.debug("transformToMemberList(String) - String memberList=\n" + memberList);
    if (memberList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToMemberList:memberList is null");
    }
    MemberListWrapper mListWrapper = null;
    try {
      // unmarshal MemberListWrapper from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", MemberListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(memberList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      mListWrapper = (MemberListWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(memberList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ContainerVO>
    List<? extends ValueObject> memList = mListWrapper.getMemberList();

    return memList;
  }

  /**
   * {@inheritDoc}
   */
  public String transformToMemberList(List<? extends ValueObject> memberList)
      throws TechnicalException {
    logger.debug("transformToMemberList(List<ValueObject>)");
    if (memberList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToMemberList:VOList is null");
    }
    // wrap the item list into the according wrapper class
    MemberListWrapper mListWrapper = new MemberListWrapper();
    mListWrapper.setMemberList(memberList);
    // transform the wrapper class into XML
    String utf8memberList = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", MemberListWrapper.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(mListWrapper, "UTF-8", null, sw);
      utf8memberList = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(memberList.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToMemberList(List<VO>) - result: String utf8memberList=\n"
          + utf8memberList);
    }
    return utf8memberList;
  }



  /**
   * {@inheritDoc}
   */
  @Deprecated
  public AffiliationResultVO transformToAffiliationResult(String affiliationResult)
      throws TechnicalException {
    logger.debug("transformToAffiliationResult(String) - String affiliation=" + affiliationResult);
    if (affiliationResult == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAffiliationResult: affiliationResult is null");
    }

    SearchResultElement searchResultElement = transformToSearchResult(affiliationResult);
    if (!(searchResultElement instanceof AffiliationResultVO)) {
      throw new TechnicalException("XML not in the right format");
    }

    return (AffiliationResultVO) searchResultElement;
  }

  public List<StatisticReportRecordVO> transformToStatisticReportRecordList(
      String statisticReportXML) throws TechnicalException {

    logger.debug("transformToStatisticReportRecordList(String) - String containerList=\n"
        + statisticReportXML);
    if (statisticReportXML == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToContainerList:containerList is null");
    }
    StatisticReportWrapper statisticReportWrapper = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticReport", StatisticReportWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(statisticReportXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      statisticReportWrapper = (StatisticReportWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(statisticReportXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    if (statisticReportWrapper.getStatisticReportRecordVOList() != null) {
      return statisticReportWrapper.getStatisticReportRecordVOList();
    } else
      return new ArrayList<StatisticReportRecordVO>();


  }

  public String transformToStatisticReportParameters(StatisticReportParamsVO statisticReportParams)
      throws TechnicalException {
    logger.debug("transformToStatisticReportParameters()");
    if (statisticReportParams == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticReportParameters:StatisticReportParamsVO is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticReport", StatisticReportParamsVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(statisticReportParams, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(statisticReportParams.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToStatisticReportParameters() - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }

  public List<StatisticReportDefinitionVO> transformToStatisticReportDefinitionList(
      String reportDefinitionList) throws TechnicalException {

    logger
        .debug("transformToStatisticReportDefinitionList(String) - String reportDefinitionList=\n"
            + reportDefinitionList);
    if (reportDefinitionList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticReportDefinitionList: reportDefinitionList is null");
    }
    SearchRetrieveResponseVO response = null;

    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticReport", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(reportDefinitionList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      response = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(reportDefinitionList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    List<StatisticReportDefinitionVO> repDefList = new ArrayList<StatisticReportDefinitionVO>();

    if (response.getRecords() != null) {
      for (SearchRetrieveRecordVO s : response.getRecords()) {
        repDefList.add((StatisticReportDefinitionVO) s.getData());
      }
    }
    return repDefList;

  }

  public String transformToStatisticReportDefinition(StatisticReportDefinitionVO reportDef)
      throws TechnicalException {
    logger.debug("transformToStatisticReportDefinition()");
    if (reportDef == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticReportDefinition:reportDef is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticReport", StatisticReportDefinitionVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(reportDef, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(reportDef.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToStatisticReportDefinition() - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }

  public StatisticReportDefinitionVO transformToStatisticReportDefinition(String reportDefXML)
      throws TechnicalException {
    logger.debug("transformToStatisticReportDefinition(String) - String reportDefinition=\n"
        + reportDefXML);
    if (reportDefXML == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticReportDefinition: reportDefXML is null");
    }
    StatisticReportDefinitionVO statisticReportDefinitionVO = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticReport", StatisticReportDefinitionVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(reportDefXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      statisticReportDefinitionVO = (StatisticReportDefinitionVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(reportDefXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    return statisticReportDefinitionVO;
  }



  public AggregationDefinitionVO transformToStatisticAggregationDefinition(String aggrDefXML)
      throws TechnicalException {
    logger
        .debug("transformToStatisticAggregationDefinition(String) - String aggregationDefinition=\n"
            + aggrDefXML);
    if (aggrDefXML == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticAggregationDefinition: aggregationDefXML is null");
    }
    AggregationDefinitionVO statisticAggrDefinitionVO = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticAggregation", AggregationDefinitionVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(aggrDefXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      statisticAggrDefinitionVO = (AggregationDefinitionVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(aggrDefXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    return statisticAggrDefinitionVO;
  }


  public String transformToStatisticAggregationDefinition(AggregationDefinitionVO aggrDef)
      throws TechnicalException {
    logger.debug("transformToStatisticAggregationDefinition()");
    if (aggrDef == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticAggregationDefinition:aggrDef is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticAggregation", AggregationDefinitionVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(aggrDef, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(aggrDef.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToStatisticAggregationDefinition() - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }


  public List<AggregationDefinitionVO> transformToStatisticAggregationDefinitionList(
      String aggregationDefinitionList) throws TechnicalException {

    logger
        .debug("transformToStatisticAggregationDefinitionList(String) - String aggregationDefinitionList=\n"
            + aggregationDefinitionList);
    if (aggregationDefinitionList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToStatisticAggregationDefinitionList: aggregationDefinitionList is null");
    }
    SearchRetrieveResponseVO response = null;

    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("StatisticAggregation", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(aggregationDefinitionList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      response = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(aggregationDefinitionList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    List<AggregationDefinitionVO> repDefList = new ArrayList<AggregationDefinitionVO>();

    if (response.getRecords() != null) {
      for (SearchRetrieveRecordVO s : response.getRecords()) {
        repDefList.add((AggregationDefinitionVO) s.getData());
      }
    }
    return repDefList;

  }



  public TocVO transformToTocVO(String tocXML) throws TechnicalException {
    logger.debug("transformToToVO(String) - String toc=\n" + tocXML);
    if (tocXML == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToTocVO: tocXML is null");
    }
    TocVO tocVO = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact = BindingDirectory.getFactory("TocItemVO_input", TocVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(tocXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      tocVO = (TocVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(tocXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    return tocVO;
  }

  public String transformToGrant(GrantVO grantVO) throws TechnicalException {
    logger.debug("transformToGrant()");
    if (grantVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToGrant:grantVO is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory("GrantVOListWrapper", GrantVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(grantVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(grantVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToGrant() - result: String utf8container=" + utf8container);
    }
    return utf8container;
  }

  public GrantVO transformToGrantVO(String grantXml) throws TechnicalException {
    logger.debug("transformToToGrantVO(String) - String grant=\n" + grantXml);
    if (grantXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToGrantVO: grantXML is null");
    }
    GrantVO grantVO = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact = BindingDirectory.getFactory("GrantVOListWrapper", GrantVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(grantXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      grantVO = (GrantVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(grantXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    return grantVO;
  }

  /**
   * {@inheritDoc}
   */
  public final List<AccountUserVO> transformToAccountUserVOList(String accountUserListXml)
      throws TechnicalException {
    logger.debug("transformToAccountUserVOList(String) - String formatList=" + accountUserListXml);
    if (accountUserListXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToAccountUserVOList:formatList is null");
    }
    AccountUserVOListWrapper accountUserVOListWrapper = null;
    try {
      // unmarshall GrantVOListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory(AccountUserVOListWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(accountUserListXml);
      accountUserVOListWrapper = (AccountUserVOListWrapper) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause(), e);
      throw new UnmarshallingException(accountUserListXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<GrantVO>
    List<AccountUserVO> accountUserVOList = accountUserVOListWrapper.getAccountUserVOList();
    return accountUserVOList;
  }


  /**
   * {@inheritDoc}
   */
  public ResultVO transformToResult(String resultXml) throws TechnicalException {
    logger.debug("transformToResult(String) - String result=" + resultXml);
    if (resultXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToResult:result is null");
    }
    ResultVO resultVO = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", ResultVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(resultXml);
      resultVO = (ResultVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error("Error transforming result", e);
      throw new UnmarshallingException(resultXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    return resultVO;
  }

  public FileVO transformToFileVO(String fileXML) throws TechnicalException {
    logger.debug("transformToFileVO(String) - String file=\n" + fileXML);
    if (fileXML == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformTofileVO: fileXML is null");
    }
    FileVO fileVO = null;
    try {
      // unmarshal StatisticReport from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input", FileVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(fileXML);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      fileVO = (FileVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(fileXML, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }


    return fileVO;
  }

  public String transformToFile(FileVO fileVO) throws TechnicalException {
    logger.debug("transformToFile(FileVO)");
    if (fileVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToFile:fileVO is null");
    }

    String utf8container = null;
    try {
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_output", FileVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(fileVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(fileVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToStatisticReportDefinition() - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }

  public String transformToPidServiceResponse(PidServiceResponseVO pidServiceResponseVO)
      throws TechnicalException {
    logger.debug("transformToPidServiceResponse()");
    if (pidServiceResponseVO == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + "transformToPidServiceResponse:pidServiceResponseVO is null");
    }
    String utf8container = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      // marshal object (with nice indentation, as UTF-8)
      IMarshallingContext mctx = bfact.createMarshallingContext();
      mctx.setIndent(2);
      StringWriter sw = new StringWriter();
      mctx.setOutput(sw);
      mctx.marshalDocument(pidServiceResponseVO, "UTF-8", null, sw);
      // use the following call to omit the leading "<?xml" tag of the generated XML
      // mctx.marshalDocument(containerVO);
      utf8container = sw.toString().trim();
    } catch (JiBXException e) {
      throw new MarshallingException(pidServiceResponseVO.getClass().getSimpleName(), e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("transformToPidServiceResponse() - result: String utf8container="
          + utf8container);
    }
    return utf8container;
  }

  public PidServiceResponseVO transformToPidServiceResponse(String pidServiceResponseXml)
      throws TechnicalException {
    logger.debug("transformToPidServiceResponse(String) - String pidServiceResponse=\n"
        + pidServiceResponseXml);
    if (pidServiceResponseXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToPidServiceResponse: pidServiceResponseXml is null");
    }
    PidServiceResponseVO pidServiceResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact = BindingDirectory.getFactory(PidServiceResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(pidServiceResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      pidServiceResponseVO = (PidServiceResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(pidServiceResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return pidServiceResponseVO;
  }


  public SearchRetrieveResponseVO transformToSearchRetrieveResponse(String searchRetrieveResponseXml)
      throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n"
        + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("PubItemVO_PubCollectionVO_input",
              SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return searchRetrieveResponseVO;
  }

  public SearchRetrieveResponseVO transformToSearchRetrieveResponseUserGroup(
      String searchRetrieveResponseXml) throws TechnicalException {
    return transformToSearchRetrieveResponseGrant(searchRetrieveResponseXml);
  }

  public SearchRetrieveResponseVO transformToSearchRetrieveResponseGrant(
      String searchRetrieveResponseXml) throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n"
        + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("binding", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return searchRetrieveResponseVO;
  }

  public SearchRetrieveResponseVO transformToSearchRetrieveResponseGrantVO(
      String searchRetrieveResponseXml) throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n"
        + searchRetrieveResponseXml);
    if (searchRetrieveResponseXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("GrantVOListWrapper", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searchRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searchRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return searchRetrieveResponseVO;
  }

  public SearchRetrieveResponseVO transformToSearchRetrieveResponseAccountUser(
      String searcRetrieveResponseXml) throws TechnicalException {
    logger.debug("transformToSearchRetrieveResponse(String) - String searchRetrieveResponse=\n"
        + searcRetrieveResponseXml);
    if (searcRetrieveResponseXml == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToSearchRetrieveResponse: searchRetrieveResponseXml is null");
    }
    SearchRetrieveResponseVO searchRetrieveResponseVO = null;

    try {
      // unmarshal pidServiceResponse from String
      IBindingFactory bfact =
          BindingDirectory.getFactory("AccountUserVO", SearchRetrieveResponseVO.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(searcRetrieveResponseXml);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      searchRetrieveResponseVO = (SearchRetrieveResponseVO) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(searcRetrieveResponseXml, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }

    return searchRetrieveResponseVO;
  }


  /**
   * {@inheritDoc}
   */
  public List<UserAttributeVO> transformToUserAttributesList(String userAttributesList)
      throws TechnicalException {
    logger.debug("transformToUserAttributesList(String) - String userAttributesList=\n"
        + userAttributesList);
    if (userAttributesList == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ":transformToUserAttributesList:userAttributesList is null");
    }
    UserAttributesWrapper listWrapper = null;
    try {
      // unmarshal MemberListWrapper from String
      IBindingFactory bfact = BindingDirectory.getFactory(UserAttributesWrapper.class);
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(userAttributesList);
      Object unmarshalledObject = uctx.unmarshalDocument(sr, null);
      listWrapper = (UserAttributesWrapper) unmarshalledObject;
    } catch (JiBXException e) {
      // throw a new UnmarshallingException, log the root cause of the JiBXException first
      logger.error(e.getRootCause());
      throw new UnmarshallingException(userAttributesList, e);
    } catch (ClassCastException e) {
      throw new TechnicalException(e);
    }
    // unwrap the List<ContainerVO>


    return listWrapper.getUserAttributes();
  }
}
