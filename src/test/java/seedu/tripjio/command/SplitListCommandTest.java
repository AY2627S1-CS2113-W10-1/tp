package seedu.tripjio.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SplitListCommand}.
 */
public class SplitListCommandTest {

    private AppState state;
    private Ui ui;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        state = new AppState();
        outputStream = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(outputStream));
    }

    @Test
    public void execute_emptyList_showsNoExpensesMessage() throws TripJioException {
        SplitListCommand command = new SplitListCommand();
        command.execute(state, ui);

        String output = outputStream.toString();
        assertTrue(output.contains("You have no split expenses yet."));
    }

    @Test
    public void execute_withExpenses_listsOnlySplitExpenses() throws TripJioException {
        LocalDate date = LocalDate.of(2026, 10, 14);
        SplitExpense split1 = new SplitExpense("Dinner", 90.00, 3, null, Category.FOOD, date);
        state.getExpenses().add(split1);

        SplitListCommand command = new SplitListCommand();
        command.execute(state, ui);

        String output = outputStream.toString();
        assertTrue(output.contains("Here are your split expenses:"));
        assertTrue(output.contains("1. [Food] Dinner - $30.00"));
    }

    @Test
    public void execute_withMonthFilter_filtersCorrectly() throws TripJioException {
        LocalDate octDate = LocalDate.of(2026, 10, 14);
        LocalDate sepDate = LocalDate.of(2026, 9, 20);

        SplitExpense octExpense = new SplitExpense("Oct Dinner", 90.00, 3, null, Category.FOOD, octDate);
        SplitExpense sepExpense = new SplitExpense("Sep Lunch", 60.00, 2, null, Category.FOOD, sepDate);

        state.getExpenses().add(octExpense);
        state.getExpenses().add(sepExpense);

        SplitListCommand command = new SplitListCommand(YearMonth.of(2026, 9));
        command.execute(state, ui);

        String output = outputStream.toString();
        assertTrue(output.contains("Sep Lunch"));
        assertFalse(output.contains("Oct Dinner"));
    }

    @Test
    public void isMutating_returnsFalse() {
        SplitListCommand command = new SplitListCommand();
        assertFalse(command.isMutating());
    }
}
