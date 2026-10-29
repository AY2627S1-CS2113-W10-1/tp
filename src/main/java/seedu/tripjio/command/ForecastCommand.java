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
 * Forecasts the user's total spending by the end of a month based on their current spending pace,
 * comparing the projected total against the monthly budget.
 */
public class ForecastCommand extends Command {
    private final YearMonth month;
    private final LocalDate today;

    /**
     * Creates a command to forecast spending for the current month.
     */
    public ForecastCommand() {
        this(null, LocalDate.now());
    }

    /**
     * Creates a command to forecast spending for the given month.
     *
     * @param month Month to forecast, or null for the current month.
     */
    public ForecastCommand(YearMonth month) {
        this(month, LocalDate.now());
    }

    /**
     * Creates a command to forecast spending with an explicit reference date.
     * This constructor is useful for deterministic testing without depending on the system clock.
     *
     * @param month Month to forecast, or null for the current month.
     * @param today Reference date used as "today".
     */
    public ForecastCommand(YearMonth month, LocalDate today) {
        this.today = (today != null) ? today : LocalDate.now();
        this.month = (month != null) ? month : YearMonth.from(this.today);
    }

    @Override
    public void execute(AppState state, Ui ui) {
        YearMonth currentMonth = YearMonth.from(today);

        if (month.isAfter(currentMonth)) {
            ui.showMessage("Cannot forecast for a future month (" + DateUtil.formatMonth(month)
                    + ") because no days have passed yet.");
            return;
        }

        double spentSoFar = state.getExpenses().getTotalSpentInMonth(month);
        Double budget = state.getBudget().getBudgetFor(month);
        int totalDays = month.lengthOfMonth();

        List<String> lines = new ArrayList<>();
        lines.add("Spending forecast for " + DateUtil.formatMonth(month) + ":");

        if (month.isBefore(currentMonth)) {
            lines.add("  Note: This month has already ended.");
            lines.add("  Budget: " + (budget != null ? MoneyUtil.format(budget) : "No budget set"));
            lines.add("  Total spent: " + MoneyUtil.format(spentSoFar));
            if (budget != null) {
                if (spentSoFar > budget) {
                    lines.add("  Status: Exceeded budget by " + MoneyUtil.format(spentSoFar - budget) + ".");
                } else {
                    lines.add("  Status: Stayed within budget (" + MoneyUtil.format(budget - spentSoFar)
                            + " remaining).");
                }
            }
            ui.showMessage(lines.toArray(String[]::new));
            return;
        }

        int daysPassed = today.getDayOfMonth();
        double dailyPace = (spentSoFar > 0 && daysPassed > 0) ? (spentSoFar / daysPassed) : 0.0;
        double projectedTotal = MoneyUtil.roundToCents(dailyPace * totalDays);

        lines.add("  Budget: " + (budget != null ? MoneyUtil.format(budget) : "No budget set"));
        lines.add("  Spent so far (day " + daysPassed + " of " + totalDays + "): " + MoneyUtil.format(spentSoFar));
        lines.add("  Current pace: " + MoneyUtil.format(MoneyUtil.roundToCents(dailyPace)) + "/day");
        lines.add("  Projected total spending: " + MoneyUtil.format(projectedTotal));

        if (budget != null) {
            if (projectedTotal > budget) {
                lines.add("  Status: Projected to exceed budget by "
                        + MoneyUtil.format(projectedTotal - budget) + "!");
            } else {
                lines.add("  Status: On track to stay within budget ("
                        + MoneyUtil.format(budget - projectedTotal) + " buffer remaining).");
            }
        } else {
            lines.add("  Status: No budget set. (Use 'budget set a/AMOUNT' to set one)");
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
