package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/** Displays the current trip and its recorded spending without changing or saving data. */
public class TripStatusCommand extends Command {
    private final Clock clock;

    /** Creates a status command using the computer's local date. */
    public TripStatusCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a command with a clock so date boundaries can be tested reliably.
     *
     * @param clock Source of the current date; must not be null.
     */
    public TripStatusCommand(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        Trip trip = state.getTrip();
        LocalDate today = LocalDate.now(clock);
        // Validate the trip before printing anything or accessing its dates for spending calculations.
        String status = trip.getStatus(today);
        double spent = state.getExpenses().getTotalSpentBetween(trip.getStart(), trip.getEnd());
        ui.showMessage("Trip: " + DateUtil.formatDate(trip.getStart()) + " to " + DateUtil.formatDate(trip.getEnd()),
                "Status: " + status,
                "Duration: " + trip.getDurationDays() + " day(s)",
                "Remaining trip days (including today): " + trip.getRemainingDays(today),
                "Recorded spending during trip: " + MoneyUtil.format(spent));
    }
}
