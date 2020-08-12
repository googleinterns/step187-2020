import React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import ConfigCard from './ConfigCard';

export default function ConfigList({ configs }) {
  const configList = (
    configs.map((config) => ConfigCard(config))
  );

  return(
    <div>
      <CssBaseline />
      { configList }
    </div>
  );
}