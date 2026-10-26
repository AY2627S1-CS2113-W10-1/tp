package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseFilter;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Exercises trip filtering through the shared parser, preserving global indexes and SGD totals. */
class TripFilterCommandTest {
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    private void run(String command) throws TripJioException {
        Parser.parse(command).execute(state, ui);
    }

    private void seed() throws TripJioException {
        run("add d/Before a/999 c/food dt/30-12-2019");
        run("add d/Start a/10 c/food dt/31-12-2019");
        run("split d/Shared a/90 n/3 c/food dt/01-01-2020");
        run("rate set from/EUR to/SGD r/2");
        run("add d/End a/5 cur/EUR c/transport dt/02-01-2020");
        run("add d/After a/999 c/food dt/03-01-2020");
        run("trip add n/Holiday s/31-12-2019 e/02-01-2020");
        output.reset();
    }

    @Test
    void list_intersectsFiltersAndSortingPreservesGlobalIndexes() throws Exception {
        seed();
        run("list trip/ c/food m/01-2020 sort/amount order/desc");
        assertTrue(output.toString().contains("3. "));
        assertFalse(output.toString().contains("2. "));
        assertFalse(output.toString().contains("Before"));
        assertTrue(output.toString().contains("Total: $30.00"));
        output.reset();
        run("list sort/amount trip/");
        String text = output.toString();
        assertTrue(text.indexOf("3. ") < text.indexOf("2. "));
        assertTrue(text.indexOf("2. ") < text.indexOf("4. "));
        assertTrue(text.contains("Total: $50.00"));
        run("edit 3 d/Changed");
        assertEquals("Changed", state.getExpenses().get(3).getDescription());
        assertEquals("Before", state.getExpenses().get(1).getDescription());
    }

    @Test
    void stats_wholeTripAndExplicitMonthCountBoundaryDatesAndSgdShares() throws Exception {
        seed();
        run("stats trip/");
        assertTrue(output.toString().contains("Spending for trip Holiday:"));
        assertTrue(output.toString().contains("Food: $40.00"));
        assertTrue(output.toString().contains("Transport: $10.00"));
        assertTrue(output.toString().contains("TOTAL SPENT: $50.00"));
        output.reset();
        run("stats m/01-2020 trip/");
        assertTrue(output.toString().contains("in January 2020:"));
        assertTrue(output.toString().contains("TOTAL SPENT: $40.00"));
        assertEquals(5, state.getExpenses().size());
    }

    @Test
    void splitList_usesSharedDateRangeAndOriginalIndexes() throws Exception {
        seed();
        run("split d/Outside a/120 n/3 dt/03-01-2020");
        output.reset();
        run("split list trip/ m/01-2020");
        assertTrue(output.toString().contains("3. "));
        assertFalse(output.toString().contains("Outside"));
        output.reset();
        run("split list m/12-2019 trip/");
        assertTrue(output.toString().contains("No split expenses match the given filter."));
    }

    @Test
    void noSelection_errorsBeforeOutputEvenWithNoExpenses() throws Exception {
        for (String input : new String[] {"list trip/", "stats trip/", "split list trip/"}) {
            assertThrows(TripJioException.class, () -> run(input));
            assertEquals("", output.toString());
        }
        state.getTrips().loadStorageLine("T|Unselected|01-01-2020|02-01-2020");
        assertFalse(state.getTrips().hasCurrent());
        assertThrows(TripJioException.class, () -> run("stats trip/"));
        assertEquals("", output.toString());
    }

    @Test
    void repeatedValuedUnknownAndUnsupportedFlags_areRejected() {
        for (String command : new String[] {"list trip/1", "list trip/ trip/", "list trip/ x/test",
            "stats trip/Holiday", "stats trip/ trip/", "stats trip/ extra", "stats trip/ c/food",
            "split list trip/1", "split list trip/ trip/", "split list trip/ x/test",
            "stats compare m/01-2020 m/02-2020 trip/", "add d/A a/1 trip/",
            "budget status trip/", "trip status trip/"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(command), command);
        }
    }

    @Test
    void filterResolvesSelectionAtExecutionAndLeavesUnfilteredReportsUnchanged() throws Exception {
        seed();
        Command command = Parser.parse("stats trip/");
        run("trip add n/Next s/03-01-2020 e/03-01-2020");
        output.reset();
        command.execute(state, ui);
        assertTrue(output.toString().contains("TOTAL SPENT: $999.00"));
        run("trip select 1");
        output.reset();
        command.execute(state, ui);
        assertTrue(output.toString().contains("TOTAL SPENT: $50.00"));
        output.reset();
        run("stats m/01-2020");
        assertTrue(output.toString().contains("TOTAL SPENT: $1,039.00"));
        for (String input : new String[] {"list trip/", "stats trip/", "split list trip/"}) {
            Command report = Parser.parse(input);
            assertFalse(report.isMutating());
            assertNull(report.getAffectedMonth());
        }
    }

    @Test
    void emptyIntersection_showsEmptyResultsAndZeroCategories() throws Exception {
        seed();
        run("stats trip/ m/01-2010");
        assertTrue(output.toString().contains("TOTAL SPENT: $0.00"));
        assertTrue(output.toString().contains("No expenses match the selected trip filter."));
        output.reset();
        run("list trip/ m/01-2010");
        assertTrue(output.toString().contains("No expenses match the given filter."));
    }

    @Test
    void dateRangeHelper_returnsFreshInclusiveInsertionOrderedList() throws Exception {
        seed();
        LocalDate start = LocalDate.of(2019, 12, 31);
        LocalDate end = LocalDate.of(2020, 1, 2);
        List<Expense> result = state.getExpenses().getExpensesBetween(start, end);
        assertEquals(List.of(state.getExpenses().get(2), state.getExpenses().get(3),
                state.getExpenses().get(4)), result);
        result.clear();
        assertEquals(5, state.getExpenses().size());
        assertEquals(1, state.getExpenses().getExpensesBetween(start, start).size());
        assertThrows(IllegalArgumentException.class, () -> state.getExpenses().getExpensesBetween(end, start));
        assertThrows(IllegalArgumentException.class, () -> state.getExpenses().getExpensesBetween(null, end));
        assertEquals(3, ExpenseFilter.select(state, null, null, true).size());
    }
}
