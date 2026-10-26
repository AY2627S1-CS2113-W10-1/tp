package seedu.tripjio.ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UiTest {
    @Test
    public void showLine_printsSixtyDashes() {
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PrintStream output = new PrintStream(baos);

        Ui ui = new Ui(input, output);

        ui.showLine();

        String expected = "-".repeat(60) + System.lineSeparator();

        assertEquals(expected, baos.toString());
    }
}
