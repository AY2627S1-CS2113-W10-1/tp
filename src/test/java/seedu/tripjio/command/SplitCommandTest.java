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
        SplitCommand command = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now());
        command.execute(state, ui);

        assertEquals(1, state.getExpenses().size());
        assertTrue(state.getExpenses().get(1) instanceof SplitExpense);
    }

    @Test
    public void execute_multipleExpenses_addsToState() throws TripJioException {
        SplitCommand command1 = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now());
        command1.execute(state, ui);

        SplitCommand command2 = new SplitCommand("Dinner", 120.00, 4, null, Category.FOOD, LocalDate.now());
        command2.execute(state, ui);

        assertEquals(2, state.getExpenses().size());
    }

    @Test
    public void isMutating_returnsTrue() {
        SplitCommand command = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, LocalDate.now());
        assertTrue(command.isMutating());
    }

    @Test
    public void getAffectedMonth_returnsCorrectYearMonth() {
        LocalDate date = LocalDate.of(2026, 9, 15);
        SplitCommand command = new SplitCommand("Taxi", 60.00, 3, null, Category.OTHER, date);
        assertEquals(YearMonth.of(2026, 9), command.getAffectedMonth());
    }
}
