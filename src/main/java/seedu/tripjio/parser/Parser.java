package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.ExitCommand;
import seedu.tripjio.command.HelpCommand;
import seedu.tripjio.exception.TripJioException;

import java.util.Locale;

/**
 * Turns a line of user input into the {@link Command} to run.
 * It only reads the command word; the arguments are passed on to the parser of each feature,
 * so each team member only needs to change one line in this class.
 */
public class Parser {

    private Parser() {
    }

    /**
     * Returns the command described by the given user input.
     * The command word is case-insensitive. Extra words after {@code help} and {@code exit} are ignored,
     * as stated in the User Guide.
     *
     * @param input Full line entered by the user, e.g. "add d/Lunch a/12.50".
     * @return Command to run.
     * @throws TripJioException If the input is empty, the command word is unknown,
     *     or the arguments are invalid.
     */
    public static Command parse(String input) throws TripJioException {
        assert input != null : "Input must not be null";

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new TripJioException("Please enter a command. Type help to see all commands.");
        }

        // Split into at most 2 parts: the command word and everything after it.
        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase(Locale.ROOT);
        String args = parts.length > 1 ? parts[1] : "";

        switch (commandWord) {
        case "help":
            return new HelpCommand();
        case "exit":
            return new ExitCommand();
        case "add":
        case "list":
        case "edit":
        case "delete":
            // TODO: A - return ExpenseCommandParser.parse(commandWord, args);
        case "split":
            // TODO: D - return SplitCommandParser.parse(args);
        case "budget":
            // TODO: B - return BudgetCommandParser.parse(args);
        case "trip":
        case "stats":
            // TODO: C - return TripCommandParser.parse(commandWord, args);
            throw new TripJioException("This command is not available yet.");
        default:
            throw new TripJioException("Unknown command: " + commandWord + ". Type help to see all commands.");
        }
    }
}
