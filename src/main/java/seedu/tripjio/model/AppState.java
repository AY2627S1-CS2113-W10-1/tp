package seedu.tripjio.model;

import seedu.tripjio.model.budget.Budget;
import seedu.tripjio.model.expense.ExpenseList;
import seedu.tripjio.model.trip.Trip;

/**
 * Holds all of the user's data (expenses, budgets and the current trip) so it can be passed to commands
 * and to storage as one object. There are no setters: to change the data, call the methods of the
 * object returned by a getter, e.g. {@code state.getBudget().setDefault(1000)}.
 */
public class AppState {

    private final ExpenseList expenses;
    private final Budget budget;
    private final Trip trip;

    public AppState() {
        this.expenses = new ExpenseList();
        this.budget = new Budget();
        this.trip = new Trip();
    }

    public AppState(ExpenseList expenses, Budget budget, Trip trip) {
        this.expenses = expenses;
        this.budget = budget;
        this.trip = trip;
    }

    public ExpenseList getExpenses() {
        return expenses;
    }

    public Budget getBudget() {
        return budget;
    }

    public Trip getTrip() {
        return trip;
    }
}
