package com.google.backend.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Contain tests for methods in {@link DataServlet} class. */
@RunWith(JUnit4.class)
public class DataServletTest {
  @Mock HttpServletRequest request;
  @Mock HttpServletResponse response;

  private static final DataServlet dataServlet = new DataServlet();
  private static final StringWriter stringWriter = new StringWriter();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
  }

  @Test
  public void testDataServlet_correctResponse() throws IOException, ServletException {
    dataServlet.doGet(request, response);

    assertEquals(stringWriter.getBuffer().toString().trim(), "Hello world from data servlet!");
  }

  /** TODO: Add tests for logic of DataServlet once more complicated logic is implemented. */

}
