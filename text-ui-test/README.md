# Text UI regression tests

Run from the repository root with Java 25:

```text
./gradlew test --tests '*TextUiTest'
```

On Windows, use `.\gradlew.bat` instead of `./gradlew`.
`./gradlew check` runs the same test through JUnit in each existing CI platform job.
Gradle builds `build/libs/tripjio.jar` before running tests.

`TextUiTest` launches the packaged application's real entry point with assertions enabled.
Each test uses a temporary working directory and redirects the input and output to files;
the user's `data/` directory is never read or changed. Both processes have a 30-second
timeout. Output is compared in full as UTF-8, normalising only CRLF to LF.

## Fixtures and coverage

- `input.txt` and `EXPECTED.TXT` cover budget and trip setup, ordinary and foreign
  expenses, named splits, atomic edits, partial and full repayments, filtered/sorted
  lists, statistics, budget warnings, deletion and exit.
- `restart-input.txt` and `RESTART-EXPECTED.TXT` run in a fresh JVM using the saved
  state. They verify trips, exchange rates, remaining debts, monthly budget totals
  and sorted trip expenses. The data file must remain unchanged after these queries.
- Dates are explicitly in January 2000. All expenses are in the past and the trip
  is completed, so outputs do not depend on the current month or remaining days.
- Expected output includes the welcome banner, errors, command separators and goodbye.
  Logging remains in the application's temporary data directory.

## Updating a fixture

Change fixtures only when the intended behaviour or output changes. Review the affected
commands and expected totals, update the corresponding expected-output file, and rerun
the test. Do not replace expected output with actual output just to make a failure pass.
The JUnit report shows the expected and actual output when a comparison fails.
