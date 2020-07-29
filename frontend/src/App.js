import React, { Component } from 'react';
import NavBar from './NavBar';
import "./App.css";


class App extends Component {
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
      <div className="Home">
        <NavBar />
        <HomeContent />
      </div>
    );
  }
}

class HomeContent extends Component {
  constructor(props) {
    super(props);
    this.state = {isLoggedIn : false};
  }

  render () {
    return (
      <div className="homeContent">
        <h1>Welcome to GreySwan!</h1>
        <p>{this.state.isLoggedIn ? "Thank you for visiting our app!" :  "Please log in." }</p>
      </div>
    );
  }
}

export default App;
