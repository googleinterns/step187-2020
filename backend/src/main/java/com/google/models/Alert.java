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

/** Store alert-related data. */
public final class Alert {
  public static final String ALERT_ENTITY_KIND = "alert";
  public static final String RESOLVED_STATUS_PROPERTY = "resolvedStatus";
  public static final String ANOMALIES_LIST_PROPERTY = "anomaliesList";
  public static final String RESOLVED_MESSAGE = "resolved";
  public static final String UNRESOLVED_MESSAGE = "unresolved";

  private final Timestamp timestampDate;
  private final List<Anomaly> anomalies;
  private String resolvedStatus;

  public Alert(Timestamp timestampDate, List<Anomaly> anomalies, String resolvedStatus) {
    this.timestampDate = timestampDate;
    this.anomalies = anomalies;
    this.resolvedStatus = resolvedStatus;
  }

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }

  public Timestamp getTimestamp() {
    return timestampDate;
  }

  public String getResolvedStatus() {
    return resolvedStatus;
  }

  public void setResolvedStatus(String resolvedStatus) {
    // TODO: Check whether resolvedStatus is valid.
    this.resolvedStatus = resolvedStatus;
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

    return target.timestampDate.equals(timestampDate) && target.resolvedStatus.equals(resolvedStatus)
        && target.anomalies.equals(anomalies);
  }

  public static Entity toEntity(Alert alert) {
    Entity alertEntity = new Entity(ALERT_ENTITY_KIND);
    alertEntity.setProperty(Timestamp.TIMESTAMP_PROPERTY, alert.timestampDate.toString());
    alertEntity.setProperty(RESOLVED_STATUS_PROPERTY, alert.resolvedStatus);

    List<EmbeddedEntity> list = new ArrayList<EmbeddedEntity>();

    alert.anomalies.forEach(anomaly -> list.add(Anomaly.toEmbeddedEntity(anomaly)));

    alertEntity.setProperty(ANOMALIES_LIST_PROPERTY, list);

    return alertEntity;
  }

  @SuppressWarnings("unchecked")
  public static Alert toAlert(Entity alertEntity) throws Exception {
    List<EmbeddedEntity> list = (List<EmbeddedEntity>) alertEntity.getProperty(ANOMALIES_LIST_PROPERTY);

    List<Anomaly> listAnomaly = new ArrayList<Anomaly>();
    if (list != null) {
      list.forEach(embeddedAnomaly -> listAnomaly.add(Anomaly.toAnomaly(embeddedAnomaly)));
    }

    return new Alert(new Timestamp((String) alertEntity.getProperty(Timestamp.TIMESTAMP_PROPERTY)), 
        listAnomaly, 
        (String) alertEntity.getProperty(RESOLVED_STATUS_PROPERTY));
  }

}
