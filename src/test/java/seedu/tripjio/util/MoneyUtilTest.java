package seedu.tripjio.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyUtilTest {
    private Locale originalLocale;

    @BeforeEach
    void saveLocale() {
        originalLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    @Test
    void format_addsDollarSignCommasAndTwoDecimals() {
        double input1 = 1234.5;
        double input2 = 0.5;
        double input3 = 1000000;
        double input4 = 5;
        double input5 = 999.999;
        double input6 = 0.1;

        assertEquals("$1,234.50", MoneyUtil.format(input1));
        assertEquals("$0.50", MoneyUtil.format(input2));
        assertEquals("$1,000,000.00", MoneyUtil.format(input3));
        assertEquals("$5.00", MoneyUtil.format(input4));
        assertEquals("$1,000.00", MoneyUtil.format(input5));
        assertEquals("$0.10", MoneyUtil.format(input6));
    }

    @Test
    void toStorageString_noDollarSignNoCommas() {
        assertEquals("1234.50", MoneyUtil.toStorageString(1234.5));
        assertEquals("1000000.00", MoneyUtil.toStorageString(1000000));
        assertEquals("0.50", MoneyUtil.toStorageString(0.5));
    }

    @Test
    void formatAndStorage_germanDefaultLocale_stillUseDotAndComma() {
        Locale.setDefault(Locale.GERMANY);

        assertEquals("$1,234.50", MoneyUtil.format(1234.5));
        assertEquals("1234.50", MoneyUtil.toStorageString(1234.5));
    }

    @Test
    void validateAmount_insideRange_noException() {
        assertDoesNotThrow(() -> MoneyUtil.validateAmount(0.01));
        assertDoesNotThrow(() -> MoneyUtil.validateAmount(1000000));
        assertDoesNotThrow(() -> MoneyUtil.validateAmount(50));
        assertDoesNotThrow(() -> MoneyUtil.validateAmount(MoneyUtil.MAX_AMOUNT));
    }

    @Test
    void validateAmount_outsideRange_throws() {
        assertThrows(TripJioException.class, () -> MoneyUtil.validateAmount(0));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateAmount(-5));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateAmount(1000000.01));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateAmount(Double.NaN));
    }

    @Test
    void roundToCents_roundsToNearestCent() {
        assertEquals(33.33, MoneyUtil.roundToCents(33.333));
        assertEquals(0.13, MoneyUtil.roundToCents(0.125));
        assertEquals(12.0, MoneyUtil.roundToCents(12.0));
    }

    @Test
    void validateOriginalAmount_validAmounts_noException() {
        assertDoesNotThrow(() -> MoneyUtil.validateOriginalAmount(0.01));
        assertDoesNotThrow(() -> MoneyUtil.validateOriginalAmount(12.5));
        assertDoesNotThrow(() -> MoneyUtil.validateOriginalAmount(12.50));
        assertDoesNotThrow(() -> MoneyUtil.validateOriginalAmount(2000000));
        assertDoesNotThrow(() -> MoneyUtil.validateOriginalAmount(1_000_000_000));
    }

    @Test
    void validateOriginalAmount_invalidAmounts_throws() {
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(0));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(-10));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(Double.NaN));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(Double.POSITIVE_INFINITY));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(12.345));
        assertThrows(TripJioException.class, () -> MoneyUtil.validateOriginalAmount(0.001));

        TripJioException exception1 = assertThrows(TripJioException.class,
                () -> MoneyUtil.validateOriginalAmount(0));
        assertEquals("Amount must be a finite number more than 0.", exception1.getMessage());

        TripJioException exception2 = assertThrows(TripJioException.class,
                () -> MoneyUtil.validateOriginalAmount(34.974));
        assertEquals("Amount must have at most 2 decimal places.", exception2.getMessage());
    }
}
