import React, { Component } from 'react';
import { withStyles } from '@material-ui/core/styles';
import AlertsContent from './AlertsContent'
import NavBar from '../NavBar';
import './AlertsManagement.css';

const styles = ({
  root: {
    flexGrow: 1,
    maxWidth: 900,
    margin: 'auto',
  },
});

class AlertsManagement extends Component {
  render() {
    const { classes } = this.props;
    return (
      <div className="alerts-management">
        <NavBar />
        <h1>BlackSwan Alerts</h1>
        <AlertsContent classes={classes}/>
      </div>
    );
  }
}

export default withStyles(styles)(AlertsManagement);
