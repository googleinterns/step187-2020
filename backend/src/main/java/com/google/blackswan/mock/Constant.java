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
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.HashMap;

/** Constants used throughout the blackswan mock classes. */
public final class Constant {
  // Only set to false, when you're pushing to Github or do 
  // not have access to key.json. 
  public static final boolean USE_CLOUD_STORAGE = false;
  
  // Constants used to filenames. 
  private static final String FILE_DELIMITER = "-";
  private static final String FILE_END = "--data.csv";
  private static final String US_COUNTRY = "us";
  private static final String JP_COUNTRY = "jp";
  private static final String UK_COUNTRY = "gb";

  // TODO: Create based on external config file (in the future).
  public static final String INTEREST = "Interest Over Time - ";
  public static final String INTEREST_US = INTEREST + US_COUNTRY.toUpperCase();
  public static final String INTEREST_JP = INTEREST + JP_COUNTRY.toUpperCase();
  public static final String INTEREST_UK = INTEREST + UK_COUNTRY.toUpperCase();
  public static final String UDON = "Udon";
  public static final String PHO = "Pho";
  public static final String RAMEN = "Ramen";

  // Constants used to generate filenames map. 
  private static final ImmutableList<String> COUNTRIES_LIST = 
      ImmutableList.of(US_COUNTRY, JP_COUNTRY, UK_COUNTRY);
  private static final ImmutableList<String> DIMENSIONS = 
      ImmutableList.of(UDON, PHO, RAMEN);

  public static final ImmutableMap<DataInfo, String> FILE_LOCATIONS = buildFileLocations();

  private static ImmutableMap<DataInfo, String> buildFileLocations() {
    Map<DataInfo, String> result = new HashMap<>();
    for (String country : COUNTRIES_LIST) {
      for (String dimension : DIMENSIONS) {
        String filename = dimension.toLowerCase() + FILE_DELIMITER + country + FILE_END;
        String metric = INTEREST + country.toUpperCase();
        result.put(DataInfo.of(metric, dimension), filename);
      }
    }
    return ImmutableMap.copyOf(result);
  }

  // Cloud storage related constants.
  public static final String KEY_LOCATION = "keys/key.json";
  public static final String PROJECT_ID = "greyswan";
  public static final String BUCKET_NAME = "greyswan.appspot.com";

  // Configuration Entity related constants.
  public static final String CONFIG_ENTITY_KIND = "Configuration";
  public static final String CONFIG_METRIC_PROPERTY = "metric";
  public static final String CONFIG_DIMENSION_PROPERTY = "dimension";
  public static final String CONFIG_RELATED_METRIC_PROPERTY = "relatedMetric";
  public static final String CONFIG_RELATED_DIMENSION_PROPERTY = "relatedDimension";
  public static final String CONFIG_USER_PROPERTY = "user";

  /** No instances. */
  private Constant() {}
}
