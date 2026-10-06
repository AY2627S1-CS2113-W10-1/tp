package seedu.tripjio.util;

import seedu.tripjio.exception.TripJioException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Provides helper methods for parsing and formatting dates and months used in TripJio.
 * All parsing is strict, so non-existent dates such as 31-02-2026 are rejected.
 */
public class DateUtil {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter MONTH_DISPLAY = DateTimeFormatter.ofPattern("MMMM uuuu", Locale.ENGLISH);

    private static final DateTimeFormatter SHORT_MONTH_DISPLAY =
            DateTimeFormatter.ofPattern("MMM uuuu", Locale.ENGLISH);

    private DateUtil() {

    }

    /**
     * Parses a date string in the format DD-MM-YYYY into a {@code LocalDate}.
     *
     * @param text Date string entered by the user, e.g. "01-09-2026".
     * @return The parsed date.
     * @throws TripJioException If the text is not in DD-MM-YYYY format or is not a valid calendar date.
     */
    public static LocalDate parseDate(String text) throws TripJioException {
        try {
            return LocalDate.parse(text.trim(), DATE_FORMAT);

        } catch (DateTimeParseException e) {
            throw new TripJioException("Invalid date: " + text + ". Use DD-MM-YYYY, e.g. 01-09-2026.");
        }
    }

    /**
     * Parses a month string in the format MM-YYYY into a {@code YearMonth}.
     *
     * @param text Month string entered by the user, e.g. "10-2026".
     * @return The parsed month.
     * @throws TripJioException If the text is not in MM-YYYY format or the month is invalid.
     */
    public static YearMonth parseMonth(String text) throws TripJioException {
        try {
            return YearMonth.parse(text.trim(), MONTH_FORMAT);
        } catch (DateTimeParseException e) {
            throw new TripJioException("Invalid month: " + text + ". Use MM-YYYY, e.g. 10-2026.");
        }
    }

    /**
     * Returns the given date as a string in the format DD-MM-YYYY.
     *
     * @param date Date to format.
     * @return Formatted date string, e.g. "01-09-2026".
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    /**
     * Returns the given month as a human-readable string with the full month name.
     *
     * @param month Month to format.
     * @return Formatted month string, e.g. "October 2026".
     */
    public static String formatMonth(YearMonth month) {
        return month.format(MONTH_DISPLAY);
    }

    /**
     * Returns the given month as a short, human-readable string with an abbreviated month name.
     *
     * @param month Month to format.
     * @return Formatted month string, e.g. "Oct 2026".
     */
    public static String formatShortMonth(YearMonth month) {
        return month.format(SHORT_MONTH_DISPLAY);
    }

}
