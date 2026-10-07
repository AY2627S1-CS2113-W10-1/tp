package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents a single expense recorded by the user, with a description, amount, category and date.
 * Subclasses such as split expenses may override {@link #getAmount()} to change which amount
 * counts towards the user's spending.
 */
public class Expense {
    /** Marks a line in the storage file as a normal expense. Split expenses use a different tag. */
    private static final String STORAGE_TAG = "E";

    /** Separates the fields of one expense in the storage file. */
    private static final String STORAGE_SEPARATOR = "|";

    /** Number of fields in one storage line: tag, description, amount, category and date. */
    private static final int STORAGE_FIELD_COUNT = 5;

    protected String description;
    protected double amount;
    protected Category category;
    protected LocalDate date;

    /**
     * Creates an expense with the given details.
     * The caller must validate the inputs first: the amount must be within the range allowed by
     * {@link MoneyUtil#validateAmount(double)}, and the description must not contain '|'
     * because it is used as the separator in the storage file.
     *
     * @param description Short description of the expense, e.g. "Lunch".
     * @param amount Amount spent.
     * @param category Category the expense belongs to.
     * @param date Date the expense was made.
     */
    public Expense(String description, double amount, Category category, LocalDate date) {
        assert description != null && !description.isBlank() : "Description must not be empty";
        assert amount > 0 && amount <= MoneyUtil.MAX_AMOUNT : "Amount must be validated before creating an expense";
        assert category != null : "Category must not be null";
        assert date != null : "Date must not be null";

        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the amount that counts towards the user's own spending.
     * Budget, trip and statistics features should always use this method instead of reading the field directly,
     * so that subclasses (e.g. split expenses returning only the user's share) are handled correctly.
     *
     * @return Amount spent by the user.
     */
    public double getAmount() {
        return amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Updates the amount of this expense.
     * Subclasses may override this, e.g. a split expense treats the amount as the full bill and recalculates
     * the user's share.
     *
     * @param amount New amount.
     * @throws TripJioException If the amount is not within the range allowed by
     *     {@link MoneyUtil#validateAmount(double)}.
     */
    public void setAmount(double amount) throws TripJioException {
        MoneyUtil.validateAmount(amount);
        this.amount = amount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the month this expense was made in, used for monthly budgets and statistics.
     *
     * @return Year and month of the expense date.
     */
    public YearMonth getMonth() {
        return YearMonth.from(date);
    }

    /**
     * Returns this expense as shown to the user, e.g. {@code [Food] Lunch - $12.50 (15-10-2026)}.
     *
     * @return Display string of this expense.
     */
    @Override
    public String toString() {
        return "[" + category.getDisplayName() + "] " + description
                + " - " + MoneyUtil.format(getAmount())
                + " (" + DateUtil.formatDate(date) + ")";
    }

    /**
     * Returns this expense as one line in the storage file format,
     * e.g. {@code E|Lunch|12.50|FOOD|15-10-2026}.
     *
     * @return Storage line representing this expense.
     */
    public String toStorageString() {
        return String.join(STORAGE_SEPARATOR,
                STORAGE_TAG,
                description,
                MoneyUtil.toStorageString(getAmount()),
                category.name(),
                DateUtil.formatDate(date));
    }

    /**
     * Creates an expense from one line of the storage file, in the format produced by {@link #toStorageString()}.
     * Every field is checked before the expense is created, so an invalid line is reported with a
     * {@code TripJioException} instead of being loaded with wrong data.
     *
     * @param line Line from the storage file.
     * @return Expense described by the line.
     * @throws TripJioException If the line has the wrong tag or number of fields, or contains an invalid value.
     */
    public static Expense fromStorageString(String line) throws TripJioException {
        // A limit of -1 keeps empty trailing fields, so "E|Lunch|12.50|FOOD|" is detected as having an empty date.
        String[] parts = line.split("\\" + STORAGE_SEPARATOR, -1);
        if (parts.length != STORAGE_FIELD_COUNT || !parts[0].equals(STORAGE_TAG)) {
            throw new TripJioException("Invalid expense line: " + line);
        }

        String description = parts[1].trim();
        if (description.isEmpty()) {
            throw new TripJioException("Missing description in expense line: " + line);
        }

        double amount;
        try {
            amount = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new TripJioException("Invalid amount in expense line: " + line);
        }
        MoneyUtil.validateAmount(amount);

        Category category;
        try {
            category = Category.valueOf(parts[3]);
        } catch (IllegalArgumentException e) {
            throw new TripJioException("Invalid category in expense line: " + line);
        }

        // Future dates are allowed here on purpose: that rule applies to user input, and rejecting them
        // when loading would silently drop valid data if the computer's clock or time zone changes.
        LocalDate date = DateUtil.parseDate(parts[4]);

        return new Expense(description, amount, category, date);
    }
}
