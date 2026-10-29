package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the trip summary command: breakdown table, budget comparisons, daily allowance and parsing.
 */
class TripSummaryCommandTest {
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    @Test
    void execute_noTripSelected_throwsTripJioException() {
        TripSummaryCommand command = new TripSummaryCommand();
        TripJioException exception = assertThrows(TripJioException.class, () -> command.execute(state, ui));
        assertTrue(exception.getMessage().contains("No trip selected."));
    }

    @Test
    void execute_tripWithBudgetAndExpenses_showsFullSummaryTable() throws TripJioException {
        state.getTrips().add("Japan", LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 10));
        state.getTrip().setBudget(1000.00);

        state.getExpenses().add(new Expense("Lunch", 100.00, Category.FOOD, LocalDate.of(2020, 10, 2)));
        state.getExpenses().add(new Expense("Bus", 50.00, Category.TRANSPORT, LocalDate.of(2020, 10, 3)));

        // Today is 2020-10-05: ongoing, 6 days remaining (Oct 5 to Oct 10 inclusive)
        LocalDate today = LocalDate.of(2020, 10, 5);
        TripSummaryCommand command = new TripSummaryCommand(today);
        command.execute(state, ui);

        assertFalse(command.isMutating());
        assertFalse(command.isExit());
        assertNull(command.getAffectedMonth());
        assertNull(command.getAffectedDate());
        assertEquals(today, command.getToday());

        String result = output.toString();
        assertTrue(result.contains("Trip summary: Japan"));
        assertTrue(result.contains("Dates: 01-10-2020 to 10-10-2020 (10 day(s))"));
        assertTrue(result.contains("Status: Ongoing"));
        assertTrue(result.contains("Remaining trip days: 6"));
        assertTrue(result.contains("Budget: $1,000.00"));
        assertTrue(result.contains("Total spent: $150.00"));
        assertTrue(result.contains("Remaining budget: $850.00"));
        // 850 / 6 = 141.67/day
        assertTrue(result.contains("Daily allowance: $141.67/day"));
        assertTrue(result.contains("Spending by category:"));
        assertTrue(result.contains("Food"));
        assertTrue(result.contains("$100.00"));
        assertTrue(result.contains("66.7%"));
        assertTrue(result.contains("Transport"));
        assertTrue(result.contains("$50.00"));
        assertTrue(result.contains("33.3%"));
        assertTrue(result.contains("Total"));
        assertTrue(result.contains("100.0%"));
    }

    @Test
    void execute_tripWithoutBudget_showsBudgetNotSet() throws TripJioException {
        state.getTrips().add("Taiwan", LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 5));
        state.getExpenses().add(new Expense("Snack", 20.00, Category.FOOD, LocalDate.of(2020, 10, 2)));

        LocalDate today = LocalDate.of(2020, 10, 2);
        new TripSummaryCommand(today).execute(state, ui);

        String result = output.toString();
        assertTrue(result.contains("Trip summary: Taiwan"));
        assertTrue(result.contains("Budget: Not set"));
        assertTrue(result.contains("Total spent: $20.00"));
        assertTrue(result.contains("Daily allowance: N/A (no budget set)"));
    }

    @Test
    void execute_completedTrip_showsCompletedStatus() throws TripJioException {
        state.getTrips().add("Korea", LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 5));
        state.getTrip().setBudget(500.00);

        LocalDate today = LocalDate.of(2020, 10, 10);
        new TripSummaryCommand(today).execute(state, ui);

        String result = output.toString();
        assertTrue(result.contains("Status: Completed"));
        assertTrue(result.contains("Remaining trip days: 0"));
        assertTrue(result.contains("Daily allowance: N/A (trip completed)"));
    }

    @Test
    void execute_overBudgetTrip_showsOverBudgetAndZeroAllowance() throws TripJioException {
        state.getTrips().add("France", LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 5));
        state.getTrip().setBudget(100.00);
        state.getExpenses().add(new Expense("Dinner", 125.00, Category.FOOD, LocalDate.of(2020, 10, 2)));

        LocalDate today = LocalDate.of(2020, 10, 2);
        new TripSummaryCommand(today).execute(state, ui);

        String result = output.toString();
        assertTrue(result.contains("Budget: $100.00"));
        assertTrue(result.contains("Total spent: $125.00"));
        assertTrue(result.contains("Over budget by: $25.00"));
        assertTrue(result.contains("Daily allowance: $0.00/day"));
    }

    @Test
    void execute_noExpenses_showsNoExpensesMessage() throws TripJioException {
        state.getTrips().add("Italy", LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 5));

        new TripSummaryCommand(LocalDate.of(2020, 10, 1)).execute(state, ui);

        String result = output.toString();
        assertTrue(result.contains("No expenses recorded for this trip yet."));
        assertTrue(result.contains("Total"));
        assertTrue(result.contains("$0.00"));
    }

    @Test
    void parse_validCommand_returnsTripSummaryCommand() throws TripJioException {
        Command command = Parser.parse("trip summary");
        assertInstanceOf(TripSummaryCommand.class, command);

        Command uppercase = Parser.parse("  TRIP   summary  ");
        assertInstanceOf(TripSummaryCommand.class, uppercase);
    }

    @Test
    void parse_extraArguments_throwsException() {
        assertThrows(TripJioException.class, () -> Parser.parse("trip summary extra"));
        assertThrows(TripJioException.class, () -> Parser.parse("trip summary a/100"));
    }
}
