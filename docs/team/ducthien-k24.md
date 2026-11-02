# Tran Duc Thien - Project Portfolio Page

[GitHub profile](https://github.com/ducthien-k24) | [About us](../AboutUs.md)

## Overview

TripJio is a command-line budget tracker and travel app for students. It records
personal, shared and foreign-currency expenses, checks monthly and trip budgets,
and reports spending by month, trip and category. All data is stored locally in a
plain text file.

I set up the base architecture, implemented expense tracking and CSV export, structured
the User Guide, and acted as the team's integrator and release manager.

## Summary of Contributions

### Code contributed

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=ducthien-k24&breakdown=true)

### Enhancements implemented

- **Base architecture and expense tracking**
  ([PR #7](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/7): 26 classes, +2,477 lines)
  - Designed the `Ui → Parser → Command → Model` architecture with `Storage`. Commands
    report through hooks whether they change data (`isMutating()`), which month needs
    a budget check (`getAffectedMonth()`) and whether to exit, so the main loop handles
    saving and warnings and no command touches storage directly.
  - Wrote skeletons of `Budget`, `Trip`, `SplitExpense` and `Statistics` with storage
    formats, so teammates could build their features in parallel from the first week.
  - Wrote the shared `ArgumentParser` (prefixes in any order; repeated, empty and unknown
    prefixes rejected the same way in every command), a sectioned `Storage` that skips
    each corrupt line with a warning instead of losing the whole file, and the
    locale-independent `MoneyUtil`/`DateUtil` helpers used by every feature.
  - Implemented `add`, `list`, `edit` and `delete`. `list` keeps each expense's global
    index under filters and sorting, so the number shown works directly with `edit`.
- **Export to CSV** ([PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60): +863 lines)
  - `export [c/CATEGORY] [m/MONTH] [trip/]` writes the expenses that `list` would show
    to a CSV file for Excel, Numbers or Google Sheets, for all three expense types.
  - Each expense type builds its own row through an overridden `toCsvFields`, so the
    exporter needs no `instanceof` checks; column positions are named constants.
  - Fields are escaped by RFC 4180, dates use ISO format so every spreadsheet region
    reads them, and the file is UTF-8 with a byte-order mark so `Phở` displays in Excel.
- **Tests** ([PR #16](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/16)): wrote 119 of
  the team's 312 JUnit tests, covering the main loop, storage, parsing, locales and export.
- **Usability, assertions and logging**
  ([PR #14](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/14),
  [PR #56](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/56),
  [PR #70](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/70))
  - Added a command separator, an aligned `stats compare` table, consistent messages,
    assertions, and logging of key events to `data/tripjio.log`.

### Contributions to the User Guide

- Restructured the guide ([PR #18](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/18)):
  table of contents, format notes, parameter table, FAQ, Known Issues and command summary.
  Wrote `help`, `add`, `list`, `edit`, `delete`, `stats compare`, `export` and data file sections.

### Contributions to the Developer Guide

- Wrote the Architecture, Main loop, Model component, Expense management and Export to
  CSV sections ([PR #19](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/19),
  [PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60)) with 6 PlantUML diagrams:
  architecture, `add` main loop, model class, `edit` sequence, export class and sequence.

### Contributions to team-based tasks

- **Integration:** merged 27 of the team's 35 PRs, each after testing it merged with the
  latest `master`; wrote resolution steps for authors whose PRs conflicted.
- **Issue tracking:** set up milestones and labels, and created 32 issues for all members.
- **Releases:** managed `v2.0` and `v2.1`. Tested each release JAR by hand across every
  command, invalid input and corrupt data files; this found 4 bugs (#64 to #67), which I
  filed and assigned to their owners before the final release.

### Review and mentoring contributions

- Reviewed PRs by running the merged code, not only reading the diff. Found split
  expenses not being saved and `owe`/`settle` unreachable
  ([PR #15](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/15)), a trip budget silently
  rounded on load ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)) and a
  test fixture broken by another PR ([PR #59](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/59)).
  Other reviews: [PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9),
  [PR #10](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/10),
  [PR #23](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/23),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57).
