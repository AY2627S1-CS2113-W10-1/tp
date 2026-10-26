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
    /** Four 15-character columns with one space between them, so even very large totals never touch. */
    private static final String ROW_FORMAT = "%-15s %15s %15s %15s";
    /** As wide as one table row: 4 columns x 15 characters + 3 spaces. */
    private static final String TABLE_LINE = "-".repeat(63);

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
                String.format(ROW_FORMAT, "Category", DateUtil.formatShortMonth(firstMonth),
                        DateUtil.formatShortMonth(secondMonth), "Change"), TABLE_LINE);
        double firstTotal = 0;
        double secondTotal = 0;

        for (Category category : Category.values()) {
            double firstAmount = first.get(category);
            double secondAmount = second.get(category);
            ui.showMessage(formatRow(category.getDisplayName(), firstAmount, secondAmount));
            firstTotal += firstAmount;
            secondTotal += secondAmount;
        }
        ui.showMessage(TABLE_LINE,
                formatRow("Total", MoneyUtil.roundToCents(firstTotal),
                        MoneyUtil.roundToCents(secondTotal)));

        if (state.getExpenses().getExpensesInMonth(firstMonth).isEmpty()
                && state.getExpenses().getExpensesInMonth(secondMonth).isEmpty()) {
            ui.showMessage("No expenses recorded for either month. :(");
        }
    }

    private String formatRow(String label, double first, double second) {
        double change = MoneyUtil.roundToCents(second - first);
        String sign = change > 0 ? "+" : change < 0 ? "-" : "";
        String changeText = sign + MoneyUtil.format(Math.abs(change));

        return String.format(ROW_FORMAT, label, MoneyUtil.format(first), MoneyUtil.format(second), changeText);
    }

}
