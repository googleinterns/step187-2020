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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.util.logging.Logger;

/** Wrapper for Cloud Storage file system to access files in greyswan bucket. */
public class CloudFileSystem implements FileSystem {
  private static final Logger log = Logger.getLogger(CloudFileSystem.class.getName());
  private static final String KEY_LOCATION = "keys/key.json";
  private static final String PROJECT_ID = "greyswan";
  private static final String BUCKET_NAME = "greyswan.appspot.com";
  private static final String EXCEPTION_MESSAGE 
      = "Cannot get key.json file. Attempting to use Application Default Credentials.";

  private Storage storage;

  public static CloudFileSystem createSystem() {
    return new CloudFileSystem();
  }

  public static CloudFileSystem createSystemForTest(Storage storage) {
    return new CloudFileSystem(storage);
  }

  /** No instance. */
  private CloudFileSystem() {
    // If key.json cannot be used to generate key, use Application Default
    // Credentials to try to get CloudStorage service.
    try {
      storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID)
        .setCredentials(getCredentialsWithKey()).build()
        .getDefaultInstance().getService();
    } catch (IOException e) {
      log.warning(EXCEPTION_MESSAGE);
      storage = StorageOptions.newBuilder()
        .setProjectId(PROJECT_ID).build()
        .getDefaultInstance().getService();
    }
  }

  private CloudFileSystem(Storage storage) {
    this.storage = storage;
  }

  public InputStream getDataAsStream(String metric, String dimension) {
    String objectName = new StringBuilder(metric).append(FileSystem.DELIMITER)
        .append(dimension).append(FileSystem.FILE_TYPE).toString();

    Blob requestedFileBlob = storage.get(BlobId.of(BUCKET_NAME, objectName));

    return Channels.newInputStream(requestedFileBlob.reader());
  }

  private GoogleCredentials getCredentialsWithKey() throws IOException {
    File credentialsPath = new File(getClass().getClassLoader().getResource(KEY_LOCATION).getFile());
    if (!credentialsPath.exists()) {
      throw new IOException(EXCEPTION_MESSAGE);
    }

    return ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath));
  }
  
}
