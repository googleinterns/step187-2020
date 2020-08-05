import React, { Component, Fragment } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import NavBar from './NavBar';
import Home from './Home';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoggedIn: false,
    };
  }

  // Test to see if backend servlet connects to frontend.
  componentDidMount() {
    fetch("/api/v1/test-servlet")
      .then((response) => response.text())
      .then((text) => {
        console.log("here is the text from servlet: ", text);
      });
  }

  render() {
    return (
      <BrowserRouter>
        <Fragment>
          <NavBar />
          <Switch>
            <Route path="/">
              <Home isLoggedIn={this.state.isLoggedIn} />
            </Route>
            <Route path="/alerts">
              {/* TODO: import AlertsManagement */}
            </Route>
            <Route path="/configs">
              {/* TODO: import AlertConfiguration */}
            </Route>
          </Switch>
        </Fragment>
      </BrowserRouter>
    );
  }
}

export default App;
