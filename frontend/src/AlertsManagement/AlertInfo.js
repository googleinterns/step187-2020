import React, { Component, Fragment } from 'react';
import { Line } from 'react-chartjs-2';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import Grid from '@material-ui/core/Grid';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Grid from '@material-ui/core/Grid';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import Typography from '@material-ui/core/Typography';
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import { getSpecificAlertData } from './management_helpers';
import { UNRESOLVED_STATUS, RESOLVED_STATUS, priorityLevels } from './management_constants';

const styles = {
  divider: {
    marginTop: '20px',
    marginBottom: '20px',
  },
  resolveButton: {
    marginTop: '10px',
    marginBottom: '30px',
  },
  relatedText: { 
    marginTop: '10px', 
    marginBottom: '5px' 
  },
  relatedDivider: { 
    marginBottom: '20px',
  },
  anomalyText: {
    marginTop: '10px'
  },
  infoText: {
    color: '#0FA3B1'
  }
};

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
    fetch('/api/v1/alerts-data?id=' + alert.id + '&status=' + statusToChangeTo, {
      method: 'POST'
    });

    const newAlert = Object.assign({}, alert);
    newAlert.status = statusToChangeTo;
    this.setState({ alert: newAlert });
  }

  createLineChart = (visData, index) => {
    // If visualizing anomaly data (has related data) use pink, otherwise use blue.
    const color = visData.relatedDataList ? 'rgba(243, 0, 87, 1)' : 'rgba(63, 81, 181, 1)';
    const chartData = {
      labels: [ ...visData.dataPoints.keys() ],
      datasets: [
        {
          label: visData.metricName + ' for ' + visData.dimensionName,
          fill: false,
          lineTension: 0.1,
          backgroundColor: 'rgba(75,192,192,0.4)',
          borderColor: color,
          borderCapStyle: 'butt',
          borderDash: [],
          borderDashOffset: 0.0,
          borderJoinStyle: 'miter',
          pointBorderColor: color,
          pointBackgroundColor: '#fff',
          pointBorderWidth: 1,
          pointHoverRadius: 5,
          pointHoverBackgroundColor: color,
          pointHoverBorderColor: 'rgba(220,220,220,1)',
          pointHoverBorderWidth: 2,
          pointRadius: 1,
          pointHitRadius: 10,
          data: [ ...visData.dataPoints.values() ]
        }
      ]
    };

    return <Line key={index} data={chartData} />;
  }

  visualizeRelatedData = (anomaly) => {
    let relatedDataCharts = [];
    anomaly.relatedDataList.forEach((relatedData, index) => 
      relatedDataCharts.push(
        <Fragment key={index}>
          <Typography variant="body2" style={styles.relatedText}>
            {`Related data from ${relatedData.metricName} for 
              ${relatedData.dimensionName} (${relatedData.username})`}
          </Typography>
          {this.createLineChart(relatedData, index)}
          {index === (anomaly.relatedDataList.length - 1) ? null :
            <Divider style={styles.divider}/> }
        </Fragment>
      )
    );
    return relatedDataCharts;
  }

  handlePriorityChange = (newPriority) => {
    const { alert } = this.state;

    // We have to get the P0, P1, or P2 version to match with enum representation in backend.
    const numToEnum = Object.keys(priorityLevels)[Object.values(priorityLevels).indexOf(newPriority)];
    fetch('/api/v1/alert-visualization?id=' + alert.id + '&priority=' + numToEnum, {
      method: 'POST'
    });

    const newAlert = Object.assign({}, alert);
    newAlert.priority = newPriority;

    this.setState({ alert: newAlert, priority: newPriority });
  }
  
  render() {
    const { alert, priority } = this.state;

    if (!alert) return <div />;

    return (
      <div className="alert-info">
        <Divider variant="middle"/>
        <IconButton size="small" id="back-button" onClick={this.props.history.goBack}>
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h4" align="center">
          Alert on {alert.timestampDate}
        </Typography>
        <Typography variant="h6" align="center">
            Number of anomalies: {alert.anomalies.length}
        </Typography>
        
        <Grid container xs={12} justify="center">
          <Grid item xs={2}>
            <Typography variant="h6" align="center">
              Status: {alert.status}
            </Typography>
            <center>
              <Button id="status-button" variant="contained" color="primary" component="span" 
                onClick={this.handleStatusChange} style={styles.resolveButton}
              >
                {alert.status === UNRESOLVED_STATUS ? "Resolve?" : "Unresolve?"}
              </Button>
            </center>
          </Grid>
          <Grid item xs={2}>
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
                  style={styles.resolveButton}
                >
                  <MenuItem value={priorityLevels.P0}>P0</MenuItem>
                  <MenuItem value={priorityLevels.P1}>P1</MenuItem>
                  <MenuItem value={priorityLevels.P2}>P2</MenuItem>
                </Select>
              </form>
            </center>
          </Grid>
        </Grid>

        <Grid container >
          <Grid item xs={6}>
            <Typography variant="h6" align="center">Anomaly Graphs</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="h6" align="center">Related Data Graphs</Typography>
          </Grid>
        </Grid>

        <Grid container justify="center">
          <Grid item xs={2.5}>
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
          </Grid>
          <Grid item xs={2}>
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
          </Grid>
        </Grid>
 
        <List className="anomalies-list">
          {alert.anomalies.map((anomaly, index) => {
            return (
              <Fragment key={index}>
                <ListItem key={index} dense>
                  <Grid container spacing={4}>
                    <Grid item xs={anomaly.relatedDataList.length !== 0 ? 6 : 12}>
                      <ListItemText id={index} 
                        disableTypography
                        primary={
                          <Typography variant="body2" style={styles.anomalyText}>
                            <Box display='inline' fontWeight="fontWeightBold" m={0.25} style={styles.infoText}>
                              {` ${anomaly.metricName} for ${anomaly.dimensionName}`}
                            </Box> on
                            <Box display='inline' fontWeight="fontWeightBold" m={0.5} style={styles.infoText}>
                              {`${anomaly.timestampDate}`}
                            </Box>
                          </Typography>
                        }
                      />
                      {this.createLineChart(anomaly, index)}
                    </Grid>
                    <Grid item xs={6}>
                      {this.visualizeRelatedData(anomaly)}
                    </Grid>
                  </Grid>
                </ListItem>
                <Divider style={styles.divider} component="li"/>
              </Fragment>
            );
          })}
        </List>
      </div>
    );
  }
}

export default AlertInfo;
