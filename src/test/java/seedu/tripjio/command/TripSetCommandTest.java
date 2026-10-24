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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks trip set from command parsing through execution and storage. */
class TripSetCommandTest {
    @TempDir
    Path tempDir;

    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    @Test
    void validDates_setReplaceAndSave() throws TripJioException {
        Command command = Parser.parse("trip set s/10-12-2026 e/24-12-2026");
        command.execute(state, ui);
        assertTrue(command.isMutating());
        assertEquals(LocalDate.of(2026, 12, 10), state.getTrip().getStart());
        assertEquals(LocalDate.of(2026, 12, 24), state.getTrip().getEnd());
        assertTrue(output.toString().contains("Trip set: 10-12-2026 to 24-12-2026."));

        // Same-day trips, leap days, reversed prefix order and uppercase command words are supported.
        Parser.parse("TRIP set e/29-02-2028 s/29-02-2028").execute(state, ui);
        Storage storage = new Storage(tempDir.resolve("tripjio.txt").toString());
        storage.save(state);
        Trip loaded = storage.load(ui).getTrip();
        assertEquals(LocalDate.of(2028, 2, 29), loaded.getStart());
        assertEquals(loaded.getStart(), loaded.getEnd());
    }

    @Test
    void invalidInput_throwsWithoutChangingExistingTrip() throws TripJioException {
        LocalDate original = LocalDate.of(2026, 12, 1);
        state.getTrips().setCurrentDates(original, original);
        String[] inputs = {
            "trip", "trip set", "trip unknown", "trip set extra s/01-12-2026 e/02-12-2026",
            "trip set s/01-12-2026", "trip set e/02-12-2026", "trip set s/ e/02-12-2026",
            "trip set s/01-12-2026 e/", "trip set s/31-02-2026 e/02-03-2026",
            "trip set s/2026-12-01 e/02-12-2026", "trip set s/03-12-2026 e/02-12-2026",
            "trip set s/01-12-2026 s/02-12-2026 e/03-12-2026",
            "trip set s/01-12-2026 e/02-12-2026 e/03-12-2026",
            "trip set s/01-12-2026 e/02-12-2026 a/5",
            "trip set s/01-12-2026 e/02-12-2026 x/unknown",
            "trip set s/01-12-2026 e/02-12-2026 trailing text"
        };
        for (String input : inputs) {
            assertThrows(TripJioException.class, () -> Parser.parse(input).execute(state, ui), input);
            assertEquals(original, state.getTrip().getStart(), input);
            assertEquals(original, state.getTrip().getEnd(), input);
        }
        assertEquals("", output.toString());
    }

    @Test
    void model_rejectsNullAndReversedDates() {
        LocalDate date = LocalDate.of(2026, 12, 1);
        assertThrows(TripJioException.class, () -> state.getTrips().setCurrentDates(null, date));
        assertThrows(TripJioException.class, () -> state.getTrips().setCurrentDates(date, null));
        assertThrows(TripJioException.class, () -> state.getTrips().setCurrentDates(date, date.minusDays(1)));
        assertFalse(state.getTrips().hasCurrent());
        assertTrue(state.getTrips().displayLines().isEmpty());
    }

    @Test
    void storage_invalidOrIncompleteTrip_notSet() throws TripJioException {
        Trip trip = new Trip();
        assertThrows(TripJioException.class, () -> trip.loadStorageLine("unknown=01-12-2026"));
        assertThrows(TripJioException.class, () -> trip.loadStorageLine("start=31-02-2026"));
        trip.loadStorageLine("end=01-12-2026");
        assertFalse(trip.isSet());
        assertTrue(trip.toStorageLines().isEmpty());
        assertThrows(TripJioException.class, () -> trip.loadStorageLine("start=02-12-2026"));
        assertFalse(trip.isSet());
        assertEquals(null, trip.getStart());
        assertEquals(null, trip.getEnd());
    }

    @Test
    void parse_unknownTripSubcommand_throws() {
        assertThrows(TripJioException.class, () -> Parser.parse("trip unknown"));
    }
}
