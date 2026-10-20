package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

import java.util.List;

/** Displays saved trips and the active selection without changing data. */
public class TripListCommand extends Command {
    @Override
    public void execute(AppState state, Ui ui) {
        List<String> lines = state.getTrips().displayLines();
        if (lines.isEmpty()) {
            ui.showMessage("No trips yet. Use: trip add n/NAME s/START_DATE e/END_DATE.");
        } else {
            ui.showMessage("Your trips:");
            ui.showMessage(lines.toArray(String[]::new));
        }
    }
}
