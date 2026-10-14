package seedu.tripjio.model.split;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;

/**
 * Represents a group bill that the user shared with other people.
 * The user's share is stored in the inherited {@code amount} field, so it is kept in exactly one place and
 * budget, trip and statistics features get the correct value through {@link #getAmount()} without any override.
 */
public class SplitExpense extends Expense {
    private static final double EPSILON = 1e-4;

    private double totalAmount;
    private final int numberOfPeople;
    private final boolean hasCustomShare;

    public SplitExpense(String description, double totalAmount, int numberOfPeople,
                        Double customShare, Category category, LocalDate date) {
        super(description, customShare != null ? customShare : calculateEqualShare(totalAmount, numberOfPeople),
                category, date);
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.hasCustomShare = customShare != null;
    }

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

    public double getMyShare() {
        return getAmount();
    }

    public boolean hasCustomShare() {
        return hasCustomShare;
    }

    @Override
    public void setAmount(double newTotal) throws TripJioException {
        MoneyUtil.validateAmount(newTotal);

        if (hasCustomShare) {
            if (newTotal < getMyShare()) {
                throw new TripJioException("The new total bill cannot be less than your own share of "
                        + MoneyUtil.format(getMyShare()) + ".");
            }
            this.totalAmount = newTotal;
        } else {
            double newShare = calculateEqualShare(newTotal, numberOfPeople);
            if (newShare < 0.01) {
                throw new TripJioException("Each person's share must be at least $0.01.");
            }
            this.totalAmount = newTotal;
            super.setAmount(newShare);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [Split: " + MoneyUtil.format(totalAmount) + " / "
                + numberOfPeople + " people]";
    }

    @Override
    public String toStorageString() {
        return "S|" + getDescription() + "|" + MoneyUtil.toStorageString(totalAmount) + "|"
                + numberOfPeople + "|" + MoneyUtil.toStorageString(getMyShare()) + "|"
                + getCategory().name() + "|" + DateUtil.formatDate(getDate());
    }

    public static SplitExpense fromStorageString(String line) throws TripJioException {
        if (line == null) {
            throw new TripJioException("Storage line cannot be null.");
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 7 || !parts[0].equals("S")) {
            throw new TripJioException("Invalid split expense storage line: " + line);
        }

        String description = parts[1].trim();
        if (description.isEmpty() || description.contains("/")) {
            throw new TripJioException("Invalid description in storage: " + parts[1]);
        }

        double total;
        try {
            total = Double.parseDouble(parts[2]);
            MoneyUtil.validateAmount(total);
        } catch (NumberFormatException e) {
            throw new TripJioException("Invalid total amount in storage: " + parts[2]);
        }

        int people;
        try {
            people = Integer.parseInt(parts[3]);
            if (people < 2 || people > 50) {
                throw new TripJioException("Number of people must be between 2 and 50.");
            }
        } catch (NumberFormatException e) {
            throw new TripJioException("Invalid number of people in storage: " + parts[3]);
        }

        double myShare;
        try {
            myShare = Double.parseDouble(parts[4]);
            MoneyUtil.validateAmount(myShare);
        } catch (NumberFormatException e) {
            throw new TripJioException("Invalid share amount in storage: " + parts[4]);
        }

        if (myShare > total) {
            throw new TripJioException("User's share cannot exceed the total bill.");
        }

        Category category = Category.fromString(parts[5]);
        LocalDate date = DateUtil.parseDate(parts[6]);

        double equalShare = calculateEqualShare(total, people);
        if (equalShare < 0.01 && Math.abs(myShare - equalShare) < EPSILON) {
            throw new TripJioException("Each person's share must be at least $0.01.");
        }

        Double customShare = (Math.abs(myShare - equalShare) < EPSILON) ? null : myShare;

        return new SplitExpense(description, total, people, customShare, category, date);
    }
}
