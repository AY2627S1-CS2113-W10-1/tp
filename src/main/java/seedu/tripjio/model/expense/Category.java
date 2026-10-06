package seedu.tripjio.model.expense;

import seedu.tripjio.exception.TripJioException;

import java.util.Locale;

/**
 * Represents the category that an expense belongs to, such as food or transport.
 */
public enum Category {
    FOOD, TRANSPORT, ACCOMMODATION, ENTERTAINMENT, SHOPPING, OTHER;

    /**
     * Returns the category whose name matches the given text, ignoring case and surrounding whitespace.
     *
     * @param text Category name entered by the user, e.g. "food" or " Transport ".
     * @return The matching category.
     * @throws TripJioException If the text does not match any category.
     */
    public static Category fromString(String text) throws TripJioException {
        try {
            return Category.valueOf(text.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new TripJioException(
                    "Invalid category: " + text
                            + ". Use one of: food, transport, accommodation, "
                            + "entertainment, shopping, other."
            );
        }
    }

    /**
     * Returns the name of this category with only the first letter capitalised, for showing to the user.
     *
     * @return Display name, e.g. "Food" for {@code FOOD}.
     */
    public String getDisplayName() {
        String name = name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
