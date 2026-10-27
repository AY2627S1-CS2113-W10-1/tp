package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.MoneyUtil;

/**
 * Sets an overall budget for the currently selected trip.
 */
public class TripBudgetCommand extends Command {
    private final double amount;

    /**
     * Creates a command to set the budget of the currently selected trip.
     *
     * @param amount Budget amount for the trip.
     */
    public TripBudgetCommand(double amount) {
        this.amount = amount;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        if (!state.getTrips().hasCurrent()) {
            throw new TripJioException("No trip selected. Use trip list, then trip select INDEX; "
                    + "if no trips exist, use trip add n/NAME s/START_DATE e/END_DATE.");
        }
        Trip trip = state.getTrips().getCurrent();
        trip.setBudget(amount);
        ui.showMessage("Budget for trip " + state.getTrips().getCurrentName() + " set to "
                + MoneyUtil.format(amount) + ".");
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    public double getAmount() {
        return amount;
    }
}
