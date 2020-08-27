import React from 'react';
import { Switch, Route } from 'react-router-dom';
import AlertsContent from './AlertsContent';
import AlertInfo from './AlertInfo';

/** Create routes for components to display for alert management. */
export default function ManagementRoutes() {
  return (
    <Switch>
      <Route exact path="/alerts" component={AlertsContent}/>
      <Route path="/alerts/:alertId" component={AlertInfo}/>
    </Switch>
  );
};
