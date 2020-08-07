import React from 'react';
import './Home.css';

export default function Home(props) {
  return (
    <div className="home-content">
      <h1>Welcome to GreySwan!</h1>
      <p className="message">
        {props.isLoggedIn ? "Thank you for visiting our app!" :  "Please log in."}
      </p>
    </div>
  );
}
