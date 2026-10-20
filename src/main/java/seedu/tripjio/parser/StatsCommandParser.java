package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.StatsCommand;
import seedu.tripjio.command.StatsCompareCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.YearMonth;
import java.util.List;

/** Validates month parameters for monthly reports and comparisons. */
public class StatsCommandParser {
    private StatsCommandParser() {
    }

    /**
     * Parses a monthly report or a comparison with exactly two ordered m/ parameters.
     *
     * @param args Text after stats.
     * @return Read-only monthly statistics command.
     * @throws TripJioException If arguments or the month are invalid.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser arguments = new ArgumentParser(args);
        arguments.checkAllowedPrefixes("m");
        boolean comparison = arguments.getPreamble().equalsIgnoreCase("compare");
        if (!comparison) {
            arguments.checkNoPreamble();
        }
        for (String prefix : new String[] {"d", "a", "c", "dt", "s", "e", "n", "my"}) {
            if (arguments.has(prefix)) {
                throw new TripJioException("Unexpected parameter: " + prefix + "/. HEy, only m/ is allowed!");
            }
        }
        if (comparison) {
            List<String> months = arguments.getAll("m");
            if (months.size() != 2) {
                throw new TripJioException("Use: stats compare m/MM-YYYY m/MM-YYYY. "
                        + "Only exactly two months can be compared");
            }
            return new StatsCompareCommand(DateUtil.parseMonth(months.get(0)), DateUtil.parseMonth(months.get(1)));
        }
        String text = arguments.getOptional("m");
        YearMonth month = text == null ? null : DateUtil.parseMonth(text);
        return new StatsCommand(month);
    }
}
