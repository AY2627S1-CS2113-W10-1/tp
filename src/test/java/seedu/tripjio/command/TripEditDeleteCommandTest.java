package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.storage.Storage;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Covers trip editing/deletion, atomic validation, selection shifts and restart behaviour. */
class TripEditDeleteCommandTest {
    @TempDir
    Path directory;
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    private void run(String command) throws TripJioException {
        Parser.parse(command).execute(state, ui);
    }

    private void seed() throws TripJioException {
        run("trip add n/Japan s/01-01-2020 e/10-01-2020");
        run("trip add n/Korea s/01-02-2020 e/10-02-2020");
        run("trip add n/France s/01-03-2020 e/10-03-2020");
        output.reset();
    }

    @Test
    void edit_selectedTripRetainsIdentityAndUnspecifiedFields() throws Exception {
        seed();
        Trip selected = state.getTrip();
        run("TRIP EDIT 3 n/FRANCE");
        assertSame(selected, state.getTrip());
        assertEquals("FRANCE", state.getTrips().getCurrentName());
        assertEquals(LocalDate.of(2020, 3, 1), selected.getStart());
        run("trip edit 3 e/01-03-2020");
        assertEquals(1, selected.getDurationDays());
        run("trip edit 3 e/02-04-2020 n/Paris s/01-04-2020");
        assertEquals("Paris", state.getTrips().getCurrentName());
        assertEquals(LocalDate.of(2020, 4, 1), selected.getStart());
        assertTrue(output.toString().contains("Updated trip 3."));
    }

    @Test
    void edit_nonSelectedTripOrNoSelection_doesNotSelectIt() throws Exception {
        seed();
        Trip selected = state.getTrip();
        run("trip edit 1 n/Tokyo");
        assertSame(selected, state.getTrip());
        run("trip delete 3");
        run("trip edit 1 s/02-01-2020");
        assertFalse(state.getTrips().hasCurrent());
        assertNull(state.getTrip());
    }

    @Test
    void edit_invalidCombinedDatesOrDuplicateName_isAtomic() throws Exception {
        seed();
        List<String> before = state.getTrips().toStorageLines();
        for (String command : new String[] {"trip edit 1 n/korea", "trip edit 1 n/Tokyo s/11-01-2020",
            "trip edit 1 n/Tokyo e/31-12-2019", "trip edit 4 n/Unknown", "trip delete 4"}) {
            assertThrows(TripJioException.class, () -> run(command), command);
            assertEquals(before, state.getTrips().toStorageLines());
            assertEquals("", output.toString());
        }
        assertThrows(TripJioException.class, () -> state.getTrips().edit(1, null, null, null));
        assertThrows(TripJioException.class, () -> state.getTrips().edit(1, "bad|name", null, null));
        assertThrows(TripJioException.class, () -> state.getTrips().edit(1, "bad\nname", null, null));
        assertEquals(before, state.getTrips().toStorageLines());
    }

    @Test
    void parser_rejectsMissingRepeatedUnknownAndMalformedArguments() {
        for (String command : new String[] {"trip edit", "trip delete", "trip edit 1", "trip edit 0 n/A",
            "trip delete -1", "trip delete 1.5", "trip delete 99999999999999", "trip edit 1 extra n/A",
            "trip delete 1 extra", "trip delete 1 n/A", "trip delete 1 x/A",
            "trip edit 1 n/", "trip edit 1 s/", "trip edit 1 e/", "trip edit 1 n/A n/B",
            "trip edit 1 s/01-01-2020 s/02-01-2020", "trip edit 1 e/01-01-2020 e/02-01-2020",
            "trip edit 1 s/31-02-2020", "trip edit 1 s/2020-01-01", "trip edit 1 a/10",
            "trip edit 1 x/A", "trip edit 1 n/A x/B"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(command), command);
        }
    }

    @Test
    void delete_beforeOrAfterSelected_keepsSameSelectedTrip() throws Exception {
        seed();
        run("trip select 2");
        Trip selected = state.getTrip();
        run("trip delete 3");
        assertSame(selected, state.getTrip());
        run("trip delete 1");
        assertSame(selected, state.getTrip());
        assertTrue(state.getTrips().displayLines().get(0).startsWith("1. Korea"));
        assertTrue(state.getTrips().displayLines().get(0).contains("[selected]"));
    }

    @Test
    void delete_selectedClearsSelectionAndKeepsExpenses() throws Exception {
        seed();
        run("add d/Lunch a/5 dt/01-01-2020");
        output.reset();
        run("trip delete 3");
        assertFalse(state.getTrips().hasCurrent());
        assertEquals("Deleted trip: France. Expenses are unchanged.\n"
                + "No trip selected. Use trip list and trip select INDEX, or add a new trip.\n",
                output.toString().replace("\r\n", "\n"));
        run("trip delete 1");
        assertFalse(state.getTrips().hasCurrent());
        assertEquals(1, state.getExpenses().size());
        assertEquals(5, state.getExpenses().get(1).getAmount());
        assertThrows(TripJioException.class, () -> run("trip status"));
        assertThrows(TripJioException.class, () -> run("trip set s/01-01-2020 e/02-01-2020"));
    }

    @Test
    void editsAndClearedSelection_surviveStorageRoundTrip() throws Exception {
        seed();
        run("trip edit 3 n/Paris s/02-03-2020");
        Storage storage = new Storage(directory.resolve("trips.txt").toString());
        storage.save(state);
        AppState loaded = storage.load(ui);
        assertEquals("Paris", loaded.getTrips().getCurrentName());
        assertEquals(LocalDate.of(2020, 3, 2), loaded.getTrip().getStart());
        run("trip delete 3");
        storage.save(state);
        loaded = storage.load(ui);
        assertFalse(loaded.getTrips().hasCurrent());
        assertEquals(2, loaded.getTrips().displayLines().size());
    }

    @Test
    void deletingAllTrips_staysEmptyAfterRestartAndSetCanCreateOriginal() throws Exception {
        seed();
        run("trip delete 3");
        run("trip delete 2");
        run("trip delete 1");
        Storage storage = new Storage(directory.resolve("empty.txt").toString());
        storage.save(state);
        AppState loaded = storage.load(ui);
        assertTrue(loaded.getTrips().displayLines().isEmpty());
        assertNull(loaded.getTrip());
        assertThrows(TripJioException.class, () -> run("trip delete 1"));
        assertThrows(TripJioException.class, () -> run("trip edit 1 n/Test"));
        Parser.parse("trip set s/29-02-2028 e/29-02-2028").execute(loaded, ui);
        assertEquals("Original trip", loaded.getTrips().getCurrentName());
    }

    @Test
    void commands_areMutatingWithoutExpenseBudgetWarnings() throws Exception {
        for (String text : new String[] {"trip edit 1 n/A", "trip delete 1"}) {
            Command command = Parser.parse(text);
            assertTrue(command.isMutating());
            assertFalse(command.isExit());
            assertNull(command.getAffectedMonth());
        }
    }
}
