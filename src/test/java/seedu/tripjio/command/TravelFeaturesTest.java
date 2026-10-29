package seedu.tripjio.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ForeignExpense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.storage.Storage;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Integration tests for sorting, currency snapshots and legacy-compatible named trips. */
class TravelFeaturesTest {
    @TempDir
    Path tempDir;
    private final AppState state = new AppState();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

    private void run(String input) throws TripJioException {
        Parser.parse(input).execute(state, ui);
    }

    @Test
    void sorting_preservesIndexesStorageOrderAndSplitShares() throws TripJioException {
        LocalDate date = LocalDate.of(2020, 1, 1);
        state.getExpenses().add(new Expense("Small", 5, Category.FOOD, date));
        state.getExpenses().add(new Expense("Large", 20, Category.FOOD, date.plusDays(1)));
        state.getExpenses().add(new SplitExpense("Split", 30, 3, null, Category.FOOD, 
                date, new ArrayList<>(), null, null, null));
        run("list sort/amount");
        String text = output.toString();
        assertTrue(text.indexOf("2. ") < text.indexOf("3. "));
        assertTrue(text.indexOf("3. ") < text.indexOf("1. "));
        assertEquals("Small", state.getExpenses().get(1).getDescription());
        assertFalse(Parser.parse("list sort/date order/asc").isMutating());
        output.reset();
        run("list c/food m/01-2020 sort/date order/asc");
        text = output.toString();
        assertTrue(text.indexOf("1. ") < text.indexOf("3. "));
        assertTrue(text.indexOf("3. ") < text.indexOf("2. "));
        run("edit 2 a/25");
        assertEquals(25, state.getExpenses().get(2).getAmount());
    }

    @Test
    void invalidNewArguments_areRejected() {
        for (String command : new String[] {"list sort/name", "list order/desc", "list sort/amount order/up",
            "list sort/date sort/amount", "list sort/", "list sort/date x/extra", "rate set from/EUR to/USD r/1",
            "rate set from/EUR to/SGD r/0", "rate set from/EUR to/SGD r/NaN",
            "rate set from/EUR to/SGD r/1e3", "rate set from/EUR to/SGD r/1.123456789",
            "rate list r/1", "rate set from/EUR from/USD to/SGD r/1",
            "add d/Food a/5 cur/INVALID", "split d/Food a/5 n/2 cur/EUR",
            "budget set a/5 cur/EUR", "stats cur/EUR", "trip list extra",
            "trip select 0", "trip select 1 extra", "trip add n/ s/01-01-2020 e/02-01-2020"}) {
            assertThrows(TripJioException.class, () -> Parser.parse(command), command);
        }
    }

    @Test
    void conversion_retainsRateWhenEditedAndWhenRatesChange() throws TripJioException {
        run("rate set from/eur to/SGD r/1.45");
        run("add d/Lunch a/12 c/food dt/01-01-2020 cur/EUR");
        assertInstanceOf(ForeignExpense.class, state.getExpenses().get(1));
        assertEquals(17.40, state.getExpenses().get(1).getAmount());
        run("rate set from/EUR to/SGD r/2");
        assertEquals(17.40, state.getExpenses().get(1).getAmount());
        run("edit 1 a/10");
        assertEquals(14.50, state.getExpenses().get(1).getAmount());
        assertTrue(state.getExpenses().get(1).toString().contains("EUR 10.00 at 1.45"));
        run("add d/New a/10 dt/01-01-2020 cur/EUR");
        assertEquals(34.50, state.getExpenses().getTotalSpentInMonth(YearMonth.of(2020, 1)));
        output.reset();
        run("stats m/01-2020");
        assertTrue(output.toString().contains(String.format("%-15s %15s", "Total", "$34.50")));
    }

