# Jeanette Ong - Project Portfolio Page

[GitHub profile](https://github.com/jeanetteong) | [About us](../AboutUs.md)

## Overview

TripJio is a command-line budget tracker and travel financial app designed for exchange students to manage their living and travel expenses. My work focuses on **monthly budget management**, **spending forecasting**, **trip-specific budgets**, **automated overspending warnings**, and **trip financial summaries**.

---

## Summary of Contributions

### Major Enhancements Implemented

* **Monthly Budgets and Status Tracking (v1.0)** ([PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9)):
  * Implemented `budget set a/AMOUNT [m/MONTH]` allowing users to configure a default monthly budget or specific monthly overrides.
  * Implemented `budget status [m/MONTH]` which reports total spent vs. budget, remaining funds or excess, days remaining in the month, and dynamic daily spending allowance (`remaining / daysLeft`).
  * Designed domain model `Budget` with `TreeMap<YearMonth, Double>` overrides to guarantee deterministic, sorted storage lines in `data/tripjio.txt`.
  * Authored comprehensive unit and integration tests across parsing, domain models, and commands (`BudgetTest`, `BudgetCheckerTest`, `SetBudgetCommandTest`, `BudgetStatusCommandTest`, `BudgetCommandParserTest`).

* **Centralized Automated Overspending Warnings (v1.0 & v2.0)** ([PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9), [PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)):
  * Designed and implemented `BudgetChecker.check(...)` to decouple warning policies from commands. Automatically triggers after expense additions, edits, or splits via the `getAffectedMonth()` hook.
  * Evaluates spending against cent-rounded 80% near-budget threshold and 100% strictly-exceeded threshold.
  * Extended `BudgetChecker` in v2.0 with `checkTrip(...)` to detect trip-level overspending on the active trip whenever an expense falls within the trip dates.

* **Spending Forecasting (v2.0)** ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)):
  * Implemented `forecast [m/MONTH]` using linear progression extrapolation `(spent / daysPassed) * totalDays` to project end-of-month spending against budgets.
  * Designed explicit logic distinguishing closed historical months (reporting actual spending), current active months (projected pacing and warnings), and future months.
  * Designed with dependency injection (`LocalDate today`) for deterministic date-boundary testing.

* **Trip Budget & Backward-Compatible Storage (v2.0)** ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)):
  * Implemented `trip budget a/AMOUNT` to assign spending limits to the currently selected trip.
  * Extended `Trip` and `TripBook` storage with an optional 5th column: `T|name|start|end|budget`. Preserved full backward compatibility with legacy 4-column data files.
  * Implemented validation for saved budgets, skipping malformed or >2 decimal lines with helpful warnings.

* **Trip Financial Summary Breakdown (v2.0)** ([PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)):
  * Implemented `trip summary` displaying a structured 3-column ASCII category breakdown table (`Category`, `Spent`, `% of Total`), planned budget vs. spent, remaining trip days, and daily allowance.

---

### Code Contributed

* [RepoSense link](https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=jeanetteong&breakdown=true)
* **Key Pull Requests:**
  * **[PR #9](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/9)**: v1.0 Monthly budget management (`budget set`, `budget status`), automated 80% and 100% overspending warnings (`BudgetChecker`), domain model & storage persistence, comprehensive JUnit tests, and User Guide documentation (1,206 additions across 13 files).
  * **[PR #20](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/20)**: v2.0 Spending forecasting (`forecast`), trip budget management (`trip budget`), trip overspending warnings (`checkTrip`), trip financial summary breakdown table (`trip summary`), `Command.getAffectedDate()` hook, unit/integration tests, and User Guide updates (1,191 additions across 24 files; Fixes [#44](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/44), Fixes [#45](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/45), Fixes [#46](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/46)).
  * **[PR #21](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/21)**: Developer Guide documentation for v1.0 and v2.0 budget architecture, design considerations, alternative trade-offs, and 5 PlantUML diagrams.
  * **[PR #5](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/5)**: Team About Us profile and portfolio starter page.
  * **[PR #61](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/61)**: Updated Project Portfolio Page content and structure.

---

### Contributions to the User Guide (UG)

* Authored complete documentation for:
  * Monthly budget configuration (`budget set`) and status tracking (`budget status`).
  * End-of-month spending predictions and pacing comparisons (`forecast`).
  * Trip budgets (`trip budget`) and comprehensive financial breakdown tables (`trip summary`).
* Maintained parameter formatting tables, notes on command prefixes, and Table of Contents consistency across merges.

---

### Contributions to the Developer Guide (DG)

* Authored the **"Monthly budgets, warnings and forecasting"** section: detailed parsing, domain model persistence, command execution, automated warnings, and extrapolation design trade-offs ([PR #21](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/21)).
* Authored the **"Trip budgets, warnings and summary"** section: detailed trip budget storage format, hook-based overspending checks, and ASCII summary table generation ([PR #21](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/21)).
* Authored 5 editable PlantUML diagrams and generated SVGs:
  * `BudgetClassDiagram.puml`: Class diagram of budget commands, models, and utility checkers.
  * `SetBudgetSequence.puml`: Sequence diagram for budget configuration and storage persistence.
  * `BudgetStatusSequence.puml`: Sequence diagram for querying monthly status and daily allowance.
  * `ForecastSequence.puml`: Sequence diagram for linear spending projection.
  * `TripSummarySequence.puml`: Sequence diagram for aggregating and rendering trip breakdown tables.
* Authored step-by-step test scenarios for budgets, forecasting, and trip budgets in **Instructions for manual testing**.

---

### Contributions to Team-Based Tasks

* **Documentation Lead (Member B)**: Ensured stylistic uniformity, consistent parameter tables, and cross-referencing between the User Guide and Developer Guide.
* **Architectural Hooks**: Designed and integrated the `Command.getAffectedDate()` hook into `TripJio`, allowing mutating commands (`add`, `edit`, `split`) to communicate affected dates polymorphically to trip budget checkers without coupling commands to trip models.
* **Reviewing and Integration**: Actively reviewed teammates' pull requests, verified build integrity under Gradle and Checkstyle, and resolved complex multi-file merge conflicts across documentation and codebases.
