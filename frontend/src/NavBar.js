import React from 'react';
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

export default function NavBar(props) {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            GreySwan
          </Typography>
          <Button color="inherit">Alerts</Button>
          <Button color="inherit">Configs</Button>
          <Button color="inherit" href={props.logURL}>
            {props.isLoggedIn ? "Logout" :  "Login"}
          </Button>
        </Toolbar>
      </AppBar>
    </div>
  );
}
