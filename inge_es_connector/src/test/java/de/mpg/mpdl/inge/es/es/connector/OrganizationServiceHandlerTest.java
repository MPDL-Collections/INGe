package de.mpg.mpdl.inge.es.es.connector;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.es.exception.IngeEsServiceException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrganizationServiceHandlerTest extends TestBase {
  private static final Logger LOG = Logger.getLogger(ContextServiceHandlerTest.class);

  @Autowired
  private OrganizationDaoEs<QueryBuilder> organizationDao;

  private String test_ou_id = "test_ou";

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testCreate() {
    try {
      String ouId = this.organizationDao.create(test_ou_id, test_ou());
      assert ouId.equals(test_ou_id);
    } catch (IngeEsServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testRead() {
    try {
      AffiliationVO affiliationVO = this.organizationDao.get(test_ou_id);
      assert affiliationVO.equals(test_ou());
    } catch (IngeEsServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Test
  public void testUpdate() {
    try {
      AffiliationVO affiliationVO = this.organizationDao.get(test_ou_id);
      affiliationVO.getDefaultMetadata().setCountryCode("DE");
      this.organizationDao.update(test_ou_id, affiliationVO);
      AffiliationVO affiliationVO2 = this.organizationDao.get(test_ou_id);
      assert affiliationVO2.getDefaultMetadata().getCountryCode().equals("DE");
    } catch (IngeEsServiceException e) {
      LOG.error(e);
      System.out.println(e);
    }
  }

  @Ignore
  @Test
  public void testZDelete() {
    String ouId = this.organizationDao.delete(test_ou_id);
    assert ouId.equals(test_ou_id);
  }

}
