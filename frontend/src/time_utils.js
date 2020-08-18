/*
 * Creates Date without a time from Timestamp model.
 * Note: timestampDate.date.month is from 1-12, while JavaScript 'Date' is from 0-11.
 */
export function convertTimestampToDate(timestampDate) {
  return new Date(
    timestampDate.date.year, timestampDate.date.month - 1, timestampDate.date.day, 0, 0, 0, 0)
    .toDateString();
}
