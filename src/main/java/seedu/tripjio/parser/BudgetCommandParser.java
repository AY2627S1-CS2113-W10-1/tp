package seedu.tripjio.parser;

import seedu.tripjio.command.BudgetStatusCommand;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.SetBudgetCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.YearMonth;
import java.util.Locale;

/**
 * Parses the arguments of the budget commands: {@code budget set} and {@code budget status}.
 */
public class BudgetCommandParser {

    private BudgetCommandParser() {
    }

    /**
     * Parses the arguments following the "budget" command word and returns the corresponding command.
     *
     * @param args Arguments following the "budget" command word.
     * @return Command to execute.
     * @throws TripJioException If the arguments or command syntax are invalid.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser argParser = new ArgumentParser(args);
        String preamble = argParser.getPreamble().trim();
        String action = preamble.toLowerCase(Locale.ROOT);

        switch (action) {
        case "set":
            return parseSet(argParser);
        case "status":
            return parseStatus(argParser);
        case "":
            throw new TripJioException("Please specify a budget command: 'budget set' or 'budget status'.");
        default:
            throw new TripJioException("Unknown budget command: " + preamble
                    + ". Use 'budget set' or 'budget status'.");
        }
    }

    private static Command parseSet(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("a", "m");
        double amount = ArgumentParser.parseAmount(argParser.getRequired("a"));
        String monthText = argParser.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        return new SetBudgetCommand(amount, month);
    }

    private static Command parseStatus(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("m");
        String monthText = argParser.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        return new BudgetStatusCommand(month);
    }
}
