package seedu.tripjio.model.trip;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the start and end dates of the user's current trip.
 * TripJio tracks one trip at a time, so setting new dates replaces the old trip.
 */
public class Trip {
    /** Key used in the storage file for the start date, e.g. "start=10-12-2026". */
    private static final String START_KEY = "start";

    /** Key used in the storage file for the end date, e.g. "end=24-12-2026". */
    private static final String END_KEY = "end";

    /** Separates the key and the date in one storage line. */
    private static final String SEPARATOR = "=";

    /** First day of the trip, or null if no trip has been set. */
    private LocalDate start;

    /** Last day of the trip, or null if no trip has been set. */
    private LocalDate end;

    /**
     * Sets the dates of the trip, replacing any trip already set.
     * Validates both dates before changing either, preserving the existing trip on failure.
     *
     * @param start First day of the trip.
     * @param end Last day of the trip, on or after the start date.
     * @throws TripJioException If either date is missing or the end is before the start.
     */
    public void set(LocalDate start, LocalDate end) throws TripJioException {
        if (start == null || end == null) {
            throw new TripJioException("Both trip start and end dates are required.");
        }
        if (end.isBefore(start)) {
            throw new TripJioException("Trip end date must be on or after the start date.");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Returns whether a trip has been set.
     *
     * @return True if both the start and end dates are set.
     */
    public boolean isSet() {
        return start != null && end != null;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    /**
     * Returns the trip as lines for the storage file, e.g. "start=10-12-2026" and "end=24-12-2026".
     *
     * @return Storage lines representing the trip, or an empty list if no trip has been set.
     */
    public List<String> toStorageLines() {
        if (!isSet()) {
            return new ArrayList<>();
        }
        return List.of(START_KEY + SEPARATOR + DateUtil.formatDate(start),
                END_KEY + SEPARATOR + DateUtil.formatDate(end));
    }

    /**
     * Loads one line of the trip section of the storage file, in the format produced by {@link #toStorageLines()}.
     * Lines are loaded one at a time, so an invalid line can be skipped with a warning.
     *
     * @param line Line from the storage file.
     * @throws TripJioException If the key, date or completed date range is invalid.
     */
    public void loadStorageLine(String line) throws TripJioException {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 2 || !(parts[0].equals(START_KEY) || parts[0].equals(END_KEY))) {
            throw new TripJioException("Invalid trip line: " + line);
        }
        LocalDate date = DateUtil.parseDate(parts[1]);
        if (parts[0].equals(START_KEY)) {
            start = date;
        } else {
            end = date;
        }
        if (isSet() && end.isBefore(start)) {
            start = null;
            end = null;
            throw new TripJioException("Trip end date is before the start date. Trip has been cleared.");
        }
    }
}
