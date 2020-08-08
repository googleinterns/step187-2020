import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import CreateAlertConfiguration from './CreateAlertConfiguration'

const useStyles = makeStyles((theme) => ({
  heroContent: {
    padding: theme.spacing(8, 0, 6),
    marginBottom: 20,
  },
}));

export default function AlertConfigurationControlPanel(props) {
  const classes = useStyles();

  var [clicked, setClick] = React.useState(false);

  function handleClick() {
    setClick(true);
  }

  if (!clicked) {
    return(
      <main className={classes.main}>
        <Container className={classes.heroContent}>
          <Typography component="h1" variant="h4" align="center" color="textPrimary" gutterBottom>
            Alert Configuration
          </Typography>
          <Typography variant="h5" align="center" color="textSecondary" paragraph>
            Create custom configurations for GreySwan alerts
          </Typography>        
          <div>
            <Grid container spacing={2} justify="center">
              <Grid item>
                <Button onClick={() => handleClick()} variant="contained" color="primary">
                  Create
                </Button>
              </Grid>
              <Grid item>
                <Button variant="outlined" color="primary">
                  Delete
                </Button>
              </Grid>
            </Grid>
          </div>
        </Container>
        <Divider />
        {props.configurations}
        {clicked = false}
      </main>
    );
  } else {
    return(
      <main>
        <CreateAlertConfiguration/>
        {clicked = false}
      </main>
    );
  }
} 
