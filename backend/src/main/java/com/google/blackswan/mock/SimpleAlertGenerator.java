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

package com.google.blackswan.mock;

import com.google.models.*;
import java.util.*;
import java.util.Collections;

/** Generate list of alerts by grouping anomalies by there month. */
public class SimpleAlertGenerator implements AlertGenerator {

  private List<Alert> alerts;
  private AnomalyGenerator anomalyGenerator;

  public SimpleAlertGenerator(AnomalyGenerator anomalyGenerator) {
    this.alerts = new ArrayList<Alert>();
    this.anomalyGenerator = anomalyGenerator;
    groupAnomaliesToAlerts();
  }

  /** TODO: Make flexible so can not only group anomalies by month but other time span. */
  private void groupAnomaliesToAlerts() {
    List<Anomaly> anomaliesList = anomalyGenerator.getAnomalies();
    Map<Timestamp, List<Anomaly>> anomalyGroups = new HashMap<>();

    // Group anomalies by month.
    for (Anomaly currAnomaly : anomaliesList) {
      Timestamp keyTimestamp = currAnomaly.getTimestamp().getFirstDayOfNextMonth();
      if (anomalyGroups.get(keyTimestamp) == null) {
        anomalyGroups.put(keyTimestamp, new ArrayList<>(Arrays.asList(currAnomaly)));
      } else {
        anomalyGroups.get(keyTimestamp).add(currAnomaly);
      }
    }

    // Create an alert for each month where there are anomalies.
    for (Map.Entry<Timestamp, List<Anomaly>> entry : anomalyGroups.entrySet()) {
      alerts.add(new Alert(entry.getKey(), entry.getValue(), Alert.StatusType.UNRESOLVED));
    }
  }

  public List<Alert> getAlerts() {
    return alerts;
  }
}
