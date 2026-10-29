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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests setting trip budgets, including parsing, validation, execution and storage.
 */
class TripBudgetCommandTest {
    @TempDir
    Path tempDir;

    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    @Test
    void execute_selectedTrip_setsBudgetAndConfirms() throws TripJioException {
        state.getTrips().add("Japan", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 14));
        Command command = new TripBudgetCommand(1500.50);
        command.execute(state, ui);

        assertTrue(command.isMutating());
        assertFalse(command.isExit());
        assertNull(command.getAffectedMonth());
        assertEquals(1500.50, ((TripBudgetCommand) command).getAmount());

        Trip currentTrip = state.getTrips().getCurrent();
        assertTrue(currentTrip.hasBudget());
        assertEquals(1500.50, currentTrip.getBudget());
        assertTrue(output.toString().contains("Budget for trip Japan set to $1,500.50."));
    }

    @Test
    void execute_noTripSelected_throwsTripJioException() {
        Command command = new TripBudgetCommand(500.0);
        TripJioException exception = assertThrows(TripJioException.class, () -> command.execute(state, ui));
        assertTrue(exception.getMessage().contains("No trip selected."));
    }

    @Test
    void parse_validInput_returnsTripBudgetCommand() throws TripJioException {
        Command command = Parser.parse("trip budget a/1200");
        assertInstanceOf(TripBudgetCommand.class, command);
        assertEquals(1200.0, ((TripBudgetCommand) command).getAmount());

        Command uppercaseCommand = Parser.parse("TRIP budget a/2500.75");
        assertInstanceOf(TripBudgetCommand.class, uppercaseCommand);
        assertEquals(2500.75, ((TripBudgetCommand) uppercaseCommand).getAmount());
    }

    @Test
    void parse_missingAmount_throwsException() {
        TripJioException exception = assertThrows(TripJioException.class, () -> Parser.parse("trip budget"));
        assertEquals("Missing parameter: a/", exception.getMessage());
    }

    @Test
    void parse_invalidAmount_throwsException() {
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/0"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/-50"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/abc"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/10000000"));
    }

    @Test
    void parse_unexpectedParameters_throwsException() {
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget extra a/500"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/500 extra"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/500 s/10-12-2026"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip budget a/500 a/600"));
    }

    @Test
    void storage_roundTrip_preservesTripBudget() throws Exception {
        state.getTrips().add("Taiwan", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 10));
        new TripBudgetCommand(800.00).execute(state, ui);

        Storage storage = new Storage(tempDir.resolve("tripjio.txt").toString());
        storage.save(state);

        AppState loadedState = storage.load(ui);
        assertTrue(loadedState.getTrips().hasCurrent());
        assertEquals("Taiwan", loadedState.getTrips().getCurrentName());
        Trip loadedTrip = loadedState.getTrips().getCurrent();
        assertTrue(loadedTrip.hasBudget());
        assertEquals(800.00, loadedTrip.getBudget());
    }

    @Test
    void loadStorageLine_threeDecimalBudget_throwsTripJioException() {
        TripJioException exception = assertThrows(TripJioException.class,
                () -> state.getTrips().loadStorageLine("T|Japan|01-10-2026|05-10-2026|1.234"));
        assertTrue(exception.getMessage().contains("Invalid budget in trip line"));
    }
}
