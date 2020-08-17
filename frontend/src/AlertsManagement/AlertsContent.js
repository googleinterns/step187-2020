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
import { tabLabels } from './management_constants';

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

/*
 * Data structure explanation: (can remove later on)
 * const allAlerts = {id1: {timestampDate, anomalies, status}, ...};
 * const unresolvedAlerts = [id1, id2, ...] which stores the ids of the alerts in allAlerts
 * const resolvedAlerts = [id3, ...] also stores ids of alerts in allAlerts
 */
class AlertsContent extends Component {
  a11yProps = (index) => {
    return {
      id: `tab-${index}`,
      'aria-controls': `tabpanel-${index}`,
    };
  }

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
