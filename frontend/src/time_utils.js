/**
 * Creates Date without a time from Timestamp model.
 * Note: timestampDate.date.month is from 1-12, while JavaScript 'Date' is from 0-11.
 */
export function convertTimestampToDate(timestampDate) {
  return new Date(
    timestampDate.date.year, timestampDate.date.month - 1, timestampDate.date.day, 0, 0, 0, 0)
    .toDateString().slice(4);
}

/** Format JavaScript date in the form MM-DD-YYYY if mFirst is true, otherwise YYYY-MM-DD. */
export function formatDate(date, mFirst) {
  var d = new Date(date),
      month = '' + (d.getMonth() + 1),
      day = '' + d.getDate(),
      year = d.getFullYear();

  if (month.length < 2) 
      month = '0' + month;
  if (day.length < 2) 
      day = '0' + day;

  return mFirst? [month, day, year].join('/') : [year, month, day].join('/');
}