    @Test
    void failedConversion_doesNotAddOrPartiallyEdit() throws TripJioException {
        assertThrows(TripJioException.class, () -> run("add d/Lunch a/12 cur/EUR"));
        assertEquals(0, state.getExpenses().size());
        run("rate set from/EUR to/SGD r/1000000");
        assertThrows(TripJioException.class, () -> run("add d/Lunch a/12 cur/EUR"));
        run("rate set from/EUR to/SGD r/0.00000001");
        assertThrows(TripJioException.class, () -> run("add d/Lunch a/0.01 cur/EUR"));
        run("rate set from/EUR to/SGD r/2");
        run("add d/Lunch a/10 cur/EUR");
        assertThrows(TripJioException.class, () -> run("edit 1 a/1000000 d/Changed"));
        assertEquals(20, state.getExpenses().get(1).getAmount());
        assertEquals("Lunch", state.getExpenses().get(1).getDescription());
        assertThrows(TripJioException.class, () -> run("rate set from/SGD to/SGD r/2"));
    }

    @Test
    void trips_addSelectSetAndInvalidUpdates_preserveOtherTrips() throws TripJioException {
        run("trip add n/Japan s/01-01-2020 e/10-01-2020");
        run("trip add n/Korea s/01-02-2020 e/10-02-2020");
        assertEquals(LocalDate.of(2020, 2, 1), state.getTrip().getStart());
        run("trip select 1");
        run("trip set s/02-01-2020 e/09-01-2020");
        assertThrows(TripJioException.class, () -> run("trip add n/japan s/01-03-2020 e/02-03-2020"));
        assertThrows(TripJioException.class, () -> run("trip add n/Bad s/02-03-2020 e/01-03-2020"));
        assertThrows(TripJioException.class, () -> run("trip select 3"));
        assertEquals(LocalDate.of(2020, 1, 2), state.getTrip().getStart());
        run("trip select 2");
        assertEquals(LocalDate.of(2020, 2, 1), state.getTrip().getStart());
        run("trip list");
        assertTrue(output.toString().contains("2. Korea (01-02-2020 to 10-02-2020) [selected]"));
    }

    @Test
    void tripStatus_usesSelectedDateRange() throws TripJioException {
        run("add d/January a/10 dt/05-01-2020");
        run("add d/February a/20 dt/05-02-2020");
        run("trip add n/January s/01-01-2020 e/31-01-2020");
        run("trip add n/February s/01-02-2020 e/29-02-2020");
        output.reset();
        run("trip status");
        assertTrue(output.toString().contains("Recorded spending during trip: $20.00"));
        run("trip select 1");
        output.reset();
        run("trip status");
        assertTrue(output.toString().contains("Recorded spending during trip: $10.00"));
    }

    @Test
    void storage_roundTripRestoresRatesSnapshotsTripsAndSelection() throws Exception {
        run("rate set from/EUR to/SGD r/1.45");
        run("add d/Café a/12 cur/EUR dt/01-01-2020");
        run("trip add n/Japan s/01-01-2020 e/31-01-2020");
        run("trip add n/Korea s/01-02-2020 e/29-02-2020");
        run("trip select 1");
        run("rate set from/EUR to/SGD r/2");
        Storage storage = new Storage(tempDir.resolve("data.txt").toString());
        storage.save(state);
        AppState loaded = storage.load(ui);
        assertEquals(17.40, loaded.getExpenses().get(1).getAmount());
        assertEquals("2", loaded.getExchangeRates().get("EUR").toPlainString());
        assertEquals(state.getTrips().displayLines(), loaded.getTrips().displayLines());
        assertEquals(LocalDate.of(2020, 1, 1), loaded.getTrip().getStart());
        storage.save(loaded);
        assertEquals(2, storage.load(ui).getTrips().displayLines().size());
    }

    @Test
    void storage_legacyTripMigratesWithoutLoss() throws Exception {
        Path path = tempDir.resolve("legacy.txt");
        Files.writeString(path, "[expense]\nE|Lunch|5.00|FOOD|01-01-2020\n"
                + "[trip]\nstart=01-01-2020\nend=02-01-2020\n");
        Storage storage = new Storage(path.toString());
        AppState loaded = storage.load(ui);
        Parser.parse("trip add n/New s/01-02-2020 e/02-02-2020").execute(loaded, ui);
        assertEquals(2, loaded.getTrips().displayLines().size());
        loaded.getTrips().select(1);
        assertEquals(LocalDate.of(2020, 1, 1), loaded.getTrip().getStart());
        assertEquals(5, loaded.getExpenses().get(1).getAmount());
        storage.save(loaded);
        assertEquals(loaded.getTrips().displayLines(), storage.load(ui).getTrips().displayLines());
    }

