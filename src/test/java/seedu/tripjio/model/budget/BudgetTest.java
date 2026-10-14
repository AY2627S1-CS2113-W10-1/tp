package seedu.tripjio.model.budget;

import seedu.tripjio.exception.TripJioException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetTest {

    private Budget budget;

    @BeforeEach
    void setUp() {
        budget = new Budget();
    }

    @Test
    void getBudgetFor_noBudgetsSet_returnsNull() {
        assertNull(budget.getBudgetFor(YearMonth.of(2026, 10)));
    }

    @Test
    void getBudgetFor_onlyDefaultSet_returnsDefault() throws TripJioException {
        budget.setDefault(1000.00);
        assertEquals(1000.00, budget.getBudgetFor(YearMonth.of(2026, 10)));
        assertEquals(1000.00, budget.getBudgetFor(YearMonth.of(2026, 11)));
    }

    @Test
    void getBudgetFor_onlyMonthBudgetSet_returnsMonthBudgetOrNull() throws TripJioException {
        budget.setForMonth(YearMonth.of(2026, 10), 1500.00);
        assertEquals(1500.00, budget.getBudgetFor(YearMonth.of(2026, 10)));
        assertNull(budget.getBudgetFor(YearMonth.of(2026, 11)));
    }

    @Test
    void getBudgetFor_monthOverridesDefault_returnsCorrectValues() throws TripJioException {
        budget.setDefault(1000.00);
        budget.setForMonth(YearMonth.of(2026, 12), 1500.00);

        // Specific month overrides default
        assertEquals(1500.00, budget.getBudgetFor(YearMonth.of(2026, 12)));
        // Other month uses default
        assertEquals(1000.00, budget.getBudgetFor(YearMonth.of(2026, 11)));
    }

    @Test
    void setDefault_invalidAmounts_throwsException() {
        assertThrows(TripJioException.class, () -> budget.setDefault(0));
        assertThrows(TripJioException.class, () -> budget.setDefault(-10));
        assertThrows(TripJioException.class, () -> budget.setDefault(1_000_000.01));
    }

    @Test
    void setForMonth_invalidAmounts_throwsException() {
        YearMonth month = YearMonth.of(2026, 10);
        assertThrows(TripJioException.class, () -> budget.setForMonth(month, 0));
        assertThrows(TripJioException.class, () -> budget.setForMonth(month, -10));
        assertThrows(TripJioException.class, () -> budget.setForMonth(month, 1_000_000.01));
    }

    @Test
    void toStorageLines_emptyBudget_returnsEmptyList() {
        assertTrue(budget.toStorageLines().isEmpty());
    }

    @Test
    void toStorageLines_onlyDefaultSet_returnsDefaultLine() throws TripJioException {
        budget.setDefault(1000.00);
        assertEquals(List.of("default=1000.00"), budget.toStorageLines());
    }

    @Test
    void toStorageLines_defaultAndMonths_returnsDefaultFirstThenChronologicalMonths() throws TripJioException {
        budget.setDefault(1000.00);
        budget.setForMonth(YearMonth.of(2026, 12), 2000.00);
        budget.setForMonth(YearMonth.of(2026, 10), 1500.00);

        // Months should be sorted chronologically: 10-2026 before 12-2026
        List<String> expected = List.of(
                "default=1000.00",
                "10-2026=1500.00",
                "12-2026=2000.00"
        );
        assertEquals(expected, budget.toStorageLines());
    }

    @Test
    void loadStorageLine_validDefaultLine_setsDefaultBudget() throws TripJioException {
        budget.loadStorageLine("default=1000.00");
        assertEquals(1000.00, budget.getDefaultBudget());
        assertEquals(1000.00, budget.getBudgetFor(YearMonth.of(2026, 10)));
    }

    @Test
    void loadStorageLine_validMonthLine_setsMonthBudget() throws TripJioException {
        budget.loadStorageLine("10-2026=1500.00");
        assertEquals(1500.00, budget.getBudgetFor(YearMonth.of(2026, 10)));
        assertNull(budget.getBudgetFor(YearMonth.of(2026, 11)));
    }

    @Test
    void loadStorageLine_roundTrip_matchesToStorageLines() throws TripJioException {
        budget.setDefault(1000.00);
        budget.setForMonth(YearMonth.of(2026, 10), 1500.00);
        budget.setForMonth(YearMonth.of(2026, 12), 2000.00);

        List<String> lines = budget.toStorageLines();

        Budget loadedBudget = new Budget();
        for (String line : lines) {
            loadedBudget.loadStorageLine(line);
        }

        assertEquals(lines, loadedBudget.toStorageLines());
        assertEquals(budget.getBudgetFor(YearMonth.of(2026, 10)), loadedBudget.getBudgetFor(YearMonth.of(2026, 10)));
        assertEquals(budget.getBudgetFor(YearMonth.of(2026, 11)), loadedBudget.getBudgetFor(YearMonth.of(2026, 11)));
        assertEquals(budget.getBudgetFor(YearMonth.of(2026, 12)), loadedBudget.getBudgetFor(YearMonth.of(2026, 12)));
    }

    @Test
    void loadStorageLine_invalidLines_throwsException() {
        assertThrows(TripJioException.class, () -> budget.loadStorageLine(null));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine(""));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("invalidLineWithoutEquals"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default=100=200"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("=1000"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default="));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default=abc"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default=-50"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default=0"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("default=1000000.01"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("unknownKey=1000"));
        assertThrows(TripJioException.class, () -> budget.loadStorageLine("13-2026=1000"));
    }
}

