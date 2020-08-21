import React, { Component } from 'react';
import { Switch, Route } from 'react-router-dom';
import Typography from '@material-ui/core/Typography';
import AlertsContent from './AlertsContent';
import AlertInfo from './AlertInfo';
import NavBar from '../NavBar';
import { tabLabels } from './management_constants';
import { getAlertsData } from './management_helpers';
import './AlertsManagement.css';

/*
 * Data structure explanation: (can remove later on)
 * const allAlerts = {id1: {timestampDate, anomalies, status}, ...}; Map between alert id and Object with info
 * const unresolvedAlerts = [id1, id2, ...] which stores the ids of the alerts in allAlerts
 * const resolvedAlerts = [id3, ...] also stores ids of alerts in allAlerts
 */
class AlertsManagement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tab: tabLabels.UNRESOLVED,
      allAlerts: null,
      unchecked: [], 
      checked: [],
    }
  }

  async componentDidMount() {
    var results = await getAlertsData();
    if (results.length != 3) {
      throw new Error("getAlertsData() did not return the correct alerts data.")
    }
    this.setState({
      allAlerts: new Map(results[0]),
      unchecked: results[1].slice(),
      checked: results[2].slice(),
    });
  }

  createRoutes = () => {
    let routes = [];
    this.state.allAlerts.forEach((alertInfo, alertId) => {
      routes.push(
        <Route key={alertId} path={"/alerts/" + alertId} 
          render={() => <AlertInfo alert={alertInfo} />} 
        />
      );
    });
    return routes;
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

    if (currentCheckedIndex === -1 && currentUncheckedIndex !== -1) {
      // Was unresolved and now want to resolve it, second check is a sanity check.
      newChecked.push(alertId);
      newUnchecked.splice(currentUncheckedIndex, 1);
    } else if (currentUncheckedIndex === -1 && currentCheckedIndex !== -1) {
      // Was resolved and now want to unresolve it, second check is a sanity check.
      newUnchecked.push(alertId);
      newChecked.splice(currentCheckedIndex, 1);
    } else {
      throw new Error("Misplaced alert: " + this.state.allAlerts[alertId]);
    }

    this.setState({
      unchecked: newUnchecked,
      checked: newChecked,
    });

    // TODO: send POST request to servlet with status change of alert with alertId.
  };

  render() {
    return (
      <div className="alerts-management">
        <NavBar />
        <Typography variant="h3" gutterBottom id="alerts-header">
          BlackSwan Alerts
        </Typography>
        <Switch>
          <Route exact path="/alerts" 
            render={() => 
              <AlertsContent tab={this.state.tab} allAlerts={this.state.allAlerts} 
                unchecked={this.state.unchecked} checked={this.state.checked} 
                handleTabs={this.handleTabs} handleCheckbox={this.handleCheckbox} 
              />
            } 
          />
          {this.state.allAlerts && this.createRoutes()}
        </Switch>
      </div>
    );
  }
}

export default AlertsManagement;
