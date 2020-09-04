import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import Box from '@material-ui/core/Box';
import InputLabel from '@material-ui/core/InputLabel';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import AllInboxIcon from '@material-ui/icons/AllInbox';
import DoneIcon from '@material-ui/icons/Done';
import ErrorIcon from '@material-ui/icons/Error';
import AlertsList from './AlertsList';
import { getAlertsData } from './management_helpers';
import { tabLabels, UNRESOLVED_STATUS, RESOLVED_STATUS, DEFAULT_ALERTS_LIMIT } from './management_constants';

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
          <Typography component={'span'} variant={'body2'}>{children}</Typography>
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

/**
 * Requests most recent x alerts, where x is pre-specified limit, and displays basic information
 * for each alert, organized by unresolved, resolved, and all alerts in tabs.
 * Data for alerts to display are passed down to AlertsList components.
 * Sends a POST request when the user toggles a checkbox to resolve or unresolve and alert.
 * Component state:
 * tab = tabLabel, enum representing the current tab that is being displayed
 * allAlerts = {id1: {timestampDate, anomalies, status}, ...}; a Map between alert id and Object with info
 * unchecked = [id1, id2, ...]; an Array that stores ids of unresolved alerts in allAlerts
 * checked = [id3, ...]; an Array that stores ids of resolved alerts in allAlerts
 */
class AlertsContent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: tabLabels.UNRESOLVED,
      allAlerts: null,
      unchecked: [], 
      checked: [],
      alertsLimit: DEFAULT_ALERTS_LIMIT,
    }
  }

  a11yProps = (index) => {
    return {
      id: `tab-${index}`,
      'aria-controls': `tabpanel-${index}`,
    };
  }

  async componentDidMount() {
    var results = await getAlertsData(this.state.alertsLimit);
    if (results.length !== 3) {
      throw new Error("getAlertsData() did not return the correct alerts data.")
    }
    this.setState({
      allAlerts: new Map(results[0]),
      unchecked: results[1].slice(),
      checked: results[2].slice(),
    });
  }

  async componentDidUpdate(_prevProps, prevState) {
    if (this.state.alertsLimit !== prevState.alertsLimit) {
      await this.componentDidMount();
    }
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

    let statusToChangeTo;

    if (currentCheckedIndex === -1 && currentUncheckedIndex !== -1) {
      // Was unresolved and now want to resolve it, second check is a sanity check.
      newChecked.push(alertId);
      newUnchecked.splice(currentUncheckedIndex, 1);
      statusToChangeTo = RESOLVED_STATUS;
    } else if (currentUncheckedIndex === -1 && currentCheckedIndex !== -1) {
      // Was resolved and now want to unresolve it, second check is a sanity check.
      newUnchecked.push(alertId);
      newChecked.splice(currentCheckedIndex, 1);
      statusToChangeTo = UNRESOLVED_STATUS;
    } else {
      throw new Error("Misplaced alert: " + this.state.allAlerts[alertId]);
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    fetch('/api/v1/alerts-data?id=' + alertId + '&status=' + statusToChangeTo, {
      method: 'POST'
    });
  };

  handleAlertsLimitChange = (event) => {
    this.setState({ alertsLimit: event.target.value });
  }

  render() {
    const { tab, allAlerts, unchecked, checked, alertsLimit } = this.state;
    const { classes } = this.props;
    return (
      <div className={classes.root}>
        <form align="center">
          <InputLabel>View # of</InputLabel>
          <TextField
            id="alert-limit"
            type="number"
            label="alerts:"
            size="small"
            InputProps={{ inputProps: { min: 0, max: 10 } }}
            value={alertsLimit}
            onChange={this.handleAlertsLimitChange}
          />
        </form>
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
