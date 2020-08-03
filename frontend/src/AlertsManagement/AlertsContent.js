import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Box from '@material-ui/core/Box';
import Paper from '@material-ui/core/Paper';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import AllInboxIcon from '@material-ui/icons/AllInbox';
import DoneIcon from '@material-ui/icons/Done';
import ErrorIcon from '@material-ui/icons/Error';
import AlertsList from './AlertsList';

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

let a11yProps = (index) => {
  return {
    id: `tab-${index}`,
    'aria-controls': `tabpanel-${index}`,
  };
}

// TODO: fetch alerts from backend and save in arrays.
const allAlerts = [0, 1, 2, 3, 4, 5, 6];
const unresolvedAlerts = [0, 1, 2, 3];
const resolvedAlerts = [4, 5, 6]

class AlertsContent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: 0,
      unchecked: unresolvedAlerts.slice(), 
      checked: resolvedAlerts.slice(), 
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
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    // TODO: send POST request to servlet with status change of alert at index = value.
  };

  render() {
    const { tab, unchecked, checked } = this.state;
    return (
      <div className={this.props.classes.root}>
        <Paper>
          <Tabs 
            value={tab} 
            onChange={this.handleTabs}
            variant="fullWidth"
            indicatorColor="secondary"
            textColor="secondary" 
            aria-label="alert tabs"
          >
            <Tab icon={<ErrorIcon />} label="Unresolved" {...a11yProps(0)} />
            <Tab icon={<DoneIcon />} label="Resolved" {...a11yProps(1)} />
            <Tab icon={<AllInboxIcon />} label="All alerts" {...a11yProps(2)} />
          </Tabs>
        </Paper>
        <TabPanel value={tab} index={0}>
          <AlertsList 
            allAlerts = {allAlerts}
            alerts={unchecked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={1}>
          <AlertsList 
            allAlerts = {allAlerts}
            alerts={checked} 
            checked={checked}
            handleToggle={this.handleCheckbox} 
          />
        </TabPanel>
        <TabPanel value={tab} index={2}>
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

export default AlertsContent;
