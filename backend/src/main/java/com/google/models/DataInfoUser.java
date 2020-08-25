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
 * Wrapper for meta data of a related data type that includes user who
 * requests the data.
 * This is an immutable class.  
 */
public class DataInfoUser {
  
  private final String username;
  private final DataInfo dataInfo;

  public static DataInfoUser of(String metricName, 
      String dimensionName, String username) {
    return new DataInfoUser(metricName, dimensionName, username);
  }

  /** TODO: Change username to List to deal with multiple users requesting same related data. */
  public DataInfoUser(String metricName, String dimensionName, String username) {
    this.dataInfo = DataInfo.of(metricName, dimensionName);
    this.username = username;
  }

  public DataInfo getDataInfo() {
    return dataInfo;
  }

  public String getMetricName() {
    return dataInfo.getMetricName();
  }

  public String getDimensionName() {
    return dataInfo.getDimensionName();
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String toString() {
    return new StringBuilder(dataInfo.toString())
        .append("Username: ").append(username).append("\n")
        .toString();
  }

}
