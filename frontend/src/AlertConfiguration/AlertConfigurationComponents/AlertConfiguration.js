import React, { useState, Fragment } from 'react';
import AppBar from '@material-ui/core/AppBar';
import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import ControlPanel from './ControlPanel';
import Footer from './Footer';
import ConfigList from './ConfigList';

const useStyles = makeStyles((theme) => ({
  main: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
  },
}));

/** Alert Configuration Feature
 * Consists of ControlPanel (a control panel used for creating new configurations and deleting old configurations)
 * as well as a ConfigList (a list of the user's current configurations)
*/
export default function AlertConfiguration() {
  const classes = useStyles();

  const [configs, setConfigs] = useState([]);

  function addConfig(config) {
    setConfigs([config, ...configs]);
  }

  return (
    <Fragment>
      <CssBaseline />
      {/** TODO: Add Catherine's NavBar during integration*/}
      <main className={classes.main}>
        <ControlPanel addConfig={addConfig}/>
        <ConfigList configs={configs}/>
      </main>
      <Footer />
    </Fragment>
  );
}
