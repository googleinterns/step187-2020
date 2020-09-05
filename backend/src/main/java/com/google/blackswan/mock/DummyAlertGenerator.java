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

import com.google.models.Alert;
import com.google.models.Timestamp;
import java.util.List;
import java.util.ArrayList;

/** Generate list of dummy alerts. Supposed to analyze list of anomalies and create different alerts. */
public class DummyAlertGenerator implements AlertGenerator {
  private static final int SET_ALERT_GROUP_SIZE = 1;

  private List<Alert> alerts;

  public DummyAlertGenerator(AnomalyGenerator anomalyGenerator) {
    alerts = new ArrayList<Alert>();
    for (int k = 0; k < SET_ALERT_GROUP_SIZE; k++) {
      alerts.add(Alert.createAlertWithoutId(Timestamp.getDummyTimestamp(k), 
          anomalyGenerator.getAnomalies(), Alert.StatusType.UNRESOLVED, 
          Alert.PriorityLevel.P2));
    }
  }

  public List<Alert> getAlerts() {
    return alerts;
  }
}
