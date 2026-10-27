package seedu.tripjio.model.expense;

import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpenseTest {

    @Test
    void toStorageString_thenFromStoragesString_sameExpense() throws TripJioException {
        Expense expense = new Expense("Lunch", 12.5, Category.FOOD,
                LocalDate.of(2026, 10, 15));

        Expense loaded = Expense.fromStorageString(expense.toStorageString());

        assertEquals("E|Lunch|12.50|FOOD|15-10-2026", expense.toStorageString());
        assertEquals("Lunch", loaded.getDescription());
        assertEquals(12.5, loaded.getAmount());
        assertEquals(Category.FOOD, loaded.getCategory());
        assertEquals(LocalDate.of(2026,10,15), loaded.getDate());
    }

    @Test
    void toString_displayFormat() {
        Expense expense = new Expense("Metro card", 1234.5, Category.TRANSPORT,
                LocalDate.of(2026, 10, 2));

        assertEquals("[Transport] Metro card - $1,234.50 (02-10-2026)", expense.toString());
        assertEquals(YearMonth.of(2026, 10), expense.getMonth());
    }

    @Test
    void fromStorageString_wrongTagOrFieldCount_throws() {
        for (String line : new String[] {
            "E|Lunch|12.50|FOOD",                     // 4 fields: date missing
            "E|Lunch|12.50|FOOD|15-10-2026|extra",    // 6 fields
            "E|Lunch|12.50|FOOD|",                    // empty date kept by split(..., -1)
            "S|Lunch|12.50|FOOD|15-10-2026",          // wrong tag
            ""}) {
            assertThrows(TripJioException.class, () -> Expense.fromStorageString(line), line);
        }
    }

    @Test
    void fromStorageString_missingDescription_throws() {
        for (String line : new String[] {"E||12.50|FOOD|15-10-2026", "E|   |12.50|FOOD|15-10-2026"}) {
            assertThrows(TripJioException.class, () -> Expense.fromStorageString(line), line);
        }
    }

    @Test
    void fromStorageString_invalidAmount_throws() {
        for (String amount : new String[] {"abc", "", "0", "-5", "1000000.01", "NaN"}) {
            String line = "E|Lunch|" + amount + "|FOOD|15-10-2026";
            assertThrows(TripJioException.class, () -> Expense.fromStorageString(line), line);
        }
    }

    @Test
    void fromStorageString_invalidCategory_throws() {
        for (String category : new String[] {"FOOOD", "food", ""}) {
            String line = "E|Lunch|12.50|" + category + "|15-10-2026";
            assertThrows(TripJioException.class, () -> Expense.fromStorageString(line), line);
        }
    }

    @Test
    void fromStorageString_invalidDate_throws() {
        for (String date : new String[] {"31-02-2026", "2026-10-15", "15/10/2026", "1-9-2026"}) {
            String line = "E|Lunch|12.50|FOOD|" + date;
            assertThrows(TripJioException.class, () -> Expense.fromStorageString(line), line);
        }
    }

    @Test
    void fromStorageString_boundaryAmountAndFutureDate_loaded() throws TripJioException {
        Expense expense = Expense.fromStorageString("E|  Hotel  |1000000.00|ACCOMMODATION|01-01-2099");

        assertEquals("Hotel", expense.getDescription());
        assertEquals(1_000_000, expense.getAmount());
        assertEquals(LocalDate.of(2099, 1, 1), expense.getDate());
    }

    @Test
    void setAmount_outOfRange_throwsAndKeepsOldAmount() throws TripJioException {
        Expense expense = new Expense("Lunch", 12.5, Category.FOOD, LocalDate.of(2026, 10, 15));

        assertThrows(TripJioException.class, () -> expense.setAmount(0));
        assertThrows(TripJioException.class, () -> expense.setAmount(1_000_000.01));
        assertEquals(12.5, expense.getAmount());

        expense.setAmount(20);
        assertEquals(20, expense.getAmount());
    }
}
