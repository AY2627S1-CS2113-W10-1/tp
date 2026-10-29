package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseFilter;
import seedu.tripjio.storage.CsvExporter;
import seedu.tripjio.ui.Ui;

import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

/** Writes the expenses that match the given filters to a CSV file. Does not change any data. */
public class ExportCommand extends Command {
    /** File written by the {@code export} command; each export replaces the previous one. */
    public static final String DEFAULT_FILE = "data/export.csv";

    private final Category category;   // null = all categories
    private final YearMonth month;     // null = all months
    private final boolean tripOnly;
    private final String filePath;

    /** Creates an export to {@link #DEFAULT_FILE}; null filters match every expense. */
    public ExportCommand(Category category, YearMonth month, boolean tripOnly) {
        this(category, month, tripOnly, DEFAULT_FILE);
    }

    /** Creates an export to the given file, so tests can write to a temporary folder. */
    public ExportCommand(Category category, YearMonth month, boolean tripOnly, String filePath) {
        this.category = category;
        this.month = month;
        this.tripOnly = tripOnly;
        this.filePath = filePath;
    }

    /** Returns the category filter, or null for all categories. */
    public Category getCategory() {
        return category;
    }

    /** Returns the month filter, or null for all months. */
    public YearMonth getMonth() {
        return month;
    }

    /** Returns whether only expenses within the selected trip's dates are exported. */
    public boolean isTripOnly() {
        return tripOnly;
    }

    /** Returns the path of the file the export is written to. */
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        List<Expense> selected = ExpenseFilter.select(state, category, month, tripOnly);

        if (selected.isEmpty()) {
            ui.showMessage("No expenses match the given filter. Nothing was exported.");
            return;
        }

        List<String> lines = CsvExporter.toCsvLines(state.getExpenses().getAll(), selected);
        CsvExporter.write(Path.of(filePath), lines);

        int n = selected.size();
        ui.showMessage("Exported " + n + " expense" + (n == 1 ? "" : "s") + " to " + filePath + ".");
    }
}
