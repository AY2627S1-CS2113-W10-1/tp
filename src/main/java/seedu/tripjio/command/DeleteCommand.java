package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;

/**
 * Deletes an expense, including a split expense, by its index in the {@code list} output.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command to delete the expense at the given index.
     *
     * @param index Index of the expense, starting from 1.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the expense and shows it to the user.
     *
     * @throws TripJioException If there is no expense at the index.
     */
    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        Expense removed = state.getExpenses().delete(index);
        ui.showMessage("Deleted expense:",
                "  " + removed,
                "You now have " + state.getExpenses().size() + " expenses.");
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
