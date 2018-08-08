package de.mpg.mpdl.inge.service.pubman.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.CacheMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.AuditRepository;
import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.FileRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.ItemObjectRepository;
import de.mpg.mpdl.inge.db.repository.ItemRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO.EventType;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.ChecksumAlgorithm;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.VersionableId;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
@Primary
public class PubItemServiceDbImpl extends GenericServiceBaseImpl<ItemVersionVO> implements PubItemService, ReindexListener {

  private static final Logger logger = LogManager.getLogger(PubItemServiceDbImpl.class);

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private ItemObjectRepository itemObjectRepository;

  @Autowired
  private AuditRepository auditRepository;

  @Autowired
  private PubItemDaoEs pubItemDao;

  @Autowired
  private FileService fileService;

  @Autowired
  private PidService pidService;

  @Autowired
  private ItemValidatingService itemValidatingService;

  @Autowired
  private OrganizationService organizationService;

  @PersistenceContext
  EntityManager entityManager;

  @Autowired
  @Qualifier("queueJmsTemplate")
  private JmsTemplate queueJmsTemplate;

  @Autowired
  @Qualifier("topicJmsTemplate")
  private JmsTemplate topicJmsTemplate;

  @Autowired
  private FileRepository fileRepository;


  public static String INDEX_MODIFICATION_DATE = "modificationDate";
  public static String INDEX_CREATION_DATE = "creationDate";
  public static String INDEX_LOCAL_TAGS = "localTags";
  public static String INDEX_CONTEXT_OBJECT_ID = "context.objectId";
  public static String INDEX_CONTEXT_TITLE = "context.name";
  public static String INDEX_OWNER_OBJECT_ID = "creator.objectId";
  public static String INDEX_OWNER_TITLE = "creator.name";
  public static String INDEX_PUBLIC_STATE = "publicState";
  public static String INDEX_PID = "objectPid";

  public static String INDEX_VERSION_STATE = "versionState";
  public static String INDEX_LATESTVERSION_VERSIONNUMBER = "latestVersion.versionNumber";
  public static String INDEX_LATESTVERSION_STATE = "latestVersion.versionState";
  public static String INDEX_LATESTRELEASE_DATE = "latestRelease.modificationDate";
  public static String INDEX_VERSION_VERSIONNUMBER = "versionNumber";
  public static String INDEX_VERSION_OBJECT_ID = "objectId";
  public static String INDEX_VERSION_PID = "versionPid";

  public static String INDEX_METADATA_CREATOR_SORT = "sort-metadata-creators-compound";
  public static String INDEX_METADATA_DATE_CATEGORY_SORT = "sort-metadata-dates-by-category";
  public static String INDEX_METADATA_DATE_CATEGORY_YEAR_SORT = "sort-metadata-dates-by-category-year";

  public static String INDEX_METADATA_CREATOR_PERSON_IDENTIFIER_ID = "metadata.creators.person.identifier.id";
  public static String INDEX_METADATA_CREATOR_PERSON_FAMILYNAME = "metadata.creators.person.familyName";
  public static String INDEX_METADATA_CREATOR_PERSON_GIVENNAME = "metadata.creators.person.givenName";

  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIER = "metadata.creators.person.organizations.identifier";
  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_IDENTIFIERPATH = "metadata.creators.person.organizations.identifierPath";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIER = "metadata.creators.organization.identifier";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_IDENTIFIERPATH = "metadata.creators.organization.identifierPath";
  public static String INDEX_METADATA_CREATOR_PERSON_ORGANIZATION_NAME = "metadata.creators.person.organizations.name";
  public static String INDEX_METADATA_CREATOR_ORGANIZATION_NAME = "metadata.creators.organization.name";
  public static String INDEX_METADATA_CREATOR_ROLE = "metadata.creators.role";

