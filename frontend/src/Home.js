import React, { useState, useEffect } from 'react';
import './Home.css';
import { getLoginStatus } from './login_helper';
import Typography from '@material-ui/core/Typography';

export default function Home() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    async function fetchLogin() {
      const result = await getLoginStatus();
      setIsLoggedIn(result.isLoggedIn);
    }
    fetchLogin();
  }, []);

  return (
    <div className="home-content">
      <Typography variant="h2" style={{ margin: '30px'}}>
        Welcome to GreySwan!  
      </Typography>
      <img src={require('./greyswan.png')} alt="Grey swan" height="100" width="100" style={{ marginLeft: '10px'}} />
      <Typography variant="h4" className="message" style={{ margin: '30px' }}>
        {isLoggedIn ? "Thank you for visiting our app!" :  "Please log in."}
      </Typography>
    </div>
  );
}
