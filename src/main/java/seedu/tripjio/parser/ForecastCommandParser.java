package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.ForecastCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.YearMonth;

/**
 * Parses arguments for the {@code forecast} command.
 */
public class ForecastCommandParser {

    private ForecastCommandParser() {
    }

    /**
     * Parses the arguments for forecasting monthly spending.
     *
     * @param args Arguments following the "forecast" command word, e.g. {@code "m/10-2026"}.
     * @return Command to forecast spending.
     * @throws TripJioException If unexpected parameters or invalid month formats are given.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.checkNoPreamble();
        parser.checkAllowedPrefixes("m");

        String monthText = parser.getOptional("m");
        YearMonth month = (monthText == null) ? null : DateUtil.parseMonth(monthText);

        return new ForecastCommand(month);
    }
}
