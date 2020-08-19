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
import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.OptionalLong;

/** Store alert-related data. */
public final class Alert {
  public static final String ALERT_ENTITY_KIND = "alert";
  public static final String STATUS_PROPERTY = "status";
  public static final String ANOMALIES_LIST_PROPERTY = "anomaliesList";
  public static enum StatusType {
    RESOLVED,
    UNRESOLVED
  };

  private static final long DEFAULT_ID = 0L;

  private final Timestamp timestampDate;
  private final List<Anomaly> anomalies;
  private final OptionalLong id;
  private StatusType status;

  private Alert(Timestamp timestampDate, List<Anomaly> anomalies, StatusType status, OptionalLong id) {
    this.timestampDate = timestampDate;
    this.anomalies = new ArrayList<>(anomalies);
    this.status = status;
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

  public long getAlertId() {
    return id.orElse(DEFAULT_ID);
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");
    str.append("Timestamp: " + timestampDate + "\n");
    str.append("Status: " + status.name() + "\n");
    str.append("Anomalies: \n");
    anomalies.forEach(anomaly -> str.append(anomaly.toString()));
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
        && target.anomalies.equals(anomalies)
        && target.getAlertId() == getAlertId();
  }

  public Entity toEntity() {
    Entity alertEntity = new Entity(ALERT_ENTITY_KIND);
    alertEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, timestampDate.toString());
    alertEntity.setProperty(STATUS_PROPERTY, status.name());

    List<EmbeddedEntity> list = new ArrayList<EmbeddedEntity>();
    anomalies.forEach(anomaly -> list.add(anomaly.toEmbeddedEntity()));

    alertEntity.setProperty(ANOMALIES_LIST_PROPERTY, list);

    return alertEntity;
  }

  /** Used when an alert is first created and not converted from an entity. */
  public static Alert createAlertWithoutId(Timestamp timestampDate, 
      List<Anomaly> anomalies, StatusType status) {
    return new Alert(timestampDate, anomalies, status, OptionalLong.empty());
  }

  @SuppressWarnings("unchecked")
  public static Alert createAlertFromEntity(Entity alertEntity) {
    List<EmbeddedEntity> listEE = 
        (List<EmbeddedEntity>) alertEntity.getProperty(ANOMALIES_LIST_PROPERTY);

    List<Anomaly> listAnomaly = new ArrayList<Anomaly>();
    if (listEE != null) {
      listEE.forEach(
        embeddedAnomaly -> listAnomaly.add(
          Anomaly.createAnomalyFromEmbeddedEntity(embeddedAnomaly)
        )
      );
    }

    return new Alert(
      new Timestamp(
        (String) alertEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY)
      ), 
      listAnomaly, 
      StatusType.valueOf((String) alertEntity.getProperty(STATUS_PROPERTY)),
      OptionalLong.of(alertEntity.getKey().getId())
    );
  }

}
