package seedu.tripjio.model.trip;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

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
        validateName(name, -1);
    }

    /** Allows a renamed trip to retain its own name, but not another trip's name. */
    private void validateName(String name, int ignoredIndex) throws TripJioException {
        if (name == null || name.isBlank() || name.contains("|") || name.contains("/")
                || name.contains("\n") || name.contains("\r")) {
            throw new TripJioException("Trip name must not be empty or contain '/', '|', or line breaks.");
        }
        for (int i = 0; i < names.size(); i++) {
            if (i != ignoredIndex && names.get(i).equalsIgnoreCase(name.trim())) {
                throw new TripJioException("A trip with that name already exists.");
            }
        }
    }

    /** Selects an existing displayed trip index without changing any expense. */
    public void select(int index) throws TripJioException {
        checkIndex(index);
        selected = index - 1;
    }

    /** Validates the one-based index shown by trip list. */
    private void checkIndex(int index) throws TripJioException {
        if (index < 1 || index > trips.size()) {
            throw new TripJioException("Invalid trip index. Use trip list to see available trips.");
        }
    }

    /**
     * Edits only supplied fields, preserving selection and all existing data on validation failure.
     * Null fields mean unchanged. At least one field is required.
     */
    public void edit(int index, String name, LocalDate start, LocalDate end) throws TripJioException {
        checkIndex(index);
        if (name == null && start == null && end == null) {
            throw new TripJioException("Give at least one trip field to edit: n/, s/ or e/.");
        }
        int position = index - 1;
        Trip trip = trips.get(position);
        String newName = name == null ? names.get(position) : name.trim();
        validateName(name == null ? newName : name, position);
        // Trip.set validates the combined dates before changing either; rename only after success.
        trip.set(start == null ? trip.getStart() : start, end == null ? trip.getEnd() : end);
        names.set(position, newName);
    }

    /** Deletes a trip only, returning its name; expenses are never removed. */
    public String delete(int index) throws TripJioException {
        checkIndex(index);
        int position = index - 1;
        String name = names.remove(position);
        trips.remove(position);
        if (selected == position) {
            selected = -1;
        } else if (selected > position) {
            selected--;
        }
        return name;
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
            String line = "T|" + names.get(i) + "|" + DateUtil.formatDate(trip.getStart())
                    + "|" + DateUtil.formatDate(trip.getEnd());
            if (trip.hasBudget()) {
                line += "|" + MoneyUtil.toStorageString(trip.getBudget());
            }
            lines.add(line);
        }
        if (hasCurrent()) {
            lines.add("A|" + names.get(selected));
        }
        return lines;
    }

    /** Restores a named trip or selection from the ordered format written by this class. */
    public void loadStorageLine(String line) throws TripJioException {
        String[] fields = line.split("\\|", -1);
        if ((fields.length == 4 || fields.length == 5) && fields[0].equals("T")) {
            validateName(fields[1]);
            Trip trip = new Trip();
            trip.set(DateUtil.parseDate(fields[2]), DateUtil.parseDate(fields[3]));
            if (fields.length == 5) {
                try {
                    double budget = Double.parseDouble(fields[4]);
                    trip.setBudget(budget);
                } catch (NumberFormatException e) {
                    throw new TripJioException("Invalid budget in trip line: " + line);
                }
            }
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
