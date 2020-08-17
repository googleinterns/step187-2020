import React, { Component } from 'react';
import Typography from '@material-ui/core/Typography';
import AlertsContent from './AlertsContent';
import AlertInfo from './AlertInfo';
import NavBar from '../NavBar';
import './AlertsManagement.css';

class AlertsManagement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showInfo: false,
      currentAlert: null,
    }
  }

  changeToInfo = (alert) => {
    this.setState({ 
      showInfo: true,
      currentAlert: alert, 
    });
  }

  changeToTabs = () => {
    this.setState({ showInfo: false });
  }

  render() {
    return (
      <div className="alerts-management">
        <NavBar />
        <Typography variant="h3" gutterBottom id="alerts-header">
          BlackSwan Alerts
        </Typography>
        {this.state.showInfo ? 
          <AlertInfo alert={this.state.currentAlert} changeDisplay={this.changeToTabs}/> : 
          <AlertsContent changeDisplay={this.changeToInfo}/>
        }
      </div>
    );
  }
}

export default AlertsManagement;
