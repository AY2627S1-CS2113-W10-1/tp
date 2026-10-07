package seedu.tripjio.parser;

import seedu.tripjio.exception.TripJioException;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArgumentParserTest {

    @Test
    void constructor_preambleAndPrefixes_splitCorrectly() throws TripJioException {
        ArgumentParser parser = new ArgumentParser("3 a/13.20 c/food");
        assertEquals("3", parser.getPreamble());
        assertEquals("13.20", parser.getRequired("a"));
        assertEquals("food", parser.getRequired("c"));
    }

    @Test
    void constructor_noPrefixes_wholeTextIsPreamble() {
        ArgumentParser parser = new ArgumentParser("  list  ");
        assertEquals("list", parser.getPreamble());
        assertFalse(parser.has("m"));
    }

    @Test
    void constructor_emptyArgs_emptyPreamble() {
        assertEquals("", new ArgumentParser("").getPreamble());
    }

    @Test
    void constructor_anyOrder_sameValues() throws TripJioException {
        ArgumentParser first = new ArgumentParser("d/Lunch a/12.50");
        ArgumentParser second = new ArgumentParser("a/12.50 d/Lunch");
        assertEquals(first.getRequired("d"), second.getRequired("d"));
        assertEquals(first.getRequired("a"), second.getRequired("a"));
    }

    @Test
    void constructor_descriptionWithSpaces_keptWhole() throws TripJioException {
        ArgumentParser parser = new ArgumentParser("d/Lunch at canteen a/5");
        assertEquals("Lunch at canteen", parser.getRequired("d"));
    }

    @Test
    void constructor_dtPrefix_notConfusedWithD() throws TripJioException {
        ArgumentParser parser = new ArgumentParser("d/Lunch dt/02-10-2026");
        assertEquals("Lunch", parser.getRequired("d"));
        assertEquals("02-10-2026", parser.getRequired("dt"));
    }

    @Test
    void constructor_myPrefix_notConfusedWithM() throws TripJioException {
        ArgumentParser parser = new ArgumentParser("my/40 m/10-2026");
        assertEquals("40", parser.getRequired("my"));
        assertEquals("10-2026", parser.getRequired("m"));
    }

    @Test
    void constructor_prefixInsideWord_notTreatedAsPrefix() throws TripJioException {
        // "a/" here is part of the word "Pizza/", not a prefix, because it does not follow a space.
        ArgumentParser parser = new ArgumentParser("d/Pizza/pasta a/5");
        assertEquals("Pizza/pasta", parser.getRequired("d"));
    }

    @Test
    void getOptional_missing_returnsNull() throws TripJioException {
        assertNull(new ArgumentParser("d/Lunch").getOptional("c"));
    }

    @Test
    void getOptional_repeatedPrefix_throws() {
        ArgumentParser parser = new ArgumentParser("c/food c/transport");
        TripJioException e = assertThrows(TripJioException.class, () -> parser.getOptional("c"));
        assertEquals("Prefix c/ is given more than once.", e.getMessage());
    }

    @Test
    void getOptional_emptyValue_throws() {
        ArgumentParser parser = new ArgumentParser("d/Lunch c/");
        TripJioException e = assertThrows(TripJioException.class, () -> parser.getOptional("c"));
        assertEquals("c/ cannot be empty.", e.getMessage());
    }

    @Test
    void getRequired_missing_throws() {
        ArgumentParser parser = new ArgumentParser("d/Lunch");
        TripJioException e = assertThrows(TripJioException.class, () -> parser.getRequired("a"));
        assertEquals("Missing parameter: a/", e.getMessage());
    }

    @Test
    void getAll_repeatedPrefix_returnsAllInOrder() {
        ArgumentParser parser = new ArgumentParser("compare m/09-2026 m/10-2026");
        assertEquals(List.of("09-2026", "10-2026"), parser.getAll("m"));
        assertTrue(parser.getAll("c").isEmpty());
    }

    @Test
    void checkNoPreamble_extraText_throws() {
        assertThrows(TripJioException.class, () -> new ArgumentParser("hello d/Lunch a/5").checkNoPreamble());
    }

    @Test
    void parseAmount_validFormats_parsed() throws TripJioException {
        assertEquals(12.0, ArgumentParser.parseAmount("12"));
        assertEquals(12.5, ArgumentParser.parseAmount("12.5"));
        assertEquals(12.5, ArgumentParser.parseAmount("12.50"));
        assertEquals(1_000_000, ArgumentParser.parseAmount("1000000"));
    }

    @Test
    void parseAmount_invalidFormatsOrRange_throws() {
        for (String text : new String[] {"", "abc", "-5", "1.234", "1,000", ".5", "5.", "0", "1000000.01"}) {
            assertThrows(TripJioException.class, () -> ArgumentParser.parseAmount(text), "Input: " + text);
        }
    }

    @Test
    void parseIndex_valid_parsed() throws TripJioException {
        assertEquals(3, ArgumentParser.parseIndex("3"));
    }

    @Test
    void parseIndex_invalid_throws() {
        for (String text : new String[] {"", "abc", "0", "-1", "+3", "1.5", "99999999999"}) {
            assertThrows(TripJioException.class, () -> ArgumentParser.parseIndex(text), "Input: " + text);
        }
    }

    @Test
    void parseDescription_valid_trimmed() throws TripJioException {
        assertEquals("Lunch", ArgumentParser.parseDescription("  Lunch "));
    }

    @Test
    void parseDescription_emptyOrForbiddenCharacters_throws() {
        for (String text : new String[] {"", "   ", "Pizza/pasta", "A|B"}) {
            assertThrows(TripJioException.class, () -> ArgumentParser.parseDescription(text), "Input: " + text);
        }
    }
}
