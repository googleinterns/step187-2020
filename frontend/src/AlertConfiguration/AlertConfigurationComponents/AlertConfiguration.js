import React, { useState, Fragment } from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from '@material-ui/core/styles';
import ControlPanel from './ControlPanel';
import Footer from './Footer';
import ConfigList from './ConfigList';
import NavBar from '../../NavBar';

const useStyles = makeStyles((theme) => ({
  main: {
    display: 'flex',
    flexDirection: 'column',
    minHeight: '100vh',
  },
}));

/** 
 * The Alert Configuration Feature consists of ControlPanel (a control panel used for creating new configurations and 
 * deleting old configurations) as well as a ConfigList (a list of the user's current configurations).
*/
export default function AlertConfiguration() {
  const classes = useStyles();

  const [configs, setConfigs] = useState([]);

  fetch("/api/v1/configurations").then(response => response.json()).then(res => setConfigs(res));

  function addConfig(config) {
    setConfigs([config, ...configs]);
  }

  return (
    <Fragment>
      <CssBaseline />
      <NavBar />
      <main className={classes.main}>
        <ControlPanel addConfig={addConfig}/>
        <ConfigList configs={configs}/>
      </main>
      <Footer />
    </Fragment>
  );
}
