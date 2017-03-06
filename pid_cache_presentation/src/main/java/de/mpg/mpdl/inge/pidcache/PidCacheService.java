package de.mpg.mpdl.inge.pidcache;

import javax.servlet.http.HttpServletResponse;

import de.mpg.mpdl.inge.model.valueobjects.PidServiceResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.pidcache.gwdg.GwdgClient;
import de.mpg.mpdl.inge.pidcache.gwdg.GwdgPidService;
import de.mpg.mpdl.inge.pidcache.process.CacheProcess;
import de.mpg.mpdl.inge.pidcache.tables.Cache;
import de.mpg.mpdl.inge.pidcache.tables.Queue;

/**
 * Implement the PID cache service
 * 
 * @author saquet
 * 
 */
public class PidCacheService {
  private Pid pid = null;
  private Cache cache = null;
  private Queue queue = null;
  private GwdgPidService gwdgPidService = null;
  private int status = HttpServletResponse.SC_OK;
  private String location = "http://hdl.handle.net/XXX_LOCATION_XXX?noredirect";

  /**
   * Default constructor
   * 
   * @throws Exception
   */
  public PidCacheService() throws Exception {
    cache = new Cache();
    queue = new Queue();
    gwdgPidService = new GwdgPidService();
  }

  /**
   * This method does the following: - Take a PID from the cache - Change the URL of the PID - Put
   * the PID in the queue - Delete the PID from the cache - Return the PID
   * 
   * Notes: - The actual editing of the PID in the GWDG service will be proceed from the queue - The
   * cache will be completed by a new PID generated from {@link CacheProcess}
   * 
   * @param url The URL to be registered.
   * 
   * @return The PID.
   **/
  public String create(String url) throws Exception {
    String xmlOutput = null;
    Pid pid = cache.getFirst();
    pid.setUrl(url);
    queue.add(pid);
    cache.remove(pid);
    this.status = HttpServletResponse.SC_CREATED;
    xmlOutput = transformToPidServiceResponse(pid, "create");
    return xmlOutput;
  }

  /**
   * Retrieve a PID from the GWDG PID service: - Check if PID still in queue, if yes, return it -
   * Check if GWDG PID service available, if no throw Exception
   * 
   * @param id
   * @return
   * @throws Exception
   */
  public String retrieve(String id) throws Exception {
    pid = queue.retrieve(id);
    if (pid != null) {
      return transformToPidServiceResponse(pid, "view");
    }
    return gwdgPidService.retrieve(id);
  }

  /**
   * Search a PID: - Search first in {@link Queue} if PID still in it - Check then if GWDG service
   * available - Search with GWDG service.
   * 
   * @param url
   * @return
   */
  public String search(String url) throws Exception {
    pid = queue.search(url);
    if (pid != null) {
      return transformToPidServiceResponse(pid, "search");
    }
    return gwdgPidService.search(url);
  }

  /**
   * Update a PID
   * 
   * @param id
   * @param url
   * @return
   * @throws Exception
   */
  public String update(String id, String url) throws Exception {
    String xmlOutput = null;
    Pid pid = new Pid(id, url);
    queue.add(pid);
    xmlOutput = transformToPidServiceResponse(pid, "modify");
    this.status = HttpServletResponse.SC_OK;
    return xmlOutput;
  }

  /**
   * Should a PID be removable?
   * 
   * @param id
   * @return
   */
  public String delete(String id) {
    return "Delete not possible for a PID";
  }

  private String transformToPidServiceResponse(Pid pid, String action) throws TechnicalException {
    this.location = this.location.replace("XXX_LOCATION_XXX", pid.getIdentifier());
    PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
    pidServiceResponseVO.setAction(action);
    pidServiceResponseVO.setCreator(GwdgClient.GWDG_PIDSERVICE_USER);
    pidServiceResponseVO.setIdentifier(pid.getIdentifier());
    pidServiceResponseVO.setUrl(pid.getUrl());
    pidServiceResponseVO.setUserUid("anonymous");
    pidServiceResponseVO.setInstitute("institute");
    pid.setContact("jon@doe.xx");
    pidServiceResponseVO.setMessage("Web proxy view URL: " + this.location);
    return XmlTransformingService.transformToPidServiceResponse(pidServiceResponseVO);
  }

  public int getCacheSize() throws Exception {
    return this.cache.size();
  }

  public int getQueueSize() throws Exception {
    return this.queue.size();
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
