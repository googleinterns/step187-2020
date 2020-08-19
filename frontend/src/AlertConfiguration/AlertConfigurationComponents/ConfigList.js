import React from 'react';
import Container from '@material-ui/core/Container';
import { makeStyles } from '@material-ui/core/styles';
import ConfigCard from './ConfigCard';

const useStyles = makeStyles({
  configList: {
    marginTopBottom: '50',
  }
});

export default function ConfigList({ configs }) {
  const classes = useStyles();

  const configList = (
    configs.map((config) => ConfigCard(config))
  );

  return(
    <Container className={classes.configList} maxWidth="md">
      { configList }
    </Container>
  );
}
