import React, { Component } from 'react';
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
    console.log("Inside componentDidMount!");
    fetch("/api/v1/test-servlet")
      .then((response) => response.text())
      .then((text) => {
        console.log("here is the text from servlet: ", text);
      });
  }

  render() {
    return (
      <div className="home">
        <NavBar />
        <Home isLoggedIn={this.state.isLoggedIn} />
      </div>
    );
  }
}

export default App;
