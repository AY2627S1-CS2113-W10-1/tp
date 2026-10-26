package seedu.tripjio.model.split;

import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExchangeRates;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SplitExpense extends Expense {
    private final int numberOfPeople;
    private Double customShare;
    private boolean hasCustomShare;
    
    private final List<String> participantNames;
    private final String payerName;
    private final String currency;
    private final Double exchangeRate;

    public SplitExpense(String description, double inputAmount, int numberOfPeople,
                        Double customShare, Category category, LocalDate date,
                        List<String> participantNames, String payerName, 
                        String currency, Double exchangeRate) {
        super(description, 
              (currency != null && exchangeRate != null) ? ExchangeRates.toSgd(inputAmount, exchangeRate) : inputAmount, 
              category, date);
        this.numberOfPeople = numberOfPeople;
        this.customShare = customShare;
        this.hasCustomShare = (customShare != null);
        this.participantNames = participantNames != null ? 
                participantNames.stream().map(String::toLowerCase).collect(Collectors.toList()) : new ArrayList<>();
        this.payerName = payerName != null ? payerName.toLowerCase() : null;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
    }

    public double getMyShare() {
        if (hasCustomShare) {
            return customShare;
        }
        return Math.round((getAmount() / numberOfPeople) * 100.0) / 100.0;
    }

    public void setCustomShare(Double customShare) {
        this.customShare = customShare;
        this.hasCustomShare = true;
    }

    public Map<String, Double> getAmountsOwedToMe() {
        Map<String, Double> owed = new LinkedHashMap<>();
        if (payerName != null || participantNames.isEmpty()) {
            return owed;
        }

        double rest = getAmount() - getMyShare();
        int otherCount = participantNames.size();
        
        long totalRestCents = Math.round(rest * 100.0);
        long baseCents = totalRestCents / otherCount;
        long leftoverCents = totalRestCents % otherCount;

        for (int i = 0; i < otherCount; i++) {
            long centsForThisPerson = baseCents + (i < leftoverCents ? 1 : 0);
            owed.put(participantNames.get(i), centsForThisPerson / 100.0);
        }
        return owed;
    }

    public String getPayerName() { return payerName; }
    public List<String> getParticipantNames() { return participantNames; }
    public String getCurrency() { return currency; }
    public Double getExchangeRate() { return exchangeRate; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public Double getCustomShare() { return customShare; }
    public boolean hasCustomShare() { return hasCustomShare; }

    @Override
    public String toString() {
        String base = super.toString() + " (Split among " + numberOfPeople + ")";
        if (currency != null) {
            base += " [" + currency + "]";
        }
        return base;
    }
}