  public static String INDEX_METADATA_TITLE = "metadata.title";
  public static String INDEX_METADATA_ALTERNATIVETITLE_VALUE = "metadata.alternativeTitles.value";
  public static String INDEX_METADATA_DATE_ANY = "metadata.anyDates";
  public static String INDEX_METADATA_DATE_PUBLISHED_IN_PRINT = "metadata.datePublishedInPrint";
  public static String INDEX_METADATA_DATE_PUBLISHED_ONLINE = "metadata.datePublishedOnline";
  public static String INDEX_METADATA_DATE_ACCEPTED = "metadata.dateAccepted";
  public static String INDEX_METADATA_DATE_CREATED = "metadata.dateCreated";
  public static String INDEX_METADATA_DATE_MODIFIED = "metadata.dateModified";
  public static String INDEX_METADATA_DATE_SUBMITTED = "metadata.dateSubmitted";
  public static String INDEX_METADATA_EVENT_TITLE = "metadata.event.title";
  public static String INDEX_METADATA_EVENT_STARTDATE = "metadata.event.startDate";
  public static String INDEX_METADATA_EVENT_ENDDATE = "metadata.event.endDate";
  public static String INDEX_METADATA_EVENT_INVITATION_STATUS = "metadata.event.invitationStatus";
  public static String INDEX_METADATA_GENRE = "metadata.genre";
  public static String INDEX_METADATA_REVIEW_METHOD = "metadata.reviewMethod";
  public static String INDEX_METADATA_SUBJECTS_VALUE = "metadata.subjects.value";
  public static String INDEX_METADATA_SUBJECTS_TYPE = "metadata.subjects.type";
  public static String INDEX_METADATA_FREEKEYWORDS = "metadata.freeKeywords";
  public static String INDEX_METADATA_DEGREE = "metadata.degree";
  public static String INDEX_METADATA_LANGUAGES = "metadata.languages";
  public static String INDEX_METADATA_IDENTIFIERS_ID = "metadata.identifiers.id";
  public static String INDEX_METADATA_IDENTIFIERS_TYPE = "metadata.identifiers.type";

  public static String INDEX_METADATA_PUBLISHINGINFO_PUBLISHER_ID = "metadata.publishingInfo.publisher";
  public static String INDEX_METADATA_PUBLISHINGINFO_PLACE = "metadata.publishingInfo.place";
  public static String INDEX_METADATA_PUBLISHINGINFO_EDITION = "metadata.publishingInfo.edition";

  public static String INDEX_METADATA_PROJECTINFO_TITLE = "metadata.projectInfo.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_TITLE = "metadata.projectInfo.fundingInfo.fundingOrganization.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_ORGANIZATION_IDENTIFIERS_ID =
      "metadata.projectInfo.fundingInfo.fundingOrganization.identifiers.id";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_TITLE = "metadata.projectInfo.fundingInfo.fundingProgram.title";
  public static String INDEX_METADATA_PROJECTINFO_FUNDING_PROGRAM_IDENTIFIERS_ID =
      "metadata.projectInfo.fundingInfo.fundingProgram.identifiers.id";
  public static String INDEX_METADATA_PROJECTINFO_GRANT_IDENTIFIER_ID = "metadata.projectInfo.grantIdentifier.id";

  public static String INDEX_METADATA_SOURCES_TITLE = "metadata.sources.title";
  public static String INDEX_METADATA_SOURCES_ALTERNATIVE_TITLE = "metadata.sources.alternativeTitles.value";

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_FAMILYNAME = "metadata.sources.creators.person.familyName";

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_GIVENNAME = "metadata.sources.creators.person.givenName";

  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATIONS_IDENTIFIER =
      "metadata.sources.creators.person.organizations.identifier";
  public static String INDEX_METADATA_SOURCES_CREATOR_PERSON_ORGANIZATIONS_IDENTIFIERPATH =
      "metadata.sources.creators.person.organizations.identifierPath";

  public static String INDEX_FILE_OBJECT_ID = "files.objectId";

  public static String INDEX_FILE_METADATA_EMBARGO_UNTIL = "files.metadata.embargoUntil";

  public static String INDEX_FILE_VISIBILITY = "files.visibility";

  public static String INDEX_FILE_CONTENTCATEGORY = "files.metadata.contentCategory";

  public static String INDEX_FILE_STORAGE = "files.storage";

  public static String INDEX_FILE_NAME = "files.name";

  public static String INDEX_FULLTEXT_CONTENT = "fileData.attachment.content";

  public static String INDEX_FULLTEXT_ITEM_ID = "fileData.itemId";

  public static String INDEX_FULLTEXT_FILE_ID = "fileData.fileId";

