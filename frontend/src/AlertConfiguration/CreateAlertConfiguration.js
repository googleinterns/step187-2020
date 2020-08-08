import React from 'react';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import AlertConfigurationControlPanel from './AlertConfigurationControlPanel';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%',
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(8),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 250,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
  root: {
    minWidth: 400,
    margin: 50,
  },
}));

export default function CreateAlertConfiguration() {
  const classes = useStyles();

  var [clicked, setClick] = React.useState(false);
  const [age, setAge] = React.useState('');

  function handleClick() {
    setClick(true);
  }

  const handleChange = (event) => {
    setAge(event.target.value);
  };

  if (!clicked) {
    return (
      <Container component="main" maxWidth="xs">
        <CssBaseline />
        <Card className={classes.root}>
        <CardContent>
        <div className={classes.paper}>
          <Typography component="h1" variant="h5">
            Create A New Alert Configuration
          </Typography>
          <form className={classes.form} noValidate>
            <Typography component="p1" variant="p2" align="center" color="textPrimary" gutterBottom>
              Select a metric and/or dimension.
            </Typography>
            <br />
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel id="demo-simple-select-outlined-label">Primary</InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  value={age}
                  onChange={handleChange}
                  label="Primary"
                >
                <MenuItem value="">
                  <em>None</em>
                </MenuItem>
                <MenuItem value={10}>Ten</MenuItem>
                <MenuItem value={20}>Twenty</MenuItem>
                <MenuItem value={30}>Thirty</MenuItem>
              </Select>
            </FormControl>
              <br />
            <Typography component="p1" variant="p2" align="center" color="textPrimary" gutterBottom>
              Select related data for that metric and/or dimension.
            </Typography>
            <br />
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel id="demo-simple-select-outlined-label">Secondary</InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  value={age}
                  onChange={handleChange}
                  label="Secondary"
                >
                <MenuItem value="">
                  <em>None</em>
                </MenuItem>
                <MenuItem value={10}>Ten</MenuItem>
                <MenuItem value={20}>Twenty</MenuItem>
                <MenuItem value={30}>Thirty</MenuItem>
              </Select>
            </FormControl>
            <Grid>
              <Grid item>
                <Button type="submit" fullWidth variant="contained" color="primary" className={classes.submit}>
                  Create
                </Button>
              </Grid>
              <Grid item>
                <Button onClick={() => handleClick()} variant="outlined" color="primary" fullWidth>
                  Cancel
                </Button>
              </Grid>
            </Grid>
          </form>
        </div>
        {clicked = false}
        </CardContent>
        </Card>
      </Container>
    );
  } else {
    return(
      <main>
        <AlertConfigurationControlPanel/>
        {clicked = false}
      </main>
    );
  }
} 
