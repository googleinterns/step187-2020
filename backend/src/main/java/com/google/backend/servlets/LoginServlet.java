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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that checks user login status and provides a login/logout URL accordingly. */
@WebServlet("/api/v1/login")
public class LoginServlet extends HttpServlet {
  
  private static final String REDIRECT_URL = "/";
  private static final String ENTITY_KIND = "User";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    UserService userService = UserServiceFactory.getUserService();
    JsonObject json = new JsonObject();

    if (userService.isUserLoggedIn()) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      
      String email = userService.getCurrentUser().getEmail();
      Query query =
          new Query(ENTITY_KIND).setFilter(
            new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
      PreparedQuery results = datastore.prepare(query);
      Entity entity = results.asSingleEntity();

      // Create new user if user does not already exist in Datastore.      
      if (entity == null) {
        Entity newUser = new Entity(ENTITY_KIND);
        newUser.setProperty("email", email);
        datastore.put(newUser);
      }

      String logoutURL = userService.createLogoutURL(REDIRECT_URL);
      json.addProperty("isLoggedIn", true);
      json.addProperty("logURL", logoutURL);
      response.getWriter().println(json);
    } else {
      String loginURL = userService.createLoginURL(REDIRECT_URL); 
      json.addProperty("isLoggedIn", false);
      json.addProperty("logURL", loginURL);
      response.getWriter().println(json);
    }
  }
}
