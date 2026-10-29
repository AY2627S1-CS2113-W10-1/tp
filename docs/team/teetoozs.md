# Titus Chia - Project Portfolio Page

[GitHub profile](https://github.com/teetoozs) | [About us](../AboutUs.md)

## Overview

TripJio is a command-line budget tracker and travel app for students. It records
personal, shared and foreign-currency expenses, checks budgets, and reports spending
by month, trip and category, with data stored locally.

I implemented trip management, monthly spending statistics, expense sorting and
manual currency conversion for personal expenses. I also contributed tests, user
instructions and implementation documentation, including UML diagrams.

## Summary of Contributions

### Code contributed

[RepoSense dashboard](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=teetoozs&breakdown=true)

### Enhancements implemented

- **Trip management and storage**
  ([PR #8](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/8),
  [PR #11](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/11),
  [PR #13](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/13))
  - Implemented trip dates/status, multiple named trips, selection, editing and
    deletion, so students can manage several travel periods in one application.
  - Refactored `TripBook` to remove its legacy fallback and represent no selection
    explicitly. Deleting the selected trip clears selection; deleting an earlier
    trip preserves the selected trip by adjusting its index.
  - Migrated old trip data while saving only the named-trip section, preventing
    deleted trips from reappearing. Validated combined name/date edits before mutation.
- **Selected-trip reporting**
  ([PR #13](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/13))
  - Added `trip/` filtering to `list`, `stats` and `split list`, reusing shared
    date-range filtering instead of duplicating expenses for each trip.
  - Included both trip endpoints, intersected trip/month/category filters, and
    preserved global expense indexes so filtered results still work with edit/delete.
- **Monthly spending statistics**
  ([PR #8](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/8),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57))
  - Implemented monthly category totals and two-month comparisons with signed
    changes. Reports use SGD spending and count only the user's share of split bills.
  - Added a shared `SpendingTable` formatter for monthly and trip statistics with
    separated totals, matching the comparison-table style later introduced by Thien.
- **Sorting and manual exchange rates**
  ([PR #11](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/11))
  - Added amount/date sorting without changing stored expense order or indexes.
  - Implemented configurable rates and personal foreign-currency expenses that retain
    their saved rate, so later rate changes do not alter historical spending.
  - Applied the SGD 1,000,000 limit after conversion, allowing larger original-currency
    amounts when their SGD equivalent is valid.
- **Tests**
  ([PR #13](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/13),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57))
  - Added regression tests for trip boundaries, invalid edits, selection changes,
    legacy migration, restart persistence and combined filters.
  - Tested statistics calculations, read-only behaviour and table formatting,
    including totals and selected-trip reports.

### Contributions to the User Guide

- Documented trip commands, selection/deletion behaviour, filtering, statistics,
  sorting and manual rates, with syntax, examples and validation rules
  ([PR #11](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/11),
  [PR #13](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/13)).
- Updated statistics examples to match the aligned output
  ([PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57)).

### Contributions to the Developer Guide

- Explained trip storage migration, selection, reporting and design alternatives;
  documented monthly statistics and manual conversion.
- Added trip class, edit/filter sequence and deletion-state object diagrams;
  contributed budget-warning and split sequence diagrams, and refined the team's
  implementation explanations
  ([PR #17](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/17)).
- Addressed professor feedback on reference frames, class notation, label directions
  and conciseness
  ([issue #26](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/26),
  [PR #57](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/57)).

### Contributions to team-based tasks

- Repaired team profile/portfolio links and added missing portfolio starter pages
  ([commit](https://github.com/teetoozs/tp/commit/b325855)).
- Resolved the export-diagram integration conflict, preserving the implemented API
  and UML corrections; verified the merged result with tests and Checkstyle
  ([merge commit](https://github.com/teetoozs/tp/commit/63e4df2)).

