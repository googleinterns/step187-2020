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
import com.google.common.collect.HashMultimap;
import com.google.blackswan.mock.filesystem.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/** Singleton class to generate related data for a given anomaly. */
public class SimpleRelatedDataGenerator implements RelatedDataGenerator {
  private static final String CONFIG_USERNAME = "catyu@";

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
        System.out.println(relatedDataMap.get(dataInfo).size());
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

  /** TODO: Find good place to call this. */
  public void fillUpdatedRelatedData() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(Const.CONFIG_ENTITY_KIND);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      relatedDataMap.put(toDataInfo(entity), toDataInfoUser(entity));
    }
  }

  /** 
   * Simulates action of querying configs from datastore and adding to 
   * relatedDataMap. Currently, manually adds these relationships.
   */
  private void prefillRelatedData() {
    // TODO: Replace with call for querying configs from datastore.
    // TODO: Deal with capitalizations when querying config from datastore.
    relatedDataMap.put(DataInfo.of(Const.INTEREST_US, Const.RAMEN), 
        DataInfoUser.of(Const.INTEREST_US, Const.UDON, CONFIG_USERNAME));
    relatedDataMap.put(DataInfo.of(Const.INTEREST_US, Const.RAMEN), 
        DataInfoUser.of(Const.INTEREST_US, Const.PHO, CONFIG_USERNAME));
    // Multimap looks like this right now: 
    // {{Interest Level, Ramen},{{Interest Level, Udon}, {Interest Level, Pho}}}, ...}.
  }

  private ImmutableMap<Timestamp, Integer> getTopicDataPoints(DataInfo topic, Timestamp startTime, Timestamp endTime) {
    if (csvDataCache.containsKey(topic) && csvDataCache.get(topic).get(startTime) != null 
        && csvDataCache.get(topic).get(endTime) != null) {
      return ImmutableMap.copyOf(csvDataCache.get(topic));
    }

    csvDataCache.put(topic, CSVParser.parseCSV(
        CloudFileSystem.createSystem().getDataAsStream(topic)
      ));
    return ImmutableMap.copyOf(csvDataCache.get(topic));
    // return ImmutableMap.copyOf(csvDataCache.computeIfAbsent(topic, key -> CSVParser.parseCSV(
    //     CloudFileSystem.createSystem().getDataAsStream(topic)
    //   )));
  }

  private ImmutableMap<Timestamp, MetricValue> getDataPointsInRange
      (DataInfo topic, Timestamp startTime, Timestamp endTime) {
    Map<Timestamp, Integer> topicDataPoints = getTopicDataPoints(topic, startTime, endTime);

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

  private DataInfo toDataInfo(Entity entity) {
    return DataInfo.of(
      (String) entity.getProperty(Const.CONFIG_METRIC_PROPERTY),
      (String) entity.getProperty(Const.CONFIG_DIMENSION_PROPERTY)
    );
  }

  private DataInfoUser toDataInfoUser(Entity entity) {
    return DataInfoUser.of(
      (String) entity.getProperty(Const.CONFIG_RMETRIC_PROPERTY),
      (String) entity.getProperty(Const.CONFIG_RDIMENSION_PROPERTY),
      (String) entity.getProperty(Const.CONFIG_USER_PROPERTY)
    );
  }

}
