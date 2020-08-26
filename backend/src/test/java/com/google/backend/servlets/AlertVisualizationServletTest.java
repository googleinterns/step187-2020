package com.google.backend.servlets;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList; 
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.models.Alert;
import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/** Contain tests for methods in {@link AlertVisualizationServlet} class. */
@RunWith(JUnit4.class)
public class AlertVisualizationServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  @Rule // JUnit 4 uses Rules for testing specific messages
  public ExpectedException thrown = ExpectedException.none();

  private static final String RESPONSE_CONTENT_TYPE = "application/json;";
  private static final String ID_PARAM = "id";
  private static final Long FAKE_ID = 1L;

  private static final AlertVisualizationServlet alertVisualizationServlet = new AlertVisualizationServlet();
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
    new LocalDatastoreServiceTestConfig());
  private StringWriter stringWriter = new StringWriter();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_ReturnsRequestedAlertEntity() throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Alert newAlert = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(0), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED);
    datastore.put(newAlert.toEntity());

    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    Alert expectedAlert = Alert.createAlertFromEntity(datastore.prepare(query).asSingleEntity());
    when(request.getParameter(ID_PARAM)).thenReturn(Long.toString(expectedAlert.getAlertId()));

    
    alertVisualizationServlet.doGet(request, response);

    verify(response).setContentType(RESPONSE_CONTENT_TYPE);
    JsonElement expected = JsonParser.parseString(
      new Gson().toJson(expectedAlert));
    JsonElement result = JsonParser.parseString(stringWriter.getBuffer().toString().trim());
    assertEquals(expected, result);
  }

  @Test
  public void doGet_AlertIdNotFound_ThrowsException() throws IOException, ServletException {
    when(request.getParameter(ID_PARAM)).thenReturn(Long.toString(FAKE_ID));

    thrown.expect(ServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(EntityNotFoundException.class));
    alertVisualizationServlet.doGet(request, response);    
  }

}
