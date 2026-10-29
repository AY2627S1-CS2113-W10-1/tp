package seedu.tripjio.model.split;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link SplitExpense}.
 */
public class SplitExpenseTest {

    @Test
    public void fromStorageString_invalidDescription_rejectsLegacyAndExtendedRecords() {
        for (String description : new String[] {"", "   ", "Dinner/Taxi"}) {
            String legacy = "S|" + description + "|90.00|3|30.00|FOOD|14-10-2026";
            assertThrows(TripJioException.class, () -> SplitExpense.fromStorageString(legacy));
            assertThrows(TripJioException.class,
                    () -> SplitExpense.fromStorageString(legacy + "||alice,bob|||false"));
        }
    }

    @Test
    public void fromStorageString_validDescription_preservesUnicodeAndTrimsWhitespace() throws TripJioException {
        SplitExpense expense = SplitExpense.fromStorageString("S|  Phở dinner  |90.00|3|30.00|FOOD|14-10-2026");
        assertEquals("Phở dinner", expense.getDescription());
        assertEquals(90.00, expense.getTotalAmount());
        assertEquals(30.00, expense.getMyShare());
    }

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
    public void setCustomShare_updatesShareAndFlag() throws TripJioException {
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
        SplitExpense expense = new SplitExpense("Snacks", 1000.0, 3, null,
                Category.FOOD, LocalDate.now(),
                                                new ArrayList<>(), null, "JPY", 0.009);
        assertEquals(9.00, expense.getTotalAmount());
        assertEquals(3.00, expense.getMyShare());
    }

    @Test
    void toCsvFields_equalSplit_showsShareBillAndPeople() {
        SplitExpense split = new SplitExpense("Group dinner", 50, 5, null,
                Category.FOOD, LocalDate.of(2026, 10, 16));

        assertEquals(List.of("5", "2026-10-16", "Group dinner", "Food", "10.00", "Split", "50.00", "5",
                "", "", ""), split.toCsvFields(5));
    }

    @Test
    void toCsvFields_customShare_amountIsMyShareNotEqualShare() {
        // my/40 of a 120 bill for 4 people: the equal share would be 30, but only 40 is the user's spending.
        SplitExpense split = new SplitExpense("Hotpot", 120, 4, 40.0,
                Category.FOOD, LocalDate.of(2026, 10, 13));

        assertEquals(List.of("5", "2026-10-13", "Hotpot", "Food", "40.00", "Split",
                "120.00", "4", "", "", ""), split.toCsvFields(5));
    }

    @Test
    void toCsvFields_foreignSplit_showsSgdBillAndCurrency() {
        SplitExpense splitExpense = new SplitExpense("Entrance tickets", 40, 4, null,
                Category.ENTERTAINMENT, LocalDate.of(2026, 10, 16), List.of("John", "Jeanette", "Titus"),
                null, "EUR", 1.45);

        assertEquals(List.of("7", "2026-10-16", "Entrance tickets", "Entertainment",
                "14.50", "Split", "58.00", "4", "", "EUR", "1.45"), splitExpense.toCsvFields(7));
    }

    @Test
    void toCsvFields_wholeNumberRate_noTrailingZero() {
        // rate/1 is stored as the Double 1.0, but the CSV should show "1", like ForeignExpense does.
        SplitExpense split = new SplitExpense("Taxi", 30, 3, null,
                Category.TRANSPORT, LocalDate.of(2026, 10, 16), List.of("John", "Titus"),
                null, "EUR", 1.0);

        assertEquals("1", split.toCsvFields(1).get(10));
    }

    @Test
    void toCsvFields_tinyRate_noScientificNotation() {
        // VND 1,000 at 0.0001 SGD/VND = SGD 0.10, so each of 2 people pays 0.05 (above the $0.01 minimum).
        // String.valueOf(0.0001) would give "1.0E-4", which spreadsheets may misread.
        SplitExpense split = new SplitExpense("Street food", 1000, 2, null,
                Category.FOOD, LocalDate.of(2026, 10, 16), List.of("John"),
                null, "VND", 0.0001);

        assertEquals("0.0001", split.toCsvFields(1).get(10));
    }
}
