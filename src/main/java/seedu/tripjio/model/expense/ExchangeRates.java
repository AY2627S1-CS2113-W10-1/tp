package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Stores manually supplied SGD per unit rates; existing expenses retain their own rate snapshots. */
public class ExchangeRates {
    private final Map<String, BigDecimal> rates = new TreeMap<>();

    /** Validates and normalises an ISO currency code supported by Java. */
    public static String currencyCode(String text) throws TripJioException {
        try {
            return Currency.getInstance(text.trim().toUpperCase(Locale.ROOT)).getCurrencyCode();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new TripJioException("Use a valid three-letter currency code, e.g. EUR or SGD.");
        }
    }

    /** Parses a positive plain decimal rate with up to eight decimal places. */
    public static BigDecimal parseRate(String text) throws TripJioException {
        if (text == null || !text.matches("\\d+(\\.\\d{1,8})?")) {
            throw new TripJioException("Rate must be a positive decimal with up to eight decimal places.");
        }
        BigDecimal rate = new BigDecimal(text);
        if (rate.signum() <= 0 || rate.compareTo(new BigDecimal("1000000")) > 0) {
            throw new TripJioException("Rate must be more than 0 and at most 1000000.");
        }
        return rate;
    }

    /** Sets the SGD value of one unit of the source currency after validating both fields. */
    public void set(String currency, String value) throws TripJioException {
        String code = currencyCode(currency);
        BigDecimal rate = parseRate(value);
        if (code.equals("SGD") && rate.compareTo(BigDecimal.ONE) != 0) {
            throw new TripJioException("The SGD rate must be 1.");
        }
        rates.put(code, rate);
    }

    /** Returns the configured rate or reports how to supply a missing manual rate. */
    public BigDecimal get(String currency) throws TripJioException {
        String code = currencyCode(currency);
        if (code.equals("SGD")) {
            return BigDecimal.ONE;
        }
        if (!rates.containsKey(code)) {
            throw new TripJioException("No rate for " + code + ". Use: rate set from/" + code + " to/SGD r/RATE.");
        }
        return rates.get(code);
    }

    /** Serialises rates in a deterministic order. */
    public List<String> toStorageLines() {
        List<String> lines = new ArrayList<>();
        rates.forEach((code, rate) -> lines.add(code + "=" + rate.toPlainString()));
        return lines;
    }

    /** Loads one validated currency=rate record. */
    public void loadStorageLine(String line) throws TripJioException {
        String[] fields = line.split("=", -1);
        if (fields.length != 2) {
            throw new TripJioException("Invalid exchange rate line.");
        }
        set(fields[0], fields[1]);
    }

    public static double toSgd(double originalAmount, double rate) {
        return Math.round(originalAmount * rate * 100.0) / 100.0;
    }
}
