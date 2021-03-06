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

import static java.lang.Math.toIntExact;

/** 
 * Wrapper for a metric value.
 * This is an immutable class. 
 * TODO: Make metric value work for double, floats, etc.  
 */
public final class MetricValue {
  
  private final int value;

  public MetricValue(int value) {
    this.value = value;
  }

  public MetricValue(long value) {
    this.value = toIntExact(value);
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
   public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof MetricValue)) {
      return false;
    }

    MetricValue target = (MetricValue) o;

    return target.value == value;
  }

}
