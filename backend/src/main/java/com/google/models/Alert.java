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

import com.google.models.Anomaly;
import com.google.models.Timestamp;
import java.util.List;


/** Store alert-related data. */
public final class Alert {
  
  private final Timestamp timestampDate;
  private final List<Anomaly> anomalies;
  private final String resolvedStatus;

  public Alert(Timestamp timestampDate, List<Anomaly> anomalies, String resolvedStatus) {
    this.timestampDate = timestampDate;
    this.anomalies = anomalies;
    this.resolvedStatus = resolvedStatus;
  }

}
