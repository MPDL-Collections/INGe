package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * Interface defining the batch functions for PubItems
 * 
 * @author walter
 *
 */
public interface PubItemBatchService {

  /**
   * add keywords for multiple Items within a Map <pubItemId, modificationDate> and a
   * BatchProcessLogDbVO
   * 
   * @param pubItemsMap
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationExcepti on
   */
  public BatchProcessLogDbVO addKeywords(List<String> pubItemObjectIdList, String keywordsNew, String message, String authenticationToken,
      AccountUserDbVO accountUser);

  /**
   * add local tags for multiple Items within a Map <pubItemId, modificationDate> and return a
   * BatchProcessLogDbVO
   * 
   * @param pubItemsMap
   * @param localTagsToAdd
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public BatchProcessLogDbVO addLocalTags(List<String> pubItemObjectIdList, List<String> localTagsToAdd, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * adding a source id for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO
   * 
   * @param pubItemObjectIdList
   * @param sourceNumber
   * @param sourceIdType
   * @param idNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO addSourceId(List<String> pubItemObjectIdList, String sourceNumber, IdType sourceIdType, String idNew,
      String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * change the context of multiple Items within a list of objectIds from contextOld to contextNew
   * return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param contextOld
   * @param contextNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeContext(List<String> pubItemObjectIdList, String contextOld, String contextNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replacing the content category for the external references of a list of objectIds and return a
   * BatchProcessLogDbVO object
   * 
   * @param pubItemsMap
   * @param contentCategoryOld
   * @param contentCategoryNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeExternalRefereneceContentCategory(List<String> pubItemObjectIdList, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replacing the audience for the files of a list of objectIds and return a BatchProcessLogDbVO
   * object
   * 
   * @param pubItemObjectIdList
   * @param audienceOld
   * @param audienceNew
   * @param message
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeFileAudience(List<String> pubItemObjectIdList, String audienceNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replacing the content category for the files of a list of objectIds and return a
   * BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param contentCategoryOld
   * @param contentCategoryNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeFileContentCategory(List<String> pubItemObjectIdList, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replacing the visibility for the files of a list of objectIds and return a BatchProcessLogDbVO
   * 
   * @param pubItemObjectIdList
   * @param visibilityOld
   * @param visibilityOld2
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return BatchProcessLogDbVO
   */
  public BatchProcessLogDbVO changeFileVisibility(List<String> pubItemObjectIdList, Visibility visibilityOld, Visibility visibilityOld2,
      String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * change genre for multiple items within a list of objectIds and return a BatchProcessLogDbVO
   * object
   * 
   * @param pubItemObjectIdList
   * @param genreOld
   * @param genreNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeGenre(List<String> pubItemObjectIdList, Genre genreOld, Genre genreNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replace one specific keyword for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param keywordsOld
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeKeywords(List<String> pubItemObjectIdList, String keywordsOld, String keywordsNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * change review method for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param reviewMethodOld
   * @param reviewMethodNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeReviewMethod(List<String> pubItemObjectIdList, String reviewMethodOld, String reviewMethodNew,
      String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * change source genre for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO
   * 
   * @param pubItemObjectIdList
   * @param genreOld
   * @param genreNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeSourceGenre(List<String> pubItemObjectIdList, SourceVO.Genre genreOld, SourceVO.Genre genreNew,
      String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replace source id for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO
   * 
   * @param pubItemObjectIdList
   * @param sourceNumber
   * @param sourceIdType
   * @param idOld
   * @param idNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeSourceIdReplace(List<String> pubItemObjectIdList, String sourceNumber, IdType sourceIdType, String idOld,
      String idNew, String message, String authenticationToken, AccountUserDbVO accountUser);

  /**
   * change source issue for multiple items within a list of objectIds and return a
   * BatchProcessLogDbVO
   * 
   * @param pubItemObjectIdList
   * @param sourceNumber
   * @param issue
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO changeSourceIssue(List<String> pubItemObjectIdList, String sourceNumber, String issue, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * delete multiple items within a list of objectIds and return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO deletePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser);

  /**
   * look up a BatchProcessLog. Returns null or a save BatchProcessLog
   * 
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO getBatchProcessLogForCurrentUser(AccountUserDbVO accountUser);

  /**
   * Release multiple Items within a list of objectIds and return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO releasePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser);

  /**
   * replacing all keywords for a Map <pubItemId, modificationDate> of items and return a Map with
   * <itemId, exception>
   * 
   * @param pubItemObjectIdList
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO replaceAllKeywords(List<String> pubItemObjectIdList, String keywordsNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * replacing local tags for a list of objectIds and return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param localTagsOld
   * @param localTagsNew
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO replaceLocalTags(List<String> pubItemObjectIdList, String localTagsOld, String localTagsNew, String message,
      String authenticationToken, AccountUserDbVO accountUser);

  /**
   * revise multiple Items within a list of objectIds and return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO revisePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser);

  /**
   * Submit multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId,
   * exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public BatchProcessLogDbVO submitPubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser);

  /**
   * withdraw multiple Items within a list of objectIds and return a BatchProcessLogDbVO object
   * 
   * @param pubItemObjectIdList
   * @param message
   * @param authenticationToken
   * @param accountUser
   * @return
   */
  public BatchProcessLogDbVO withdrawPubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser);



  // public boolean checkAccess(AccessType at, Principal userAccount, ItemVersionVO item)
  // throws IngeApplicationException, IngeTechnicalException;

  // public void reindex(String id, boolean includeFulltext, String authenticationToken)
  // throws IngeTechnicalException, AuthenticationException, AuthorizationException,
  // IngeApplicationException;
}
