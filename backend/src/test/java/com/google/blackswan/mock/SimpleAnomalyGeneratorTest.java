package com.google.blackswan.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.blackswan.mock.*;
import com.google.models.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.List;

/** Contain tests for methods in the {@link SimpleAnomalyGenerator} class. */
@RunWith(JUnit4.class)
public class SimpleAnomalyGeneratorTest {
  private static final String METRIC_NAME = "Interest Over Time";
  private static final String DIMENSION_NAME = "Ramen";
  private static final ImmutableMap<String, Integer> SAMPLE_DATA = ImmutableMap.of(
    "2019-07-21", 73, 
    "2019-07-28", 59, 
    "2019-08-04", 75,
    "2019-08-11", 79,
    "2019-08-18", 67
  );
  private static final int SET_THRESHOLD_HIGH = 6;
  private static final int SET_THRESHOLD_LOW = 4;
  private static final int SET_DATAPOINTS = 2;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  
  private SimpleAnomalyGenerator anomalyGenerator; 
      

  @Before
  public void setUp() throws Exception {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getAnomalies_returnsListOfAnomaliesWithSizeOne() {
    anomalyGenerator = SimpleAnomalyGenerator.createGeneratorWithString(
      DataInfo.of(METRIC_NAME, DIMENSION_NAME),
      MockTestHelper.inputForAnomalyGenerator(SAMPLE_DATA), 
      SET_THRESHOLD_HIGH, SET_DATAPOINTS
    );
    // Avg = 70, only 1 data point exceeds 76. 
    ImmutableMap<Timestamp, MetricValue> expectedDataPoints = ImmutableMap.of(
      new Timestamp("2019-07-28"), new MetricValue(59),
      new Timestamp("2019-08-04"), new MetricValue(75), 
      new Timestamp("2019-08-11"), new MetricValue(79), 
      new Timestamp("2019-08-18"), new MetricValue(67)
    );
    Anomaly expectedAnomaly = new Anomaly(
      new Timestamp("2019-08-11"), METRIC_NAME, DIMENSION_NAME, 
      expectedDataPoints, ImmutableList.of(RelatedData.getDummyRelatedData())
    );

    List<Anomaly> generatedAnomalies = anomalyGenerator.getAnomalies();

    assertEquals(1, generatedAnomalies.size());
    assertEquals(expectedAnomaly, generatedAnomalies.get(0));
  }

  @Test
  public void getAnomalies_returnsListOfAnomaliesWithSizeTwo() {
    anomalyGenerator = SimpleAnomalyGenerator.createGeneratorWithString(
      DataInfo.of(METRIC_NAME, DIMENSION_NAME),
      MockTestHelper.inputForAnomalyGenerator(SAMPLE_DATA), 
      SET_THRESHOLD_LOW, SET_DATAPOINTS
    );
    // Avg = 70, only 2 data points exceed 74.
    ImmutableMap<Timestamp, MetricValue> expectedDataPoints1 = ImmutableMap.of(
      new Timestamp("2019-07-21"), new MetricValue(73),
      new Timestamp("2019-07-28"), new MetricValue(59),
      new Timestamp("2019-08-04"), new MetricValue(75), 
      new Timestamp("2019-08-11"), new MetricValue(79), 
      new Timestamp("2019-08-18"), new MetricValue(67)
    );
    Anomaly expectedAnomaly1 = new Anomaly(
      new Timestamp("2019-08-04"), METRIC_NAME, DIMENSION_NAME, 
      expectedDataPoints1, ImmutableList.of(RelatedData.getDummyRelatedData())
    );
    ImmutableMap<Timestamp, MetricValue> expectedDataPoints2 = ImmutableMap.of(
      new Timestamp("2019-07-28"), new MetricValue(59),
      new Timestamp("2019-08-04"), new MetricValue(75), 
      new Timestamp("2019-08-11"), new MetricValue(79), 
      new Timestamp("2019-08-18"), new MetricValue(67)
    );
    Anomaly expectedAnomaly2 = new Anomaly(
      new Timestamp("2019-08-11"), METRIC_NAME, DIMENSION_NAME, 
      expectedDataPoints2, ImmutableList.of(RelatedData.getDummyRelatedData())
    );

    List<Anomaly> generatedAnomalies = anomalyGenerator.getAnomalies();

    assertEquals(2, generatedAnomalies.size());
    assertEquals(expectedAnomaly1, generatedAnomalies.get(0));
    assertEquals(expectedAnomaly2, generatedAnomalies.get(1));
  }

}
