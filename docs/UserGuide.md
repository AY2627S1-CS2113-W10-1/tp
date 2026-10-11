# User Guide

## Introduction

{Give a product intro}

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 25 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features 

### Viewing the current trip: `trip status`

Format: `trip status` (no parameters).

Shows the configured dates, status (Upcoming, Ongoing or Completed), inclusive trip
duration, remaining trip days, and recorded spending between the start and end dates.
Before departure, remaining days equals the full duration. During the trip it includes
today; after the end date it is zero. A same-day trip lasts one day. The computer's
local date determines the status.

Spending includes expenses on both boundary dates and counts only your share of split
bills. It totals all recorded expenses within the trip dates, including future-dated
records loaded from a manually edited file. No expenses produces `$0.00`. This command
does not change or save data. If no complete trip is set, it asks you to use `trip set`.

Example output for `trip status` on 11 December 2026:

```text
Trip: 10-12-2026 to 12-12-2026
Status: Ongoing
Duration: 3 day(s)
Remaining trip days (including today): 2
Recorded spending during trip: $60.30
```

### Setting trip dates: `trip set`

Format: `trip set s/START_DATE e/END_DATE`

Example: `trip set s/10-12-2026 e/24-12-2026`

Dates must be valid calendar dates in `DD-MM-YYYY` format. Both parameters are required
exactly once and can appear in either order. The end date must be on or after the start
date; same-day trips and past or future trips are also allowed. 

A successful command replaces the current trip dates and automatically saves them to
`data/tripjio.txt`. They are restored on the next launch. Invalid commands leave the
existing trip unchanged. If saving fails, an error is displayed; the change remains in
memory but may not survive restarting until a later save succeeds.

{Give detailed description of each feature}

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.  

Example of usage: 

`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## Command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
