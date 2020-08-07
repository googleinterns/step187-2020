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
import java.util.*;
import java.text.ParseException;
import java.util.logging.Logger;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.lang.Math;
import java.lang.Object;

/** Generate list of anomalies based data in the csv file using average and threshold. */
public class SimpleAnomalyGenerator implements AnomalyGenerator {
  private static final String DATA_FILE_LOCATION = "/sample-ramen-data.csv";
  private static final String EXCEPTION_MESSAGE = "Invalid row format.";
  private static final Logger log = Logger.getLogger(SimpleAnomalyGenerator.class.getName());
  private static final int THRESHOLD = 13;
  private static final int NUM_POINTS = 5;

  private List<Anomaly> anomalies;
  private Map<Timestamp, Integer> data;

  public SimpleAnomalyGenerator() {
    anomalies = new ArrayList<Anomaly>();
    data = new LinkedHashMap<>();
    parseCSV();
    generateAnomalies();
  }

  private void generateAnomalies() {
    int sum = 0;
    for (int val : data.values()) {
      sum += val;
    }
    int ave = sum / data.size();

    // Find instances where exceed threshold.
    Map<Timestamp, Integer> anomalyPoints = data.entrySet().stream()
        .filter(e -> e.getValue() - ave > THRESHOLD)
        .collect(toMap(e -> e.getKey(), e -> e.getValue()));

    // Create anomaly objects from those instances.
    for (Map.Entry<Timestamp, Integer> entry : anomalyPoints.entrySet()) {
      anomalies.add(createAnomalyFromDataPoint(entry.getKey()));
    }
  }

  /** TODO: Depending on size of future data, alter algorithm of finding associated data points. */
  private Anomaly createAnomalyFromDataPoint(Timestamp time) {
    // TODO: Make const or obtain metric/dimension name from csv file.
    String metricName = "Interest Over Time";
    String dimensionName = "Ramen";

    // Convert keys into ArrayList.
    Set<Timestamp> keys = data.keySet();
    List<Timestamp> listKeys = new ArrayList<Timestamp>(keys);
    int indexOfAnomaly = listKeys.indexOf(time); // Potentially slow operation.
    if (indexOfAnomaly == -1) {
      // Should never happen since listKeys is converted from keys.
      throw new AssertionError("Key does not exist in list.");
    }

    int firstDataPointIndex = (indexOfAnomaly - NUM_POINTS >= 0) ? indexOfAnomaly - NUM_POINTS : 0;
    int lastDataPointIndex = (indexOfAnomaly + NUM_POINTS < data.size()) ? indexOfAnomaly + NUM_POINTS : data.size() - 1;

    Map<Timestamp, MetricValue> dataPoints = new HashMap<>();
    for (int k = firstDataPointIndex; k <= lastDataPointIndex; k++) {
      dataPoints.put(listKeys.get(k), new MetricValue(data.get(listKeys.get(k))));
    }

    return new Anomaly(time, metricName, dimensionName, dataPoints);
  }

  private void parseCSV() {
    Scanner scanner = new Scanner(SimpleAnomalyGenerator.class.getResourceAsStream(DATA_FILE_LOCATION));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      // Parse a row of data, if contain invalid format, skip to next row.
      try {
        parseRow(line);
      } catch (ParseException e) {
        // Catch ParseException, but keep scanning.
        continue;
      }
    }
    scanner.close();
  }

  /** Each row of csv has following format: { yyyy-mm-dd, popularity }. */
  private void parseRow(String row) throws ParseException {
    String[] cells = row.split(",");
    if (cells.length != 2) {
      log.warning("Cannot parse row: " + row);
      throw new ParseException(EXCEPTION_MESSAGE, 0);
    }

    Timestamp date;

    try {
      date = new Timestamp(cells[0]);
    } catch (DateTimeParseException e) {
      log.warning("Cannot create Timestamp object: " + cells[0]);
      throw new ParseException(EXCEPTION_MESSAGE, 0);
    }
    
    int popularity;
    try {
      popularity = Integer.parseInt(cells[1]);
    } catch (NumberFormatException e) {
      log.warning("Cannot parse interest over time: " + cells[1]);
      throw new ParseException(EXCEPTION_MESSAGE, 1);
    }

    if (popularity < 0 || popularity > 100) {
      log.warning("Interest over time out of range: " + popularity);
      throw new ParseException(EXCEPTION_MESSAGE, 1);
    }

    data.put(date, popularity); 
  }

  public List<Anomaly> getAnomalies() {
    return anomalies;
  }
}
