package seedu.tripjio.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.split.Settlement;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.parser.ArgumentParser;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.storage.Storage;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression tests for the data loss, command routing and split validation reported in PR 15. */
class ReviewFeedbackTest {
    @TempDir
    Path tempDir;
    private AppState state;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        state = new AppState();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    private void run(String input) throws TripJioException {
        Parser.parse(input).execute(state, ui);
    }

    /** Simulates restarting with the saved state; warnings fail the test. */
    private void restart() throws TripJioException {
        Storage storage = new Storage(tempDir.resolve("tripjio.txt").toString());
        storage.save(state);
        output.reset();
        state = storage.load(ui);
        assertEquals("", output.toString());
    }

    @Test
    void foreignSplit_halfCentConversionMatchesPersonalExpenseAndSurvivesRestart() throws TripJioException {
        run("rate set from/USD to/SGD r/1.005");
        run("add d/Personal a/1 cur/USD");
        run("split d/Shared a/1 n/2 p/Alice cur/USD rate/1.005");

        SplitExpense split = (SplitExpense) state.getExpenses().get(2);
        assertEquals(1.01, state.getExpenses().get(1).getAmount());
        assertEquals(state.getExpenses().get(1).getAmount(), split.getTotalAmount());
        assertEquals(0.51, split.getMyShare());
        assertEquals(0.50, split.getAmountsOwedToMe().get("alice"));

        restart();
        split = (SplitExpense) state.getExpenses().get(2);
        assertEquals(1.01, split.getTotalAmount());
        assertEquals(0.51, split.getMyShare());
    }

    @Test
    void foreignSplit_halfCentAtMinimumDoesNotRejectValidShare() throws TripJioException {
        run("split d/Small a/1 n/4 cur/USD rate/0.015");
        SplitExpense split = (SplitExpense) state.getExpenses().get(1);
        assertEquals(0.02, split.getTotalAmount());
        assertEquals(0.01, split.getMyShare());
    }

    @Test
    void splitSaveRestart_keepsBillAndShare() throws TripJioException {
        run("split d/Dinner a/90 n/3");
        restart();
        SplitExpense split = assertInstanceOf(SplitExpense.class, state.getExpenses().get(1));
        assertEquals(90, split.getTotalAmount());
        assertEquals(30, split.getAmount());
        assertEquals(3, split.getNumberOfPeople());
        assertFalse(split.hasCustomShare());
        run("split list");
        assertTrue(output.toString().contains("Dinner"));
    }

    @Test
    void namedForeignSplitRestart_preservesEveryFieldAndDebt() throws TripJioException {
        run("split d/Dinner a/100 n/3 p/Alice,Bob paid/ALICE cur/EUR rate/1.45 my/45 c/food dt/14-10-2026");
        String saved = state.getExpenses().get(1).toStorageString();
        restart();
        SplitExpense split = (SplitExpense) state.getExpenses().get(1);
        assertEquals(saved, split.toStorageString());
        assertEquals(145, split.getTotalAmount());
        assertEquals(45, split.getMyShare());
        assertEquals(List.of("alice", "bob"), split.getParticipantNames());
        assertEquals("alice", split.getPayerName());
        assertEquals("EUR", split.getCurrency());
        assertEquals(1.45, split.getExchangeRate());
        assertEquals(Category.FOOD, split.getCategory());
        assertEquals(LocalDate.of(2026, 10, 14), split.getDate());
        run("owe p/ALICE");
        assertTrue(output.toString().contains("You owe alice: $45.00"));
        run("settle p/alice a/5");
        restart();
        run("owe p/alice");
        assertTrue(output.toString().contains("You owe alice: $40.00"));
    }

    @Test
    void incomingDebt_partialAndFullSettlementsSurviveRestart() throws TripJioException {
        run("split d/Dinner a/100 n/3 p/Alice,Bob");
        run("settle p/ALICE a/10");
        restart();
        run("owe p/alice");
        assertTrue(output.toString().contains("alice owes you: $23.34"));
        assertFalse(output.toString().contains("bob owes you"));
        run("settle p/alice");
        restart();
        run("owe p/alice");
        assertTrue(output.toString().contains("All debts are settled!"));
        output.reset();
        run("owe");
        assertTrue(output.toString().contains("bob owes you: $33.33"));
    }

    @Test
    void outgoingDebt_fullSettlementHasNegativeSign() throws TripJioException {
        run("split d/Taxi a/60 n/2 p/Alice paid/alice");
        run("settle p/alice");
        assertEquals(-30, state.getSettlements().getFirst().getAmount());
        restart();
        run("owe");
        assertTrue(output.toString().contains("All debts are settled!"));
    }

    @Test
    void rejectedSettlements_leaveLedgerUnchanged() throws TripJioException {
        run("split d/Taxi a/60 n/2 p/Alice");
        for (String input : List.of("settle p/alice a/31", "settle p/bob", "settle p/alice a/0")) {
            assertThrows(TripJioException.class, () -> run(input), input);
            assertTrue(state.getSettlements().isEmpty());
        }
    }

    @Test
    void legacySplits_surviveAnotherSave() throws Exception {
        Path path = tempDir.resolve("tripjio.txt");
        Files.write(path, List.of("[expense]", "S|Dinner|90.00|3|30.00|FOOD|14-10-2026",
                "S|Taxi|60.00|3|15.00|TRANSPORT|14-10-2026"));
        state = new Storage(path.toString()).load(ui);
        assertEquals("", output.toString());
        restart();
        assertEquals(2, state.getExpenses().size());
        assertFalse(((SplitExpense) state.getExpenses().get(1)).hasCustomShare());
        assertTrue(((SplitExpense) state.getExpenses().get(2)).hasCustomShare());
        assertEquals(15, state.getExpenses().get(2).getAmount());
    }

