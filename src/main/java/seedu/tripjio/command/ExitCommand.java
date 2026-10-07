package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/**
 * Shows the goodbye message and ends the application.
 * It is marked as mutating so that the main loop saves the data one last time before exiting.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(AppState state, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
