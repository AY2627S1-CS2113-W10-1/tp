package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;

/** Adds a named trip without replacing other trips and makes it the active trip. */
public class TripAddCommand extends Command {
    private final String name;
    private final LocalDate start;
    private final LocalDate end;

    public TripAddCommand(String name, LocalDate start, LocalDate end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        state.getTrips().add(name, start, end);
        ui.showMessage("Added and selected trip: " + name + ".");
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
