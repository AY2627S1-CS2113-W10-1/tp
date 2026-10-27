package seedu.tripjio.model.split;

import org.junit.jupiter.api.Test;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SplitExpense}.
 */
public class SplitExpenseTest {

    private final LocalDate testDate = LocalDate.of(2026, 10, 14);

    @Test
    public void toString_equalSplit_formattedCorrectly() {
        SplitExpense expense = new SplitExpense("Group dinner", 90.00, 3, null, Category.FOOD, testDate);
        assertEquals("[Food] Group dinner - $30.00 (14-10-2026) [Split: $90.00 / 3 people]", expense.toString());
    }

    @Test
    public void setAmount_equalSplit_recalculatesShare() throws TripJioException {
        SplitExpense expense = new SplitExpense("Group dinner", 90.00, 3, null, Category.FOOD, testDate);
        expense.setAmount(120.00);
        assertEquals(40.00, expense.getMyShare());
        assertEquals(120.00, expense.getTotalAmount());
    }

    @Test
    public void setAmount_customShare_keepsOwnShare() throws TripJioException {
        SplitExpense expense = new SplitExpense("Group dinner", 100.00, 4, 40.00, Category.FOOD, testDate);
        expense.setAmount(150.00);
        assertEquals(40.00, expense.getMyShare());
        assertEquals(150.00, expense.getTotalAmount());
    }

    @Test
    public void setAmount_customShareBelowOwnShare_throwsException() {
        SplitExpense expense = new SplitExpense("Group dinner", 100.00, 4, 40.00, Category.FOOD, testDate);
        TripJioException e = assertThrows(TripJioException.class, () -> expense.setAmount(30.00));
        assertEquals("The new total bill cannot be less than your own share of $40.00.", e.getMessage());
    }

    @Test
    public void setAmount_equalSplitTooSmall_throwsException() {
        SplitExpense expense = new SplitExpense("Snack", 10.00, 50, null, Category.FOOD, testDate);
        TripJioException e = assertThrows(TripJioException.class, () -> expense.setAmount(0.05));
        assertEquals("Each person's share must be at least $0.01.", e.getMessage());
    }

    @Test
    public void storage_roundTrip_equalAndCustom() throws TripJioException {
        SplitExpense equalExpense = new SplitExpense("Group dinner", 90.00, 3, null, Category.FOOD, testDate);
        String equalStorage = equalExpense.toStorageString();
        SplitExpense restoredEqual = SplitExpense.fromStorageString(equalStorage);
        assertEquals(equalExpense.getTotalAmount(), restoredEqual.getTotalAmount());
        assertEquals(equalExpense.getMyShare(), restoredEqual.getMyShare());
        assertFalse(restoredEqual.hasCustomShare());

        SplitExpense customExpense = new SplitExpense("Party", 200.00, 5, 80.00, Category.OTHER, testDate);
        String customStorage = customExpense.toStorageString();
        SplitExpense restoredCustom = SplitExpense.fromStorageString(customStorage);
        assertEquals(customExpense.getTotalAmount(), restoredCustom.getTotalAmount());
        assertEquals(customExpense.getMyShare(), restoredCustom.getMyShare());
        assertTrue(restoredCustom.hasCustomShare());
    }

    @Test
    public void fromStorageString_invalidFormat_throwsException() {
        assertThrows(TripJioException.class, () -> SplitExpense.fromStorageString(null));
        assertThrows(TripJioException.class, () -> SplitExpense.fromStorageString("S|Dinner|100.00|3|33.33|FOOD"));
        assertThrows(TripJioException.class, () -> SplitExpense.fromStorageString("E|Dinner|100.00|FOOD|14-10-2026"));
    }
}
