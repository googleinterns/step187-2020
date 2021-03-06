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


/** Contain tests for methods in {@link MetricValue} class. */
@RunWith(JUnit4.class)
public final class MetricValueTest {
  private static final int VALUE_CONST = 1;
  private static final MetricValue METRIC_VALUE = new MetricValue(VALUE_CONST);

  @Test
  public void getValue_workingGetter() {
    assertEquals(VALUE_CONST, METRIC_VALUE.getValue());
  }

  @Test
  public void equals_workingComparator() {
    MetricValue sameMetricValue = new MetricValue(VALUE_CONST);
    MetricValue diffMetricValue = new MetricValue(VALUE_CONST + 1);

    assertEquals(METRIC_VALUE, METRIC_VALUE);
    assertEquals(METRIC_VALUE, sameMetricValue);
    assertFalse(METRIC_VALUE.equals(diffMetricValue));
    assertFalse(METRIC_VALUE.equals(null));
  }

  /** TODO: Add tests for toString() and other methods once logic of Metric Value complicates. */

}
