package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Adds an expense that the user paid for themselves.
 */
public class AddCommand extends Command {
    private final Expense expense;

    /**
     * Creates a command to add an expense. All values must already be validated by the parser.
     *
     * @param description Description of the expense.
     * @param amount Amount spent.
     * @param category Category of the expense.
     * @param date Date of the expense.
     */
    public AddCommand(String description, double amount, Category category, LocalDate date) {
        this.expense = new Expense(description, amount, category, date);
    }

    @Override
    public void execute(AppState state, Ui ui) {
        state.getExpenses().add(expense);
        ui.showMessage("Added expense:",
                "  " + expense,
                "You now have " + state.getExpenses().size() + " expenses.");
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public YearMonth getAffectedMonth() {
        return expense.getMonth();
    }
}
