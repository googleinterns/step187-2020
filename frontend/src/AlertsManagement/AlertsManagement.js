import React, { Component } from 'react';
import AlertsContent from './AlertsContent'
import NavBar from '../NavBar';
import './AlertsManagement.css';

class AlertsManagement extends Component {
  render() {
    return (
      <div className="alerts-management">
        <NavBar />
        <h1>BlackSwan Alerts</h1>
        <AlertsContent />
      </div>
    );
  }
}

export default AlertsManagement;
