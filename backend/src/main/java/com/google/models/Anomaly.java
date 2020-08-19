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
import com.google.common.collect.ImmutableList;
import com.google.models.MetricValue;
import com.google.models.Timestamp;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeParseException;

/** 
 * Store anomaly-related data. 
 * This is an immutable class. 
 */
public final class Anomaly {
  public static final String ANOMALY_ENTITY_KIND = "anomaly";
  public static final String METRIC_NAME_PROPERTY = "metricName";
  public static final String DIMENSION_NAME_PROPERTY = "dimensionName";
  public static final String DATA_POINTS_PROPERTY = "dataPoints";
  public static final String RELATED_DATA_LIST_PROPERTY = "relatedDataList";

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
  private final List<RelatedData> relatedDataList;
  
  public Anomaly(Timestamp timestampDate, String metricName, String dimensionName, 
      Map<Timestamp, MetricValue> dataPoints, List<RelatedData> relatedDataList) {
    this.timestampDate = timestampDate;
    this.metricName = metricName;
    this.dimensionName = dimensionName;
    this.dataPoints = ImmutableMap.copyOf(dataPoints);
    this.relatedDataList = ImmutableList.copyOf(relatedDataList);
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

  public List<RelatedData> getRelatedDataList() {
    return relatedDataList;
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
    str.append("Related Data: \n");
    relatedDataList.forEach(data -> str.append(data));
    return str.toString();
  }

  public static Anomaly getDummyAnomaly() {
    return new Anomaly(Timestamp.getDummyTimestamp(1), DUMMY_METRIC_NAME, DUMMY_DIMENSION_NAME, 
        DUMMY_DATA_POINTS, ImmutableList.of(RelatedData.getDummyRelatedData()));
  }

  public Entity toEntity() {
    Entity anomalyEntity = new Entity(ANOMALY_ENTITY_KIND);
    anomalyEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, timestampDate.toString());
    anomalyEntity.setProperty(METRIC_NAME_PROPERTY, metricName);
    anomalyEntity.setProperty(DIMENSION_NAME_PROPERTY, dimensionName);

    EmbeddedEntity dataPointsEntity = new EmbeddedEntity();
    // Datastore stores numbers as long, so to metricValue should be cast to a long. 
    dataPoints.forEach((timestamp, metricValue) -> 
        dataPointsEntity.setProperty(timestamp.toString(), (long) metricValue.getValue()));
    anomalyEntity.setProperty(DATA_POINTS_PROPERTY, dataPointsEntity);

    List<EmbeddedEntity> relatedDataEntityList = relatedDataList.stream()
        .map(relatedData -> relatedData.toEmbeddedEntity())
        .collect(ImmutableList.toImmutableList());
    anomalyEntity.setProperty(RELATED_DATA_LIST_PROPERTY, relatedDataEntityList);

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
  
  @SuppressWarnings("unchecked")
  public static Anomaly createAnomalyFromEmbeddedEntity(EmbeddedEntity anomalyEmbeddedEntity) {
    EmbeddedEntity dataPointsEE = (EmbeddedEntity) anomalyEmbeddedEntity.getProperty(DATA_POINTS_PROPERTY);
    SortedMap<Timestamp, MetricValue> dataPointsMap = new TreeMap<>();

    if (dataPointsEE != null) {
      for (String key : dataPointsEE.getProperties().keySet()) {
        dataPointsMap.put(new Timestamp(key), new MetricValue(((long) dataPointsEE.getProperty(key))));
      }
    }

    List<EmbeddedEntity> relatedDataEE = (List<EmbeddedEntity>) anomalyEmbeddedEntity.getProperty(RELATED_DATA_LIST_PROPERTY);

    if (relatedDataEE == null) {
      throw new AssertionError("Cannot get property of related data.");
    }

    List<RelatedData> listRelatedData = relatedDataEE.stream()
        .map(relatedData -> RelatedData.createFromEmbeddedEntity(relatedData))
        .collect(ImmutableList.toImmutableList());

    return new Anomaly(new Timestamp((String) anomalyEmbeddedEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY)), 
        (String) anomalyEmbeddedEntity.getProperty(METRIC_NAME_PROPERTY),
        (String) anomalyEmbeddedEntity.getProperty(DIMENSION_NAME_PROPERTY),
        dataPointsMap,
        listRelatedData);
  }

}
