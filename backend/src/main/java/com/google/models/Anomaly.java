// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.models;

import com.google.common.collect.ImmutableMap;
import com.google.models.MetricValue;
import com.google.models.Timestamp;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeParseException;

/** Store anomaly-related data. */
public final class Anomaly {
  public static final String ANOMALY_ENTITY_KIND = "anomaly";
  public static final String METRIC_NAME_PROPERTY = "metricName";
  public static final String DIMENSION_NAME_PROPERTY = "dimensionName";
  public static final String DATA_POINTS_PROPERTY = "dataPoints";

  private static final String DUMMY_METRIC_NAME = "Sample metric name";
  private static final String DUMMY_DIMENSION_NAME = "Sample dimension name";
  private static final Map<Timestamp, MetricValue> DUMMY_DATA_POINTS = ImmutableMap.of( 
      Timestamp.getDummyTimestamp(1), new MetricValue(1), 
      Timestamp.getDummyTimestamp(2), new MetricValue(2), 
      Timestamp.getDummyTimestamp(3), new MetricValue(3));
  
  private final Timestamp timestampDate;
  private final String metricName;
  private final String dimensionName;
  private final Map<Timestamp, MetricValue> dataPoints;
  
  public Anomaly(Timestamp timestampDate, String metricName, String dimensionName, 
      Map<Timestamp, MetricValue> dataPoints) {
    this.timestampDate = timestampDate;
    this.metricName = metricName;
    this.dimensionName = dimensionName;
    this.dataPoints = dataPoints;
  }

  public Timestamp getTimestamp() {
    return timestampDate;
  }

  public String getMetricName() {
    return metricName;
  }

  public String getDimensionName() {
    return dimensionName;
  }

  public Map<Timestamp, MetricValue> getDataPoints() {
    return dataPoints;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Anomaly)) {
      return false;
    }

    Anomaly target = (Anomaly) o;

    return target.timestampDate.equals(timestampDate) && target.metricName.equals(metricName)
        && target.dimensionName.equals(dimensionName) 
        && dataPoints.entrySet().stream().allMatch(
          e -> e.getValue().equals(target.dataPoints.get(e.getKey()))
        );
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");
    str.append("Timestamp: " + timestampDate + "\n");
    str.append("Metric Name: " + metricName + "\n");
    str.append("Dimension Name: " + dimensionName + "\n");
    str.append("Datapoints: \n");
    dataPoints.forEach((key, value) -> 
      str.append(key + ": " + value + "\n")
    );
    return str.toString();
  }

  public static Anomaly getDummyAnomaly() {
    return new Anomaly(Timestamp.getDummyTimestamp(1), DUMMY_METRIC_NAME, DUMMY_DIMENSION_NAME, 
        DUMMY_DATA_POINTS);
  }

  public Entity toEntity() {
    Entity anomalyEntity = new Entity(ANOMALY_ENTITY_KIND);
    anomalyEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, timestampDate.toString());
    anomalyEntity.setProperty(METRIC_NAME_PROPERTY, metricName);
    anomalyEntity.setProperty(DIMENSION_NAME_PROPERTY, dimensionName);

    EmbeddedEntity dataPointsEntity = new EmbeddedEntity();
    dataPoints.forEach((timestamp, metricValue) -> 
        dataPointsEntity.setProperty(timestamp.toString(), metricValue.getValue()));
    anomalyEntity.setProperty(DATA_POINTS_PROPERTY, dataPointsEntity);

    return anomalyEntity;
  }

  /** TODO: Move Entity to Embedded Entity conversion somewhere else that all entity can share. */
  public EmbeddedEntity toEmbeddedEntity() {
    Entity anomalyEntity = toEntity();
    EmbeddedEntity anomalyEmbeddedEntity = new EmbeddedEntity();
    anomalyEmbeddedEntity.setKey(anomalyEntity.getKey());
    anomalyEmbeddedEntity.setPropertiesFrom(anomalyEntity);
    return anomalyEmbeddedEntity;
  }

  public static Anomaly createAnomalyFromEmbeddedEntity(EmbeddedEntity anomalyEmbeddedEntity) {
    EmbeddedEntity dataPointsEE = (EmbeddedEntity) anomalyEmbeddedEntity.getProperty(DATA_POINTS_PROPERTY);
    Map<Timestamp, MetricValue> dataPointsMap = new HashMap<>();

    if (dataPointsEE != null) {
      for (String key : dataPointsEE.getProperties().keySet()) {
        dataPointsMap.put(new Timestamp(key), new MetricValue((int) dataPointsEE.getProperty(key)));
      }
    }

    return new Anomaly(new Timestamp((String) anomalyEmbeddedEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY)), 
        (String) anomalyEmbeddedEntity.getProperty(METRIC_NAME_PROPERTY),
        (String) anomalyEmbeddedEntity.getProperty(DIMENSION_NAME_PROPERTY),
        dataPointsMap);
  }

}