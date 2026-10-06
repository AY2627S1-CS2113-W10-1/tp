package seedu.tripjio.util;

import seedu.tripjio.exception.TripJioException;

import java.util.Locale;

/**
 * Provides helper methods for validating and formatting amounts of money used in TripJio.
 * A fixed locale is always used, so that the output and the storage file look the same on every computer.
 */
public class MoneyUtil {
    /** Largest amount allowed for a single expense, budget or bill. */
    public static final double MAX_AMOUNT = 1_000_000;

    private MoneyUtil() {
    }

    /**
     * Checks that the given amount is more than 0 and not more than {@link #MAX_AMOUNT}.
     *
     * @param amount Amount to check.
     * @throws TripJioException If the amount is out of range.
     */
    public static void validateAmount(double amount) throws TripJioException {
        if (Double.isNaN(amount) || amount <= 0) {
            throw new TripJioException("Amount must be more than 0.");
        }
        if (amount > MAX_AMOUNT) {
            throw new TripJioException("Amount must not be more than " + format(MAX_AMOUNT) + ".");
        }
    }

    /**
     * Rounds the given amount to the nearest cent (2 decimal places), e.g. 33.333... becomes 33.33.
     *
     * @param amount Amount to round.
     * @return Rounded amount.
     */
    public static double roundToCents(double amount) {
        return Math.round(amount * 100) / 100.0;
    }

    /**
     * Returns the given amount for showing to the user, with a dollar sign, thousands separators
     * and two decimal places.
     *
     * @param amount Amount of money to format.
     * @return Formatted amount, e.g. "$1,234.50".
     */
    public static String format(double amount) {
        return String.format(Locale.ROOT, "$%,.2f", amount);
    }

    /**
     * Returns the given amount for writing to the storage file, with two decimal places
     * and no dollar sign or thousands separators.
     *
     * @param amount Amount of money to format.
     * @return Formatted amount, e.g. "1234.50".
     */
    public static String toStorageString(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}
