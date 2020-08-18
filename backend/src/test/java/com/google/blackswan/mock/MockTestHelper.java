package com.google.blackswan.servlets;

import java.util.Map;

/** Contain helper methods for blackswan mock tests. */
public final class MockTestHelper {

  /** Outputs data based on input using format of csv data. */
  public static String inputForAnomalyGenerator(Map<String, Integer> input) {
    StringBuilder str = new StringBuilder("");
    for (Map.Entry<String, Integer> entry : input.entrySet()) {
      str.append(entry.getKey() + "," + entry.getValue().toString() + "\n");
    }
    return str.toString();
  }

}
