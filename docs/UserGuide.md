# TripJio - User Guide

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
Configures the start and end dates of your current trip. Setting new dates replaces any previously saved trip.

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

[trip]
start=10-12-2026
end=24-12-2026
```

* **UTF-8 Encoding**: File content is encoded in UTF-8, so international characters (e.g. `Phở`, `Café`) are preserved across platforms.
* **Corrupted Line Protection**: If any individual line in the storage file is corrupted or formatted incorrectly, TripJio skips that line with a warning and continues loading all other valid records.

---

## FAQ

**Q: How do I transfer my data to another computer?**  
**A:** Simply copy the `data/tripjio.txt` file from your current TripJio folder to the `data/` folder on your other computer.

**Q: Can I manually edit `data/tripjio.txt`?**  
**A:** Yes, you can edit the file in any text editor. Ensure you follow the block structure (`[expense]`, `[budget]`, `[trip]`) and date formats (`DD-MM-YYYY` for dates, `MM-YYYY` for budgets). If any line contains invalid syntax, TripJio will skip that line with a warning upon launch while preserving the rest.

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
