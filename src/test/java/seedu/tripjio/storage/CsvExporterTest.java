package seedu.tripjio.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks CSV escaping, row building with global indexes, and writing the export file. */
class CsvExporterTest {
    /** JUnit creates a new empty folder for each test, so no real file is written. */
    @TempDir
    Path tempDir;

    private static Expense expense(String description, double amount, int day) {
        return new Expense(description, amount, Category.FOOD, LocalDate.of(2026, 10, day));
    }

    @Test
    void escape_plainText_unchanged() {
        assertEquals("Lunch", CsvExporter.escape("Lunch"));
        assertEquals("", CsvExporter.escape(""));
    }

    @Test
    void escape_comma_wrappedInQuotes() {
        assertEquals("\"Ramen, large\"", CsvExporter.escape("Ramen, large"));
    }

    @Test
    void escape_quote_doubledAndWrapped() {
        // Ramen "large"  ->  "Ramen ""large"""
        assertEquals("\"Ramen \"\"large\"\"\"", CsvExporter.escape("Ramen \"large\""));
    }

    @Test
    void escape_lineBreak_wrappedInQuotes() {
        assertEquals("\"a\nb\"", CsvExporter.escape("a\nb"));
        assertEquals("\"a\rb\"", CsvExporter.escape("a\rb"));
    }

    @Test
    void toCsvLine_joinsEscapedFields() {
        // The last field is empty, so the line ends with a comma.
        assertEquals("1,\"a,b\",", CsvExporter.toCsvLine(List.of("1", "a,b", "")));
    }

    @Test
    void toCsvLines_someSelected_headerThenGlobalIndexes() {
        Expense a = expense("Breakfast", 5, 1);
        Expense b = expense("Lunch", 12.5, 1);
        Expense c = expense("Dinner", 20, 3);

        List<String> lines = CsvExporter.toCsvLines(List.of(a, b, c), List.of(b, c));

        assertEquals(3, lines.size());
        assertEquals(CsvExporter.toCsvLine(CsvExporter.HEADER), lines.get(0));
        assertTrue(lines.get(1).startsWith("2,"), lines.get(1));
        assertTrue(lines.get(1).contains("Lunch"), lines.get(1));
        assertTrue(lines.get(2).startsWith("3,"), lines.get(2));
        assertTrue(lines.get(2).contains("Dinner"), lines.get(2));
    }

    @Test
    void toCsvLines_nothingSelected_onlyHeader() {
        List<String> lines = CsvExporter.toCsvLines(List.of(expense("Lunch", 12.5, 2)), List.of());

        assertEquals(List.of(CsvExporter.toCsvLine(CsvExporter.HEADER)), lines);
    }

    @Test
    void write_createsFolderWithBomAndUtf8() throws TripJioException, IOException {
        Path file = tempDir.resolve("sub").resolve("export.csv");   // "sub" does not exist yet

        CsvExporter.write(file, List.of("Index,Description", "1,Phở"));

        assertTrue(Files.exists(file));
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("\uFEFF"), "File should start with a UTF-8 byte-order mark");
        assertEquals("\uFEFFIndex,Description\n1,Phở\n", content);
    }
}
