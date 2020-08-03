import React, { Component } from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Tooltip from '@material-ui/core/Tooltip';

class AlertsList extends Component {
  render() {
    const alerts = this.props.alerts;

    return (
      <List className="alerts-list">
        {/* alert represents the index of the actual alert in allAlerts array. */}
        {alerts.map((alert, value) => {
          const labelId = `checkbox-list-label-${alert}`;

          return (
            <ListItem key={value} role={undefined} dense divider>
              <ListItemIcon>
                <Tooltip title="Resolved?" placement="left">
                  <Checkbox
                    edge="start"
                    checked={this.props.checked.indexOf(alert) !== -1}
                    tabIndex={-1}
                    onClick={() => this.props.handleToggle(alert)}
                    inputProps={{ 'aria-labelledby': labelId }}
                  />
                </Tooltip>
              </ListItemIcon>
              <ListItemText id={labelId} primary={`Alert number ${this.props.allAlerts[alert] + 1}`} />
            </ListItem>
          );
        })}
      </List>
    );
  }
}

export default AlertsList;