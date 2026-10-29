package seedu.tripjio.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ForeignExpense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks that export writes exactly the filtered expenses, with global indexes, and changes no data. */
class ExportCommandTest {
    /** JUnit creates a new empty folder for each test, so the real data/export.csv is never touched. */
    @TempDir
    Path tempDir;

    private AppState state;
    private ByteArrayOutputStream output;
    private Ui ui;
    private Path file;

    @BeforeEach
    void setUp() {
        state = new AppState();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output, true, StandardCharsets.UTF_8));
        file = tempDir.resolve("export.csv");
    }

    /** Adds four expenses: index 1 and 3 are October food, 2 is October transport, 4 is September food. */
    private void addSampleExpenses() {
        state.getExpenses().add(new Expense("Lunch", 12.5, Category.FOOD, LocalDate.of(2026, 10, 15)));
        state.getExpenses().add(new Expense("Metro card", 45, Category.TRANSPORT, LocalDate.of(2026, 10, 2)));
        state.getExpenses().add(new SplitExpense("Group dinner", 90, 3, null,
                Category.FOOD, LocalDate.of(2026, 10, 14)));
        state.getExpenses().add(new Expense("Snack", 3, Category.FOOD, LocalDate.of(2026, 9, 20)));
    }

    private ExportCommand export(Category category, YearMonth month, boolean tripOnly) {
        return new ExportCommand(category, month, tripOnly, file.toString());
    }

    /** Returns the data rows of the exported file, without the header line. */
    private List<String> dataRows() throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return lines.subList(1, lines.size());
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void execute_noFilter_exportsAllWithHeader() throws TripJioException, IOException {
        addSampleExpenses();

        export(null, null, false).execute(state, ui);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(5, lines.size(), "header + 4 expenses");
        // The first line starts with the UTF-8 byte-order mark, so contains() is used instead of equals().
        assertTrue(lines.get(0).contains("Index,Date,Description,Category,Amount (SGD),Type,"
                + "Full bill (SGD),People,Original amount,Currency,Rate"), lines.get(0));
        assertTrue(printed().contains("Exported 4 expenses to " + file + "."), printed());
    }

    @Test
    void execute_oneMatch_singularMessage() throws TripJioException {
        addSampleExpenses();

        export(Category.TRANSPORT, null, false).execute(state, ui);

        assertTrue(printed().contains("Exported 1 expense to " + file + "."), printed());
        assertFalse(printed().contains("1 expenses"), printed());
    }

    @Test
    void execute_monthAndCategoryFilter_keepsGlobalIndexes() throws TripJioException, IOException {
        addSampleExpenses();

        export(Category.FOOD, YearMonth.of(2026, 10), false).execute(state, ui);

        List<String> rows = dataRows();
        assertEquals(2, rows.size(), rows.toString());
        // Lunch and Group dinner are expenses 1 and 3 in the full list, as "list c/food m/10-2026" shows.
        assertTrue(rows.get(0).startsWith("1,2026-10-15,Lunch,"), rows.get(0));
        assertTrue(rows.get(1).startsWith("3,2026-10-14,Group dinner,"), rows.get(1));
    }

    @Test
    void execute_splitAndForeign_haveTheirTypes() throws TripJioException, IOException {
        addSampleExpenses();
        state.getExpenses().add(new ForeignExpense("Ramen", 12, Category.FOOD,
                LocalDate.of(2026, 10, 10), "EUR", new BigDecimal("1.45")));

        export(null, null, false).execute(state, ui);

        List<String> rows = dataRows();
        assertTrue(rows.contains("3,2026-10-14,Group dinner,Food,30.00,Split,90.00,3,,,"), rows.toString());
        assertTrue(rows.contains("5,2026-10-10,Ramen,Food,17.40,Foreign,,,12.00,EUR,1.45"), rows.toString());
    }

    @Test
    void execute_descriptionWithComma_isQuoted() throws TripJioException, IOException {
        state.getExpenses().add(new Expense("Ramen, large", 15, Category.FOOD, LocalDate.of(2026, 10, 10)));

        export(null, null, false).execute(state, ui);

        assertTrue(dataRows().get(0).startsWith("1,2026-10-10,\"Ramen, large\",Food,15.00,"),
                dataRows().get(0));
    }

    @Test
    void execute_tripOnlyWithSelectedTrip_exportsTripDatesOnly() throws TripJioException, IOException {
        state.getTrips().add("Japan", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15));
        state.getExpenses().add(new Expense("First day", 10, Category.FOOD, LocalDate.of(2026, 10, 1)));
        state.getExpenses().add(new Expense("Last day", 20, Category.FOOD, LocalDate.of(2026, 10, 15)));
        state.getExpenses().add(new Expense("After trip", 30, Category.FOOD, LocalDate.of(2026, 10, 16)));

        export(null, null, true).execute(state, ui);

        List<String> rows = dataRows();
        // Both the start and the end date of the trip are included; the day after is not.
        assertEquals(2, rows.size(), rows.toString());
        assertTrue(rows.get(0).contains("First day"), rows.get(0));
        assertTrue(rows.get(1).contains("Last day"), rows.get(1));
    }

    @Test
    void execute_tripOnlyWithoutSelection_throwsAndWritesNoFile() {
        addSampleExpenses();

        assertThrows(TripJioException.class, () -> export(null, null, true).execute(state, ui));
        assertFalse(Files.exists(file));
    }

    @Test
    void execute_noMatch_noFileAndMessage() throws TripJioException {
        addSampleExpenses();

        export(null, YearMonth.of(2025, 1), false).execute(state, ui);

        assertTrue(printed().contains("Nothing was exported"), printed());
        assertFalse(Files.exists(file));
    }

    @Test
    void execute_readOnly_stateUnchanged() throws TripJioException {
        addSampleExpenses();
        String before = state.getExpenses().getAll().toString();

        Command command = export(null, null, false);
        command.execute(state, ui);

        assertEquals(4, state.getExpenses().size());
        assertEquals(before, state.getExpenses().getAll().toString());
        assertFalse(command.isMutating(), "export must not trigger saving of the data file");
        assertNull(command.getAffectedMonth(), "export must not trigger a budget check");
    }

    @Test
    void constructor_defaultPath_isDataExportCsv() {
        assertEquals("data/export.csv", ExportCommand.DEFAULT_FILE);
    }
}
