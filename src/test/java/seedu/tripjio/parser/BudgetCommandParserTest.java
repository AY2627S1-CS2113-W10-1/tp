package seedu.tripjio.parser;

import seedu.tripjio.command.BudgetStatusCommand;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.SetBudgetCommand;
import seedu.tripjio.exception.TripJioException;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetCommandParserTest {

    @Test
    void parse_setValidDefaultBudget_returnsSetBudgetCommand() throws TripJioException {
        Command command = BudgetCommandParser.parse("set a/1000");
        assertTrue(command instanceof SetBudgetCommand);

        SetBudgetCommand setCmd = (SetBudgetCommand) command;
        assertEquals(1000.00, setCmd.getAmount());
        assertNull(setCmd.getMonth());
    }

    @Test
    void parse_setValidMonthBudget_returnsSetBudgetCommand() throws TripJioException {
        Command command = BudgetCommandParser.parse("set a/1500.50 m/12-2026");
        assertTrue(command instanceof SetBudgetCommand);

        SetBudgetCommand setCmd = (SetBudgetCommand) command;
        assertEquals(1500.50, setCmd.getAmount());
        assertEquals(YearMonth.of(2026, 12), setCmd.getMonth());
    }

    @Test
    void parse_setMissingAmount_throwsException() {
        TripJioException e = assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set m/12-2026"));
        assertEquals("Missing parameter: a/", e.getMessage());
    }

    @Test
    void parse_setInvalidAmount_throwsException() {
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set a/abc"));
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set a/-50"));
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set a/0"));
    }

    @Test
    void parse_setInvalidMonth_throwsException() {
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set a/100 m/invalid"));
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("set a/100 m/13-2026"));
    }

    @Test
    void parse_statusNoMonth_returnsBudgetStatusCommand() throws TripJioException {
        Command command = BudgetCommandParser.parse("status");
        assertTrue(command instanceof BudgetStatusCommand);
    }

    @Test
    void parse_statusValidMonth_returnsBudgetStatusCommand() throws TripJioException {
        Command command = BudgetCommandParser.parse("status m/10-2026");
        assertTrue(command instanceof BudgetStatusCommand);

        BudgetStatusCommand statusCmd = (BudgetStatusCommand) command;
        assertEquals(YearMonth.of(2026, 10), statusCmd.getMonth());
    }

    @Test
    void parse_statusInvalidMonth_throwsException() {
        assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("status m/2026-10"));
    }

    @Test
    void parse_emptyPreamble_throwsException() {
        TripJioException e = assertThrows(TripJioException.class, () -> BudgetCommandParser.parse(""));
        assertEquals("Please specify a budget command: 'budget set' or 'budget status'.", e.getMessage());
    }

    @Test
    void parse_unknownAction_throwsException() {
        TripJioException e = assertThrows(TripJioException.class, () -> BudgetCommandParser.parse("view"));
        assertEquals("Unknown budget command: view. Use 'budget set' or 'budget status'.", e.getMessage());
    }

    @Test
    void parse_caseInsensitiveAction_success() throws TripJioException {
        Command setCommand = BudgetCommandParser.parse("SET a/500");
        assertTrue(setCommand instanceof SetBudgetCommand);

        Command statusCommand = BudgetCommandParser.parse("STATUS");
        assertTrue(statusCommand instanceof BudgetStatusCommand);
    }
}
