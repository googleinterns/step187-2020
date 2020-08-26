import React from 'react';
import Container from '@material-ui/core/Container';
import { makeStyles } from '@material-ui/core/styles';
import ConfigCard from './ConfigCard';

const useStyles = makeStyles({
  configList: {
    marginTopBottom: '50',
  }
});

/** 
 * ConfigList is a list that visually represents information about all configurations.
 * It takes as input configs (an array of all user configurations).
 */
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
