package de.mpg.mpdl.inge.service.pubman.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.mpg.mpdl.inge.db.repository.ContextRepository;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl;
import de.mpg.mpdl.inge.db.repository.IdentifierProviderServiceImpl.ID_PREFIX;
import de.mpg.mpdl.inge.db.repository.OrganizationRepository;
import de.mpg.mpdl.inge.db.repository.UserAccountRepository;
import de.mpg.mpdl.inge.db.repository.UserLoginRepository;
import de.mpg.mpdl.inge.es.dao.GenericDaoEs;
import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ReindexListener;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
public class UserAccountServiceImpl extends GenericServiceImpl<AccountUserDbVO, String> implements UserAccountService, ReindexListener {

  private static Logger logger = LogManager.getLogger(UserAccountServiceImpl.class);

  private final static int TOKEN_MAX_AGE_HOURS = 24;

  @Autowired
  private AuthorizationService aaService;

  @Autowired
  private IdentifierProviderServiceImpl idProviderService;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @Autowired
  private UserLoginRepository userLoginRepository;

  @Autowired
  private UserAccountDaoEs userAccountDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ContextRepository contextRepository;

  @Autowired
  private OrganizationRepository organizationRepository;



  private Algorithm jwtAlgorithmKey;

  private String jwtIssuer;

  private JWTVerifier jwtVerifier;

  // private final static String PASSWORD_REGEX =
  // "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
  private final static String PASSWORD_REGEX = "^(?=.*[A-Za-z0-9])(?=\\S+$).{6,}$";

  /**
   * Loginname must consist of at least 4 characters of a-z, A-Z, 0-9, @, _, -, .
   */
  private final static String LOGINNAME_REGEX = "^[A-Za-z0-9@_\\-\\.]{4,}$";


