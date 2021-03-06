package com.google.blackswan.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Contain tests for methods in the {@link CronServlet} class. */
@RunWith(JUnit4.class)
public class CronServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  private CronServlet cronServlet;
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    MockitoAnnotations.initMocks(this);
    cronServlet = new CronServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCronServletGet_returnStatus() throws IOException, ServletException {
    cronServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
  }

  /** TODO: Include tests for other logics of CronServlet once implemented. */

}
