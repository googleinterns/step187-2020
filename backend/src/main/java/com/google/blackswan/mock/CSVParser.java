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
public final class CSVParser {
  private static final String DATA_FILE_LOCATION = "/sample-ramen-data.csv";
  private static final String EXCEPTION_MESSAGE = "Invalid row format.";
  private static final String COMMA_DELIMITER = ",";
  private static final Logger log = 
    Logger.getLogger(CSVParser.class.getName());

  public static Map<Timestamp, Integer> parseCSV(InputStream inputSource) {
    Scanner scanner = new Scanner(inputSource);
    Map<Timestamp, Integer> data = new LinkedHashMap<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      // Parse a row of data, if contain invalid format, skip to next row.
      try {
        Map.Entry<Timestamp, Integer> entry = parseRow(line);
        data.put(entry.getKey(), entry.getValue());
      } catch (ParseException e) {
        // Catch ParseException, but keep scanning.
        continue;
      }
    }
    scanner.close();
    return data;
  }

  /** 
   * Each row of csv has following format: { yyyy-mm-dd, popularity }. 
   * ParseException takes 2 parameters: error message and index of string that failed to be parsed.
   */
  private static Map.Entry<Timestamp, Integer> parseRow(String row) 
      throws ParseException {
    String[] cells = row.split(COMMA_DELIMITER);
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

    return new AbstractMap.SimpleEntry<Timestamp, Integer>(date, popularity);
  }
}
