# Jeanette Ong - Project Portfolio Page

[GitHub profile](https://github.com/jeanetteong) | [About us](../AboutUs.md)

## Overview

TripJio is a command-line budget tracker and travel financial app designed for exchange students to manage their living and travel expenses. My work focuses on **monthly budget management**, **spending forecasting**, **trip-specific budgets**, **automated overspending warnings**, and **trip financial summaries**.

---

## Summary of Contributions

### Major Enhancements Implemented

* **Monthly Budgets and Status Tracking (v1.0)**:
  * Implemented `budget set a/AMOUNT [m/MONTH]` allowing users to configure a default monthly budget or specific monthly overrides.
  * Implemented `budget status [m/MONTH]` which reports total spent vs. budget, remaining funds or excess, days remaining in the month, and dynamic daily spending allowance (`remaining / daysLeft`).
  * Designed domain model `Budget` with `TreeMap<YearMonth, Double>` overrides to guarantee deterministic, sorted storage lines in `data/tripjio.txt`.

* **Centralized Automated Overspending Warnings (v1.0 & v2.0)**:
  * Designed and implemented `BudgetChecker.check(...)` to decouple warning policies from commands. Automatically triggers after expense additions, edits, or splits via the `getAffectedMonth()` hook.
  * Evaluates spending against cent-rounded 80% near-budget threshold and 100% strictly-exceeded threshold.
  * Extended `BudgetChecker` in v2.0 with `checkTrip(...)` to detect trip-level overspending on the active trip whenever an expense falls within the trip dates.

* **Spending Forecasting (v2.0)**:
  * Implemented `forecast [m/MONTH]` using linear progression extrapolation `(spent / daysPassed) * totalDays` to project end-of-month spending against budgets.
  * Designed explicit logic distinguishing closed historical months (reporting actual spending), current active months (projected pacing and warnings), and future months.
  * Designed with dependency injection (`LocalDate today`) for deterministic date-boundary testing.

* **Trip Budget & Backward-Compatible Storage (v2.0)**:
  * Implemented `trip budget a/AMOUNT` to assign spending limits to the currently selected trip.
  * Extended `Trip` and `TripBook` storage with an optional 5th column: `T|name|start|end|budget`. Preserved full backward compatibility with legacy 4-column data files.
  * Implemented validation for saved budgets, skipping malformed or >2 decimal lines with helpful warnings.

* **Trip Financial Summary Breakdown (v2.0)**:
  * Implemented `trip summary` displaying a structured 3-column ASCII category breakdown table (`Category`, `Spent`, `% of Total`), planned budget vs. spent, remaining trip days, and daily allowance.

---

### Code Contributed
* [RepoSense Code Analysis](https://nus-cs2113-ay2627s1.github.io/tp-dashboard/?search=jeanetteong) *(to be updated upon final repo dashboard generation)*
* Pull Requests:
  * [PR #21](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/21): Developer Guide documentation for v1 and v2 budget architecture and UML diagrams.
  * [PR #47](https://github.com/AY2627S1-CS2113-W10-1/tp/pull/47): Implementation of `forecast`, `trip budget`, trip budget warnings, `trip summary`, unit/integration tests, and User Guide updates (Fixes [#44](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/44), Fixes [#45](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/45), Fixes [#46](https://github.com/AY2627S1-CS2113-W10-1/tp/issues/46)).

---

### Contributions to the User Guide (UG)
* Authored documentation for:
  * Monthly budget configuration (`budget set`) and status tracking (`budget status`).
  * End-of-month spending predictions (`forecast`).
  * Trip budgets (`trip budget`) and comprehensive financial breakdown tables (`trip summary`).
* Maintained parameter formatting tables, notes on command prefixes, and Table of Contents consistency across merges.

---

### Contributions to the Developer Guide (DG)
* Authored the **"Monthly budgets, warnings and forecasting"** section: detailed parsing, domain model persistence, command execution, automated warnings, and extrapolation design trade-offs.
* Authored the **"Trip budgets, warnings and summary"** section: detailed trip budget storage format, hook-based overspending checks, and ASCII summary table generation.
* Authored 5 editable PlantUML diagrams and generated SVGs:
  * `BudgetClassDiagram.puml`: Class diagram of budget commands, models, and utility checkers.
  * `SetBudgetSequence.puml`: Sequence diagram for budget configuration and storage persistence.
  * `BudgetStatusSequence.puml`: Sequence diagram for querying monthly status and daily allowance.
  * `ForecastSequence.puml`: Sequence diagram for linear spending projection.
  * `TripSummarySequence.puml`: Sequence diagram for aggregating and rendering trip breakdown tables.
* Authored step-by-step test scenarios for budgets, forecasting, and trip budgets in **Instructions for manual testing**.
