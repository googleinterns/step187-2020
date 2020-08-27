package com.google.blackswan.mock.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.models.DataInfo;


/** Contain tests for methods in {@link CloudFileSystem} class. */
@RunWith(JUnit4.class)
public class CloudFileSystemTest {
  @Mock Storage MOCK_STORAGE;
  @Mock Blob MOCK_BLOB;

  private static final String EXPECTED_BUCKET_NAME = "greyswan.appspot.com";
  private static final String METRIC = "Interest Over Time - US";
  private static final String DIMENSION = "Ramen";
  private static final String EXPECTED_OBJECT_NAME = "ramen-us--data.csv";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(MOCK_STORAGE.get(BlobId.of(EXPECTED_BUCKET_NAME, EXPECTED_OBJECT_NAME)))
        .thenReturn(MOCK_BLOB);
  }

  /** 
  * NullPtrException expected because MOCK_BLOB.reader() returns null as MOCK_BLOB
  * is not a real blob. However, this tests if the correct name for blob is used,
  * so NullPtrException should not matter.
  */
  @Test(expected = NullPointerException.class)
  public void testGetDataAsStream_correctFileName() {
    CloudFileSystem system = CloudFileSystem.createSystemForTest(MOCK_STORAGE);

    // Try finally block is necessary or else test will exit before reaching verify
    // statement. 
    try {
      system.getDataAsStream(DataInfo.of(METRIC, DIMENSION));
    } finally {
      verify(MOCK_STORAGE).get(BlobId.of(EXPECTED_BUCKET_NAME, EXPECTED_OBJECT_NAME));
    }
  }

}
