package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForecastCommandTest {

    private AppState state;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        state = new AppState();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    @Test
    void execute_currentMonthWithinBudget_showsOnTrack() throws TripJioException {
        // Today is 10 October 2026 (day 10 of 31). Budget is $1,000.00.
        // Spent so far: $200.00.
        // Pace: 200 / 10 = $20.00/day.
        // Projected: 20 * 31 = $620.00. Buffer: $380.00.
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth oct2026 = YearMonth.of(2026, 10);
        state.getBudget().setDefault(1000.00);
        state.getExpenses().add(new Expense("Groceries", 200.00, Category.FOOD, today));

        ForecastCommand command = new ForecastCommand(oct2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Spending forecast for October 2026:",
                "  Budget: $1,000.00",
                "  Spent so far (day 10 of 31): $200.00",
                "  Current pace: $20.00/day",
                "  Projected total spending: $620.00",
                "  Status: On track to stay within budget ($380.00 buffer remaining).") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_currentMonthExceedingBudget_showsProjectedToExceed() throws TripJioException {
        // Today is 10 October 2026 (day 10 of 31). Budget is $500.00.
        // Spent so far: $250.00.
        // Pace: 250 / 10 = $25.00/day.
        // Projected: 25 * 31 = $775.00. Exceeds by: $275.00.
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth oct2026 = YearMonth.of(2026, 10);
        state.getBudget().setDefault(500.00);
        state.getExpenses().add(new Expense("Shopping", 250.00, Category.SHOPPING, today));

        ForecastCommand command = new ForecastCommand(oct2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Spending forecast for October 2026:",
                "  Budget: $500.00",
                "  Spent so far (day 10 of 31): $250.00",
                "  Current pace: $25.00/day",
                "  Projected total spending: $775.00",
                "  Status: Projected to exceed budget by $275.00!") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_currentMonthNoBudget_showsNoBudgetSet() {
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth oct2026 = YearMonth.of(2026, 10);

        ForecastCommand command = new ForecastCommand(oct2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Spending forecast for October 2026:",
                "  Budget: No budget set",
                "  Spent so far (day 10 of 31): $0.00",
                "  Current pace: $0.00/day",
                "  Projected total spending: $0.00",
                "  Status: No budget set. (Use 'budget set a/AMOUNT' to set one)") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_futureMonth_showsCannotForecast() {
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth nov2026 = YearMonth.of(2026, 11);

        ForecastCommand command = new ForecastCommand(nov2026, today);
        command.execute(state, ui);

        String expected = "Cannot forecast for a future month (November 2026) because no days have passed yet."
                + System.lineSeparator();
        assertEquals(expected, output.toString());
    }

    @Test
    void execute_pastMonthWithinBudget_showsStayedWithinBudget() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth sep2026 = YearMonth.of(2026, 9);
        state.getBudget().setDefault(800.00);
        state.getExpenses().add(new Expense("Flight", 500.00, Category.TRANSPORT, LocalDate.of(2026, 9, 15)));

        ForecastCommand command = new ForecastCommand(sep2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Spending forecast for September 2026:",
                "  Note: This month has already ended.",
                "  Budget: $800.00",
                "  Total spent: $500.00",
                "  Status: Stayed within budget ($300.00 remaining).") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_pastMonthExceededBudget_showsExceededBudget() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 10);
        YearMonth sep2026 = YearMonth.of(2026, 9);
        state.getBudget().setDefault(400.00);
        state.getExpenses().add(new Expense("Hotel", 500.00, Category.ACCOMMODATION, LocalDate.of(2026, 9, 15)));

        ForecastCommand command = new ForecastCommand(sep2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Spending forecast for September 2026:",
                "  Note: This month has already ended.",
                "  Budget: $400.00",
                "  Total spent: $500.00",
                "  Status: Exceeded budget by $100.00.") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }
}
