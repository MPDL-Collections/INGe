package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.BasicDbRO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericService;

public abstract class GenericServiceImpl<ModelObject, DbObject extends BasicDbRO, Id extends Serializable>
    extends GenericServiceBaseImpl<ModelObject> implements GenericService<ModelObject, Id> {

  @Autowired
  private AuthorizationService aaService;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;

  private final static Logger logger = LogManager.getLogger(GenericServiceImpl.class);

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject create(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToCreate = createEmptyDbObject();
    List<Id> reindexList = updateObjectWithValues(object, objectToCreate, userAccount, true);
    updateWithTechnicalMetadata(objectToCreate, userAccount, true);
    checkAa("create", userAccount, transformToOld(objectToCreate));
    try {
      objectToCreate = getDbRepository().saveAndFlush(objectToCreate);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    ModelObject objectToReturn = transformToOld(objectToCreate);
    if (getElasticDao() != null) {
      getElasticDao().createImmediately(objectToCreate.getObjectId(), objectToReturn);
    }

    if (reindexList != null) {
      reindex(reindexList);;
    }
    return objectToReturn;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public ModelObject update(ModelObject object, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeUpdated = getDbRepository().findOne(getObjectId(object));
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkEqualModificationDate(getModificationDate(object), getModificationDate(transformToOld(objectToBeUpdated)));
    List<Id> reindexList = updateObjectWithValues(object, objectToBeUpdated, userAccount, false);
    updateWithTechnicalMetadata(objectToBeUpdated, userAccount, false);

    checkAa("update", userAccount, transformToOld(objectToBeUpdated));
    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    ModelObject objectToReturn = transformToOld(objectToBeUpdated);
    if (getElasticDao() != null) {
      getElasticDao().updateImmediately(objectToBeUpdated.getObjectId(), objectToReturn);
    }
    if (reindexList != null) {
      reindex(reindexList);
    }
    return objectToReturn;
  }



  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = aaService.checkLoginRequired(authenticationToken);
    DbObject objectToBeDeleted = getDbRepository().findOne(id);
    if (objectToBeDeleted == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }
    checkAa("delete", userAccount, transformToOld(objectToBeDeleted));
    getDbRepository().delete(id);
    if (getElasticDao() != null) {
      getElasticDao().delete(getIdForElasticSearch(id));
    }

  }

  @Transactional(readOnly = true)
  @Override
  public ModelObject get(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    AccountUserVO userAccount = null;
    ModelObject object = transformToOld(getDbRepository().findOne(id));
    if (authenticationToken != null) {
      userAccount = aaService.checkLoginRequired(authenticationToken);
    }

    checkAa("get", userAccount, object);
    return object;
  }



  protected static void updateWithTechnicalMetadata(BasicDbRO object, AccountUserVO userAccount, boolean create) {
    Date currentDate = new Date();
    AccountUserDbRO mod = new AccountUserDbRO();
    mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getReference().getObjectId());

    if (create) {
      object.setCreationDate(currentDate);
      object.setCreator(mod);
    }

    object.setLastModificationDate(currentDate);
    object.setModifier(mod);
  }



  protected abstract DbObject createEmptyDbObject();

  protected abstract List<Id> updateObjectWithValues(ModelObject givenObject, DbObject objectToBeUpdated, AccountUserVO userAccount,
      boolean create) throws IngeTechnicalException, IngeApplicationException;

  protected abstract ModelObject transformToOld(DbObject dbObject);

  protected abstract JpaRepository<DbObject, Id> getDbRepository();

  protected abstract GenericDaoEs<ModelObject> getElasticDao();

  protected abstract Id getObjectId(ModelObject object);

  protected abstract Date getModificationDate(ModelObject object);

  protected String getIdForElasticSearch(Id id) {
    return id.toString();
  };

  protected void reindex(List<Id> idList) throws IngeTechnicalException {
    // Reindex old and new Parents
    if (getElasticDao() != null) {
      for (Id id : idList) {
        reindex(id, true);
      }
    }
  }



  @Transactional(readOnly = true)
  protected void reindex(Id id, boolean immediate) throws IngeTechnicalException {
    // Reindex old and new Parents
    if (getElasticDao() != null) {
      logger.info("Reindexing object " + id);
      ModelObject vo = transformToOld(getDbRepository().findOne(id));
      if (immediate) {
        getElasticDao().createImmediately(getIdForElasticSearch(id), vo);
      } else {
        getElasticDao().create(getIdForElasticSearch(id), vo);
      }
    }
  }

  @Override
  public void reindex(Id id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    // TODO AA
    reindex(id, false);
  }



  @Override
  @Transactional(readOnly = true)
  public void reindexAll(String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    // TODO AA
    if (getElasticDao() != null) {
      String entityName =
          ((Class<ModelObject>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();



      Query<Id> query = (Query<Id>) entityManager.createQuery("SELECT e.objectId FROM " + entityName + " e");
      query.setReadOnly(true);
      query.setFetchSize(500);
      query.setCacheMode(CacheMode.IGNORE);
      query.setFlushMode(FlushModeType.COMMIT);
      query.setCacheable(false);
      ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

      int count = 0;
      while (results.next()) {
        try {
          count++;

          Id id = (Id) results.get(0);

          queueJmsTemplate.convertAndSend("reindex-" + entityName, id);

          // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
          if (count % 1000 == 0) {
            logger.info("Clearing entity manager while reindexing");
            entityManager.flush();
            entityManager.clear();
          }

        } catch (Exception e) {
          logger.error("Error while reindexing ", e);
        }


      }
    }
  }

  protected void checkEqualModificationDate(Date date1, Date date2) throws IngeApplicationException {
    if (date1 == null || date2 == null || !date1.equals(date2)) {
      throw new IngeApplicationException("Object changed in the meantime: " + date1 + "  does not equal  " + date2);
    }
  }


  protected static void handleDBException(DataAccessException exception) throws IngeApplicationException {

    try {
      throw exception;
    } catch (ObjectRetrievalFailureException ex) {
      throw new IngeApplicationException(ex.getMessage(), ex);
    } catch (DataIntegrityViolationException ex) {
      StringBuilder message = new StringBuilder("Object already exists.");
      // Get message from
      if (ex.getCause() != null && ex.getCause().getCause() != null) {
        message.append(" ").append(ex.getCause().getCause().getMessage());
      }
      throw new IngeApplicationException(message.toString(), ex);
    }

  }



}



// public void reindex() throws IngeServiceException, AaException;


