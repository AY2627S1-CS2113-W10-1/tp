package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;

/** Edits a named trip without changing which trip is selected or modifying expenses. */
public class TripEditCommand extends Command {
    private final int index;
    private final String name;
    private final LocalDate start;
    private final LocalDate end;

    /** Captures the target index and optional replacement fields; null fields remain unchanged. */
    public TripEditCommand(int index, String name, LocalDate start, LocalDate end) {
        this.index = index;
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        state.getTrips().edit(index, name, start, end);
        ui.showMessage("Updated trip " + index + ".");
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
