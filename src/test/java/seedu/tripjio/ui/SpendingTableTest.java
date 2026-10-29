package seedu.tripjio.ui;

import org.junit.jupiter.api.Test;
import seedu.tripjio.model.expense.Category;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks stable table layout, empty categories and large values without mutating totals. */
class SpendingTableTest {
    @Test
    void format_zeroTotals_hasAllCategoriesAndSeparatedTotal() {
        Map<Category, Double> totals = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            totals.put(category, 0.0);
        }
        String[] lines = SpendingTable.format(totals, "Amount (SGD)");
        assertEquals(10, lines.length);
        assertEquals("Category           Amount (SGD)", lines[0]);
        assertEquals("-".repeat(31), lines[1]);
        assertEquals("-".repeat(31), lines[8]);
        assertEquals(String.format("%-15s %15s", "Total", "$0.00"), lines[9]);
        for (int i = 0; i < Category.values().length; i++) {
            assertTrue(lines[i + 2].startsWith(Category.values()[i].getDisplayName()));
            assertEquals(31, lines[i + 2].length());
        }
    }

    @Test
    void format_largeTotals_doesNotTruncateOrMutateInput() {
        Map<Category, Double> totals = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            totals.put(category, 1_000_000_000.0);
        }
        Map<Category, Double> original = new EnumMap<>(totals);
        String[] lines = SpendingTable.format(totals, "Oct 2026");
        assertTrue(lines[2].contains("$1,000,000,000.00"));
        assertTrue(lines[9].contains("$6,000,000,000.00"));
        assertEquals(original, totals);
    }
}
