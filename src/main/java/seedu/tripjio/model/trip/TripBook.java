package seedu.tripjio.model.trip;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Owns named trips and an optional active selection; no fallback trip is retained. */
public class TripBook {
    private final List<String> names = new ArrayList<>();
    private final List<Trip> trips = new ArrayList<>();
    private int selected = -1;

    public TripBook() {
    }

    /** Returns the selected trip, or null when there is no active selection. */
    public Trip getCurrent() {
        return hasCurrent() ? trips.get(selected) : null;
    }

    public boolean hasCurrent() {
        return selected >= 0 && selected < trips.size();
    }

    /** Returns the selected name, or null when no trip is selected. */
    public String getCurrentName() {
        return hasCurrent() ? names.get(selected) : null;
    }

    /** Imports a complete v1.0 trip once; incomplete legacy dates do not create a trip. */
    public void importOriginal(Trip original) {
        if (trips.isEmpty() && original != null && original.isSet()) {
            names.add("Original trip");
            trips.add(original);
            selected = 0;
        }
    }

    /** Creates Original trip for an empty book, otherwise updates only an explicitly selected trip. */
    public void setCurrentDates(LocalDate start, LocalDate end) throws TripJioException {
        if (trips.isEmpty()) {
            add("Original trip", start, end);
        } else if (!hasCurrent()) {
            throw new TripJioException("No trip selected. Use trip list, then trip select INDEX.");
        } else {
            getCurrent().set(start, end);
        }
    }

    /** Adds and selects a validated named trip without changing any existing trip. */
    public void add(String name, LocalDate start, LocalDate end) throws TripJioException {
        validateName(name);
        Trip trip = new Trip();
        trip.set(start, end);
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
        int size = trips.size();
        if (index < 1 || index > size) {
            throw new TripJioException("Invalid trip index. Use trip list to see available trips.");
        }
        selected = index - 1;
    }

    /** Provides names, dates and an active marker in stable creation order. */
    public List<String> displayLines() {
        List<String> lines = new ArrayList<>();
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
        if (hasCurrent()) {
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
        } else if (fields.length == 2 && fields[0].equals("A")) {
            if (!names.contains(fields[1])) {
                selected = -1;
                throw new TripJioException("Selected trip not found. Use trip list, then trip select INDEX.");
            }
            selected = names.indexOf(fields[1]);
        } else {
            throw new TripJioException("Invalid named trip or missing selected trip.");
        }
    }
}
