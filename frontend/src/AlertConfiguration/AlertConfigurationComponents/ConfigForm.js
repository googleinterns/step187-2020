import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import InputLabel from '@material-ui/core/InputLabel';
import { makeStyles } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import uuid from "uuid";
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';

{/**TO DO: Fix issue with setting state */}

const useStyles = makeStyles((theme) => ({
  formControl: {
    backgroundColor: theme.palette.background.paper,
    margin: theme.spacing(1),
    minWidth: 250,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
}));


export default function ConfigForm({ addConfig }) {
  const classes = useStyles();

  const [config, setConfig] = useState("");

  const dimensionsMetrics = ["noodle", "spice", "egg", "soup", "instant noodle"];
  const rDimensionsMetrics = ["noodle", "spice", "egg", "soup", "instant noodle"];

  const dataOptions = (
    dimensionsMetrics.map((d) => <MenuItem value={d}>{d}</MenuItem>)
  );

  const rDataOptions = (
    rDimensionsMetrics.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>)
  );

  function handleDataChange(event) {
    console.log(config);
    setConfig("random");
    console.log(config);
  };

  function handleRDataChange(event) {
    setConfig({ ...config, rData: event.target.value });
  };

  function handleSubmit(event) {
    event.preventDefault();
    addConfig({ ...config, id: 10 }); {/**TO DO: ADD ID and add if (config.data.trim() && config.rData.trim())*/}
    // reset form
    setConfig({ ...config, data: "", rData: ""});
  }

  { /* TODO: create IDs */ }
  return(
    <div>
      <CssBaseline />
      <Tooltip title="If an anomaly is detected regarding the data, the related data will automatically accompany the generated alert.">
        <Typography variant="h5" align="center">
          Data + Related Data
        </Typography>
      </Tooltip>
      <br />
      <form align="center">
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="id">
            Data
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config}
            onChange={e => {setConfig(e.target.value); console.log(e.target.value); console.log(config);}}
            displayEmpty
            className={classes.selectEmpty}
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { dataOptions }
          </Select>
          <FormHelperText>Select a metric or dimension.</FormHelperText>
        </FormControl>
        <br />
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="id">
            Related Data
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config.rData}
            onChange={handleRDataChange}
            displayEmpty
            className={classes.selectEmpty}
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { rDataOptions }
          </Select>
          <FormHelperText>Select a correlated metric or dimension.</FormHelperText>
        </FormControl>
        <br />
          <Button onClick={handleSubmit} variant="contained" color="primary">Submit</Button>
      </form>
    </div>
  );
}