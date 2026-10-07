package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/**
 * Shows the format of every command, as listed in the Command Summary of the User Guide.
 */
public class HelpCommand extends Command {
    private static final String[] COMMANDS = {
        "help",
        "add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]",
        "list [c/CATEGORY] [m/MONTH]",
        "edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]",
        "delete INDEX",
        "split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE "
                + "[my/MY_SHARE] [c/CATEGORY] [dt/DATE]",
        "split list [m/MONTH]",
        "budget set a/AMOUNT [m/MONTH]",
        "budget status [m/MONTH]",
        "trip set s/START_DATE e/END_DATE",
        "trip status",
        "stats [m/MONTH]",
        "stats compare m/MONTH m/MONTH",
        "exit"
    };

    @Override
    public void execute(AppState state, Ui ui) {
        ui.showMessage("Here are the commands you can use:");
        ui.showMessage(COMMANDS);
    }

}
