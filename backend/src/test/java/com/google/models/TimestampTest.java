package com.google.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.time.format.DateTimeParseException;


/** Contain tests for methods in {@link Timestamp} class. */
@RunWith(JUnit4.class)
public final class TimestampTest {
  private static final int DAY_CONST = 1;
  private static final int MONTH_CONST = 1;
  private static final int YEAR_CONST = 2000;
  private static final Timestamp timestamp = new Timestamp(DAY_CONST, MONTH_CONST, YEAR_CONST);

  @Test
  public void constructor_convertStringToDate() throws DateTimeParseException {
    Timestamp stringTimestamp = new Timestamp("2020-12-5");
    Timestamp stringTimestampPaddedZero = new Timestamp("2020-12-05");
    Timestamp stringTimestampDiff = new Timestamp("2020-1-01");

    assertEquals(stringTimestamp.getDay(), 5);
    assertEquals(stringTimestamp.getMonth(), 12);
    assertEquals(stringTimestamp.getYear(), 2020);
    assertEquals(stringTimestampPaddedZero.getDay(), 5);
    assertEquals(stringTimestampPaddedZero.getMonth(), 12);
    assertEquals(stringTimestampPaddedZero.getYear(), 2020);
    assertEquals(stringTimestampDiff.getDay(), 1);
    assertEquals(stringTimestampDiff.getMonth(), 1);
    assertEquals(stringTimestampDiff.getYear(), 2020);
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_1() throws DateTimeParseException {
    Timestamp stringTimestamp = new Timestamp("202012-5");
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_2() throws DateTimeParseException {
    Timestamp stringTimestamp = new Timestamp("2020-13-5");
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_3() throws DateTimeParseException {
    Timestamp stringTimestamp = new Timestamp("2020-13-1-5");
  }

  @Test
  public void toString_correctConversion() {
    assertEquals(timestamp.toString(),
        YEAR_CONST + "-" + String.format("%02d", MONTH_CONST) + "-" + String.format("%02d", DAY_CONST));
  }

  @Test
  public void equals_workingComparator() throws DateTimeParseException {
    Timestamp sameTimestamp = new Timestamp(DAY_CONST, MONTH_CONST, YEAR_CONST);
    Timestamp diffTimestamp = new Timestamp(DAY_CONST + 1, MONTH_CONST, YEAR_CONST);

    assertEquals(timestamp, timestamp);
    assertEquals(sameTimestamp, timestamp);
    assertFalse(timestamp.equals(diffTimestamp));
    assertFalse(timestamp.equals(null));
  }

}
