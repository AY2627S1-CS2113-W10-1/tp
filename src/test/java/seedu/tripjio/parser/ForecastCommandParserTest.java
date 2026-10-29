package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.ForecastCommand;
import seedu.tripjio.exception.TripJioException;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForecastCommandParserTest {

    @Test
    void parse_noArguments_returnsForecastCommandForCurrentMonth() throws TripJioException {
        Command command = ForecastCommandParser.parse("");
        assertTrue(command instanceof ForecastCommand);
        assertEquals(YearMonth.now(), ((ForecastCommand) command).getMonth());
    }

    @Test
    void parse_validMonth_returnsForecastCommand() throws TripJioException {
        Command command = ForecastCommandParser.parse("m/11-2026");
        assertTrue(command instanceof ForecastCommand);
        assertEquals(YearMonth.of(2026, 11), ((ForecastCommand) command).getMonth());
    }

    @Test
    void parse_invalidMonth_throwsException() {
        assertThrows(TripJioException.class, () -> ForecastCommandParser.parse("m/2026-11"));
        assertThrows(TripJioException.class, () -> ForecastCommandParser.parse("m/13-2026"));
    }

    @Test
    void parse_unexpectedPreamble_throwsException() {
        assertThrows(TripJioException.class, () -> ForecastCommandParser.parse("now m/11-2026"));
    }

    @Test
    void parse_unexpectedPrefix_throwsException() {
        assertThrows(TripJioException.class, () -> ForecastCommandParser.parse("a/100"));
    }
}
