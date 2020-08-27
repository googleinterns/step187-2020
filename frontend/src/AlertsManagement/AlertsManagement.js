import React, { Component } from 'react';
import Typography from '@material-ui/core/Typography';
import ManagementRoutes from './ManagementRoutes';
import NavBar from '../NavBar';
import './AlertsManagement.css';

/**
 * Parent component for the AlertsManagement page.
 * Contains NavBar, page title, and routes for the child components.
 */
class AlertsManagement extends Component {
  render() {
    return (
      <div className="alerts-management">
        <NavBar />
        <Typography variant="h3" gutterBottom id="alerts-header">
          BlackSwan Alerts
        </Typography>
        <ManagementRoutes />
      </div>
    );
  }
}

export default AlertsManagement;
