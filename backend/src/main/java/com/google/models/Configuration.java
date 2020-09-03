package com.google.models;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.common.collect.ImmutableList;
import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.OptionalLong;

public final class Configuration {
  private final String user;
  private final String dimension;
  private final String metric;
  private final String relatedDimension;
  private final String relatedMetric;

  public static final String USER_PROPERTY = "user";
  public static final String DIMENSION_PROPERTY = "dimension";
  public static final String METRIC_PROPERTY = "metric";
  public static final String RELATED_DIMENSION_PROPERTY = "relatedDimension";
  public static final String RELATED_METRIC_PROPERTY = "relatedMetric";

  public Configuration(String user, String dimension, String metric, String relatedDimension, String relatedMetric) {
      
    this.user = user;
    this.dimension = dimension;
    this.metric = metric;
    this.relatedDimension = relatedDimension;
    this.relatedMetric = relatedMetric;
  }
  @Override
  public String toString() {
    return dimension + " " + metric + " " + relatedDimension + " " + relatedMetric;
  }
}