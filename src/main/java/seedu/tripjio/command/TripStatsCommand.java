package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseFilter;
import seedu.tripjio.model.trip.Statistics;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/** Reports category spending across the selected trip, optionally intersected with one month. */
public class TripStatsCommand extends Command {
    private final YearMonth month;

    public TripStatsCommand(YearMonth month) {
        this.month = month;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        List<Expense> expenses = ExpenseFilter.select(state, null, month, true);
        Map<Category, Double> totals = Statistics.getCategoryTotals(expenses);
        ui.showMessage("Spending for trip " + state.getTrips().getCurrentName()
                + (month == null ? "" : " in " + DateUtil.formatMonth(month)) + ":");
        double total = 0;
        for (Category category : Category.values()) {
            double amount = totals.get(category);
            ui.showMessage(category.getDisplayName() + ": " + MoneyUtil.format(amount));
            total += amount;
        }
        ui.showMessage("==============================",
                "TOTAL SPENT: " + MoneyUtil.format(MoneyUtil.roundToCents(total)),
                "==============================");
        if (expenses.isEmpty()) {
            ui.showMessage("No expenses match the selected trip filter.");
        }
    }
}
