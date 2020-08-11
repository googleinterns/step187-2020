import React, { Component } from 'react';
import Typography from '@material-ui/core/Typography';
import AlertsContent from './AlertsContent'
// import Footer from '../Footer';
import NavBar from '../NavBar';
import './AlertsManagement.css';

class AlertsManagement extends Component {
  render() {
    return (
      <div className="alerts-management">
        <NavBar />
        <Typography component="h1" variant="h4" align="center" color="textPrimary" gutterBottom>
            BlackSwan Alerts
        </Typography>
        <AlertsContent />
        {/* <Footer /> */}
      </div>
    );
  }
}

export default AlertsManagement;