  public static final String REST_SERVICE_URL = PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL);
  public static final String REST_COMPONENT_PATH = PropertyReader.getProperty(PropertyReader.INGE_REST_FILE_PATH);

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO create(ItemVersionVO pubItemVO, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    long start = System.currentTimeMillis();
    Principal principal = aaService.checkLoginRequired(authenticationToken);

    de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO contextNew =
        contextRepository.findOne(pubItemVO.getObject().getContext().getObjectId());


    if (contextNew == null) {
      throw new IngeApplicationException("Context with id " + pubItemVO.getObject().getContext().getObjectId() + "not found");
    }

    PubItemUtil.cleanUpItem(pubItemVO);

    PubItemUtil.setOrganizationIdPathInItem(pubItemVO, organizationService);

    ItemVersionVO pubItemToCreate = buildPubItemToCreate("dummyId", contextNew, pubItemVO, principal.getUserAccount());

    pubItemToCreate.setFiles(pubItemVO.getFiles());

    checkAa("create", principal, pubItemToCreate, contextNew);

    validate(pubItemToCreate, ValidationPoint.SAVE);

    String id = idProviderService.getNewId(ID_PREFIX.ITEM);
    VersionableId newId = new VersionableId(id, 1);
    pubItemToCreate.setObjectId(id);
    pubItemToCreate.getObject().setObjectId(id);

    pubItemToCreate.setFiles(handleFiles(pubItemVO, null, principal, newId));

    try {
      pubItemToCreate = itemRepository.saveAndFlush(pubItemToCreate);
    } catch (DataAccessException e) {
      rollbackSavedFiles(pubItemVO);
      GenericServiceImpl.handleDBException(e);
    }


    createAuditEntry(pubItemToCreate, EventType.CREATE);
    reindex(pubItemToCreate);
    sendEventTopic(pubItemToCreate, "create");
    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + newId + " successfully created in " + time + " ms");

    return pubItemToCreate;
  }



  private void createAuditEntry(ItemVersionVO pubItem, EventType event) throws IngeApplicationException, IngeTechnicalException {
    AuditDbVO audit = new AuditDbVO();
    audit.setEvent(event);
    audit.setComment(pubItem.getMessage());
    audit.setModificationDate(pubItem.getModificationDate());
    audit.setModifier(pubItem.getModifier());
    audit.setPubItem(pubItem);
    try {
      auditRepository.saveAndFlush(audit);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
  }

  private ItemVersionVO buildPubItemToCreate(String objectId, de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO context,
      ItemVersionVO givenPubItemVO, AccountUserDbVO userAccount) {
    Date currentDate = new Date();

    ItemVersionVO pubItem = new ItemVersionVO();

    pubItem.setMetadata(givenPubItemVO.getMetadata());
    pubItem.setMessage(null);
    pubItem.setModificationDate(currentDate);
    AccountUserDbRO mod = new AccountUserDbRO();
    //Moved out due to DSGVO
    //mod.setName(userAccount.getName());
    mod.setObjectId(userAccount.getObjectId());
    pubItem.setModifier(mod);
    pubItem.setObjectId(objectId);
    pubItem.setVersionState(ItemVersionRO.State.PENDING);
    pubItem.setVersionNumber(1);
    pubItem.setVersionPid(null);// TODO

    ItemRootVO pubItemObject = new ItemRootVO();
    pubItemObject.setContext(context);
    pubItemObject.setCreationDate(currentDate);
    pubItemObject.setLastModificationDate(currentDate);
    pubItemObject.setLatestVersion(pubItem);
    pubItemObject.setLocalTags(givenPubItemVO.getObject().getLocalTags());
    pubItemObject.setObjectId(objectId);
    pubItemObject.setCreator(mod);
    pubItemObject.setObjectPid(null);// TODO
    pubItemObject.setPublicState(ItemVersionRO.State.PENDING);

    pubItem.setObject(pubItemObject);

    return pubItem;
  }


  private ItemVersionRO updatePubItemWithTechnicalMd(ItemVersionVO latestVersion, String modifierName, String modifierId) {
    Date currentDate = new Date();

    latestVersion.setModificationDate(currentDate);
    de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO mod = new de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbRO();
    //Moved out due do DSGVO
    //mod.setName(modifierName);
    mod.setObjectId(modifierId);
    latestVersion.setModifier(mod);
    latestVersion.getObject().setLastModificationDate(currentDate);

    return latestVersion;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO update(ItemVersionVO pubItemVO, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    long start = System.currentTimeMillis();
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    PubItemUtil.cleanUpItem(pubItemVO);
    ItemVersionVO latestVersion = itemRepository.findLatestVersion(pubItemVO.getObjectId());
    if (latestVersion == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }

    checkEqualModificationDate(pubItemVO.getModificationDate(), latestVersion.getModificationDate());

    ContextDbVO context = contextRepository.findOne(latestVersion.getObject().getContext().getObjectId());

    checkAa("update", principal, latestVersion, context);



    if (ItemVersionRO.State.RELEASED.equals(latestVersion.getVersionState())) {
      entityManager.detach(latestVersion);
      // Reset latestRelase reference because it is the same object as latest version
      ItemVersionRO latestReleaseDbRO = new ItemVersionRO();
      latestReleaseDbRO.setObjectId(latestVersion.getObject().getLatestRelease().getObjectId());
      latestReleaseDbRO.setVersionNumber(latestVersion.getObject().getLatestRelease().getVersionNumber());
      latestVersion.getObject().setLatestRelease(latestReleaseDbRO);

      // if current user is owner, set to status pending. Else, set to status
      // submitted

      if (GrantUtil.hasRole(principal.getUserAccount(), PredefinedRoles.MODERATOR, context.getObjectId())) {
        latestVersion.setVersionState(ItemVersionRO.State.SUBMITTED);
      } else {
        latestVersion.setVersionState(ItemVersionRO.State.PENDING);
      }

      latestVersion.setVersionNumber(latestVersion.getVersionNumber() + 1);
      latestVersion.getObject().setLatestVersion(latestVersion);
    }

    updatePubItemWithTechnicalMd(latestVersion, principal.getUserAccount().getName(), principal.getUserAccount().getObjectId());
    latestVersion.setMetadata(pubItemVO.getMetadata());

    PubItemUtil.setOrganizationIdPathInItem(latestVersion, organizationService);



    latestVersion.getObject().setLocalTags(pubItemVO.getObject().getLocalTags());

    //Set Files and keep old file list
    List<FileDbVO> oldFiles = latestVersion.getFiles();
    latestVersion.setFiles(pubItemVO.getFiles());

    validate(latestVersion);

    //Reset files to old files and handle them
    latestVersion.setFiles(oldFiles);
    latestVersion.setFiles(handleFiles(pubItemVO, latestVersion, principal, new VersionableId(latestVersion.getObjectIdAndVersion())));

    try {
      latestVersion = itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }
    createAuditEntry(latestVersion, EventType.UPDATE);
    reindex(latestVersion);
    sendEventTopic(latestVersion, "update");
    logger.info(
        "PubItem " + latestVersion.getObjectIdAndVersion() + " successfully updated in " + (System.currentTimeMillis() - start) + " ms");
    return latestVersion;
  }

  private List<FileDbVO> handleFiles(ItemVersionVO newPubItemVO, ItemVersionVO currentPubItemVO, Principal principal, VersionableId itemId)
      throws IngeApplicationException, IngeTechnicalException {

    List<FileDbVO> updatedFileList = new ArrayList<>();

    Map<String, FileDbVO> currentFiles = new HashMap<>();
    if (currentPubItemVO != null) {
      for (FileDbVO file : currentPubItemVO.getFiles()) {
        currentFiles.put(file.getObjectId(), file);
      }
    }

    Date currentDate = new Date();
    for (FileDbVO fileVo : newPubItemVO.getFiles()) {

      FileDbVO currentFileDbVO;

      if (fileVo.getObjectId() != null) {

        //Check if this file exists for the given item id
        String errorMessage =
            "File with id [" + fileVo.getObjectId() + "] does not exist for this item. Please remove identifier to create as new file";

        List<String> items = itemRepository.findItemsForFile(fileVo.getObjectId());
        if (items == null || items.isEmpty() || !items.contains(itemId.getObjectId())) {
          throw new IngeApplicationException(errorMessage);
        }
        // Already existing file, just update some fields
        //currentFileDbVO = currentFiles.remove(fileVo.getObjectId());
        currentFileDbVO = fileRepository.findOne(fileVo.getObjectId());

      } else {

        //New file or locator
        currentFileDbVO = new FileDbVO();

        if (fileVo.getContent() == null || fileVo.getContent().trim().isEmpty()) {
          throw new IngeApplicationException(
              "A file content has to be provided containing the identifier of the staged file or the url to an external reference.");
        }

        if (fileVo.getStorage() == null) {
          throw new IngeApplicationException(
              "A file storage type has to be provided. Use 'INTERNAL_MANAGED' for uploading from staging area or an URL, use 'EXTERNAL_URL' for an external reference without uploading");
        }

        currentFileDbVO.setObjectId(idProviderService.getNewId(ID_PREFIX.FILES));

        //New real file
        if (Storage.INTERNAL_MANAGED.equals(fileVo.getStorage())) {



          fileService.createFileFromStagedFile(fileVo, principal);
          currentFileDbVO.setLocalFileIdentifier(fileVo.getLocalFileIdentifier());
          // TODO Set content to a REST path

          fileVo.setContent("/rest/items/" + itemId.toString() + "/component/" + currentFileDbVO.getObjectId() + "/content");

          currentFileDbVO.setChecksum(fileVo.getChecksum());
          currentFileDbVO.setChecksumAlgorithm(ChecksumAlgorithm.valueOf(fileVo.getChecksumAlgorithm().name()));
          currentFileDbVO.setSize(fileVo.getSize());
          currentFileDbVO.setMimeType(fileVo.getMimeType());
          currentFileDbVO.setName(fileVo.getName());

        }



        currentFileDbVO.setStorage(FileDbVO.Storage.valueOf(fileVo.getStorage().name()));

        // oldFileVo.setChecksumAlgorithm(FileVO.ChecksumAlgorithm.valueOf(newFileVo
        // .getChecksumAlgorithm().name()));

        //Content still links to older version which is ok, because the old version might already been
        currentFileDbVO.setContent(fileVo.getContent());
        currentFileDbVO.setCreationDate(currentDate);
        AccountUserDbRO creator = new AccountUserDbRO();
        creator.setObjectId(principal.getUserAccount().getObjectId());
        //Remove name due to DSGVO
        //creator.setName(principal.getUserAccount().getName());
        currentFileDbVO.setCreator(creator);

        // TODO Pid ?
        currentFileDbVO.setPid(fileVo.getPid());


      }

      currentFileDbVO.setLastModificationDate(currentDate);
      currentFileDbVO.setMetadata(fileVo.getMetadata());

      currentFileDbVO.setVisibility(Visibility.valueOf(fileVo.getVisibility().name()));
      currentFileDbVO.setAllowedAudienceIds(fileVo.getAllowedAudienceIds());
      updatedFileList.add(currentFileDbVO);
    }

    // TODO
    // Delete files which are left in currentFiles Map if they are not part of an released item

    return updatedFileList;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void delete(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = aaService.checkLoginRequired(authenticationToken);

    ItemVersionVO latestPubItemDbVersion = itemRepository.findLatestVersion(id);
    if (latestPubItemDbVersion == null) {
      throw new IngeApplicationException("Item " + id + " not found");
    }


    ContextDbVO context = contextRepository.findOne(latestPubItemDbVersion.getObject().getContext().getObjectId());
    checkAa("delete", principal, latestPubItemDbVersion, context);

    List<FileDbVO> files = new ArrayList<>();

    //Delete all files
    for (int i = 1; i <= latestPubItemDbVersion.getVersionNumber(); i++) {
      ItemVersionVO item = itemRepository.findOne(new VersionableId(latestPubItemDbVersion.getObjectId(), i));
      for (FileDbVO file : item.getFiles()) {
        fileRepository.delete(file);
      }
      item.setFiles(null);
    }



    // Delete reference to Object in latestRelease and latestVersion. Otherwise the object is not
    // deleted by EntityManager.
    // See http://www.baeldung.com/delete-with-hibernate or JPA spec section 3.2.2
    ItemRootVO pubItemObjectToDelete = itemObjectRepository.findOne(latestPubItemDbVersion.getObject().getObjectId());
    ((ItemVersionVO) pubItemObjectToDelete.getLatestVersion()).setObject(null);
    if (pubItemObjectToDelete.getLatestRelease() != null) {
      ((ItemVersionVO) pubItemObjectToDelete.getLatestRelease()).setObject(null);
    }


    itemObjectRepository.delete(pubItemObjectToDelete);

    SearchRetrieveResponseVO<ItemVersionVO> resp = getAllVersions(id);
    for (SearchRetrieveRecordVO<ItemVersionVO> rec : resp.getRecords()) {
      pubItemDao.deleteImmediatly(rec.getPersistenceId());
      pubItemDao.deleteByQuery(QueryBuilders.termQuery(INDEX_FULLTEXT_ITEM_ID, rec.getPersistenceId()));
    }
    sendEventTopic(latestPubItemDbVersion, "delete");

    logger.info("PubItem " + id + " successfully deleted");

  }

  @Override
  @Transactional(readOnly = true)
  public ItemVersionVO get(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    long start = System.currentTimeMillis();

    String[] splittedId = id.split("_");
    String objectId = splittedId[0] + "_" + splittedId[1];
    String version = null;
    if (splittedId.length == 3) {
      version = splittedId[2];
    }

    ItemVersionVO requestedItem = null;

    Principal principal = null;

    if (authenticationToken != null) {
      principal = aaService.checkLoginRequired(authenticationToken);
    }


    if (version == null) {
      if (authenticationToken == null) {
        // Return latest release
        requestedItem = itemRepository.findLatestRelease(objectId);
      } else {
        // Check if user is allowed to see latest version
        requestedItem = itemRepository.findLatestVersion(objectId);
        if (requestedItem != null) {

          ContextDbVO context = contextRepository.findOne(requestedItem.getObject().getContext().getObjectId());
          try {
            checkAa("get", principal, requestedItem, context);
          } catch (AuthenticationException | AuthorizationException e) {
            requestedItem = itemRepository.findLatestRelease(objectId);
          }
        }
      }
    } else // version != null
    {
      requestedItem = itemRepository.findOne(new VersionableId(objectId, Integer.parseInt(version)));
      if (requestedItem != null) {
        ContextDbVO context = contextRepository.findOne(requestedItem.getObject().getContext().getObjectId());
        checkAa("get", principal, requestedItem, context);
      }
    }

    if (requestedItem == null) {
      logger.info("Item " + id + " not found");
      return null;
    }

    long time = System.currentTimeMillis() - start;
    logger.info("PubItem " + id + " successfully retrieved in " + time + " ms");

    return requestedItem;
  }



  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO submitPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.SUBMITTED, message, "submit", authenticationToken,
        EventType.SUBMIT);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO revisePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.IN_REVISION, message, "revise", authenticationToken,
        EventType.REVISE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO releasePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.RELEASED, message, "release", authenticationToken,
        EventType.RELEASE);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public ItemVersionVO withdrawPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(pubItemId, modificationDate, ItemVersionRO.State.WITHDRAWN, message, "withdraw", authenticationToken,
        EventType.WITHDRAW);
  }

  private ItemVersionVO changeState(String id, Date modificationDate, ItemVersionRO.State state, String message, String aaMethod,
      String authenticationToken, EventType auditEventType)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);

    ItemVersionVO latestVersion = itemRepository.findLatestVersion(id);

    if (latestVersion == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }


    checkEqualModificationDate(modificationDate, latestVersion.getModificationDate());

    ContextDbVO context = contextRepository.findOne(latestVersion.getObject().getContext().getObjectId());

    checkAa(aaMethod, principal, latestVersion, context);

    if (ItemVersionRO.State.SUBMITTED.equals(state) && !ItemVersionRO.State.RELEASED.equals(latestVersion.getObject().getPublicState())) {
      latestVersion.getObject().setPublicState(ItemVersionRO.State.SUBMITTED);
    }

    if (ItemVersionRO.State.RELEASED.equals(state)) {
      latestVersion.getObject().setPublicState(ItemVersionRO.State.RELEASED);
      latestVersion.getObject().setLatestRelease(latestVersion);
      ItemRootVO pubItemObject = latestVersion.getObject();
      try {
        if (pubItemObject.getObjectPid() == null) {
          URI url = new URI(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH)
              + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1", latestVersion.getObjectId()));
          pubItemObject.setObjectPid("hdl:" + pidService.createPid(url).getIdentifier());
        }
        URI url = new URI(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL)
            + PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH) + PropertyReader
                .getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN).replaceAll("\\$1", latestVersion.getObjectIdAndVersion()));
        latestVersion.setVersionPid("hdl:" + pidService.createPid(url).getIdentifier());
      } catch (URISyntaxException | TechnicalException e) {
        logger.error("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
        throw new IngeTechnicalException("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
      }


      for (FileDbVO fileDbVO : latestVersion.getFiles()) {
        try {
          if ((FileDbVO.Storage.INTERNAL_MANAGED).equals(fileDbVO.getStorage()) && fileDbVO.getPid() == null) {
            URI uri = new URI(REST_SERVICE_URL + REST_COMPONENT_PATH + "/" + fileDbVO.getObjectId());
            fileDbVO.setPid("hdl:" + pidService.createPid(uri).getIdentifier());
          }
        } catch (URISyntaxException | TechnicalException e) {
          logger.error("Error creating PID for file [" + fileDbVO.getObjectId() + "] part of the item ["
              + latestVersion.getObjectIdAndVersion() + "]", e);
          throw new IngeTechnicalException("Error creating PID for item [" + latestVersion.getObjectIdAndVersion() + "]", e);
        }
      }

    }

    if (ItemVersionRO.State.WITHDRAWN.equals(state)) {
      // change public state to withdrawn, leave version state as is
      latestVersion.getObject().setPublicState(ItemVersionRO.State.WITHDRAWN);
      //latestVersion.getObject().setWithdrawComment(message);
    } else {
      latestVersion.setVersionState(state);
    }

    updatePubItemWithTechnicalMd(latestVersion, principal.getUserAccount().getName(), principal.getUserAccount().getObjectId());

    latestVersion.setMessage(message);

    validate(latestVersion);

    try {
      latestVersion = itemRepository.saveAndFlush(latestVersion);
    } catch (DataAccessException e) {
      GenericServiceImpl.handleDBException(e);
    }


    createAuditEntry(latestVersion, auditEventType);



    reindex(latestVersion);
    sendEventTopic(latestVersion, aaMethod);
    return latestVersion;
  }



  private void validate(ItemVersionVO pubItem)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    ValidationPoint vp = ValidationPoint.STANDARD;

    if (pubItem.getObject().getPublicState() != null && State.PENDING.equals(pubItem.getObject().getPublicState())) {
      vp = ValidationPoint.SAVE;
    }

    validate(pubItem, vp);
  }

  private void validate(ItemVersionVO pubItem, ValidationPoint vp)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    try {
      this.itemValidatingService.validate(pubItem, vp);
    } catch (ValidationException e) {
      throw new IngeApplicationException("Invalid metadata", e);
    } catch (Exception e) {
      throw new IngeTechnicalException(e.getMessage(), e);
    }
  }

  private SearchRetrieveResponseVO<ItemVersionVO> getAllVersions(String objectId) throws IngeTechnicalException {
    QueryBuilder latestReleaseQuery = QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, objectId);
    SearchRetrieveResponseVO<ItemVersionVO> resp = executeSearchSortByVersion(latestReleaseQuery, 10000, 0);

    return resp;
  }

  private SearchRetrieveResponseVO<ItemVersionVO> executeSearchSortByVersion(QueryBuilder query, int limit, int offset)
      throws IngeTechnicalException {

    SearchSortCriteria sortByVersion = new SearchSortCriteria(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, SortOrder.DESC);
    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(query, limit, offset, sortByVersion);
    return pubItemDao.search(srr);
  }

  @Override
  @Transactional(readOnly = true)
  public void reindexAll(String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Query<String> query = (Query<String>) entityManager.createQuery("SELECT itemObject.objectId FROM ItemRootVO itemObject");
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
        String id = (String) results.get(0);
        queueJmsTemplate.convertAndSend("reindex-ItemVersionVO", id);

        // Clear entity manager after every 1000 items, otherwise OutOfMemory can occur
        if (count % 1000 == 0) {
          logger.info("Clearing entity manager");
          entityManager.flush();
          entityManager.clear();
        }

      } catch (Exception e) {
        logger.error("Error while reindexing ", e);
      }

    }
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-ItemVersionVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);
  }


  private void reindex(ItemRootVO object, boolean immediate) throws IngeTechnicalException {

    ItemVersionVO latestVersion = (ItemVersionVO) object.getLatestVersion();
    latestVersion = latestVersion;
    // First try to delete the old version from index
    String oldVersion = new VersionableId(latestVersion.getObjectId(), latestVersion.getVersionNumber() - 1).toString();
    pubItemDao.delete(oldVersion);

    //Delete all fulltexts of old version from index
    pubItemDao.deleteByQuery(QueryBuilders.termQuery(INDEX_FULLTEXT_ITEM_ID, oldVersion));

    logger.info("Reindexing item latest version " + latestVersion.getObjectIdAndVersion());

    if (immediate) {
      pubItemDao.createImmediately(latestVersion.getObjectId() + "_" + latestVersion.getVersionNumber(), latestVersion);
    } else {
      pubItemDao.create(latestVersion.getObjectId() + "_" + latestVersion.getVersionNumber(), latestVersion);
    }

    queueJmsTemplate.convertAndSend("reindex-fulltext", latestVersion);

    if (object.getLatestRelease() != null && object.getLatestRelease().getVersionNumber() != object.getLatestVersion().getVersionNumber()) {
      ItemVersionVO latestRelease = (ItemVersionVO) object.getLatestRelease();
      latestRelease = latestRelease;
      logger.info("Reindexing item latest release " + latestRelease.getObjectIdAndVersion());
      if (immediate) {
        pubItemDao.createImmediately(latestRelease.getObjectId() + "_" + latestRelease.getVersionNumber(), latestRelease);
      } else {
        pubItemDao.create(latestRelease.getObjectId() + "_" + latestRelease.getVersionNumber(), latestRelease);
      }
      queueJmsTemplate.convertAndSend("reindex-fulltext", latestRelease);
    }

  }

  private void reindex(String objectId, boolean immediate) throws IngeTechnicalException {
    de.mpg.mpdl.inge.model.db.valueobjects.ItemRootVO object = itemObjectRepository.findOne(objectId);
    reindex(object, immediate);

  }

  private void reindex(ItemVersionVO item) throws IngeTechnicalException {
    reindex(item.getObject(), true);
  }

  @Override
  public void reindex(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    // TODO AA
    reindex(id, false);
  }


  @Override
  @Transactional(readOnly = true)
  public List<AuditDbVO> getVersionHistory(String pubItemId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException {

    List<AuditDbVO> list = auditRepository.findDistinctAuditByPubItemObjectIdOrderByModificationDateDesc(pubItemId);

    return list;
  }



  private void rollbackSavedFiles(ItemVersionVO pubItemVO) throws IngeTechnicalException {
    for (FileDbVO fileVO : pubItemVO.getFiles()) {
      if ((FileDbVO.Storage.INTERNAL_MANAGED).equals(fileVO.getStorage())) {
        String relativePath = fileVO.getContent();
        try {
          fileService.deleteFile(relativePath);
        } catch (IngeTechnicalException e) {
          logger.error("Could not upload staged file [" + relativePath + "]", e);
          throw new IngeTechnicalException("Could not upload staged file [" + relativePath + "]", e);
        }
      }
    }
  }

  @Override
  protected GenericDaoEs<ItemVersionVO> getElasticDao() {
    return pubItemDao;
  }


  private void sendEventTopic(ItemVersionVO item, String method) {
    topicJmsTemplate.convertAndSend(item, new MessagePostProcessor() {
      @Override
      public Message postProcessMessage(Message message) throws JMSException {
        message.setStringProperty("method", method);
        return message;
      }
    });
  }


  public boolean checkAccess(AccessType at, Principal principal, ItemVersionVO item)
      throws IngeApplicationException, IngeTechnicalException {
    try {
      checkAa(at.getMethodName(), principal, item, ((ContextDbVO) item.getObject().getContext()));
    } catch (AuthenticationException | AuthorizationException e) {
      return false;
    } catch (IngeTechnicalException | IngeApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new IngeTechnicalException("", e);
    }
    return true;
  }


  /*
  public ItemVersionVO enrichItem(ItemVersionVO item) {
  
    if (item != null) {
      try {
        entityManager.detach(item);
      } catch (Exception e) {
  
      }
      if (item.getFiles() != null) {
        for (FileDbVO file : item.getFiles()) {
          if (Storage.INTERNAL_MANAGED.equals(file.getStorage())) {
            file.setContent("/rest/items/" + item.getObjectIdAndVersion() + "/component/" + file.getObjectId() + "/content");
          }
        }
      }
    }
    return item;
  }
  */


}
