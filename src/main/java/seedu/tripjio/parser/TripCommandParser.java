package seedu.tripjio.parser;

import seedu.tripjio.command.Command;
import seedu.tripjio.command.TripAddCommand;
import seedu.tripjio.command.TripBudgetCommand;
import seedu.tripjio.command.TripDeleteCommand;
import seedu.tripjio.command.TripEditCommand;
import seedu.tripjio.command.TripListCommand;
import seedu.tripjio.command.TripSelectCommand;
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
     * Parses trip creation, selection, editing, deletion, budget and status commands.
     *
     * @param args Text after the trip command word.
     * @return Command containing the parsed parameters.
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
        String[] words = arguments.getPreamble().split("\\s+", 2);
        if (words[0].equalsIgnoreCase("budget")) {
            if (words.length > 1) {
                throw new TripJioException("Unexpected argument: " + words[1] + ". Use: trip budget a/AMOUNT.");
            }
            arguments.checkAllowedPrefixes("a");
            double amount = ArgumentParser.parseAmount(arguments.getRequired("a"));
            return new TripBudgetCommand(amount);
        }
        if (words[0].equalsIgnoreCase("edit") || words[0].equalsIgnoreCase("delete")) {
            boolean editing = words[0].equalsIgnoreCase("edit");
            arguments.checkAllowedPrefixes(editing ? new String[] {"n", "s", "e"} : new String[0]);
            if (words.length != 2) {
                throw new TripJioException("A trip index is required. Use trip list to see available trips.");
            }
            int index = ArgumentParser.parseIndex(words[1]);
            if (!editing) {
                return new TripDeleteCommand(index);
            }
            String name = arguments.getOptional("n");
            String start = arguments.getOptional("s");
            String end = arguments.getOptional("e");
            if (name == null && start == null && end == null) {
                throw new TripJioException("Give at least one trip field to edit: n/, s/ or e/.");
            }
            return new TripEditCommand(index, name == null ? null : ArgumentParser.parseDescription(name),
                    start == null ? null : DateUtil.parseDate(start), end == null ? null : DateUtil.parseDate(end));
        }
        if (arguments.getPreamble().equalsIgnoreCase("add")) {
            arguments.checkAllowedPrefixes("n", "s", "e");
            String name = ArgumentParser.parseDescription(arguments.getRequired("n"));
            return new TripAddCommand(name, DateUtil.parseDate(arguments.getRequired("s")),
                    DateUtil.parseDate(arguments.getRequired("e")));
        }
        arguments.checkAllowedPrefixes("s", "e");
        if (!arguments.getPreamble().equalsIgnoreCase("set")) {
            throw new TripJioException("Use trip add n/NAME s/DATE e/DATE, trip list, trip select INDEX, "
                    + "trip edit INDEX [n/NAME] [s/DATE] [e/DATE], trip delete INDEX, "
                    + "trip budget a/AMOUNT, trip set s/DATE e/DATE, or trip status. Dates: DD-MM-YYYY.");
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
