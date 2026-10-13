package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.YearMonth;

/**
 * Sets the default monthly budget or a budget for a specific month.
 */
public class SetBudgetCommand extends Command {
    private final double amount;
    private final YearMonth month;

    /**
     * Creates a command to set the default monthly budget.
     *
     * @param amount Budget amount.
     */
    public SetBudgetCommand(double amount) {
        this(amount, null);
    }

    /**
     * Creates a command to set a monthly budget. If {@code month} is null, the default budget is set.
     *
     * @param amount Budget amount.
     * @param month Month the budget applies to, or null for the default budget.
     */
    public SetBudgetCommand(double amount, YearMonth month) {
        this.amount = amount;
        this.month = month;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        if (month == null) {
            state.getBudget().setDefault(amount);
            ui.showMessage("Default monthly budget set to " + MoneyUtil.format(amount) + ".");
        } else {
            state.getBudget().setForMonth(month, amount);
            ui.showMessage("Budget for " + DateUtil.formatMonth(month) + " set to " + MoneyUtil.format(amount) + ".");
        }
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    public double getAmount() {
        return amount;
    }

    public YearMonth getMonth() {
        return month;
    }
}
