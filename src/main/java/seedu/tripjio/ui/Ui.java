package seedu.tripjio.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles all interaction with the user: reading commands and printing responses.
 * The input and output streams can be swapped out, which allows the UI to be tested with fake input.
 */
public class Ui {
    private final Scanner in;

    private final PrintStream out;

    /**
     * Creates a Ui that reads from standard input and writes to standard output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a Ui that reads from and writes to the given streams.
     *
     * @param in Stream to read user input from.
     * @param out Stream to print output to.
     */
    public Ui(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    /**
     * Reads the next line of user input, with leading and trailing whitespace removed.
     * If there is no more input (e.g. end of file is reached), returns "exit" so the application can stop cleanly.
     *
     * @return The trimmed user input, or "exit" if no input remains.
     */
    public String readCommand() {
        if (in.hasNextLine()) {
            return in.nextLine().trim();
        }
        return "exit";
    }

    /**
     * Prints the TripJio banner followed by a welcome message.
     */
    public void showWelcome() {
        out.println("""
            ████████╗██████╗ ██╗██████╗     ██╗██╗ ██████╗
            ╚══██╔══╝██╔══██╗██║██╔══██╗    ██║██║██╔═══██╗
               ██║   ██████╔╝██║██████╔╝    ██║██║██║   ██║
               ██║   ██╔══██╗██║██╔═══╝██   ██║██║██║   ██║
               ██║   ██║  ██║██║██║    ╚█████╔╝██║╚██████╔╝
               ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚════╝ ╚═╝ ╚═════╝

            Hello! Welcome to TripJio, your travel budget buddy.
            What would you like to do?
            """);
    }

    /**
     * Prints the goodbye message shown when the application exits.
     */
    public void showGoodbye() {
        out.println("Bye! Your data has been saved. Safe travels");
    }

    /**
     * Prints each given line on its own line.
     *
     * @param lines Lines of text to print.
     */
    public void showMessage(String... lines) {
        for (String line : lines) {
            out.println(line);
        }
    }

    /**
     * Prints an error message, prefixed with "Error: ".
     *
     * @param message Description of the error.
     */
    public void showError(String message) {
        out.println("Error: " + message);
    }

    /**
     * Returns the given amount as a dollar string with thousands separators and two decimal places.
     *
     * @param amount Amount of money to format.
     * @return Formatted amount, e.g. "$1,234.50".
     */
    public static String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }

}
