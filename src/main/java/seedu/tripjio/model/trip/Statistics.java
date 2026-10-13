package seedu.tripjio.model.trip;

import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseList;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;
import java.util.EnumMap;
import java.util.Map;

/**
 * Calculates spending statistics for the {@code stats} and {@code stats compare} commands:
 * the spending of a month divided by category, and the change in each category between two months.
 */
public class Statistics {

    private Statistics() {
    }

    /**
     * Calculates a fresh set of category totals for the requested month without changing expenses.
     * Uses getAmount so split bills contribute only the user's share.
     *
     * @param expenses Source expense collection.
     * @param month Year and month to include.
     * @return Totals rounded to cents, including zero for unused categories, in enum order.
     */
    public static Map<Category, Double> getMonthlyTotals(ExpenseList expenses, YearMonth month) {
        Map<Category, Double> totals = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            totals.put(category, 0.0);
        }
        for (Expense expense : expenses.getExpensesInMonth(month)) {
            Category category = expense.getCategory();
            totals.put(category, totals.get(category) + expense.getAmount());
        }
        totals.replaceAll((category, amount) -> MoneyUtil.roundToCents(amount));
        return totals;
    }

}
