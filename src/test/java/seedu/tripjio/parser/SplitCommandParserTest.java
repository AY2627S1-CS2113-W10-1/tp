package seedu.tripjio.parser;

import org.junit.jupiter.api.Test;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.SplitListCommand;
import seedu.tripjio.exception.TripJioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SplitCommandParserTest {

    @Test
    public void parse_listCommand_returnsSplitListCommand() throws TripJioException {
        Command command = SplitCommandParser.parse("list");
        assertTrue(command instanceof SplitListCommand);
    }

    @Test
    public void parse_listCommandWithMonth_returnsSplitListCommand() throws TripJioException {
        Command command = SplitCommandParser.parse("list m/09-2026");
        assertTrue(command instanceof SplitListCommand);
    }

    @Test
    public void parse_validEqualSplit_success() throws TripJioException {
        Command command = SplitCommandParser.parse("d/Dinner a/90 n/3");
        assertNotNull(command);
    }

    @Test
    public void parse_validCustomSplit_success() throws TripJioException {
        Command command = SplitCommandParser.parse("d/Dinner a/90 n/3 my/45 c/FOOD");
        assertNotNull(command);
    }

    @Test
    public void parse_invalidPeople_throwsException() {
        TripJioException e1 = assertThrows(TripJioException.class, () ->
                SplitCommandParser.parse("d/Dinner a/90 n/1"));
        assertEquals("Number of people must be between 2 and 50.", e1.getMessage());

        TripJioException e2 = assertThrows(TripJioException.class, () ->
                SplitCommandParser.parse("d/Dinner a/90 n/51"));
        assertEquals("Number of people must be between 2 and 50.", e2.getMessage());
    }

    @Test
    public void parse_nonNumericPeople_throwsException() {
        TripJioException e = assertThrows(TripJioException.class, () ->
                SplitCommandParser.parse("d/Dinner a/90 n/abc"));
        assertEquals("Number of people must be between 2 and 50.", e.getMessage());
    }

    @Test
    public void parse_customShareExceedsTotal_throwsException() {
        assertThrows(TripJioException.class, () -> SplitCommandParser.parse("d/Dinner a/90 n/3 my/100"));
    }

    @Test
    public void parse_equalShareTooSmall_throwsException() {
        assertThrows(TripJioException.class, () -> SplitCommandParser.parse("d/Candy a/0.05 n/50"));
    }
}
