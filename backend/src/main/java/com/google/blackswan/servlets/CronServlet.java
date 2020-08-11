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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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

    // Simple logic for cron job, since we only have one set of alerts for now. 
    clearCurrentAlertsInDatastore(); // Need to clear alerts as we only have one set of alerts now. 
    storeAlertsInDatastoreSimple();
    response.setStatus(HttpServletResponse.SC_ACCEPTED); 
  }

  private void clearCurrentAlertsInDatastore() {
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Iterate through the entities to delete all comment objects.
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      Key alertEntityKey = KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, id);
      datastore.delete(alertEntityKey);
    }

  }

  /** Storing alerts from simpleGenerator into the datastore. */
  private void storeAlertsInDatastoreSimple() {
    AlertGenerator simpleGenerator = new SimpleAlertGenerator(new SimpleAnomalyGenerator());
    simpleGenerator.getAlerts().forEach(alert -> {
      DatastoreServiceFactory.getDatastoreService().put(alert.toEntity());
    });
  }

  /** 
   * Sample fetch alerts from datastore and creating alert objects from it. 
   * Used during demo for MVP presentation. TODO: delete or modify. 
   */
  private void fetchAlertsFromDatastore() {
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Alert> alertList = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      alertList.add(Alert.createAlertFromEntity(entity));
    }

    // Print out to see if alerts are correctly converted.
    // Used primarily during backend demo. 
    alertList.forEach(alert -> System.out.println(alert));
  }

}
