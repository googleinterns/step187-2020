import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';

const useStyles = makeStyles((theme) => ({
  padding: {
    marginTop: 50,
  },
  card: {
    height: '100%',
    display: 'flex',
    flexDirection: 'column',
    paddingTop: theme.spacing(2),
    paddingBottom: theme.spacing(2),
    marginTop: 50
  },
  cardContent: {
    flexGrow: 1,
  },
}));

export default function ConfigurCard(card) {
  const classes = useStyles();

  return (
    <Container className={classes.padding} maxWidth="md">
      <Card className={classes.card}>
        <CardContent className={classes.cardContent}>
          <Typography gutterBottom variant="h5" component="h2">
            Alert Configuration for "{ card.data }"
          </Typography>
          <Typography>
            When sent an alert regarding "{ card.data }", I also want to see data for "{ card.related_data }".
          </Typography>
        </CardContent>
      </Card>
    </Container>
  );
}