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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;


/** Servlet that stores and fetches conifigurations in Datastore */
@WebServlet("/api/v1/configurations")
public class AlertConfigurationServlet extends HttpServlet {

  private static final String EMPTY_BODY_ERROR = "No data was sent in HTTP request body.";
  private static final String WRONG_ALERT_DATA = "Incorrect alert data sent in HTTP request.";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<String> results = new ArrayList<String>();

    Query query = new Query("Configuration");
    PreparedQuery entities = datastore.prepare(query);

    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(results));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String[] parameters = processRequestBody(request);
    UserService userService = UserServiceFactory.getUserService();

    String email = new String();
    if (userService.isUserLoggedIn()) {
      email = userService.getCurrentUser().getEmail();
    } else {
      email = parameters[0];
    }
    email = email.split("@")[0];
    String metric = parameters[1];
    String dimension = parameters[2];
    String relatedMetric = parameters[3];
    String relatedDimension = parameters[4];

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    try {
      Entity configurationEntity = new Entity("Configuration");
      configurationEntity.setProperty("user", email);
      configurationEntity.setProperty("metric", metric);
      configurationEntity.setProperty("dimension", dimension);
      configurationEntity.setProperty("relatedMetric", relatedMetric);
      configurationEntity.setProperty("relatedDimension", relatedDimension);
      datastore.put(configurationEntity);
    } catch (DatastoreFailureException e) {
      throw new ServletException(e);
    }
  }

  private String[] processRequestBody(HttpServletRequest request) throws IOException, ServletException {
    BufferedReader reader = request.getReader();
    String body = reader.readLine();
    if (body == null) {
      throw new ServletException(EMPTY_BODY_ERROR);
    }
    String[] parameters = body.split("%"); 

    if (parameters.length != 5) {
      throw new ServletException(WRONG_ALERT_DATA);
    }

    return parameters;
  }
}
