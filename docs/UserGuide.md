# TripJio - User Guide

## New: sorting, manual exchange rates and multiple trips

All existing amounts and budgets are interpreted as **SGD**. Reports continue to
use the dollar symbol for SGD. This version does not fetch live exchange rates
or support changing the reporting currency.

### Sorting expenses

```text
list sort/amount order/desc
list sort/date order/desc
list c/food m/10-2026 sort/amount order/asc
```

Sort by `amount` (your SGD share) or `date` (purchase date, not entry time).
Descending is the default when a sort is supplied. Without sorting, insertion
order is unchanged. Equal values retain insertion order. `order/` requires
`sort/`. Displayed indexes remain the original indexes for editing/deleting;
sorting does not rearrange or save the underlying records.

### Manual exchange rates

```text
rate set from/EUR to/SGD r/1.45
rate list
add d/Lunch a/12 c/food dt/10-10-2026 cur/EUR
```

The example rate means **1 EUR = 1.45 SGD**, not the reverse. 
Supply a rate yourself by searching online; these are not live quotes. Rates
must be positive plain decimals, at most 1000000, with up to eight decimal places.
Original foreign-currency amounts may exceed 1,000,000. They must be positive,
finite amounts with at most two decimal places. Only the converted SGD amount is
capped at SGD 1,000,000.00 (after rounding to cents); conversions rounding to zero
are rejected. The same rules apply when editing or loading foreign expenses.

The lunch contributes SGD 17.40 to budgets, statistics and trip spending. Its
original EUR amount and rate are displayed and saved. Updating EUR's rate affects
only subsequently added expenses. `edit INDEX a/10` on this expense means EUR 10
using its original saved rate. Currency/rate changes on an existing expense are
not supported; delete and re-add if necessary. Without `cur/`, amounts remain SGD.
`cur/SGD` uses rate 1 automatically. Foreign-currency split bills are not supported;
`split ... cur/EUR` is rejected rather than silently counted as SGD.

### Multiple trips

```text
trip add n/Japan s/01-12-2026 e/14-12-2026
trip add n/Korea s/15-12-2026 e/21-12-2026
trip list
trip select 1
trip status
```

Adding a trip selects it automatically. Names must be non-empty and unique
(case-insensitive), and cannot contain `/`, `|` or line breaks.
`trip list` shows indexes and a selected marker. `trip select INDEX` saves the
selection. `trip set` updates only the selected trip's dates; it does not erase
other trips. With no trips, `trip set` creates and selects `Original trip`.
If trips exist but none is selected, `trip set` and `trip status` report:
`No trip selected` and explain how to select one. Use `trip list`, then
`trip select INDEX`; the application never chooses a trip silently.
### Editing and deleting trips

```text
trip edit 1 n/Tokyo
trip edit 1 s/01-12-2026 e/15-12-2026
trip delete 1
```

`trip edit INDEX [n/NAME] [s/START_DATE] [e/END_DATE]` requires at least one field.
Omitted fields stay unchanged. Dates use DD-MM-YYYY; the final combined date range
must be valid, including when only one endpoint changes. Names follow trip add's
rules and must be unique ignoring case, except that a trip may keep its own name
or change only its capitalisation. Invalid edits leave all fields unchanged.
Editing does not select the trip. Success prints `Updated trip 1.` for index 1.

`trip delete INDEX` accepts no other parameters and deletes immediately.
Deleting the selected trip leaves **no trip selected**, even if others remain.
Deleting an earlier trip shifts the selected index down while preserving the
same selection. Remaining trips are renumbered; use `trip list` before the next
edit/delete. If Japan was selected, deleting it prints:

```text
Deleted trip: Japan. Expenses are unchanged.
No trip selected. Use trip list and trip select INDEX, or add a new trip.
```

If another trip remains selected, the second line is `Selected trip: NAME.`
Both commands save automatically. Neither deletes or edits expenses or monthly
budgets. Editing trip dates changes the date range used for trip spending.
After deleting every trip, restart still shows no trips; `trip set` can create a
new Original trip. There is no undo command, so back up data before deleting.

Trip spending still includes all expenses within the selected trip's dates,
including both boundaries. Expenses are **not assigned exclusively to trips**:
overlapping trips can count the same expense. Selecting a trip does not filter
`list`, `stats`, or budgets, which remain global/month-based.

