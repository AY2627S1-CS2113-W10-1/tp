package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.MoneyUtil;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all expenses recorded by the user, including split expenses, in the order they were added.
 * Indexes used by this class start from 1, matching the numbers shown to the user by the {@code list} command.
 */
public class ExpenseList {
    private final List<Expense> expenses = new ArrayList<>();

    public void add(Expense expense) {
        expenses.add(expense);
    }

    /**
     * Returns the expense at the given index.
     *
     * @param index Index of the expense, starting from 1.
     * @return Expense at the index.
     * @throws TripJioException If the list is empty or the index is out of range.
     */
    public Expense get(int index) throws TripJioException {
        checkIndex(index);
        return expenses.get(index - 1);
    }

    /**
     * Removes the expense at the given index. Expenses after it move up by one index.
     *
     * @param index Index of the expense, starting from 1.
     * @return The removed expense.
     * @throws TripJioException If the list is empty or the index is out of range.
     */
    public Expense delete(int index) throws TripJioException {
        checkIndex(index);
        return expenses.remove(index - 1);
    }

    public int size(){
        return expenses.size();
    }

    public boolean isEmpty(){
        return expenses.isEmpty();
    }

    /**
     * Returns a copy of all expenses, so callers can read or loop over them
     * without being able to add or remove expenses from this list.
     *
     * @return New list containing all expenses, in the order they were added.
     */
    public List<Expense> getAll() {
        return new ArrayList<>(expenses);
    }

    /**
     * Returns the total amount the user spent in the given month.
     * Split expenses count only the user's share. The total is rounded to cents, so small floating-point
     * errors from adding many amounts do not affect budget comparisons.
     *
     * @param month Month to total.
     * @return Total amount spent in the month, or 0 if there are no expenses in it.
     */
    public double getTotalSpentInMonth(YearMonth month) {
        double total = 0.0;

        for (Expense expense : expenses) {
            if (expense.getMonth().equals(month)) {
                total += expense.getAmount();
            }
        }
        return MoneyUtil.roundToCents(total);
    }

    /**
     * Returns the total amount the user spent from the start date to the end date, including both dates.
     * Split expenses count only the user's share. The total is rounded to cents, so small floating-point
     * errors from adding many amounts do not affect budget comparisons.
     *
     * @param start First date to include.
     * @param end Last date to include.
     * @return Total amount spent in the period, or 0 if there are no expenses in it.
     */
    public double getTotalSpentBetween(LocalDate start, LocalDate end) {
        double total = 0.0;

        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();

            if (!date.isBefore(start) && !date.isAfter(end)){
                total += expense.getAmount();
            }
        }
        return MoneyUtil.roundToCents(total);
    }

    /**
     * Returns the expenses made in the given month, in the order they were added.
     *
     * @param month Month to filter by.
     * @return New list containing the expenses in the month, which is empty if there are none.
     */
    public List<Expense> getExpensesInMonth(YearMonth month) {
        List<Expense> result = new ArrayList<>();

        for (Expense expense : expenses) {
            if (expense.getMonth().equals(month)){
                result.add(expense);
            }
        }
        return result;
    }

    private void checkIndex(int index) throws TripJioException {
        if (expenses.isEmpty()) {
            throw new TripJioException("You have no expenses yet.");
        }

        if (index < 1 || index > expenses.size()) {
            throw new TripJioException(
                    "Invalid index: " + index
                            + ". You have " + expenses.size() + " expenses."
            );
        }
    }
}
