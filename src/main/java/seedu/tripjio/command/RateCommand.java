package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.ui.Ui;

/** Sets or lists manually entered exchange rates into the fixed SGD reporting currency. */
public class RateCommand extends Command {
    private final String currency;
    private final String rate;

    /** A null currency creates a read-only listing command. */
    public RateCommand(String currency, String rate) {
        this.currency = currency;
        this.rate = rate;
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        if (currency == null) {
            ui.showMessage("Manual exchange rates (SGD per unit; SGD is always 1):");
            ui.showMessage(state.getExchangeRates().toStorageLines().toArray(String[]::new));
        } else {
            state.getExchangeRates().set(currency, rate);
            ui.showMessage("Manual rate saved: 1 " + currency + " = " + rate + " SGD.",
                    "Existing expenses retain their saved rates.");
        }
    }

    @Override
    public boolean isMutating() {
        return currency != null;
    }
}
