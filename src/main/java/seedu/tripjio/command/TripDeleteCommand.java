package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/** Deletes a trip without deleting expenses and reports the resulting selection. */
public class TripDeleteCommand extends Command {
    private final int index;

    public TripDeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        String name = state.getTrips().delete(index);
        ui.showMessage("Deleted trip: " + name + ". Expenses are unchanged.");
        if (state.getTrips().hasCurrent()) {
            ui.showMessage("Selected trip: " + state.getTrips().getCurrentName() + ".");
        } else {
            ui.showMessage("No trip selected. Use trip list and trip select INDEX, or add a new trip.");
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
