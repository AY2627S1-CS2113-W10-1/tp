package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Edits the given fields of an existing expense. Fields that are not given keep their current values.
 * For a split expense, the new amount is the full bill, and {@code SplitExpense.setAmount} works out the user's share.
 */
public class EditCommand extends Command {
    private final int index;

    /** New values, each null when the field should stay the same. */
    private final String description;
    private final Double amount;
    private final Category category;
    private final LocalDate date;

    /** Month of the expense after editing, known only once the command has run. */
    private YearMonth affectedMonth;

    /**
     * Creates a command to edit an expense. All given values must already be validated by the parser,
     * and at least one of them must not be null.
     *
     * @param index Index of the expense, starting from 1.
     * @param description New description, or null to keep the current one.
     * @param amount New amount, or null to keep the current one.
     * @param category New category, or null to keep the current one.
     * @param date New date, or null to keep the current one.
     */
    public EditCommand(int index, String description, Double amount, Category category, LocalDate date) {
        assert description != null || amount != null || category != null || date != null
                : "At least one field must be edited";
        this.index = index;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Updates the expense and shows it to the user.
     * The amount is changed first because it is the only change that can fail (e.g. a new bill smaller than
     * the user's own share of a split expense); if it fails, nothing has been changed yet.
     *
     * @throws TripJioException If there is no expense at the index, or the new amount is not allowed.
     */
    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        Expense expense = state.getExpenses().get(index);

        if (amount != null) {
            expense.setAmount(amount);
        }
        if (description != null) {
            expense.setDescription(description);
        }
        if (category != null) {
            expense.setCategory(category);
        }
        if (date != null) {
            expense.setDate(date);
        }

        affectedMonth = expense.getMonth();
        ui.showMessage("Edited expense:", "  " + expense);
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    /**
     * Returns the month of the expense after editing, so that the budget of that month is checked.
     * If the date was moved to another month, the old month's spending only went down, so it needs no warning.
     *
     * @return Month of the edited expense, or null if the command has not run successfully.
     */
    @Override
    public YearMonth getAffectedMonth() {
        return affectedMonth;
    }
}
