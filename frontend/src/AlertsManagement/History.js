import React, { Component } from 'react';
import MaterialTable from 'material-table';
import Chip from '@material-ui/core/Chip';
import Typography from '@material-ui/core/Typography';
import LinkIcon from '@material-ui/icons/Link';
import { DATA_DELIMITER } from './management_constants';
import { getAlertsData } from './management_helpers';
import { TABLE_ICONS } from './table_icons';
import { formatDate } from '../time_utils';

const createData = (id, timestampDate, numAnomalies, status, priority, metrics, dimensions) => {
  return { id, timestampDate, numAnomalies, status, priority, metrics, dimensions };
}

const rows = (allAlerts) => { 
  let rowItems = [];
  allAlerts.forEach((alert, alertId) => {
    let metrics = new Set(alert.anomalies.map((anomaly) => anomaly.metricName));
    let dimensions = new Set(alert.anomalies.map((anomaly) => anomaly.dimensionName));

    // TODO: replace 1 with alert.priority
    rowItems.push(
      createData(alertId, formatDate(alert.timestampDate, /** monthFirst = */ false), 
        alert.anomalies.length, alert.status, 1,
        [...metrics].reduce((accumulator, value) => accumulator + DATA_DELIMITER + value), 
        [...dimensions].reduce((accumulator, value) => accumulator + DATA_DELIMITER + value)
      )
    );
  });
  return rowItems; 
}

class History extends Component {
  constructor(props) {
    super(props);
    this.state = {
      allAlerts: null,
    }
  }

  async componentDidMount() {
    var results = await getAlertsData();
    if (results.length !== 3) {
      throw new Error("getAlertsData() did not return the correct alerts data.")
    }
    this.setState({
      allAlerts: new Map(results[0]),
    });
  }

  render() {
    const styles = ({
      root: {
        flexGrow: 1,
        maxWidth: '90%',
        margin: 'auto',
      },
      header: {
        marginTop: '50px',
        marginBottom: '30px'
      },
      chip: {
        margin: '5px',
      }
    });

    const { allAlerts } = this.state;
    
    if (allAlerts === null) return <div />;

    return (
      <div style={styles.root}>
        <Typography variant="h4" gutterBottom style={styles.header}>Alerts Archive</Typography>
        
        <MaterialTable icons={TABLE_ICONS}
          actions={[
            {
              icon: () => <LinkIcon />,
              tooltip: 'See More',
              onClick: (_event, rowData) => this.props.history.push(`alerts/${rowData.id}`),
            }
          ]}
          columns={[
            { title: 'Timestamp', field: 'timestampDate', filtering: false },
            { title: '# of Anomalies', field: 'numAnomalies' },
            { title: 'Status', field: 'status', sorting: false },
            { title: 'Priority', field: 'priority', type: 'numeric', align: 'left' },
            { 
              title: 'Metrics', field: 'metrics', sorting: false,
              render: rowData => rowData.metrics.split(DATA_DELIMITER).map((metric) => 
                <Chip label={metric} color="secondary" variant="outlined" 
                  size="small" style={styles.chip}
                />)
            },
            { 
              title: 'Dimensions', field: 'dimensions', sorting: false,
              render: rowData => rowData.dimensions.split(DATA_DELIMITER).map((dimension) => 
                <Chip label={dimension} color="primary" variant="outlined" 
                  size="small" style={styles.chip}
                />)
            }
          ]}
          data={rows(allAlerts)} 
          options={{
            exportButton: true,
            search: true,
            pageSize: 10,
            filtering: true
          }}
          title=""
          exportFileName="BlackSwan_Alerts"
        />
      </div>
    );
  }
}

export default History;
