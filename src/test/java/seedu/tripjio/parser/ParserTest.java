package seedu.tripjio.parser;

import seedu.tripjio.command.ExitCommand;
import seedu.tripjio.command.HelpCommand;
import seedu.tripjio.exception.TripJioException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    @Test
    void parse_help_returnsHelpCommand() throws TripJioException {
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    @Test
    void parse_upperCaseExit_returnsExitCommand() throws TripJioException {
        assertInstanceOf(ExitCommand.class, Parser.parse("EXIT"));
    }

    @Test
    void parse_extraSpacesAndWords_ignored() throws TripJioException {
        assertInstanceOf(HelpCommand.class, Parser.parse("   help   me please "));
    }

    @Test
    void parse_emptyInput_throws() {
        for (String input : new String[] {"", "   "}) {
            TripJioException e = assertThrows(TripJioException.class, () -> Parser.parse(input));
            assertEquals("Please enter a command. Type help to see all commands.", e.getMessage());
        }
    }

    @Test
    void parse_unknownCommand_throws() {
        TripJioException e = assertThrows(TripJioException.class, () -> Parser.parse("abc"));
        assertEquals("Unknown command: abc. Type help to see all commands.", e.getMessage());
    }
}
