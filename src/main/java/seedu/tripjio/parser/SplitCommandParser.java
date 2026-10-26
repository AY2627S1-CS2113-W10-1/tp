package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.SplitCommand;
import seedu.tripjio.command.SplitListCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

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
        double amount = ArgumentParser.parseAmount(p.getRequired("a"));
        int people = parseNumberOfPeople(p.getRequired("n"));

        Double myShare = null;
        if (p.has("my")) {
            myShare = ArgumentParser.parseAmount(p.getRequired("my"));
            if (myShare > amount) {
                throw new TripJioException("Your custom share cannot be more than the total bill.");
            }
        } else {
            double equalShare = MoneyUtil.roundToCents(amount / people);
            if (equalShare < 0.01) {
                throw new TripJioException("Each person's share must be at least $0.01.");
            }
        }

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
            for (String name : namesStr.split(",")) {
                participants.add(name.trim());
            }
        }

        String payer = p.getOptional("paid");
        if (payer != null) {
            payer = payer.trim();
        }

        String currency = p.getOptional("cur");
        if (currency != null) {
            currency = currency.trim().toUpperCase();
        }

        Double rate = null;
        if (p.has("rate")) {
            try {
                rate = Double.parseDouble(p.getRequired("rate"));
                if (rate <= 0) {
                    throw new TripJioException("Exchange rate must be strictly positive.");
                }
            } catch (NumberFormatException e) {
                throw new TripJioException("Invalid exchange rate format.");
            }
        }

        if (currency != null && rate == null) {
            throw new TripJioException("You must provide an exchange rate (rate/) " 
                    + "when specifying a foreign currency (cur/).");
        }

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