  public UserAccountServiceImpl() throws Exception {
    String key = PropertyReader.getProperty("inge.jwt.shared-secret");
    if (key == null || key.trim().isEmpty()) {
      logger.warn("No 'inge.jwt.shared-secret' is set. Generating a random secret, which might not be secure.");
      key = UUID.randomUUID().toString();
    }

    jwtAlgorithmKey = Algorithm.HMAC512(key);

    jwtIssuer = PropertyReader.getProperty("pubman.instance.url");

    jwtVerifier = JWT.require(jwtAlgorithmKey).withIssuer(jwtIssuer).build();

  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO create(AccountUserDbVO givenUser, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUser = super.create(givenUser, authenticationToken);
    validatePassword(givenUser.getPassword());
    try {
      userLoginRepository.insertLogin(accountUser.getLoginname(), passwordEncoder.encode(givenUser.getPassword()));
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    if (givenUser.getGrantList() != null && !givenUser.getGrantList().isEmpty()) {
      accountUser = this.addGrants(accountUser.getObjectId(), accountUser.getLastModificationDate(),
          givenUser.getGrantList().toArray(new GrantVO[] {}), authenticationToken);
    }


    return accountUser;
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void delete(String userId, String authenticationToken)
      throws IngeTechnicalException, IngeApplicationException, AuthenticationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = userAccountRepository.findOne(userId);

    try {
      userLoginRepository.removeLogin(accountUserDbVO.getLoginname());
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    super.delete(userId, authenticationToken);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public AccountUserDbVO changePassword(String userId, Date modificationDate, String newPassword, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Principal principal = aaService.checkLoginRequired(authenticationToken);
    validatePassword(newPassword);
    AccountUserDbVO userDbToUpdated = userAccountRepository.findOne(userId);

    if (userDbToUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }


    checkEqualModificationDate(modificationDate, getModificationDate(userDbToUpdated));

    checkAa("changePassword", principal, userDbToUpdated);
    userLoginRepository.updateLogin(userDbToUpdated.getLoginname(), passwordEncoder.encode(newPassword));

    updateWithTechnicalMetadata(userDbToUpdated, principal.getUserAccount(), false);

    try {
      userDbToUpdated = getDbRepository().saveAndFlush(userDbToUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    getElasticDao().createImmediately(userDbToUpdated.getObjectId(), userDbToUpdated);
    return userDbToUpdated;

  }


  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO addGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }


    checkEqualModificationDate(modificationDate, getModificationDate(objectToBeUpdated));

    for (GrantVO grantToBeAdded : grants) {

      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(grantToBeAdded.getRole(), existingGrant.getRole())
            && Objects.equals(grantToBeAdded.getObjectRef(), existingGrant.getObjectRef())) {
          throw new IngeApplicationException("Grant with given value [role=" + grantToBeAdded.getRole() + ", objectRef= "
              + grantToBeAdded.getObjectRef() + "] already exists in user account " + objectToBeUpdated.getObjectId());
        }
      }

      grantToBeAdded.setGrantedTo(null);
      grantToBeAdded.setGrantType(null);
      grantToBeAdded.setReference(null);
      grantToBeAdded.setLastModificationDate(null);

      Object referencedObject = null;

      if (grantToBeAdded.getObjectRef() != null) {
        if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.CONTEXT.getPrefix())) {
          ContextDbVO referencedContext = contextRepository.findOne(grantToBeAdded.getObjectRef());
          if (referencedContext != null) {
            referencedObject = EntityTransformer.transformToOld(referencedContext);
          }
        } else if (grantToBeAdded.getObjectRef().startsWith(ID_PREFIX.OU.getPrefix())) {
          AffiliationDbVO referencedOu = organizationRepository.findOne(grantToBeAdded.getObjectRef());
          if (referencedOu != null) {
            referencedObject = EntityTransformer.transformToOld(referencedOu);
          }
        }

        if (referencedObject == null) {
          throw new IngeApplicationException("Unknown identifier reference: " + grantToBeAdded.getObjectRef());
        }
      }



      checkAa("addGrants", principal, objectToBeUpdated, grantToBeAdded, referencedObject);


    }
    objectToBeUpdated.getGrantList().addAll(Arrays.asList(grants));
    updateWithTechnicalMetadata(objectToBeUpdated, principal.getUserAccount(), false);


    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    getElasticDao().createImmediately(objectToBeUpdated.getObjectId(), objectToBeUpdated);

    return objectToBeUpdated;

  }

  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO removeGrants(String userId, Date modificationDate, GrantVO[] grants, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO objectToBeUpdated = getDbRepository().findOne(userId);
    if (objectToBeUpdated == null) {
      throw new IngeApplicationException("Object with given id not found.");
    }


    checkEqualModificationDate(modificationDate, getModificationDate(objectToBeUpdated));

    for (GrantVO givenGrant : grants) {
      GrantVO grantToBeRemoved = null;
      for (GrantVO existingGrant : objectToBeUpdated.getGrantList()) {
        if (Objects.equals(givenGrant.getRole(), existingGrant.getRole())
            && Objects.equals(givenGrant.getObjectRef(), existingGrant.getObjectRef())) {
          grantToBeRemoved = existingGrant;
        }
      }

      if (grantToBeRemoved == null) {
        throw new IngeApplicationException("Grant with given values [role=" + givenGrant.getRole() + ", objectRef= "
            + givenGrant.getObjectRef() + "] does not exist in user account " + objectToBeUpdated.getObjectId());
      }


      checkAa("removeGrants", principal, objectToBeUpdated, givenGrant);
      objectToBeUpdated.getGrantList().remove(grantToBeRemoved);
    }
    updateWithTechnicalMetadata(objectToBeUpdated, principal.getUserAccount(), false);

    try {
      objectToBeUpdated = getDbRepository().saveAndFlush(objectToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }
    getElasticDao().createImmediately(objectToBeUpdated.getObjectId(), objectToBeUpdated);

    return objectToBeUpdated;

  }



  @Override
  public void logout(String authenticationToken, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    // Delete cookie
    if (request != null && request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("inge_auth_token".equals(cookie.getName())) {
          cookie.setValue("");
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
        }
      }
    }

  }


