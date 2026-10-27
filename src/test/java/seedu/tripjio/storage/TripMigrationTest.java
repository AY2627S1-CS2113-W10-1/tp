package seedu.tripjio.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.command.TripStatusCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.model.trip.TripBook;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression coverage for trip migration, section precedence and explicit no-selection persistence. */
class TripMigrationTest {
    private static final String LEGACY = "[trip]\nstart=01-01-2020\nend=02-01-2020\n";
    private static final String NAMED = "[trips]\nT|Japan|01-02-2020|02-02-2020\n"
            + "T|Korea|01-03-2020|02-03-2020\nA|Korea\n";
    @TempDir
    Path directory;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    private AppState load(String content) throws Exception {
        Path file = directory.resolve("tripjio.txt");
        Files.writeString(file, content);
        return new Storage(file.toString()).load(ui);
    }

    private AppState roundTrip(AppState state) throws Exception {
        Storage storage = new Storage(directory.resolve("saved.txt").toString());
        storage.save(state);
        List<String> lines = Files.readAllLines(directory.resolve("saved.txt"));
        assertTrue(lines.contains("[trips]"));
        assertFalse(lines.contains("[trip]"));
        return storage.load(ui);
    }

    @Test
    void v1File_migratesCompleteTripAndKeepsExpensesBudgetsAndRates() throws Exception {
        AppState state = load("[expense]\nE|Lunch|5.00|FOOD|01-01-2020\n[budget]\ndefault=100.00\n"
                + LEGACY + "[rates]\nEUR=1.45\n");
        assertEquals("Original trip", state.getTrips().getCurrentName());
        assertEquals(LocalDate.of(2020, 1, 1), state.getTrip().getStart());
        AppState restored = roundTrip(state);
        assertEquals(state.getTrips().displayLines(), restored.getTrips().displayLines());
        assertEquals(1, restored.getExpenses().size());
        assertEquals(100.0, restored.getBudget().getBudgetFor(java.time.YearMonth.of(2020, 1)));
        assertEquals("1.45", restored.getExchangeRates().get("EUR").toPlainString());
    }

    @Test
    void pr11File_namedSectionWinsInEitherOrderWithoutDuplicateTrip() throws Exception {
        for (String contents : new String[] {LEGACY + NAMED, NAMED + LEGACY}) {
            AppState state = load(contents);
            assertEquals(2, state.getTrips().displayLines().size());
            assertEquals("Korea", state.getTrips().getCurrentName());
            assertEquals(state.getTrips().displayLines(), roundTrip(state).getTrips().displayLines());
        }
    }

    @Test
    void emptyOrInvalidNamedSection_neverResurrectsLegacyTrip() throws Exception {
        for (String named : new String[] {"[trips]\n", "[trips]\nT|Bad|02-01-2020|01-01-2020\n",
            "[trips]\nA|Missing\n"}) {
            AppState state = load(LEGACY + named);
            assertTrue(state.getTrips().displayLines().isEmpty());
            assertFalse(state.getTrips().hasCurrent());
            assertNull(state.getTrip());
            assertFalse(roundTrip(state).getTrips().hasCurrent());
        }
    }

    @Test
    void newFile_withoutSelection_keepsTripsUnselectedAcrossRestarts() throws Exception {
        AppState state = load("[trips]\nT|Japan|01-02-2020|02-02-2020\n");
        assertFalse(state.getTrips().hasCurrent());
        assertNull(state.getTrips().getCurrentName());
        output.reset();
        assertThrows(TripJioException.class, () -> new TripStatusCommand().execute(state, ui));
        assertThrows(TripJioException.class,
                () -> Parser.parse("trip set s/01-04-2020 e/02-04-2020").execute(state, ui));
        assertEquals("", output.toString());
        assertEquals(1, state.getTrips().displayLines().size());
        AppState restored = roundTrip(state);
        assertFalse(restored.getTrips().hasCurrent());
        restored.getTrips().select(1);
        assertEquals("Japan", restored.getTrips().getCurrentName());
    }

    @Test
    void selectedNameBeforeRecords_resolvesAfterLoadingAndInvalidNameClearsSelection() throws Exception {
        AppState state = load("[trips]\nA|Japan\nT|Japan|01-02-2020|02-02-2020\n");
        assertEquals("Japan", state.getTrips().getCurrentName());
        state = load(NAMED + "A|Missing\n");
        assertFalse(state.getTrips().hasCurrent());
        assertEquals(2, state.getTrips().displayLines().size());
        assertTrue(output.toString().contains("Selected trip not found"));
    }

    @Test
    void malformedLegacy_doesNotProduceIncompleteTripAndIsIgnoredWhenNamedExists() throws Exception {
        for (String dates : new String[] {"start=01-01-2020\n",
            "start=02-01-2020\nend=01-01-2020\n", "start=bad\nend=01-01-2020\n"}) {
            assertFalse(load("[trip]\n" + dates).getTrips().hasCurrent());
        }
        output.reset();
        assertEquals("Korea", load("[trip]\ninvalid stale copy\n" + NAMED).getTrips().getCurrentName());
        assertEquals("", output.toString());
    }

    @Test
    void tripSet_emptyCreatesOriginalAndLaterUpdatesOnlySelection() throws Exception {
        AppState state = new AppState();
        Parser.parse("trip set s/01-01-2020 e/02-01-2020").execute(state, ui);
        assertEquals("Original trip", state.getTrips().getCurrentName());
        Parser.parse("trip add n/Japan s/01-02-2020 e/02-02-2020").execute(state, ui);
        Parser.parse("trip set s/03-02-2020 e/04-02-2020").execute(state, ui);
        state.getTrips().select(1);
        assertEquals(LocalDate.of(2020, 1, 1), state.getTrip().getStart());
        assertEquals(2, state.getTrips().displayLines().size());
        assertEquals(state.getTrips().displayLines(), roundTrip(state).getTrips().displayLines());
    }

    @Test
    void emptyBookAndContains_haveSafeBoundaries() throws Exception {
        TripBook book = new TripBook();
        assertNull(book.getCurrent());
        assertFalse(book.hasCurrent());
        assertThrows(TripJioException.class, () -> book.select(1));
        Trip trip = new Trip();
        LocalDate start = LocalDate.of(2020, 1, 1);
        assertFalse(trip.contains(start));
        trip.set(start, start.plusDays(1));
        assertTrue(trip.contains(start));
        assertTrue(trip.contains(start.plusDays(1)));
        assertFalse(trip.contains(start.minusDays(1)));
        assertFalse(trip.contains(start.plusDays(2)));
        assertFalse(trip.contains(null));
    }

    @Test
    void tripStorage_withBudget_loadsBudgetAndSkipsInvalidBudget() throws Exception {
        AppState state = load("[trips]\nT|BudgetTrip|01-02-2020|10-02-2020|1200.50\n"
                + "T|BadBudget|01-03-2020|10-03-2020|not_a_number\n"
                + "A|BudgetTrip\n");
        assertEquals(1, state.getTrips().displayLines().size());
        assertEquals("BudgetTrip", state.getTrips().getCurrentName());
        assertTrue(state.getTrip().hasBudget());
        assertEquals(1200.50, state.getTrip().getBudget());
        assertTrue(output.toString().contains("Skipped invalid line 3"));

        AppState restored = roundTrip(state);
        assertTrue(restored.getTrip().hasBudget());
        assertEquals(1200.50, restored.getTrip().getBudget());
    }
}
