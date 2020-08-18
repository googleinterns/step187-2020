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
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/** Contain tests for methods in {@link Anomaly} class. */
@RunWith(JUnit4.class)
public final class AnomalyTest {
  private static final int TIMESTAMP_CONSTANT = 1;
  private static final String METRIC_NAME = "Sample metric name";
  private static final String DIMENSION_NAME = "Sample dimension name";
  private static final Map<Timestamp, MetricValue> DATA_POINTS = ImmutableMap.of( 
      Timestamp.getDummyTimestamp(1), new MetricValue(1), 
      Timestamp.getDummyTimestamp(2), new MetricValue(2), 
      Timestamp.getDummyTimestamp(3), new MetricValue(3));
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final Anomaly ANOMALY = new Anomaly(
      Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), 
      METRIC_NAME, DIMENSION_NAME, DATA_POINTS
    );

  @Before
  public void setUp() throws Exception {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getTimestamp_workingGetter() {
    assertEquals(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), 
        ANOMALY.getTimestamp());
  }

  @Test
  public void getMetricName_workingGetter() {
    assertEquals(METRIC_NAME, ANOMALY.getMetricName());
  }

  @Test
  public void getDimensionName_workingGetter() {
    assertEquals(DIMENSION_NAME, ANOMALY.getDimensionName());
  }

  @Test
  public void getDataPoints_workingGetter() {
    assertEquals(DATA_POINTS, ANOMALY.getDataPoints());
  }

  @Test
  public void equals_workingComparator() {
    Anomaly sameAnomaly = new Anomaly(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT),  
        METRIC_NAME, DIMENSION_NAME, DATA_POINTS);
    Anomaly diffTimeAnomaly = new Anomaly(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT + 1), 
        METRIC_NAME, DIMENSION_NAME, DATA_POINTS);
    Anomaly diffMetricNameAnomaly = new Anomaly(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), 
        "diff name", DIMENSION_NAME, DATA_POINTS);
    Anomaly diffDimensionNameAnomaly = new Anomaly(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), 
        METRIC_NAME, "diff name", DATA_POINTS);
    Anomaly diffDataPointsAnomaly = new Anomaly(Timestamp.getDummyTimestamp(TIMESTAMP_CONSTANT), 
      METRIC_NAME, "diff name", 
      ImmutableMap.of(Timestamp.getDummyTimestamp(2), new MetricValue(5), 
                      Timestamp.getDummyTimestamp(1), new MetricValue(2), 
                      Timestamp.getDummyTimestamp(3), new MetricValue(3))
    );

    assertTrue(ANOMALY.equals(ANOMALY));
    assertTrue(ANOMALY.equals(sameAnomaly));
    assertFalse(ANOMALY.equals(diffTimeAnomaly));
    assertFalse(ANOMALY.equals(diffMetricNameAnomaly));
    assertFalse(ANOMALY.equals(diffDimensionNameAnomaly));
    assertFalse(ANOMALY.equals(diffDataPointsAnomaly));
    assertFalse(ANOMALY.equals(null));
  }

  @Test
  public void toString_correctConversion() {
    StringBuilder expectedStr = new StringBuilder("");
    expectedStr.append("Timestamp: " + ANOMALY.getTimestamp() + "\n");
    expectedStr.append("Metric Name: " + ANOMALY.getMetricName() + "\n");
    expectedStr.append("Dimension Name: " + ANOMALY.getDimensionName() + "\n");
    expectedStr.append("Datapoints: \n");
    ANOMALY.getDataPoints().forEach((key, value) -> 
      expectedStr.append(key + ": " + value + "\n")
    );

    assertEquals(expectedStr.toString(), ANOMALY.toString());
  }

  @Test
  public void toEntity_correctAnomalyToEntityConversion() {
    Entity anomalyEntity = ANOMALY.toEntity();
    EmbeddedEntity dataPointsEE = 
        (EmbeddedEntity) anomalyEntity.getProperty(Anomaly.DATA_POINTS_PROPERTY);
    
    assertFalse(dataPointsEE.equals(null));
    assertTrue(embeddedEntityDataPoints_equal(ANOMALY.getDataPoints(), 
        dataPointsEE));
    assertEquals(ANOMALY.getTimestamp().toString(), 
        anomalyEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY));
    assertEquals(ANOMALY.getMetricName(), 
        anomalyEntity.getProperty(Anomaly.METRIC_NAME_PROPERTY));
    assertEquals(ANOMALY.getDimensionName(), 
        anomalyEntity.getProperty(Anomaly.DIMENSION_NAME_PROPERTY));
  }

  @Test
  public void toEmbeddedEntity_correctAnomalyToEmbeddedEntityConversion() {
    EmbeddedEntity anomalyEmbeddedEntity = ANOMALY.toEmbeddedEntity();
    EmbeddedEntity dataPointsEE = 
        (EmbeddedEntity) anomalyEmbeddedEntity.getProperty(Anomaly.DATA_POINTS_PROPERTY);
    
    assertNotNull(dataPointsEE);
    assertTrue(embeddedEntityDataPoints_equal(ANOMALY.getDataPoints(), dataPointsEE));
    assertEquals(ANOMALY.getTimestamp().toString(), 
        anomalyEmbeddedEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY));
    assertEquals(ANOMALY.getMetricName(), 
        anomalyEmbeddedEntity.getProperty(Anomaly.METRIC_NAME_PROPERTY));
    assertEquals(ANOMALY.getDimensionName(), 
        anomalyEmbeddedEntity.getProperty(Anomaly.DIMENSION_NAME_PROPERTY));
  }

  @Test
  public void createAnomalyFromEmbeddedEntity_correctEmbeddedEntityToAnomalyConversion() {
    EmbeddedEntity anomalyEmbeddedEntity = ANOMALY.toEmbeddedEntity();
    Anomaly convertedAnomaly = 
        Anomaly.createAnomalyFromEmbeddedEntity(anomalyEmbeddedEntity);

    assertEquals(ANOMALY, convertedAnomaly);
  }


  private boolean embeddedEntityDataPoints_equal(Map<Timestamp, MetricValue> dataPointsMap,
      EmbeddedEntity dataPointsEE) {
    return dataPointsMap.entrySet().stream().allMatch(
      e -> e.getValue().getValue() == ((long) dataPointsEE.getProperty(e.getKey().toString()))
    );
  }

}