    @Test
    void customShareEqualToEqualSplit_keepsCustomFlagAfterRestart() throws TripJioException {
        run("split d/Dinner a/90 n/3 my/30");
        restart();
        run("edit 1 a/120");
        assertEquals(30, state.getExpenses().get(1).getAmount());
    }

    @Test
    void invalidSplitEdits_leaveAllFieldsUnchanged() throws TripJioException {
        run("split d/Dinner a/90 n/3 my/40");
        SplitExpense split = (SplitExpense) state.getExpenses().get(1);
        String original = split.toStorageString();
        for (String input : List.of("edit 1 a/30 d/Changed", "edit 1 a/120 my/121 d/Changed",
                "edit 1 my/91 d/Changed")) {
            assertThrows(TripJioException.class, () -> run(input), input);
            assertEquals(original, split.toStorageString());
        }
        assertThrows(TripJioException.class, () -> split.setAmount(30));
        assertThrows(TripJioException.class, () -> split.setCustomShare(0.001));
        assertEquals(original, split.toStorageString());
        run("split d/Candy a/1 n/50");
        SplitExpense equal = (SplitExpense) state.getExpenses().get(2);
        assertThrows(TripJioException.class, () -> equal.setAmount(0.01));
        assertEquals(1, equal.getTotalAmount());
    }

    @Test
    void combinedEdits_validateFinalBillAndShareTogether() throws TripJioException {
        run("split d/Dinner a/90 n/3 my/40");
        run("edit 1 a/30 my/10");
        SplitExpense split = (SplitExpense) state.getExpenses().get(1);
        assertEquals(30, split.getTotalAmount());
        assertEquals(10, split.getAmount());
        run("split d/Taxi a/60 n/3");
        run("edit 2 a/90");
        assertEquals(30, state.getExpenses().get(2).getAmount());
        run("add d/Lunch a/5");
        assertThrows(TripJioException.class, () -> run("edit 3 a/10 my/3"));
        assertEquals(5, state.getExpenses().get(3).getAmount());
    }

    @Test
    void invalidCommandArguments_areRejected() {
        for (String input : List.of("owe p/", "owe p/alice p/bob", "owe a/1", "owe alice",
                "settle", "settle a/5", "settle p/alice a/1 a/2", "settle p/alice cur/EUR",
                "split d/X a/90 n/3 p/alice,ALICE", "split d/X a/90 n/3 p/alice,",
                "split d/X a/90 n/3 p/alice,bob paid/charlie", "split d/X a/90 n/3 p/alice,bob rate/1",
                "split d/X a/90 n/3 cur/EUR", "split d/X a/90 n/3 cur/EUR rate/NaN",
                "split d/X a/90 n/3 cur/EUR rate/Infinity", "split d/X a/90 n/3 cur/QQQ rate/1",
                "split d/X a/90 n/3 cur/SGD rate/2", "split d/X a/90 n/3 cur/EUR rate/1 rate/2")) {
            assertThrows(TripJioException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    void foreignSplits_validateConvertedSgdAndKeepSmallRate() throws TripJioException {
        run("split d/Foreign a/2000000 n/2 cur/JPY rate/0.00000001");
        restart();
        SplitExpense split = (SplitExpense) state.getExpenses().get(1);
        assertEquals(0.02, split.getTotalAmount());
        assertEquals(0.01, split.getAmount());
        assertEquals(0.00000001, split.getExchangeRate());
        assertThrows(TripJioException.class, () -> run("split d/X a/1 n/2 cur/JPY rate/0.001"));
        assertThrows(TripJioException.class, () -> run("split d/X a/1000000 n/2 cur/EUR rate/2"));
        run("split d/Y a/100 n/2 cur/JPY rate/0.01 my/0.50");
        assertThrows(TripJioException.class, () -> run("split d/Z a/100 n/2 cur/JPY rate/0.01 my/2"));
    }

    @Test
    void prefixes_paidAndRateAreDistinctFromShortPrefixes() throws TripJioException {
        ArgumentParser parser = new ArgumentParser("p/Alice,Bob paid/Alice rate/1.45 r/2");
        assertEquals("Alice,Bob", parser.getRequired("p"));
        assertEquals("Alice", parser.getRequired("paid"));
        assertEquals("1.45", parser.getRequired("rate"));
        assertEquals("2", parser.getRequired("r"));
    }

    @Test
    void settlementStorage_formatsSignedAmountsToCents() throws Exception {
        state.getSettlements().add(new Settlement("Alice", 33.333333));
        state.getSettlements().add(new Settlement("Bob", -12.345678));
        restart();
        String saved = Files.readString(tempDir.resolve("tripjio.txt"));
        assertTrue(saved.contains("alice|33.33"));
        assertTrue(saved.contains("bob|-12.35"));
        assertEquals(33.33, state.getSettlements().get(0).getAmount());
        assertEquals(-12.35, state.getSettlements().get(1).getAmount());
    }

    @Test
    void malformedExtendedSplits_warnAndKeepOtherData() throws Exception {
        Path path = tempDir.resolve("tripjio.txt");
        String base = "S|Dinner|90.00|3|30.00|FOOD|14-10-2026|";
        Files.write(path, List.of("[expense]", base + "|alice,alice|||false",
                base + "|alice,bob|EUR|NaN|false", base + "|alice,bob|||maybe",
                "S|Dinner|90.00|3|40.00|FOOD|14-10-2026||alice,bob|||false",
                "E|Lunch|5.00|FOOD|14-10-2026"));
        state = new Storage(path.toString()).load(ui);
        assertEquals(1, state.getExpenses().size());
        for (int line = 2; line <= 5; line++) {
            assertTrue(output.toString().contains("Skipped invalid line " + line + " "));
        }
    }
}
