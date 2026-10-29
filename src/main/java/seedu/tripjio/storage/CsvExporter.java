package seedu.tripjio.storage;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.expense.Expense;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Turns expense rows into CSV text and writes it to a file that spreadsheets can open. */
public class CsvExporter {
    /** Column names, in the same order as Expense.toCsvFields. */
    public static final List<String> HEADER = List.of("Index", "Date", "Description", "Category",
            "Amount (SGD)", "Type", "Full bill (SGD)", "People", "Original amount", "Currency", "Rate");

    /** Byte-order mark: tells Excel the file is UTF-8, so "Phở" is not shown as garbage. */
    private static final String BOM = "\uFEFF";

    private CsvExporter() {
    }

    /**
     * Escapes one field by the RFC 4180 rule: a field containing a comma, double quote or line break
     * is wrapped in double quotes, and each double quote inside it is doubled.
     */
    public static String escape(String field) {
        if (field.contains(",") || field.contains("\"")
                || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /** Joins the escaped fields of one row with commas. */
    static String toCsvLine(List<String> fields) {
        return fields.stream().map(CsvExporter::escape).collect(Collectors.joining(","));
    }

    /**
     * Builds the header line and one line per selected expense, in the order of the full list.
     *
     * @param allExpenses Every expense, used to give each row its global index as shown by {@code list}.
     * @param selected Expenses to export; must be a subset of {@code allExpenses}.
     * @return CSV lines without line breaks, header first.
     */
    public static List<String> toCsvLines(List<Expense> allExpenses, List<Expense> selected) {
        List<String> lines = new ArrayList<>();

        lines.add(toCsvLine(HEADER));

        for (int i = 0; i < allExpenses.size(); i++) {
            Expense expense = allExpenses.get(i);

            if (selected.contains(expense)) {
                lines.add(toCsvLine(expense.toCsvFields(i + 1)));
            }
        }
        return lines;
    }

    /**
     * Writes the lines as UTF-8 with a byte-order mark, creating missing folders and replacing any earlier file.
     *
     * @throws TripJioException If the file cannot be written.
     */
    public static void write(Path path, List<String> lines) throws TripJioException {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            String content = BOM + String.join("\n", lines) + "\n";
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TripJioException("Could not export: " + e.getMessage());
        }
    }
}
