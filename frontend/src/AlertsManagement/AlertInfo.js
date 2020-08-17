import React, { Fragment } from 'react';
import { Line } from 'react-chartjs-2';
import { makeStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography';
import ArrowBackIcon from '@material-ui/icons/ArrowBack';

const useStyles = makeStyles({
  divider: {
    marginTop: '20px',
    marginBottom: '20px',
  },
});

export default function AlertInfo(props) {
  const classes = useStyles();
  const alert = props.alert;

  return (
    <div className="alert-info">
      <Divider variant="middle"/>
      <IconButton size="small" id="back-button" onClick={props.changeDisplay}>
          <ArrowBackIcon />
      </IconButton>
      <Typography variant="h5" align="center">
        Alert on {alert.timestampDate}
      </Typography>
      <Typography variant="h6" align="center">
          Status: {alert.status}
        </Typography>
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
            <Fragment>
              <ListItem key={index} role={undefined} dense>
                <ListItemText id={index} 
                  primary={`Anomaly in ${anomaly.metricName} for ${anomaly.dimensionName} on ${anomaly.timestampDate}`} 
                />
              </ListItem>
              <Line key={index} data={chartData} />
              <Divider className={classes.divider} variant="inset" component="li"/>
            </Fragment>
          );
        })}
      </List>
    </div>
  );
}

