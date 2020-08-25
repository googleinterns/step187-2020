import React, { Component, Fragment } from 'react';
import NavBar from './NavBar';
import Home from './Home';

/**
 * Home page of web application.
 */
class App extends Component {
  render() {
    return (
      <Fragment>
        <NavBar />
        <Home />
      </Fragment>
    );
  }
}

export default App;
