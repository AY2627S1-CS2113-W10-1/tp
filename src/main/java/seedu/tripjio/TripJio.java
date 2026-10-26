package seedu.tripjio;

import seedu.tripjio.command.Command;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.budget.BudgetChecker;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.storage.Storage;
import seedu.tripjio.ui.Ui;

import java.time.YearMonth;

/**
 * Main entry point of TripJio, a budget tracker and travel app for students.
 */
public class TripJio {
    private static final String DATA_FILE = "data/tripjio.txt";

    private final Ui ui;
    private final Storage storage;
    private final AppState state;

    /**
     * Creates the application that reads from the keyboard and loads data from the given file.
     *
     * @param filePath Path of the data file.
     */
    public TripJio(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates the application with the given UI, so tests can supply fake input and capture the output.
     *
     * @param filePath Path of the data file.
     * @param ui UI used for all input and output.
     */
    public TripJio(String filePath, Ui ui) {
        this.ui = ui;
        this.storage = new Storage(filePath);
        this.state = storage.load(ui);
    }

    /**
     * Shows the welcome message, then reads and runs commands until the user exits.
     * After each command, the budget of the affected month is checked and the data is saved if it changed.
     * A command that fails shows an error and does neither of these.
     */
    public void run() {
        ui.showWelcome();

        boolean isExit = false;

        while (!isExit) {
            String input = ui.readCommand();
            try {
                Command command = Parser.parse(input);
                command.execute(state, ui);
                showBudgetWarning(command.getAffectedMonth());

                if (command.isMutating()) {
                    storage.save(state);
                }
                isExit = command.isExit();
            } catch (TripJioException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }

    private void showBudgetWarning(YearMonth month) {
        if (month == null) {
            return;
        }
        double spent = state.getExpenses().getTotalSpentInMonth(month);
        String warning = BudgetChecker.check(state.getBudget(), spent, month);
        if (warning != null) {
            ui.showMessage(warning);
        }
    }

    /**
     * Starts the TripJio application.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        new TripJio(DATA_FILE).run();
    }
}
