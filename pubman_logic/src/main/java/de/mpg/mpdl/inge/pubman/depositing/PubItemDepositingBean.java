/*
 * 
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

package de.mpg.mpdl.inge.pubman.depositing;

import static de.mpg.mpdl.inge.pubman.logging.PMLogicMessages.PUBITEM_CREATED;
import static de.mpg.mpdl.inge.pubman.logging.PMLogicMessages.PUBITEM_UPDATED;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.xmltransforming.logging.LogMethodDurationInterceptor;
import de.mpg.mpdl.inge.xmltransforming.logging.LogStartEndInterceptor;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.FrameworkContextTypeFilter;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.ItemRefFilter;
import de.mpg.mpdl.inge.model.valueobjects.FilterTaskParamVO.PubCollectionStatusFilter;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.ItemRelationVO;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AlternativeTitleVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SubjectVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.pubman.PubItemDepositing;
import de.mpg.mpdl.inge.pubman.PubItemPublishing;
import de.mpg.mpdl.inge.pubman.exceptions.ExceptionHandler;
import de.mpg.mpdl.inge.pubman.exceptions.PubCollectionNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemAlreadyReleasedException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemNotFoundException;
import de.mpg.mpdl.inge.pubman.exceptions.PubItemStatusInvalidException;
import de.mpg.mpdl.inge.pubman.exceptions.PubManException;
import de.mpg.mpdl.inge.pubman.logging.ApplicationLog;
import de.mpg.mpdl.inge.pubman.logging.PMLogicMessages;
import de.mpg.mpdl.inge.services.ContextInterfaceConnectorFactory;
import de.mpg.mpdl.inge.services.ItemInterfaceConnectorFactory;
import de.mpg.mpdl.inge.validation.ItemInvalidException;
import de.mpg.mpdl.inge.validation.ItemValidating;
import de.mpg.mpdl.inge.validation.ValidationSchemaNotFoundException;
import de.mpg.mpdl.inge.validation.valueobjects.ValidationReportVO;

/**
 * This class provides the ejb implementation of the {@link PubItemDepositing} interface.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 19.09.2007
 */
