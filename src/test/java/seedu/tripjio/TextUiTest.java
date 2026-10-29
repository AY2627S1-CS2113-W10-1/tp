package seedu.tripjio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the packaged CLI with file fixtures, including a second process that reloads saved state. */
class TextUiTest {
    @TempDir
    Path workingDirectory;

    /** Checks the complete workflow and then starts a fresh JVM to verify persistence. */
    @Test
    void packagedApp_fullWorkflowAndRestart_matchExpectedOutput() throws IOException, InterruptedException {
        assertEquals(fixture("EXPECTED.TXT"), runApp("input.txt", "workflow"));
        String savedBeforeRestart = Files.readString(workingDirectory.resolve("data/tripjio.txt"));

        assertEquals(fixture("RESTART-EXPECTED.TXT"), runApp("restart-input.txt", "restart"));
        assertEquals(savedBeforeRestart, Files.readString(workingDirectory.resolve("data/tripjio.txt")),
                "Reloading and displaying data must preserve the saved records");
    }

    /** Runs the real entry point in a disposable directory, capturing both streams for failure diagnostics. */
    private String runApp(String inputFixture, String runName) throws IOException, InterruptedException {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path input = workingDirectory.resolve(runName + "-input.txt");
        Path output = workingDirectory.resolve(runName + "-output.txt");
        Path errors = workingDirectory.resolve(runName + "-errors.txt");
        Files.writeString(input, fixture(inputFixture), StandardCharsets.UTF_8);

        Process process = new ProcessBuilder(java.toString(), "-ea", "-Duser.language=en", "-Duser.country=SG",
                "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-jar", System.getProperty("tripjio.test.jar"))
                .directory(workingDirectory.toFile())
                .redirectInput(input.toFile())
                .redirectOutput(output.toFile())
                .redirectError(errors.toFile())
                .start();
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly().waitFor(5, TimeUnit.SECONDS);
        }
        assertTrue(finished, "CLI did not exit within 30 seconds for " + inputFixture);
        assertEquals(0, process.exitValue(), Files.readString(errors));
        return Files.readString(output, StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    /** Reads checked-in UTF-8 fixtures, normalising only platform line endings. */
    private String fixture(String name) throws IOException {
        try (InputStream input = getClass().getResourceAsStream("/" + name)) {
            if (input == null) {
                throw new IOException("Missing CLI fixture: " + name);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        }
    }
}
