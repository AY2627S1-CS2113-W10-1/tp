package seedu.tripjio.parser;

import seedu.tripjio.command.AddCommand;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.DeleteCommand;
import seedu.tripjio.command.EditCommand;
import seedu.tripjio.command.ListCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Parses the arguments of the expense commands: {@code add}, {@code list}, {@code edit} and {@code delete}.
 * All checks that only need the input text are done here, so the commands receive values that are already valid.
 */
public class ExpenseCommandParser {

    private ExpenseCommandParser() {
    }

    /**
     * Returns the expense command described by the given command word and arguments.
     *
     * @param commandWord One of "add", "list", "edit" or "delete", in lower case.
     * @param args Arguments after the command word.
     * @return Command to run.
     * @throws TripJioException If the arguments are invalid.
     */
    public static Command parse(String commandWord, String args) throws TripJioException {
        ArgumentParser argParser = new ArgumentParser(args);
        switch (commandWord) {
        case "add":
            return parseAdd(argParser);
        case "list":
            return parseList(argParser);
        case "edit":
            return parseEdit(argParser);
        case "delete":
            return parseDelete(argParser);
        default:
            // Parser only sends the four words above, so reaching here is a programming error.
            throw new AssertionError("Unexpected command word: " + commandWord);
        }
    }

    private static Command parseAdd(ArgumentParser argParser) throws TripJioException {
        argParser.checkNoPreamble();

        String description = ArgumentParser.parseDescription(argParser.getRequired("d"));
        double amount = ArgumentParser.parseAmount(argParser.getRequired("a"));

        String categoryText = argParser.getOptional("c");
        Category category = (categoryText == null) ? Category.OTHER : Category.fromString(categoryText);

        String dateText = argParser.getOptional("dt");
        LocalDate date = (dateText == null) ? LocalDate.now() : parseDateNotInFuture(dateText);

        return new AddCommand(description, amount, category, date);
    }

    private static Command parseList(ArgumentParser argParser) throws TripJioException {
        argParser.checkNoPreamble();

        // null means "do not filter by this field".
        String categoryText = argParser.getOptional("c");
        Category category = (categoryText == null) ? null : Category.fromString(categoryText);

        String monthText = argParser.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        return new ListCommand(category, month);
    }

    private static Command parseEdit(ArgumentParser argParser) throws TripJioException {
        int index = ArgumentParser.parseIndex(argParser.getPreamble());

        // Each field is null when it is not given, meaning "keep the current value".
        String descriptionText = argParser.getOptional("d");
        String amountText = argParser.getOptional("a");
        String categoryText = argParser.getOptional("c");
        String dateText = argParser.getOptional("dt");

        if (descriptionText == null && amountText == null && categoryText == null && dateText == null) {
            throw new TripJioException("Please give at least one field to edit: d/, a/, c/ or dt/.");
        }

        String description = (descriptionText == null) ? null : ArgumentParser.parseDescription(descriptionText);
        Double amount = (amountText == null) ? null : ArgumentParser.parseAmount(amountText);
        Category category = (categoryText == null) ? null : Category.fromString(categoryText);
        LocalDate date = (dateText == null) ? null : parseDateNotInFuture(dateText);

        return new EditCommand(index, description, amount, category, date);
    }

    private static Command parseDelete(ArgumentParser argParser) throws TripJioException {
        return new DeleteCommand(ArgumentParser.parseIndex(argParser.getPreamble()));
    }

    /**
     * Parses a date and checks that it is not after today, as required by the User Guide.
     *
     * @param text Date entered by the user, in the format DD-MM-YYYY.
     * @return The parsed date.
     * @throws TripJioException If the date is invalid or in the future.
     */
    private static LocalDate parseDateNotInFuture(String text) throws TripJioException {
        LocalDate date = DateUtil.parseDate(text);
        if (date.isAfter(LocalDate.now())) {
            throw new TripJioException("Date cannot be in the future: " + text);
        }
        return date;
    }
}
