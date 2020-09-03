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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.common.collect.ImmutableList;
import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.OptionalLong;

/** Store alert-related data. */
public final class Alert {
  public static final String ALERT_ENTITY_KIND = "alert";
  public static final String STATUS_PROPERTY = "status";
  public static final String PRIORITY_PROPERTY = "priority";
  public static final String ANOMALIES_LIST_PROPERTY = "anomaliesList";
  public static enum StatusType {
    RESOLVED,
    UNRESOLVED
  };
  public static enum PriorityLevel {
    P0,
    P1,
    P2
  };

  private static final long DEFAULT_ID = 0L;

  private final Timestamp timestampDate;
  private final ImmutableList<Anomaly> anomalies;
  private final OptionalLong id;
  private StatusType status;
  private PriorityLevel priority;

  private Alert(Timestamp timestampDate, List<Anomaly> anomalies, StatusType status, 
      PriorityLevel priority, OptionalLong id) {
    this.timestampDate = timestampDate;
    this.anomalies = ImmutableList.copyOf(anomalies);
    this.status = status;
    this.priority = priority;
    this.id = id;
  }

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }

  public Timestamp getTimestamp() {
    return timestampDate;
  }

  public StatusType getStatus() {
    return status;
  }

  public PriorityLevel getPriority() {
    return priority;
  }

  public long getAlertId() {
    return id.orElse(DEFAULT_ID);
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder()
        .append("Timestamp: ").append(timestampDate).append("\n")
        .append("Status: ").append(status.name()).append("\n")
        .append("Priority: ").append(priority.name()).append("\n")
        .append("Anomalies: \n");
    anomalies.forEach(str::append);
    return str.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Alert)) {
      return false;
    }

    Alert target = (Alert) o;

    return target.timestampDate.equals(timestampDate) 
        && target.status.equals(status)
        && target.priority.equals(priority)
        && target.anomalies.equals(anomalies)
        && target.getAlertId() == getAlertId();
  }

  public Entity toEntity() {
    Entity alertEntity = new Entity(ALERT_ENTITY_KIND);
    alertEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, timestampDate.toEpochDay());
    alertEntity.setProperty(STATUS_PROPERTY, status.name());
    alertEntity.setProperty(PRIORITY_PROPERTY, priority.name());

    List<EmbeddedEntity> list = anomalies.stream()
        .map(Anomaly::toEmbeddedEntity)
        .collect(ImmutableList.toImmutableList());

    alertEntity.setProperty(ANOMALIES_LIST_PROPERTY, list);

    return alertEntity;
  }

  /** Used when an alert is first created and not converted from an entity. */
  public static Alert createAlertWithoutId(Timestamp timestampDate, 
      List<Anomaly> anomalies, StatusType status, PriorityLevel priority) {
    return new Alert(timestampDate, anomalies, status, priority, OptionalLong.empty());
  }

  @SuppressWarnings("unchecked")
  public static Alert createAlertFromEntity(Entity alertEntity) {
    List<EmbeddedEntity> listEE = 
        (List<EmbeddedEntity>) alertEntity.getProperty(ANOMALIES_LIST_PROPERTY);

    if (listEE == null) {
      throw new AssertionError("No list of anomaly embedded entity found.");
    }

    List<Anomaly> listAnomaly = listEE.stream()
        .map(Anomaly::createAnomalyFromEmbeddedEntity)
        .collect(ImmutableList.toImmutableList());

    return new Alert(
      Timestamp.of((long) alertEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY)), 
      listAnomaly, 
      StatusType.valueOf((String) alertEntity.getProperty(STATUS_PROPERTY)),
      PriorityLevel.valueOf((String) alertEntity.getProperty(PRIORITY_PROPERTY)),
      OptionalLong.of(alertEntity.getKey().getId())
    );
  }

}
