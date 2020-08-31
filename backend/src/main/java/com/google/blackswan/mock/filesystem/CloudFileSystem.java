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

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.ReadChannel;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.models.DataInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.util.logging.Logger;
import com.google.blackswan.mock.Constant;

/** Wrapper for Cloud Storage file system to access files in greyswan bucket. */
public class CloudFileSystem implements FileSystem {
  private static final Logger log = Logger.getLogger(CloudFileSystem.class.getName());
  private static final String EXCEPTION_MESSAGE 
      = "Cannot get key.json file. Attempting to use Application Default Credentials.";

  private Storage storage;

  public static CloudFileSystem createSystem() {
    // If key.json cannot be used to generate key, use Application Default
    // Credentials to try to get CloudStorage service.
    Storage cloudStorage;
    try {
      cloudStorage = StorageOptions.newBuilder()
        .setProjectId(Constant.PROJECT_ID)
        .setCredentials(getCredentialsWithKey()).build()
        .getDefaultInstance().getService();
    } catch (IOException e) {
      log.warning(EXCEPTION_MESSAGE);
      cloudStorage = StorageOptions.newBuilder()
        .setProjectId(Constant.PROJECT_ID).build()
        .getDefaultInstance().getService();
    }
    return new CloudFileSystem(cloudStorage);
  }

  public static CloudFileSystem createSystemForTest(Storage storage) {
    return new CloudFileSystem(storage);
  }

  private CloudFileSystem(Storage storage) {
    this.storage = storage;
  }

  public InputStream getDataAsStream(DataInfo requestedData) {
    Blob requestedFileBlob = storage.get(BlobId.of(Constant.BUCKET_NAME, 
        Constant.FILE_LOCATIONS.get(requestedData)));

    return Channels.newInputStream(requestedFileBlob.reader());
  }

  private static GoogleCredentials getCredentialsWithKey() throws IOException {
    File credentialsPath = new File(CloudFileSystem.class
        .getClassLoader().getResource(Constant.KEY_LOCATION).getFile());
    if (!credentialsPath.exists()) {
      throw new IOException(EXCEPTION_MESSAGE);
    }

    return ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath));
  }
  
}
