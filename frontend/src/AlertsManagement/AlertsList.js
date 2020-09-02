import React from 'react';
import { Link } from 'react-router-dom';
import Box from '@material-ui/core/Box';
import Checkbox from '@material-ui/core/Checkbox';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Tooltip from '@material-ui/core/Tooltip';
import Typography from '@material-ui/core/Typography';
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
  const displayAlerts = props.displayAlerts;
  const allAlerts = props.allAlerts;
  
  return (
    <List className="alerts-list">
      {displayAlerts.map((alertId, value) => {
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
                      ${formatDate(allAlerts.get(alertId).timestampDate, /** mFirst = */ true)})`}
                  </Box> has
                  <Box display='inline' m={1} style={{ color: '#0FA3B1'}}>
                    {` ${allAlerts.get(alertId).anomalies.length} anomalies`}
                  </Box>
                  </Typography>
                }
              />
            </Link>
          </ListItem>
        );
      })}
    </List>
  );
}

