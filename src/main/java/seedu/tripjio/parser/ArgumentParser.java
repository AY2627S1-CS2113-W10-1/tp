package seedu.tripjio.parser;

import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.util.MoneyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits the arguments of a command into the preamble (the text before the first prefix)
 * and the values given for each prefix.
 * For example, {@code "3 a/13.20 c/food"} has the preamble {@code "3"}, {@code a} = {@code "13.20"}
 * and {@code c} = {@code "food"}. Prefixes can be given in any order.
 *
 * <p>Prefixes are written without the slash when calling methods of this class, e.g. {@code has("dt")}.
 * Valid prefixes: {@code d/ a/ c/ dt/ m/ s/ e/ n/ my/}.
 */
public class ArgumentParser {
    /**
     * Matches a prefix at the start of the arguments or after a space.
     * {@code dt} and {@code my} come before {@code d} and {@code m},
     * so that "dt/" is not read as "d/" followed by "t/".
     */
    private static final Pattern PREFIX = Pattern.compile("(?:^|\\s)(dt|my|d|a|c|m|s|e|n)/");

    /** A positive number with at most 2 decimal places, e.g. "12" or "12.50". */
    private static final Pattern AMOUNT_FORMAT = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /** A whole number made only of digits, e.g. "3". Signs such as "+3" or "-1" are rejected. */
    private static final Pattern INDEX_FORMAT = Pattern.compile("^\\d+$");

    private final String preamble;

    /** Values of each prefix, in the order given. A list is used so that repeated prefixes can be detected. */
    private final Map<String, List<String>> values = new HashMap<>();

    /**
     * Splits the given arguments into the text before the first prefix (the preamble) and the values of each prefix.
     *
     * @param args Arguments of a command, without the command word, e.g. "d/Lunch a/12.50 c/food".
     */
    public ArgumentParser(String args) {
        assert args != null : "Arguments must not be null; use an empty string instead";

        Matcher matcher = PREFIX.matcher(args);
        List<Integer> starts = new ArrayList<>();
        List<Integer> valueStarts = new ArrayList<>();
        List<String> prefixes = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
            valueStarts.add(matcher.end());
            prefixes.add(matcher.group(1));
        }
        preamble = (starts.isEmpty() ? args : args.substring(0, starts.get(0))).trim();
        for (int i = 0; i < prefixes.size(); i++) {
            int end = (i + 1 < starts.size()) ? starts.get(i + 1) : args.length();
            String value = args.substring(valueStarts.get(i), end).trim();
            values.computeIfAbsent(prefixes.get(i), k -> new ArrayList<>()).add(value);
        }
    }

    /**
     * Returns the text before the first prefix, with surrounding whitespace removed.
     *
     * @return The preamble, e.g. "3", "set" or "list", or an empty string if there is none.
     */
    public String getPreamble() {
        return preamble;
    }

    public boolean has(String prefix) {
        return values.containsKey(prefix);
    }

    /**
     * Returns every value given for the prefix, in the order they were given.
     * Useful for commands that take the same prefix more than once, e.g. {@code stats compare m/09-2026 m/10-2026}.
     *
     * @param prefix Prefix without the slash, e.g. "m".
     * @return New list of values, which is empty if the prefix was not given.
     */
    public List<String> getAll(String prefix) {
        return new ArrayList<>(values.getOrDefault(prefix, new ArrayList<>()));
    }

    /**
     * Returns the value of an optional prefix that may be given at most once.
     *
     * @param prefix Prefix without the slash, e.g. "c".
     * @return The value, or null if the prefix was not given.
     * @throws TripJioException If the prefix is given more than once, or its value is empty.
     */
    public String getOptional(String prefix) throws TripJioException {
        List<String> prefixValues = values.get(prefix);
        if (prefixValues == null) {
            return null;
        }
        if (prefixValues.size() > 1) {
            throw new TripJioException("Prefix " + prefix + "/ is given more than once.");
        }
        String value = prefixValues.get(0);
        if (value.isEmpty()) {
            throw new TripJioException(prefix + "/ cannot be empty.");
        }
        return value;
    }

    /**
     * Returns the value of a prefix that must be given exactly once.
     *
     * @param prefix Prefix without the slash, e.g. "a".
     * @return The value.
     * @throws TripJioException If the prefix is missing, given more than once, or its value is empty.
     */
    public String getRequired(String prefix) throws TripJioException {
        String value = getOptional(prefix);
        if (value == null) {
            throw new TripJioException("Missing parameter: " + prefix + "/");
        }
        return value;
    }

    /**
     * Checks that there is no text before the first prefix, for commands that do not take a preamble,
     * e.g. it rejects {@code add hello d/Lunch a/5}.
     *
     * @throws TripJioException If the preamble is not empty.
     */
    public void checkNoPreamble() throws TripJioException {
        if (!preamble.isEmpty()) {
            throw new TripJioException("Unexpected text before the parameters: " + preamble);
        }
    }

    /**
     * Parses an amount of money entered by the user.
     * The format is checked here, while the allowed range is checked by {@link MoneyUtil#validateAmount(double)},
     * so the range is defined in only one place.
     *
     * @param text Amount entered by the user, e.g. "12.50".
     * @return The parsed amount.
     * @throws TripJioException If the text is not a positive number with at most 2 decimal places,
     *     or the amount is out of range.
     */
    public static double parseAmount(String text) throws TripJioException {
        if (!AMOUNT_FORMAT.matcher(text).matches()) {
            throw new TripJioException("Amount must be a positive number with at most 2 decimal places.");
        }
        double amount = Double.parseDouble(text);
        MoneyUtil.validateAmount(amount);
        return amount;
    }

    /**
     * Parses an index entered by the user, as shown next to an expense in the {@code list} output.
     *
     * @param text Index entered by the user, e.g. "3".
     * @return The parsed index, starting from 1.
     * @throws TripJioException If the text is not a positive whole number that fits in an {@code int}.
     */
    public static int parseIndex(String text) throws TripJioException {
        String errorMessage = "Index must be a positive whole number.";
        if (!INDEX_FORMAT.matcher(text).matches()) {
            throw new TripJioException(errorMessage);
        }
        int index;
        try {
            index = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // The text has only digits, so this happens only when the number is too large for an int.
            throw new TripJioException(errorMessage);
        }
        if (index == 0) {
            throw new TripJioException(errorMessage);
        }
        return index;
    }

    /**
     * Checks a description entered by the user.
     * '/' is not allowed because it marks a prefix, and '|' is not allowed because it separates fields in the
     * storage file.
     *
     * @param text Description entered by the user.
     * @return The description with surrounding whitespace removed.
     * @throws TripJioException If the description is empty or contains '/' or '|'.
     */
    public static String parseDescription(String text) throws TripJioException {
        String description = text.trim();
        if (description.isEmpty()) {
            throw new TripJioException("Description cannot be empty.");
        }
        if (description.contains("/") || description.contains("|")) {
            throw new TripJioException("Description must not contain '/' or '|'.");
        }
        return description;
    }
}
