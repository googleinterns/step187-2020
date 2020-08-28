// Copyright 2020 Google LLC
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
import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.LinkedListMultimap;

/** Generate list of alerts of topics requested by grouping anomalies by their month. */
public class AdvanceAlertGenerator implements AlertGenerator {

  private final ImmutableList<Alert> alerts;

  public AdvanceAlertGenerator(List<DataInfo> topics) {
    this.alerts = groupAnomaliesToAlerts(topics);
  }

  private static ImmutableList<Alert> groupAnomaliesToAlerts(List<DataInfo> topics) {
    // TODO: Move this elsewhere.
    SimpleRelatedDataGenerator.createGenerator().fillUpdatedRelatedData();
    List<Anomaly> anomaliesList = topics.stream()
        .flatMap(topic -> SimpleAnomalyGenerator.createGenerator(topic).getAnomalies().stream())
        .collect(ImmutableList.toImmutableList());
    
    ListMultimap<Timestamp, Anomaly> anomalyGroups = LinkedListMultimap.create();

    // Group anomalies by month.
    for (Anomaly currAnomaly : anomaliesList) {
      Timestamp keyTimestamp = currAnomaly.getTimestamp().getFirstDayOfNextMonth();
      anomalyGroups.put(keyTimestamp, currAnomaly);
    }

    return anomalyGroups.keySet().stream()
        .map(key -> Alert.createAlertWithoutId(key, anomalyGroups.get(key), Alert.StatusType.UNRESOLVED))
        .collect(ImmutableList.toImmutableList());
  }

  public List<Alert> getAlerts() {
    return alerts;
  }
}
