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

package com.google.backend.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.models.Alert;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that handles Alert data. 
 * Gets specified amount of most recent Alert data from the Datastore and returns JSON.
 * Given user input, changes Alert status in the Datastore. 
 */
@WebServlet("/api/v1/alerts-data")
public class AlertsDataServlet extends HttpServlet {

  private static final int MAX_LIMIT = 1000;
  private static final int DEFAULT_ALERTS_LIMIT = 5;
  private static final String EMPTY_BODY_ERROR = "No data was sent in HTTP request body.";
  private static final String WRONG_ALERT_DATA = "Incorrect alert data sent in HTTP request.";
  private static final Logger log = Logger.getLogger(AlertsDataServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: only get alerts that the user is subscribed to.
    Query query = new Query(Alert.ALERT_ENTITY_KIND).addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    int limit;
    try {
      limit = Integer.parseInt(request.getParameter("limit"));
    } catch (NumberFormatException e) {
      log.warning("Received " + request.getParameter("limit") + " instead of an integer.");
      limit = DEFAULT_ALERTS_LIMIT;
    }

    if (limit == 0) limit = MAX_LIMIT;
    
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));
    List<Alert> alertList = results.stream().map(Alert::createAlertFromEntity).collect(Collectors.toList());
    
    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(limit));
    List<Alert> alertList = results.stream().map(Alert::createAlertFromEntity).collect(Collectors.toList());
    
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(alertList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    String[] data = processRequestBody(request);
    Long id = Long.parseLong(data[0]);
    String status = data[1];

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity alertEntity = datastore.get(KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, id));
      alertEntity.setProperty(Alert.STATUS_PROPERTY, status);
      datastore.put(alertEntity);
    } catch (EntityNotFoundException e) {
      throw new ServletException(e);
    }
  }

  /** 
    * Read from the request body, which contains data in the format "alertId statusToChangeTo".
    * For example, the body could be "4785074604081152 RESOLVED".
    */
  private String[] processRequestBody(HttpServletRequest request) 
      throws IOException, ServletException {
    BufferedReader reader = request.getReader();
    String body = reader.readLine();
    if (body == null) {
      throw new ServletException(EMPTY_BODY_ERROR);
    }
    String[] data = body.split(" "); 
    if (data.length != 2) {
      throw new ServletException(WRONG_ALERT_DATA);
    }
    return data;
  }
}
