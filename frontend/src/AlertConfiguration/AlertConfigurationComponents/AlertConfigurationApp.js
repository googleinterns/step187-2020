import React, { Fragment }from 'react';
import AppBar from '@material-ui/core/AppBar';
import CssBaseline from '@material-ui/core/CssBaseline';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import AlertConfigurationControlPanel from './AlertConfigurationControlPanel';
import AlertConfigurationCard from './AlertConfigurationCard';
import Footer from './Footer';

const cards = [1, 2, 3, 4, 5, 6, 7, 8, 9]; // Right now this represent the user's previous configuratons

export default function AlertConfiguration() {
  const configurations = (
    cards.map((card) => AlertConfigurationCard(card))
  );

  return (
    <Fragment>
      <CssBaseline/>

      {/* TODO: Replace with Catherine's NavBar */}
      {/* Navigation bar START */}
      <AppBar position="relative">
        <Toolbar>
          <Typography variant="h6" color="inherit" noWrap>
            Alert Configuration
          </Typography>
        </Toolbar>
      </AppBar>
      {/* Navigation bar END */}
      
      <AlertConfigurationControlPanel configurations={configurations}/>
      <Footer />
    </Fragment>
  );
}