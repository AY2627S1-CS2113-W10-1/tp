package seedu.tripjio.model.split;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;

/**
 * Represents a group bill that the user shared with other people.
 * The user's share is stored in the inherited {@code amount} field, so it is kept in exactly one place and
 * budget, trip and statistics features get the correct value through {@link #getAmount()} without any override.
 *
 * <p>Rules agreed by the team for editing the full bill with {@code edit INDEX a/AMOUNT} (see the User Guide):
 * <ul>
 *     <li>Equal split: the user's share is recalculated as the new bill divided by the number of people.</li>
 *     <li>Custom share ({@code my/}): the user's share stays the same; only the full bill changes.
 *         If the new bill is less than the user's share, the edit is rejected and nothing changes.</li>
 *     <li>The number of people and the custom share cannot be edited in v1.0
 *         (the user deletes and re-adds the split expense instead).</li>
 * </ul>
 */
public class SplitExpense extends Expense {
    private double totalAmount;
    private final int numberOfPeople;
    private final boolean hasCustomShare;

    /**
     * Creates a split expense. If no custom share is given, the bill is split equally among all people.
     * The caller must validate the inputs first: the total amount must be a valid amount, the number of
     * people must be from 2 to 50, a custom share must be more than 0 and not more than the total amount,
     * and an equal share must be at least $0.01 after rounding (see {@link #calculateEqualShare(double, int)}).
     *
     * @param description Short description of the bill, e.g. "Group dinner".
     * @param totalAmount Full bill for the whole group.
     * @param numberOfPeople Number of people sharing the bill, including the user.
     * @param customShare User's own share, or {@code null} to split the bill equally.
     * @param category Category the expense belongs to.
     * @param date Date the expense was made.
     */
    public SplitExpense(String description, double totalAmount, int numberOfPeople,
                        Double customShare, Category category, LocalDate date) {
        super(description, customShare != null ? customShare : calculateEqualShare(totalAmount, numberOfPeople),
                category, date);
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.hasCustomShare = customShare != null;
    }

    /**
     * Returns the user's share when the bill is split equally, rounded to 2 decimal places.
     * For a very small bill shared by many people this can round down to 0, so callers should check that
     * the result is at least $0.01 before creating a split expense.
     *
     * @param totalAmount Full bill for the whole group.
     * @param numberOfPeople Number of people sharing the bill, including the user.
     * @return The user's equal share of the bill.
     */
    public static double calculateEqualShare(double totalAmount, int numberOfPeople) {
        assert numberOfPeople > 0 : "Number of people must be positive";
        return MoneyUtil.roundToCents(totalAmount / numberOfPeople);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    /**
     * Returns the user's own share of the bill. This is the same value as {@link #getAmount()};
     * the method exists only to make code that works with split expenses easier to read.
     *
     * @return The user's share.
     */
    public double getMyShare() {
        return getAmount();
    }

    public boolean hasCustomShare() {
        return hasCustomShare;
    }

    /**
     * Creates a split expense from one line of the storage file,
     * e.g. {@code S|Group dinner|90.00|3|30.00|FOOD|14-10-2026}.
     * Not implemented yet: it currently always throws an exception.
     *
     * @param line Line from the storage file.
     * @return Split expense described by the line.
     * @throws TripJioException Always, until loading split expenses is implemented.
     */
    public static SplitExpense fromStorageString(String line) throws TripJioException {
        // TODO: D - parse and validate every field like Expense.fromStorageString does.
        //  The line has no separate flag for a custom share: treat the share as custom when it differs from
        //  calculateEqualShare(total, people), so the storage format in the User Guide stays unchanged.
        throw new TripJioException("Split expenses cannot be loaded yet.");
    }

    // TODO: D - override setAmount (follow the rules in the class comment; update totalAmount and,
    //  for an equal split, call super.setAmount with the new share), toString and toStorageString.
}
