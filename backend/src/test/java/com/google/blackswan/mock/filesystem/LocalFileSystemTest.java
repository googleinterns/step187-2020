package com.google.blackswan.mock.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.ClassLoader;
import java.io.InputStream;


/** Contain tests for methods in {@link LocalFileSystem} class. */
@RunWith(JUnit4.class)
public class LocalFileSystemTest {
  @Mock ClassLoader LOADER;
  @Mock InputStream INPUT_STREAM;

  private static final String METRIC = "interest";
  private static final String DIMENSION = "ramen";
  private static final String EXPECTED_NAME = "interest-ramen.csv";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(LOADER.getResourceAsStream(EXPECTED_NAME)).thenReturn(INPUT_STREAM);
  }

  @Test
  public void testGetDataAsStream_correctFileName() {
    LocalFileSystem system = LocalFileSystem.createSystemForTest(LOADER);

    system.getDataAsStream(METRIC, DIMENSION);

    verify(LOADER).getResourceAsStream(EXPECTED_NAME);
  }

}
