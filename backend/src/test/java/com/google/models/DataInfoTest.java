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


/** Contain tests for methods in {@link DataInfo} and {@link DataInfoUser} class. */
@RunWith(JUnit4.class)
public final class DataInfoTest {
  private static final String METRIC_NAME = "Interest Over Time";
  private static final String DIMENSION_NAME = "Ramen";
  private static final String USER_NAME = "catyu@";
  private static final DataInfo DATA_INFO 
      = DataInfo.of(METRIC_NAME, DIMENSION_NAME);
  private static final DataInfoUser DATA_INFO_USER 
      = DataInfoUser.of(METRIC_NAME, DIMENSION_NAME, USER_NAME);

  @Test
  public void getMetricName_workingGetter() {
    assertEquals(METRIC_NAME, DATA_INFO.getMetricName());
  }

  @Test
  public void getDimensionName_workingGetter() {
    assertEquals(DIMENSION_NAME, DATA_INFO.getDimensionName());
  }

  @Test
  public void equals_workingComparatorAndHashCode() {
    DataInfo same = DataInfo.of(METRIC_NAME, DIMENSION_NAME);
    DataInfo diffMetric = DataInfo.of("Country", DIMENSION_NAME);
    DataInfo diffDimension = DataInfo.of(METRIC_NAME, "Udon");

    assertEquals(DATA_INFO, DATA_INFO);
    assertEquals(DATA_INFO.hashCode(), DATA_INFO.hashCode());
    assertEquals(DATA_INFO, same);
    assertEquals(same, DATA_INFO);
    assertEquals(DATA_INFO.hashCode(), same.hashCode());
    assertFalse(DATA_INFO.equals(diffMetric));
    assertFalse(DATA_INFO.hashCode() == diffMetric.hashCode());
    assertFalse(DATA_INFO.equals(diffDimension));
    assertFalse(DATA_INFO.hashCode() == diffDimension.hashCode());
    assertFalse(DATA_INFO.equals(null));
  }

  /** Tests for DataInfoUser class below. */

  @Test
  public void getUsername_workingGetter() {
    assertEquals(USER_NAME, DATA_INFO_USER.getUsername());
  }

  @Test
  public void getDataInfo_workingGetter() {
    assertEquals(DATA_INFO, DATA_INFO_USER.getDataInfo());
  }

}
