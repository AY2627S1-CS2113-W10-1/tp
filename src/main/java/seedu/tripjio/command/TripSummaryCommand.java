package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.trip.Statistics;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.DateUtil;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Displays a comprehensive summary of the currently selected trip, including dates, duration,
 * status, budget vs. spending, daily allowance, and a breakdown of spending by category.
 */
public class TripSummaryCommand extends Command {
    private static final String ROW_FORMAT = "%-15s %15s %15s";
    private static final String TABLE_LINE = "-".repeat(47);

    private final LocalDate today;

    /**
     * Creates a command to summarize the selected trip using the current date.
     */
    public TripSummaryCommand() {
        this(LocalDate.now());
    }

    /**
     * Creates a command to summarize the selected trip with an explicit reference date.
     * This constructor allows deterministic testing without depending on the system clock.
     *
     * @param today Reference date used as "today".
     */
    public TripSummaryCommand(LocalDate today) {
        this.today = (today != null) ? today : LocalDate.now();
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        if (!state.getTrips().hasCurrent()) {
            throw new TripJioException("No trip selected. Use trip list, then trip select INDEX; "
                    + "if no trips exist, use trip add n/NAME s/START_DATE e/END_DATE.");
        }

        Trip trip = state.getTrip();
        double spent = state.getExpenses().getTotalSpentBetween(trip.getStart(), trip.getEnd());
        List<Expense> expenses = state.getExpenses().getExpensesBetween(trip.getStart(), trip.getEnd());
        Map<Category, Double> totals = Statistics.getCategoryTotals(expenses);

        List<String> lines = new ArrayList<>();
        lines.add("Trip summary: " + state.getTrips().getCurrentName());
        lines.add("  Dates: " + DateUtil.formatDate(trip.getStart()) + " to "
                + DateUtil.formatDate(trip.getEnd()) + " (" + trip.getDurationDays() + " day(s))");
        lines.add("  Status: " + trip.getStatus(today));

        long remainingDays = trip.getRemainingDays(today);
        lines.add("  Remaining trip days: " + remainingDays);

        if (trip.hasBudget()) {
            double budget = trip.getBudget();
            lines.add("  Budget: " + MoneyUtil.format(budget));
            lines.add("  Total spent: " + MoneyUtil.format(spent));
            if (spent > budget) {
                lines.add("  Over budget by: " + MoneyUtil.format(spent - budget));
            } else {
                lines.add("  Remaining budget: " + MoneyUtil.format(budget - spent));
            }

            if ("Completed".equals(trip.getStatus(today))) {
                lines.add("  Daily allowance: N/A (trip completed)");
            } else if (spent >= budget) {
                lines.add("  Daily allowance: $0.00/day");
            } else if (remainingDays > 0) {
                double remaining = budget - spent;
                double dailyAllowance = MoneyUtil.roundToCents(remaining / remainingDays);
                lines.add("  Daily allowance: " + MoneyUtil.format(dailyAllowance) + "/day");
            } else {
                lines.add("  Daily allowance: N/A");
            }
        } else {
            lines.add("  Budget: Not set");
            lines.add("  Total spent: " + MoneyUtil.format(spent));
            lines.add("  Daily allowance: N/A (no budget set)");
        }

        lines.add("");
        lines.add("Spending by category:");
        lines.add(String.format(Locale.ROOT, ROW_FORMAT, "Category", "Spent", "% of Total"));
        lines.add(TABLE_LINE);

        for (Category category : Category.values()) {
            double amount = totals.get(category);
            double percentage = (spent > 0) ? (amount / spent) * 100.0 : 0.0;
            String percentageText = String.format(Locale.ROOT, "%.1f%%", percentage);
            lines.add(String.format(Locale.ROOT, ROW_FORMAT, category.getDisplayName(),
                    MoneyUtil.format(amount), percentageText));
        }

        lines.add(TABLE_LINE);
        String totalPercentageText = (spent > 0) ? "100.0%" : "0.0%";
        lines.add(String.format(Locale.ROOT, ROW_FORMAT, "Total", MoneyUtil.format(spent), totalPercentageText));

        if (expenses.isEmpty()) {
            lines.add("");
            lines.add("No expenses recorded for this trip yet.");
        }

        ui.showMessage(lines.toArray(String[]::new));
    }

    public LocalDate getToday() {
        return today;
    }
}
