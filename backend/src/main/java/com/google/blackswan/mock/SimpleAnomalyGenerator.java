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

import static java.util.stream.Collectors.toMap;

import com.google.models.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.AbstractMap;
import java.util.logging.Logger;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.lang.Math;
import java.lang.Object;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.blackswan.mock.filesystem.*;

/** Generate list of anomalies based on data in the csv file using average and threshold. */
public class SimpleAnomalyGenerator implements AnomalyGenerator {
  private static final int THRESHOLD = 13;
  private static final int NUM_POINTS = 5;

  private final ImmutableList<Anomaly> anomalies;
  private final DataInfo topic;
  private final ImmutableMap<Timestamp, Integer> data;

  public static SimpleAnomalyGenerator createGenerator(DataInfo topic) {
    return new SimpleAnomalyGenerator(
      topic,
      // For [push] to git, always use LocalFileSystem, as CloudFileSystem will fail 
      // unit test without access to key.json. 
      LocalFileSystem.createSystem().getDataAsStream(topic),
      THRESHOLD,
      NUM_POINTS
    );
  }

  /** Use mainly in testing to have custom input as csv data. */
  public static SimpleAnomalyGenerator createGeneratorWithString(DataInfo topic,
      String input, int threshold, int numDataPoints) {
    return new SimpleAnomalyGenerator(
      topic,
      new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
      threshold,
      numDataPoints
    );
  }

  private SimpleAnomalyGenerator(DataInfo topic, InputStream source, int threshold, 
      int numDataPoints) {
    this.topic = topic;
    this.data = ImmutableMap.copyOf(CSVParser.parseCSV(source));
    this.anomalies = generateAnomalies(threshold, numDataPoints);
  }

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }

  private ImmutableList<Anomaly> generateAnomalies(int threshold, int numDataPoints) {
    int avg = data.values().stream().reduce(0, Integer::sum) / data.size();

    // Find instances where exceed threshold.
    Map<Timestamp, Integer> anomalyPoints = data.entrySet().stream()
        .filter(entry -> entry.getValue() - avg > threshold)
        .collect(toMap(
            entry -> entry.getKey(), 
            entry -> entry.getValue(),
            // In case of conflicting keys, pick value of second key. 
            // But there should not be conflicting keys at all. 
            (x, y) -> y,
            LinkedHashMap::new
          ));

    // Create anomaly objects from those instances.
    return anomalyPoints.keySet().stream()
        .map(key -> createAnomalyFromDataPoint(key, numDataPoints))
        .collect(ImmutableList.toImmutableList());
  }

  /** 
   * TODO: Depending on size of future data, alter algorithm of finding 
   * associated data points. 
   */
  private Anomaly createAnomalyFromDataPoint(Timestamp time, int numDataPoints) {

    // Convert keys into ArrayList.
    List<Timestamp> listKeys = new ArrayList<Timestamp>(data.keySet());
    int indexOfAnomaly = listKeys.indexOf(time); // Potentially slow operation.
    if (indexOfAnomaly == -1) {
      // Should never happen since listKeys is converted from keys.
      throw new AssertionError("Key does not exist in list.");
    }

    // Index of data points to include with anomaly calculated by using index of 
    // current anomaly +- numDataPoints to include. If the index goes out of bound
    // then do not include out of bound indices. 
    int firstDataPointIndex = (indexOfAnomaly - numDataPoints >= 0) 
        ? indexOfAnomaly - numDataPoints : 0;
    int lastDataPointIndex = (indexOfAnomaly + numDataPoints < data.size()) 
        ? indexOfAnomaly + numDataPoints : data.size() - 1;

    Map<Timestamp, MetricValue> dataPoints = new HashMap<>();
    for (int k = firstDataPointIndex; k <= lastDataPointIndex; k++) {
      dataPoints.put(listKeys.get(k), new MetricValue(data.get(listKeys.get(k))));
    }

    List<RelatedData> relatedDataList = SimpleRelatedDataGenerator.createGenerator()
        .getRelatedData(topic, 
                        listKeys.get(firstDataPointIndex),
                        listKeys.get(lastDataPointIndex));

    return new Anomaly(time, topic.getMetricName(), topic.getDimensionName(), dataPoints, relatedDataList);
  }

}
