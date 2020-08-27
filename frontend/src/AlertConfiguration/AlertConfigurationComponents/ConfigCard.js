import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';


const useStyles = (theme) => makeStyles({
  card: {
    paddingTopBottom: 50,
    marginTop: 50,
    marginBottom: 50,
  },
  cardContent: {
    flexGrow: 1,
  },
});

/** 
 * ConfigCard is a card that visually represents information about a single configuration.
 * It takes as input card (an object that stores data for a single configuration).
*/
export default function ConfigCard(card) {
  const classes = useStyles();

  return (
    <Card className={classes.card} style={{marginTop: "50px", marginBottom: "50px"}}>
      <CardContent className={classes.cardContent}>
        <Typography
          gutterBottom
          variant="h5"
          component="h2"
        >
          Alert Configuration for "{ card.metric }"
        </Typography>
        <Typography>
          When sent an alert regarding "{ card.metric }", you will also be sent data for "{ card.relatedMetric }".
         </Typography>
      </CardContent>
    </Card>
  );
}
