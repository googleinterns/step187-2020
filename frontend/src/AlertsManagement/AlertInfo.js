import React, { Component, Fragment } from 'react';
import { Line } from 'react-chartjs-2';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import { getSpecificAlertData } from './management_helpers';
import { UNRESOLVED_STATUS, RESOLVED_STATUS } from './management_constants';

/**
 * Requests alert data given specified alert ID from route and 
 * visualizes the historical metric data using a chart.js wrapper for React.
 */
class AlertInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      alert: null,
    }
  }

  async componentDidMount() {
    var result = await getSpecificAlertData(this.props.match.params.alertId);
    // TODO: instead of checking for null response, check for 404 status code.
    if (result === null) {
      throw new Error("Could not find alert with id " + this.props.match.params.alertId);
    }
    this.setState({ alert: result });
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

  createLineChart = (visData, index) => {
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
          {this.createLineChart(relatedData, index)}
          <Typography variant="body2" align="center" style={{ margin: '10px', paddingBottom: '10px' }}>
            {`Related data from ${relatedData.metricName} for 
              ${relatedData.dimensionName} (${relatedData.username})`}
          </Typography>
          {index === (anomaly.relatedDataList.length - 1) ? null :
            <Divider style={{ marginBottom: '20px'}}/> }
        </Fragment>
      )
    );
    return relatedDataCharts;
  }
  
  render() {
    const style = {
      marginTop: '20px',
      marginBottom: '20px',
    };

    const { alert } = this.state;

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
            Status: {alert.status}
        </Typography>
        <Typography variant="h6" align="center">
            Number of anomalies: {alert.anomalies.length}
        </Typography>
        <center>
          <Button id="status-button" variant="contained" color="primary" component="span" 
            onClick={this.handleStatusChange} style={{ marginTop: '10px', marginBottom: '10px'}}
          >
            {alert.status === UNRESOLVED_STATUS ? "Resolve?" : "Unresolve?"}
          </Button>
        </center>
        <List className="anomalies-list">
          {alert.anomalies.map((anomaly, index) => {
            return (
              <Fragment key={index}>
                <ListItem key={index} dense>
                  <Grid container spacing={3}>
                    <Grid item xs={12}>
                      <ListItemText id={index} 
                        disableTypography
                        primary={
                          <Typography variant="body1" style={{ marginTop: '10px' }}> Anomaly in 
                            <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                              {` ${anomaly.metricName} for ${anomaly.dimensionName}`}
                            </Box> on
                            <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                              {`${anomaly.timestampDate}`}
                            </Box>
                          </Typography>
                        }
                      />
                    </Grid>
                    <Grid item xs={anomaly.relatedDataList.length !== 0 ? 6 : 12}>
                      {this.createLineChart(anomaly, index)}
                    </Grid>
                    <Grid item xs={6}>
                      {this.visualizeRelatedData(anomaly)}
                    </Grid>
                  </Grid>
                </ListItem>
                <Divider style={style} component="li"/>
              </Fragment>
            );
          })}
        </List>
      </div>
    );
  }
}

export default AlertInfo;
