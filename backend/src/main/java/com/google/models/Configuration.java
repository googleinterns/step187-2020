package com.google.models;

public final class Configuration {
  private final String user;
  private final String dimension;
  private final String metric;
  private final String relatedDimension;
  private final String relatedMetric;

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
