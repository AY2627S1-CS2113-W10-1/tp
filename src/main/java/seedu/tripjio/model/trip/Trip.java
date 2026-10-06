package seedu.tripjio.model.trip;

import seedu.tripjio.exception.TripJioException;

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
     * The caller must check that the end date is not before the start date and show an error to the user if it is.
     *
     * @param start First day of the trip.
     * @param end Last day of the trip, on or after the start date.
     */
    public void set(LocalDate start, LocalDate end) {
        assert start != null && end != null : "Trip dates must not be null";
        assert !end.isBefore(start) : "End date must be validated to be on or after the start date";

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
        // TODO: C - use DateUtil.formatDate for both dates
        return new ArrayList<>();
    }

    /**
     * Loads one line of the trip section of the storage file, in the format produced by {@link #toStorageLines()}.
     * Lines are loaded one at a time, so an invalid line can be skipped with a warning.
     * Not implemented yet: it currently always throws an exception.
     *
     * @param line Line from the storage file.
     * @throws TripJioException Always, until loading the trip is implemented.
     */
    public void loadStorageLine(String line) throws TripJioException {
        // TODO: C - if the end date is before the start date after loading, treat the trip as not set
        throw new TripJioException("Trip data cannot be loaded yet.");
    }
}
