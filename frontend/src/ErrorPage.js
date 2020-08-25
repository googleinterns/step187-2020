import React from 'react';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles({
  root: {
    margin: 'auto',
    width: '100%',
    maxWidth: 800,
    paddingTop: 100,
    textAlign: 'center',
  },
});

export default function ErrorPage() {
  const classes = useStyles();
  return (
    <div className={classes.root}>
      <Typography variant="h1" component="h2" gutterBottom>
        404 Error 
      </Typography>
      <Typography variant="h2" gutterBottom>
        Oops! This isn't a page.
      </Typography>
      <Button href="/" variant="contained" size="large" color="primary">
        Go to home
      </Button>
    </div>
  );
}
