import React, { Component } from 'react';
import logo from './logo.svg';
import "./App.css";


class App extends Component {
  // Test to see if backend servlet connects to frontend.
  componentDidMount() {
    console.log("Inside componentDidMount!");
    fetch("/blackswan/test")
      .then((response) => response.text())
      .then((text) => {
        console.log("here is the text from servlet: ", text);
      });
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React</h2>
        </div>
      </div>
    );
  }
}

export default App;
