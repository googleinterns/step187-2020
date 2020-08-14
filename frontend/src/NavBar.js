import React from 'react';
import { NavLink } from 'react-router-dom';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';

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

export default function NavBar(props) {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar className={classes.links}>
          <Typography variant="h6" className={classes.title} component={CustomLink} to="/">
            GreySwan
          </Typography>
          <Button color="inherit" component={CustomLink} to="/alerts">Alerts</Button>
          <Button color="inherit" component={CustomLink} to="/configs">Configs</Button>
          <Button color="inherit" href={props.logURL}>
            {props.isLoggedIn ? "Logout" :  "Login"}
          </Button>
        </Toolbar>
      </AppBar>
    </div>
  );
}
