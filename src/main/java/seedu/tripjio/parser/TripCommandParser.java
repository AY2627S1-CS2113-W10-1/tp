package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.TripSetCommand;
import seedu.tripjio.command.TripStatusCommand;
import seedu.tripjio.command.TripAddCommand;
import seedu.tripjio.command.TripListCommand;
import seedu.tripjio.command.TripSelectCommand;
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
        if (args.trim().equalsIgnoreCase("list")) {
            return new TripListCommand();
        }
        if (args.trim().matches("(?i)select\\s+.*")) {
            return new TripSelectCommand(ArgumentParser.parseIndex(args.trim().split("\\s+", 2)[1]));
        }
        if (args.trim().equalsIgnoreCase("status")) {
            return new TripStatusCommand();
        }
        if (args.trim().matches("(?i)status\\s+.*")) {
            throw new TripJioException("Trip status does not accept parameters. Use: trip status.");
        }
        ArgumentParser arguments = new ArgumentParser(args);
        if (arguments.getPreamble().equalsIgnoreCase("add")) {
            arguments.checkAllowedPrefixes("n", "s", "e");
            String name = ArgumentParser.parseDescription(arguments.getRequired("n"));
            return new TripAddCommand(name, DateUtil.parseDate(arguments.getRequired("s")),
                    DateUtil.parseDate(arguments.getRequired("e")));
        }
        arguments.checkAllowedPrefixes("s", "e");
        if (!arguments.getPreamble().equalsIgnoreCase("set")) {
            throw new TripJioException("Use trip add n/NAME s/DATE e/DATE, trip list, trip select INDEX, "
                    + "trip set s/DATE e/DATE, or trip status. Dates: DD-MM-YYYY.");
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
