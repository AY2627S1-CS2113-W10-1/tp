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

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        List<Expense> allExpenses = state.getExpenses().getAll();
        List<SplitExpense> splitExpenses = new ArrayList<>();
        List<Integer> actualIndexes = new ArrayList<>();

        for (int i = 0; i < allExpenses.size(); i++) {
            if (allExpenses.get(i) instanceof SplitExpense) {
                splitExpenses.add((SplitExpense) allExpenses.get(i));
                actualIndexes.add(i + 1);
            }
        }

        if (splitExpenses.isEmpty()) {
            ui.showMessage("You have no split expenses yet.");
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