package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.trip.Trip;

import java.time.YearMonth;
import java.util.List;

/** Shared read-only date, month and category filtering for reports and future export commands. */
public class ExpenseFilter {
    private ExpenseFilter() {
    }

    /** Intersects the supplied filters, resolving the selected trip at execution time. */
    public static List<Expense> select(AppState state, Category category, YearMonth month, boolean tripOnly)
            throws TripJioException {
        List<Expense> result;
        if (tripOnly) {
            if (!state.getTrips().hasCurrent()) {
                throw new TripJioException("No trip selected. Use trip list, then trip select INDEX; "
                        + "if no trips exist, use trip add n/NAME s/START_DATE e/END_DATE.");
            }
            Trip trip = state.getTrips().getCurrent();
            result = state.getExpenses().getExpensesBetween(trip.getStart(), trip.getEnd());
        } else {
            result = state.getExpenses().getAll();
        }
        result.removeIf(expense -> (category != null && expense.getCategory() != category)
                || (month != null && !expense.getMonth().equals(month)));
        return result;
    }
}
