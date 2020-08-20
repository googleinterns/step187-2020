import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import App from './App';
import Dummy from './Dummy';
import AlertsManagement from './AlertsManagement/AlertsManagement'
import './index.css';

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Switch>
        <Route exact path="/" component={App}>
        </Route>
        <Route path="/alerts" component={AlertsManagement}>
        </Route>
        {/* Dummy is a temporary component to routing works */}
        <Route path="/configs" component={Dummy}>
          {/* TODO: import AlertConfiguration */}
        </Route>
      </Switch>
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);
