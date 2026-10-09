package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.TripSetCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;

/** Parses commands that configure the user's current trip. */
public class TripCommandParser {
    private TripCommandParser() {
    }

    /**
     * Parses trip set with exactly one start date and one end date.
     *
     * @param args Text after the trip command word.
     * @return Command containing the parsed dates.
     * @throws TripJioException If the subcommand, parameters or dates are invalid.
     */
    public static Command parse(String args) throws TripJioException {
        ArgumentParser arguments = new ArgumentParser(args);
        if (!arguments.getPreamble().equals("set")) {
            throw new TripJioException("Use: trip set s/START_DATE e/END_DATE (DD-MM-YYYY).");
        }
        for (String prefix : new String[] {"d", "a", "c", "dt", "m", "n", "my"}) {
            if (arguments.has(prefix)) {
                throw new TripJioException("Unexpected parameter: " + prefix + "/. Trip set accepts only s/ and e/.");
            }
        }
        LocalDate start = DateUtil.parseDate(arguments.getRequired("s"));
        LocalDate end = DateUtil.parseDate(arguments.getRequired("e"));
        return new TripSetCommand(start, end);
    }
}
