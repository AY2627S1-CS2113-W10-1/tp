package seedu.tripjio.storage;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.budget.Budget;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.expense.ExpenseList;
import seedu.tripjio.model.expense.ForeignExpense;
import seedu.tripjio.model.split.Settlement;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.model.trip.Trip;
import seedu.tripjio.ui.Ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the user's data to a plain text file and loads it back when the application starts.
 * The file is divided into blocks that start with a header such as {@code [expense]}, as described in
 * "Editing the data file" in the User Guide.
 */
public class Storage {
    private static final String EXPENSE_HEADER = "[expense]";
    private static final String BUDGET_HEADER = "[budget]";
    private static final String TRIP_HEADER = "[trip]";
    private static final String TRIPS_HEADER = "[trips]";
    private static final String RATES_HEADER = "[rates]";
    private static final String OWE_HEADER = "[owe]";

    /** Start of an expense line for a normal expense, e.g. "E|Lunch|12.50|FOOD|15-10-2026". */
    private static final String EXPENSE_TAG = "E|";

    /** Start of an expense line for a split expense, e.g. "S|Group dinner|90.00|3|30.00|FOOD|14-10-2026". */
    private static final String SPLIT_EXPENSE_TAG = "S|";

    private final Path filePath;

    /**
     * Creates a storage that reads from and writes to the given file.
     *
     * @param filePath Path of the data file, e.g. "data/tripjio.txt".
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads the user's data from the file.
     * This method never fails: if the file does not exist or cannot be read, it starts with no data,
     * and every invalid line is skipped with a warning so the other lines are still loaded.
     *
     * @param ui User interface used to show warnings.
     * @return The loaded data, or empty data if the file does not exist or cannot be read.
     */
    public AppState load(Ui ui) {
        if (!Files.exists(filePath)) {
            return new AppState();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ui.showError("Could not read data file. Starting with no data.");
            return new AppState();
        }

        ExpenseList expenses = new ExpenseList();
        Budget budget = new Budget();
        Trip trip = new Trip();
        AppState state = new AppState(expenses, budget, trip);

        // Header of the block being read, or null before the first header.
        String section = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            int lineNumber = i + 1;

            if (line.isEmpty()) {
                continue;
            }
            if (isHeader(line)) {
                section = line;
                continue;
            }

            try {
                if (TRIPS_HEADER.equals(section)) {
                    state.getTrips().loadStorageLine(line);
                } else if (RATES_HEADER.equals(section)) {
                    state.getExchangeRates().loadStorageLine(line);
                } else if (OWE_HEADER.equals(section)) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        state.getSettlements().add(new Settlement(parts[0], Double.parseDouble(parts[1])));
                    } else {
                        throw new TripJioException("Invalid owe line format.");
                    }
                } else {
                    loadLine(line, section, expenses, budget, trip);
                }
            } catch (TripJioException | NumberFormatException e) {
                ui.showError("Skipped invalid line " + lineNumber + " in data file: " + e.getMessage());
            }
        }
        return state;
    }

    private static boolean isHeader(String line) {
        return line.equals(EXPENSE_HEADER) || line.equals(BUDGET_HEADER) || line.equals(TRIP_HEADER)
                || line.equals(TRIPS_HEADER) || line.equals(RATES_HEADER) || line.equals(OWE_HEADER);
    }

    /**
     * Loads one non-empty, non-header line into the object of the block it belongs to.
     *
     * @throws TripJioException If the line is outside any block or is invalid for its block.
     */
    private static void loadLine(String line, String section, ExpenseList expenses, Budget budget, Trip trip)
            throws TripJioException {
        if (section == null) {
            throw new TripJioException("Line is not inside an [expense], [budget] or [trip] block.");
        }

        switch (section) {
        case EXPENSE_HEADER:
            if (line.startsWith(EXPENSE_TAG)) {
                expenses.add(Expense.fromStorageString(line));
            } else if (line.startsWith(SPLIT_EXPENSE_TAG)) {
                expenses.add(SplitExpense.fromStorageString(line));
            } else if (line.startsWith("F|")) {
                expenses.add(ForeignExpense.fromStorageString(line));
            } else {
                throw new TripJioException("Expense lines must start with E|, S| or F|.");
            }
            break;
        case BUDGET_HEADER:
            budget.loadStorageLine(line);
            break;
        case TRIP_HEADER:
            trip.loadStorageLine(line);
            break;
        default:
            throw new AssertionError("Unknown section: " + section);
        }
    }

    /**
     * Saves all of the user's data to the file, creating the folder if it does not exist.
     * The whole file is rewritten, with one block for expenses, budgets and the trip.
     *
     * @param state Data to save.
     * @throws TripJioException If the file cannot be written.
     */
    public void save(AppState state) throws TripJioException {
        List<String> lines = new ArrayList<>();

        lines.add(EXPENSE_HEADER);
        for (Expense expense : state.getExpenses().getAll()) {
            lines.add(expense.toStorageString());
        }
        lines.add("");

        lines.add(BUDGET_HEADER);
        lines.addAll(state.getBudget().toStorageLines());
        lines.add("");

        lines.add(TRIP_HEADER);
        lines.addAll(state.getTrip().toStorageLines());
        
        List<String> tripLines = state.getTrips().toStorageLines();
        if (!tripLines.isEmpty()) {
            lines.add("");
            lines.add(TRIPS_HEADER);
            lines.addAll(tripLines);
        }
        
        List<String> rateLines = state.getExchangeRates().toStorageLines();
        if (!rateLines.isEmpty()) {
            lines.add("");
            lines.add(RATES_HEADER);
            lines.addAll(rateLines);
        }

        List<Settlement> settlements = state.getSettlements();
        if (!settlements.isEmpty()) {
            lines.add("");
            lines.add(OWE_HEADER);
            for (Settlement s : settlements) {
                lines.add(s.getPerson() + "|" + s.getAmount());
            }
        }

        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            // UTF-8 is always used so that descriptions such as "Phở" are saved correctly on every computer.
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TripJioException("Could not save data: " + e.getMessage());
        }
    }
}
