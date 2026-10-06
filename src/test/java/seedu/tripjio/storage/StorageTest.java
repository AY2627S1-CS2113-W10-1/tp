package seedu.tripjio.storage;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.ui.Ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(List.of("[expense]", "E|Lunch|12.50|FOOD|15-10-2026", "", "[budget]", "", "[trip]"), lines);
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
}
