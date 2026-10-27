package seedu.tripjio.model;

import seedu.tripjio.model.budget.Budget;
import seedu.tripjio.model.expense.ExpenseList;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.model.trip.TripBook;
import seedu.tripjio.model.expense.ExchangeRates;
import seedu.tripjio.model.split.Settlement;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all of the user's data (expenses, budgets and the current trip) so it can be passed to commands
 * and to storage as one object.
 */
public class AppState {

    private final ExpenseList expenses;
    private final Budget budget;
    private final TripBook trips;
    private final ExchangeRates exchangeRates = new ExchangeRates();
    private final List<Settlement> settlements = new ArrayList<>();

    public AppState() {
        this.expenses = new ExpenseList();
        this.budget = new Budget();
        this.trips = new TripBook();
    }

    public AppState(ExpenseList expenses, Budget budget, Trip trip) {
        this.expenses = expenses;
        this.budget = budget;
        this.trips = new TripBook();
        this.trips.importOriginal(trip);
    }

    public ExpenseList getExpenses() {
        return expenses;
    }

    public Budget getBudget() {
        return budget;
    }

    /** Returns the selected trip, or null if none is selected; callers must check hasCurrent first. */
    public Trip getTrip() {
        return trips.getCurrent();
    }

    public TripBook getTrips() {
        return trips;
    }

    public ExchangeRates getExchangeRates() {
        return exchangeRates;
    }

    public List<Settlement> getSettlements() {
        return settlements;
    }
}
