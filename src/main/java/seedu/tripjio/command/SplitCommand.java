package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.util.List;

public class SplitCommand extends Command {
    private final String description;
    private final double amount;
    private final int numberOfPeople;
    private final Double customShare;
    private final Category category;
    private final LocalDate date;
    private final List<String> participantNames;
    private final String payerName;
    private final String currency;
    private final Double exchangeRate;

    public SplitCommand(String description, double amount, int numberOfPeople, Double customShare,
                        Category category, LocalDate date, List<String> participantNames,
                        String payerName, String currency, Double exchangeRate) {
        this.description = description;
        this.amount = amount;
        this.numberOfPeople = numberOfPeople;
        this.customShare = customShare;
        this.category = category;
        this.date = date;
        this.participantNames = participantNames;
        this.payerName = payerName;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        if (participantNames != null && !participantNames.isEmpty() && participantNames.size() != numberOfPeople - 1) {
            throw new TripJioException("Number of names in p/ must be exactly n - 1.");
        }
        if (payerName != null && !participantNames.contains(payerName.toLowerCase())) {
            throw new TripJioException("Payer specified in paid/ must be included in the p/ list.");
        }

        SplitExpense expense = new SplitExpense(description, amount, numberOfPeople, customShare, 
                                                category, date, participantNames, payerName, currency, exchangeRate);
        state.getExpenses().add(expense);
        
        int count = state.getExpenses().size();
        String expenseWord = (count == 1) ? "expense" : "expenses";

        ui.showMessage("Got it. I've added this split expense:",
                "  " + expense.toString(),
                "Now you have " + count + " " + expenseWord + " in the list.");
    }

    @Override
    public boolean isMutating() { 
        return true; 
    }
}