    @Test
    void foreignAmountsAboveMillion_addEditAndReloadWithSgdOnlyCap() throws Exception {
        run("rate set from/JPY to/SGD r/0.009");
        run("add d/Japan a/2000000 cur/JPY dt/01-01-2020");
        assertEquals(18000, state.getExpenses().get(1).getAmount());
        run("edit 1 a/3000000");
        assertEquals(27000, state.getExpenses().get(1).getAmount());
        Storage storage = new Storage(tempDir.resolve("large.txt").toString());
        storage.save(state);
        AppState loaded = storage.load(ui);
        assertEquals(27000, loaded.getExpenses().get(1).getAmount());
        assertTrue(loaded.getExpenses().get(1).toString().contains("JPY 3000000.00"));
        run("rate set from/JPY to/SGD r/0.5");
        run("add d/Boundary a/2000000 cur/JPY");
        assertEquals(1000000, state.getExpenses().get(2).getAmount());
        assertThrows(TripJioException.class, () -> run("add d/Over a/2000000.02 cur/JPY"));
        assertThrows(TripJioException.class, () -> run("edit 2 a/2000000.02 d/Changed"));
        assertEquals("Boundary", state.getExpenses().get(2).getDescription());
        assertEquals(1000000, state.getExpenses().get(2).getAmount());
    }

    @Test
    void sgdCapsAndInvalidForeignAmounts_remainRejected() throws Exception {
        run("rate set from/JPY to/SGD r/0.009");
        for (String amount : new String[] {"0", "-1", "NaN", "Infinity", "2000000.001", "9".repeat(400)}) {
            assertThrows(TripJioException.class, () -> run("add d/Bad a/" + amount + " cur/JPY"));
        }
        assertThrows(TripJioException.class, () -> run("add d/Over a/1000000.01"));
        assertThrows(TripJioException.class, () -> run("add d/Over a/1000000.01 cur/SGD"));
        run("add d/Normal a/10");
        assertThrows(TripJioException.class, () -> run("edit 1 a/1000000.01 d/Changed"));
        assertEquals("Normal", state.getExpenses().get(1).getDescription());
        run("split d/Shared a/100 n/2");
        assertThrows(TripJioException.class, () -> run("edit 2 a/1000000.01"));
        assertEquals(50, state.getExpenses().get(2).getAmount());
        assertThrows(TripJioException.class, () -> ForeignExpense.fromStorageString(
                "F|Over|2000000.02|FOOD|01-01-2020|JPY|0.5"));
        assertThrows(TripJioException.class, () -> ForeignExpense.fromStorageString(
                "F|Invalid|Infinity|FOOD|01-01-2020|JPY|0.5"));
    }

    @Test
    void storage_invalidNewRecords_warnAndKeepValidData() throws Exception {
        Path path = tempDir.resolve("invalid.txt");
        Files.writeString(path, "[rates]\nEUR=0\nUSD=1.3\n[trips]\nT|Bad|02-01-2020|01-01-2020\n"
                + "T|Good|01-01-2020|02-01-2020\nA|Missing\n[expense]\n"
                + "F|Bad|5.00|FOOD|01-01-2020|EUR|NaN\nE|Good|5.00|FOOD|01-01-2020\n");
        AppState loaded = new Storage(path.toString()).load(ui);
        assertEquals(1, loaded.getExpenses().size());
        assertEquals(1, loaded.getTrips().displayLines().size());
        assertEquals("1.3", loaded.getExchangeRates().get("USD").toPlainString());
        assertTrue(output.toString().contains("Skipped invalid line"));
    }
}
