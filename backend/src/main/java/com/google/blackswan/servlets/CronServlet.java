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
import com.google.blackswan.mock.Constant;
import com.google.blackswan.mock.AlertGenerator;
import com.google.blackswan.mock.MultiInputAlertGenerator;
import com.google.blackswan.mock.SimpleRelatedDataGenerator;
import com.google.models.Alert;
import com.google.models.DataInfo;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableList;
import com.google.models.DataInfo;

/**
* Servlet to run cron job that generates Alerts to store in the datastore.
*/
@WebServlet("/blackswan/test")
public class CronServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(CronServlet.class.getName());
  private static final ImmutableList<DataInfo> ANOMALY_TYPES 
      = ImmutableList.of(DataInfo.of(Constant.INTEREST_US, Constant.RAMEN));
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.info("Cron job ran.");

    storeAlertsInDatastore();
    response.setStatus(HttpServletResponse.SC_ACCEPTED); 
  }

  private void clearCurrentAlertsInDatastore() {
    Query query = new Query(Alert.ALERT_ENTITY_KIND);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Iterate through the entities to delete all alert objects.
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      Key alertEntityKey = KeyFactory.createKey(Alert.ALERT_ENTITY_KIND, id);
      datastore.delete(alertEntityKey);
    }
  }

  /** Store alerts based on ANOMALY_TYPES into the datastore. */
  private void storeAlertsInDatastore() {
    // Need to run before every cron job batch operation to get updated related
    // data requests and clear cache of previous cron job. 
    SimpleRelatedDataGenerator.createGenerator().clearCacheAndFillRelatedData();
    AlertGenerator simpleGenerator = new MultiInputAlertGenerator(ANOMALY_TYPES);
    simpleGenerator.getAlerts().forEach(alert -> {
      DatastoreServiceFactory.getDatastoreService().put(alert.toEntity());
    });
  }
}
