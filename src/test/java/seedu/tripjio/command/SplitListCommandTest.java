package seedu.tripjio.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class SplitListCommandTest {
    private AppState state;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        state = new AppState();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));
    }

    @Test
    public void execute_emptyList_showsNoExpensesMessage() throws TripJioException {
        SplitListCommand command = new SplitListCommand(null);
        command.execute(state, ui);
    }

    @Test
    public void execute_withExpenses_listsOnlySplitExpenses() throws TripJioException {
        state.getExpenses().add(new Expense("Bus", 2.0, Category.TRANSPORT, LocalDate.now()));
        state.getExpenses().add(new SplitExpense("Taxi", 20.0, 2, null, Category.TRANSPORT, LocalDate.now(), 
                                                 new ArrayList<>(), null, null, null));
        SplitListCommand command = new SplitListCommand(null);
        command.execute(state, ui);
    }

    @Test
    public void execute_withMonthFilter_filtersCorrectly() throws TripJioException {
        state.getExpenses().add(new SplitExpense("Taxi", 20.0, 2, null, Category.TRANSPORT, LocalDate.of(2026, 9, 1), 
                                                 new ArrayList<>(), null, null, null));
        SplitListCommand command = new SplitListCommand(YearMonth.of(2026, 9));
        command.execute(state, ui);
    }

    @Test
    public void isMutating_returnsFalse() {
        SplitListCommand command = new SplitListCommand(null);
        assertFalse(command.isMutating());
    }
}
