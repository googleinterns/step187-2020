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

/** Generate list of dummy alerts. Supposed to analyze list of anomalies and create different alerts. */
public class DummyAlertGenerator implements AlertGenerator {
  private static final int SET_ALERT_GROUP_SIZE = 1;

  private List<Alert> alerts;
  private AnomalyGenerator anomalyGenerator;

  public DummyAlertGenerator() {
    anomalyGenerator = new DummyAnomalyGenerator();
    alerts = new ArrayList<Alert>();
    alerts.add(new Alert(Timestamp.getDummyTimestamp(1), anomalyGenerator.getAnomalies(), 
        Alert.UNRESOLVED_STATUS));
  }

  public List<Alert> getAlerts() {
    return alerts;
  }
}
