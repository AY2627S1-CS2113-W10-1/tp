package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.SplitCommand;
import seedu.tripjio.command.SplitListCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;

public class SplitCommandParser {

    public static Command parse(String args) throws TripJioException {
        ArgumentParser p = new ArgumentParser(args);
        
        if ("list".equals(p.getPreamble())) {
            return new SplitListCommand();
        }

        p.checkNoPreamble();

        String description = ArgumentParser.parseDescription(p.getRequired("d"));
        double amount = ArgumentParser.parseAmount(p.getRequired("a"));
        int people = ArgumentParser.parseIndex(p.getRequired("n"));

        if (people < 2 || people > 50) {
            throw new TripJioException("Number of people must be between 2 and 50.");
        }

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

        return new SplitCommand(description, amount, people, myShare, category, date);
    }
}