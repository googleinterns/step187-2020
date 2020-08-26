import { convertTimestampToDate } from '../time_utils';
import { UNRESOLVED_STATUS } from './management_constants';

/**
 * Fetch alerts data from Datastore and organize into data structures.
 * Returns an array with a map of alert IDs to Object containing alert info, 
 * an array of ids of unresolved alerts, and an array of ids of resolved alerts.
 */
export async function getAlertsData() {
  let allAlerts = new Map();
  let unresolvedAlerts = [];
  let resolvedAlerts = [];

  const alertsResponse = await fetch('/api/v1/alerts-data').then(response => response.json());
  alertsResponse.forEach((alert) => {
    const alertId = alert.id.value;
    
    let editedAnomalies = [];
    alert.anomalies.forEach(anomaly => 
      editedAnomalies.push({
        timestampDate: convertTimestampToDate(anomaly.timestampDate),
        metricName: anomaly.metricName,
        dimensionName: anomaly.dimensionName,
      })
    );

    allAlerts.set(alertId, {
      timestampDate: convertTimestampToDate(alert.timestampDate), 
      anomalies: editedAnomalies,
      status: alert.status,
    });
    
    if (alert.status === UNRESOLVED_STATUS) {
      unresolvedAlerts.push(alertId);
    } else {
      resolvedAlerts.push(alertId);
    }
  });

  return [allAlerts, unresolvedAlerts, resolvedAlerts];
}

/**
 * Fetch specific alert data from the Datastore given alert ID.
 * Returns an Object with processed alert data.
 */
export async function getSpecificAlertData(alertId) {
  const alert = await fetch('/api/v1/alert-visualization?id=' + alertId)
    .then(response => response.json());
  let editedAnomalies = alert.anomalies.slice();
  for (let key in alert.anomalies) {
    editedAnomalies[key].timestampDate = convertTimestampToDate(alert.anomalies[key].timestampDate);
    let editedData = new Map();
    for (const date in alert.anomalies[key].dataPoints) {
      editedData.set(date, alert.anomalies[key].dataPoints[date].value);
    }
    editedAnomalies[key].dataPoints = editedData;
  }

  return({ 
    id: alert.id.value,
    timestampDate: convertTimestampToDate(alert.timestampDate), 
    anomalies: editedAnomalies,
    status: alert.status,
  });
}
