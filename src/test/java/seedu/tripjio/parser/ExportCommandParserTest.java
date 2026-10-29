package seedu.tripjio.parser;

import org.junit.jupiter.api.Test;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.ExportCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks that export accepts the same filters as list, in any order, and rejects everything else. */
class ExportCommandParserTest {
    /** Parses the input through the main Parser, so the "export" case is tested as well. */
    private static ExportCommand parse(String input) throws TripJioException {
        return assertInstanceOf(ExportCommand.class, Parser.parse(input));
    }

    @Test
    void parse_noArguments_exportsEverythingToDefaultFile() throws TripJioException {
        ExportCommand command = parse("export");

        assertNull(command.getCategory());
        assertNull(command.getMonth());
        assertFalse(command.isTripOnly());
        assertEquals(ExportCommand.DEFAULT_FILE, command.getFilePath());
    }

    @Test
    void parse_allFilters_allSet() throws TripJioException {
        ExportCommand command = parse("export c/food m/10-2026 trip/");

        assertEquals(Category.FOOD, command.getCategory());
        assertEquals(YearMonth.of(2026, 10), command.getMonth());
        assertTrue(command.isTripOnly());
    }

    @Test
    void parse_anyOrderAndUpperCase_sameResult() throws TripJioException {
        ExportCommand command = parse("  EXPORT  trip/ m/01-2027 c/TRANSPORT  ");

        assertEquals(Category.TRANSPORT, command.getCategory());
        assertEquals(YearMonth.of(2027, 1), command.getMonth());
        assertTrue(command.isTripOnly());
    }

    @Test
    void parse_singleFilter_othersEmpty() throws TripJioException {
        ExportCommand byMonth = parse("export m/09-2026");
        assertNull(byMonth.getCategory());
        assertEquals(YearMonth.of(2026, 9), byMonth.getMonth());
        assertFalse(byMonth.isTripOnly());

        ExportCommand byTrip = parse("export trip/");
        assertNull(byTrip.getCategory());
        assertNull(byTrip.getMonth());
        assertTrue(byTrip.isTripOnly());
    }

    @Test
    void parse_readOnlyCommand_noSaveAndNoBudgetCheck() throws TripJioException {
        Command command = parse("export c/food");

        assertFalse(command.isMutating());
        assertFalse(command.isExit());
        assertNull(command.getAffectedMonth());
    }

    @Test
    void parse_invalidInput_throws() {
        String[] inputs = {
            "export c/drinks",                 // unknown category
            "export c/",                       // empty category
            "export m/2026-10",                // wrong month format
            "export m/13-2026",                // month out of range
            "export m/",                       // empty month
            "export c/food c/transport",       // repeated prefix
            "export m/10-2026 m/11-2026",      // repeated prefix
            "export trip/1",                   // trip/ takes no value
            "export trip/ trip/",              // repeated flag
            "export sort/amount",              // sorting is only for list
            "export d/Lunch",                  // prefix not allowed
            "export all",                      // text before the parameters
            "export file.csv",                 // file name is not supported
        };
        for (String input : inputs) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), "Input: " + input);
        }
    }
}
