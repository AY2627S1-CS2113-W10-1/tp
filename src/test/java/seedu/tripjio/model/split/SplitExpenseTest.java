package seedu.tripjio.model.split;

import org.junit.jupiter.api.Test;
import seedu.tripjio.model.expense.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SplitExpense}.
 */
public class SplitExpenseTest {

    @Test
    public void getMyShare_noCustomShare_returnsEqualSplit() {
        SplitExpense expense = new SplitExpense("Taxi", 60.0, 3, null, Category.OTHER, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        assertEquals(20.0, expense.getMyShare());
    }

    @Test
    public void getMyShare_customShare_returnsCustomShare() {
        SplitExpense expense = new SplitExpense("Taxi", 60.0, 3, 15.0, Category.OTHER, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        assertEquals(15.0, expense.getMyShare());
    }

    @Test
    public void setCustomShare_updatesShareAndFlag() {
        SplitExpense expense = new SplitExpense("Taxi", 60.0, 3, null, Category.OTHER, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        expense.setCustomShare(25.0);
        assertTrue(expense.hasCustomShare());
        assertEquals(25.0, expense.getMyShare());
    }

    @Test
    public void getAmountsOwedToMe_noPayer_distributesLeftoverCents() {
        // $100 / 3 people. My share = 33.33. Rest = 66.67.
        // 2 people -> Alice pays 33.34, Bob pays 33.33 to ensure exact rounding.
        SplitExpense expense = new SplitExpense("Dinner", 100.0, 3, null, Category.FOOD, LocalDate.now(), 
                                                Arrays.asList("alice", "bob"), null, null, null);
        Map<String, Double> owed = expense.getAmountsOwedToMe();
        assertEquals(33.34, owed.get("alice"));
        assertEquals(33.33, owed.get("bob"));
    }

    @Test
    public void getAmountsOwedToMe_withPayer_returnsEmpty() {
        SplitExpense expense = new SplitExpense("Dinner", 100.0, 3, null, Category.FOOD, LocalDate.now(), 
                                                Arrays.asList("alice", "bob"), "alice", null, null);
        assertTrue(expense.getAmountsOwedToMe().isEmpty());
    }

    @Test
    public void getAmountsOwedToMe_noParticipants_returnsEmpty() {
        SplitExpense expense = new SplitExpense("Dinner", 100.0, 3, null, Category.FOOD, LocalDate.now(), 
                                                new ArrayList<>(), null, null, null);
        assertTrue(expense.getAmountsOwedToMe().isEmpty());
    }

    @Test
    public void foreignCurrency_convertsToSgdCorrectly() {
        // 1000 JPY at 0.009 = 9.00 SGD. Split 3 ways = 3.00 SGD (share).
        SplitExpense expense = new SplitExpense("Snacks", 1000.0, 3, null, Category.FOOD, LocalDate.now(), 
                                                new ArrayList<>(), null, "JPY", 0.009);
        assertEquals(9.00, expense.getTotalAmount());
        assertEquals(3.00, expense.getMyShare());
    }
}
