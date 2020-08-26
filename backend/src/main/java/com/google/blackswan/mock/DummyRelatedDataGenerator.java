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

import com.google.models.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/** Generate list of hard-coded related data. */
public class DummyRelatedDataGenerator implements RelatedDataGenerator {
  private static final ImmutableList<RelatedData> relatedDataList = 
        new ImmutableList.Builder<RelatedData>()
        .add(RelatedData.getDummyRelatedData())
        .build();

  private DummyRelatedDataGenerator() {}

  public static RelatedDataGenerator createGenerator() {
    return new DummyRelatedDataGenerator();
  }

  /** 
   * Input parameters are not used at all, since this implementation generates 
   * dummy related data objects that do not depend on input. 
   */
  public ImmutableList<RelatedData> getRelatedData(String metricName, String dimensionName,
      Timestamp startTimeInclusive, Timestamp endTimeInclusive) {
    return relatedDataList;
  }
  
}
