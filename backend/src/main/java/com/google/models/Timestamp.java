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

package com.google.models;

/** Wrapper for timestamp with day, month, year fields. */
public final class Timestamp {
  public static String TIMESTAMP_PROPERTY = "timestamp";
  private static final String EXCEPTION_MESSAGE = "Invalid string format.";
  private static int EXPECTED_STR_ARR_LENGTH = 3;

  private final int day;
  private final int month;
  private final int year;
 
  public Timestamp(int day, int month, int year) {
    this.day = day;
    this.month = month;
    this.year = year;
  }

  public Timestamp(String dateString) throws Exception {
    String[] cells = dateString.split("-");
    if (cells.length != EXPECTED_STR_ARR_LENGTH) {
      System.err.println("Could not parse string: " + dateString);
      throw new Exception(EXCEPTION_MESSAGE);
    }

    int year, month, day;

    try {
      year = Integer.parseInt(cells[0]);
      month = Integer.parseInt(cells[1]);
      day = Integer.parseInt(cells[2]);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert string to int: " + dateString);
      e.printStackTrace();
      throw new NumberFormatException(EXCEPTION_MESSAGE);
    }

    this.day = day;
    this.month = month;
    this.year = year;
  }

  public int getDay() {
    return day;
  }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }

  @Override
  public String toString() {
    return year + "-" + month + "-" + day;
  }

  /** TODO: Better hashcode function. */
  @Override
  public int hashCode() {
    return day + month + year;
  }


  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Timestamp)) {
      return false;
    }

    Timestamp target = (Timestamp) o;

    return target.day == day && target.month == month && target.year == year;
  }

  public static Timestamp getDummyTimestamp(int random) {
    return new Timestamp(1, random, 2000);
  }

}
