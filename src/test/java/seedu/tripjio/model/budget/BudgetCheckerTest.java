package seedu.tripjio.model.budget;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.trip.Trip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BudgetCheckerTest {

    private Budget budget;
    private final YearMonth oct2026 = YearMonth.of(2026, 10);
    private final YearMonth nov2026 = YearMonth.of(2026, 11);

    @BeforeEach
    void setUp() {
        budget = new Budget();
    }

    @Test
    void check_nullBudgetOrMonth_returnsNull() {
        assertNull(BudgetChecker.check(null, 100.00, oct2026));
        assertNull(BudgetChecker.check(budget, 100.00, null));
    }

    @Test
    void check_monthWithoutBudget_returnsNull() {
        // Neither default nor month budget set
        assertNull(BudgetChecker.check(budget, 500.00, oct2026));
    }

    @Test
    void check_justBelow80Percent_returnsNull() throws TripJioException {
        budget.setDefault(1000.00);
        // 80% of 1000 is 800; 799.99 is just below
        assertNull(BudgetChecker.check(budget, 799.99, oct2026));
    }

    @Test
    void check_exactly80Percent_returnsWarning() throws TripJioException {
        budget.setDefault(1000.00);
        String expected = "Warning: You have used 80% or more of your budget for October 2026! "
                + "Spent: $800.00 / $1,000.00 ($200.00 remaining).";
        assertEquals(expected, BudgetChecker.check(budget, 800.00, oct2026));
    }

    @Test
    void check_between80And100Percent_returnsWarning() throws TripJioException {
        budget.setDefault(1000.00);
        String expected = "Warning: You have used 80% or more of your budget for October 2026! "
                + "Spent: $950.00 / $1,000.00 ($50.00 remaining).";
        assertEquals(expected, BudgetChecker.check(budget, 950.00, oct2026));
    }

    @Test
    void check_exactly100Percent_returns80PercentWarningWithZeroRemaining() throws TripJioException {
        budget.setDefault(1000.00);
        String expected = "Warning: You have used 80% or more of your budget for October 2026! "
                + "Spent: $1,000.00 / $1,000.00 ($0.00 remaining).";
        assertEquals(expected, BudgetChecker.check(budget, 1000.00, oct2026));
    }

    @Test
    void check_exceededBudget_returnsExceededWarning() throws TripJioException {
        budget.setDefault(1000.00);
        String expected = "Warning: You have exceeded your budget for October 2026! "
                + "Spent: $1,050.00 / $1,000.00 ($50.00 over budget).";
        assertEquals(expected, BudgetChecker.check(budget, 1050.00, oct2026));
    }

    @Test
    void check_monthBudgetOverridesDefault_usesMonthBudgetThreshold() throws TripJioException {
        budget.setDefault(1000.00);
        budget.setForMonth(nov2026, 500.00);

        // For October (using default 1000.00), spent 450 is 45% -> null
        assertNull(BudgetChecker.check(budget, 450.00, oct2026));

        // For November (specific budget 500.00), spent 450 is 90% (>= 80% of 500 = 400) -> warning
        String expected = "Warning: You have used 80% or more of your budget for November 2026! "
                + "Spent: $450.00 / $500.00 ($50.00 remaining).";
        assertEquals(expected, BudgetChecker.check(budget, 450.00, nov2026));
    }

    @Test
    void checkTrip_nullTripOrBudget_returnsNull() {
        assertNull(BudgetChecker.checkTrip((Trip) null, "Japan", 100.00));
        assertNull(BudgetChecker.checkTrip((Double) null, "Japan", 100.00));
        Trip trip = new Trip();
        assertNull(BudgetChecker.checkTrip(trip, "Japan", 100.00));
    }

    @Test
    void checkTrip_justBelow80Percent_returnsNull() throws TripJioException {
        Trip trip = new Trip();
        trip.set(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10));
        trip.setBudget(1000.00);
        assertNull(BudgetChecker.checkTrip(trip, "Japan", 799.99));
    }

    @Test
    void checkTrip_exactly80Percent_returnsWarning() throws TripJioException {
        Trip trip = new Trip();
        trip.set(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10));
        trip.setBudget(1000.00);
        String expected = "Warning: You have used 80% or more of your budget for trip Japan! "
                + "Spent: $800.00 / $1,000.00 ($200.00 remaining).";
        assertEquals(expected, BudgetChecker.checkTrip(trip, "Japan", 800.00));
    }

    @Test
    void checkTrip_between80And100Percent_returnsWarning() throws TripJioException {
        Trip trip = new Trip();
        trip.set(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10));
        trip.setBudget(1000.00);
        String expected = "Warning: You have used 80% or more of your budget for trip Japan! "
                + "Spent: $950.00 / $1,000.00 ($50.00 remaining).";
        assertEquals(expected, BudgetChecker.checkTrip(trip, "Japan", 950.00));
    }

    @Test
    void checkTrip_exceededBudget_returnsExceededWarning() throws TripJioException {
        Trip trip = new Trip();
        trip.set(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 10));
        trip.setBudget(1000.00);
        String expected = "Warning: You have exceeded your budget for trip Japan! "
                + "Spent: $1,250.00 / $1,000.00 ($250.00 over budget).";
        assertEquals(expected, BudgetChecker.checkTrip(trip, "Japan", 1250.00));
    }

    @Test
    void checkTrip_blankTripName_usesFallbackName() {
        String expected = "Warning: You have exceeded your budget for your trip! "
                + "Spent: $600.00 / $500.00 ($100.00 over budget).";
        assertEquals(expected, BudgetChecker.checkTrip(500.00, null, 600.00));
        assertEquals(expected, BudgetChecker.checkTrip(500.00, "   ", 600.00));
    }
}
