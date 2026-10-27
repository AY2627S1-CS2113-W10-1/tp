package seedu.tripjio.storage;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ForeignExpense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {
    /** JUnit creates a new empty folder for each test and deletes it afterwards. */
    @TempDir
    Path tempDir;

    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }

    private Storage storageAt(Path path) {
        return new Storage(path.toString());
    }

    @Test
    void load_fileDoesNotExist_returnsEmptyState() {
        AppState state = storageAt(tempDir.resolve("missing.txt")).load(ui);
        assertTrue(state.getExpenses().isEmpty());
        assertEquals("", output.toString());
    }

    @Test
    void saveThenLoad_expenses_sameData() throws TripJioException {
        Path file = tempDir.resolve("data").resolve("tripjio.txt");   // folder does not exist yet
        AppState state = new AppState();
        state.getExpenses().add(new Expense("Lunch", 12.5, Category.FOOD, LocalDate.of(2026, 10, 15)));
        state.getExpenses().add(new Expense("Phở", 1234.5, Category.OTHER, LocalDate.of(2026, 10, 2)));

        storageAt(file).save(state);
        AppState loaded = storageAt(file).load(ui);

        assertEquals(2, loaded.getExpenses().size());
        assertEquals(state.getExpenses().get(1).toString(), loaded.getExpenses().get(1).toString());
        assertEquals(state.getExpenses().get(2).toString(), loaded.getExpenses().get(2).toString());
    }

    @Test
    void save_writesBlocksInUserGuideFormat() throws TripJioException, IOException {
        Path file = tempDir.resolve("tripjio.txt");
        AppState state = new AppState();
        state.getExpenses().add(new Expense("Lunch", 12.5, Category.FOOD, LocalDate.of(2026, 10, 15)));

        storageAt(file).save(state);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(List.of("[expense]", "E|Lunch|12.50|FOOD|15-10-2026", "", "[budget]", "", "[trips]"), lines);
    }

    @Test
    void load_invalidLines_skippedWithWarningAndOthersLoaded() throws IOException {
        Path file = tempDir.resolve("tripjio.txt");
        Files.write(file, List.of(
                "outside any block",                       // line 1: before the first header
                "[expense]",
                "E|Lunch|12.50|FOOD|15-10-2026",
                "E|Broken|abc|FOOD|15-10-2026",            // line 4: invalid amount
                "X|Unknown tag",                           // line 5: wrong tag
                "",
                "E|Metro card|45.00|TRANSPORT|02-10-2026"
        ), StandardCharsets.UTF_8);

        AppState state = storageAt(file).load(ui);

        assertEquals(2, state.getExpenses().size());
        String warnings = output.toString();
        assertTrue(warnings.contains("Skipped invalid line 1 "));
        assertTrue(warnings.contains("Skipped invalid line 4 "));
        assertTrue(warnings.contains("Skipped invalid line 5 "));
    }

    @Test
    void saveThenLoad_normalSplitAndForeignExpenses_sameDataAndTypes() throws TripJioException {
        Path file = tempDir.resolve("tripjio.txt");
        AppState state = new AppState();
        state.getExpenses().add(new Expense("Lunch", 12.5, Category.FOOD, LocalDate.of(2026, 10, 15)));
        state.getExpenses().add(new SplitExpense("Group dinner", 90, 3, null,
                Category.FOOD, LocalDate.of(2026, 10, 14)));                      // equal split: share 30.00
        state.getExpenses().add(new SplitExpense("Hotpot", 120, 4, 40.0,
                Category.FOOD, LocalDate.of(2026, 10, 13)));                      // custom share (my/40)
        state.getExpenses().add(new ForeignExpense("Ramen", 12, Category.FOOD, LocalDate.of(2026, 10, 12),
                "EUR", new BigDecimal("1.45")));                                  // EUR 12 = SGD 17.40

        storageAt(file).save(state);
        AppState loaded = storageAt(file).load(ui);

        assertEquals("", output.toString(), "No line should be skipped");
        assertEquals(4, loaded.getExpenses().size());
        for (int i = 1; i <= 4; i++) {
            Expense saved = state.getExpenses().get(i);
            Expense restored = loaded.getExpenses().get(i);
            // Same class: an S| line must come back as a SplitExpense, not a plain Expense.
            assertEquals(saved.getClass(), restored.getClass(), "expense " + i);
            // Same storage line: every field (bill, people, currency, rate...) survived the round trip.
            assertEquals(saved.toStorageString(), restored.toStorageString(), "expense " + i);
            assertEquals(saved.getAmount(), restored.getAmount(), "expense " + i);
        }
        assertFalse(((SplitExpense) loaded.getExpenses().get(2)).hasCustomShare());
        assertTrue(((SplitExpense) loaded.getExpenses().get(3)).hasCustomShare());
        // 12.50 + 30.00 + 40.00 + 17.40: only the user's own shares, all in SGD.
        assertEquals(99.90, loaded.getExpenses().getTotalSpentInMonth(YearMonth.of(2026, 10)));
    }

    @Test
    void load_handWrittenSplitLine_countsOnlyOwnShare() throws IOException, TripJioException {
        Path file = tempDir.resolve("tripjio.txt");
        Files.write(file, List.of("[expense]", "S|Group dinner|90.00|3|30.00|FOOD|14-10-2026"),
                StandardCharsets.UTF_8);

        AppState state = storageAt(file).load(ui);

        assertEquals("", output.toString());
        SplitExpense split = assertInstanceOf(SplitExpense.class, state.getExpenses().get(1));
        assertEquals(90.00, split.getTotalAmount());
        assertEquals(3, split.getNumberOfPeople());
        assertEquals(30.00, split.getAmount());
    }

    @Test
    void load_invalidSplitAndForeignLines_skippedWithWarning() throws IOException {
        Path file = tempDir.resolve("tripjio.txt");
        Files.write(file, List.of(
                "[expense]",
                "S|Dinner|90.00|3|30.00|FOOD",                  // line 2: date field missing
                "S|Dinner|90.00|abc|30.00|FOOD|14-10-2026",     // line 3: people is not a number
                "S|Dinner|90.00|1|90.00|FOOD|14-10-2026",       // line 4: fewer than 2 people
                "S|Dinner|90.00|3|95.00|FOOD|14-10-2026",       // line 5: share larger than the bill
                "F|Ramen|12.00|FOOD|12-10-2026|QQQ|1.45",       // line 6: unknown currency
                "F|Ramen|12.00|FOOD|12-10-2026|EUR|-1",         // line 7: negative rate
                "E|Lunch|12.50|FOOD|15-10-2026"                 // line 8: valid, must still load
        ), StandardCharsets.UTF_8);

        AppState state = storageAt(file).load(ui);

        assertEquals(1, state.getExpenses().size());
        for (int line = 2; line <= 7; line++) {
            assertTrue(output.toString().contains("Skipped invalid line " + line + " "), "line " + line);
        }
    }
}
