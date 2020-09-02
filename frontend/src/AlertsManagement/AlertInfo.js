import React, { Component, Fragment } from 'react';
import { Line } from 'react-chartjs-2';
import { Link } from 'react-router-dom';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import Typography from '@material-ui/core/Typography';
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import { getSpecificAlertData } from './management_helpers';
import { UNRESOLVED_STATUS, RESOLVED_STATUS, priorityLevels } from './management_constants';

/**
 * Requests alert data given specified alert ID from route and 
 * visualizes the historical metric data using a chart.js wrapper for React.
 */
class AlertInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      alert: null,
      priority: null,
    }
  }

  async componentDidMount() {
    var result = await getSpecificAlertData(this.props.match.params.alertId);
    // TODO: instead of checking for null response, check for 404 status code.
    if (result === null) {
      throw new Error("Could not find alert with id " + this.props.match.params.alertId);
    }
    this.setState({ alert: result, priority: priorityLevels[result.priority] });
  }

  handleStatusChange = () => {
    const { alert } = this.state;
    
    const statusToChangeTo = alert.status === UNRESOLVED_STATUS ? RESOLVED_STATUS : UNRESOLVED_STATUS;
    fetch('/api/v1/alerts-data', {
      method: 'POST',
      body: alert.id + " " + statusToChangeTo,
    });

    const newAlert = Object.assign({}, alert);
    newAlert.status = statusToChangeTo;
    this.setState({ alert: newAlert });
  }

  handlePriorityChange = (newPriority) => {
    const { alert } = this.state;

    // We have to get the P0, P1, or P2 version to match with enum representation in backend.
    const numToEnum = Object.keys(priorityLevels)[Object.values(priorityLevels).indexOf(newPriority)];
    fetch('/api/v1/alert-visualization', {
      method: 'POST',
      body: alert.id + " " + numToEnum,
    });

    const newAlert = Object.assign({}, alert);
    newAlert.priority = newPriority;

    this.setState({ alert: newAlert, priority: newPriority});
  }
  
  render() {
    const style = {
      marginTop: '20px',
      marginBottom: '20px',
    };

    const { alert, priority } = this.state;

    if (!alert) {
      return <div />;
    }

    return (
      <div className="alert-info">
        <Divider variant="middle"/>
        <Link to="/alerts">
          <IconButton size="small" id="back-button">
            <ArrowBackIcon />
          </IconButton>
        </Link>
        <Typography variant="h5" align="center">
          Alert on {alert.timestampDate}
        </Typography>
        <Typography variant="h6" align="center">
          Status: {alert.status}
        </Typography>
        <center>
          <Button id="status-button" variant="contained" color="primary" component="span" 
            onClick={this.handleStatusChange}
          >
            {alert.status === UNRESOLVED_STATUS ? "Resolve?" : "Unresolve?"}
          </Button>
        </center>

        <Typography variant="h6" align="center">
          Priority: P{priority}
        </Typography>
        <center>
          <form>
            <Select
              labelId="priority-select"
              id="priority-select"
              value={priority}
              onChange={event => this.handlePriorityChange(event.target.value)}
            >
              <MenuItem value={priorityLevels.P0}>P0</MenuItem>
              <MenuItem value={priorityLevels.P1}>P1</MenuItem>
              <MenuItem value={priorityLevels.P2}>P2</MenuItem>
            </Select>
          </form>
        </center>

        <List className="anomalies-list">
          {alert.anomalies.map((anomaly, index) => {
            const chartData = {
              labels: [ ...anomaly.dataPoints.keys() ],
              datasets: [
                {
                  label: anomaly.metricName + ' for ' + anomaly.dimensionName,
                  fill: false,
                  lineTension: 0.1,
                  backgroundColor: 'rgba(75,192,192,0.4)',
                  borderColor: 'rgba(243, 0, 87, 1)',
                  borderCapStyle: 'butt',
                  borderDash: [],
                  borderDashOffset: 0.0,
                  borderJoinStyle: 'miter',
                  pointBorderColor: 'rgba(243, 0, 87, 1)',
                  pointBackgroundColor: '#fff',
                  pointBorderWidth: 1,
                  pointHoverRadius: 5,
                  pointHoverBackgroundColor: 'rgba(243, 0, 87, 1)',
                  pointHoverBorderColor: 'rgba(220,220,220,1)',
                  pointHoverBorderWidth: 2,
                  pointRadius: 1,
                  pointHitRadius: 10,
                  data: [ ...anomaly.dataPoints.values() ]
                }
              ]
            };

            return (
              <Fragment key={index}>
                <ListItem key={index} dense>
                  <ListItemText id={index} 
                    primary={`Anomaly in ${anomaly.metricName} for ${anomaly.dimensionName} on ${anomaly.timestampDate}`} 
                  />
                </ListItem>
                <Line key={index} data={chartData} />
                <Divider style={style} variant="inset" component="li"/>
              </Fragment>
            );
          })}
        </List>
      </div>
    );
  }
}

export default AlertInfo;
