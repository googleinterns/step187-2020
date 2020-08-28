import React, { useState } from 'react';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import InputLabel from '@material-ui/core/InputLabel';
import { makeStyles } from '@material-ui/core/styles';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
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

  const DEFAULT_USER = "example@gmail.com";

  const [config, setConfig] = useState({
    user: DEFAULT_USER,
    dimension: '',
    metric: '',
    relatedDimension: '',
    relatedMetric: '',
  });

  /**TODO: Replace drop-down menu and hard-coded data options with text-input that recommends data options*/
  const POSSIBLE_DIMENSIONS = ["Ramen", "Pho", "Udon"];
  const POSSIBLE_METRICS = ["Interest Over Time - US", "Interest Over Time - UK", "Interest Over Time - JP"]; //, "Interest Over Time - Web Search", "Interest Over Time - Images", "Interest Over Time - YouTube"];
  const dimensions = POSSIBLE_DIMENSIONS;
  const relatedDimensions = POSSIBLE_DIMENSIONS;
  const metrics =  POSSIBLE_METRICS;
  const relatedMetrics =  POSSIBLE_METRICS;

  function handleDimensionChange(event) {
    setConfig({ ...config, dimension: event.target.value });
  }

  function handleMetricChange(event) {
    setConfig({ ...config, metric: event.target.value });
  }

  function handleRelatedDimensionChange(event) {
    setConfig({ ...config, relatedDimension: event.target.value });
  }

  function handleRelatedMetricChange(event) {
    setConfig({ ...config, relatedMetric: event.target.value });
  }
  
  function handleSubmit(event) {
    event.preventDefault();
    if (config.dimension.trim() && config.metric.trim() && config.relatedDimension.trim() && config.relatedMetric.trim()) {
      fetch("/api/v1/configurations", {
        method: 'POST',
        body: config.user + "%" + config.metric + "%" + config.dimension + "%" + config.relatedMetric + "%" + config.relatedDimension,
      });
      addConfig({ ...config });
      setConfig({ ...config, metric: "", dimension: "", relatedMetric: "", relatedDimension: ""});
    }
  }

  // TODO: Create IDs.
  return (
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
            Dimension
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config.dimension}
            onChange={handleDimensionChange}
            displayEmpty
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { dimensions.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>) }
          </Select>
          <FormHelperText>Select a metric or dimension.</FormHelperText>
        </FormControl>
        <br />
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="id">
            Metric
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config.metric}
            onChange={handleMetricChange}
            displayEmpty
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { metrics.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>) }
          </Select>
          <FormHelperText>Select a metric or dimension.</FormHelperText>
        </FormControl>
        <br />
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="id">
            Related Dimension
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config.relatedDimension}
            onChange={handleRelatedDimensionChange}
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
        <FormControl className={classes.formControl}>
          <InputLabel shrink id="id">
            Related Metric
          </InputLabel>
          <Select
            labelId="id"
            id="id"
            value={config.relatedMetric}
            onChange={handleRelatedMetricChange}
            displayEmpty
          >
            <MenuItem value="">
              <em>None</em>
            </MenuItem>
            { relatedMetrics.map((rd) => <MenuItem value={rd}>{rd}</MenuItem>) }
          </Select>
          <FormHelperText>Select a metric or dimension.</FormHelperText>
        </FormControl>
        <br />
        <Button onClick={handleSubmit} variant="contained" color="primary">Submit</Button>
      </form>
    </div>
  );
}
