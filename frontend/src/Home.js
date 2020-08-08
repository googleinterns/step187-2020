import React from 'react';
import Typography from '@material-ui/core/Typography';
import './Home.css';

export default function Home(props) {
  return (
    <div className="home-content">
      <Typography component="h1" variant="h3" align="center" color="textPrimary" gutterBottom>
        Welcome to GreySwan!
      </Typography>
      <Typography component="p" color="textPrimary">
        {props.isLoggedIn ? "Thank you for visiting our app!" :  "Please log in."}
      </Typography>
    </div>
  );
}
