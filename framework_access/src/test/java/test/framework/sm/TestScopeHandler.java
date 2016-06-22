package test.framework.sm;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.framework.ServiceLocator;

/**
 * Test cases for the basic service ScopeHandler.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class TestScopeHandler {
  private Logger logger = Logger.getLogger(getClass());

  /**
   * Test method for {@link de.fiz.escidoc.sm.ScopeHandlerLocal#retrieveScopes(java.lang.String)}.
   */
  @Test
  public void retrieveScopes() throws Exception {
    long zeit = -System.currentTimeMillis();
    HashMap<String, String[]> emptymap = new HashMap<String, String[]>();
    String scopes = ServiceLocator.getScopeHandler().retrieveScopes(emptymap);
    zeit += System.currentTimeMillis();
    logger.info("retrieveScopes()->" + zeit + "ms");
    assertNotNull(scopes);
    logger.debug("Scopes()=" + scopes);
  }

  /**
   * Test method for {@link de.fiz.escidoc.sm.ScopeHandlerLocal#retrieve(java.lang.String)}.
   */
  @Test
  public void retrieveAdminScopeScope() throws Exception {
    String id = "2";
    long zeit = -System.currentTimeMillis();
    String scope = ServiceLocator.getScopeHandler().retrieve(id);
    zeit += System.currentTimeMillis();
    logger.info("retrieveScope()->" + zeit + "ms");
    assertNotNull(scope);
    logger.debug("Scope()=" + scope);
  }
}
