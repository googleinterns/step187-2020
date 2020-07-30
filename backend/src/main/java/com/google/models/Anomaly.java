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

/** Store anomaly-related data. */
public final class Anomaly {
  public static final String ANOMALY_ENTITY_KIND = "anomaly";
  public static final String METRIC_NAME_PROPERTY = "metricName";
  public static final String DIMENSION_NAME_PROPERTY = "dimensionName";
  public static final String DATA_POINTS_PROPERTY = "dataPoints";

  private static final String DUMMY_METRIC_NAME = "Sample metric name";
  private static final String DUMMY_DIMENSION_NAME = "Sample dimension name";
  private static final Map<Timestamp, MetricValue> DUMMY_DATA_POINTS = ImmutableMap.of( 
      Timestamp.getDummyTimestamp(), new MetricValue(1), 
      Timestamp.getDummyTimestamp(), new MetricValue(2), 
      Timestamp.getDummyTimestamp(), new MetricValue(3));
  
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

  public static Anomaly getDummyAnomaly() {
    return new Anomaly(Timestamp.getDummyTimestamp(), DUMMY_METRIC_NAME, DUMMY_DIMENSION_NAME, 
        DUMMY_DATA_POINTS);
  }

  public static Entity toEntity(Anomaly anomaly) {
    Entity anomalyEntity = new Entity(ANOMALY_ENTITY_KIND);
    anomalyEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, anomaly.timestampDate.toString());
    anomalyEntity.setProperty(METRIC_NAME_PROPERTY, anomaly.metricName);
    anomalyEntity.setProperty(DIMENSION_NAME_PROPERTY, anomaly.dimensionName);

    EmbeddedEntity dataPointsEntity = new EmbeddedEntity();
    anomaly.dataPoints.forEach((timestamp, metricValue) -> 
        dataPointsEntity.setProperty(timestamp.toString(), metricValue.getValue()));
    anomalyEntity.setProperty(DATA_POINTS_PROPERTY, dataPointsEntity);

    return anomalyEntity;
  }

}
