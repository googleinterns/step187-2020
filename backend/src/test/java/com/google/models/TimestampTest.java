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
  private static final Timestamp TIMESTAMP = new Timestamp(DAY_CONST, MONTH_CONST, YEAR_CONST);

  @Test
  public void constructor_convertStringToDate() {
    Timestamp stringTimestamp = new Timestamp("2020-12-5");
    Timestamp stringTimestampPaddedZero = new Timestamp("2020-12-05");
    Timestamp stringTimestampDiff = new Timestamp("2020-1-01");

    assertEquals(5, stringTimestamp.getDay());
    assertEquals(12, stringTimestamp.getMonth());
    assertEquals(2020, stringTimestamp.getYear());
    assertEquals(5, stringTimestampPaddedZero.getDay());
    assertEquals(12, stringTimestampPaddedZero.getMonth());
    assertEquals(2020, stringTimestampPaddedZero.getYear());
    assertEquals(1, stringTimestampDiff.getDay());
    assertEquals(1, stringTimestampDiff.getMonth());
    assertEquals(2020, stringTimestampDiff.getYear());
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_missingDash() {
    Timestamp stringTimestamp = new Timestamp("202012-5");
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_monthTooLarge() {
    Timestamp stringTimestamp = new Timestamp("2020-13-5");
  }

  @Test(expected = DateTimeParseException.class)
  public void constructor_throwsExceptionForBadFormat_extraDash() {
    Timestamp stringTimestamp = new Timestamp("2020-13-1-5");
  }

  @Test
  public void toString_correctConversion() {
    assertEquals(
      YEAR_CONST + "-" + String.format("%02d", MONTH_CONST) + "-" + 
          String.format("%02d", DAY_CONST),
      TIMESTAMP.toString()
    );
  }

  @Test
  public void equals_workingComparator() {
    Timestamp sameTimestamp = new Timestamp(DAY_CONST, MONTH_CONST, YEAR_CONST);
    Timestamp diffTimestamp = new Timestamp(DAY_CONST + 1, MONTH_CONST, YEAR_CONST);

    assertEquals(TIMESTAMP, TIMESTAMP);
    assertEquals(TIMESTAMP, sameTimestamp);
    assertFalse(TIMESTAMP.equals(diffTimestamp));
    assertFalse(TIMESTAMP.equals(null));
  }

}