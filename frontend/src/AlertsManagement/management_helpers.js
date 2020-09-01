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

  const alertsResponse = await fetch('/api/v1/alerts-data')
  if (!alertsResponse.ok) throw new Error('Error getting alerts data: ' + alertsResponse.status);
  const data = await alertsResponse.json();
  data.forEach((alert) => {
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
  const alertResponse = await fetch('/api/v1/alert-visualization?id=' + alertId)
  if (!alertResponse.ok) throw new Error(
    'Error getting alert data for id ' + alertId + ':' + alertResponse.status
  );
  const alert = await alertResponse.json();
  
  let editedAnomalies = alert.anomalies.slice();
  alert.anomalies.forEach((anomaly, index) => {
    editedAnomalies[index].timestampDate = convertTimestampToDate(anomaly.timestampDate);
    
    let editedData = new Map();
    for (const date in anomaly.dataPoints) {
      editedData.set(date, anomaly.dataPoints[date].value);
    }
    editedAnomalies[index].dataPoints = editedData;
    
    let editedRelatedDataList = anomaly.relatedDataList.slice();
    anomaly.relatedDataList.forEach((relatedData, index) => {
      let editedData = new Map();
      for (const date in relatedData.dataPoints) {
        editedData.set(date, relatedData.dataPoints[date].value);
      }
      editedRelatedDataList[index].dataPoints = editedData;
    });
    editedAnomalies[index].relatedDataList = editedRelatedDataList;
  });    

  return({ 
    id: alert.id.value,
    timestampDate: convertTimestampToDate(alert.timestampDate), 
    anomalies: editedAnomalies,
    status: alert.status,
  });
}

