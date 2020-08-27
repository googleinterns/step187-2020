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
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.List;
import java.util.Collections;

/** Contain tests for methods in {@link RelatedData} class. */
@RunWith(JUnit4.class)
public final class RelatedDataTest {
  private static final String INDENT = "  ";
  private static final String INDENT_DOUBLE = "    ";
  private static final String METRIC_NAME = "Sample metric name";
  private static final String DIMENSION_NAME = "Sample dimension name";
  private static final String USERNAME = "catyu@";
  private static final Map<Timestamp, MetricValue> DATA_POINTS = ImmutableMap.of( 
      Timestamp.getDummyTimestamp(1), new MetricValue(1), 
      Timestamp.getDummyTimestamp(2), new MetricValue(2), 
      Timestamp.getDummyTimestamp(3), new MetricValue(3));
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static final RelatedData RELATED_DATA = new RelatedData(
      USERNAME, METRIC_NAME, DIMENSION_NAME, DATA_POINTS
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
  public void getMetricName_workingGetter() {
    assertEquals(METRIC_NAME, RELATED_DATA.getMetricName());
  }

  @Test
  public void getDimensionName_workingGetter() {
    assertEquals(DIMENSION_NAME, RELATED_DATA.getDimensionName());
  }

  @Test
  public void getDataPoints_workingGetter() {
    assertEquals(DATA_POINTS, RELATED_DATA.getDataPoints());
  }

  @Test
  public void getUsername_workingGetter() {
    assertEquals(USERNAME, RELATED_DATA.getUsername());
  }

  @Test
  public void equals_workingComparator() {
    RelatedData sameRelatedData = new RelatedData(USERNAME, METRIC_NAME, 
        DIMENSION_NAME, DATA_POINTS);
    RelatedData diffUsername = new RelatedData("leodson@", METRIC_NAME, 
        DIMENSION_NAME, DATA_POINTS);
    RelatedData diffMetricName = new RelatedData(USERNAME, "diff name", 
        DIMENSION_NAME, DATA_POINTS);
    RelatedData diffDimensionName = new RelatedData(USERNAME, METRIC_NAME, 
        "diff name", DATA_POINTS);
    RelatedData diffDataPoints = new RelatedData(USERNAME, METRIC_NAME, 
        DIMENSION_NAME, ImmutableMap.of(Timestamp.getDummyTimestamp(2), new MetricValue(5), 
                                        Timestamp.getDummyTimestamp(1), new MetricValue(2), 
                                        Timestamp.getDummyTimestamp(3), new MetricValue(3)));

    assertTrue(RELATED_DATA.equals(RELATED_DATA));
    assertTrue(RELATED_DATA.equals(sameRelatedData));
    assertFalse(RELATED_DATA.equals(diffUsername));
    assertFalse(RELATED_DATA.equals(diffMetricName));
    assertFalse(RELATED_DATA.equals(diffDimensionName));
    assertFalse(RELATED_DATA.equals(diffDataPoints));
    assertFalse(RELATED_DATA.equals(null));
  }

  @Test
  public void toString_correctConversion() {
    StringBuilder expectedStr = new StringBuilder()
        .append(INDENT).append("Username: ")
        .append(RELATED_DATA.getUsername()).append("\n")
        .append(INDENT).append("Metric Name: ")
        .append(RELATED_DATA.getMetricName()).append("\n")
        .append(INDENT).append("Dimension Name: ")
        .append(RELATED_DATA.getDimensionName()).append("\n")
        .append(INDENT).append("Datapoints: \n");
    RELATED_DATA.getDataPoints().forEach((key, value) -> 
      expectedStr.append(INDENT_DOUBLE).append(key)
          .append(": ").append(value).append("\n")
    );

    assertEquals(expectedStr.toString(), RELATED_DATA.toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void toEntity_correctToEntityConversion() {
    Entity relatedDataEntity = RELATED_DATA.toEntity();
    EmbeddedEntity dataPointsEE = 
        (EmbeddedEntity) relatedDataEntity.getProperty(RelatedData.DATA_POINTS_PROPERTY);

    assertFalse(dataPointsEE.equals(null));
    assertTrue(embeddedEntityDataPoints_equal(RELATED_DATA.getDataPoints(), 
        dataPointsEE));
    assertEquals(RELATED_DATA.getUsername(), 
        relatedDataEntity.getProperty(RelatedData.USERNAME_PROPERTY));
    assertEquals(RELATED_DATA.getMetricName(), 
        relatedDataEntity.getProperty(RelatedData.METRIC_NAME_PROPERTY));
    assertEquals(RELATED_DATA.getDimensionName(), 
        relatedDataEntity.getProperty(RelatedData.DIMENSION_NAME_PROPERTY));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void toEmbeddedEntity_correctToEmbeddedEntityConversion() {
    EmbeddedEntity relatedDataEmbeddedEntity = RELATED_DATA.toEmbeddedEntity();
    EmbeddedEntity dataPointsEE = (EmbeddedEntity) relatedDataEmbeddedEntity
        .getProperty(RelatedData.DATA_POINTS_PROPERTY);

    assertFalse(dataPointsEE.equals(null));
    assertTrue(embeddedEntityDataPoints_equal(RELATED_DATA.getDataPoints(), 
        dataPointsEE));
    assertEquals(RELATED_DATA.getUsername(), 
        relatedDataEmbeddedEntity.getProperty(RelatedData.USERNAME_PROPERTY));
    assertEquals(RELATED_DATA.getMetricName(), 
        relatedDataEmbeddedEntity.getProperty(RelatedData.METRIC_NAME_PROPERTY));
    assertEquals(RELATED_DATA.getDimensionName(), 
        relatedDataEmbeddedEntity.getProperty(RelatedData.DIMENSION_NAME_PROPERTY));
  }

  @Test
  public void createFromEmbeddedEntity_correctEmbeddedEntityConversion() {
    EmbeddedEntity relatedDataEmbeddedEntity = RELATED_DATA.toEmbeddedEntity();
    RelatedData converted = 
        RelatedData.createFromEmbeddedEntity(relatedDataEmbeddedEntity);

    assertEquals(RELATED_DATA, converted);
  }

  private boolean embeddedEntityDataPoints_equal(Map<Timestamp, MetricValue> dataPointsMap,
      EmbeddedEntity dataPointsEE) {
    return dataPointsMap.entrySet().stream().allMatch(
      e -> e.getValue().getValue() == ((long) dataPointsEE.getProperty(e.getKey().toString()))
    );
  }

}
