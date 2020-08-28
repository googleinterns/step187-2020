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

  public static DataInfo of(String metricName, String dimensionName) {
    return new DataInfo(metricName, dimensionName);
  }

  public DataInfo(String metricName, String dimensionName) {
    this.metricName = metricName;
    this.dimensionName = dimensionName;
  }

  public String getMetricName() {
    return metricName;
  }

  public String getDimensionName() {
    return dimensionName;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Metric Name: ").append(metricName).append("\n")
        .append("Dimension Name: ").append(dimensionName).append("\n")
        .toString();
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
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

    return target.metricName.equals(metricName) &&
        target.dimensionName.equals(dimensionName);
  }

}
