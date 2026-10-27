package seedu.tripjio.util;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateUtilTest {
    @Test
    void parseDate_validDates_parsed() throws TripJioException {
        assertEquals(LocalDate.of(2026, 9, 1), DateUtil.parseDate("01-09-2026"));
        assertEquals(LocalDate.of(2026, 2, 28), DateUtil.parseDate("28-02-2026"));
        assertEquals(LocalDate.of(2026, 10, 16), DateUtil.parseDate(" 16-10-2026 "));
    }

    @Test
    void parseDate_invalidDates_throws() {
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("29-02-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("31-02-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("1-9-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("16/10/2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("32-01-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate(" "));
        assertThrows(TripJioException.class, () -> DateUtil.parseDate("01-13-2026"));
    }

    @Test
    void parseMonth_validMonths_parsed() throws TripJioException {
        assertEquals(YearMonth.of(2026, 10), DateUtil.parseMonth("10-2026"));
        assertEquals(YearMonth.of(2026, 1), DateUtil.parseMonth(" 01-2026 "));
    }

    @Test
    void parseMonth_invalidMonths_throws() {
        assertThrows(TripJioException.class, () -> DateUtil.parseMonth("13-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseMonth("00-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseMonth("1-2026"));
        assertThrows(TripJioException.class, () -> DateUtil.parseMonth("2026-10"));
    }

    @Test
    void formatDate_singleDigitDayAndMonth_paddedWithZero() {
        assertEquals("01-09-2026", DateUtil.formatDate(LocalDate.of(2026, 9, 1)));
        assertEquals("16-10-2026", DateUtil.formatDate(LocalDate.of(2026, 10, 16)));
    }

    @Test
    void formatMonthForStorage_singleDigitMonth_paddedWithZero() {
        assertEquals("01-2026", DateUtil.formatMonthForStorage(YearMonth.of(2026, 1)));
        assertEquals("10-2026", DateUtil.formatMonthForStorage(YearMonth.of(2026, 10)));
    }

    @Test
    void formatDate_thenParseDate_sameDate() throws TripJioException {
        LocalDate date = LocalDate.of(2026, 10, 16);
        String dateString = DateUtil.formatDate(date);
        assertEquals(date, DateUtil.parseDate(dateString));

        LocalDate singleDigitDate = LocalDate.of(2026, 9, 1);
        String singleDigitDateString = DateUtil.formatDate(singleDigitDate);
        assertEquals(singleDigitDate, DateUtil.parseDate(singleDigitDateString));

        LocalDate leapDate = LocalDate.of(2028, 2, 29);
        String leapDateString = DateUtil.formatDate(leapDate);
        assertEquals(leapDate, DateUtil.parseDate(leapDateString));
    }

    @Test
    void formatMonthForStorage_thenParseMonth_sameMonth() throws TripJioException {
        YearMonth month = YearMonth.of(2026, 10);
        String monthString = DateUtil.formatMonthForStorage(month);
        assertEquals(month, DateUtil.parseMonth(monthString));
    }

    @Test
    void formatMonthAndShortMonth_englishNames() {
        YearMonth october = YearMonth.of(2026, 10);
        assertEquals("October 2026", DateUtil.formatMonth(october));
        assertEquals("Oct 2026", DateUtil.formatShortMonth(october));

        YearMonth january = YearMonth.of(2026, 1);
        assertEquals("January 2026", DateUtil.formatMonth(january));
        assertEquals("Jan 2026", DateUtil.formatShortMonth(january));
    }
}
