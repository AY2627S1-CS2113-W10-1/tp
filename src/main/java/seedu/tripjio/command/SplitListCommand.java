package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SplitListCommand extends Command {
    private final YearMonth month;

    public SplitListCommand() {
        this(null);
    }

    public SplitListCommand(YearMonth month) {
        this.month = month;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        List<Expense> allExpenses = state.getExpenses().getAll();
        List<SplitExpense> splitExpenses = new ArrayList<>();
        List<Integer> actualIndexes = new ArrayList<>();

        for (int i = 0; i < allExpenses.size(); i++) {
            Expense expense = allExpenses.get(i);
            if (expense instanceof SplitExpense) {
                SplitExpense splitExpense = (SplitExpense) expense;
                if (month == null || splitExpense.getMonth().equals(month)) {
                    splitExpenses.add(splitExpense);
                    actualIndexes.add(i + 1);
                }
            }
        }

        if (splitExpenses.isEmpty()) {
            if (month != null) {
                ui.showMessage("No split expenses match the given filter.");
            } else {
                ui.showMessage("You have no split expenses yet.");
            }
            return;
        }

        StringBuilder sb = new StringBuilder("Here are your split expenses:\n");
        for (int i = 0; i < splitExpenses.size(); i++) {
            sb.append(actualIndexes.get(i)).append(". ").append(splitExpenses.get(i)).append("\n");
        }
        
        ui.showMessage(sb.toString().trim());
    }

    @Override
    public boolean isExit() {
        return false;
    }

    @Override
    public boolean isMutating() {
        return false; 
    }

    @Override
    public YearMonth getAffectedMonth() {
        return null; 
    }
}