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
  public static final String INTEREST_US = "Interest Over Time - US";
  public static final String UDON = "Udon";
  public static final String PHO = "Pho";
  public static final String RAMEN = "Ramen";
  // TODO: Add more const for other metrics.

  public static final ImmutableMap<DataInfo, String> FILE_LOCATIONS 
      = ImmutableMap.of(
          DataInfo.of(INTEREST_US, UDON), "udon-data.csv",
          DataInfo.of(INTEREST_US, PHO), "pho-data.csv",
          DataInfo.of(INTEREST_US, RAMEN), "interest-ramen.csv"
        );

  // Cloud storage related constants.
  public static final String KEY_LOCATION = "keys/key.json";
  public static final String PROJECT_ID = "greyswan";
  public static final String BUCKET_NAME = "greyswan.appspot.com";

  /** No instances. */
  private Constant() {}
}
