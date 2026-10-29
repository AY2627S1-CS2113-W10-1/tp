package seedu.tripjio.model.split;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExchangeRates;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a group bill. The inherited amount is the full SGD bill; getAmount exposes only the user's
 * SGD share to budgets and reports. The currency and rate describe how the original bill was converted.
 */
public class SplitExpense extends Expense {
    private final int numberOfPeople;
    /** Null means the user's share is recalculated equally whenever the bill changes. */
    private Double customShare;
    private final List<String> participantNames;
    /** Null means the user paid the bill; otherwise this names another participant. */
    private final String payerName;
    private final String currency;
    private final Double exchangeRate;

    /** Creates an SGD split without named participants. Inputs must already be validated. */
    public SplitExpense(String description, double total, int people, Double share, Category category, LocalDate date) {
        this(description, total, people, share, category, date, List.of(), null, null, null);
    }

    /** Creates a split, converting the bill once; custom shares are always expressed in SGD. */
    public SplitExpense(String description, double inputAmount, int people, Double share, Category category,
                        LocalDate date, List<String> names, String payer, String currency, Double rate) {
        this(description, inputAmount, people, share, category, date, names, payer, currency, rate, false);
    }

    /** Restores an already converted SGD bill without applying the saved rate again. */
    private SplitExpense(String description, double sgdTotal, int people, Double share, Category category,
                         LocalDate date, List<String> names, String payer, String currency, Double rate,
                         boolean alreadyConverted) {
        super(description, alreadyConverted || currency == null ? sgdTotal : ExchangeRates.toSgd(sgdTotal, rate),
                category, date);
        this.numberOfPeople = people;
        this.customShare = share;
        this.participantNames = names == null ? List.of() : names.stream()
                .map(name -> name.trim().toLowerCase(Locale.ROOT)).toList();
        this.payerName = payer == null ? null : payer.trim().toLowerCase(Locale.ROOT);
        this.currency = currency == null ? null : currency.toUpperCase(Locale.ROOT);
        this.exchangeRate = rate;
    }

    /** Returns the equal SGD share rounded to the nearest cent. */
    public static double calculateEqualShare(double total, int people) {
        return MoneyUtil.roundToCents(total / people);
    }

    public double getTotalAmount() {
        return super.getAmount();
    }

    @Override
    public double getAmount() {
        return getMyShare();
    }

    public double getMyShare() {
        return customShare == null ? calculateEqualShare(getTotalAmount(), numberOfPeople) : customShare;
    }

    /** Validates a proposed SGD bill and share before either field changes. */
    public static void validateShare(double total, int people, Double share) throws TripJioException {
        MoneyUtil.validateAmount(total);
        if (people < 2 || people > 50) {
            throw new TripJioException("Number of people must be between 2 and 50.");
        }
        double ownShare = share == null ? calculateEqualShare(total, people) : share;
        if (!Double.isFinite(ownShare) || ownShare < 0.01) {
            throw new TripJioException("Each person's share must be at least $0.01.");
        }
        if (ownShare > total) {
            throw new TripJioException("Your custom share cannot be more than the total bill.");
        }
        MoneyUtil.validateOriginalAmount(ownShare);
    }

    /** Updates the full SGD bill while preserving any custom share. */
    @Override
    public void setAmount(double total) throws TripJioException {
        updateAmounts(total, customShare);
    }

    /** Changes the user's SGD share after validating it against the bill. */
    public void setCustomShare(Double share) throws TripJioException {
        updateAmounts(getTotalAmount(), share);
    }

    /** Applies a bill and optional custom share atomically, including combined edits. */
    public void updateAmounts(double total, Double share) throws TripJioException {
        validateShare(total, numberOfPeople, share);
        MoneyUtil.validateOriginalAmount(total);
        amount = total;
        customShare = share;
    }

