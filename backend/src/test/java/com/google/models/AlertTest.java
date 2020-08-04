package com.google.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import java.util.*;
import com.google.blackswan.mock.*;
import java.time.format.DateTimeParseException;

/** Contain tests for methods in {@link Alert} class. */
@RunWith(JUnit4.class)
public final class AlertTest {
  private static final int TIMESTAMP_CONSTANT = 1;
  private static final AnomalyGenerator dummyAnomalyGenerator = new DummyAnomalyGenerator();
  private static final LocalServiceTestHelper helper = 
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private Alert alert;

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    // TODO: Find out whether it's better to set parameters for Alert as private static variables,
    //       since they're used later to test getters. 
    alert = new Alert(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), dummyAnomalyGenerator.getAnomalies(), 
        Alert.UNRESOLVED_STATUS);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getTimestamp_workingGetter() {
    assertEquals(alert.getTimestamp(), Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT));
  }

  @Test
  public void getAnomalies_workingGetter() {
    // Check to see if List<Anomaly> returned is same as List<Anomaly> used to generate alert.
    assertEquals(alert.getAnomalies(), dummyAnomalyGenerator.getAnomalies());
  }

  @Test
  public void getStatus_workingGetter() {
    assertEquals(alert.getStatus(), Alert.UNRESOLVED_STATUS);
  }

  @Test
  public void setStatus_workingSetter() {
    alert.setStatus(Alert.RESOLVED_STATUS);

    assertEquals(alert.getStatus(), Alert.RESOLVED_STATUS);
  }

  @Test
  public void equals_workingComparator() {
    Alert sameAlert = new Alert(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), dummyAnomalyGenerator.getAnomalies(), 
        Alert.UNRESOLVED_STATUS);
    Alert diffTimeAlert = new Alert(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT + 1), dummyAnomalyGenerator.getAnomalies(), 
        Alert.UNRESOLVED_STATUS);
    Alert diffResolveAlert = new Alert(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), dummyAnomalyGenerator.getAnomalies(), 
        Alert.RESOLVED_STATUS);
    // TODO: Test equals with Alert object that has different list of anomalies. Currently, dummyAnomalyGenerator can only 
    //       generate one list of anomalies right now. 

    assertTrue(alert.equals(alert));
    assertTrue(alert.equals(sameAlert));
    assertFalse(alert.equals(diffTimeAlert));
    assertFalse(alert.equals(diffResolveAlert));
    assertFalse(alert.equals(null));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void toEntity_correctAlertToEntityConversion() {
    List<Anomaly> anomalyList = new ArrayList<Anomaly>();

    Entity alertEntity = Alert.toEntity(alert);
    List<EmbeddedEntity> anomalyEmbeddedList = 
        (List<EmbeddedEntity>) alertEntity.getProperty(Alert.ANOMALIES_LIST_PROPERTY);
    anomalyEmbeddedList.forEach(
      embeddedAnomaly -> anomalyList.add(Anomaly.toAnomaly(embeddedAnomaly))
    );

    assertEquals(anomalyList, alert.getAnomalies());
    assertEquals(alertEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY), alert.getTimestamp().toString());
    assertEquals(alertEntity.getProperty(Alert.STATUS_PROPERTY), alert.getStatus());
  }

  @Test
  public void toAlert_correctEntityToAlertConversion() throws DateTimeParseException {
    Entity alertEntity = Alert.toEntity(alert);
    Alert convertedAlert = Alert.toAlert(alertEntity);

    assertEquals(alert, convertedAlert);
  }

}
