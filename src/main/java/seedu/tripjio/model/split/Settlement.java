package seedu.tripjio.model.split;

public class Settlement {
    private final String person;
    private final double amount;

    public Settlement(String person, double amount) {
        this.person = person.toLowerCase();
        this.amount = amount;
    }

    public String getPerson() {
        return person;
    }

    public double getAmount() {
        return amount;
    }
}