    /** Validates a name so it can safely appear in command arguments and saved records. */
    public static String parsePerson(String text) throws TripJioException {
        String name = text.trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.contains("|") || name.contains(",") || name.contains("/")
                || name.contains("\r") || name.contains("\n")) {
            throw new TripJioException("Names cannot be empty or contain ',', '/', '|', or line breaks.");
        }
        return name;
    }

    /** Checks that participants are distinct and include any named payer. */
    public static void validateParticipants(List<String> names, String payer, int people) throws TripJioException {
        if (!names.isEmpty() && names.size() != people - 1) {
            throw new TripJioException("Number of names in p/ must be exactly n - 1.");
        }
        List<String> normalized = new ArrayList<>();
        for (String name : names) {
            normalized.add(parsePerson(name));
        }
        if (normalized.stream().distinct().count() != normalized.size()) {
            throw new TripJioException("Participant names must be unique ignoring case.");
        }
        if (payer != null && !normalized.contains(parsePerson(payer))) {
            throw new TripJioException("Payer specified in paid/ must be included in the p/ list.");
        }
    }

    /** Divides remaining SGD cents among other participants, assigning leftover cents in list order. */
    public Map<String, Double> getAmountsOwedToMe() {
        Map<String, Double> owed = new LinkedHashMap<>();
        if (payerName != null || participantNames.isEmpty()) {
            return owed;
        }
        long restCents = Math.round((getTotalAmount() - getMyShare()) * 100);
        int count = participantNames.size();
        for (int i = 0; i < count; i++) {
            owed.put(participantNames.get(i), (restCents / count + (i < restCents % count ? 1 : 0)) / 100.0);
        }
        return owed;
    }

    public String getPayerName() {
        return payerName;
    }

    public List<String> getParticipantNames() {
        return participantNames;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public Double getCustomShare() {
        return customShare;
    }

    public boolean hasCustomShare() {
        return customShare != null;
    }

    @Override
    public String toString() {
        return super.toString() + " (Split among " + numberOfPeople + ")"
                + (currency == null ? "" : " [" + currency + "]");
    }

    /** Keeps the seven legacy fields and appends payer, names, currency, rate and a custom-share flag. */
    @Override
    public String toStorageString() {
        return String.join("|", "S", description, MoneyUtil.toStorageString(getTotalAmount()),
                Integer.toString(numberOfPeople), MoneyUtil.toStorageString(getMyShare()), category.name(),
                DateUtil.formatDate(date), payerName == null ? "" : payerName, String.join(",", participantNames),
                currency == null ? "" : currency, exchangeRate == null ? ""
                        : BigDecimal.valueOf(exchangeRate).stripTrailingZeros().toPlainString(),
                Boolean.toString(hasCustomShare()));
    }

    /** Loads legacy and extended records; saved totals and shares are already in SGD. */
    public static SplitExpense fromStorageString(String line) throws TripJioException {
        if (line == null) {
            throw new TripJioException("Storage line cannot be null.");
        }
        String[] fields = line.split("\\|", -1);
        if ((fields.length != 7 && fields.length != 12) || !fields[0].equals("S")) {
            throw new TripJioException("Invalid split expense storage line.");
        }
        // Storage validation belongs to the model and must not depend on the command parser.
        String description = fields[1].trim();
        if (description.isEmpty() || description.contains("/")) {
            throw new TripJioException("Invalid description in split expense storage line.");
        }
        try {
            double total = Double.parseDouble(fields[2]);
            int people = Integer.parseInt(fields[3]);
            double share = Double.parseDouble(fields[4]);
            validateShare(total, people, share);
            MoneyUtil.validateOriginalAmount(total);
            Double custom = Math.abs(share - calculateEqualShare(total, people)) < 0.0001 ? null : share;
            String payer = null;
            List<String> names = List.of();
            String currency = null;
            Double rate = null;
            if (fields.length == 12) {
                payer = fields[7].isEmpty() ? null : parsePerson(fields[7]);
                names = fields[8].isEmpty() ? List.of() : List.of(fields[8].split(",", -1));
                validateParticipants(names, payer, people);
                if (!fields[9].isEmpty() || !fields[10].isEmpty()) {
                    currency = ExchangeRates.currencyCode(fields[9]);
                    rate = ExchangeRates.parseRate(fields[10]).doubleValue();
                    if (currency.equals("SGD") && rate != 1.0) {
                        throw new TripJioException("The SGD rate must be 1.");
                    }
                }
                if (!fields[11].equals("true") && !fields[11].equals("false")) {
                    throw new TripJioException("Invalid custom-share flag.");
                }
                if (fields[11].equals("false") && custom != null) {
                    throw new TripJioException("Equal share does not match the stored bill.");
                }
                custom = fields[11].equals("true") ? share : null;
            }
            return new SplitExpense(description, total, people, custom,
                    Category.fromString(fields[5]), DateUtil.parseDate(fields[6]), names, payer, currency, rate, true);
        } catch (IllegalArgumentException e) {
            throw new TripJioException("Invalid number in split expense storage line.");
        }
    }

    /** Fills the full SGD bill, people and, for a foreign split, currency and rate columns. */
    @Override
    public List<String> toCsvFields(int index) {
        List<String> fields = super.toCsvFields(index);
        fields.set(CSV_TYPE, "Split");
        fields.set(CSV_FULL_BILL, MoneyUtil.toStorageString(getTotalAmount()));
        fields.set(CSV_PEOPLE, String.valueOf(numberOfPeople));

        // Only the converted SGD bill is kept, so the original-amount column stays empty.
        if (currency != null) {
            fields.set(CSV_CURRENCY, currency);
            fields.set(CSV_RATE, BigDecimal.valueOf(exchangeRate).stripTrailingZeros().toPlainString());
        }
        return fields;
    }
}
