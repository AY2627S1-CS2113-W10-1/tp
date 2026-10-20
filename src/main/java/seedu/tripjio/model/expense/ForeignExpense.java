package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Keeps original currency and a fixed manual rate while exposing SGD spending to existing reports. */
public class ForeignExpense extends Expense {
    private final String currency;
    private final BigDecimal rate;
    private double originalAmount;

    /** Validates the conversion before creating an expense; rates are never looked up again. */
    public ForeignExpense(String description, double originalAmount, Category category, LocalDate date,
                          String currency, BigDecimal rate) throws TripJioException {
        super(description, convert(originalAmount, rate), category, date);
        this.currency = ExchangeRates.currencyCode(currency);
        this.rate = ExchangeRates.parseRate(rate.toPlainString());
        if (this.currency.equals("SGD") && rate.compareTo(BigDecimal.ONE) != 0) {
            throw new TripJioException("The SGD rate must be 1.");
        }
        this.originalAmount = originalAmount;
    }

    /** Converts once using decimal arithmetic and rounds to SGD cents. */
    private static double convert(double original, BigDecimal rate) throws TripJioException {
        MoneyUtil.validateAmount(original);
        if (rate == null) {
            throw new TripJioException("An exchange rate is required.");
        }
        ExchangeRates.parseRate(rate.toPlainString());
        double converted = BigDecimal.valueOf(original).multiply(rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
        MoneyUtil.validateAmount(converted);
        return converted;
    }

    /** Edits the original-currency amount using the saved rate, not the latest configured rate. */
    @Override
    public void setAmount(double original) throws TripJioException {
        double converted = convert(original, rate);
        amount = converted;
        originalAmount = original;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + currency + " " + MoneyUtil.toStorageString(originalAmount)
                + " at " + rate.toPlainString() + " SGD/" + currency + "]";
    }

    @Override
    public String toStorageString() {
        return String.join("|", "F", description, MoneyUtil.toStorageString(originalAmount), category.name(),
                DateUtil.formatDate(date), currency, rate.toPlainString());
    }

    /** Restores a snapshot independently of today's configured rates. */
    public static ForeignExpense fromStorageString(String line) throws TripJioException {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7 || !fields[0].equals("F")) {
            throw new TripJioException("Invalid foreign expense line.");
        }
        Expense original = Expense.fromStorageString(String.join("|", "E", fields[1], fields[2], fields[3], fields[4]));
        return new ForeignExpense(original.getDescription(), original.getAmount(), original.getCategory(),
                original.getDate(), fields[5], ExchangeRates.parseRate(fields[6]));
    }
}
