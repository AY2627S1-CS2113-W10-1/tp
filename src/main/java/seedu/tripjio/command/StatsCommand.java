package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.trip.Statistics;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.ui.SpendingTable;

import java.time.Clock;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;

/** Displays monthly category spending without modifying or saving application data. */
public class StatsCommand extends Command {
    private final YearMonth month;
    private final Clock clock;

    /**
     * Creates a report command using the computer's local clock.
     *
     * @param month Requested month, or null to use the current month at execution.
     */
    public StatsCommand(YearMonth month) {
        this(month, Clock.systemDefaultZone());
    }

    /**
     * Creates a report command with a controllable clock for reliable tests.
     *
     * @param month Requested month, or null for the current month.
     * @param clock Source of today's date.
     */
    public StatsCommand(YearMonth month, Clock clock) {
        this.month = month;
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    @Override
    public void execute(AppState state, Ui ui) {
        YearMonth selectedMonth = month == null ? YearMonth.now(clock) : month;
        Map<Category, Double> totals = Statistics.getMonthlyTotals(state.getExpenses(), selectedMonth);
        ui.showMessage("Spending for " + DateUtil.formatMonth(selectedMonth) + ":");
        ui.showMessage(SpendingTable.format(totals, DateUtil.formatShortMonth(selectedMonth)));
        if (state.getExpenses().getExpensesInMonth(selectedMonth).isEmpty()) {
            ui.showMessage("No expenses recorded for this month.");
        }
    }
}
