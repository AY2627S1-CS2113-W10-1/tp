# Developer Guide

## Selected-trip filtering

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

TripFilterCommandTest covers cross-year boundaries, combined filters/sorting,
missing selection, invalid flags, SGD conversion, split shares and unchanged data.
Shared changes to ExpenseList, ListCommand, ArgumentParser and HelpCommand need
Thien's review; SplitListCommand and SplitCommandParser changes need John's review.

## Trip edit and delete

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

The existing named-trip storage persists edits and the absence of selection.
`TripEditDeleteCommandTest` verifies validation, selection shifts, retained
expenses, renamed selection after reload, and no resurrection after deleting all
trips. HelpCommand has only the two new command lines; its owner should review
that shared-file change.

## Sorting, currencies and multiple trips

`ListCommand` sorts a temporary list of original expense indexes using a comparator.
Amount comparisons call the polymorphic `getAmount()`; date comparisons use purchase
dates. Stable sorting preserves ties and the source collection is never reordered.
`ArgumentParser` recognises the new prefixes and command-specific allowed-prefix
checks prevent options such as `cur/` being silently ignored by unrelated commands.

`ExchangeRates` stores manually supplied SGD-per-unit decimal rates.
`ForeignExpense extends Expense` snapshots its original amount, currency and rate,
and exposes the converted SGD amount to existing consumers.
The original foreign amount has no SGD cap; positivity, finiteness and cent
precision are validated separately. Add/edit parsing permits large original
amounts and the expense model applies the SGD cap after conversion. SGD expenses
and split bills retain their existing model-level amount limits.
Conversion uses `BigDecimal` with half-up cent rounding. Amount edits validate the conversion before
mutating either amount; they use the saved rate. Split expenses remain SGD-only.
The reporting currency is fixed, so existing budgets and historical expenses are
never silently reinterpreted after a base-currency change.

`TripBook` owns named trips and their selection. `AppState.getTrip()` delegates to
the selected trip, preserving existing trip commands and date-range calculations.
There is no legacy field or fallback. The selected index is -1 when none is selected.
`hasCurrent()` guards nullable `getCurrent()` and `getCurrentName()`; `AppState.getTrip()`
is also nullable. Commands validate selection before accessing trip dates.
`setCurrentDates()` creates Original trip only for an empty book, otherwise updates
the selected trip or throws if none is selected. `Trip.contains(date)` checks both
inclusive boundaries and returns false for null dates or unset trips.

Storage first checks whether a `[trips]` header exists anywhere in the file.
If present, all `[trip]` data is ignored, including when named records are empty or
invalid. Otherwise, a complete old trip is imported once as selected Original trip.
Selections are resolved after reading trip records so section/record order does not
affect valid references. No selection record means no selection; an invalid reference
warns and clears selection. Saving always emits `[trips]`, even for an empty book,
and never emits `[trip]`. TripMigrationTest covers all three file generations.

Names are unique, and selected-trip persistence uses a name rather than an index
so skipped invalid trip records do not select an unintended trip.

`Storage` supports foreign expense records, named trips and rates in addition to
legacy formats. Malformed new records use the existing warning/skip behaviour.
There is no live rate dependency or exclusive trip-expense assignment.

Manual checks: sort filtered expenses then edit their displayed original indexes;
change a configured rate and confirm an existing foreign expense is unchanged;
restart after selecting a trip and confirm the selection, names and rate snapshots
survive. `TravelFeaturesTest` covers these paths and old-file migration.

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

### Monthly statistics

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
shares without changes to split, budget or storage code.

### Trip status

`TripCommandParser` accepts `status` without arguments and creates a `TripStatusCommand`.
The command extends `Command` and inherits its non-mutating behaviour, so the main loop
does not save data or check budget warnings. It obtains dates and lifecycle calculations
from `Trip`, spending from `ExpenseList.getTotalSpentBetween`, and prints through `Ui`.
`Trip` rejects calculations on an unset trip with `TripJioException` before any output.

Inclusive day counts use `ChronoUnit.DAYS`. `TripStatusCommand` receives a `Clock` through
an overloaded constructor; production uses the local system clock and tests use a fixed
clock. This keeps boundary tests reproducible without hard-coding today's date. Expenses
are summed via `getAmount()`, preserving polymorphic support for the user's split-bill share.

### Trip date configuration

`Parser` appoints `trip` input to `TripCommandParser`, which checks the `set`
subcommand and required `s/` and `e/` parameters using `ArgumentParser`. 
`DateUtil` handles strict calendar parsing into the required format. 
The resulting `TripSetCommand` extends `Command`
and updates the existing `Trip` held by `AppState`.

`Trip` keep tracks of its dates and validates both before mutation, throwing the
existing checked `TripJioException` for missing dates or an invalid range. This
protects the model even when called without the CLI parser. The command overrides
`execute` and `isMutating`, allowing the main loop to execute and save it through
the common `Command` abstraction. No separate exception hierarchy is needed.

`Storage` reads legacy `[trip]` dates only when no `[trips]` header exists.
An incomplete pair creates no trip; a reversed pair is cleared with a warning.
Current saves use `T|NAME|START|END` records and an optional `A|NAME` selection
inside `[trips]`, as described in the migration section above.
Monthly statistics, comparisons and trip status are described above.

{Describe the design and implementation of the product. Use UML diagrams and short code snippets where applicable.}


## Product scope
### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* *glossary item* - Definition

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
