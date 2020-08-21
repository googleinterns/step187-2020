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
import { convertTimestampToDate } from '../time_utils';
import { tabLabels, UNRESOLVED_STATUS, RESOLVED_STATUS } from './management_constants';

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

class AlertsContent extends Component {
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
    alertsResponse.forEach((alert) => {
      const alertId = alert.id.value;
      this.state.allAlerts.set(alertId, {
        timestamp: convertTimestampToDate(alert.timestampDate), 
        anomalies: alert.anomalies.length
      });
      if (alert.status === UNRESOLVED_STATUS) {
        unresolvedAlerts.push(alertId);
      } else {
        resolvedAlerts.push(alertId);
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

    let changeStatus;

    if (currentCheckedIndex === -1 && currentUncheckedIndex !== -1) {
      // Was unresolved and now want to resolve it, second check is a sanity check.
      newChecked.push(alertId);
      newUnchecked.splice(currentUncheckedIndex, 1);
      changeStatus = RESOLVED_STATUS;
    } else if (currentUncheckedIndex === -1 && currentCheckedIndex !== -1) {
      // Was resolved and now want to unresolve it, second check is a sanity check.
      newUnchecked.push(alertId);
      newChecked.splice(currentCheckedIndex, 1);
      changeStatus = UNRESOLVED_STATUS;
    } else {
      throw new Error("Misplaced alert: " + this.state.allAlerts[alertId]);
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    fetch('/api/v1/alerts-data', {
      method: 'POST',
      body: alertId + " " + changeStatus,
    });
  };

  render() {
    const { classes, tab, allAlerts, unchecked, checked, handleTabs, handleCheckbox } = this.props;
    return (
      <div className={classes.root}>
        <Paper>
          <Tabs 
            value={tab} 
            onChange={handleTabs}
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
            handleToggle={handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.RESOLVED}>
          <AlertsList 
            allAlerts={allAlerts}
            displayAlerts={checked} 
            checked={checked}
            handleToggle={handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={tabLabels.ALL}>
          <AlertsList 
            allAlerts={allAlerts}
            displayAlerts={unchecked.concat(checked)} 
            checked={checked}
            handleToggle={handleCheckbox}
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
