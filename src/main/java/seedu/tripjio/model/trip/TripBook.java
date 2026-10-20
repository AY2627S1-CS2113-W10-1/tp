package seedu.tripjio.model.trip;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Owns named trips and an active selection while preserving legacy single-trip data. */
public class TripBook {
    private final Trip legacy;
    private final List<String> names = new ArrayList<>();
    private final List<Trip> trips = new ArrayList<>();
    private int selected;

    public TripBook(Trip legacy) {
        this.legacy = legacy;
    }

    public Trip getCurrent() {
        return trips.isEmpty() ? legacy : trips.get(selected);
    }

    /** Adds and selects a validated trip, migrating an existing unnamed trip on first use. */
    public void add(String name, LocalDate start, LocalDate end) throws TripJioException {
        validateName(name);
        Trip trip = new Trip();
        trip.set(start, end);
        if (trips.isEmpty() && legacy.isSet()) {
            if (name.equalsIgnoreCase("Original trip")) {
                throw new TripJioException("Original trip is reserved for your existing trip. Choose another name.");
            }
            names.add("Original trip");
            trips.add(legacy);
        }
        names.add(name.trim());
        trips.add(trip);
        selected = trips.size() - 1;
    }

    /** Rejects ambiguous names and storage delimiters before changing the collection. */
    private void validateName(String name) throws TripJioException {
        if (name == null || name.isBlank() || name.contains("|") || name.contains("/")
                || name.contains("\n") || name.contains("\r")) {
            throw new TripJioException("Trip name must not be empty or contain '/', '|', or line breaks.");
        }
        for (String existing : names) {
            if (existing.equalsIgnoreCase(name.trim())) {
                throw new TripJioException("A trip with that name already exists.");
            }
        }
    }

    /** Selects an existing displayed trip index without changing any expense. */
    public void select(int index) throws TripJioException {
        int size = trips.isEmpty() && legacy.isSet() ? 1 : trips.size();
        if (index < 1 || index > size) {
            throw new TripJioException("Invalid trip index. Use trip list to see available trips.");
        }
        selected = index - 1;
    }

    /** Provides names, dates and an active marker in stable creation order. */
    public List<String> displayLines() {
        List<String> lines = new ArrayList<>();
        if (trips.isEmpty()) {
            if (legacy.isSet()) {
                lines.add(formatLine(1, "Original trip", legacy, true));
            }
            return lines;
        }
        for (int i = 0; i < trips.size(); i++) {
            lines.add(formatLine(i + 1, names.get(i), trips.get(i), i == selected));
        }
        return lines;
    }

    private String formatLine(int index, String name, Trip trip, boolean active) {
        return index + ". " + name + " (" + DateUtil.formatDate(trip.getStart()) + " to "
                + DateUtil.formatDate(trip.getEnd()) + ")" + (active ? " [selected]" : "");
    }

    /** Serialises named trips followed by selection by name, avoiding index drift on invalid records. */
    public List<String> toStorageLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            lines.add("T|" + names.get(i) + "|" + DateUtil.formatDate(trip.getStart())
                    + "|" + DateUtil.formatDate(trip.getEnd()));
        }
        if (!trips.isEmpty()) {
            lines.add("A|" + names.get(selected));
        }
        return lines;
    }

    /** Restores a named trip or selection from the ordered format written by this class. */
    public void loadStorageLine(String line) throws TripJioException {
        String[] fields = line.split("\\|", -1);
        if (fields.length == 4 && fields[0].equals("T")) {
            validateName(fields[1]);
            Trip trip = new Trip();
            trip.set(DateUtil.parseDate(fields[2]), DateUtil.parseDate(fields[3]));
            names.add(fields[1].trim());
            trips.add(trip);
        } else if (fields.length == 2 && fields[0].equals("A") && names.contains(fields[1])) {
            selected = names.indexOf(fields[1]);
        } else {
            throw new TripJioException("Invalid named trip or missing selected trip.");
        }
    }
}
