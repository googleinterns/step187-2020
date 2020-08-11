import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Box from '@material-ui/core/Box';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import AllInboxIcon from '@material-ui/icons/AllInbox';
import DoneIcon from '@material-ui/icons/Done';
import ErrorIcon from '@material-ui/icons/Error';
import AlertsList from './AlertsList';

const styles = ({
  root: {
    flexGrow: 1,
    maxWidth: 900,
    margin: 'auto',
  },
});

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tabpanel-${index}`}
      aria-labelledby={`tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box p={3}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function createDate(timestampDate) {
  return new Date(
    timestampDate.date.year, timestampDate.date.month - 1, timestampDate.date.day, 0, 0, 0, 0);
}

function Alert(props) {
  return (
    <li>
      Alert {props.index} at {props.timestamp} has {props.anomalies} anomalies
    </li>
  );
}

let allAlerts = [];
let unresolvedAlerts = [];
let resolvedAlerts = [];

// let allAlerts = new Map();

const getAlerts = () => {
  fetch('/api/v1/alerts-data').then(response => response.json()).then(alerts => {
    alerts.forEach((alert, index) => {
      allAlerts.push(
        <Alert 
          index={index}
          timestamp={createDate(alert.timestampDate)}
          anomalies={alert.anomalies.length}
        />
      );
      // allAlerts.set(index, {timestamp: createDate(alert.timestampDate), anomalies: alert.anomalies.length});
      if (alert.status === "UNRESOLVED") {
        unresolvedAlerts.push(index);
      } else {
        resolvedAlerts.push(index);
      }
    });
  });
  // console.log(allAlerts)
}

// TODO: fetch alerts from backend and save in arrays.
// Intended plan for reference: 
// const allAlerts = [id1, id2, id3] where id'x' is the id of AlertX
// const alertsMap = {id1: <Alert1>, 2: <Alert2>, 3: <Alert3>, ...};
// const unresolvedAlerts = [0, 1] which stores the indices of the alerts in allAlerts
// const resolvedAlerts = [2]

// const allAlerts = [0, 1, 2, 3, 4, 5, 6];
// const unresolvedAlerts = [0, 1, 2, 3];
// const resolvedAlerts = [4, 5, 6];

// TODO: create constants file. ref: https://stackoverflow.com/questions/39036457/react-create-constants-file.
const tabLabels = {
  UNRESOLVED: 0,
  RESOLVED: 1,
  ALL: 2,
};

class AlertsContent extends Component {
  constructor(props) {
    super(props);
    // getAlerts();
    this.state = {
      tab: tabLabels.UNRESOLVED,
      unchecked: unresolvedAlerts.slice(), 
      checked: resolvedAlerts.slice(), 
    };
  }

  a11yProps = (index) => {
    return {
      id: `tab-${index}`,
      'aria-controls': `tabpanel-${index}`,
    };
  }
  
  handleTabs = (event, newTab) => {
    this.setState({tab: newTab});
  };

  handleCheckbox = (value) => {
    const unchecked = this.state.unchecked.slice();
    const checked = this.state.checked.slice();
    const newUnchecked = [...unchecked];
    const newChecked = [...checked];

    const currentUncheckedIndex = unchecked.indexOf(value);
    const currentCheckedIndex = checked.indexOf(value);

    if (currentCheckedIndex === -1 && currentUncheckedIndex !== -1) {
      // Was unresolved and now want to resolve it, second check is a sanity check.
      newChecked.push(value);
      newUnchecked.splice(currentUncheckedIndex, 1);
    } else if (currentUncheckedIndex === -1 && currentCheckedIndex !== -1) {
      // Was resolved and now want to unresolve it, second check is a sanity check.
      newUnchecked.push(value);
      newChecked.splice(currentCheckedIndex, 1);
    } else {
      // This should never happen (programmer error).
      throw new Error("Misplaced alert: " + allAlerts[value]);
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    // TODO: send POST request to servlet with status change of alert at index = value.
  };

  render() {
    const { tab, unchecked, checked } = this.state;
    const { classes } = this.props;
    // getAlerts();
    return (
      <div className={classes.root}>
        <Paper>
          <Tabs 
            value={tab} 
            onChange={this.handleTabs}
            variant="fullWidth"
            indicatorColor="secondary"
            textColor="secondary" 
            aria-label="alert tabs"
          >
            <Tab icon={<ErrorIcon />} label="Unresolved" {...this.a11yProps(tabLabels.UNRESOLVED)} />
            <Tab icon={<DoneIcon />} label="Resolved" {...this.a11yProps(tabLabels.RESOLVED)} />
            <Tab icon={<AllInboxIcon />} label="All alerts" {...this.a11yProps(tabLabels.ALL)} />
          </Tabs>
        </Paper>
        <TabPanel value={tab} index={tabLabels.UNRESOLVED}>
          <AlertsList 
            allAlerts = {allAlerts}
            alerts={unchecked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.RESOLVED}>
          <AlertsList 
            allAlerts = {allAlerts}
            alerts={checked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.ALL}>
          <AlertsList 
            allAlerts = {allAlerts}
            alerts={unchecked.concat(checked)} 
            checked={checked}
            handleToggle={this.handleCheckbox}
          />
        </TabPanel>
      </div>
    );
  }
}

AlertsContent.propTypes = {
  classes: PropTypes.object.isRequired,
}

export { AlertsContent as PureAlertsContent };
export default withStyles(styles)(AlertsContent);
