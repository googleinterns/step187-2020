import React, { Fragment }from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import AlertConfigurationControlPanel from './AlertConfigurationControlPanel';
import AlertConfigurationCard from './AlertConfigurationCard';
// import Footer from '../Footer';
import NavBar from '../NavBar';

const cards = [1, 2, 3, 4, 5, 6, 7, 8, 9]; // Right now this represent the user's previous configuratons

export default function AlertConfiguration() {
  const configurations = (
    cards.map((card) => AlertConfigurationCard(card))
  );

  return (
    <Fragment>
      <CssBaseline/>
      <NavBar />
      <AlertConfigurationControlPanel configurations={configurations}/>
      {/* <Footer /> */}
    </Fragment>
  );
} 