  @Override
  public Principal login(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, request, response);
  }


  @Override
  public Principal login(String username, String password) throws IngeTechnicalException, AuthenticationException {
    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      throw new AuthenticationException("Could not login, Please provide correct username and password!");
    }
    return loginUserOrAnonymous(username, password, null, null);
  }

  public Principal login(HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    return loginUserOrAnonymous(null, null, request, response);
  }



  private Principal loginUserOrAnonymous(String username, String password, HttpServletRequest request, HttpServletResponse response)
      throws IngeTechnicalException, AuthenticationException {

    Principal principal = null;

    if (username != null) {
      // Helper to login as any user if you are sysadmin
      if (username.contains("#")) {
        String[] parts = username.split("#");
        AccountUserDbVO userAccountSysadmin = userAccountRepository.findByLoginname(parts[0]);
        String encodedPassword = userLoginRepository.findPassword(parts[0]);

        if (userAccountSysadmin != null && encodedPassword != null && passwordEncoder.matches(password, encodedPassword)) {
          for (GrantVO grant : userAccountSysadmin.getGrantList()) {
            if (grant.getRole().equals(PredefinedRoles.SYSADMIN.frameworkValue())) {
              AccountUserDbVO userAccountToLogin = userAccountRepository.findByLoginname(parts[1]);
              String token = createToken(userAccountToLogin, request);
              principal = new Principal(userAccountToLogin, token);
            }
          }
        }
        if (principal == null) {
          throw new AuthenticationException("Could not login, Please provide correct username and password!");
        }

      }

      else {
        AccountUserDbVO userAccount = userAccountRepository.findByLoginname(username);
        String encodedPassword = userLoginRepository.findPassword(username);

        if (userAccount != null && encodedPassword != null && passwordEncoder.matches(password, encodedPassword)) {
          String token = createToken(userAccount, request);
          principal = new Principal(userAccount, token);

        } else {
          throw new AuthenticationException("Could not login, Please provide correct username and password!");
        }
      }

      //Set Cookie
      if (principal != null && response != null) {
        Cookie cookie = new Cookie("inge_auth_token", principal.getJwToken());
        cookie.setPath("/");
        cookie.setMaxAge(TOKEN_MAX_AGE_HOURS * 3600);
        response.addCookie(cookie);
      }
    }

    //Login anonymous, ip-based user
    else if (request != null && request.getHeader("X-Forwarded-For") != null) {
      String token = createToken(null, request);
      principal = new Principal(null, token);
    }

    return principal;

  }

  @Transactional(readOnly = true)
  @Override
  public AccountUserDbVO get(String id, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String userId = id;
    if (!id.startsWith(ID_PREFIX.USER.getPrefix())) {
      AccountUserDbVO user = userAccountRepository.findByLoginname(id);
      if (user != null) {
        userId = user.getObjectId();
      }
    }
    return super.get(userId, authenticationToken);

  }


  @Override
  public AccountUserDbVO get(String authenticationToken) throws IngeTechnicalException, AuthenticationException {
    DecodedJWT jwt = verifyToken(authenticationToken);
    String userId = jwt.getSubject();
    return userAccountRepository.findByLoginname(userId);
  }


  public DecodedJWT verifyToken(String authenticationToken) throws AuthenticationException {
    try {
      DecodedJWT jwt = jwtVerifier.verify(authenticationToken);
      return jwt;
    } catch (JWTVerificationException e) {
      throw new AuthenticationException("Could not verify token: " + e.getMessage(), e);
    }

  }

  private String createToken(AccountUserDbVO user, HttpServletRequest request) throws IngeTechnicalException {
    try {
      Instant now = Instant.now();
      Date issueDate = Date.from(now);
      Date expirationDate = Date.from(now.plus(TOKEN_MAX_AGE_HOURS, ChronoUnit.HOURS));
      logger.info("Creating token with issue date: " + issueDate + " and expiration date " + expirationDate);

      Builder jwtBuilder = JWT.create().withIssuedAt(issueDate).withIssuer(jwtIssuer).withExpiresAt(expirationDate);

      if (user != null) {
        jwtBuilder.withClaim("id", user.getObjectId()).withSubject(user.getLoginname());
      }

      //Write ip adress as header in token
      if (request != null && request.getHeader("X-Forwarded-For") != null) {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", request.getHeader("X-Forwarded-For"));
        jwtBuilder.withHeader(headerMap);
      }


      return jwtBuilder.sign(jwtAlgorithmKey);
    } catch (Exception e) {
      throw new IngeTechnicalException("Could not generate token " + e.getMessage(), e);
    }

  }



  @Override
  protected AccountUserDbVO createEmptyDbObject() {
    return new AccountUserDbVO();
  }

  @Override
  protected List<String> updateObjectWithValues(AccountUserDbVO givenUser, AccountUserDbVO tobeUpdatedUser, AccountUserDbVO callingUser,
      boolean create) throws IngeApplicationException {


    if (givenUser.getName() == null || givenUser.getName().trim().isEmpty() || givenUser.getLoginname() == null
        || givenUser.getLoginname().trim().isEmpty()) {
      throw new IngeApplicationException("A name and user id is required");
    }

    validateLoginname(givenUser.getLoginname());

    if (create) {
      tobeUpdatedUser.setActive(true);
    }


    tobeUpdatedUser.setAffiliation(givenUser.getAffiliation());



    tobeUpdatedUser.setEmail(givenUser.getEmail());
    tobeUpdatedUser.setLoginname(givenUser.getLoginname());
    tobeUpdatedUser.setName(givenUser.getName());
    // tobeUpdatedUser.setPassword(givenUser.getPassword());


    // tobeUpdatedUser.setGrantList(givenUser.getGrants());



    if (create) {
      tobeUpdatedUser.setObjectId(idProviderService.getNewId(ID_PREFIX.USER));
    }
    return null;

  }



  @Override
  protected JpaRepository<AccountUserDbVO, String> getDbRepository() {
    return userAccountRepository;
  }

  @Override
  protected GenericDaoEs<AccountUserDbVO> getElasticDao() {
    return userAccountDao;
  }

  @Override
  protected String getObjectId(AccountUserDbVO object) {
    return object.getObjectId();
  }

  private void validatePassword(String password) throws IngeApplicationException {
    if (password == null || password.trim().isEmpty()) {
      throw new IngeApplicationException("A password has to be provided");
    } else if (!password.matches(PASSWORD_REGEX)) {
      throw new IngeApplicationException("Password  must consist of at least 6 characters, no whitespaces");
    }

  }

  private void validateLoginname(String loginname) throws IngeApplicationException {
    if (loginname == null || loginname.trim().isEmpty()) {
      throw new IngeApplicationException("A loginname (userId) has to be provided");
    } else if (!loginname.matches(LOGINNAME_REGEX)) {
      throw new IngeApplicationException(
          "Invalid loginname (userId). Loginname  must consist of an email adress or at least 4 characters, no whitespaces, no special characters");
    }

  }

  @Override
  protected Date getModificationDate(AccountUserDbVO object) {
    return object.getLastModificationDate();
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO activate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, true);
  }


  @Override
  @Transactional(rollbackFor = Throwable.class)
  public AccountUserDbVO deactivate(String id, Date modificationDate, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    return changeState(id, modificationDate, authenticationToken, false);
  }

  private AccountUserDbVO changeState(String id, Date modificationDate, String authenticationToken, boolean active)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Principal principal = aaService.checkLoginRequired(authenticationToken);
    AccountUserDbVO accountToBeUpdated = userAccountRepository.findOne(id);
    if (accountToBeUpdated == null) {
      throw new IngeTechnicalException("User account with given id " + id + " not found.");
    }

    if (accountToBeUpdated.isActive() == active) {
      throw new IngeApplicationException("Account [" + accountToBeUpdated.getObjectId() + "] is already in state " + active);
    }

    checkEqualModificationDate(modificationDate, getModificationDate(accountToBeUpdated));

    checkAa((active ? "activate" : "deactivate"), principal, accountToBeUpdated);

    accountToBeUpdated.setActive(active);
    updateWithTechnicalMetadata(accountToBeUpdated, principal.getUserAccount(), false);

    try {
      accountToBeUpdated = userAccountRepository.saveAndFlush(accountToBeUpdated);
    } catch (DataAccessException e) {
      handleDBException(e);
    }

    userAccountDao.updateImmediately(accountToBeUpdated.getObjectId(), accountToBeUpdated);
    return accountToBeUpdated;
  }

  @Override
  @JmsListener(containerFactory = "queueContainerFactory", destination = "reindex-AccountUserDbVO")
  public void reindexListener(String id) throws IngeTechnicalException {
    reindex(id, false);

  }



}
