package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.Settlement;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;

public class SettleCommand extends Command {
    private final String person;
    private final Double amount;

    public SettleCommand(String person, Double amount) {
        this.person = person.toLowerCase();
        this.amount = amount;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        double net = calculateNetBalance(state, person);
        if (Math.abs(net) < 0.005) {
            throw new TripJioException("You have no outstanding balance with " + person + ".");
        }

        double settleAmount;
        if (amount == null) {
            settleAmount = Math.abs(net);
        } else {
            if (amount > Math.abs(net) + 0.005) {
                throw new TripJioException("Cannot settle more than the balance of $" + String.format("%.2f", Math.abs(net)));
            }
            settleAmount = amount;
        }

        double transactionAmount = net > 0 ? settleAmount : -settleAmount;
        state.getSettlements().add(new Settlement(person, transactionAmount));

        ui.showMessage("Recorded settlement of $" + String.format("%.2f", settleAmount) + " with " + person + ".");
    }

    private double calculateNetBalance(AppState state, String targetPerson) {
        double net = 0.0;
        
        // Use .getAll() here as well
        for (Expense e : state.getExpenses().getAll()) {
            if (e instanceof SplitExpense) {
                SplitExpense se = (SplitExpense) e;
                if (se.getPayerName() == null) {
                    net += se.getAmountsOwedToMe().getOrDefault(targetPerson, 0.0);
                } else if (se.getPayerName().equals(targetPerson)) {
                    net -= se.getMyShare();
                }
            }
        }
        
        for (Settlement s : state.getSettlements()) {
            if (s.getPerson().equals(targetPerson)) {
                net -= s.getAmount(); 
            }
        }
        return net;
    }

    @Override
    public boolean isMutating() { 
        return true; 
    }
}
