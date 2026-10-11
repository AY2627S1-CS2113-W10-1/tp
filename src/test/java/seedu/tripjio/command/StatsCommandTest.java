package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.model.trip.Statistics;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies monthly reports, parsing, split shares and read-only execution. */
class StatsCommandTest {
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    @Test
    void parse_validCommands_readOnly() throws TripJioException {
        for (String input : new String[] {"stats", " STATS  m/02-2028  "}) {
            Command command = Parser.parse(input);
            assertInstanceOf(StatsCommand.class, command);
            assertFalse(command.isMutating());
            assertFalse(command.isExit());
            assertNull(command.getAffectedMonth());
        }
    }

    @Test
    void parse_invalidArguments_throwWithoutOutput() {
        for (String input : new String[] {"stats m/", "stats m/13-2026", "stats m/00-2026",
            "stats m/2-2026", "stats m/2026-02", "stats m/02-2026 m/03-2026",
            "stats m/02-2026 trailing", "stats c/food", "stats m/02-2026 a/10",
            "stats x/unknown", "stats m/02-2026 x/unknown", "stats compare m/01-2026 m/02-2026"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), input);
        }
        assertEquals("", output.toString());
    }

    @Test
    void execute_monthFilterAndSplitShares_correctTotalsAndOrder() throws TripJioException {
        state.getExpenses().add(new Expense("First day", 0.10, Category.FOOD, LocalDate.of(2028, 2, 1)));
        state.getExpenses().add(new Expense("Leap day", 0.20, Category.FOOD, LocalDate.of(2028, 2, 29)));
        state.getExpenses().add(new SplitExpense("Equal", 90, 3, null, Category.FOOD, LocalDate.of(2028, 2, 15)));
        state.getExpenses().add(new SplitExpense("Custom", 100, 4, 12.50,
                Category.TRANSPORT, LocalDate.of(2028, 2, 16)));
        state.getExpenses().add(new Expense("Before", 500, Category.FOOD, LocalDate.of(2028, 1, 31)));
        state.getExpenses().add(new Expense("After", 500, Category.FOOD, LocalDate.of(2028, 3, 1)));
        state.getExpenses().add(new Expense("Other year", 500, Category.FOOD, LocalDate.of(2027, 2, 1)));
        Parser.parse("stats m/02-2028").execute(state, ui);
        assertEquals("Spending for February 2028:\nFood: $30.30\nTransport: $12.50\n"
                + "Accommodation: $0.00\nEntertainment: $0.00\nShopping: $0.00\nOther: $0.00\n"
                + "==============================\nTOTAL SPENT: $42.80\n"
                + "==============================\n", output.toString().replace("\r\n", "\n"));
        assertEquals(7, state.getExpenses().size());
        assertFalse(state.getTrip().isSet());
    }

    @Test
    void execute_defaultMonthUsesClock_explicitMonthOverridesClock() {
        Clock clock = Clock.fixed(Instant.parse("2027-01-01T00:00:00Z"), ZoneOffset.UTC);
        new StatsCommand(null, clock).execute(state, ui);
        assertTrue(output.toString().contains("Spending for January 2027:"));
        assertTrue(output.toString().contains("TOTAL SPENT: $0.00"));
        assertTrue(output.toString().contains("No expenses recorded for this month."));
        output.reset();
        new StatsCommand(YearMonth.of(2026, 12), clock).execute(state, ui);
        assertTrue(output.toString().contains("Spending for December 2026:"));
    }

    @Test
    void statistics_allCategoriesAndFreshResults_doNotMutateExpenses() {
        YearMonth month = YearMonth.of(2026, 10);
        for (Category category : Category.values()) {
            state.getExpenses().add(new Expense(category.name(), 1.25, category, month.atDay(1)));
        }
        Map<Category, Double> totals = Statistics.getMonthlyTotals(state.getExpenses(), month);
        assertEquals(Category.values().length, totals.size());
        for (Category category : Category.values()) {
            assertEquals(1.25, totals.get(category));
        }
        totals.put(Category.FOOD, 999.0);
        assertEquals(1.25, Statistics.getMonthlyTotals(state.getExpenses(), month).get(Category.FOOD));
        assertEquals(7.50, state.getExpenses().getTotalSpentInMonth(month));
    }
}
