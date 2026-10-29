# Tran Duc Thien - Project Portfolio Page

[GitHub profile](https://github.com/ducthien-k24) | [About us](../AboutUs.md)

## Overview

TripJio is a command-line budget tracker and travel app for students. It records
personal, shared and foreign-currency expenses, checks monthly and trip budgets,
and reports spending by month, trip and category. All data is stored locally in a
plain text file.

I set up the base architecture that the rest of the team builds on, implemented
expense tracking and CSV export, and was responsible for the overall structure of
the User Guide and the architecture sections of the Developer Guide.

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627s1.github.io/tp-dashboard/?search=ducthien-k24&breakdown=true)

### Enhancements implemented

- **Base structure and expense tracking**
  ([PR #7](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/7))
  - Designed the `Ui → Parser → Command → Model` architecture with `Storage`, so that
    each feature only adds its own parser and `Command` subclasses. The main loop
    asks each command whether its month needs a budget check (`getAffectedMonth()`),
    whether data must be saved (`isMutating()`) and whether to exit, so commands never
    deal with budgets or storage themselves.
  - Wrote the shared `ArgumentParser` (prefixes in any order; repeated, empty and
    unexpected prefixes rejected the same way in every command), the sectioned text
    `Storage` that skips corrupt lines with a warning instead of losing the whole
    file, and the `MoneyUtil`/`DateUtil` helpers used across all features.
  - Implemented `add`, `list`, `edit` and `delete`. `list` keeps each expense's global
    index under filters and sorting, so the number shown can be used directly with
    `edit` and `delete`.
- **Export to CSV** ([PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60))
  - `export [c/CATEGORY] [m/MONTH] [trip/]` writes the expenses that `list` would show
    to `data/export.csv` for Excel, Numbers or Google Sheets, reusing the shared
    `ExpenseFilter`.
  - Each expense type builds its own row through an overridden `toCsvFields`, like
    `toStorageString`, so the exporter needs no `instanceof` checks. Column positions
    are named constants, so a subclass cannot write into the wrong column.
  - Fields are escaped by the RFC 4180 rule, dates use ISO format so every spreadsheet
    region reads them correctly, and the file is UTF-8 with a byte-order mark so
    descriptions such as `Phở` display correctly in Excel.
- **Usability improvements**
  ([PR #14](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/14),
  [PR #55](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/55))
  - Added a separator line after every command, including failed ones, so successive
    responses are easy to tell apart.
  - Reworked `stats compare` into an aligned table that stays aligned even for the
    largest possible totals.
  - Made `settle` amounts use the same money format as the rest of the app.
- **Assertions and logging**
  ([PR #56](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/56))
  - Enabled assertions for `gradle run` and logged key events (data loaded, each
    command executed, failed commands, skipped corrupt lines, failed saves) to
    `data/tripjio.log`, so logs never mix with the command output.
- **Tests** ([PR #16](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/16),
  [PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60))
  - Added tests for the main loop (saving, budget warnings, separator, exit), storage
    round trips and invalid lines, `Expense`, `ExpenseList`, `MoneyUtil` (including
    non-English locales) and `DateUtil`.
  - Added tests for every part of export: CSV escaping, rows of each expense type,
    filters with global indexes, and parsing of valid and invalid arguments.

### Contributions to the User Guide

- Restructured the whole guide for v2.0
  ([PR #18](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/18)): table of contents,
  command format notes with a parameter table, features grouped by area, FAQ, Known
  Issues and a complete command summary. Kept teammates' authorship of moved sections
  with `@@author` tags ([PR #25](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/25)).
- Wrote the sections for `help`, `add`, `list`, `edit`, `delete`, `stats compare`,
  `export`, `exit`, saving and editing the data file.

### Contributions to the Developer Guide

- Wrote the Architecture, Main loop, Model component and Expense management sections
  ([PR #19](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/19)), and the Export to
  CSV section, first as a proposed design and later updated to the implementation
  ([PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60)).
- Drew the diagrams for these sections: architecture diagram, main loop sequence
  diagram for `add`, model class diagram, `edit` sequence diagram, and the export class
  and sequence diagrams.

### Contributions to team-based tasks

- Set up the issue tracker: milestones `v1.0`, `v2.0` and `v2.0b`, `type.*` and
  `priority.*` labels, and issues for every member's completed and planned work.
- Before merging, checked each pull request for conflicts with the latest `master` and
  ran the full test suite and the app on the merged result. When a conflict appeared,
  wrote the exact steps for the author to resolve it.

### Review and mentoring contributions

- Reviewed teammates' pull requests by running the merged code, not only reading the
  diff. Found issues such as:
  - split expenses not being saved, and `owe`/`settle` not reachable from the parser
    ([PR #15](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/15));
  - `split` no longer triggering the monthly budget warning after a refactor (PR #15);
  - a trip budget with three decimal places being loaded and silently rounded
    ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20));
  - a CLI regression fixture that would fail after another PR changed the `stats`
    output ([PR #59](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/59)).
- Other reviews: [PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9),
  [PR #10](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/10),
  [PR #23](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/23),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57).
