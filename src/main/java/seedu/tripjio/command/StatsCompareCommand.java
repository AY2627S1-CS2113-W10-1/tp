package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.trip.Statistics;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;

/** Displays two months of category spending and their changes without modifying data. */
public class StatsCompareCommand extends Command {
    private final YearMonth firstMonth;
    private final YearMonth secondMonth;

    /**
     * Creates a comparison preserving the order supplied by the user.
     *
     * @param firstMonth Baseline month, not null.
     * @param secondMonth Comparison month, not null; may equal or precede the baseline.
     */
    public StatsCompareCommand(YearMonth firstMonth, YearMonth secondMonth) {
        this.firstMonth = Objects.requireNonNull(firstMonth, "First month must not be empty");
        this.secondMonth = Objects.requireNonNull(secondMonth, "Second month must not be empty  ");
    }

    @Override
    public void execute(AppState state, Ui ui) {
        Map<Category, Double> first = Statistics.getMonthlyTotals(state.getExpenses(), firstMonth);
        Map<Category, Double> second = Statistics.getMonthlyTotals(state.getExpenses(), secondMonth);
        ui.showMessage("Spending comparison: " + DateUtil.formatMonth(firstMonth)
                + " -> " + DateUtil.formatMonth(secondMonth),
                "Category: first month -> second month (change: second minus first)");
        double firstTotal = 0;
        double secondTotal = 0;
        for (Category category : Category.values()) {
            double firstAmount = first.get(category);
            double secondAmount = second.get(category);
            ui.showMessage(formatComparison(category.getDisplayName(), firstAmount, secondAmount));
            firstTotal += firstAmount;
            secondTotal += secondAmount;
        }
        ui.showMessage("==============================",
                formatComparison("TOTAL SPENT", MoneyUtil.roundToCents(firstTotal),
                        MoneyUtil.roundToCents(secondTotal)),
                "==============================");
        if (state.getExpenses().getExpensesInMonth(firstMonth).isEmpty()
                && state.getExpenses().getExpensesInMonth(secondMonth).isEmpty()) {
            ui.showMessage("No expenses recorded for either month. :(");
        }
    }

    /** Formats an ordered pair of amounts and its signed, cent-rounded dollar change. */
    private String formatComparison(String label, double first, double second) {
        double change = MoneyUtil.roundToCents(second - first);
        String sign = change > 0 ? "+" : change < 0 ? "-" : "";
        return label + ": " + MoneyUtil.format(first) + " -> " + MoneyUtil.format(second)
                + " (change: " + sign + MoneyUtil.format(Math.abs(change)) + ")";
    }
}