@Remote(PubItemDepositing.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Interceptors({LogStartEndInterceptor.class, LogMethodDurationInterceptor.class})
public class PubItemDepositingBean implements PubItemDepositing {
  private static final String PREDICATE_ISREVISIONOF =
      "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";

  /**
   * Logger for this class.
   */
  private static final Logger logger = Logger.getLogger(PubItemDepositingBean.class);



  /**
   * A XmlTransforming instance.
   */
  @EJB
  private XmlTransforming xmlTransforming;

  /**
   * A PubItemPublishing instance.
   */
  @EJB
  private PubItemPublishing pmPublishing;

  /**
   * A ItemValidating instance.
   */
  @EJB
  private ItemValidating itemValidating;

  /*
   * Validation points
   */
  @SuppressWarnings("unused")
  private static final String VALIDATION_POINT_DEFAULT = "default";
  private static final String VALIDATION_POINT_SUBMIT = "submit_item";
  private static final String VALIDATION_POINT_ACCEPT = "accept_item";

  /**
   * {@inheritDoc}
   */
  public PubItemVO createPubItem(ContextRO pubCollectionRef, AccountUserVO user)
      throws TechnicalException, SecurityException, PubCollectionNotFoundException,
      IllegalArgumentException {
    if (pubCollectionRef == null) {
      throw new IllegalArgumentException(getClass()
          + ".createPubItem: pubCollection reference is null.");
    }
    if (pubCollectionRef.getObjectId() == null) {
      throw new IllegalArgumentException(getClass()
          + ".createPubItem: pubCollection reference does not contain an objectId.");
    }
    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".createPubItem: user is null.");
    }

    // retrieve PubCollection for default metadata
    ContextVO collection = null;
    try {
       String context =
       de.mpg.mpdl.inge.framework.ServiceLocator.getContextHandler(user.getHandle()).retrieve(
       pubCollectionRef.getObjectId());
       collection = xmlTransforming.transformToContext(context);
//      collection =
//          ContextInterfaceConnectorFactory.getInstance()
//              .readContext(pubCollectionRef.getObjectId());
    } catch (ContextNotFoundException e) {
      throw new PubCollectionNotFoundException(pubCollectionRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, getClass() + ".createPubItem");
    }

    // create new PubItemVO
    PubItemVO result = new PubItemVO();
    result.setContext(pubCollectionRef);
    if (collection.getDefaultMetadata() != null
        && collection.getDefaultMetadata() instanceof MdsPublicationVO) {
      // set default values of collection
      result.setMetadata((MdsPublicationVO) collection.getDefaultMetadata());
    } else {
      // create empty metadata
      result.setMetadata(new MdsPublicationVO());
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  public void deletePubItem(ItemRO pubItemRef, AccountUserVO user) throws TechnicalException,
      PubItemNotFoundException, PubItemLockedException, PubItemStatusInvalidException {
    if (pubItemRef == null) {
      throw new IllegalArgumentException(getClass() + ".deletePubItem: pubItem reference is null.");
    }
    if (pubItemRef.getObjectId() == null) {
      throw new IllegalArgumentException(getClass()
          + ".deletePubItem: pubItem reference does not contain an objectId.");
    }
    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".deletePubItem: user is null.");
    }

    try {
      ServiceLocator.getItemHandler(user.getHandle()).delete(pubItemRef.getObjectId());
      ApplicationLog.info(PMLogicMessages.PUBITEM_DELETED, new Object[] {pubItemRef.getObjectId(),
          user.getUserid()});
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItemRef, e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemRef, e);
    } catch (AlreadyPublishedException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemRef, e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, getClass() + "deletePubItem");
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<ContextVO> getPubCollectionListForDepositing(AccountUserVO user)
      throws TechnicalException {
    if (user == null) {
      throw new IllegalArgumentException(getClass()
          + ".getPubCollectionListForDepositing: user is null.");
    }
    if (user.getReference() == null || user.getReference().getObjectId() == null) {
      throw new IllegalArgumentException(getClass()
          + ".getPubCollectionListForDepositing: user reference does not contain an objectId");
    }

    try {
      List<ContextVO> contextList = new ArrayList<ContextVO>();
      String xmlGrants =
          ServiceLocator.getUserAccountHandler(user.getHandle()).retrieveCurrentGrants(
              user.getReference().getObjectId());

      List<GrantVO> grants = xmlTransforming.transformToGrantVOList(xmlGrants);

      if (grants.size() == 0) {
        return contextList;
      }

      // Create filter
      FilterTaskParamVO filterParam = new FilterTaskParamVO();

      FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("Pubman");
      filterParam.getFilterList().add(typeFilter);

      ItemRefFilter itmRefFilter = filterParam.new ItemRefFilter();
      filterParam.getFilterList().add(itmRefFilter);

      boolean hasGrants = false;

      for (GrantVO grant : grants) {
        if (PredefinedRoles.DEPOSITOR.frameworkValue().equals(grant.getRole())) {
          if (grant.getObjectRef() != null) {
            itmRefFilter.getIdList().add(new ItemRO(grant.getObjectRef()));
            hasGrants = true;
          }
        }
      }

      if (!hasGrants) {
        return contextList;
      }

      PubCollectionStatusFilter statusFilter =
          filterParam.new PubCollectionStatusFilter(ContextVO.State.OPENED);
      filterParam.getFilterList().add(statusFilter);

      HashMap<String, String[]> filterMap = filterParam.toMap();

      // Get context list
      String xmlContextList =
          ServiceLocator.getContextHandler(user.getHandle()).retrieveContexts(filterMap);
      contextList =
          (List<ContextVO>) xmlTransforming
              .transformSearchRetrieveResponseToContextList(xmlContextList);

      return contextList;

    } catch (Exception e) {
      // No business exceptions expected.
      ExceptionHandler.handleException(e,
          "getPubCollectionListForDepositing for user <" + user.getUserid() + ">");
      throw new TechnicalException(e);
    }
  }

  public List<ContextVO> getPubCollectionListForDepositing() throws TechnicalException {
    try {
      // Create filter
      FilterTaskParamVO filterParam = new FilterTaskParamVO();

      FrameworkContextTypeFilter typeFilter = filterParam.new FrameworkContextTypeFilter("Pubman");
      filterParam.getFilterList().add(typeFilter);

      PubCollectionStatusFilter statusFilter =
          filterParam.new PubCollectionStatusFilter(ContextVO.State.OPENED);
      filterParam.getFilterList().add(statusFilter);

      HashMap<String, String[]> filterMap = filterParam.toMap();

      if (logger.isDebugEnabled()) {
        String query[] = filterMap.get("query");
        logger.debug("getPubCollectionListForDepositing " + query[0]);
      }

      // Get context list
      String xmlContextList = ServiceLocator.getContextHandler().retrieveContexts(filterMap);
      // ... and transform to PubCollections.
      List<ContextVO> contextList =
          (List<ContextVO>) xmlTransforming
              .transformSearchRetrieveResponseToContextList(xmlContextList);

      return contextList;
    } catch (Exception e) {
      // No business exceptions expected.
      ExceptionHandler.handleException(e, "PubItemDepositing.getPubCollectionListForDepositing");
      throw new TechnicalException(e);
    }
  }

  /**
   * {@inheritDoc} Changed by Peter Broszeit, 17.10.2007: Method prepared to save also released
   * items and restructed.
   */
  public PubItemVO savePubItem(PubItemVO pubItem, AccountUserVO user) throws TechnicalException,
      PubItemMandatoryAttributesMissingException, PubCollectionNotFoundException,
      PubItemLockedException, PubItemNotFoundException, PubItemStatusInvalidException,
      SecurityException, PubItemAlreadyReleasedException, URISyntaxException,
      AuthorizationException {
    // Check parameters and prepare
    if (pubItem == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ".savePubItem: pubItem is null.");
    }
    if (user == null) {
      throw new IllegalArgumentException(getClass().getSimpleName() + ".savePubItem: user is null.");
    }

    // Transform the item to XML
    String itemXML = xmlTransforming.transformToItem(pubItem);
    if (logger.isDebugEnabled()) {
      logger
          .debug("PubItemDepositingBean.savePubItem: pubItem[VO] successfully transformed to item[XML]"
              + "\nitem: " + itemXML);
    }
    try {
      /*
       * Validation of the existing items - gets Item string and Validation point "default" -
       * returns validation report (as exception).
       */

      ValidationReportVO report = itemValidating.validateItemObject(pubItem);
      if (!report.isValid()) {
        throw new ItemInvalidException(report);
      }
      // Get item handler
      ItemHandler itemHandler = ServiceLocator.getItemHandler(user.getHandle());
      PMLogicMessages message;
      String itemStored;
      PubItemVO pubItemStored;
      // Check whether item has to be created or updated
      if (pubItem.getVersion() == null || pubItem.getVersion().getObjectId() == null) {
        itemStored = itemHandler.create(itemXML);
        message = PUBITEM_CREATED;
      } else {
        logger.debug("pubItem.getVersion(): " + pubItem.getVersion());

        // Update the item and set message
        itemStored = itemHandler.update(pubItem.getVersion().getObjectId(), itemXML);
        message = PUBITEM_UPDATED;
      }

      // Transform the item and log the action.
      pubItemStored = xmlTransforming.transformToPubItem(itemStored);
      ApplicationLog.info(message,
          new Object[] {pubItemStored.getVersion().getObjectId(), user.getUserid()});
      return pubItemStored;
    } catch (MissingAttributeValueException e) {
      throw new PubItemMandatoryAttributesMissingException(pubItem, e);
    } catch (ContextNotFoundException e) {
      throw new PubCollectionNotFoundException(pubItem.getContext(), e);
    } catch (MissingElementValueException e) {
      throw new PubItemMandatoryAttributesMissingException(pubItem, e);
    } catch (LockingException e) {
      throw new PubItemLockedException(pubItem.getVersion(), e);
    } catch (InvalidContextException e) {
      throw new PubCollectionNotFoundException(pubItem.getContext(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItem.getVersion(), e);
    } catch (AlreadyPublishedException e) {
      throw new PubItemAlreadyReleasedException(pubItem.getVersion(), e);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItem.getVersion(), e);
    } catch (FileNotFoundException e) {
      throw new PubFileContentNotFoundException(pubItem.getFiles(), e);
    } catch (NotPublishedException e) {
      throw new TechnicalException(e);
    } catch (AuthorizationException e) {
      throw e;
    } catch (Exception e) {
      ExceptionHandler.handleException(e, getClass().getSimpleName() + ".savePubItem");
      throw new TechnicalException();
    }
  }

  /**
   * {@inheritDoc}
   */
  public PubItemVO submitPubItem(PubItemVO pubItem, String submissionComment, AccountUserVO user)
      throws DepositingException, TechnicalException, PubItemNotFoundException, SecurityException,
      PubManException, ItemInvalidException, URISyntaxException, AuthorizationException {
    long gstart = System.currentTimeMillis();

    if (pubItem == null) {
      throw new IllegalArgumentException(getClass() + ".submitPubItem: pubItem is null.");
    }

    logger.info("*** start submit of <" + pubItem.getVersion().getObjectId() + ">");

    if (user == null) {
      throw new IllegalArgumentException(getClass() + ".submitPubItem: user is null.");
    }
    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
    // Validate the item
    try {
      long start = System.currentTimeMillis();
      ValidationReportVO report =
          itemValidating.validateItemObject(pubItem, VALIDATION_POINT_SUBMIT);
      long end = System.currentTimeMillis();
      logger.info("validation of <" + pubItem.getVersion().getObjectId() + "> needed <"
          + (end - start) + "> msec");
      if (!report.isValid()) {
        throw new ItemInvalidException(report);
      }
    } catch (ValidationSchemaNotFoundException e) {
      throw new TechnicalException("Error validation item", e);
    }
    // first save the item
    PubItemVO savedPubItem = pubItem;
    // PubItemVO savedPubItem = savePubItem(pubItem, user);
    PubItemVO pubItemActual = null;
    // then submit the item
    try {
      TaskParamVO taskParam =
          new TaskParamVO(savedPubItem.getModificationDate(), submissionComment);
      long s1 = System.currentTimeMillis();
      itemHandler.submit(savedPubItem.getVersion().getObjectId(),
          xmlTransforming.transformToTaskParam(taskParam));
      ApplicationLog.info(PMLogicMessages.PUBITEM_SUBMITTED, new Object[] {
          savedPubItem.getVersion().getObjectId(), user.getUserid()});
      long e1 = System.currentTimeMillis();
      logger.info("pure itemHandler.submit item " + pubItem.getVersion().getObjectId()
          + "> needed <" + (e1 - s1) + "> msec");

      // Retrieve item once again.
      String item = itemHandler.retrieve(savedPubItem.getVersion().getObjectId());
      pubItemActual = xmlTransforming.transformToPubItem(item);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(savedPubItem.getVersion(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(savedPubItem.getVersion(), e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "PubItemDepositing.submitPubItem");
    }

    long gend = System.currentTimeMillis();
    logger.info("*** total submit of <" + pubItem.getVersion().getObjectId() + "> needed <"
        + (gend - gstart) + "> msec");
    return pubItemActual;
  }

  /**
   * {@inheritDoc}
   * 
   * @author Peter Broszeit
   */
  public PubItemVO acceptPubItem(PubItemVO pubItem, String acceptComment, AccountUserVO user)
      throws TechnicalException, SecurityException, PubItemNotFoundException, ItemInvalidException {
    // Check parameters and prepare
    if (pubItem == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ".acceptPubItem: pubItem is null.");
    }
    if (user == null) {
      throw new IllegalArgumentException(getClass().getSimpleName()
          + ".acceptPubItem: user is null.");
    }
    ItemHandler itemHandler;
    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }
    // Validate the item
    try {
      ValidationReportVO report =
          itemValidating.validateItemObject(pubItem, VALIDATION_POINT_ACCEPT);
      if (!report.isValid()) {
        throw new ItemInvalidException(report);
      }
    } catch (ValidationSchemaNotFoundException e) {
      throw new TechnicalException("Error validation item", e);
    }
    // Release the item
    try {
      // Because no workflow system is used at this time
      // automatic release is triggered here
      // item has to be retrieved again to get actual modification date
      pmPublishing.releasePubItem(pubItem.getVersion(), pubItem.getModificationDate(),
          acceptComment, user);

      // Retrieve item once again.
      String item = itemHandler.retrieve(pubItem.getVersion().getObjectId());
      pubItem = xmlTransforming.transformToPubItem(item);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItem.getVersion(), e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, getClass().getSimpleName() + ".acceptPubItem");
    }
    // Return the accepted item
    return pubItem;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.pubman.PubItemDepositing#createRevisionOfItem(de.mpg.escidoc.services
   * .common.valueobjects.PubItemVO, java.lang.String,
   * de.mpg.mpdl.inge.model.valueobjects.ContextVO,
   * de.mpg.mpdl.inge.model.valueobjects.AccountUserVO)
   */
  public PubItemVO createRevisionOfItem(PubItemVO originalPubItem, String relationComment,
      ContextRO pubCollection, AccountUserVO owner) throws SecurityException,
      PubItemMandatoryAttributesMissingException, PubItemLockedException,
      PubCollectionNotFoundException, PubItemNotFoundException, PubItemStatusInvalidException,
      PubItemAlreadyReleasedException, TechnicalException {
    logger.debug("createRevisionOfItem(" + originalPubItem.getVersion().getObjectId() + ","
        + relationComment + "," + pubCollection.getObjectId() + "," + owner.getHandle());
    // Create an empty new item.
    PubItemVO copiedPubItem = new PubItemVO();
    // Set the owner.
    copiedPubItem.setOwner(owner.getReference());
    // Set the collection.
    copiedPubItem.setContext(pubCollection);
    // Set new empty metadata.
    copiedPubItem.setMetadata(new MdsPublicationVO());
    // Copy the genre.
    copiedPubItem.getMetadata().setGenre(originalPubItem.getMetadata().getGenre());
    // Copy the creators.
    for (CreatorVO creator : originalPubItem.getMetadata().getCreators()) {
      copiedPubItem.getMetadata().getCreators().add((CreatorVO) creator.clone());
    }
    // Copy the title.
    if (originalPubItem.getMetadata().getTitle() != null) {
      copiedPubItem.getMetadata().setTitle(originalPubItem.getMetadata().getTitle());
    }
    // Copy the languages.
    for (String language : originalPubItem.getMetadata().getLanguages()) {
      copiedPubItem.getMetadata().getLanguages().add(language);
    }
    // Copy the alternative titles.
    for (AlternativeTitleVO title : originalPubItem.getMetadata().getAlternativeTitles()) {
      copiedPubItem.getMetadata().getAlternativeTitles().add((AlternativeTitleVO) title.clone());
    }
    // Copy free keywords.
    if (originalPubItem.getMetadata().getFreeKeywords() != null) {
      copiedPubItem.getMetadata().setFreeKeywords(originalPubItem.getMetadata().getFreeKeywords());
    }

    // copy subjects
    if (originalPubItem.getMetadata().getSubjects() != null) {
      for (SubjectVO subject : originalPubItem.getMetadata().getSubjects()) {
        copiedPubItem.getMetadata().getSubjects().add(subject);
      }

    }

    ItemRelationVO relation = new ItemRelationVO();
    relation.setType(PREDICATE_ISREVISIONOF);
    relation.setTargetItemRef(originalPubItem.getVersion());
    relation.setDescription(relationComment);
    copiedPubItem.getRelations().add(relation);
    // return the new created revision of the given pubItem.
    return copiedPubItem;
  }



  /**
   * {@inheritDoc}
   */
  public PubItemVO submitAndReleasePubItem(PubItemVO pubItem, String submissionComment,
      AccountUserVO user) throws DepositingException, TechnicalException, PubItemNotFoundException,
      SecurityException, PubManException, ItemInvalidException {

    ItemHandler itemHandler;

    try {
      itemHandler = ServiceLocator.getItemHandler(user.getHandle());
    } catch (Exception e) {
      throw new TechnicalException(e);
    }

    PubItemVO pubItemActual = null;

    // then release the item
    try {
      pubItemActual = submitPubItem(pubItem, submissionComment, user);

      pmPublishing.releasePubItem(pubItemActual.getVersion(), pubItemActual.getModificationDate(),
          submissionComment, user);

      // Retrieve item once again.
      String item = itemHandler.retrieve(pubItemActual.getVersion().getObjectId());
      pubItemActual = xmlTransforming.transformToPubItem(item);
    } catch (InvalidStatusException e) {
      throw new PubItemStatusInvalidException(pubItemActual.getVersion(), e);
    } catch (ItemNotFoundException e) {
      throw new PubItemNotFoundException(pubItemActual.getVersion(), e);
    } catch (Exception e) {
      ExceptionHandler.handleException(e, "PubItemDepositing.submitPubItem");
    }
    return pubItemActual;
  }
}
