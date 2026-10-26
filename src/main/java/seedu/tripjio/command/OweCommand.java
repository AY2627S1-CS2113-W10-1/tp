package seedu.tripjio.command;

import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.Settlement;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OweCommand extends Command {
    private final String targetPerson;

    public OweCommand(String targetPerson) {
        this.targetPerson = targetPerson != null ? targetPerson.toLowerCase() : null;
    }

    @Override
    public void execute(AppState state, Ui ui) {
        Map<String, Double> owedToMe = new HashMap<>();
        Map<String, Double> iOwe = new HashMap<>();

        // Use .getAll() to get the iterable List<Expense>
        for (Expense e : state.getExpenses().getAll()) {
            if (e instanceof SplitExpense) {
                SplitExpense se = (SplitExpense) e;
                if (se.getPayerName() == null) {
                    se.getAmountsOwedToMe().forEach((person, amt) -> 
                            owedToMe.merge(person, amt, Double::sum));
                } else {
                    iOwe.merge(se.getPayerName(), se.getMyShare(), Double::sum);
                }
            }
        }

        for (Settlement s : state.getSettlements()) {
            String p = s.getPerson();
            if (s.getAmount() > 0) {
                owedToMe.merge(p, -s.getAmount(), Double::sum);
            } else {
                iOwe.merge(p, s.getAmount(), Double::sum);
            }
        }

        ui.showMessage("=== Debt Ledger ===");
        Set<String> allPeople = new HashSet<>(owedToMe.keySet());
        allPeople.addAll(iOwe.keySet());

        boolean hasDebt = false;
        for (String person : allPeople) {
            if (targetPerson != null && !person.equals(targetPerson)) {
                continue;
            }
            
            double net = owedToMe.getOrDefault(person, 0.0) - iOwe.getOrDefault(person, 0.0);
            if (Math.abs(net) > 0.005) {
                hasDebt = true;
                if (net > 0) {
                    ui.showMessage(person + " owes you: $" + String.format("%.2f", net));
                } else {
                    ui.showMessage("You owe " + person + ": $" + String.format("%.2f", Math.abs(net)));
                }
            }
        }
        
        if (!hasDebt) {
            ui.showMessage("All debts are settled!");
        }
    }

    @Override
    public boolean isMutating() { 
        return false; 
    }
}
