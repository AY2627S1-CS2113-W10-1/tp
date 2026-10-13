package seedu.tripjio.model.budget;

import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;

/**
 * Checks a month's spending against its budget and produces the budget warnings described in the User Guide.
 * It returns the warning instead of printing it, so the model does not depend on the UI.
 */
public class BudgetChecker {
    /** Spending ratio (80%) that triggers the warning threshold. */
    public static final double WARNING_THRESHOLD_RATIO = 0.8;

    private BudgetChecker() {
    }

    /**
     * Returns the warning to show after the spending of a month changes, or null if no warning is needed.
     *
     * @param budget Budget of the user.
     * @param spent Total amount spent in the month.
     * @param month Month to check.
     * @return Warning message, or null if the month has no budget or less than 80% of it is used.
     */
    public static String check(Budget budget, double spent, YearMonth month) {
        if (budget == null || month == null) {
            return null;
        }

        Double budgetAmount = budget.getBudgetFor(month);
        if (budgetAmount == null) {
            return null;
        }

        if (spent > budgetAmount) {
            double overAmount = spent - budgetAmount;
            return "Warning: You have exceeded your budget for " + DateUtil.formatMonth(month) + "! "
                    + "Spent: " + MoneyUtil.format(spent) + " / " + MoneyUtil.format(budgetAmount)
                    + " (" + MoneyUtil.format(overAmount) + " over budget).";
        }

        double threshold = MoneyUtil.roundToCents(budgetAmount * WARNING_THRESHOLD_RATIO);
        if (spent >= threshold) {
            double remaining = budgetAmount - spent;
            return "Warning: You have used 80% or more of your budget for " + DateUtil.formatMonth(month) + "! "
                    + "Spent: " + MoneyUtil.format(spent) + " / " + MoneyUtil.format(budgetAmount)
                    + " (" + MoneyUtil.format(remaining) + " remaining).";
        }

        return null;
    }
}
