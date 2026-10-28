package seedu.tripjio.parser;

import seedu.tripjio.command.AddCommand;
import seedu.tripjio.command.Command;
import seedu.tripjio.command.DeleteCommand;
import seedu.tripjio.command.EditCommand;
import seedu.tripjio.command.ListCommand;
import seedu.tripjio.command.AddForeignExpenseCommand;
import seedu.tripjio.command.OweCommand;
import seedu.tripjio.command.SettleCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Parses the arguments of the expense commands.
 * All checks that only need the input text are done here, so the commands receive values that are already valid.
 */
public class ExpenseCommandParser {

    private ExpenseCommandParser() {
    }

    /**
     * Returns the expense command described by the given command word and arguments.
     *
     * @param commandWord One of "add", "list", "edit", "delete", "owe", or "settle", in lower case.
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
        case "owe":
            return parseOwe(argParser);
        case "settle":
            return parseSettle(argParser);
        default:
            throw new AssertionError("Unexpected command word: " + commandWord);
        }
    }

    private static Command parseAdd(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("d", "a", "c", "dt", "cur");
        argParser.checkNoPreamble();

        String description = ArgumentParser.parseDescription(argParser.getRequired("d"));
        String currency = argParser.getOptional("cur");
        double amount = currency == null ? ArgumentParser.parseAmount(argParser.getRequired("a"))
                : ArgumentParser.parseOriginalAmount(argParser.getRequired("a"));

        String categoryText = argParser.getOptional("c");
        Category category = (categoryText == null) ? Category.OTHER : Category.fromString(categoryText);

        String dateText = argParser.getOptional("dt");
        LocalDate date = (dateText == null) ? LocalDate.now() : parseDateNotInFuture(dateText);

        return currency == null ? new AddCommand(description, amount, category, date)
                : new AddForeignExpenseCommand(description, amount, category, date, currency);
    }

    private static Command parseList(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("c", "m", "sort", "order", "trip");
        argParser.checkNoPreamble();

        String categoryText = argParser.getOptional("c");
        Category category = (categoryText == null) ? null : Category.fromString(categoryText);

        String monthText = argParser.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        String sort = argParser.getOptional("sort");
        String order = argParser.getOptional("order");
        if (sort != null && !sort.equals("amount") && !sort.equals("date")) {
            throw new TripJioException("Use sort/amount or sort/date.");
        }
        if (order != null && (sort == null || (!order.equals("asc") && !order.equals("desc")))) {
            throw new TripJioException("Use order/asc or order/desc together with sort/amount or sort/date.");
        }
        return new ListCommand(category, month, sort, order == null || order.equals("desc"),
                argParser.getFlag("trip"));
    }

    private static Command parseEdit(ArgumentParser argParser) throws TripJioException {
        // ADDED "my" to allowed prefixes
        argParser.checkAllowedPrefixes("d", "a", "c", "dt", "my");
        int index = ArgumentParser.parseIndex(argParser.getPreamble());

        String descriptionText = argParser.getOptional("d");
        String amountText = argParser.getOptional("a");
        String categoryText = argParser.getOptional("c");
        String dateText = argParser.getOptional("dt");
        String myText = argParser.getOptional("my");

        if (descriptionText == null && amountText == null && categoryText == null 
                && dateText == null && myText == null) {
            throw new TripJioException("Please give at least one field to edit: d/, a/, c/, dt/ or my/.");
        }

        String description = (descriptionText == null) ? null : ArgumentParser.parseDescription(descriptionText);
        Double amount = (amountText == null) ? null : ArgumentParser.parseOriginalAmount(amountText);
        Category category = (categoryText == null) ? null : Category.fromString(categoryText);
        LocalDate date = (dateText == null) ? null : parseDateNotInFuture(dateText);
        Double customShare = (myText == null) ? null : ArgumentParser.parseAmount(myText);

        // Updated constructor to pass customShare
        return new EditCommand(index, description, amount, category, date, customShare);
    }

    private static Command parseDelete(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes();
        return new DeleteCommand(ArgumentParser.parseIndex(argParser.getPreamble()));
    }

    private static Command parseOwe(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("p");
        argParser.checkNoPreamble();
        String person = argParser.getOptional("p");
        return new OweCommand(person == null ? null : SplitExpense.parsePerson(person));
    }

    private static Command parseSettle(ArgumentParser argParser) throws TripJioException {
        argParser.checkAllowedPrefixes("p", "a");
        argParser.checkNoPreamble();
        
        String person = SplitExpense.parsePerson(argParser.getRequired("p"));
        String amountText = argParser.getOptional("a");
        Double amount = (amountText == null) ? null : ArgumentParser.parseAmount(amountText);
        
        return new SettleCommand(person, amount);
    }

    private static LocalDate parseDateNotInFuture(String text) throws TripJioException {
        LocalDate date = DateUtil.parseDate(text);
        if (date.isAfter(LocalDate.now())) {
            throw new TripJioException("Date cannot be in the future: " + text);
        }
        return date;
    }
}
