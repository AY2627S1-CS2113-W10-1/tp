package seedu.tripjio.model.budget;

import java.time.YearMonth;

/**
 * Checks a month's spending against its budget and produces the budget warnings described in the User Guide.
 * It returns the warning instead of printing it, so the model does not depend on the UI.
 */
public class BudgetChecker {

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
        // TODO: B - 80% warning and exceeded warning, see "Budget warnings" in the User Guide
        return null;
    }
}
