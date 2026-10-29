package seedu.tripjio.model.budget;

import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;

/**
 * Checks spending against monthly and trip budgets and produces the budget warnings described in the User Guide.
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

    /**
     * Returns the warning to show after spending for a trip changes, or null if no warning is needed.
     *
     * @param trip Selected trip.
     * @param tripName Name of the selected trip.
     * @param spent Total amount spent within the trip's date range.
     * @return Warning message, or null if the trip has no budget or less than 80% is used.
     */
    public static String checkTrip(Trip trip, String tripName, double spent) {
        if (trip == null || !trip.hasBudget()) {
            return null;
        }
        return checkTrip(trip.getBudget(), tripName, spent);
    }

    /**
     * Returns the warning to show after spending for a trip changes, or null if no warning is needed.
     *
     * @param budgetAmount Budget amount of the trip.
     * @param tripName Name of the trip.
     * @param spent Total amount spent within the trip's date range.
     * @return Warning message, or null if the budget is null or less than 80% is used.
     */
    public static String checkTrip(Double budgetAmount, String tripName, double spent) {
        if (budgetAmount == null) {
            return null;
        }

        String target = (tripName == null || tripName.isBlank()) ? "your trip" : "trip " + tripName;

        if (spent > budgetAmount) {
            double overAmount = spent - budgetAmount;
            return "Warning: You have exceeded your budget for " + target + "! "
                    + "Spent: " + MoneyUtil.format(spent) + " / " + MoneyUtil.format(budgetAmount)
                    + " (" + MoneyUtil.format(overAmount) + " over budget).";
        }

        double threshold = MoneyUtil.roundToCents(budgetAmount * WARNING_THRESHOLD_RATIO);
        if (spent >= threshold) {
            double remaining = budgetAmount - spent;
            return "Warning: You have used 80% or more of your budget for " + target + "! "
                    + "Spent: " + MoneyUtil.format(spent) + " / " + MoneyUtil.format(budgetAmount)
                    + " (" + MoneyUtil.format(remaining) + " remaining).";
        }

        return null;
    }
}
