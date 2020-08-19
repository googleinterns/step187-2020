import React from 'react';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import ConfigForm from './ConfigForm'

const useStyles = makeStyles({
  controlPanel: {
    backgroundColor: 'white',
    padding: 50,
    width: '100wv'
  },
  configForm: {
    padding: 25,
  },
});

export default function ControlPanel({ addConfig }) {
  const classes = useStyles();

  const [configForm, setConfigForm] = React.useState(false);
  
  const handleConfigForm = () => {
    setConfigForm(!configForm);
  };

  return(
    <div className={classes.controlPanel}>
      <Container>
        <Typography
          component="h1"
          variant="h2"
          align="center"
          color="textPrimary"
          gutterBottom
        >
          Alert Configuration
        </Typography>
        <Typography
          variant="h5"
          align="center"
          color="textSecondary"
          paragraph
        >
          Create custom configurations for GreySwan alerts
        </Typography>        
        <Grid container spacing={2} justify="center">
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleConfigForm}>
              Create
            </Button>
          </Grid>
          <Grid item>
            <Button variant="outlined" color="primary">
              Delete
            </Button>
          </Grid>
        </Grid>
      </Container>
        { configForm ? <Container className={classes.configForm}><ConfigForm addConfig={addConfig} /></Container> : null }
    </div>
  );
}
