package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;

import java.time.LocalDate;

/** Sets the current trip's dates; successful changes are saved by the main loop. */
public class TripSetCommand extends Command {
    private final LocalDate start;
    private final LocalDate end;

    /**
     * Creates a command with the requested dates. The trip model validates their order on execution.
     *
     * @param start First day of the trip.
     * @param end Last day of the trip, inclusive.
     */
    public TripSetCommand(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        state.getTrips().setCurrentDates(start, end);
        ui.showMessage("Trip set: " + DateUtil.formatDate(start) + " to " + DateUtil.formatDate(end) + ".");
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
