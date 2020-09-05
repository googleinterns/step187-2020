import React, { Fragment, useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Box from '@material-ui/core/Box';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import MenuItem from '@material-ui/core/MenuItem';
import Select from '@material-ui/core/Select';
import Tooltip from '@material-ui/core/Tooltip';
import Typography from '@material-ui/core/Typography';
import ArrowDownwardIcon from '@material-ui/icons/ArrowDownward';
import ArrowUpwardIcon from '@material-ui/icons/ArrowUpward';
import { priorityLevels } from './management_constants';
import { formatDate } from '../time_utils';

/**
 * Displays alerts in a list given information passed down as props from AlertsContent.
 * Clicking on the text for an alert will route the user to a page containing 
 * more information and data about that alert (AlertInfo component).
 * Props include:
 * displayAlerts = an Array of alerts to display
 * allAlerts = the same Map between alert ID and Object with alert info
 * checked = the same Array of ids of alerts that are resolved
 * handleToggle = callback function to handle clicking on the checkbox
 */
export default function AlertsList(props) {
  const allAlerts = props.allAlerts;
  const originalDisplay = props.displayAlerts;

  const [displayAlerts, setDisplayAlerts] = useState(props.displayAlerts);
  const [priority, setPriority] = useState(priorityLevels.P0);
  const [sortDirectionPriority, setSortDirectionPriority] = useState(false);
  const [sortDirectionDate, setSortDirectionDate] = useState(false);
  
  useEffect(() => {
    setDisplayAlerts(props.displayAlerts);
  }, [props.displayAlerts]);

  const handlePriorityChange = (newPriority, alertId, allAlerts) => {
    const numToEnum = Object.keys(priorityLevels)[Object.values(priorityLevels).indexOf(newPriority)];
    fetch('/api/v1/alert-visualization?id=' + alertId + '&priority=' + numToEnum, {
      method: 'POST'
    });

    allAlerts.get(alertId).priority = numToEnum;
    // Set state in order to re-render the component, although the state is not used.
    setPriority(newPriority ? newPriority : priority); 
  }

  const sortPriority = (displayAlerts) => {
    let sorted = displayAlerts.slice();
    sorted.sort((a, b) => (allAlerts.get(a).priority > allAlerts.get(b).priority) ? 1 : -1);
    sorted = sortDirectionPriority ? sorted.reverse() : sorted;
    setSortDirectionPriority(!sortDirectionPriority);
    return sorted;
  }

  const sortDate = (displayAlerts) => {
    let sorted = displayAlerts.slice();
    sorted.sort((a, b) => (
        formatDate(allAlerts.get(a).timestampDate, false) > 
        formatDate(allAlerts.get(b).timestampDate, false)
      ) ? 1 : -1);
    sorted = sortDirectionDate ? sorted.reverse() : sorted;
    setSortDirectionDate(!sortDirectionDate);
    return sorted;
  }
  
  return (
    <Fragment>
      <Button color="secondary" onClick={() => setDisplayAlerts(sortDate(displayAlerts))} 
        style={{ margin: '10px' }}
      >
        Sort by Date
        { sortDirectionDate ? <ArrowUpwardIcon /> : <ArrowDownwardIcon /> }
      </Button>
      <Button color="secondary" onClick={() => setDisplayAlerts(sortPriority(displayAlerts))} 
        style={{ margin: '10px' }}
      >
        Sort by Priority
        { sortDirectionPriority ? <ArrowDownwardIcon /> : <ArrowUpwardIcon /> }
      </Button>
      <List className="alerts-list">
        {(originalDisplay.length !== displayAlerts.length ? originalDisplay : displayAlerts)
          .map((alertId, value) => {
          const labelId = `checkbox-list-label-${alertId}`;

          return (
            <ListItem key={value} role={undefined} dense divider>
              <ListItemIcon>
                <Tooltip title="Resolved?" placement="left">
                  <Checkbox
                    edge="start"
                    checked={props.checked.indexOf(alertId) !== -1}
                    tabIndex={-1}
                    onClick={() => props.handleToggle(alertId)}
                    inputProps={{ 'aria-labelledby': labelId }}
                  />
                </Tooltip>
              </ListItemIcon>
              <Link to={`/alerts/${alertId}`} style={{ color: "inherit", textDecoration: "none"}} >
                <ListItemText id={labelId} 
                  disableTypography
                  primary={<Typography variant="body1" >Alert on 
                    <Box fontWeight='fontWeightBold' display='inline' m={1} style={{ color: '#0FA3B1'}}>
                      {` ${allAlerts.get(alertId).timestampDate} (
                        ${formatDate(allAlerts.get(alertId).timestampDate, /** mFirst = */ true)} )`}
                    </Box> has
                    <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                      {` ${allAlerts.get(alertId).anomalies.length} anomalies`}
                    </Box>
                    </Typography>
                  }
                />
              </Link>
              <ListItemSecondaryAction>
              <form>
                <Select
                  labelId="priority-select"
                  id="priority-select"
                  value={priorityLevels[allAlerts.get(alertId).priority]}
                  onChange={event => handlePriorityChange(event.target.value, alertId, allAlerts)}
                >
                  <MenuItem value={priorityLevels.P0}>P0</MenuItem>
                  <MenuItem value={priorityLevels.P1}>P1</MenuItem>
                  <MenuItem value={priorityLevels.P2}>P2</MenuItem>
                </Select>
              </form>
              </ListItemSecondaryAction>
            </ListItem>
          );
        })}
      </List>
    </Fragment>
  );
}
