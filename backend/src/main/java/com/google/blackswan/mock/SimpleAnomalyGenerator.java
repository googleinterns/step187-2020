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
import java.util.Scanner;
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

/** Generate list of anomalies based on data in the csv file using average and threshold. */
public class SimpleAnomalyGenerator implements AnomalyGenerator {
  private static final String DATA_FILE_LOCATION = "/sample-ramen-data.csv";
  private static final String EXCEPTION_MESSAGE = "Invalid row format.";
  private static final String COMMA_DELIMITER = ",";
  private static final int THRESHOLD = 13;
  private static final int NUM_POINTS = 5;

  private final ImmutableList<Anomaly> anomalies;

  private SimpleAnomalyGenerator(InputStream source, int threshold, 
      int numDataPoints) {
    anomalies = generateAnomalies(CSVParser.parseCSV(source), threshold, numDataPoints);
  }

  private static ImmutableList<Anomaly> generateAnomalies
      (Map<Timestamp, Integer> data, int threshold, int numDataPoints) {
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
        .map(key -> createAnomalyFromDataPoint(key, data, numDataPoints))
        .collect(ImmutableList.toImmutableList());
  }

  /** 
   * TODO: Depending on size of future data, alter algorithm of finding 
   * associated data points. 
   */
  private static Anomaly createAnomalyFromDataPoint
      (Timestamp time, Map<Timestamp, Integer> data, int numDataPoints) {
    // TODO: Make const or obtain metric/dimension name from csv file.
    String metricName = "Interest Over Time";
    String dimensionName = "Ramen";

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
        .getRelatedData(DataInfo.of(metricName, dimensionName), 
                        listKeys.get(firstDataPointIndex),
                        listKeys.get(lastDataPointIndex));

    return new Anomaly(time, metricName, dimensionName, dataPoints, relatedDataList);
  }

  /** TODO: Specify which metric and dimension data to look at for this generator. */
  public static SimpleAnomalyGenerator createGenerator() {
    return new SimpleAnomalyGenerator(
      SimpleAnomalyGenerator.class.getResourceAsStream(DATA_FILE_LOCATION),
      THRESHOLD,
      NUM_POINTS
    );
  }

  /** Use mainly in testing to have custom input as csv data. */
  public static SimpleAnomalyGenerator createGeneratorWithString(String input, 
      int threshold, int numDataPoints) {
    return new SimpleAnomalyGenerator(
      new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
      threshold,
      numDataPoints
    );
  }

  /** TODO: Add more creation methods if needed with different parameters. */

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }
}
