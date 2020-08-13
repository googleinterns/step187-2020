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

// TODO: create constants file. ref: https://stackoverflow.com/questions/39036457/react-create-constants-file.
const tabLabels = {
  UNRESOLVED: 0,
  RESOLVED: 1,
  ALL: 2,
};

/*
 * Helper function to create Date from Timestamp model.
 */
const createDate = (timestampDate) => {
  return new Date(
    timestampDate.date.year, timestampDate.date.month - 1, timestampDate.date.day, 0, 0, 0, 0)
    .toDateString();
}

/*
 * Data structure explanation: (can remove later on)
 * const allAlerts = {id1: {timestamp, # of anomalies}, id2: {timestamp, # of anomalies}, ...};
 * const unresolvedAlerts = [id1, id2, ...] which stores the ids of the alerts in allAlerts
 * const resolvedAlerts = [id3, ...] also stores ids of alerts in allAlerts
 */
class AlertsContent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: tabLabels.UNRESOLVED,
      allAlerts: new Map(),
      unchecked: [], 
      checked: [], 
    };
  }

  a11yProps = (index) => {
    return {
      id: `tab-${index}`,
      'aria-controls': `tabpanel-${index}`,
    };
  }

  async componentDidMount() {
    let unresolvedAlerts = [];
    let resolvedAlerts = [];

    const alertsResponse = await fetch('/api/v1/alerts-data').then(response => response.json());
    alertsResponse.forEach((alert, value) => {
      // TODO: replace value with actual alert ID (received from JSON, e.g. alert.id).
      this.state.allAlerts.set(value, {
        timestamp: createDate(alert.timestampDate), 
        anomalies: alert.anomalies.length
      });
      if (alert.status === "UNRESOLVED") {
        unresolvedAlerts.push(value);
      } else {
        resolvedAlerts.push(value);
      }
    });

    this.setState({
      unchecked: unresolvedAlerts.slice(),
      checked: resolvedAlerts.slice(),
    });
  }
  
  handleTabs = (event, newTab) => {
    this.setState({tab: newTab});
  };

  handleCheckbox = (alertId) => {
    const unchecked = this.state.unchecked.slice();
    const checked = this.state.checked.slice();
    const newUnchecked = [...unchecked];
    const newChecked = [...checked];

    const currentUncheckedIndex = unchecked.indexOf(alertId);
    const currentCheckedIndex = checked.indexOf(alertId);

    if (currentCheckedIndex === -1 && currentUncheckedIndex !== -1) {
      // Was unresolved and now want to resolve it, second check is a sanity check.
      newChecked.push(alertId);
      newUnchecked.splice(currentUncheckedIndex, 1);
    } else if (currentUncheckedIndex === -1 && currentCheckedIndex !== -1) {
      // Was resolved and now want to unresolve it, second check is a sanity check.
      newUnchecked.push(alertId);
      newChecked.splice(currentCheckedIndex, 1);
    } else {
      throw new Error("Misplaced alert: " + this.state.allAlerts[alertId]);
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    // TODO: send POST request to servlet with status change of alert with alertId.
  };

  render() {
    const { tab, allAlerts, unchecked, checked } = this.state;
    const { classes } = this.props;
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
            allAlerts={allAlerts}
            displayAlerts={unchecked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.RESOLVED}>
          <AlertsList 
            allAlerts={allAlerts}
            displayAlerts={checked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.ALL}>
          <AlertsList 
            allAlerts={allAlerts}
            displayAlerts={unchecked.concat(checked)} 
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
