import React, { Component, Fragment } from 'react';
import NavBar from './NavBar';
import Home from './Home';

const LOGGED_IN_STATUS = "logged in";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = { 
      isLoggedIn: false, 
      logURL: "" 
    };
  }

  async componentDidMount() {
    const loginResponse = await fetch("/api/v1/login").then(response => response.text());

    // results[0] is the login status, results[1] is the login or logout URL.
    const results = loginResponse.split("\n");
    if (results[0] === LOGGED_IN_STATUS) {
      this.setState({ isLoggedIn: true });
    } else {
      this.setState({ isLoggedIn: false });
    }
    this.setState({ logURL: results[1] });  
  }

  render() {
    return (
      <Fragment>
        <NavBar isLoggedIn={this.state.isLoggedIn} logURL={this.state.logURL} />
        <Home isLoggedIn={this.state.isLoggedIn} />
      </Fragment>
    );
  }
}

export default App;
