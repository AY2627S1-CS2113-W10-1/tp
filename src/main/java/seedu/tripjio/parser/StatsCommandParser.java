package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.StatsCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.YearMonth;

/** Validates the optional month parameter for monthly spending statistics. */
public class StatsCommandParser {
    private StatsCommandParser() {
    }

    /**
     * Parses stats with at most one m/ parameter and no other arguments.
     *
     * @param args Text after stats.
     * @return Read-only monthly statistics command.
     * @throws TripJioException If arguments or the month are invalid.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser arguments = new ArgumentParser(args);
        arguments.checkNoPreamble();
        for (String prefix : new String[] {"d", "a", "c", "dt", "s", "e", "n", "my"}) {
            if (arguments.has(prefix)) {
                throw new TripJioException("Unexpected parameter: " + prefix + "/. Use: stats [m/MM-YYYY].");
            }
        }
        String text = arguments.getOptional("m");
        YearMonth month = text == null ? null : DateUtil.parseMonth(text);
        return new StatsCommand(month);
    }
}
