package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.ExchangeRates;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Adds a shared bill whose SGD share contributes to the user's spending and debt ledger. */
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
        SplitExpense.validateParticipants(participantNames == null ? List.of() : participantNames,
                payerName, numberOfPeople);
        SplitExpense.validateShare(currency == null ? amount : ExchangeRates.toSgd(amount, exchangeRate),
                numberOfPeople, customShare);

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

    /** Returns the split's month so the main loop checks spending against that month's budget. */
    @Override
    public YearMonth getAffectedMonth() {
        return YearMonth.from(date);
    }

    @Override
    public LocalDate getAffectedDate() {
        return date;
    }
}
