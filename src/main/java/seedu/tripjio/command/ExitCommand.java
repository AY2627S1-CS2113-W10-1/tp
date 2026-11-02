package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/**
 * Requests application exit; the main loop prints goodbye only after saving successfully.
 * It is marked as mutating so that the main loop saves the data one last time before exiting.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(AppState state, Ui ui) {
        // Saving and the success message belong to the main loop, which owns persistence.
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
