package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;

import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/items")
public class ItemRestController {

  private static final Logger logger = Logger.getLogger(ItemRestController.class);

  private final String ITEM_ID_PATH = "/{itemId}";
  private final String ITEM_ID_VAR = "itemId";

  @Autowired
  private PubItemService pis;

  @Autowired
  private UtilServiceBean utils;

  @Autowired
  private FileServiceExternal fileService;



  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.item.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.item.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> query(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO item = null;
    if (token != null && !token.isEmpty()) {
      item = pis.get(itemId, token);
    } else {
      item = pis.get(itemId, null);
    }
    return new ResponseEntity<ItemVersionVO>(item, HttpStatus.OK);
  }


  /**
   * Retrieve a file with a given ID
   * 
   * @param componentId
   * @param response
   */
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/content", method = RequestMethod.GET)
  public void getComponentContent(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    try {


      FileVOWrapper fileVOWrapper = fileService.readFile(itemId, componentId, token);
      response.setContentType(fileVOWrapper.getFileVO().getMimeType());
      response.setHeader("Content-disposition", "attachment; filename=" + fileVOWrapper.getFileVO().getName());
      OutputStream output = response.getOutputStream();
      fileVOWrapper.readFile(output);
      output.flush();
      output.close();
    } catch (IOException e) {
      logger.error("could not read file [" + componentId + "]");
      throw new IngeTechnicalException("Error while opening input stream", e);
    }
  }

  /**
   * Retrive the technical Metadata of a file
   * 
   * @param componentId
   * @return
   * @throws IOException
   * @throws SAXException
   * @throws TikaException
   */
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/metadata", method = RequestMethod.GET,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String getTechnicalMetadataByTika(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    return fileService.getFileMetadata(itemId, componentId, token);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/history", method = RequestMethod.GET)
  public ResponseEntity<List<VersionHistoryEntryVO>> getVersionHistory(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    List<VersionHistoryEntryVO> list = null;
    list = pis.getVersionHistory(itemId, token);
    return new ResponseEntity<List<VersionHistoryEntryVO>>(list, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ItemVersionVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO created = null;
    created = pis.create(item, token);
    return new ResponseEntity<ItemVersionVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/release", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> release(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO released = null;
    released = pis.releasePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> revise(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO revised = null;
    revised = pis.revisePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> submit(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO submitted = null;
    submitted = pis.submitPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> withdraw(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO withdrawn = null;
    withdrawn = pis.withdrawPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(withdrawn, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //TODO Write itemId into item
    ItemVersionVO updated = null;
    updated = pis.update(item, token);
    return new ResponseEntity<ItemVersionVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    pis.delete(itemId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
