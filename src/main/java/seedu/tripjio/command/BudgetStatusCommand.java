package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the user's budget status for a given month or the current month: the budget,
 * amount spent, amount remaining or exceeded, and (for the current month) the days left and daily allowance.
 */
public class BudgetStatusCommand extends Command {
    private final YearMonth month;
    private final LocalDate today;

    /**
     * Creates a command to show the budget status for the current month.
     */
    public BudgetStatusCommand() {
        this(null, LocalDate.now());
    }

    /**
     * Creates a command to show the budget status for a specific month.
     *
     * @param month Month to show the status for, or null for the current month.
     */
    public BudgetStatusCommand(YearMonth month) {
        this(month, LocalDate.now());
    }

    /**
     * Creates a command to show the budget status for a specific month with a reference date.
     * This constructor is useful for testing without depending on the system clock.
     *
     * @param month Month to show the status for, or null for the current month.
     * @param today Reference date used as "today".
     */
    public BudgetStatusCommand(YearMonth month, LocalDate today) {
        this.today = (today != null) ? today : LocalDate.now();
        this.month = (month != null) ? month : YearMonth.from(this.today);
    }

    @Override
    public void execute(AppState state, Ui ui) {
        Double budgetAmount = state.getBudget().getBudgetFor(month);
        if (budgetAmount == null) {
            ui.showMessage("No budget has been set for " + DateUtil.formatMonth(month) + ".");
            return;
        }

        double spent = state.getExpenses().getTotalSpentInMonth(month);
        double remaining = budgetAmount - spent;

        List<String> lines = new ArrayList<>();
        lines.add("Budget status for " + DateUtil.formatMonth(month) + ":");
        lines.add("  Budget: " + MoneyUtil.format(budgetAmount));
        lines.add("  Spent: " + MoneyUtil.format(spent));

        if (remaining >= 0) {
            lines.add("  Remaining: " + MoneyUtil.format(remaining));
        } else {
            lines.add("  Over budget by: " + MoneyUtil.format(spent - budgetAmount));
        }

        if (month.equals(YearMonth.from(today))) {
            int daysLeft = month.lengthOfMonth() - today.getDayOfMonth() + 1;
            String dayWord = (daysLeft == 1) ? "day" : "days";
            lines.add("  Days left: " + daysLeft + " " + dayWord + " (including today)");

            double dailyAllowance = (remaining > 0) ? MoneyUtil.roundToCents(remaining / daysLeft) : 0.0;
            lines.add("  Daily allowance: " + MoneyUtil.format(dailyAllowance) + "/day");
        }

        ui.showMessage(lines.toArray(String[]::new));
    }

    public YearMonth getMonth() {
        return month;
    }

    public LocalDate getToday() {
        return today;
    }
}
