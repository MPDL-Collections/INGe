package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.model.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.referenceobjects.AffiliationRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.util.EntityTransformer;

@Service
@Primary
public class ContextServiceDbImpl extends GenericServiceImpl<ContextVO, ContextDbVO> implements
    ContextService {
  private final static Logger logger = LogManager.getLogger(ContextServiceDbImpl.class);

  public final static String INDEX_OBJECT_ID = "reference.objectId";
  public final static String INDEX_STATE = "state";

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private ContextDaoEs<QueryBuilder> contextDao;

  @Autowired
  private ContextRepository contextRepository;

  @PersistenceContext
  EntityManager entityManager;



  public void reindex() {

    Query<de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO> query =
        (Query<de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO>) entityManager
            .createQuery("SELECT context FROM ContextVO context");
    query.setReadOnly(true);
    query.setFetchSize(1000);
    query.setCacheable(false);
    ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

    while (results.next()) {
      try {
        de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO object =
            (de.mpg.mpdl.inge.db.model.valueobjects.ContextDbVO) results.get(0);
        ContextVO context = EntityTransformer.transformToOld(object);
        logger.info("Reindexing context " + context.getReference().getObjectId());
        contextDao.create(context.getReference().getObjectId(), context);
      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }


    }

  }


  @Override
  @Transactional
  public ContextVO open(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, ContextDbVO.State.OPENED);
  }


  @Override
  @Transactional
  public ContextVO close(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException,
      IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, ContextDbVO.State.CLOSED);
  }

  private ContextVO changeState(String id, Date modificationDate, String authenticationToken,
      ContextDbVO.State state) throws IngeTechnicalException, AuthenticationException,
      AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    ContextDbVO contextDbToBeUpdated = contextRepository.findOne(id);
    if (contextDbToBeUpdated == null) {
      throw new IngeTechnicalException("Context with given id " + id + " not found.");
    }

    ContextVO contextVoToBeUpdated = transformToOld(contextDbToBeUpdated);

    if (!checkEqualModificationDate(modificationDate, getModificationDate(contextVoToBeUpdated))) {
      throw new IngeTechnicalException("Object changed in meantime");
    }

    checkAa((state == ContextDbVO.State.OPENED ? "open" : "close"), userAccount,
        contextVoToBeUpdated);

    contextDbToBeUpdated.setState(state);
    updateWithTechnicalMetadata(contextDbToBeUpdated, userAccount, false);

    contextDbToBeUpdated = contextRepository.saveAndFlush(contextDbToBeUpdated);

    ContextVO contextToReturn = EntityTransformer.transformToOld(contextDbToBeUpdated);
    contextDao.update(contextDbToBeUpdated.getObjectId(), contextToReturn);
    return contextToReturn;
  }



  @Override
  protected ContextDbVO createEmptyDbObject() {
    return new ContextDbVO();
  }



  @Override
  protected List<String> updateObjectWithValues(ContextVO givenContext,
      ContextDbVO toBeUpdatedContext, AccountUserVO userAccount, boolean createNew)
      throws IngeTechnicalException {

    toBeUpdatedContext.setAdminDescriptor(givenContext.getAdminDescriptor());

    toBeUpdatedContext.setDescription(givenContext.getDescription());
    toBeUpdatedContext.setName(givenContext.getName());

    if (givenContext.getName() == null || givenContext.getName().trim().isEmpty()) {
      throw new IngeTechnicalException("A name is required");
    }

    if (givenContext.getResponsibleAffiliations() != null) {
      toBeUpdatedContext.setResponsibleAffiliations(new ArrayList<AffiliationDbRO>());
      for (AffiliationRO aff : givenContext.getResponsibleAffiliations()) {
        AffiliationDbRO newAffRo = new AffiliationDbRO();
        newAffRo.setObjectId(aff.getObjectId());
        toBeUpdatedContext.getResponsibleAffiliations().add(newAffRo);
      }
    }
    toBeUpdatedContext.setType(givenContext.getType());


    if (createNew) {
      toBeUpdatedContext.setObjectId(idProviderService.getNewId(ID_PREFIX.CONTEXT));
      toBeUpdatedContext.setState(ContextDbVO.State.CREATED);
    }

    return null;

  }



  @Override
  protected ContextVO transformToOld(ContextDbVO dbObject) {
    return EntityTransformer.transformToOld(dbObject);
  }



  @Override
  protected JpaRepository<ContextDbVO, String> getDbRepository() {
    return contextRepository;
  }



  @Override
  protected String getObjectId(ContextVO object) {
    return object.getReference().getObjectId();
  }



  @Override
  protected GenericDaoEs<ContextVO, QueryBuilder> getElasticDao() {
    return contextDao;
  }


  @Override
  protected Date getModificationDate(ContextVO object) {
    return object.getLastModificationDate();
  }


}
