import { convertTimestampToDate } from '../time_utils';
import { UNRESOLVED_STATUS } from './management_constants';

/**
 * Fetch alerts data from Datastore and organize into data structures.
 * Returns an array with a map of alert IDs to alert information, 
 * an array of ids of unresolved alerts, and an array of ids of resolved alerts.
 */
export async function getAlertsData() {
  let allAlerts = new Map();
  let unresolvedAlerts = [];
  let resolvedAlerts = [];

  const alertsResponse = await fetch('/api/v1/alerts-data').then(response => response.json());
  // TODO: replace value with actual alert ID (received from JSON, e.g. alert.id).
  alertsResponse.forEach((alert, value) => {
    let editedAnomalies = alert.anomalies.slice();
    for (let key in alert.anomalies) {
      editedAnomalies[key].timestampDate = convertTimestampToDate(alert.anomalies[key].timestampDate);
      let editedData = new Map();
      for (const date in alert.anomalies[key].dataPoints) {
        editedData.set(date, alert.anomalies[key].dataPoints[date].value);
      }
      editedAnomalies[key].dataPoints = editedData;
    }

    allAlerts.set(value, {
      timestampDate: convertTimestampToDate(alert.timestampDate), 
      anomalies: editedAnomalies,
      status: alert.status,
    });
    
    if (alert.status === UNRESOLVED_STATUS) {
      unresolvedAlerts.push(value);
    } else {
      resolvedAlerts.push(value);
    }
  });

  return [allAlerts, unresolvedAlerts, resolvedAlerts];
}