### Saved-data compatibility

Existing `E|` and `S|` expenses and the legacy `[trip]` section still load.
An old file containing only `[trip]` migrates its complete dates to a selected
`Original trip`. If `[trips]` is present, it is authoritative and the old
`[trip]` copy is ignored, even when `[trips]` is empty or invalid.
New saves write only `[trips]`, never `[trip]`.
Each `T|NAME|START|END` line stores one trip. An optional `A|NAME` line selects
one of them; no `A|` line means no trip selected. Invalid selection names produce
a warning and leave no selection. Empty books remain empty after restarting.
Foreign expenses use `F|description|originalAmount|category|date|currency|rate`.
Named trips and selection are stored under `[trips]`; manual rates under `[rates]`.
Keep a backup before using this format with an older application release: older
releases do not understand the new records.

## Introduction

**TripJio** is a desktop Command-Line Interface (CLI) budget tracker and travel management application designed for students. It combines the speed of keyboard typing with straightforward expense tracking, flexible monthly budgets, trip date monitoring, and detailed spending statistics to help you keep your student travel finances under control.

---

## Quick Start

1. Ensure you have **Java 25** or above installed on your computer.
2. Download the latest `tripjio.jar` or clone this repository.
3. Open a terminal, navigate to the folder containing the application, and start TripJio:
   ```bash
   java -jar tripjio.jar
   ```
   *(Or if running from source with Gradle: `./gradlew run --console=plain -q`)*
4. You will see the TripJio welcome banner:
   ```text
   ████████╗██████╗ ██╗██████╗     ██╗██╗ ██████╗ 
   ╚══██╔══╝██╔══██╗██║██╔══██╗    ██║██║██╔═══██╗
      ██║   ██████╔╝██║██████╔╝    ██║██║██║   ██║
      ██║   ██╔══██╗██║██╔═══╝██   ██║██║██║   ██║
      ██║   ██║  ██║██║██║    ╚█████╔╝██║╚██████╔╝
      ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚════╝ ╚═╝ ╚═════╝ 

   Hello! Welcome to TripJio, your travel budget buddy.
   What would you like to do?
   ```
5. Type any command (e.g. `help`) and press **Enter** to run it.

---

## Command Syntax & Conventions

* **Command Word**: The primary action (e.g. `add`, `list`, `budget`, `trip`, `stats`, `exit`). Command words are case-insensitive.
* **Prefixes**: Parameters are supplied with a prefix ending in a forward slash `/`:
  * `d/` Description (e.g. `d/Lunch at cafe`)
  * `a/` Amount of money (e.g. `a/12.50`)
  * `c/` Category (e.g. `c/food`)
  * `dt/` Date (e.g. `dt/15-10-2026`)
  * `m/` Month (e.g. `m/10-2026`)
  * `s/` Trip start date (e.g. `s/10-12-2026`)
  * `e/` Trip end date (e.g. `e/24-12-2026`)
* **Prefix Order**: Prefixes can appear in any order (e.g. `a/12.50 d/Lunch` is identical to `d/Lunch a/12.50`).
* **Parameters in Brackets**: Items enclosed in square brackets `[ ... ]` are optional.
* **Amount Constraints**: Amounts must be positive numbers with up to 2 decimal places and at most `$1,000,000.00`.
* **Date & Month Format**:
  * Dates use strict **`DD-MM-YYYY`** format (e.g. `15-10-2026`). Future dates are not permitted for recorded expenses.
  * Months use **`MM-YYYY`** format (e.g. `10-2026`).

---

## Features

### 1. General Commands

#### Viewing Help: `help`
Shows a list of all available commands and their syntax formats.

* **Format**: `help`
* **Example**: `help`
* **Output**:
  ```text
  Here are the commands you can use:
  help
  add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]
  list [c/CATEGORY] [m/MONTH]
  edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]
  delete INDEX
  split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE]
  split list [m/MONTH]
  budget set a/AMOUNT [m/MONTH]
  budget status [m/MONTH]
  trip set s/START_DATE e/END_DATE
  trip status
  stats [m/MONTH]
  stats compare m/MONTH m/MONTH
  exit
  ```

