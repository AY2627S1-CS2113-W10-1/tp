package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the add, list, edit and delete commands, checking both the data and the output shown to the user.
 */
class ExpenseCommandsTest {
    private AppState state;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        state = new AppState();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    /** Returns everything printed so far, with Windows line endings changed to "\n". */
    private String getOutput() {
        return output.toString().replace("\r\n", "\n");
    }

    private void addSampleExpenses() {
        state.getExpenses().add(new Expense("Lunch", 12.50, Category.FOOD, LocalDate.of(2026, 10, 15)));
        state.getExpenses().add(new Expense("Metro card", 45, Category.TRANSPORT, LocalDate.of(2026, 10, 2)));
        state.getExpenses().add(new SplitExpense("Group dinner", 90, 3, null, Category.FOOD,
                LocalDate.of(2026, 9, 14)));
    }

    @Test
    void add_validExpense_addedAndShown() {
        AddCommand command = new AddCommand("Metro card", 45, Category.TRANSPORT, LocalDate.of(2026, 10, 2));
        command.execute(state, ui);

        assertEquals(1, state.getExpenses().size());
        assertEquals("Added expense:\n  [Transport] Metro card - $45.00 (02-10-2026)\nYou now have 1 expenses.\n",
                getOutput());
        assertTrue(command.isMutating());
        assertEquals(YearMonth.of(2026, 10), command.getAffectedMonth());
    }

    @Test
    void delete_validIndex_removedAndShown() throws TripJioException {
        addSampleExpenses();
        new DeleteCommand(1).execute(state, ui);

        assertEquals(2, state.getExpenses().size());
        assertEquals("Metro card", state.getExpenses().get(1).getDescription());
        assertTrue(getOutput().startsWith("Deleted expense:\n  [Food] Lunch - $12.50 (15-10-2026)\n"));
    }

    @Test
    void delete_invalidIndex_throwsAndNothingRemoved() {
        addSampleExpenses();
        assertThrows(TripJioException.class, () -> new DeleteCommand(4).execute(state, ui));
        assertEquals(3, state.getExpenses().size());
    }

    @Test
    void list_noExpenses_showsMessage() {
        new ListCommand(null, null).execute(state, ui);
        assertEquals("You have no expenses yet.\n", getOutput());
    }

    @Test
    void list_noFilter_showsAllWithTotalOfUserShare() {
        addSampleExpenses();
        new ListCommand(null, null).execute(state, ui);

        String result = getOutput();
        assertTrue(result.startsWith("Here are your expenses:\n1. [Food] Lunch"));
        // The split expense counts only the user's share: 12.50 + 45.00 + 30.00.
        assertTrue(result.endsWith("Total: $87.50\n"));
    }

    @Test
    void list_withFilters_keepsOriginalIndexes() {
        addSampleExpenses();
        new ListCommand(Category.FOOD, YearMonth.of(2026, 9)).execute(state, ui);

        String result = getOutput();
        assertTrue(result.contains("3. [Food] Group dinner"));
        assertTrue(!result.contains("Lunch") && !result.contains("Metro card"));
        assertTrue(result.endsWith("Total: $30.00\n"));
    }

    @Test
    void list_filterMatchesNothing_showsMessage() {
        addSampleExpenses();
        new ListCommand(Category.SHOPPING, null).execute(state, ui);
        assertEquals("No expenses match the given filter.\n", getOutput());
    }

    @Test
    void edit_someFields_onlyThoseChanged() throws TripJioException {
        addSampleExpenses();
        EditCommand command = new EditCommand(1, null, 13.20, null, LocalDate.of(2026, 11, 1));
        command.execute(state, ui);

        Expense edited = state.getExpenses().get(1);
        assertEquals("Lunch", edited.getDescription());
        assertEquals(13.20, edited.getAmount());
        assertEquals(Category.FOOD, edited.getCategory());
        assertEquals(LocalDate.of(2026, 11, 1), edited.getDate());
        assertEquals("Edited expense:\n  [Food] Lunch - $13.20 (01-11-2026)\n", getOutput());
        assertEquals(YearMonth.of(2026, 11), command.getAffectedMonth());
    }

    @Test
    void edit_invalidIndex_throws() {
        addSampleExpenses();
        EditCommand command = new EditCommand(9, "Dinner", null, null, null);
        assertThrows(TripJioException.class, () -> command.execute(state, ui));
        assertNull(command.getAffectedMonth());
    }
}
