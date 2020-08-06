## Simulate action of cron job loading alerts into datastore
1) Run backend server
```
mvn appengine:run
```
2) Make a fetch to `/blackswan/test` to simulate cron job running. This will intiate series of actions that generate alerts and store them in your local datastore. 
Make sure the following line in `blackswan/servlets/CronServlet.java` is uncommented before running the fetch to `/blackswan/test`.
```
public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  // other code...
  storeAlertsInDataStoreSimple(); // Should not be commented.
}
```
3) Check to see if the alerts are stored in your local datastore by visiting http://localhost:8888/_ah/admin/datastore?kind=alert.
This page should not be empty.

4) To fetch alerts from the datastore, you can reference the `testFetch()` function in `blackswan/servlets/CronServlet.java`. The function fetches alert entities from
the datastore and converts them to alert objects, and then prints the alert objects out to make sure all embedded entities are converted back correctly.

What a single `Alert` object looks like when printed: (This alert contains just two anomalies.)
```
[INFO] Timestamp: 2020-02-01
[INFO] Status: UNRESOLVED
[INFO] Anomalies: 
[INFO] Timestamp: 2020-01-05
[INFO] Metric Name: Interest Over Time
[INFO] Dimension Name: Ramen
[INFO] Datapoints: 
[INFO] 2019-12-08: 76
[INFO] 2019-12-29: 100
[INFO] 2019-12-15: 76
[INFO] 2020-01-12: 75
[INFO] 2020-02-02: 68
[INFO] 2020-01-26: 82
[INFO] 2019-12-22: 59
[INFO] 2020-01-05: 93
[INFO] 2019-12-01: 93
[INFO] 2020-01-19: 94
[INFO] 2020-02-09: 73
[INFO] Timestamp: 2020-01-19
[INFO] Metric Name: Interest Over Time
[INFO] Dimension Name: Ramen
[INFO] Datapoints: 
[INFO] 2019-12-29: 100
[INFO] 2020-02-23: 79
[INFO] 2019-12-15: 76
[INFO] 2020-01-12: 75
[INFO] 2020-02-02: 68
[INFO] 2020-01-26: 82
[INFO] 2020-02-16: 84
[INFO] 2019-12-22: 59
[INFO] 2020-01-05: 93
[INFO] 2020-01-19: 94
[INFO] 2020-02-09: 73 
```
5) It is important to note that if you fetch twice to `/blackswan/test`, there will be two sets of the same alerts in the datastore. So to prevent that from happening 
you should fetch just once to the `/blackswan/test` or run the `clearCurrentAlertsInDatastore()` function in the `CronServlet.java`.

To run the `clearCurrentAlertsInDatastore()` comment and uncomment the lines below, and make a fetch to `/blackswan/test`. Then the datastore should be empty. 
```
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // other code...
    clearCurrentAlertsInDatastore(); // Uncomment this line!
    // storeAlertsInDataStoreSimple(); // Comment this line!
    testFetch();
    response.setStatus(HttpServletResponse.SC_ACCEPTED); 
  }
```
