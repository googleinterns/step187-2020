package com.google.blackswan.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.blackswan.mock.*;
import com.google.models.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/** Contain tests for methods in the {@link CronServlet} class. */
@RunWith(JUnit4.class)
public class SimpleAlertGeneratorTest {
  private static final String METRIC_NAME = "Interest Over Time";
  private static final String DIMENSION_NAME = "Ramen";
  private static final Map<String, Integer> SAMPLE_DATA = ImmutableMap.of(
    "2019-07-21", 73, 
    "2019-07-28", 59, 
    "2019-08-04", 75,
    "2019-08-11", 79,
    "2019-08-18", 67
  );
  private static final int SET_THRESHOLD_LOW = 2;
  private static final int SET_DATAPOINTS = 2;
  private static final int EXPECTED_ALERT_SIZE = 2;
  private static final AlertGenerator alertGenerator = new SimpleAlertGenerator(
    SimpleAnomalyGenerator.createGeneratorWithString(
      inputForAnomalyGenerator(), SET_THRESHOLD_LOW, SET_DATAPOINTS
    )
  );
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws Exception {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getAlerts_returnsListOfAlerts() {
    // Create first expected alert which contains one anomaly in July.
    Map<Timestamp, MetricValue> expectedDataPoints1 = ImmutableMap.of(
      new Timestamp("2019-07-21"), new MetricValue(73),
      new Timestamp("2019-07-28"), new MetricValue(59), 
      new Timestamp("2019-08-04"), new MetricValue(75)
    );
    Anomaly expectedAnomalyGroup1 = new Anomaly(
      new Timestamp("2019-07-21"), METRIC_NAME, DIMENSION_NAME, expectedDataPoints1
    );
    Alert expectedAlertJuly = new Alert(
      new Timestamp("2019-08-01"), 
      Arrays.asList(expectedAnomalyGroup1), 
      Alert.StatusType.UNRESOLVED
    );
    // Create second expected alert which contains two anomalies that occurs in August. 
    Map<Timestamp, MetricValue> expectedDataPoints2 = ImmutableMap.of(
      new Timestamp("2019-07-21"), new MetricValue(73),
      new Timestamp("2019-07-28"), new MetricValue(59),
      new Timestamp("2019-08-04"), new MetricValue(75), 
      new Timestamp("2019-08-11"), new MetricValue(79), 
      new Timestamp("2019-08-18"), new MetricValue(67)
    );
    Anomaly expectedAnomalyGroup2_1 = new Anomaly(
      new Timestamp("2019-08-04"), METRIC_NAME, DIMENSION_NAME, expectedDataPoints2
    );
    Map<Timestamp, MetricValue> expectedDataPoints3 = ImmutableMap.of(
      new Timestamp("2019-07-28"), new MetricValue(59),
      new Timestamp("2019-08-04"), new MetricValue(75), 
      new Timestamp("2019-08-11"), new MetricValue(79), 
      new Timestamp("2019-08-18"), new MetricValue(67)
    );
    Anomaly expectedAnomalyGroup2_2 = new Anomaly(
      new Timestamp("2019-08-11"), METRIC_NAME, DIMENSION_NAME, expectedDataPoints3
    );
    Alert expectedAlertAugust = new Alert(
      new Timestamp("2019-09-01"), 
      Arrays.asList(expectedAnomalyGroup2_1, expectedAnomalyGroup2_2), 
      Alert.StatusType.UNRESOLVED
    );

    List<Alert> generatedAlerts = alertGenerator.getAlerts();

    assertEquals(generatedAlerts.size(), EXPECTED_ALERT_SIZE);
    assertEquals(generatedAlerts.get(0), expectedAlertJuly);
    assertEquals(generatedAlerts.get(1), expectedAlertAugust);
  }

  private static String inputForAnomalyGenerator() {
    StringBuilder str = new StringBuilder("");
    for (Map.Entry<String, Integer> entry : SAMPLE_DATA.entrySet()) {
      str.append(entry.getKey() + "," + entry.getValue().toString() + "\n");
    }
    return str.toString();
  }

}
