package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists the user's expenses, optionally filtered by category and/or month.
 * Each expense is shown with its original index, even when a filter is used,
 * so the numbers can be used directly with {@code edit} and {@code delete}.
 */
public class ListCommand extends Command {
    /** Category to show, or null to show every category. */
    private final Category category;

    /** Month to show, or null to show every month. */
    private final YearMonth month;

    /**
     * Creates a command to list expenses.
     *
     * @param category Category to filter by, or null for no category filter.
     * @param month Month to filter by, or null for no month filter.
     */
    public ListCommand(Category category, YearMonth month) {
        this.category = category;
        this.month = month;
    }

    @Override
    public void execute(AppState state, Ui ui) {
        List<Expense> expenses = state.getExpenses().getAll();
        if (expenses.isEmpty()) {
            ui.showMessage("You have no expenses yet.");
            return;
        }

        List<String> lines = new ArrayList<>();
        double total = 0;
        // A counter loop is used instead of a for-each loop, because the original index must be shown.
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            if (matches(expense)) {
                lines.add((i + 1) + ". " + expense);
                total += expense.getAmount();
            }
        }

        if (lines.isEmpty()) {
            ui.showMessage("No expenses match the given filter.");
            return;
        }
        ui.showMessage("Here are your expenses:");
        ui.showMessage(lines.toArray(new String[0]));
        ui.showMessage("Total: " + MoneyUtil.format(MoneyUtil.roundToCents(total)));
    }

    private boolean matches(Expense expense) {
        boolean categoryMatches = category == null || expense.getCategory() == category;
        boolean monthMatches = month == null || expense.getMonth().equals(month);
        return categoryMatches && monthMatches;
    }
}
