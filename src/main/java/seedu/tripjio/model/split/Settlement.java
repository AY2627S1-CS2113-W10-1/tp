package seedu.tripjio.model.split;

import java.util.Locale;

/** A payment in SGD: positive means the user received money, negative means the user paid money. */
public class Settlement {
    private final String person;
    /** Signed payment amount; subtracting it from net debt accounts for either payment direction. */
    private final double amount;

    /** Creates a payment for a validated participant name and signed SGD amount. */
    public Settlement(String person, double amount) {
        this.person = person.trim().toLowerCase(Locale.ROOT);
        this.amount = amount;
    }

    public String getPerson() {
        return person;
    }

    public double getAmount() {
        return amount;
    }
}
