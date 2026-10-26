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
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies comparison validation, ordered changes, month boundaries and read-only behaviour. */
class StatsCompareCommandTest {
    /** Horizontal rule as wide as the comparison table. */
    private static final String LINE = "-".repeat(63);

    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    @Test
    void parse_validComparison_readOnly() throws TripJioException {
        Command command = Parser.parse("  STATS  COMPARE  m/12-2026  m/01-2027  ");
        assertInstanceOf(StatsCompareCommand.class, command);
        assertFalse(command.isMutating());
        assertFalse(command.isExit());
        assertNull(command.getAffectedMonth());
    }

    @Test
    void parse_invalidComparisons_throwWithoutOutput() {
        for (String args : new String[] {"", "m/01-2026", "m/01-2026 m/02-2026 m/03-2026",
            "m/ m/02-2026", "m/01-2026 m/", "m/13-2026 m/02-2026", "m/01-2026 m/00-2026",
            "m/1-2026 m/02-2026", "m/01-2026 m/2026-02", "extra m/01-2026 m/02-2026",
            "m/01-2026 m/02-2026 extra", "m/01-2026 m/02-2026 a/10",
            "x/unknown m/01-2026 m/02-2026", "m/01-2026 x/unknown m/02-2026",
            "m/01-2026 m/02-2026 x/unknown"}) {
            assertThrows(TripJioException.class, () -> Parser.parse("stats compare " + args), args);
        }
        assertEquals("", output.toString());
    }

    @Test
    void execute_categoryChangesAndSplitShares_correctOutput() throws TripJioException {
        state.getExpenses().add(new Expense("Food", 10.10, Category.FOOD, LocalDate.of(2028, 1, 31)));
        state.getExpenses().add(new Expense("Food", 0.20, Category.FOOD, LocalDate.of(2028, 2, 1)));
        state.getExpenses().add(new SplitExpense("Food", 90, 3, null, Category.FOOD, LocalDate.of(2028, 2, 29)));
        state.getExpenses().add(new SplitExpense("Bus", 100, 4, 12.50,
                Category.TRANSPORT, LocalDate.of(2028, 1, 1)));
        state.getExpenses().add(new Expense("Excluded", 999, Category.FOOD, LocalDate.of(2028, 3, 1)));
        state.getExpenses().add(new Expense("Other year", 999, Category.FOOD, LocalDate.of(2027, 2, 1)));
        Parser.parse("stats compare m/01-2028 m/02-2028").execute(state, ui);
        assertEquals("Spending comparison: January 2028 -> February 2028\n"
                + row("Category", "Jan 2028", "Feb 2028", "Change") + "\n"
                + LINE + "\n"
                + row("Food", "$10.10", "$30.20", "+$20.10") + "\n"
                + row("Transport", "$12.50", "$0.00", "-$12.50") + "\n"
                + row("Accommodation", "$0.00", "$0.00", "$0.00") + "\n"
                + row("Entertainment", "$0.00", "$0.00", "$0.00") + "\n"
                + row("Shopping", "$0.00", "$0.00", "$0.00") + "\n"
                + row("Other", "$0.00", "$0.00", "$0.00") + "\n"
                + LINE + "\n"
                + row("Total", "$22.60", "$30.20", "+$7.60") + "\n", output.toString().replace("\r\n", "\n"));
        assertTrue(output.toString().contains(
                "Food" + " ".repeat(21) + "$10.10" + " ".repeat(10) + "$30.20" + " ".repeat(9) + "+$20.10"));
        assertEquals(6, state.getExpenses().size());
        assertEquals(22.60, state.getExpenses().getTotalSpentInMonth(YearMonth.of(2028, 1)));
        assertFalse(state.getTrips().hasCurrent());
    }

    @Test
    void execute_reversedMonthsAcrossYears_preservesInputOrder() throws TripJioException {
        state.getExpenses().add(new Expense("Food", 5, Category.FOOD, LocalDate.of(2027, 1, 1)));
        Parser.parse("stats compare m/01-2027 m/12-2026").execute(state, ui);
        assertTrue(output.toString().contains("January 2027 -> December 2026"));
        assertTrue(output.toString().contains(row("Total", "$5.00", "$0.00", "-$5.00")));
        assertFalse(output.toString().contains("No expenses"));
    }

    @Test
    void execute_sameMonth_zeroChange() throws TripJioException {
        state.getExpenses().add(new Expense("Food", 0.10, Category.FOOD, LocalDate.of(2026, 10, 1)));
        Parser.parse("stats compare m/10-2026 m/10-2026").execute(state, ui);
        assertTrue(output.toString().contains(row("Total", "$0.10", "$0.10", "$0.00")));
        assertFalse(output.toString().contains("-$0.00"));
    }

    @Test
    void execute_emptyMonths_showsZeroAndMessage() throws TripJioException {
        Parser.parse("stats compare m/01-2026 m/12-2027").execute(state, ui);
        for (Category category : Category.values()) {
            assertTrue(output.toString().contains(row(category.getDisplayName(), "$0.00", "$0.00", "$0.00")));
        }
        assertTrue(output.toString().contains(row("Total", "$0.00", "$0.00", "$0.00")));
        assertTrue(output.toString().contains("No expenses recorded for either month."));
    }

    @Test
    void execute_largeTotals_columnsStayAligned() throws TripJioException {
        // 12 expenses of the maximum $1,000,000 give "-$12,000,000.00", which fills a whole 15-character column.
        for (int day = 1; day <= 12; day++) {
            state.getExpenses().add(new Expense("Flight", 1_000_000, Category.TRANSPORT, LocalDate.of(2026, 1, day)));
        }
        Parser.parse("stats compare m/01-2026 m/02-2026").execute(state, ui);
        String[] lines = output.toString().replace("\r\n", "\n").split("\n");
        // Every line after the title belongs to the table: 4 columns x 15 characters + 3 separating spaces.
        for (int i = 1; i < lines.length; i++) {
            assertEquals(63, lines[i].length(), lines[i]);
        }
        assertTrue(output.toString().contains(row("Total", "$12,000,000.00", "$0.00", "-$12,000,000.00")));
        assertTrue(output.toString().contains("$0.00 -$12,000,000.00"));
    }

    @Test
    void constructor_nullMonth_rejected() {
        YearMonth month = YearMonth.of(2026, 10);
        assertThrows(NullPointerException.class, () -> new StatsCompareCommand(null, month));
        assertThrows(NullPointerException.class, () -> new StatsCompareCommand(month, null));
    }

    /** Builds one expected table row with the same column widths as the command. */
    private static String row(String label, String first, String second, String change) {
        return String.format("%-15s %15s %15s %15s", label, first, second, change);
    }
}
