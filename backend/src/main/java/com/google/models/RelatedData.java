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
import java.util.TreeMap;
import java.util.SortedMap;
import java.time.format.DateTimeParseException;

/** 
 * Store related-data and metadata. 
 * This is an immutable class. 
 */
public final class RelatedData {
  public static final String RELATED_DATA_ENTITY_KIND = "relatedData";
  public static final String USERNAME_PROPERTY = "username";
  public static final String METRIC_NAME_PROPERTY = "metricName";
  public static final String DIMENSION_NAME_PROPERTY = "dimensionName";
  public static final String DATA_POINTS_PROPERTY = "dataPoints";

  private static final String INDENT = "  ";
  private static final String INDENT_DOUBLE = "    ";
  private static final String DUMMY_USERNAME = "bob@";
  private static final String DUMMY_METRIC_NAME = "Sample metric name";
  private static final String DUMMY_DIMENSION_NAME = "Sample dimension name";
  private static final Map<Timestamp, MetricValue> DUMMY_DATA_POINTS = ImmutableMap.of( 
      Timestamp.getDummyTimestamp(1), new MetricValue(1), 
      Timestamp.getDummyTimestamp(2), new MetricValue(2), 
      Timestamp.getDummyTimestamp(3), new MetricValue(3));
  
  private final String metricName;
  private final String dimensionName;
  private final Map<Timestamp, MetricValue> dataPoints;
  private final String username;

  public RelatedData(String username, String metricName, String dimensionName, 
      Map<Timestamp, MetricValue> dataPoints) {
    this.username = username;
    this.metricName = metricName;
    this.dimensionName = dimensionName;
    this.dataPoints = ImmutableMap.copyOf(dataPoints);
  }

  public String getUsername() {
    return username;
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

    if (!(o instanceof RelatedData)) {
      return false;
    }

    RelatedData target = (RelatedData) o;

    return target.metricName.equals(metricName)
        && target.dimensionName.equals(dimensionName) 
        && target.username.equals(username)
        && dataPoints.entrySet().stream().allMatch(
          e -> e.getValue().equals(target.dataPoints.get(e.getKey()))
        );
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");
    str.append(INDENT + "Username: " + username + "\n");
    str.append(INDENT + "Metric Name: " + metricName + "\n");
    str.append(INDENT + "Dimension Name: " + dimensionName + "\n");
    str.append(INDENT + "Datapoints: \n");
    dataPoints.forEach((key, value) -> 
      str.append(INDENT_DOUBLE + key + ": " + value + "\n")
    );
    return str.toString();
  }

  public static RelatedData getDummyRelatedData() {
    return new RelatedData(DUMMY_USERNAME, DUMMY_METRIC_NAME, DUMMY_DIMENSION_NAME, 
        DUMMY_DATA_POINTS);
  }

  public Entity toEntity() {
    Entity relatedDataEntity = new Entity(RELATED_DATA_ENTITY_KIND);
    relatedDataEntity.setProperty(METRIC_NAME_PROPERTY, metricName);
    relatedDataEntity.setProperty(DIMENSION_NAME_PROPERTY, dimensionName);
    relatedDataEntity.setProperty(USERNAME_PROPERTY, username);

    EmbeddedEntity dataPointsEntity = new EmbeddedEntity();
    // Datastore stores numbers as long, so to metricValue should be cast to a long. 
    dataPoints.forEach((timestamp, metricValue) -> 
        dataPointsEntity.setProperty(timestamp.toString(), (long) metricValue.getValue()));
    relatedDataEntity.setProperty(DATA_POINTS_PROPERTY, dataPointsEntity);

    return relatedDataEntity;
  }

  /** TODO: Move Entity to Embedded Entity conversion somewhere else that all entity can share. */
  public EmbeddedEntity toEmbeddedEntity() {
    Entity relatedDataEntity = toEntity();
    EmbeddedEntity relatedDataEmbeddedEntity = new EmbeddedEntity();
    relatedDataEmbeddedEntity.setKey(relatedDataEntity.getKey());
    relatedDataEmbeddedEntity.setPropertiesFrom(relatedDataEntity);
    
    return relatedDataEmbeddedEntity;
  }

  public static RelatedData createFromEmbeddedEntity(EmbeddedEntity relatedDataEmbeddedEntity) {
    EmbeddedEntity dataPointsEE = (EmbeddedEntity) relatedDataEmbeddedEntity.getProperty(DATA_POINTS_PROPERTY);
    SortedMap<Timestamp, MetricValue> dataPointsMap = new TreeMap<>();

    if (dataPointsEE != null) {
      for (String key : dataPointsEE.getProperties().keySet()) {
        dataPointsMap.put(new Timestamp(key), new MetricValue(((long) dataPointsEE.getProperty(key))));
      }
    }

    return new RelatedData((String) relatedDataEmbeddedEntity.getProperty(USERNAME_PROPERTY), 
        (String) relatedDataEmbeddedEntity.getProperty(METRIC_NAME_PROPERTY),
        (String) relatedDataEmbeddedEntity.getProperty(DIMENSION_NAME_PROPERTY),
        dataPointsMap);
  }

}
