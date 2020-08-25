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
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.models.Alert;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * Servlet that returns data for requested specific Alert.
 */
@WebServlet("/api/v1/alert-visualization")
public class AlertVisualizationServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    Long id = Long.parseLong(request.getParameter("id"));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Entity alertEntity = datastore.get(KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, id));
      Alert alert = Alert.createAlertFromEntity(alertEntity);
      response.setContentType("application/json;");
      response.getWriter().println(new Gson().toJson(alert));
    } catch (EntityNotFoundException e) {
      throw new ServletException(e);
    }
  }
}
