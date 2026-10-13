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

class BudgetStatusCommandTest {

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
    void execute_noBudgetSet_showsNoBudgetSetMessage() {
        LocalDate today = LocalDate.of(2026, 10, 15);
        BudgetStatusCommand command = new BudgetStatusCommand(YearMonth.of(2026, 10), today);
        command.execute(state, ui);

        String expected = "No budget has been set for October 2026." + System.lineSeparator();
        assertEquals(expected, output.toString());
    }

    @Test
    void execute_currentMonthWithinBudget_showsFullStatusWithAllowance() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 15);
        YearMonth oct2026 = YearMonth.of(2026, 10);
        state.getBudget().setDefault(1000.00);

        // Add $350 in expenses
        state.getExpenses().add(new Expense("Transport", 350.00, Category.TRANSPORT, today));

        BudgetStatusCommand command = new BudgetStatusCommand(oct2026, today);
        command.execute(state, ui);

        // October has 31 days. From 15th to 31st inclusive: 31 - 15 + 1 = 17 days left.
        // Remaining = $650.00. Daily allowance = 650.00 / 17 = 38.235... rounded to $38.24.
        String expected = String.join(System.lineSeparator(),
                "Budget status for October 2026:",
                "  Budget: $1,000.00",
                "  Spent: $350.00",
                "  Remaining: $650.00",
                "  Days left: 17 days (including today)",
                "  Daily allowance: $38.24/day") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_currentMonthOverBudget_showsOverBudgetAndZeroAllowance() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 15);
        YearMonth oct2026 = YearMonth.of(2026, 10);
        state.getBudget().setDefault(1000.00);

        // Add $1050 in expenses
        state.getExpenses().add(new Expense("Flight", 1050.00, Category.TRANSPORT, today));

        BudgetStatusCommand command = new BudgetStatusCommand(oct2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Budget status for October 2026:",
                "  Budget: $1,000.00",
                "  Spent: $1,050.00",
                "  Over budget by: $50.00",
                "  Days left: 17 days (including today)",
                "  Daily allowance: $0.00/day") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_lastDayOfCurrentMonth_showsSingularDay() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 31);
        YearMonth oct2026 = YearMonth.of(2026, 10);
        state.getBudget().setDefault(1000.00);

        BudgetStatusCommand command = new BudgetStatusCommand(oct2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Budget status for October 2026:",
                "  Budget: $1,000.00",
                "  Spent: $0.00",
                "  Remaining: $1,000.00",
                "  Days left: 1 day (including today)",
                "  Daily allowance: $1,000.00/day") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }

    @Test
    void execute_nonCurrentMonth_omitsDaysLeftAndDailyAllowance() throws TripJioException {
        LocalDate today = LocalDate.of(2026, 10, 15);
        YearMonth sep2026 = YearMonth.of(2026, 9);
        state.getBudget().setForMonth(sep2026, 800.00);
        state.getExpenses().add(new Expense("Books", 150.00, Category.OTHER, LocalDate.of(2026, 9, 5)));

        BudgetStatusCommand command = new BudgetStatusCommand(sep2026, today);
        command.execute(state, ui);

        String expected = String.join(System.lineSeparator(),
                "Budget status for September 2026:",
                "  Budget: $800.00",
                "  Spent: $150.00",
                "  Remaining: $650.00") + System.lineSeparator();

        assertEquals(expected, output.toString());
    }
}
