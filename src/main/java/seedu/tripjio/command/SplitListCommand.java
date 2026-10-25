package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseFilter;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Lists all split expenses, optionally filtered by a specific month.
 */
public class SplitListCommand extends Command {
    private final YearMonth month;
    private final boolean tripOnly;

    public SplitListCommand() {
        this(null);
    }

    public SplitListCommand(YearMonth month) {
        this(month, false);
    }

    /** Creates a split-only view intersecting month and optional selected-trip dates. */
    public SplitListCommand(YearMonth month, boolean tripOnly) {
        this.month = month;
        this.tripOnly = tripOnly;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        Set<Expense> matching = new HashSet<>(ExpenseFilter.select(state, null, month, tripOnly));
        List<Expense> allExpenses = state.getExpenses().getAll();
        List<SplitExpense> splitExpenses = new ArrayList<>();
        List<Integer> actualIndexes = new ArrayList<>();

        for (int i = 0; i < allExpenses.size(); i++) {
            Expense expense = allExpenses.get(i);
            if (expense instanceof SplitExpense && matching.contains(expense)) {
                SplitExpense splitExpense = (SplitExpense) expense;
                if (month == null || splitExpense.getMonth().equals(month)) {
                    splitExpenses.add(splitExpense);
                    actualIndexes.add(i + 1);
                }
            }
        }

        if (splitExpenses.isEmpty()) {
            if (month != null || tripOnly) {
                ui.showMessage("No split expenses match the given filter.");
            } else {
                ui.showMessage("You have no split expenses yet.");
            }
            return;
        }

        List<String> messages = new ArrayList<>();
        messages.add("Here are your split expenses:");
        for (int i = 0; i < splitExpenses.size(); i++) {
            messages.add(actualIndexes.get(i) + ". " + splitExpenses.get(i));
        }

        ui.showMessage(messages.toArray(new String[0]));
    }
}
