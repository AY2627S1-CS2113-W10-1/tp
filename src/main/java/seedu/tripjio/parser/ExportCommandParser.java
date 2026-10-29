package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.ExportCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.util.DateUtil;

import java.time.YearMonth;

/** Parses the arguments of {@code export}, which accepts the same filters as {@code list} but no sorting. */
public class ExportCommandParser {
    private ExportCommandParser() {
    }

    /**
     * Returns an export command for the given filters.
     *
     * @param args Text after "export".
     * @return Command that writes the matching expenses to data/export.csv.
     * @throws TripJioException If a prefix is not allowed, repeated or invalid.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser arguments = new ArgumentParser(args);

        arguments.checkAllowedPrefixes("c", "m", "trip");
        arguments.checkNoPreamble();

        String categoryText = arguments.getOptional("c");
        Category category = (categoryText == null) ? null : Category.fromString(categoryText);

        String monthText = arguments.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        boolean tripOnly = arguments.getFlag("trip");

        return new ExportCommand(category, month, tripOnly);
    }
}
