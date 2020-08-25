import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import App from './App';
import Dummy from './Dummy';
import './index.css';
/**Remove afterwards */
import AlertConfiguration from './AlertConfiguration/AlertConfigurationComponents/AlertConfiguration'

ReactDOM.render(
  <React.StrictMode>
    <BrowserRouter>
      <Switch>
        <Route exact path="/" component={App}>
        </Route>
        {/* Dummy is a temporary component to routing works */}
        <Route path="/alerts" component={Dummy}>
          {/* TODO: import AlertsManagement */}
        </Route>
        <Route path="/configs" component={AlertConfiguration}>
          {/* TODO: import AlertConfiguration */}
        </Route>
      </Switch>
    </BrowserRouter>
  </React.StrictMode>,
  document.getElementById('root')
);
