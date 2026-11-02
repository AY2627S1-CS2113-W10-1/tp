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

[RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=ducthien-k24&breakdown=true)

### Enhancements implemented

- **Base structure and expense tracking**
  ([PR #7](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/7),
  [PR #16](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/16))
  - Designed the `Ui → Parser → Command → Model` architecture with `Storage`. Each
    feature only adds its own parser and `Command` subclasses; the main loop handles
    budget checks, saving and exit, so commands never deal with storage themselves.
  - Wrote the shared `ArgumentParser` (prefixes in any order, invalid prefixes rejected
    the same way in every command), a sectioned `Storage` that skips corrupt lines
    instead of losing the whole file, and the `MoneyUtil`/`DateUtil` helpers.
  - Implemented `add`, `list`, `edit` and `delete`. `list` keeps each expense's global
    index under filters and sorting, so it can be used directly with `edit`/`delete`.
  - Tested the main loop, storage, expenses and helpers, including non-English locales.
- **Export to CSV** ([PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60))
  - `export [c/CATEGORY] [m/MONTH] [trip/]` writes the expenses that `list` would show
    to a CSV file for Excel, Numbers or Google Sheets.
  - Each expense type builds its own row through an overridden `toCsvFields`, so the
    exporter needs no `instanceof` checks; column positions are named constants.
  - Fields are escaped by RFC 4180, dates use ISO format, and the file is UTF-8 with a
    byte-order mark so descriptions such as `Phở` display correctly in Excel.
- **Usability, assertions and logging**
  ([PR #14](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/14),
  [PR #55](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/55),
  [PR #56](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/56),
  [PR #70](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/70))
  - Added a separator after every command, an aligned `stats compare` table and
    consistent money formats and error messages.
  - Enabled assertions and logged key events to `data/tripjio.log`, separate from the
    command output.

### Contributions to the User Guide

- Restructured the guide ([PR #18](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/18)):
  table of contents, command format notes, features grouped by area, FAQ, Known Issues
  and command summary, keeping teammates' authorship with `@@author` tags.
- Wrote `help`, `add`, `list`, `edit`, `delete`, `stats compare`, `export`, `exit` and
  the data file sections.

### Contributions to the Developer Guide

- Wrote the Architecture, Main loop, Model component, Expense management and Export to
  CSV sections ([PR #19](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/19),
  [PR #60](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/60)), with the architecture,
  main loop, model, `edit` and export diagrams.

### Contributions to team-based tasks

- Set up milestones, labels and issues, and managed the `v2.0` and `v2.1` releases.
- Before each merge, ran the full tests and the app on the PR merged with the latest
  `master`, and wrote resolution steps for the author when a conflict appeared.

### Review and mentoring contributions

- Reviewed PRs by running the merged code. Found split expenses not being saved and
  `owe`/`settle` unreachable ([PR #15](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/15)),
  a trip budget silently rounded on load ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20))
  and a test fixture broken by another PR ([PR #59](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/59)).
- Other reviews: [PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9),
  [PR #10](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/10),
  [PR #23](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/23),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57),
  [PR #68](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/68),
  [PR #69](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/69).
