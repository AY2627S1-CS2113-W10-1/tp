package seedu.tripjio.command;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.model.expense.Category;
import seedu.tripjio.model.expense.ExchangeRates;
import seedu.tripjio.model.expense.ForeignExpense;
import seedu.tripjio.ui.Ui;

import java.time.LocalDate;
import java.time.YearMonth;

/** Adds a currency-converted expense only when its rate and converted amount are valid. */
public class AddForeignExpenseCommand extends Command {
    private final String description;
    private final double amount;
    private final Category category;
    private final LocalDate date;
    private final String currency;

    /** Captures validated input; the current manual rate is resolved when executed. */
    public AddForeignExpenseCommand(String description, double amount, Category category, LocalDate date,
                                   String currency) throws TripJioException {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.currency = ExchangeRates.currencyCode(currency);
    }

    @Override
    public void execute(AppState state, Ui ui) throws TripJioException {
        ForeignExpense expense = new ForeignExpense(description, amount, category, date, currency,
                state.getExchangeRates().get(currency));
        state.getExpenses().add(expense);
        ui.showMessage("Added expense (SGD):", "  " + expense,
                "You now have " + state.getExpenses().size() + " expenses.");
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public YearMonth getAffectedMonth() {
        return YearMonth.from(date);
    }

    @Override
    public LocalDate getAffectedDate() {
        return date;
    }
}
