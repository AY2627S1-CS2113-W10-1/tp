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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SplitCommand}.
 */
public class SplitCommandTest {

    private AppState state;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        state = new AppState();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(new ByteArrayOutputStream()));
    }

    @Test
    public void execute_singleExpense_addsToState() throws TripJioException {
        // Notice the new trailing arguments: ArrayList for names, and nulls for payer, currency, and rate
        SplitCommand command = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        command.execute(state, ui);

        assertEquals(1, state.getExpenses().size());
        // ExpenseList is 1-indexed for the .get() method
        assertTrue(state.getExpenses().get(1) instanceof SplitExpense);
    }

    @Test
    public void execute_multipleExpenses_addsToState() throws TripJioException {
        SplitCommand command1 = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now(), 
                                                 new ArrayList<>(), null, null, null);
        command1.execute(state, ui);

        SplitCommand command2 = new SplitCommand("Dinner", 120.00, 4, null, Category.FOOD, LocalDate.now(), 
                                                 new ArrayList<>(), null, null, null);
        command2.execute(state, ui);

        assertEquals(2, state.getExpenses().size());
    }

    @Test
    public void isMutating_returnsTrue() {
        SplitCommand command = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        assertTrue(command.isMutating());
    }
}
