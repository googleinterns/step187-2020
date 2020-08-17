/*
 * Helper function to create Date from Timestamp model.
 */
export function convertTimestampToDate(timestampDate) {
  // timestampDate.date.month is from 1-12, while JavaScript 'Date' is from 0-11.
  return new Date(
    timestampDate.date.year, timestampDate.date.month - 1, timestampDate.date.day, 0, 0, 0, 0)
    .toDateString();
}
