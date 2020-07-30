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

public class DummyAnomalyGenerator implements AnomalyGenerator {
  private static final int SET_ANOMALY_GROUP_SIZE = 5;

  private List<Anomaly> anomalies;

  public DummyAnomalyGenerator() {
    anomalies = new ArrayList<Anomaly>();
    for (int k = 0; k < SET_ANOMALY_GROUP_SIZE; k++) {
      anomalies.add(Anomaly.getDummyAnomaly());
    }
  }

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }
}