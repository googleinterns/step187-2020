package com.google.backend.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/** Contain tests for methods in {@link LoginServlet} class. */
@RunWith(JUnit4.class)
public class LoginServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  private static final LoginServlet loginServlet = new LoginServlet();
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig());

  private static final String CONTENT_TYPE = "text/html;";
  private static final String TEST_EMAIL = "test@example.com";
  private static final String REDIRECT_URL = "/";
  private static final String LOGIN_STATUS = "logged in";
  private static final String LOGOUT_STATUS = "stranger";
  private static final String ENTITY_KIND = "User";
  private static final int MAX_QUERIES = 1000;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_GetsCorrectResponseLoggedIn() throws IOException, ServletException {
    helper.setEnvIsLoggedIn(true).setEnvEmail(TEST_EMAIL).setEnvAuthDomain("localhost");
    UserService userService = UserServiceFactory.getUserService();
    String logoutUrl = userService.createLogoutURL(REDIRECT_URL);
    String loginResponse = LOGIN_STATUS + "\n" + logoutUrl;
    
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    loginServlet.doGet(request, response);

    verify(response).setContentType(CONTENT_TYPE);
    assertEquals(stringWriter.getBuffer().toString().trim(), loginResponse);
  }

  @Test
  public void doGet_UserDoesNotExistCreateNewEntity() throws IOException, ServletException {
    helper.setEnvIsLoggedIn(true).setEnvEmail(TEST_EMAIL).setEnvAuthDomain("localhost");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    loginServlet.doGet(request, response);
    
    Query query =
          new Query(ENTITY_KIND).setFilter(
            new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, TEST_EMAIL));
    Entity result = datastore.prepare(query).asSingleEntity();
    assertNotNull(result);
  }

  @Test
  public void doGet_UserExistsNoNewEntity() throws IOException, ServletException {
    helper.setEnvIsLoggedIn(true).setEnvEmail(TEST_EMAIL).setEnvAuthDomain("localhost");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity newUser = new Entity(ENTITY_KIND);
    newUser.setProperty("email", TEST_EMAIL);
    datastore.put(newUser);
    
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    loginServlet.doGet(request, response);

    Query query =
          new Query(ENTITY_KIND).setFilter(
            new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, TEST_EMAIL));
    int numComments = datastore.prepare(query).countEntities(
        FetchOptions.Builder.withLimit(MAX_QUERIES)
    );
    assertEquals(1, numComments);
  }

  @Test
  public void doGet_GetsCorrectResponseLoggedOut() throws IOException, ServletException {
    helper.setEnvIsLoggedIn(false);
    UserService userService = UserServiceFactory.getUserService();
    String loginUrl = userService.createLoginURL(REDIRECT_URL);
    String logoutResponse = LOGOUT_STATUS + "\n" + loginUrl;

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    loginServlet.doGet(request, response);

    verify(response).setContentType(CONTENT_TYPE);
    assertEquals(stringWriter.getBuffer().toString().trim(), logoutResponse);
  }
}
