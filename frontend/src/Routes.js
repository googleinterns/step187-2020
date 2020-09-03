import React from 'react';
import { Route, Switch } from 'react-router-dom';
import AlertConfiguration from './AlertConfiguration/AlertConfigurationComponents/AlertConfiguration';
import AlertsManagement from './AlertsManagement/AlertsManagement';
import App from './App';
import ErrorPage from './ErrorPage';
import History from './AlertsManagement/History';

/** Create routes for application. */
export default function Routes() {
  return (
    <Switch>
      <Route exact path="/" component={App} />
      <Route path="/alerts" component={AlertsManagement} />
      <Route path="/history" component={History} />
      <Route path="/configs" component={AlertConfiguration} />
      <Route component={ErrorPage} />
    </Switch>
  );
}
