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


/** 
 * Wrapper for meta data of a related data type used in RelatedDataGenerator.
 * This is an immutable class.  
 */
public final class DataInfo {
  
  private final String metricName;
  private final String dimensionName;
  private final String username;

  /** TODO: Change username to List to deal with multiple users requesting same related data. */
  public DataInfo(String metricName, String dimensionName, String username) {
    this.metricName = metricName;
    this.dimensionName = dimensionName;
    this.username = username;
  }

  public String getMetricName() {
    return metricName;
  }

  public String getDimensionName() {
    return dimensionName;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("");
    str.append("Metric Name: " + metricName + "\n");
    str.append("Dimension Name: " + dimensionName + "\n");
    str.append("Username: " + username + "\n");
    return str.toString();
  }

  @Override
   public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof DataInfo)) {
      return false;
    }

    DataInfo target = (DataInfo) o;

    return target.metricName == metricName &&
        target.dimensionName == dimensionName;
  }

}
