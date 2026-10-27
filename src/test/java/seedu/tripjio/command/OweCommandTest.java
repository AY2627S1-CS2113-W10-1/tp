package seedu.tripjio.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.tripjio.exception.TripJioException;
import seedu.tripjio.model.AppState;
import seedu.tripjio.parser.Parser;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Checks stable ordering and shared money formatting for both directions of net debt. */
class OweCommandTest {
    private AppState state;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUp() {
        state = new AppState();
        output = new ByteArrayOutputStream();
        ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private void run(String input) throws TripJioException {
        Parser.parse(input).execute(state, ui);
    }

    private String ledger(String input) throws TripJioException {
        output.reset();
        run(input);
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    @Test
    void execute_mixedDebts_sortsAllNamesRegardlessOfInsertionOrder() throws TripJioException {
        run("split d/First a/20 n/2 p/Zoe");
        run("split d/Second a/20 n/2 p/Charlie");
        run("split d/Third a/20 n/2 p/Bob");
        run("split d/Fourth a/20 n/2 p/Alice paid/Alice");
        String expected = "=== Debt Ledger ===\nYou owe alice: $10.00\nbob owes you: $10.00\n"
                + "charlie owes you: $10.00\nzoe owes you: $10.00\n";
        assertEquals(expected, ledger("owe"));
        assertEquals(expected, ledger("owe"));
    }

    @Test
    void execute_largeDebts_formatsBothDirectionsWithGroupingAndFixedLocale() throws TripJioException {
        run("split d/Incoming a/2469 n/2 p/Bob");
        run("split d/Outgoing a/2469 n/2 p/Alice paid/Alice");
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertEquals("=== Debt Ledger ===\nYou owe alice: $1,234.50\nbob owes you: $1,234.50\n",
                    ledger("owe"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void execute_filteredAndSettledBalances_keepsQueryBehaviour() throws TripJioException {
        run("split d/Dinner a/2469 n/2 p/Bob");
        run("split d/Taxi a/20 n/2 p/Alice paid/Alice");
        run("settle p/Bob a/234.50");
        assertEquals("=== Debt Ledger ===\nbob owes you: $1,000.00\n", ledger("owe p/BOB"));
        run("settle p/Bob");
        assertEquals("=== Debt Ledger ===\nAll debts are settled!\n", ledger("owe p/bob"));
        assertEquals("=== Debt Ledger ===\nYou owe alice: $10.00\n", ledger("owe"));
    }
}
