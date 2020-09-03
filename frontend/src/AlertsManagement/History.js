import React, { Component } from 'react';
import Box from '@material-ui/core/Box';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography';
import { getAlertsData } from './management_helpers';
import { formatDate } from '../time_utils';

/**
 * Displays all alert data in a list. Clicking on an alert will take you to that alert's route.
 */
class History extends Component {
  constructor(props) {
    super(props);
    this.state = {
      allAlerts: null,
    }
  }

  async componentDidMount() {
    var results = await getAlertsData();
    if (Object.keys(results).length !== 3) {
      throw new Error("getAlertsData() did not return the correct alerts data.")
    }
    this.setState({
      allAlerts: new Map(results.all),
    });
  }

  createListItems = (allAlerts) => {
    let alertItems = [];
    allAlerts.forEach((alert, alertId) => {
      alertItems.push(
        <ListItem key={alertId} role={undefined} divider 
          onClick={() => this.props.history.push(`alerts/${alertId}`)}
        >
          <ListItemText id={alertId}
            disableTypography
            primary={<Typography variant="body1" >Alert on 
              <Box fontWeight='fontWeightBold' display='inline' m={1} style={{ color: '#0FA3B1'}}>
                {` ${alert.timestampDate} (${formatDate(alert.timestampDate)})`}
              </Box> has
              <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                {` ${alert.anomalies.length} anomalies`}
              </Box> with status
              <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                {` ${alert.status}`}
              </Box>
              </Typography>
            }
          />
        </ListItem>
      );
    });
    return alertItems;
  }

  render() {
    const styles = ({
      root: {
        flexGrow: 1,
        maxWidth: 900,
        margin: 'auto',
      },
    });
    const { allAlerts } = this.state;
    
    if (allAlerts === null) {
      return <div />;
    }

    return (
      <div style={styles.root}>
        <Typography variant="h4" gutterBottom style={{ marginTop: '50px'}}>Alerts Archive</Typography>
        <List className="all-alerts-list">
          {this.createListItems(allAlerts)}
        </List>
      </div>
    );
  }
}

export default History;
