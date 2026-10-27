package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.SplitCommand;
import seedu.tripjio.command.SplitListCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.ExchangeRates;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses input arguments and creates a new SplitCommand or SplitListCommand object.
 */
public class SplitCommandParser {

    private SplitCommandParser() {
    }

    public static Command parse(String args) throws TripJioException {
        ArgumentParser p = new ArgumentParser(args);

        if ("list".equals(p.getPreamble())) {
            p.checkAllowedPrefixes("m", "trip");
            String monthText = p.getOptional("m");
            YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);
            return new SplitListCommand(month, p.getFlag("trip"));
        }

        p.checkNoPreamble();
        p.checkAllowedPrefixes("d", "a", "n", "my", "c", "dt", "p", "paid", "cur", "rate");

        String description = ArgumentParser.parseDescription(p.getRequired("d"));
        String currencyText = p.getOptional("cur");
        String rateText = p.getOptional("rate");
        if ((currencyText == null) != (rateText == null)) {
            throw new TripJioException("Provide cur/ and rate/ together for a foreign split.");
        }
        String currency = currencyText == null ? null : ExchangeRates.currencyCode(currencyText);
        Double rate = rateText == null ? null : ExchangeRates.parseRate(rateText).doubleValue();
        if ("SGD".equals(currency) && rate != 1.0) {
            throw new TripJioException("The SGD rate must be 1.");
        }
        double amount = currency == null ? ArgumentParser.parseAmount(p.getRequired("a"))
                : ArgumentParser.parseOriginalAmount(p.getRequired("a"));
        int people = parseNumberOfPeople(p.getRequired("n"));

        Double myShare = null;
        if (p.has("my")) {
            myShare = ArgumentParser.parseAmount(p.getRequired("my"));
        }
        double sgdTotal = currency == null ? amount : ExchangeRates.toSgd(amount, rate);
        SplitExpense.validateShare(sgdTotal, people, myShare);

        Category category = Category.OTHER;
        if (p.has("c")) {
            category = Category.fromString(p.getRequired("c"));
        }

        LocalDate date = LocalDate.now();
        if (p.has("dt")) {
            date = DateUtil.parseDate(p.getRequired("dt"));
            if (date.isAfter(LocalDate.now())) {
                throw new TripJioException("Expense date cannot be in the future.");
            }
        }

        String namesStr = p.getOptional("p");
        List<String> participants = new ArrayList<>();
        if (namesStr != null && !namesStr.trim().isEmpty()) {
            for (String name : namesStr.split(",", -1)) {
                participants.add(SplitExpense.parsePerson(name));
            }
        }

        String payer = p.getOptional("paid");
        if (payer != null) {
            payer = SplitExpense.parsePerson(payer);
        }
        SplitExpense.validateParticipants(participants, payer, people);

        return new SplitCommand(description, amount, people, myShare, category, date, 
                                participants, payer, currency, rate);
    }

    private static int parseNumberOfPeople(String text) throws TripJioException {
        try {
            int people = Integer.parseInt(text.trim());
            if (people >= 2 && people <= 50) {
                return people;
            }
        } catch (NumberFormatException e) {
            // Fall through to throw exception
        }
        throw new TripJioException("Number of people must be between 2 and 50.");
    }
}
