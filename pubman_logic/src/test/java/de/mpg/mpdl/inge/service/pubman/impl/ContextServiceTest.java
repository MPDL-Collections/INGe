package de.mpg.mpdl.inge.service.pubman.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogicTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfigPubmanLogicTest.class})
public class ContextServiceTest {

  @Autowired
  ContextService contextService;

  @Autowired
  UserAccountService userAccountService;

  private static final String ADMIN_LOGIN = "admin";
  private static final String ADMIN_PASSWORD = "tseT";

  private static final String USER_OBJECTID_DEPOSITOR = "user_3000056";
  private static final String DEPOSITOR_PASSWORD = "tseT";

  @Test
  public void objects() {
    assertTrue(contextService != null);
  }

  @Test
  public void openAndClose() throws Exception {

    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);

    switch (contextVO.getState()) {
      case OPENED:
        contextVO =
            contextService.close("ctx_persistent3", contextVO.getLastModificationDate(),
                authenticationToken);
        assertTrue(contextVO.getState().equals(ContextVO.State.CLOSED));
        break;
      case CLOSED:
      case CREATED:
        contextVO =
            contextService.open("ctx_persistent3", contextVO.getLastModificationDate(),
                authenticationToken);
        assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));
      default:
        break;
    }
  }

  @Test(expected = AuthorizationException.class)
  public void openWhenAlreadyOpen() throws Exception {
    String authenticationToken = userAccountService.login(ADMIN_LOGIN, ADMIN_PASSWORD);
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO =
        contextService.open("ctx_persistent3", contextVO.getLastModificationDate(),
            authenticationToken);
  }

  @Test(expected = AuthenticationException.class)
  public void openWithoutAuthorization() throws Exception {
    String authenticationToken =
        userAccountService.login(USER_OBJECTID_DEPOSITOR, DEPOSITOR_PASSWORD);
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO =
        contextService.open("ctx_persistent3", contextVO.getLastModificationDate(),
            authenticationToken);
  }

  @Test(expected = AuthenticationException.class)
  public void openWrongAuthentication() throws Exception {
    String authenticationToken =
        userAccountService.login(USER_OBJECTID_DEPOSITOR, "XXXXXXXXXXXXXX");
    assertTrue(authenticationToken != null);

    ContextVO contextVO = contextService.get("ctx_persistent3", authenticationToken);
    assertTrue(contextVO != null);
    assertTrue(contextVO.getState().equals(ContextVO.State.OPENED));

    contextVO =
        contextService.open("ctx_persistent3", contextVO.getLastModificationDate(),
            authenticationToken);
  }

}
