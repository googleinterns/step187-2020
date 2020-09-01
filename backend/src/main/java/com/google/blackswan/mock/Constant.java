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

import com.google.models.DataInfo;
import com.google.common.collect.ImmutableMap;

/** Constants used throughout the blackswan mock classes. */
public final class Constant {
  // TODO: Create based on external config file (in the future).
  public static final String INTEREST_US = "Interest Over Time - US";
  public static final String INTEREST_JP = "Interest Over Time - JP";
  public static final String INTEREST_UK = "Interest Over Time - UK";
  public static final String UDON = "Udon";
  public static final String PHO = "Pho";
  public static final String RAMEN = "Ramen";

  // TODO: Build with builder or for-loop.
  public static final ImmutableMap<DataInfo, String> FILE_LOCATIONS 
      = ImmutableMap.<DataInfo, String>builder()
          .put(DataInfo.of(INTEREST_US, UDON), "udon-us--data.csv")
          .put(DataInfo.of(INTEREST_US, PHO), "pho-us--data.csv")
          .put(DataInfo.of(INTEREST_US, RAMEN), "ramen-us--data.csv")
          .put(DataInfo.of(INTEREST_JP, RAMEN), "ramen-jp--data.csv")
          .put(DataInfo.of(INTEREST_JP, PHO), "pho-jp--data.csv")
          .put(DataInfo.of(INTEREST_JP, UDON), "udon-jp--data.csv")
          .put(DataInfo.of(INTEREST_UK, RAMEN), "ramen-gb--data.csv")
          .put(DataInfo.of(INTEREST_UK, PHO), "pho-gb--data.csv")
          .put(DataInfo.of(INTEREST_UK, UDON), "udon-gb--data.csv")
          .build();

  // Cloud storage related constants.
  public static final String KEY_LOCATION = "keys/key.json";
  public static final String PROJECT_ID = "greyswan";
  public static final String BUCKET_NAME = "greyswan.appspot.com";

  // Configuration Entity related constants.
  public static final String CONFIG_ENTITY_KIND = "Configuration";
  public static final String CONFIG_METRIC_PROPERTY = "metric";
  public static final String CONFIG_DIMENSION_PROPERTY = "dimension";
  public static final String CONFIG_RMETRIC_PROPERTY = "relatedMetric";
  public static final String CONFIG_RDIMENSION_PROPERTY = "relatedDimension";
  public static final String CONFIG_USER_PROPERTY = "user";

  /** No instances. */
  private Constant() {}
}
