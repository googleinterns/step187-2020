import React, { Component } from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Tooltip from '@material-ui/core/Tooltip';

class AlertsList extends Component {
  render() {
    const displayAlerts = this.props.displayAlerts;
    const allAlerts = this.props.allAlerts;
    
    return (
      <List className="alerts-list">
        {displayAlerts.map((alertId, value) => {
          const labelId = `checkbox-list-label-${alertId}`;

          return (
            <ListItem key={value} role={undefined} dense divider>
              <ListItemIcon>
                <Tooltip title="Resolved?" placement="left">
                  <Checkbox
                    edge="start"
                    checked={this.props.checked.indexOf(alertId) !== -1}
                    tabIndex={-1}
                    onClick={() => this.props.handleToggle(alertId)}
                    inputProps={{ 'aria-labelledby': labelId }}
                  />
                </Tooltip>
              </ListItemIcon>
              <ListItemText id={labelId} 
                primary={`Alert ${alertId} on ${allAlerts.get(alertId).timestamp} has ${allAlerts.get(alertId).anomalies} anomalies`} 
              />
            </ListItem>
          );
        })}
      </List>
    );
  }
}

export default AlertsList;