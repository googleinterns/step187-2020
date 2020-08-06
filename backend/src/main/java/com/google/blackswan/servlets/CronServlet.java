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

package com.google.blackswan.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import com.google.blackswan.mock.*;
import com.google.models.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.util.*;

/**
* Servlet to run cron job that generates Alerts to store in the datastore.
*/
@WebServlet("/blackswan/test")
public class CronServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(CronServlet.class.getName());
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Update or clean up log message. 
    log.info("Cron job ran.");
    
    // TODO: Logic for cron job to run blackswan mock. (#13)
    // storeAlertInDatastore();
    testSimpleAnomaly();
    
    response.setStatus(HttpServletResponse.SC_ACCEPTED); 
  }

  private void testSimpleAnomaly() {
    AlertGenerator simpleGenerator = new SimpleAlertGenerator(new SimpleAnomalyGenerator());
    simpleGenerator.getAlerts().forEach(alert -> {
      DatastoreServiceFactory.getDatastoreService().put(alert.toEntity());
    });
  }

  /** Temporary method for storing dummy alert into datastore. */
  private void storeAlertInDatastore() {
    AlertGenerator testAlertGenerator = new DummyAlertGenerator(new DummyAnomalyGenerator());
    List<Alert> alerts = testAlertGenerator.getAlerts();

    DatastoreServiceFactory.getDatastoreService().put(alerts.get(0).toEntity());
  }

}
