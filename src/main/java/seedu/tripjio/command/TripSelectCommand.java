package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/** Changes the active trip used by trip set and trip status and persists the selection. */
public class TripSelectCommand extends Command {
    private final int index;

    public TripSelectCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        state.getTrips().select(index);
        ui.showMessage("Selected trip " + index + ".");
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
