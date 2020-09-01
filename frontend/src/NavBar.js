import React, { Fragment, useState, useEffect } from 'react';
import { NavLink } from 'react-router-dom';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import { getLoginStatus } from './login_helper';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  },
}));

const CustomLink = React.forwardRef((props, ref) => (
  <NavLink 
    ref={ref} 
    style={{
      color: 'inherit',
      textDecoration: 'none',
    }}
    {...props} 
  />
));

export default function NavBar() {
  const classes = useStyles();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [logURL, setLogURL] = useState("");

  useEffect(() => {
    async function fetchLogin() {
      const result = await getLoginStatus();
      setIsLoggedIn(result.isLoggedIn);
      setLogURL(result.logURL);
    }
    fetchLogin();
  }, []);
  
  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar className={classes.links}>
          <Typography variant="h6" className={classes.title} component={CustomLink} to="/">
            GreySwan
          </Typography>
          {/* {isLoggedIn ?  */}
            <Fragment>
              <Button color="inherit" id="alerts-button"
                component={CustomLink} to="/alerts"
              >
                Alerts
              </Button>
              <Button color="inherit" id="history-button" 
                component={CustomLink} to="/history"
              >
                History
              </Button>
              <Button color="inherit" id="configs-button" 
                component={CustomLink} to="/configs"
              >
                Configs
              </Button>
            </Fragment>
            {/* : null} */}
          <Button color="inherit" href={logURL} id="login-button">
            {isLoggedIn ? "Logout" :  "Login"}
          </Button>
        </Toolbar>
      </AppBar>
    </div>
  );
}
