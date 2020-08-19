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

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.lang.Math;

/**
 * Wrapper for LocalDate object with day, month, year fields. This class is immutable. 
 * Accepts format in yyyy-MM-dd or yyyy-M-d or yyyy-MM-d or yyyy-M-dd, but prints format in yyyy-MM-dd.
 */
public final class Timestamp {
  public static final String TIMESTAMP_PROPERTY = "timestamp";
  private static final String EXCEPTION_MESSAGE = "Invalid string format.";
  private static final int NUM_OF_MONTHS = 12;
  private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
                                                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d"))
                                                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-d"))
                                                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-dd"))
                                                    .toFormatter();

  private final LocalDate date;
 
  public Timestamp(int day, int month, int year) {
    date = LocalDate.of(year, month, day);
  }

  public Timestamp(String dateString) {
    date = LocalDate.parse(dateString, DATE_FORMATTER);
  }

  public Timestamp(LocalDate date) {
    this.date = date;
  }

  public int getDay() {
    return date.getDayOfMonth();
  }

  public int getMonth() {
    return date.getMonthValue();
  }

  public int getYear() {
    return date.getYear();
  }

  @Override
  public String toString() {
    return date.toString();
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
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

    return target.date.equals(date);
  }

  public static Timestamp getDummyTimestamp(int random) {
    return new Timestamp(1, Math.abs(random) % NUM_OF_MONTHS + 1, 2000);
  }

  public Timestamp getFirstDayOfNextMonth() {
    return new Timestamp(LocalDate.of(getYear(), getMonth(), 1).plusMonths(1));
  }

}
