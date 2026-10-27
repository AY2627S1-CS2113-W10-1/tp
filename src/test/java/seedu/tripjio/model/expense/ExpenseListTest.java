package seedu.tripjio.model.expense;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseListTest {
    private ExpenseList list;

    @BeforeEach
    void setUp() {
        list = new ExpenseList();
    }

    private static Expense expense(double amount, int year, int month, int day) {
        return new Expense("Item", amount, Category.OTHER, LocalDate.of(year, month, day));
    }

    @Test
    void get_emptyList_throwsNoExpensesYet() {
        TripJioException exception = assertThrows(TripJioException.class, () -> list.get(1));
        assertEquals("You have no expenses yet.", exception.getMessage());
    }

    @Test
    void get_indexZeroOrAfterLast_throws() {
        list.add(expense(12, 2026, 10, 16));
        list.add(expense(30, 2026, 10, 20));

        assertThrows(TripJioException.class, () -> list.get(0));
        assertThrows(TripJioException.class, () -> list.get(-1));

        TripJioException exception = assertThrows(TripJioException.class, () -> list.get(3));
        assertEquals("Invalid index: 3. You have 2 expenses.", exception.getMessage());
    }

    @Test
    void get_validIndex_oneBased() throws TripJioException {
        Expense first = expense(12, 2026, 10, 16);
        Expense second = expense(50, 2026, 10, 22);

        list.add(first);
        list.add(second);

        assertSame(first, list.get(1));
        assertSame(second, list.get(2));
    }

    @Test
    void delete_returnsRemovedAndShiftsLaterIndexes() throws TripJioException {
        Expense first = expense(12, 2026, 10, 16);
        Expense second = expense(50, 2026, 10, 22);
        Expense third = expense(30, 2026, 10, 23);

        list.add(first);
        list.add(second);
        list.add(third);

        Expense removed = list.delete(2);

        assertSame(second, removed);
        assertEquals(2, list.size());
        assertSame(third, list.get(2));
    }

    @Test
    void getAll_returnsCopy() throws TripJioException {
        Expense first = expense(12, 2026, 10, 16);
        Expense second = expense(50, 2026, 10, 22);
        Expense third = expense(30, 2026, 10, 23);

        list.add(first);
        list.add(second);
        list.add(third);

        List<Expense> copy = list.getAll();
        int size = list.size();
        copy.clear();

        assertEquals(size, list.size());
        assertSame(first, list.get(1));
    }

    @Test
    void getTotalSpentInMonth_includesWholeMonthOnly() {
        list.add(expense(1, 2026, 9, 30));
        list.add(expense(10, 2026, 10, 1));
        list.add(expense(100, 2026, 10, 15));
        list.add(expense(1000, 2026, 10, 31));
        list.add(expense(10000, 2026, 11, 1));
        list.add(expense(100000, 2025, 10, 15));

        YearMonth october = YearMonth.of(2026,10);
        assertEquals(1110, list.getTotalSpentInMonth(october));
    }

    @Test
    void getTotalSpentBetween_includesBothEndDates() {
        LocalDate start = LocalDate.of(2026, 10, 25);
        LocalDate end = LocalDate.of(2026, 11, 5);

        list.add(expense(1, 2026, 10, 24));
        list.add(expense(10, 2026, 10, 25));
        list.add(expense(100, 2026, 10, 31));
        list.add(expense(1000, 2026, 11, 1));
        list.add(expense(10000, 2026, 11, 5));
        list.add(expense(100000, 2026, 11, 6));

        assertEquals(11110, list.getTotalSpentBetween(start, end));
    }

    @Test
    void getExpensesBetween_endBeforeStart_throws() {
        LocalDate start = LocalDate.of(2026, 10, 25);
        LocalDate end = LocalDate.of(2026, 10, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> list.getExpensesBetween(start, end));
        assertEquals("A valid start and end date are required.", exception.getMessage());
    }
}
