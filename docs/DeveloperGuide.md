# Developer Guide

## Contents

- [Purpose and implementation status](#purpose-and-implementation-status)
- [Setting up and testing](#setting-up-and-testing)
- [Architecture](#architecture)
- [Design & implementation](#design--implementation)
  - [Expense management](#expense-management)
  - [Monthly budgets and warnings](#monthly-budgets-and-warnings)
  - [Shared-bill expenses](#shared-bill-expenses)
  - [Trip management and selected-trip reporting](#trip-management-and-selected-trip-reporting)
  - [Monthly statistics and comparison](#monthly-statistics-and-comparison)
  - [Manual currency conversion](#manual-currency-conversion)
- [Maintaining the diagrams](#maintaining-the-diagrams)
- [Product scope](#product-scope)
- [User stories](#user-stories)
- [Non-functional requirements](#non-functional-requirements)
- [Glossary](#glossary)
- [Manual testing](#instructions-for-manual-testing)

## Purpose and implementation status

TripJio is a local command-line spending tracker for students on exchange. This
guide describes the code in this checkout, not every feature in the team's roadmap.
It is intended for developers maintaining parsers, commands, models and persistence.
For command syntax and end-user instructions, please see the [User Guide](UserGuide.md).

Implemented command families are expense add/list/edit/delete, split creation and
listing, monthly budget configuration/status/warnings, named-trip management,
manual exchange rates, monthly statistics/comparisons and selected-trip reports.
Named split participants, repayments (`owe`/`settle`), foreign-currency splits,
`edit ... my/`, trip budgets/summary, forecasting, export and recurring expenses
are planned work, not available commands in this checkout. The global command-output
separator and aligned monthly comparison table are implemented.

## Setting up and testing

Use JDK 25 and the checked-in Gradle wrapper from the repository root. Open PowerShell
in that directory on Windows. These commands run the app, check it, build the JAR
and run the packaged app respectively; exit an interactive run before continuing:

```powershell
.\gradlew.bat run
.\gradlew.bat test checkstyleMain checkstyleTest
.\gradlew.bat shadowJar
java -jar build/libs/tripjio.jar
```

On macOS/Linux, use `./gradlew` in place of `.\gradlew.bat`. The entry point is
`seedu.tripjio.TripJio`. Gradle uses JUnit Jupiter for tests and Checkstyle for Java
style checks. Test results are written to `build/reports/tests/test/index.html`.
The build does not currently configure JaCoCo: the test report is not a coverage report.

The data path is `data/tripjio.txt`, relative to the process working directory.
Run manual experiments in a separate directory with a copy of the packaged JAR;
do not delete or overwrite real spending data to obtain a clean test environment.

## Architecture

The application runs synchronously in one terminal process. `TripJio` constructs
`Ui`, `Storage` and the loaded `AppState`, then repeatedly reads, parses and executes
a command. There is no web backend, account service or live exchange-rate API.

| Component | Main types | Responsibility |
| --- | --- | --- |
| Application | `TripJio` | Coordinate startup, command execution, warnings, saving and exit |
| Presentation | `Ui` | Read input and display command results/errors |
| Parsing | `Parser`, feature parsers, `ArgumentParser` | Validate CLI syntax and create commands |
| Commands | `Command` subclasses | Execute an operation against shared state and report its result |
| Model | `AppState`, `ExpenseList`, `Budget`, `TripBook`, `ExchangeRates` | Store and calculate domain data |
| Persistence | `Storage` and model serialisation methods | Load/save sectioned UTF-8 text |
| Utilities | `MoneyUtil`, `DateUtil` | Shared validation, rounding and formatting |

`Parser.parse` dispatches on the case-insensitive first command word. Feature
parsers handle subcommands and allowed prefixes. `Command.execute(state, ui)` is
the common polymorphic entry point. The main loop calls `getAffectedMonth()` to
decide whether to check a monthly budget and `isMutating()` to decide whether to
save. Both default to no action (`null` and `false` respectively). Only exit commands
return true from `isExit()`. The main loop also calls `Ui.showLine()` after each
command, including errors, to separate successive responses.

Commands depend on models and UI, while domain calculations do not print. Storage
is a deliberate boundary exception: `load(Ui)` reports invalid-line warnings through
the UI. The architecture does not introduce separate service interfaces or a database.

### Error handling and persistence boundaries

Expected input/domain errors use checked `TripJioException`. The main loop displays
the message and continues. Parsing failures occur before execution; model validation
should precede mutation. Constructors that rely on parser validation are not a
substitute for validating user input at the parser or storage boundary.

After a successful mutating command, `Storage.save` rewrites the full file. It is
not an atomic temporary-file replacement and there is no automatic backup or rollback.
A save failure does not reverse an in-memory mutation. A missing data file starts
an empty state; an unreadable file reports an error and also starts empty. Invalid
records are skipped with warnings. Back up and investigate damaged files before
issuing further mutating commands, which could overwrite recoverable records.

## Acknowledgements

The implementation-section organisation is adapted from the
[AB-3 Developer Guide example](https://se-education.org/addressbook-level3/DeveloperGuide.html#proposed-undoredo-feature).
Diagrams are authored in PlantUML and stored with their rendered SVGs. The project
uses Gradle, JUnit Jupiter, Checkstyle and the Shadow Gradle plugin as declared in
`build.gradle`. Contributors should add attribution here when introducing other
reused code, assets or documentation.

## Design & implementation

Each enhancement below describes its implementation, an example scenario, and the
trade-offs behind the design. Diagrams deliberately omit routine getters and utility
calls when they do not help explain the behaviour. Method parameters shown as `...`
are omitted for readability; notes identify other simplifications.

### Expense management

#### Implementation

`ExpenseCommandParser` handles `add`, `list`, `edit` and `delete`. It constructs a
concrete command after checking required fields, optional fields and allowed
prefixes. `ArgumentParser` centralises common parsing; `DateUtil` and `MoneyUtil`
avoid separate date and amount conventions across features. Expense dates cannot
be in the future. Dates use `DD-MM-YYYY` and month filters use `MM-YYYY`.

`AppState` exposes one `ExpenseList` shared by all commands. `AddCommand` holds the
new `Expense` and appends it during execution. `EditCommand` stores optional replacement
fields; null means unchanged. It looks up the one-based global index and changes the
amount first, because subtype amount validation may reject the edit. Remaining
parser-validated fields are then applied. After execution, the affected month is
the expense's final month. Moving an expense to another month does not require an
overspending warning for the old month because its spending has decreased.

`ListCommand` builds a temporary list of global indexes. Category, month and trip
filters determine membership; a comparator optionally sorts by amount or date.
Amount sorting uses personal SGD amounts via `getAmount()`; stable sorting preserves
insertion order for equal keys. It does not reorder `ExpenseList`. A displayed index identifies the global entry,
not the position in the filtered view. Deleting an entry can shift subsequent global
indexes, so users should list again before a second index-based operation.

#### Example scenario

1. `add d/Lunch a/12 c/food dt/01-10-2026` creates an ordinary expense.
2. `list sort/amount order/desc` displays entries by spending while retaining indexes.
3. `edit 1 a/15` delegates to the existing object's `setAmount`; it does not replace
   a split or foreign expense with an ordinary expense.
4. The main loop checks the affected month's budget and persists the modified state.

#### Design considerations

Aspect: Identifying expenses in reports.

- Alternative 1 (current choice): Temporary views retaining global indexes.
  - Pros: One shared collection avoids divergent copies and filtering preserves indexes.
  - Cons: Deleting entries shifts indexes; they are not permanent identifiers.
- Alternative 2: Persistent expense IDs.
  - Pros: Stable references across deletion and export.
  - Cons: Requires new storage fields and different CLI semantics.

Aspect: Editing different expense types.

- Alternative 1 (current choice): Polymorphic `Expense.setAmount`.
  - Pros: Each subtype owns its validation.
  - Cons: Input meanings differ: full bill for a split, original currency for a foreign expense.
- Alternative 2: Type checks inside `EditCommand`.
  - Pros: All edit rules are visible in one command.
  - Cons: New subtypes require command changes and may duplicate model rules.

`ExpenseCommandsTest`, `ExpenseCommandParserTest`, `ArgumentParserTest` and
`TravelFeaturesTest` exercise command behaviour, invalid input and subtype integration.

### Monthly budgets and warnings

#### Implementation

The monthly budget component comprises command parsing (`BudgetCommandParser`), the domain model (`Budget`),
command execution (`SetBudgetCommand`, `BudgetStatusCommand`), and automated post-execution evaluation (`BudgetChecker`).

`BudgetCommandParser` dispatches on the subcommand action word (`set` or `status`):
* `set`: Validates allowed prefixes (`a/` and optional `m/`). Amounts are parsed with `ArgumentParser.parseAmount`
  and validated through `MoneyUtil.validateAmount` (must be $> 0$ and $\le \$1,000,000.00$). The optional month is
  parsed via `DateUtil.parseMonth`.
* `status`: Validates the optional `m/` prefix.

`Budget` stores an optional default monthly amount (`Double defaultBudget`) and a `TreeMap<YearMonth, Double>`
of monthly overrides. `getBudgetFor(month)` returns a month's override first, then the default, or `null` when neither
exists. `null` represents an unset budget, distinct from a zero budget. In storage, the `[budget]` block writes
`default=AMOUNT` followed by sorted `MM-YYYY=AMOUNT` lines. `TreeMap` guarantees chronological ordering in saved files,
ensuring deterministic persistence.

![Budget class diagram](images/BudgetClassDiagram.svg)

`SetBudgetCommand` modifies either the default budget or a specific month's override in `Budget` and displays a
confirmation message through `Ui`. It returns `true` from `isMutating()`, prompting `TripJio` to save the state via
`Storage.save(state)`. It does not override `getAffectedMonth()`, so configuring a budget does not trigger an
overspending warning.

![Setting a monthly budget sequence](images/SetBudgetSequence.svg)

`BudgetStatusCommand` reads the effective budget for the requested month via `Budget.getBudgetFor(month)` and sums
the month's expenses using `ExpenseList.getTotalSpentInMonth(month)`. It reports the total budget, amount spent, and
either remaining funds or the amount exceeded. For the ongoing month (where `month.equals(YearMonth.from(today))`),
it additionally computes:
* `daysLeft = month.lengthOfMonth() - today.getDayOfMonth() + 1` (including today)
* `dailyAllowance = (remaining > 0) ? MoneyUtil.roundToCents(remaining / daysLeft) : 0.0`

A secondary constructor `BudgetStatusCommand(YearMonth month, LocalDate today)` injects an explicit reference date,
making date-boundary and leap-year tests deterministic.

![Viewing budget status sequence](images/BudgetStatusSequence.svg)

After every mutating expense command (`add`, `edit`, `split`), the `TripJio` main loop retrieves `command.getAffectedMonth()`
and queries `ExpenseList.getTotalSpentInMonth(month)`. It then invokes `BudgetChecker.check(budget, spent, month)`:
* If spending strictly exceeds the budget, an **exceeded warning** is produced (`Warning: You have exceeded your budget...`).
* Otherwise, if spending meets or exceeds the cent-rounded 80% threshold (`MoneyUtil.roundToCents(budget * 0.8)`), an
  **80% warning** is produced (`Warning: You have used 80% or more of your budget...`).
* Exactly 100% spending takes the 80% threshold branch with `$0.00 remaining`, not the exceeded branch.
* Spending below 80% or months without a configured budget return `null` (no warning displayed).

`BudgetChecker` returns a formatted `String` rather than printing directly, maintaining clean separation between the
model layer and the user interface.

![Monthly budget warning sequence](images/BudgetWarningSequence.svg)

#### Example scenario

1. The user enters `budget set a/1000`. `SetBudgetCommand` sets `defaultBudget = 1000.0`.
2. The user enters `budget set a/500 m/12-2026`. `SetBudgetCommand` adds an entry for `2026-12` in `monthlyBudgets`.
3. The user adds an expense on `15-12-2026` of $420. `TripJio` queries `getAffectedMonth()` (`2026-12`), fetches
   the December budget ($500), and calls `BudgetChecker.check`. Since $420 $\ge$ $400 (80%), an 80% warning is shown.
4. The user enters `budget status m/12-2026`. `BudgetStatusCommand` shows $500 budget, $420 spent, and $80 remaining.
5. Inquiries for any other month (e.g. `budget status m/11-2026`) fall back to the default $1,000 budget.

#### Design considerations

Aspect: Configuring monthly budgets.

- Alternative 1 (current choice): Default with monthly overrides.
  - Pros: Avoids repetitive monthly setup while allowing flexibility for unusual or travel months.
  - Cons: Requires an explicit fallback check (`monthly override ?? defaultBudget ?? null`).
- Alternative 2: Explicit budgets for every individual month.
  - Pros: Simpler data structure (only a map without default fallback).
  - Cons: Burdens users with configuring every month from scratch; unconfigured months have no budget.

Aspect: Triggering budget warnings.

- Alternative 1 (current choice): Centralized post-execution hook in `TripJio`.
  - Pros: Decouples commands from budget policy; `add`, `edit`, and `split` reuse one unified warning mechanism.
  - Cons: Requires commands that affect spending to implement `getAffectedMonth()`.
- Alternative 2: Each command evaluates and prints its own warnings during `execute`.
  - Pros: Warnings occur directly inside the command logic.
  - Cons: Duplicates threshold logic across commands and tightly couples command execution to `BudgetChecker`.

Aspect: Daily allowance scope in `budget status`.

- Alternative 1 (current choice): Compute daily allowance only when viewing the current ongoing month.
  - Pros: Daily pace is only actionable for the current active period; past months are closed and future months have not started.
  - Cons: Users querying other months do not see pacing figures.
- Alternative 2: Display daily allowance for any queried month.
  - Pros: Output fields remain identical regardless of the target month.
  - Cons: Displaying a "daily allowance" for a completed historical month is confusing and mathematically artificial.

`BudgetTest`, `BudgetCheckerTest`, `SetBudgetCommandTest` and `BudgetStatusCommandTest`
cover overrides, thresholds, missing budgets, parsing, and date-dependent allowance calculations.

### Shared-bill expenses

#### Implementation

`SplitCommandParser` accepts a description, full SGD bill and participant count,
plus an optional personal share. The count must be between 2 and 50. A custom
share must be positive and no greater than the bill. With no custom share, the
equal share is rounded to cents and must be at least $0.01. Category defaults to
`OTHER` and date defaults to today. Unsupported prefixes, including named people
and foreign currency, are rejected by the current parser.

`SplitCommand.execute` constructs a `SplitExpense`, appends it to the shared
`ExpenseList` and prints the result. `SplitExpense` extends `Expense`: the inherited
amount stores the user's share, while the subtype stores the full bill, participant
count and whether the share was custom. `getAmount()` is inherited, not overridden.
Consequently budgets, trip totals and statistics count only the user's share without
knowing that the item is a split bill.

![Split creation and shared expense storage sequence](images/SplitSequence.svg)

`SplitExpense.setAmount` interprets its argument as a replacement full bill. For
an equal split it recalculates the share; for a custom split it preserves the share
and rejects a bill smaller than it. Validation precedes mutation. `split list`
restricts results to split instances, optionally intersecting month/trip filters,
while retaining global expense indexes.

#### Example scenario

1. `split d/Dinner a/90 n/3 c/food dt/01-10-2026` records a $30 personal expense.
2. `edit 1 a/120` changes an equal split's personal amount to $40.
3. `split d/Taxi a/50 n/3 my/10 dt/01-10-2026` records only $10 for the user.
4. Editing that taxi bill below $10 fails; raising its bill preserves the $10 share.

An `S|` record stores description, full bill, people, personal share, category and
date. The loader validates each record and infers whether the share is custom by
comparing it with the rounded equal share. It does not store the custom/equal flag.
Thus a deliberately custom share equal to the equal share reloads as an equal split.
This is a current format limitation, not a guarantee of preserving user intent.

#### Design considerations

Aspect: Representing shared bills.

- Alternative 1 (current choice): A split is an expense subtype.
  - Pros: Existing reports automatically use the personal cost.
  - Cons: Full bill and personal share require clearly distinguished meanings.
- Alternative 2: Store split bills separately.
  - Pros: Isolates group-specific operations.
  - Cons: Reports must merge collections and avoid double counting.

Aspect: Scope of repayment tracking.

- Alternative 1 (current choice): Personal spending only.
  - Pros: No debt ledger to reconcile.
  - Cons: No named debtors or allocation of leftover cents among participants.
- Alternative 2 (planned): Separate named debts and settlements.
  - Pros: Tracks repayments without counting them again as expenses.
  - Cons: Requires a rounding-allocation rule, new storage and consistency tests.

`SplitCommandParserTest` and `SplitExpenseTest` cover input rules and split calculations;
storage and reporting tests cover reuse of the same expenses. Direct construction
relies on validated inputs, so new entry points must not bypass boundary checks.

### Trip management and selected-trip reporting

#### Implementation

Trip management is facilitated by `TripBook`, which stores parallel lists of names
and `Trip` objects and an optional selected index. Commands extend `Command` and
access the same model through `AppState`; they do not maintain separate trip data.
The following partial class diagram shows the model relationships relevant to these features.
Unrelated budget and exchange-rate configuration classes are omitted for clarity.

![Trip management and reporting class diagram](images/TripModel.svg)

The principal operations are:

- `TripBook#edit(index, name, start, end)` — validates and applies supplied fields;
  omitted fields retain their existing values.
- `TripBook#delete(index)` — removes a trip and adjusts or clears the selection,
  without deleting expenses.
- `TripBook#hasCurrent()` / `getCurrent()` — guard and retrieve the optional selection.
- `TripBook#toStorageLines()` / `loadStorageLine(line)` — serialise and restore named trips.
- `ExpenseFilter#select(state, category, month, tripOnly)` — returns the intersection
  of the supplied filters in a fresh list, using the selected trip at execution time.
- `Statistics#getCategoryTotals(expenses)` — totals filtered expenses by category in SGD.

##### Validation and report integration

`TripCommandParser` validates the index, allowed prefixes, duplicate/empty values
and date syntax for `trip edit` and `trip delete`. Separate commands extend
`Command`, delegate mutation to `TripBook`, and print through `Ui`.
Both return true from `isMutating()` for automatic saving. They leave
`getAffectedMonth()` null because neither changes expense spending.

`TripBook.edit` validates the name against every other trip and validates the
combined old/new dates through `Trip.set` before renaming. The same Trip object,
selection and omitted fields are preserved. Failure changes nothing.
`TripBook.delete` removes the matching name/trip pair: deleting the selected
position sets selection to -1; deleting a preceding position decrements selection.
Deleting after it leaves selection unchanged. No expenses or budgets are removed.

`ArgumentParser.getFlag("trip")` handles the valueless flag without relaxing
validation of ordinary required/optional values. Only list, stats and split list
allow it; comparisons and mutating commands reject it.

`ExpenseList.getExpensesBetween(start, end)` returns a fresh insertion-ordered
list including both endpoints; invalid/null ranges throw IllegalArgumentException.
`ExpenseFilter.select(state, category, month, tripOnly)` is the shared filtering
entry point for commands and future export. It validates selection before returning
results, resolves it at execution time, and intersects trip dates/month/category.
The returned list is independent, but contains the original Expense objects.

List and split list retain indexes from the global expense collection rather than
renumbering filtered results. TripStatsCommand is read-only and uses
`Statistics.getCategoryTotals(filteredExpenses)`, which also backs monthly totals,
so foreign amounts and split shares continue through polymorphic getAmount().
The existing StatsCommand clock-based default and StatsCompareCommand are unchanged.

##### Date configuration and lifecycle

`setCurrentDates(start, end)` creates `Original trip` only when the book is empty.
Otherwise it updates the explicitly selected trip, or rejects the command when
selection is absent. `getCurrent()`, `getCurrentName()` and `AppState.getTrip()` can
return null, so callers must guard them with `hasCurrent()`.

`TripStatusCommand` is read-only. It combines `Trip` lifecycle calculations with
`ExpenseList.getTotalSpentBetween`. Inclusive duration is the number of days between
the endpoints plus one. Remaining days equal the duration before departure, include
today during the trip, and are zero after completion. Status is Upcoming, Ongoing
or Completed. An injected `Clock` keeps command tests deterministic; the model
receives an explicit reference date. `Trip.contains(date)` returns false for null
dates or unset trips, otherwise checks the inclusive bounds.

#### Example scenario

##### Editing and deleting trips

Step 1. The user adds Japan for 01–14 December 2026 and Korea for 20–25 December
2026. The book contains `[Japan, Korea]`; Korea is selected (`selected = 1`).
Displayed trip indexes are one-based; the internal selected index is zero-based.

Step 2. The user runs `trip edit 1 n/Japan e/15-12-2026`. The parser checks the
arguments and constructs `TripEditCommand`. `TripBook.edit` checks the index and
name, then asks `Trip.set` to validate the combined date range before updating it.
The Japan trip retains its object identity and start date; Korea remains selected.
The main loop saves the successful mutation. The sequence is shown below.

![Trip edit execution sequence](images/TripEditSequence.svg)

Step 3. The user runs `trip delete 1`. Japan is removed and Korea becomes the first
trip. Its selected index changes from 1 to 0, so the same trip remains selected.

Step 4. The user runs `trip delete 1` again. Korea is removed and `selected` becomes
-1. Saving writes an empty `[trips]` section with no selection record. Restarting
does not resurrect either trip. Expenses remain available to unfiltered reports.
The following object snapshots show the selection before and after each deletion;
arrows between snapshots indicate progression, not object associations.

![Trip deletion state snapshots](images/TripDeletionSnapshots.svg)

> A parsing or model-validation failure displays an error and skips saving.
> An I/O failure during saving is different: the model has already changed in memory.
> There is no undo/redo feature or transactional rollback of a failed save.

##### Selected-trip reporting

Step 1. In a separate scenario, the user adds/selects Japan for 01–15 October 2026
and records expenses before, during and after those dates. Use past expense dates
when reproducing this scenario; future expenses are rejected.

Step 2. The user runs `stats trip/`. `StatsCommandParser` creates a
`TripStatsCommand` with no month restriction. The command asks `ExpenseFilter`
for expenses within the selected trip, including both boundary dates.

Step 3. `Statistics` sums each expense's polymorphic `getAmount()`: a split
contributes the user's share, and a foreign expense contributes its SGD value.
The command prints category totals and total spending without saving or mutating data.

![Selected-trip statistics sequence](images/TripFilterSequence.svg)

Step 4. Adding `m/10-2026` intersects the trip range with October. `list trip/`
and `split list trip/` reuse the same filter; list commands retain original expense
indexes, including after sorting, so subsequent edit/delete commands target the
same global entries. An empty intersection is a valid empty report, whereas no
selected trip produces an error before report output.

##### Storage compatibility

On loading, `Storage` checks for a `[trips]` header before interpreting legacy data.
If present, named-trip records are used and `[trip]` is ignored, even when the named
section is empty. Otherwise, a complete valid legacy trip becomes `Original trip`.
Selection records are applied after trip records. Saving writes only `[trips]`,
with `T|NAME|START|END` records and an optional `A|NAME` selection record.
No selection record means no selection. An invalid selection reference warns and
clears selection. Names rather than indexes identify the persisted selection, so
skipped malformed trip records do not accidentally select a different trip.
A legacy incomplete pair is not imported; a reversed pair is cleared with a warning.
This prevents an old copy of a selected trip from restoring a deleted trip.

#### Design considerations

Aspect: How trips relate to expenses.

- Alternative 1 (current choice): Filter by the selected trip's inclusive dates.
  - Pros: Reuses existing expense data without changing expense storage; overlapping
    trips can report the same expense.
  - Cons: Expenses cannot be assigned exclusively to one trip; editing trip dates
    changes which expenses appear in its reports.
- Alternative 2: Store a trip identifier on each expense.
  - Pros: Supports explicit attribution even when dates overlap.
  - Cons: Requires expense migration and additional rules when a trip is deleted.

Aspect: What happens when the selected trip is deleted.

- Alternative 1 (current choice): Clear selection and require explicit selection.
  - Pros: Avoids silently reporting spending for a different trip or restoring a deleted one.
  - Cons: The user must select or add a trip before using trip-specific reports.
- Alternative 2: Automatically select another remaining trip.
  - Pros: Reduces the number of follow-up commands.
  - Cons: Subsequent reports may refer to a trip the user did not intend.

Aspect: Where filtering is implemented.

- Alternative 1 (current choice): Share `ExpenseFilter` across report commands.
  - Pros: Keeps date boundaries and missing-selection errors consistent; reusable by export.
  - Cons: Changes to the helper require regression tests for every consumer.
- Alternative 2: Implement filtering independently in each command.
  - Pros: Each command can evolve independently.
  - Cons: Duplicates validation and risks inconsistent results.

#### Verification

`TripEditDeleteCommandTest` covers atomic validation, index shifts and selection.
`TripMigrationTest` covers legacy, mixed and current storage formats.
`TripFilterCommandTest` covers date boundaries, combined filters, missing selection,
SGD amounts and unchanged source data. Shared parser/list changes need their owners'
review before integration.

### Monthly statistics and comparison

#### Implementation

`Parser` delegates `stats` to `StatsCommandParser`. It uses `ArgumentParser` to
reject extra or repeated parameters and `DateUtil.parseMonth` for strict month
validation. `StatsCommand` extends `Command` and inherits its read-only behaviour.
Its optional injected `Clock` makes default-month tests deterministic; an explicit
month overrides that clock.

`Statistics.getMonthlyTotals` filters through `ExpenseList.getExpensesInMonth` and
returns a fresh `EnumMap<Category, Double>` containing every category. It totals
`Expense.getAmount()` so split bills contribute the user's share, then rounds each
category to cents. `StatsCommand` formats the results using the shared money and
date utilities and sums the displayed category amounts for a consistent grand total.
Calculations are separate from parsing and display and reused by `StatsCompareCommand`.

For `stats compare`, `StatsCommandParser` recognises the case-insensitive `compare`
preamble and uses `ArgumentParser.getAll("m")` to require exactly two months in input
order. Both are validated before constructing the command. Other parameters and
unexpected text are rejected without changing the shared argument parser.

`StatsCompareCommand` extends `Command` with its default read-only behaviour and
encapsulates two non-null `YearMonth` values. It obtains two fresh category maps from
`Statistics.getMonthlyTotals`, then displays their values and cent-rounded changes
(second minus first). Totals sum the displayed category amounts. Equal or reversed
months are valid; empty months yield zeros. No percentage calculation is needed, so
a zero baseline has no division-by-zero case. Expense polymorphism preserves split
shares without changes to split, budget or storage code. Output uses four aligned
columns (category, first month, second month and change), with ASCII horizontal
separators. Positive changes have a plus sign, negative changes a minus sign, and
zero changes no sign. Column widths are minimum widths, not truncation limits.

#### Example scenario

1. Suppose Food spending is $90 in September and $100 in October 2026.
2. `stats m/09-2026` obtains the September category map and displays a $90 Food total.
3. `stats compare m/09-2026 m/10-2026` obtains both maps and displays a +$10 Food change.
4. Reversing the months gives -$10. Comparing a month with itself gives zero changes;
   missing categories and empty months contribute zero rather than disappearing.

#### Design considerations

Aspect: Reuse of totals.

- Alternative 1 (current choice): Share category calculations in `Statistics`.
  - Pros: Monthly, comparison and trip reports use the same personal-SGD semantics.
  - Cons: Changes in rounding affect several reports and need regression tests.
- Alternative 2: Sum amounts inside each command.
  - Pros: Each report can choose its own calculation independently.
  - Cons: Duplicates logic and risks inconsistent totals for the same expenses.

Aspect: Representing change.

- Alternative 1 (current choice): Display an absolute second-minus-first amount.
  - Pros: Defined even when the first month has no spending.
  - Cons: Does not convey relative growth.
- Alternative 2: Add percentage changes.
  - Pros: Makes categories of different sizes easier to compare.
  - Cons: Needs an explicit zero-baseline rule and additional output conventions.

`StatsCommandTest` and `StatsCompareCommandTest` cover totals and comparison behaviour.

### Manual currency conversion

#### Implementation

`ExchangeRates` stores manually configured SGD-per-unit decimal rates. During
execution, `AddForeignExpenseCommand` looks up the configured rate and constructs a
`ForeignExpense` before adding it to the shared collection. Missing/invalid rates
or invalid converted amounts prevent insertion. The subtype retains the original
amount, currency and rate snapshot; its inherited amount is the SGD value consumed
by budgets, sorting and statistics.

Conversion uses `BigDecimal` multiplication followed by half-up rounding to SGD
cents. The original amount must be positive, finite and at most two decimal places;
the $1,000,000 cap applies to the converted SGD amount, not the foreign number.
`ForeignExpense.setAmount` converts using its saved rate and validates the result
before changing either amount. Changing a configured rate therefore does not
revalue historical purchases. An `F|` record saves the original amount and snapshot
rate, allowing restoration independently of current configuration.

#### Example scenario

1. Set `rate set from/JPY to/SGD r/0.01`.
2. Add a JPY 1000 purchase: its personal SGD amount is $10.
3. Change the configured rate to 0.02: the existing expense stays at $10; a new
   JPY 1000 purchase is $20.
4. Edit the first purchase to JPY 2000: it becomes $20 using its original 0.01 rate.
   Saving and restarting preserves both expenses' independent rate snapshots.

#### Design considerations

Aspect: When to resolve the rate.

- Alternative 1 (current choice): Snapshot a manual rate when creating the expense.
  - Pros: Offline operation and reproducible historical spending after rate updates.
  - Cons: Users must enter rates and cannot automatically retrieve market prices.
- Alternative 2: Look up the latest rate whenever reporting.
  - Pros: Reports could reflect current conversion values.
  - Cons: Historical budgets would change without expense edits; live lookup also
    introduces availability and reproducibility problems.

The base reporting currency remains SGD. Foreign-currency splits are planned, not
implemented. `TravelFeaturesTest` covers conversion, rate changes and persistence.

## Maintaining the diagrams

Editable PlantUML sources are in `docs/diagrams`; rendered SVGs are in `docs/images`.
Regenerate them with `java -jar PATH_TO_PLANTUML.jar -tsvg -o ../images docs/diagrams/*.puml`
from the repository root (the output path is relative to the diagram source folder).
The class diagram uses PlantUML's bundled Smetana layout engine, so Graphviz is not required.
Update both source and image when the implementation changes.


## Product scope

### Target user profile

Students on exchange who prefer keyboard-driven expense entry, need to track their
own share of group spending and travel costs, and can maintain data on one computer.

### Value proposition

TripJio combines personal expenses, monthly budgets, manual currency conversion and
date-based trip reports in one offline CLI. It is not a booking platform, housing
search service, bank integration or cloud-synchronised financial account.

## User Stories

| Status | As a ... | I want to ... | So that I can ... |
| --- | --- | --- | --- |
| Implemented | new user | view command help | learn the accepted input formats |
| Implemented | exchange student | record and correct expenses | keep accurate spending records |
| Implemented | traveller | record foreign purchases using a saved rate | compare costs in SGD |
| Implemented | group traveller | record my share of a bill | avoid counting the entire group bill |
| Implemented | budget-conscious student | receive monthly budget warnings | recognise overspending |
| Implemented | traveller | manage trips and filter by their dates | review trip-specific costs |
| Implemented | student | compare two months by category | understand spending changes |
| Planned | group traveller | track named debts and repayments | know who still owes money |
| Planned | student | export filtered spending | analyse it outside the app |

## Non-Functional Requirements

The following are design targets and maintenance constraints, not measured benchmarks:

- Run on JDK 25 using a text terminal; require no network for ordinary commands.
- Preserve valid records across restart using UTF-8, including non-ASCII descriptions.
- Reject malformed CLI input with a useful error instead of terminating the session.
- Keep money presentation consistent in SGD to two decimal places; use shared utilities.
- Keep old supported storage formats readable and regression-test migrations.
- Keep domain calculations separate from terminal output and test time-dependent logic
  with explicit dates/clocks where supported. Some parser defaults still use system time.
- Maintain JUnit tests and Checkstyle checks for changes. No latency/load benchmark
  has been established; filtering and full-file saving scale with the amount of data.
- Treat the file as single-user, unencrypted local data. Concurrent writers, access
  control, encryption and cloud backup are not implemented guarantees.

## Glossary

- **CLI:** Command-line interface accepting text commands.
- **SGD:** Singapore dollar; the reporting currency.
- **Global index:** One-based position in the complete expense or trip list.
- **Selected trip:** The explicitly active trip; selection may be absent.
- **Personal share:** The portion of a split bill counted as the user's expense.
- **Rate snapshot:** Conversion rate stored with a foreign expense at creation time.
- **Mutating command:** A command whose successful execution requests saving.
- **Affected month:** Month returned by a command for the post-execution budget check.
- **Legacy storage:** Older `[trip]` data accepted for backward compatibility.

## Instructions for manual testing

Use an isolated working directory with `tripjio.jar` and no existing `data` folder.
The fixed expense dates below must be in the past when testing. Each scenario states
its own setup; start with a fresh isolated directory when indexes would otherwise differ.

### Launch, persistence and invalid input

1. Run `java -jar tripjio.jar`, then `help`. Check that available commands are listed.
2. Run `add d/Lunch a/12 c/food dt/01-10-2026`, then `exit` and restart from the same directory.
3. Run `list`. The lunch remains. Run `add d/Bad a/-1`; an error appears and no entry is added.
4. Back up the test data file before experimenting with corrupt records. A malformed
   expense line should produce a skipped-line warning on restart while valid records load.

### Budget boundaries

1. In a fresh instance, run `budget set a/100 m/10-2026`.
2. Add $79 on 01-10-2026: no threshold warning. Add $1 on the same date: 80% warning.
3. Add $20: threshold warning with $0 remaining, not an exceeded warning.
4. Add $1: exceeded warning. `budget status m/10-2026` reports $101 spent and $1 over.

### Split calculations

1. In a fresh instance, run `split d/Dinner a/90 n/3 c/food dt/01-10-2026`.
2. Run `stats m/10-2026`; Food and total spending are $30, not $90.
3. Run `edit 1 a/120`; spending becomes $40. `split list` retains global index 1.
4. `split d/Invalid a/10 n/1` fails without changing the list.

### Trips, filtering and selection

1. Add trips with `trip add n/Japan s/01-10-2026 e/15-10-2026` and
   `trip add n/Korea s/20-10-2026 e/25-10-2026`.
2. Run `trip select 1`, add expenses on 01, 15 and 16 October 2026, then `list trip/`.
   Only the first two dates match; `stats trip/` uses the same inclusive range.
3. Run `trip edit 1 e/16-10-2026`; all three now match. Try an end before the start;
   the edit fails and the original range remains.
4. Run `trip delete 1`. No trip is selected; `stats trip/` reports an error.
5. Select and delete the remaining trip, restart, then `trip list`. No deleted trip
   returns, and unfiltered `list` still shows the expenses.

### Rates and historical amounts

1. In a fresh instance, run `rate set from/JPY to/SGD r/0.01` and
   `add d/Train a/1000 cur/JPY c/transport dt/01-10-2026`.
2. Verify the reported SGD amount is $10. Change the configured rate to `0.02`.
3. The existing expense remains $10, including after restart. A new identical purchase
   uses the new rate and contributes $20. `edit 1 a/2000` uses the original saved rate.

### Statistics and sorting

1. Add differently priced expenses in September and October 2026.
2. Compare `stats m/09-2026`, `stats m/10-2026` and
   `stats compare m/09-2026 m/10-2026`; changes must be second minus first.
3. Reverse the months and confirm change signs reverse; an empty month yields zeros.
4. Run `list sort/amount order/desc`; verify descending personal SGD amounts and
   original global indexes. Reports must not change the stored collection.
