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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.blackswan.mock.Constant;
import com.google.appengine.api.datastore.Entity;

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
  private static final LocalServiceTestHelper helper =
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

  @Test
  public void createFromEntityToDataInfo_correctConversion() {
    Entity entity = new Entity(Constant.CONFIG_ENTITY_KIND);
    entity.setProperty(Constant.CONFIG_METRIC_PROPERTY, METRIC_NAME);
    entity.setProperty(Constant.CONFIG_DIMENSION_PROPERTY, DIMENSION_NAME);

    assertEquals(DATA_INFO, DataInfo.createFrom(entity));
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

  @Test
  public void createFromEntityToDataInfoUser_correctConversion() {
    Entity entity = new Entity(Constant.CONFIG_ENTITY_KIND);
    entity.setProperty(Constant.CONFIG_METRIC_PROPERTY, METRIC_NAME);
    entity.setProperty(Constant.CONFIG_DIMENSION_PROPERTY, DIMENSION_NAME);
    entity.setProperty(Constant.CONFIG_USER_PROPERTY, USER_NAME);

    assertEquals(DATA_INFO_USER, DataInfoUser.createFrom(entity));
  }
}
