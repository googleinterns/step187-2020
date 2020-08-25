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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

/** Singleton class to generate related data for a given anomaly. */
public class SimpleRelatedDataGenerator implements RelatedDataGenerator {
  private static final String TARGET_DIMENSION = "Ramen";
  private static final String INTEREST_METRIC = "Interest Over Time";
  private static final String CONFIG_USERNAME = "catyu@";
  // TODO: Replace FILE_LOCATIONS with a shared class that has map of metric to file names.
  private static final ImmutableMap<DataInfo, String> FILE_LOCATIONS = ImmutableMap.of(
                                                        DataInfo.of(INTEREST_METRIC, "Udon"), "/udon-data.csv",
                                                        DataInfo.of(INTEREST_METRIC, "Pho"), "/pho-data.csv"
                                                      );

  private static final SimpleRelatedDataGenerator INSTANCE = new SimpleRelatedDataGenerator();

  private Multimap<DataInfo, DataInfoUser> relatedDataMap;
  private Map<DataInfo, Map<Timestamp, Integer>> csvDataCache;

  private SimpleRelatedDataGenerator() {
    relatedDataMap = ArrayListMultimap.create();
    csvDataCache = new HashMap<>();
    prefillRelatedData(); // TODO: Remove when connected to datastore.
  }

  public static SimpleRelatedDataGenerator createGenerator() {
    return INSTANCE;
  }
  
  public ImmutableList<RelatedData> getRelatedData(DataInfo dataInfo, 
      Timestamp startTime, Timestamp endTime) {
    List<RelatedData> relatedDataList = new ArrayList<RelatedData>();
    
    if (relatedDataMap.containsKey(dataInfo)) {
      for (DataInfoUser relatedTopic : relatedDataMap.get(dataInfo)) {
        Map<Timestamp, MetricValue> dataPointsPlot = 
            getDataPointsInRange(relatedTopic, startTime, endTime);
        
        if (dataPointsPlot.isEmpty()) {
          continue;
        }

        relatedDataList.add(new RelatedData(
            relatedTopic.getUsername(), relatedTopic.getMetricName(), 
            relatedTopic.getDimensionName(), dataPointsPlot)
          );
      }
    }
    // When dataInfo is not found in relatedDataMap, empty list
    // is returned. 
    return ImmutableList.copyOf(relatedDataList);
  }

  /** 
   * Simulates action of querying configs from datastore and adding to 
   * relatedDataMap. Currently, manually adds these relationships.
   */
  private void prefillRelatedData() {
    // TODO: Replace with call for querying configs from datastore.
    // TODO: Deal with capitalizations when querying config from datastore.
    relatedDataMap.put(DataInfo.of(INTEREST_METRIC, "Ramen"), 
        DataInfoUser.of(INTEREST_METRIC, "Udon", CONFIG_USERNAME));
    relatedDataMap.put(DataInfo.of(INTEREST_METRIC, "Ramen"), 
        DataInfoUser.of(INTEREST_METRIC, "Pho", CONFIG_USERNAME));
    // Multimap looks like this right now: 
    // {{Interest Level, Ramen},{{Interest Level, Udon}, {Interest Level, Pho}}}, ...}.
  }

  private ImmutableMap<Timestamp, Integer> getTopicDataPoints(DataInfo topic) {
    return ImmutableMap.copyOf(csvDataCache.computeIfAbsent(topic, key -> CSVParser.parseCSV(
        SimpleRelatedDataGenerator.class.getResourceAsStream(FILE_LOCATIONS.get(key))
      )));
  }

  private ImmutableMap<Timestamp, MetricValue> getDataPointsInRange
      (DataInfo topic, Timestamp startTime, Timestamp endTime) {
    Map<Timestamp, Integer> topicDataPoints = getTopicDataPoints(topic);

    List<Timestamp> listKeys = new ArrayList<Timestamp>(topicDataPoints.keySet());
    int indexOfStart = listKeys.indexOf(startTime);
    int indexOfEnd = listKeys.indexOf(endTime);

    if (indexOfStart == -1 && indexOfEnd == -1) {
      return ImmutableMap.of();
    }
    
    indexOfStart = indexOfStart == -1 ? 0 : indexOfStart;
    indexOfEnd = indexOfEnd == -1 ? listKeys.size() - 1 : indexOfEnd;

    Map<Timestamp, MetricValue> dataPoints = new HashMap<>();
    for (int k = indexOfStart; k <= indexOfEnd; k++) {
      dataPoints.put(listKeys.get(k), new MetricValue(topicDataPoints.get(listKeys.get(k))));
    }

    return ImmutableMap.copyOf(dataPoints);
  }

}