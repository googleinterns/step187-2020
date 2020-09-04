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

import com.google.blackswan.mock.filesystem.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import com.google.models.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/** Singleton class to generate related data for a given anomaly. */
public class SimpleRelatedDataGenerator implements RelatedDataGenerator {
  private static final String CONFIG_USERNAME = "catyu@";
  /** Ideally should be injected. Currently, putting as class variable. */
  private static final FileSystem FILE_SYSTEM = LocalFileSystem.createSystem();
  private static final SimpleRelatedDataGenerator INSTANCE = new SimpleRelatedDataGenerator();

  private Multimap<DataInfo, DataInfoUser> relatedDataMap;
  private Map<DataInfo, Map<Timestamp, Integer>> csvDataCache;

  private SimpleRelatedDataGenerator() {
    relatedDataMap = HashMultimap.create();
    csvDataCache = new HashMap<>();
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
            getDataPointsInRange(relatedTopic.getDataInfo(), startTime, endTime);
        
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
   * Call before every new cron job execution to fetch most up to date related
   * data requests and clear cache and old requests.
   */
  public void clearCacheAndFillRelatedData() {
    relatedDataMap.clear();
    csvDataCache.clear();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(Constant.CONFIG_ENTITY_KIND);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      relatedDataMap.put(DataInfo.createFromEntity(entity), 
          DataInfoUser.createFromEntity(entity));
    }
  }

  /** 
   * DEPRECATED, used for filling up dummy related data requests when not connected
   * to datastore.
   */
  private void prefillRelatedData() {
    relatedDataMap.put(DataInfo.of(Constant.INTEREST_US, Constant.RAMEN), 
        DataInfoUser.of(Constant.INTEREST_US, Constant.UDON, CONFIG_USERNAME));
    relatedDataMap.put(DataInfo.of(Constant.INTEREST_US, Constant.RAMEN), 
        DataInfoUser.of(Constant.INTEREST_US, Constant.PHO, CONFIG_USERNAME));
    // Multimap looks like this right now: 
    // {{Interest Level, Ramen},{{Interest Level, Udon}, {Interest Level, Pho}}}, ...}.
  }

  private ImmutableMap<Timestamp, Integer> getTopicDataPoints(DataInfo topic) {
    return ImmutableMap.copyOf(csvDataCache.computeIfAbsent(topic, key -> CSVParser.parseCSV(
        FILE_SYSTEM.getDataAsStream(topic)
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
