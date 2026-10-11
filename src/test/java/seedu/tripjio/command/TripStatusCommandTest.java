package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests status parsing, date boundaries, spending and read-only behaviour. */
class TripStatusCommandTest {
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    private TripStatusCommand on(String date) {
        Clock clock = Clock.fixed(LocalDate.parse(date).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        return new TripStatusCommand(clock);
    }

    @Test
    void parse_statusWithoutArguments_returnsReadOnlyCommand() throws TripJioException {
        Command command = Parser.parse("  TRIP   status  ");
        assertInstanceOf(TripStatusCommand.class, command);
        assertFalse(command.isMutating());
        assertFalse(command.isExit());
        assertNull(command.getAffectedMonth());
    }

    @Test
    void parse_extraArgumentsAndOtherCommands_rejected() {
        for (String input : new String[] {"trip status s/01-12-2026", "trip status extra",
            "trip status x/unknown", "trip status m/12-2026", "budget status", "split status", "stats"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    void execute_unsetOrIncompleteTrip_showsHelpfulErrorWithoutOutput() throws TripJioException {
        TripJioException error = assertThrows(TripJioException.class, () -> on("2026-12-10").execute(state, ui));
        assertTrue(error.getMessage().contains("trip set s/START_DATE e/END_DATE"));
        state.getTrip().loadStorageLine("start=10-12-2026");
        assertThrows(TripJioException.class, () -> on("2026-12-10").execute(state, ui));
        assertEquals("", output.toString());
    }

    @Test
    void execute_dateBoundaries_countBothEndpoints() throws TripJioException {
        state.getTrip().set(LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 12));
        String[] dates = {"2026-12-09", "2026-12-10", "2026-12-11", "2026-12-12", "2026-12-13"};
        String[] phases = {"Upcoming", "Ongoing", "Ongoing", "Ongoing", "Completed"};
        int[] remaining = {3, 3, 2, 1, 0};
        for (int i = 0; i < dates.length; i++) {
            output.reset();
            on(dates[i]).execute(state, ui);
            String text = output.toString();
            assertTrue(text.contains("Status: " + phases[i]), dates[i]);
            assertTrue(text.contains("Duration: 3 day(s)"), dates[i]);
            assertTrue(text.contains("Remaining trip days (including today): " + remaining[i]), dates[i]);
            assertTrue(text.contains("Recorded spending during trip: $0.00"));
        }
    }

    @Test
    void execute_sameDayLeapDayTrip_hasOneDay() throws TripJioException {
        LocalDate leapDay = LocalDate.of(2028, 2, 29);
        state.getTrip().set(leapDay, leapDay);
        on("2028-02-29").execute(state, ui);
        assertTrue(output.toString().contains("Duration: 1 day(s)"));
        assertTrue(output.toString().contains("Remaining trip days (including today): 1"));
        state.getTrip().set(leapDay.minusDays(1), leapDay.plusDays(1));
        assertEquals(3, state.getTrip().getDurationDays());
    }

    @Test
    void execute_spendingIncludesEndpointsAndOnlyUserShare_withoutMutation() throws TripJioException {
        LocalDate start = LocalDate.of(2026, 12, 31);
        LocalDate end = LocalDate.of(2027, 1, 2);
        state.getTrip().set(start, end);
        state.getExpenses().add(new Expense("Before", 100, Category.FOOD, start.minusDays(1)));
        state.getExpenses().add(new Expense("Start", 10.10, Category.FOOD, start));
        state.getExpenses().add(new SplitExpense("Dinner", 90, 3, null, Category.FOOD, start.plusDays(1)));
        state.getExpenses().add(new Expense("End", 20.20, Category.TRANSPORT, end));
        state.getExpenses().add(new Expense("After", 200, Category.FOOD, end.plusDays(1)));
        on("2027-01-02").execute(state, ui);
        assertTrue(output.toString().contains("Recorded spending during trip: $60.30"));
        assertEquals(start, state.getTrip().getStart());
        assertEquals(end, state.getTrip().getEnd());
        assertEquals(5, state.getExpenses().size());
    }
}
