// Copyright 2020 Google LLC
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

package com.google.blackswan.mock.filesystem;

import com.google.models.DataInfo;
import com.google.blackswan.mock.Constant;
import java.io.InputStream;
import java.lang.ClassLoader;

/** Local file system wrapper for data stored in resources/ folder. */
public class LocalFileSystem implements FileSystem {
  private final ClassLoader classLoader;

  public static LocalFileSystem createSystem() {
    return new LocalFileSystem(LocalFileSystem.class.getClassLoader());
  }

  public static LocalFileSystem createSystemForTest(ClassLoader mock) {
    return new LocalFileSystem(mock);
  }

  private LocalFileSystem(ClassLoader loader) {
    this.classLoader = loader;
  }

  public InputStream getDataAsStream(DataInfo requestedData) {
    return classLoader.getResourceAsStream(Constant.FILE_LOCATIONS.get(requestedData));
  }

}
