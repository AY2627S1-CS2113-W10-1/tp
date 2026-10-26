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

import static org.junit.jupiter.api.Assertions.*;

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
    void run_addExpense_savesDataAndPrintSeparator() throws  IOException {
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

        assertTrue(errorIndex >= 0, "An error line shoule be printed");
        assertEquals(DIVIDER, lines.get(errorIndex + 1), "The separator should follow the error");

        assertFalse(readDataFile(). contains("E| "));

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
    void run_exitCommand_stopsBeforeLaterCommands() throws IOException {
        String output = run("exit", "add d/Lunch a/12.50 dt/15-10-2026");

        assertTrue(output.contains("Bye!"));
        assertFalse(output.contains("Added "));
        assertFalse(readDataFile().contains("E|"));
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
