package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.TripSetCommand;
import seedu.tripjio.command.TripStatusCommand;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;

/** Parses commands that configure or display the user's current trip. */
public class TripCommandParser {
    private TripCommandParser() {
    }

    /**
     * Parses trip set with two required dates, or trip status without arguments.
     *
     * @param args Text after the trip command word.
     * @return Command containing the parsed dates.
     * @throws TripJioException If the subcommand, parameters or dates are invalid.
     */
    public static Command parse(String args) throws TripJioException {
        if (args.trim().equals("status")) {
            return new TripStatusCommand();
        }
        if (args.trim().matches("status\\s+.*")) {
            throw new TripJioException("Trip status does not accept parameters. Use: trip status.");
        }
        ArgumentParser arguments = new ArgumentParser(args);
        if (!arguments.getPreamble().equals("set")) {
            throw new TripJioException("Use: trip set s/START_DATE e/END_DATE (DD-MM-YYYY), or trip status.");
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
