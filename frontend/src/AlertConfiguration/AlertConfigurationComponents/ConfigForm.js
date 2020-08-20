import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import InputLabel from '@material-ui/core/InputLabel';
import { makeStyles } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import uuid from "uuid";
import Typography from '@material-ui/core/Typography';
import Tooltip from '@material-ui/core/Tooltip';

const useStyles = makeStyles({
  formControl: {
    backgroundColor: 'white',
    marginBottom: 25,
    minWidth: 250,
  },
});

/** 
 * ConfigForm is a form used to create a single user configuration.
 * It takes as input, addConfig (a function for adding a single configuration to a list of all user configurations).
 */
export default function ConfigForm({ addConfig }) {
  const classes = useStyles();

  const [config, setConfig] = useState({
    id: '',
    data: '',
    relatedData: '',
  });

  const DEFAULT_ID = 10;

  {/**TODO: Replace drop-down menu and hard-coded data options with text-input that recommends data options*/}
  const POSSIBLE_DIMENSIONS = ["noodle", "spice", "egg", "soup", "instant noodle"];
  const RELATED_POSSIBLE_DIMENSIONS = ["noodle", "spice", "egg", "soup", "instant noodle"];
  const dimensions = POSSIBLE_DIMENSIONS
  const relatedDimensions = RELATED_POSSIBLE_DIMENSIONS

  function handleDataChange(event) {
    setConfig({ ...config, data: event.target.value });
  }

  function handleRelatedDataChange(event) {
    setConfig({ ...config, relatedData: event.target.value });
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (config.data.trim() && config.relatedData.trim()) {
      addConfig({ ...config, id: DEFAULT_ID });
      setConfig({ ...config, data: "", relatedData: ""});
    }
  }

  { /* TODO: create IDs */ }
  return(
    <div>
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
            value={config.data}
            onChange={handleDataChange}
            displayEmpty
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { relatedDimensions.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>) }
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
            value={config.relatedData}
            onChange={handleRelatedDataChange}
            displayEmpty
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { relatedDimensions.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>) }
          </Select>
          <FormHelperText>Select a correlated metric or dimension.</FormHelperText>
        </FormControl>
        <br />
        <Button onClick={handleSubmit} variant="contained" color="primary">Submit</Button>
      </form>
    </div>
  );
}
