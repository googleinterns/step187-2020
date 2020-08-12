import React from 'react';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import Grid from '@material-ui/core/Grid';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import ConfigForm from './ConfigForm'

const useStyles = makeStyles((theme) => ({
  controlPanel: {
    backgroundColor: theme.palette.background.paper,
    padding: theme.spacing(8, 0, 6),
    marginBottom: 10,
  },
  configForm: {
    backgroundColor: theme.palette.background.paper,
    padding: theme.spacing(8, 0, 6),
  },
}));

export default function ControlPanel({ addConfig }) {
  const classes = useStyles();

  const [showConfigForm, setShowConfigForm] = React.useState(false);
  
  const handleShowConfigForm = () => {
    setShowConfigForm(!showConfigForm);
  };

  return(
    <div>
      <CssBaseline />
      <div className={classes.controlPanel}>
        <Typography component="h1" variant="h2" align="center" color="textPrimary" gutterBottom>
          Alert Configuration
        </Typography>
        <Typography variant="h5" align="center" color="textSecondary" paragraph>
          Create custom configurations for GreySwan alerts
        </Typography>        
        <Grid container spacing={2} justify="center">
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleShowConfigForm}>Create</Button>
          </Grid>
          <Grid item>
            <Button variant="outlined" color="primary">Delete</Button>
          </Grid>
        </Grid>
      </div>
      { showConfigForm ? <div className={classes.configForm}><ConfigForm addConfig={addConfig} /></div>: null }
    </div>
  );
}