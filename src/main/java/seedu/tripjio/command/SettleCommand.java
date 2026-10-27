package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Expense;
import seedu.tripjio.model.split.Settlement;
import seedu.tripjio.model.split.SplitExpense;
import seedu.tripjio.ui.Ui;
import seedu.tripjio.util.MoneyUtil;

import java.util.Locale;

/** Records a full or partial SGD payment against one person's outstanding net balance. */
public class SettleCommand extends Command {
    private final String person;
    /** Null requests full settlement; otherwise this is a positive payment amount in SGD. */
    private final Double amount;

    /** Creates a settlement request; the parser validates the name and optional payment first. */
    public SettleCommand(String person, Double amount) {
        this.person = person.trim().toLowerCase(Locale.ROOT);
        this.amount = amount;
    }

    /** Validates the payment before recording it; a failed request leaves the ledger unchanged. */
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
            MoneyUtil.validateAmount(amount);
            MoneyUtil.validateOriginalAmount(amount);
            if (amount > Math.abs(net) + 0.005) {
                throw new TripJioException("Cannot settle more than the balance of $" 
                        + String.format(Locale.ROOT, "%.2f", Math.abs(net)));
            }
            settleAmount = amount;
        }

        double transactionAmount = net > 0 ? settleAmount : -settleAmount;
        state.getSettlements().add(new Settlement(person, transactionAmount));

        ui.showMessage("Recorded settlement of $" + String.format(Locale.ROOT, "%.2f", settleAmount)
                + " with " + person + ".");
    }

    /** Returns incoming debt minus outgoing debt and previously recorded signed payments. */
    private double calculateNetBalance(AppState state, String targetPerson) {
        double net = 0.0;
        
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
