package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

import java.time.YearMonth;

/**
 * Represents a command entered by the user. Each subclass carries out one kind of command.
 */
public abstract class Command {
    /**
     * Carries out this command.
     *
     * @param state Data of the application.
     * @param ui User interface used to show the result.
     * @throws TripJioException If the command cannot be carried out, e.g. because of invalid input.
     */
    public abstract void execute(AppState state, Ui ui) throws TripJioException;

    /**
     * Returns whether the application should stop after this command. Only {@code ExitCommand} overrides this.
     *
     * @return True if the application should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command changes the user's data. The main loop saves the data file after every
     * mutating command, so commands such as {@code add}, {@code edit}, {@code delete}, {@code split},
     * {@code budget set} and {@code trip set} must override this to return true.
     *
     * @return True if the data should be saved after this command.
     */
    public boolean isMutating() {
        return false;
    }

    /**
     * Returns the month whose spending was changed by this command. The main loop checks the budget of this month
     * and shows a warning if needed, so {@code add}, {@code edit} and {@code split} must override this.
     *
     * @return Month to check the budget of, or null if no budget check is needed.
     */
    public YearMonth getAffectedMonth() {
        return null;
    }
}
