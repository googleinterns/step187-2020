import React, { useState, useEffect } from 'react';
import './Home.css';
import { getLoginStatus } from './login_helper';

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
      <h1>Welcome to GreySwan!</h1>
      <p className="message">
        {isLoggedIn ? "Thank you for visiting our app!" :  "Please log in."}
      </p>
    </div>
  );
}
