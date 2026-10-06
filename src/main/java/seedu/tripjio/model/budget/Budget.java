package seedu.tripjio.model.budget;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores the user's monthly budgets: a default budget for every month,
 * and optional budgets for specific months that override the default.
 */
public class Budget {
    /** Key used in the storage file for the default budget, e.g. "default=1000.00". */
    private static final String DEFAULT_KEY = "default";

    /** Separates the key and the amount in one storage line. */
    private static final String SEPARATOR = "=";

    /** Default monthly budget, or null if the user has not set one. */
    private Double defaultBudget;

    /** Budgets for specific months. A TreeMap keeps the months sorted, so the storage file is easy to read. */
    private final Map<YearMonth, Double> monthlyBudgets = new TreeMap<>();

    /**
     * Sets the default budget, which applies to every month without its own budget.
     *
     * @param amount Budget amount.
     * @throws TripJioException If the amount is not within the range allowed by
     *     {@link MoneyUtil#validateAmount(double)}.
     */
    public void setDefault(double amount) throws TripJioException {
        MoneyUtil.validateAmount(amount);
        this.defaultBudget = amount;
    }

    /**
     * Sets the budget for one month, replacing any budget already set for that month.
     *
     * @param month Month the budget applies to.
     * @param amount Budget amount.
     * @throws TripJioException If the amount is not within the range allowed by
     *     {@link MoneyUtil#validateAmount(double)}.
     */
    public void setForMonth(YearMonth month, double amount) throws TripJioException {
        MoneyUtil.validateAmount(amount);
        monthlyBudgets.put(month, amount);
    }

    /**
     * Returns the budget that applies to the given month: its own budget if set, otherwise the default budget.
     *
     * @param month Month to look up.
     * @return Budget for the month, or null if neither a month budget nor a default budget has been set.
     */
    public Double getBudgetFor(YearMonth month) {
        // TODO: B
        return null;
    }

    /**
     * Returns the budgets as lines for the storage file: first "default=1000.00" (if set),
     * then one line per month, e.g. "12-2026=1500.00".
     *
     * @return Storage lines representing the budgets.
     */
    public List<String> toStorageLines() {
        // TODO: B
        return new ArrayList<>();
    }

    /**
     * Loads one line of the budget section of the storage file, in the format produced by {@link #toStorageLines()}.
     * Lines are loaded one at a time, so an invalid line can be skipped without losing the other budgets.
     * Not implemented yet: it currently always throws an exception.
     *
     * @param line Line from the storage file.
     * @throws TripJioException Always, until loading budgets is implemented.
     */
    public void loadStorageLine(String line) throws TripJioException {
        // TODO: B
        throw new TripJioException("Budget data cannot be loaded yet.");
    }
}
