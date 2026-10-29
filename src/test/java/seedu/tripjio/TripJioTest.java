package seedu.tripjio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.tripjio.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the whole main loop with fake keyboard input to check separators, saving and budget warnings. */
class TripJioTest {
    private static final String DIVIDER = "-".repeat(60);

    @TempDir
    Path tempDir;

    private Path dataFile;

    /**
     * Runs TripJio on the given input lines and returns everything it printed.
     * When the input runs out, readCommand returns "exit", so the loop always ends.
     */
    private String run(String... inputLines) {
        dataFile = tempDir.resolve("tripjio.txt");
        byte[] input = String.join(System.lineSeparator(), inputLines).getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(input), new PrintStream(output, true, StandardCharsets.UTF_8));
        new TripJio(dataFile.toString(), ui).run();
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private String readDataFile() throws IOException {
        return Files.readString(dataFile, StandardCharsets.UTF_8);
    }

    @Test
    void run_addExpense_savesDataAndPrintsSeparator() throws IOException {
        String output = run("add d/Lunch a/12.50 c/food dt/15-10-2026");

        assertTrue(output.contains("Added expense"));
        assertTrue(readDataFile().contains("E|Lunch|12.50|FOOD|15-10-2026"));
        assertEquals(2, countLines(output, DIVIDER));
    }

    @Test
    void run_invalidCommand_showsErrorThenSeparatorAndAddsNoData() throws IOException {
        String output = run("add d/Lunch a/abc");

        List<String> lines = Arrays.asList(output.split("\n"));
        int errorIndex = indexOfLineStartingWith(lines, "Error: ");

        assertTrue(errorIndex >= 0, "An error line should be printed");
        assertEquals(DIVIDER, lines.get(errorIndex + 1), "The separator should follow the error");

        assertFalse(readDataFile().contains("E|"));
    }

    @Test
    void run_spendingReaches80Percent_showsBudgetWarning() {
        String output = run("budget set a/100 m/10-2026", "add d/Hotel a/85 dt/15-10-2026");

        assertTrue(output.contains("Warning: You have used 80% or more of your budget for October 2026"));
    }

    @Test
    void run_spendingBelow80Percent_noWarning() {
        String output = run("budget set a/100 m/10-2026", "add d/Snack a/10 dt/15-10-2026");

        assertFalse(output.contains("Warning:"));
    }

    @Test
    void run_spendingReaches80PercentOfTripBudget_showsTripBudgetWarning() {
        String output = run("trip add n/Japan s/01-10-2020 e/10-10-2020",
                "trip budget a/100",
                "add d/Train a/85 dt/05-10-2020");

        assertTrue(output.contains("Warning: You have used 80% or more of your budget for trip Japan!"));
    }

    @Test
    void run_spendingExceedsTripBudget_showsTripBudgetExceededWarning() {
        String output = run("trip add n/Japan s/01-10-2020 e/10-10-2020",
                "trip budget a/100",
                "add d/Hotel a/120 dt/05-10-2020");

        assertTrue(output.contains("Warning: You have exceeded your budget for trip Japan!"));
    }

    @Test
    void run_spendingOutsideTripDates_noTripBudgetWarning() {
        String output = run("trip add n/Japan s/01-10-2020 e/10-10-2020",
                "trip budget a/100",
                "add d/BeforeTrip a/90 dt/20-09-2020");

        assertFalse(output.contains("Warning: You have used 80% or more of your budget for trip Japan!"));
    }

    @Test
    void run_editExpenseTriggersTripBudgetWarning() {
        String output = run("trip add n/Japan s/01-10-2020 e/10-10-2020",
                "trip budget a/100",
                "add d/Meal a/10 dt/05-10-2020",
                "edit 1 a/85");

        assertTrue(output.contains("Warning: You have used 80% or more of your budget for trip Japan!"));
    }

    @Test
    void run_splitExpenseTriggersTripBudgetWarning() {
        String output = run("trip add n/Japan s/01-10-2020 e/10-10-2020",
                "trip budget a/100",
                "split d/Dinner a/330 n/3 dt/05-10-2020");

        assertTrue(output.contains("Warning: You have exceeded your budget for trip Japan!"));
    }

    @Test
    void run_exitCommand_stopsBeforeLaterCommands() throws IOException {
        String output = run("exit", "add d/Lunch a/12.50 dt/15-10-2026");

        assertTrue(output.contains("Bye!"));
        assertFalse(output.contains("Added "));
        assertFalse(readDataFile().contains("E|"));
    }

    @Test
    void run_splitAndSettlementPersistAcrossRestarts() throws IOException {
        String first = run("split d/Dinner a/90 n/3 p/Alice,Bob", "exit");
        assertFalse(first.contains("Error:"));
        assertTrue(readDataFile().contains("S|Dinner|90.00|3|30.00|"));

        String second = run("split list", "owe p/Alice", "settle p/Alice a/10", "exit");
        assertFalse(second.contains("Error:"));
        assertTrue(second.contains("Dinner"));
        assertTrue(second.contains("alice owes you: $30.00"));
        assertTrue(readDataFile().contains("alice|10.00"));

        String third = run("owe p/Alice", "settle p/Alice", "exit");
        assertFalse(third.contains("Error:"));
        assertTrue(third.contains("alice owes you: $20.00"));
        String fourth = run("owe p/Alice", "exit");
        assertFalse(fourth.contains("Error:"));
        assertTrue(fourth.contains("All debts are settled!"));
    }

    @Test
    void run_splitReachesThresholdAndExceedsBudget_showsWarningsAndSaves() throws IOException {
        String output = run("budget set a/100 m/10-2026",
                "split d/Dinner a/270 n/3 dt/20-10-2026",
                "split d/Taxi a/33 n/3 dt/20-10-2026");

        assertFalse(output.contains("Error:"));
        assertTrue(output.contains("Warning: You have used 80% or more of your budget for October 2026! "
                + "Spent: $90.00 / $100.00"));
        assertTrue(output.contains("Warning: You have exceeded your budget for October 2026! "
                + "Spent: $101.00 / $100.00"));
        assertTrue(readDataFile().contains("S|Dinner|270.00|3|90.00|"));
        assertTrue(readDataFile().contains("S|Taxi|33.00|3|11.00|"));
    }

    @Test
    void run_splitWithSmallCustomShare_doesNotWarnForFullBill() {
        String output = run("budget set a/100 m/10-2026",
                "split d/Dinner a/300 n/3 my/10 dt/20-10-2026");

        assertFalse(output.contains("Error:"));
        assertFalse(output.contains("Warning:"));
    }

    @Test
    void run_foreignSplit_checksConvertedSgdShare() {
        String output = run("budget set a/100 m/10-2026",
                "split d/Hotel a/90 n/2 cur/EUR rate/2 dt/20-10-2026");

        assertFalse(output.contains("Error:"));
        assertTrue(output.contains("Warning: You have used 80% or more of your budget for October 2026! "
                + "Spent: $90.00 / $100.00"));
    }

    private static int countLines(String output, String line) {
        return (int) Arrays.stream(output.split("\n")).filter(line::equals).count();
    }

    private static int indexOfLineStartingWith(List<String> lines, String prefix) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }
}
