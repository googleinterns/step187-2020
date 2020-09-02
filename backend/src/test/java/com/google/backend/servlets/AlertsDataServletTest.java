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
import java.util.logging.Logger;
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

/** Contain tests for methods in {@link AlertsDataServlet} class. */
@RunWith(JUnit4.class)
public class AlertsDataServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  @Rule // JUnit 4 uses Rules for testing specific messages
  public ExpectedException thrown = ExpectedException.none();

  private static final String RESPONSE_CONTENT_TYPE = "application/json;";
  private static final String REQUEST_CONTENT_TYPE = "text/plain;";
  private static final String REQUEST_CHARSET = "UTF-8";
  private static final Long FAKE_ID = 1L;
  private static final String EMPTY_BODY_ERROR = "No data was sent in HTTP request body.";
  private static final String LIMIT_PARAM = "limit";
  private static final String FAKE_LIMIT = "2";
  
  private static final AlertsDataServlet alertsDataServlet = new AlertsDataServlet();
  private static final Logger log = Logger.getLogger(AlertsDataServletTest.class.getName());
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
  public void doGet_ReturnsAlertEntityAsJson() throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Alert newAlert = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(0), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED, Alert.PriorityLevel.P2);
    datastore.put(newAlert.toEntity());
    // The new alert needs to be queried from the datastore in order to contain a valid id. 
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    Alert expectedAlert = Alert.createAlertFromEntity(datastore.prepare(query).asSingleEntity());
    
    when(request.getParameter(LIMIT_PARAM)).thenReturn(FAKE_LIMIT);

    alertsDataServlet.doGet(request, response);

    verify(response).setContentType(RESPONSE_CONTENT_TYPE);
    JsonElement expected = JsonParser.parseString(
      new Gson().toJson(ImmutableList.of(expectedAlert)));
    JsonElement result = JsonParser.parseString(stringWriter.getBuffer().toString().trim());
    assertEquals(expected, result);
  }

  @Test
  public void doGet_ReturnsLimitedNumberOfSortedAlerts() throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Alert newAlertOne = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(0), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED, Alert.PriorityLevel.P2);
    Alert newAlertTwo = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(1), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED, Alert.PriorityLevel.P2);
    Alert newAlertThree = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(2), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED, Alert.PriorityLevel.P2);
    datastore.put(newAlertOne.toEntity());
    datastore.put(newAlertTwo.toEntity());
    datastore.put(newAlertThree.toEntity());

    when(request.getParameter(LIMIT_PARAM)).thenReturn(FAKE_LIMIT);


    List<Alert> expectedAlerts = new ArrayList<>();
    try {
      Entity alertThree = datastore.get(KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, 3));
      Entity alertTwo = datastore.get(KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, 2));    
      expectedAlerts.add(Alert.createAlertFromEntity(alertThree));
      expectedAlerts.add(Alert.createAlertFromEntity(alertTwo));
    } catch (EntityNotFoundException e) {
      log.warning("Test entities were not put into Datastore.");
    }

    alertsDataServlet.doGet(request, response);

    verify(response).setContentType(RESPONSE_CONTENT_TYPE);
    JsonElement expected = JsonParser.parseString(
      new Gson().toJson(ImmutableList.copyOf(expectedAlerts)));
    JsonElement result = JsonParser.parseString(stringWriter.getBuffer().toString().trim());
    assertEquals(expected, result);
  }

  @Test
  public void doPost_ChangesAlertStatusInDatastore() throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Alert newAlert = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(0), 
        Arrays.asList(Anomaly.getDummyAnomaly()), Alert.StatusType.UNRESOLVED, Alert.PriorityLevel.P2);
    Entity newAlertEntity = newAlert.toEntity();
    datastore.put(newAlertEntity);
    Long id = newAlertEntity.getKey().getId();

    String data = id + " " + Alert.StatusType.RESOLVED;
    when(request.getReader()).thenReturn(new BufferedReader(new StringReader(data)));
    when(request.getContentType()).thenReturn(REQUEST_CONTENT_TYPE);
    when(request.getCharacterEncoding()).thenReturn(REQUEST_CHARSET);


    alertsDataServlet.doPost(request, response);

    assertEquals(1, datastore.prepare(new Query(Alert.ALERT_ENTITY_KIND)).countEntities(withLimit(10)));
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    Entity resultEntity = datastore.prepare(query).asSingleEntity();
    assertEquals(Alert.StatusType.RESOLVED.name(), resultEntity.getProperty(Alert.STATUS_PROPERTY).toString());
  }

  @Test
  public void doPost_EmptyRequestBody_ThrowsException() throws IOException, ServletException {
    when(request.getReader()).thenReturn(new BufferedReader(new StringReader("")));
    when(request.getContentType()).thenReturn(REQUEST_CONTENT_TYPE);
    when(request.getCharacterEncoding()).thenReturn(REQUEST_CHARSET);

    thrown.expect(ServletException.class);
    thrown.expectMessage(EMPTY_BODY_ERROR);
    alertsDataServlet.doPost(request, response);
  }

  @Test
  public void doPost_EntityNotInDatastore_ThrowsException() throws IOException, ServletException {
    String data = FAKE_ID + " " + Alert.StatusType.RESOLVED;
    when(request.getReader()).thenReturn(new BufferedReader(new StringReader(data)));
    when(request.getContentType()).thenReturn(REQUEST_CONTENT_TYPE);
    when(request.getCharacterEncoding()).thenReturn(REQUEST_CHARSET);

    thrown.expect(ServletException.class);
    thrown.expectCause(IsInstanceOf.<Throwable>instanceOf(EntityNotFoundException.class));
    alertsDataServlet.doPost(request, response);    
  }

}
