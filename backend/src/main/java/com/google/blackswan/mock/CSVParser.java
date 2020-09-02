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

import com.google.models.Timestamp;
import java.util.Scanner;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.AbstractMap;
import java.util.logging.Logger;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import com.google.common.collect.ImmutableList;

/** Parse csv data and return map version of the data. */
public final class CSVParser {
  private static final String EXCEPTION_MESSAGE = "Invalid row format.";
  private static final String COMMA_DELIMITER = ",";
  private static final Logger log = 
    Logger.getLogger(CSVParser.class.getName());
  
  /** No instances. */
  private CSVParser() {}

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
        log.warning(e.getMessage());
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
      throw new ParseException("Cannot parse row: " + row, 0);
    }

    Timestamp date;

    try {
      date = new Timestamp(cells[0]);
    } catch (DateTimeParseException e) {
      throw new ParseException("Cannot create Timestamp object: " + cells[0], 0);
    }
    
    int popularity;
    try {
      popularity = Integer.parseInt(cells[1]);
    } catch (NumberFormatException e) {
      throw new ParseException("Cannot parse interest over time: " + cells[1], 1);
    }

    if (popularity < 0 || popularity > 100) {
      throw new ParseException("Interest over time out of range: " + popularity, 1);
    }

    return new AbstractMap.SimpleEntry<Timestamp, Integer>(date, popularity);
  }
}
