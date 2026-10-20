package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.RateCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.ExchangeRates;

/** Parses manual rate updates and rejects unsupported conversion destinations. */
public class RateCommandParser {
    private RateCommandParser() {
    }

    /** Parses rate list or rate set from/CURRENCY to/SGD r/RATE. */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser p = new ArgumentParser(args);
        if (p.getPreamble().equalsIgnoreCase("list")) {
            p.checkAllowedPrefixes();
            return new RateCommand(null, null);
        }
        if (!p.getPreamble().equalsIgnoreCase("set")) {
            throw new TripJioException("Use: rate list, or rate set from/EUR to/SGD r/1.45.");
        }
        p.checkAllowedPrefixes("from", "to", "r");
        String currency = ExchangeRates.currencyCode(p.getRequired("from"));
        if (!ExchangeRates.currencyCode(p.getRequired("to")).equals("SGD")) {
            throw new TripJioException("Reporting currency is fixed at SGD. Use to/SGD.");
        }
        String rate = ExchangeRates.parseRate(p.getRequired("r")).toPlainString();
        return new RateCommand(currency, rate);
    }
}
