package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SetBudgetCommandTest {

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
    void execute_defaultBudget_setsDefaultBudgetAndPrintsMessage() throws TripJioException {
        SetBudgetCommand command = new SetBudgetCommand(1000.00);
        assertTrue(command.isMutating());
        assertEquals(1000.00, command.getAmount());

        command.execute(state, ui);

        assertEquals(1000.00, state.getBudget().getDefaultBudget());
        assertEquals("Default monthly budget set to $1,000.00." + System.lineSeparator(), output.toString());
    }

    @Test
    void execute_monthBudget_setsMonthBudgetAndPrintsMessage() throws TripJioException {
        YearMonth month = YearMonth.of(2026, 12);
        SetBudgetCommand command = new SetBudgetCommand(1500.00, month);
        assertTrue(command.isMutating());
        assertEquals(1500.00, command.getAmount());
        assertEquals(month, command.getMonth());

        command.execute(state, ui);

        assertEquals(1500.00, state.getBudget().getBudgetFor(month));
        assertEquals("Budget for December 2026 set to $1,500.00." + System.lineSeparator(), output.toString());
    }
}
