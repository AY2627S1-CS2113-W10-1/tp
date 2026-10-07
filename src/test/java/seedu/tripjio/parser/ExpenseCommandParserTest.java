package seedu.tripjio.parser;

import seedu.tripjio.command.AddCommand;
import seedu.tripjio.command.DeleteCommand;
import seedu.tripjio.command.EditCommand;
import seedu.tripjio.command.ListCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseCommandParserTest {
    private static final String TOMORROW = DateUtil.formatDate(LocalDate.now().plusDays(1));

    @Test
    void parse_validCommands_returnCorrectCommandTypes() throws TripJioException {
        assertInstanceOf(AddCommand.class, Parser.parse("add d/Lunch a/12.50 c/food dt/01-09-2026"));
        assertInstanceOf(AddCommand.class, Parser.parse("ADD a/5 d/Snack"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ListCommand.class, Parser.parse("list c/food m/10-2026"));
        assertInstanceOf(EditCommand.class, Parser.parse("edit 1 a/13.20"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    void parseAdd_invalidInput_throws() {
        String[] inputs = {
            "add a/12.50",                         // missing description
            "add d/Lunch",                         // missing amount
            "add d/Lunch a/12.345",                // too many decimal places
            "add d/Lunch a/5 c/drinks",            // unknown category
            "add d/Lunch a/5 dt/2026-09-01",       // wrong date format
            "add d/Lunch a/5 dt/" + TOMORROW,      // date in the future
            "add hello d/Lunch a/5",               // extra text before the prefixes
            "add d/Lunch a/5 a/6",                 // repeated prefix
        };
        for (String input : inputs) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), "Input: " + input);
        }
    }

    @Test
    void parseList_invalidInput_throws() {
        for (String input : new String[] {"list c/drinks", "list m/2026-10", "list all"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), "Input: " + input);
        }
    }

    @Test
    void parseEdit_invalidInput_throws() {
        String[] inputs = {
            "edit 1",                              // no field to edit
            "edit a/5",                            // missing index
            "edit 0 a/5",                          // index not positive
            "edit 1 a/-5",                         // invalid amount
            "edit 1 dt/" + TOMORROW,               // date in the future
        };
        for (String input : inputs) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), "Input: " + input);
        }
    }

    @Test
    void parseDelete_invalidIndex_throws() {
        for (String input : new String[] {"delete", "delete abc", "delete 0", "delete -1"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), "Input: " + input);
        }
    }
}