#### Exiting the Application: `exit`
Saves all data to the local data file and exits TripJio.

* **Format**: `exit`
* **Output**:
  ```text
  Bye! Your data has been saved. Safe travels!
  ```

---

### 2. Expense Tracking

#### Adding an Expense: `add`
Records a personal expense paid by you.

* **Format**: `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]`
* **Parameters**:
  * `d/DESCRIPTION`: What you spent money on. Must not be empty and cannot contain `/` or `|`.
  * `a/AMOUNT`: Amount spent in dollars and cents (e.g. `12.50`).
  * `[c/CATEGORY]`: Optional category. Allowed values: `food`, `transport`, `accommodation`, `entertainment`, `shopping`, `other`. Defaults to `other` if omitted.
  * `[dt/DATE]`: Optional date in `DD-MM-YYYY` format. Defaults to today's date if omitted. Cannot be a date in the future.
* **Examples**:
  * `add d/Lunch at canteen a/8.50 c/food`
  * `add d/Museum ticket a/15 dt/12-10-2026 c/entertainment`
  * `add d/Notebook a/3.20` *(defaults to category `other` and today's date)*
* **Expected Output**:
  ```text
  Added expense:
    [Food] Lunch at canteen - $8.50 (15-10-2026)
  You now have 1 expenses.
  ```

#### Listing Expenses: `list`
Displays your recorded expenses, optionally filtered by category and/or month. Each item retains its original index so you can immediately use that number to `edit` or `delete`.

* **Format**: `list [c/CATEGORY] [m/MONTH]`
* **Examples**:
  * `list`
  * `list c/food`
  * `list m/10-2026`
  * `list c/transport m/10-2026`
* **Expected Output**:
  ```text
  Here are your expenses:
  1. [Food] Lunch at canteen - $8.50 (15-10-2026)
  2. [Entertainment] Museum ticket - $15.00 (12-10-2026)
  Total: $23.50
  ```
* **Empty States**:
  * If no expenses have been recorded yet:
    ```text
    You have no expenses yet.
    ```
  * If expenses exist but none match your filter:
    ```text
    No expenses match the given filter.
    ```

#### Editing an Expense: `edit`
Updates one or more fields of an existing expense at the specified index.

* **Format**: `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]`
* **Parameters**:
  * `INDEX`: The positive whole number corresponding to the expense in the `list` output.
  * You must provide at least one optional field to update (`d/`, `a/`, `c/`, or `dt/`). Unspecified fields retain their existing values.
* **Examples**:
  * `edit 1 a/9.00`
  * `edit 2 d/Art museum ticket c/entertainment`
  * `edit 1 d/Brunch a/14.00 dt/14-10-2026`
* **Expected Output**:
  ```text
  Edited expense:
    [Food] Brunch - $14.00 (14-10-2026)
  ```

#### Deleting an Expense: `delete`
Removes an expense from your records by its index number.

* **Format**: `delete INDEX`
* **Example**: `delete 1`
* **Expected Output**:
  ```text
  Deleted expense:
    [Food] Brunch - $14.00 (14-10-2026)
  You now have 1 expenses.
  ```

---

### 3. Monthly Budget & Warnings

#### Setting a Budget: `budget set`
Sets either a default monthly budget or a budget specifically for a designated month.

* **Format**: `budget set a/AMOUNT [m/MONTH]`
* **Parameters**:
  * `a/AMOUNT`: The budget limit in dollars and cents.
  * `[m/MONTH]`: Optional month in `MM-YYYY` format (e.g. `12-2026`). If omitted, sets your **default monthly budget** which applies to every month without its own custom budget.
* **Examples**:
  * `budget set a/1000` *(Sets default monthly budget to $1,000.00)*
  * `budget set a/1500 m/12-2026` *(Sets December 2026 budget to $1,500.00, overriding the default)*
* **Expected Output**:
  * Setting default budget:
    ```text
    Default monthly budget set to $1,000.00.
    ```
  * Setting specific month budget:
    ```text
    Budget for December 2026 set to $1,500.00.
    ```

#### Checking Budget Status: `budget status`
Displays your current budget progress, total money spent, remaining allowance, and for the ongoing month, the days left and daily allowance.

* **Format**: `budget status [m/MONTH]`
* **Parameters**:
  * `[m/MONTH]`: Optional month in `MM-YYYY` format. If omitted, checks the **current ongoing month**.
* **Examples**:
  * `budget status`
  * `budget status m/12-2026`
* **Expected Output (Current Month - Within Budget)**:
  ```text
  Budget status for October 2026:
    Budget: $1,000.00
    Spent: $350.00
    Remaining: $650.00
    Days left: 17 days (including today)
    Daily allowance: $38.24/day
  ```
* **Expected Output (Current Month - Over Budget)**:
  ```text
  Budget status for October 2026:
    Budget: $1,000.00
    Spent: $1,050.00
    Over budget by: $50.00
    Days left: 17 days (including today)
    Daily allowance: $0.00/day
  ```
* **Expected Output (When No Budget is Set)**:
  ```text
  No budget has been set for October 2026.
  ```

#### Automatic Budget Warnings
Whenever you add or edit an expense, TripJio automatically evaluates your spending against the month's budget. If your spending reaches or exceeds designated thresholds, a warning message is automatically displayed after the command output:

1. **80% Spending Warning** (Triggered when monthly spending reaches 80% to 100% of the budget):
   ```text
   Warning: You have used 80% or more of your budget for October 2026! Spent: $800.00 / $1,000.00 ($200.00 remaining).
   ```
2. **Budget Exceeded Warning** (Triggered when monthly spending exceeds 100% of the budget):
   ```text
   Warning: You have exceeded your budget for October 2026! Spent: $1,050.00 / $1,000.00 ($50.00 over budget).
   ```

---

### 4. Trip Dates & Management

#### Setting Trip Dates: `trip set`
Updates the selected trip's dates without changing other trips. With an empty trip
list it creates and selects Original trip. If trips exist but none is selected,
use `trip select INDEX` first.

* **Format**: `trip set s/START_DATE e/END_DATE`
* **Parameters**:
  * `s/START_DATE`: The start date of the trip in `DD-MM-YYYY` format.
  * `e/END_DATE`: The end date of the trip in `DD-MM-YYYY` format. Must be on or after the start date.
  * Both parameters are required and can appear in either order. Same-day trips, past trips, and future trips are all allowed.
* **Example**:
  ```
  trip set s/10-12-2026 e/24-12-2026
  ```
* **Notes**:
  * A successful command replaces the current trip dates and automatically saves them to `data/tripjio.txt`.
  * If the command is invalid (e.g. end date before start date), the existing trip remains unchanged.

#### Viewing Trip Status: `trip status`
Displays the configured trip dates, status (Upcoming, Ongoing, or Completed), duration, remaining days, and recorded spending between the start and end dates.

* **Format**: `trip status` (no parameters)
* **Status Details**:
  * **Upcoming**: The current date is before the start date. Remaining days equals the full trip duration.
  * **Ongoing**: The current date is between the start and end dates (inclusive). Remaining days includes today.
  * **Completed**: The current date is after the end date. Remaining days is 0.
* **Spending**:
  * Totals all recorded expenses between the start and end dates (inclusive of both boundary dates).
  * Only counts your own share of split bills.
  * If no expenses occurred during the trip, shows `$0.00`.
  * If no trip has been configured yet, prompts you to set one using `trip set`.
* **Example Output (for an ongoing trip on 11-12-2026)**:
  ```text
  Trip: 10-12-2026 to 12-12-2026
  Status: Ongoing
  Duration: 3 day(s)
  Remaining trip days (including today): 2
  Recorded spending during trip: $60.30
  ```

---

### 5. Spending Statistics

#### Monthly Spending by Category: `stats`
Generates a breakdown of your spending across all six categories for a selected month, followed by total spending.

* **Format**: `stats [m/MM-YYYY]`
* **Parameters**:
  * `[m/MONTH]`: Optional month in `MM-YYYY` format. If omitted, uses the **current month** based on your computer's local date.
* **Behavior**:
  * Lists categories in a fixed order: **Food**, **Transport**, **Accommodation**, **Entertainment**, **Shopping**, and **Other**, followed by the total.
  * Amounts are rounded to two decimal places.
  * Unused categories display `$0.00`.
  * Split bills count only your share.
  * If the entire month has no recorded expenses, all categories show `$0.00` followed by `No expenses recorded for this month.`
  * Does not mutate or save data; a configured trip or budget is not required.
* **Examples**:
  * `stats`
  * `stats m/10-2026`
* **Expected Output**:
  ```text
  Spending for October 2026:
  Food: $30.30
  Transport: $12.50
  Accommodation: $0.00
  Entertainment: $0.00
  Shopping: $0.00
  Other: $0.00
  ==============================
  TOTAL SPENT: $42.80
  ==============================
  ```

#### Comparing Monthly Spending: `stats compare`
Compares spending across all categories between two specified months, showing category changes and total differences.

* **Format**: `stats compare m/MM-YYYY m/MM-YYYY`
* **Parameters**:
  * Exactly two valid months are required using `m/MM-YYYY`.
  * The first month is the base month; the second month is compared against it (even if earlier).
  * Comparing the same month is supported and shows zero change.
* **Behavior**:
  * For all six categories, displays the base amount, second amount, and the dollar difference (second minus first).
  * A `+` sign indicates spending increased, while `-` indicates spending decreased.
  * `$0.00` indicates no change in spending.
  * Missing expenses in a category show `$0.00`.
  * The highlighted `TOTAL SPENT` row compares the total expenditures for both months.
* **Example**:
  ```
  stats compare m/09-2026 m/10-2026
  ```
* **Sample Output Rows**:
  ```text
  Food: $10.10 -> $30.20 (change: +$20.10)
  Transport: $12.50 -> $0.00 (change: -$12.50)
  ```

---

### 6. Data Storage

All your data is automatically saved to a plain text file at `data/tripjio.txt` whenever you make changes (adding, editing, deleting expenses, setting budgets, setting trip dates, or exiting).

The file is organized into distinct sections:
```text
[expense]
E|Lunch at canteen|8.50|FOOD|15-10-2026
E|Museum ticket|15.00|ENTERTAINMENT|12-10-2026

[budget]
default=1000.00
12-2026=1500.00

[trips]
T|Original trip|10-12-2026|24-12-2026
A|Original trip
```

* **UTF-8 Encoding**: File content is encoded in UTF-8, so international characters (e.g. `Phở`, `Café`) are preserved across platforms.
* **Corrupted Line Protection**: If any individual line in the storage file is corrupted or formatted incorrectly, TripJio skips that line with a warning and continues loading all other valid records.

---

## FAQ

**Q: How do I transfer my data to another computer?**  
**A:** Simply copy the `data/tripjio.txt` file from your current TripJio folder to the `data/` folder on your other computer.

**Q: Can I manually edit `data/tripjio.txt`?**  
**A:** Yes, but back up the file first. Current sections include `[expense]`,
`[budget]`, `[trips]` and `[rates]`. Use `DD-MM-YYYY` for dates and `MM-YYYY`
for budgets. Invalid records produce warnings; invalid selected-trip names leave
no trip selected. The old `[trip]` section is used only when `[trips]` is absent.

---

## Command Summary

| Action | Command Format | Example |
| :--- | :--- | :--- |
| **Help** | `help` | `help` |
| **Add Expense** | `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]` | `add d/Dinner a/24.50 c/food dt/15-10-2026` |
| **List Expenses** | `list [c/CATEGORY] [m/MONTH]` | `list c/food m/10-2026` |
| **Edit Expense** | `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]` | `edit 1 a/20.00 d/Group dinner` |
| **Delete Expense**| `delete INDEX` | `delete 2` |
| **Set Budget** | `budget set a/AMOUNT [m/MONTH]` | `budget set a/800 m/11-2026` |
| **Budget Status** | `budget status [m/MONTH]` | `budget status` |
| **Set Trip Dates** | `trip set s/START_DATE e/END_DATE` | `trip set s/10-12-2026 e/24-12-2026` |
| **Trip Status** | `trip status` | `trip status` |
| **Monthly Statistics** | `stats [m/MONTH]` | `stats m/10-2026` |
| **Compare Statistics** | `stats compare m/MONTH m/MONTH` | `stats compare m/09-2026 m/10-2026` |
| **Exit** | `exit` | `exit` |
