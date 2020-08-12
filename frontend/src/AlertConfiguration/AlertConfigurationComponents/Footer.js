import React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Container from '@material-ui/core/Container';
import Link from '@material-ui/core/Link';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles((theme) => ({
  margin: {
    marginTop: 50,
  },
  footer: {
    padding: theme.spacing(3, 2),
    marginTop: 'auto',
    backgroundColor: '#3f50b5',
    color: "white",
  },
}));

function Copyright() {
  return (
    <Typography variant="body2">
      {'Copyright © '}
      <Link color="inherit" href="https://material-ui.com/">
        GreySwan
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

export default function Footer() {
  const classes = useStyles();

  return (
    <div className={classes.margin}>
      <CssBaseline />
      <footer className={classes.footer}>
        <Container maxWidth="sm" align="center">
          <Typography variant="body1">STEP 2020</Typography>
          <Copyright />
        </Container>
      </footer>
    </div>
  );
}