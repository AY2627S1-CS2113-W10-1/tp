package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;

public class SplitCommand extends Command {
    private final String description;
    private final double totalAmount;
    private final int numberOfPeople;
    private final Double customShare;
    private final Category category;
    private final LocalDate date;
    
    private YearMonth affectedMonth;

    public SplitCommand(String description, double totalAmount, int numberOfPeople,
                        Double customShare, Category category, LocalDate date) {
        this.description = description;
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.customShare = customShare;
        this.category = category;
        this.date = date;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        SplitExpense expense = new SplitExpense(description, totalAmount, numberOfPeople, 
                                                customShare, category, date);
        state.getExpenses().add(expense);
        
        affectedMonth = expense.getMonth();

        ui.showMessage("Got it. I've added this split expense:\n  " + expense 
                + "\nNow you have " + state.getExpenses().size() + " expenses in the list.");
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public YearMonth getAffectedMonth() {
        return affectedMonth;
    }
}