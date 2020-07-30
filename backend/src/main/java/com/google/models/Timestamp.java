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

  private final int day;
  private final int month;
  private final int year;
 
  public Timestamp(int day, int month, int year) {
    this.day = day;
    this.month = month;
    this.year = year;
  }

  public String toString() {
    return year + "-" + month + "-" + day;
  }

  public static Timestamp getDummyTimestamp() {
    return new Timestamp(1, 1, 2000);
  }

}
