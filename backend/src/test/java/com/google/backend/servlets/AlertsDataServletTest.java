package com.google.backend.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList; 
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.models.Alert;
import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
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

/** Contain tests for methods in {@link AlertsDataServlet} class. */
@RunWith(JUnit4.class)
public class AlertsDataServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  private static final String CONTENT_TYPE = "application/json;";
  private static final String ENTITY_KIND = Alert.ALERT_ENTITY_KIND;

  private static final AlertsDataServlet alertsDataServlet = new AlertsDataServlet();
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
  public void doGet_returnAlertEntitiesAsJson() throws IOException, ServletException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Alert newAlert = Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(0), new ArrayList<Anomaly>(), 
        Alert.StatusType.UNRESOLVED);
    datastore.put(newAlert.toEntity());

    // ExpectedAlert needs to be queried from the datastore in order to contain a valid id. 
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    Alert expectedAlert = Alert.createAlertFromEntity(datastore.prepare(query).asSingleEntity());

    alertsDataServlet.doGet(request, response);

    verify(response).setContentType(CONTENT_TYPE);
    JsonElement expected = JsonParser.parseString(
      new Gson().toJson(ImmutableList.of(expectedAlert)));
    JsonElement result = JsonParser.parseString(stringWriter.getBuffer().toString().trim());
    assertEquals(expected, result);
  }
}
