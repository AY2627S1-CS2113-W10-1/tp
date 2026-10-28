package seedu.tripjio.ui;

import seedu.tripjio.model.expense.Category;
import seedu.tripjio.util.MoneyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Formats monthly and trip category totals consistently with the comparison table. */
public final class SpendingTable {
    private static final String ROW_FORMAT = "%-15s %15s";
    private static final String TABLE_LINE = "-".repeat(31);

    private SpendingTable() {
    }

    /** Returns a fresh table in category order, summing the displayed amounts for its total. */
    public static String[] format(Map<Category, Double> totals, String amountHeading) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format(Locale.ROOT, ROW_FORMAT, "Category", amountHeading));
        lines.add(TABLE_LINE);
        double total = 0;
        for (Category category : Category.values()) {
            double amount = MoneyUtil.roundToCents(totals.get(category));
            lines.add(String.format(Locale.ROOT, ROW_FORMAT, category.getDisplayName(), MoneyUtil.format(amount)));
            total += amount;
        }
        lines.add(TABLE_LINE);
        lines.add(String.format(Locale.ROOT, ROW_FORMAT, "Total", MoneyUtil.format(MoneyUtil.roundToCents(total))));
        return lines.toArray(String[]::new);
    }
}
