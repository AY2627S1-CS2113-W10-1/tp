package seedu.tripjio.model.budget;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;
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
     * Returns the default budget, or null if no default budget has been set.
     *
     * @return Default budget amount, or null.
     */
    public Double getDefaultBudget() {
        return defaultBudget;
    }

    /**
     * Returns the budget that applies to the given month: its own budget if set, otherwise the default budget.
     *
     * @param month Month to look up.
     * @return Budget for the month, or null if neither a month budget nor a default budget has been set.
     */
    public Double getBudgetFor(YearMonth month) {
        if (month != null && monthlyBudgets.containsKey(month)) {
            return monthlyBudgets.get(month);
        }
        return defaultBudget;
    }

    /**
     * Returns the budgets as lines for the storage file: first "default=1000.00" (if set),
     * then one line per month, e.g. "12-2026=1500.00".
     *
     * @return Storage lines representing the budgets.
     */
    public List<String> toStorageLines() {
        List<String> lines = new ArrayList<>();
        if (defaultBudget != null) {
            lines.add(DEFAULT_KEY + SEPARATOR + MoneyUtil.toStorageString(defaultBudget));
        }
        for (Map.Entry<YearMonth, Double> entry : monthlyBudgets.entrySet()) {
            lines.add(DateUtil.formatMonthForStorage(entry.getKey()) + SEPARATOR
                    + MoneyUtil.toStorageString(entry.getValue()));
        }
        return lines;
    }

    /**
     * Loads one line of the budget section of the storage file, in the format produced by {@link #toStorageLines()}.
     * Lines are loaded one at a time, so an invalid line can be skipped without losing the other budgets.
     *
     * @param line Line from the storage file.
     * @throws TripJioException If the line does not match the expected format or contains invalid data.
     */
    public void loadStorageLine(String line) throws TripJioException {
        if (line == null) {
            throw new TripJioException("Budget line cannot be null.");
        }
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 2) {
            throw new TripJioException("Invalid budget line: " + line);
        }

        String key = parts[0].trim();
        String amountString = parts[1].trim();
        if (key.isEmpty() || amountString.isEmpty()) {
            throw new TripJioException("Invalid budget line: " + line);
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            throw new TripJioException("Invalid amount in budget line: " + line);
        }
        MoneyUtil.validateAmount(amount);

        if (key.equals(DEFAULT_KEY)) {
            setDefault(amount);
        } else {
            YearMonth month = DateUtil.parseMonth(key);
            setForMonth(month, amount);
        }
    }
}
