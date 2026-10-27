package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Adds a split expense shared among multiple people to the expense list.
 */
public class SplitCommand extends Command {
    private final String description;
    private final double totalAmount;
    private final int numberOfPeople;
    private final Double customShare;
    private final Category category;
    private final LocalDate date;
    private final YearMonth affectedMonth;

    public SplitCommand(String description, double totalAmount, int numberOfPeople,
                        Double customShare, Category category, LocalDate date) {
        this.description = description;
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.customShare = customShare;
        this.category = category;
        this.date = date;
        this.affectedMonth = YearMonth.from(date);
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        SplitExpense expense = new SplitExpense(description, totalAmount, numberOfPeople,
                customShare, category, date);
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

    @Override
    public YearMonth getAffectedMonth() {
        return affectedMonth;
    }

    @Override
    public LocalDate getAffectedDate() {
        return date;
    }
}
