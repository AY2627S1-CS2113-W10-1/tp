# TripJio User Guide

TripJio is a **budget tracker and travel app for students**. Whether you are on a weekend getaway, a recess-week trip, a graduation trip or a semester abroad, TripJio helps you keep track of what you spend and stay within your budget.

TripJio is a **desktop app operated through a Command Line Interface (CLI)**: you type short commands instead of clicking through menus, so if you type fast, you can record an expense in a few seconds.

TripJio is a **single-user app** with **no paywalls or friction**. All your data stays on your own computer in a plain text file. There are no accounts and no internet connection is needed.

With TripJio you can:

* record your daily expenses and sort them into categories,
* record expenses in other currencies using exchange rates you enter yourself (counted in SGD),
* split a group bill (e.g. a dinner with friends) and record only **your share**,
* set a **monthly budget** and get warned before you overspend,
* plan several **trips**, see how much you spent on each, and view spending statistics.

---

## Table of Contents

* [Quick Start](#quick-start)
* [Features](#features)
  * [Notes about the command format](#notes-about-the-command-format)
  * [Viewing help: `help`](#viewing-help-help)
  * **Expenses**: `add`, `list`, `edit`, `delete`
  * **Exchange rates**: `rate set`, `rate list`
  * **Split expenses**: `split`, `split list`
  * **Monthly budget**: `budget set`, `budget status`, budget warnings
  * **Trips**: `trip add`, `trip list`, `trip select`, `trip set`, `trip edit`, `trip delete`, `trip status`
  * **Statistics**: `stats`, `stats compare`
  * [Exiting the program: `exit`](#exiting-the-program-exit)
  * [Saving the data](#saving-the-data)
  * [Editing the data file](#editing-the-data-file)
* [FAQ](#faq)
* [Known Issues](#known-issues)
* [Command Summary](#command-summary)

---

## Quick Start

1. Ensure you have **Java 25** or above installed on your computer.
   * **Mac users:** make sure you have the exact JDK version prescribed in the course.
2. Download the latest `tripjio.jar` from the [Releases page](https://github.com/AY2627S1-CS2113-W10-1/tp/releases).
3. Copy the file to the folder you want to use as the home folder for TripJio.
4. Open a terminal, `cd` into that folder, and run:
   ```
   java -jar tripjio.jar
   ```
5. You should see the TripJio logo, followed by a welcome message:
   ```
   ████████╗██████╗ ██╗██████╗     ██╗██╗ ██████╗ 
   ╚══██╔══╝██╔══██╗██║██╔══██╗    ██║██║██╔═══██╗
      ██║   ██████╔╝██║██████╔╝    ██║██║██║   ██║
      ██║   ██╔══██╗██║██╔═══╝██   ██║██║██║   ██║
      ██║   ██║  ██║██║██║    ╚█████╔╝██║╚██████╔╝
      ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚════╝ ╚═╝ ╚═════╝ 

   Hello! Welcome to TripJio, your travel budget buddy.
   What would you like to do?
   ```
6. Type a command and press Enter. Some commands to try:
   * `add d/Lunch a/12.50 c/food`: adds a $12.50 food expense for today.
   * `list`: lists all your expenses.
   * `budget set a/1000`: sets a monthly budget of $1,000.
   * `help`: shows every command.
   * `exit`: exits the app.
7. Refer to [Features](#features) below for the details of each command.

   > Each response ends with a separator line (`------...`). It is left out of the examples in this guide.

---

## Features

### Notes about the command format

### Notes about the command format

* Words in `UPPER_CASE` are parameters you supply.<br>
  e.g. in `add d/DESCRIPTION a/AMOUNT`, `DESCRIPTION` and `AMOUNT` are parameters, as in `add d/Lunch a/12.50`.
* Items in square brackets are optional.<br>
  e.g. `add d/DESCRIPTION a/AMOUNT [c/CATEGORY]` can be used as `add d/Lunch a/12.50 c/food` or as `add d/Lunch a/12.50`.
* Parameters with a prefix (such as `d/` or `a/`) can be given in any order.<br>
  e.g. `add d/Lunch a/12.50` and `add a/12.50 d/Lunch` are both accepted.
* Each prefix can be given only once. A prefix that the command does not use (e.g. `add d/Lunch a/5 s/01-10-2026`) is rejected with an error.
* `trip/` is a flag: it takes no value, e.g. `list trip/`.
* `INDEX` refers to the number shown next to an item in the most recent `list` (or `trip list`) output. It must be a positive whole number, e.g. 1, 2, 3.
* Command words are case-insensitive: `LIST` and `list` both work.
* Extra words after commands that take no parameters (such as `help` and `exit`) are ignored.
* All amounts are in **SGD**, unless you add an expense with `cur/`. Then TripJio converts it to SGD with the rate you set (see [Exchange rates](#exchange-rates)).

**Parameter formats**

| Parameter | Format | Example |
|---|---|---|
| `DESCRIPTION` | Text, must not be empty and must not contain `/` or `\|` | `Lunch at canteen` |
| `AMOUNT` | Positive number in SGD, up to 2 decimal places, at most 1,000,000 | `12.50` |
| `CATEGORY` | One of: `food`, `transport`, `accommodation`, `entertainment`, `shopping`, `other` (case-insensitive) | `food` |
| `DATE` | `DD-MM-YYYY`, must be a real date | `01-09-2026` |
| `MONTH` | `MM-YYYY` | `10-2026` |
| `PEOPLE` (`n/` in `split`) | Whole number from 2 to 50, **including you** | `3` |
| `MY_SHARE` | Same rules as `AMOUNT`, and not more than the full bill | `40` |
| `NAME` (`n/` in `trip`) | Trip name, same rules as `DESCRIPTION`, unique (case-insensitive) | `Japan` |
| `CURRENCY` | Three-letter currency code (case-insensitive) | `EUR`, `JPY` |
| `RATE` | How many SGD one unit of the currency is worth. Positive, up to 8 decimal places, at most 1,000,000 | `1.45` |
| `sort/` | `amount` or `date` (lowercase) | `sort/amount` |
| `order/` | `asc` or `desc` (lowercase), only together with `sort/`. Default: `desc` | `order/asc` |

---

### Viewing help: `help`

Shows the list of all commands and their formats.

Format: `help`

---

## Expenses

### Adding an expense: `add`

Adds an expense that you paid for yourself.

Format: `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]`

* If `CATEGORY` is not given, the expense is put under `other`.
* If `DATE` is not given, today's date is used.
* `DATE` cannot be in the future.

Examples:

* `add d/Lunch a/12.50 c/food`
* `add d/Metro card a/45 c/transport dt/02-10-2026`

Expected output:

```
Added expense:
  [Transport] Metro card - $45.00 (02-10-2026)
You now have 2 expenses.
```

> After adding an expense, TripJio checks your budget for that month and may show a [budget warning](#budget-warnings).

---

### Listing expenses: `list`

Shows your expenses, optionally filtered by category and/or month.

Format: `list [c/CATEGORY] [m/MONTH]`

* Expenses are shown in the order they were added.
* Split expenses are shown with your share as the amount, followed by the full bill.
* The total at the bottom is the sum of the expenses shown.

Examples:

* `list` shows all expenses.
* `list c/food` shows all food expenses.
* `list c/food m/10-2026` shows food expenses in October 2026.

Expected output for `list`:

```
Here are your expenses:
1. [Food] Lunch - $12.50 (15-10-2026)
2. [Transport] Metro card - $45.00 (02-10-2026)
3. [Food] Group dinner - $30.00 (14-10-2026) [Split: $90.00 / 3 people]
Total: $87.50
```

> When a filter is used, the numbers shown are still the expenses' original indexes, so you can use them directly with `edit` and `delete`.

---

### Editing an expense: `edit`

Edits an existing expense.

Format: `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]`

* At least one of the optional fields must be given.
* Fields that are given replace the existing values. Fields not given stay the same.
* For a **split expense**, `a/AMOUNT` changes the **full bill**:
  * If the bill was split equally, your share is recalculated as the new bill divided by the number of people.
  * If you gave your own share with `my/`, your share stays the same and only the full bill changes. The new bill cannot be less than your share.
  * To change the number of people or your own share, delete the split expense and add it again.

Examples:

* `edit 1 a/13.20` changes the amount of expense 1 to $13.20.
* `edit 2 c/transport dt/03-10-2026` changes the category and date of expense 2.

Expected output:

```
Edited expense:
  [Food] Lunch - $13.20 (15-10-2026)
```

---

### Deleting an expense: `delete`

Deletes an expense, including a split expense.

Format: `delete INDEX`

Example: `delete 3`

Expected output:

```
Deleted expense:
  [Food] Group dinner - $30.00 (14-10-2026) [Split: $90.00 / 3 people]
You now have 2 expenses.
```

---

## Split expenses

Use this when you share a bill with friends, e.g. a group dinner or a shared taxi. TripJio records **only your share** as your expense, so your budget and statistics show what **you** actually spent.

### Splitting a group expense: `split`

Adds a group expense and records your share of it.

Format: `split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE]`

* `TOTAL_AMOUNT` is the **full bill** for the whole group.
* `PEOPLE` is the number of people sharing the bill, **including you**.
* By default, the bill is split equally: your share is `TOTAL_AMOUNT / PEOPLE`, rounded to 2 decimal places.
  Your share must be at least $0.01, so a very small bill cannot be split among many people.
* If you did not pay an equal share (e.g. you ordered more), use `my/MY_SHARE` to give your actual share. `MY_SHARE` must be more than 0 and not more than `TOTAL_AMOUNT`.
* `CATEGORY` and `DATE` work the same way as in [`add`](#adding-an-expense-add).
* A split expense appears in `list` like a normal expense and can be edited or deleted with `edit` and `delete`.

Examples:

* `split d/Group dinner a/90 n/3 c/food dt/14-10-2026`
* `split d/Hotpot a/120 n/4 my/40 c/food`

Expected output:

```
Added split expense:
  [Food] Group dinner - $30.00 (14-10-2026) [Split: $90.00 / 3 people]
Your share: $30.00 of $90.00.
You now have 3 expenses.
```

---

### Listing split expenses: `split list`

Shows only your split expenses, with the full bill and your share.

Format: `split list [m/MONTH]`

Example: `split list m/10-2026`

Expected output:

```
Split expenses in October 2026:
3. Group dinner (14-10-2026) | Bill: $90.00  | People: 3 | Your share: $30.00
5. Hotpot (20-10-2026)       | Bill: $120.00 | People: 4 | Your share: $40.00
Total bills: $210.00 | Your total share: $70.00
```

> The numbers shown are the same indexes as in `list`.

---

## Monthly budget

### Setting a monthly budget: `budget set`

Sets how much you plan to spend in a month.

Format: `budget set a/AMOUNT [m/MONTH]`

* Without `m/MONTH`: sets your **default monthly budget**, which applies to every month that does not have its own budget.
* With `m/MONTH`: sets a budget for that month only, e.g. a higher budget for a month in which you travel.
* Setting a budget again replaces the old value.

Examples:

* `budget set a/1000`
* `budget set a/1500 m/12-2026`

Expected output:

```
Default monthly budget set to $1,000.00.
```

```
Budget for December 2026 set to $1,500.00.
```

---

### Viewing budget status: `budget status`

Shows how much of a month's budget you have used.

Format: `budget status [m/MONTH]`

* If `MONTH` is not given, the current month is shown.
* For the current month, TripJio also shows how much you can spend per day for the rest of the month.

Example: `budget status`

Expected output (on 15-10-2026):

```
Budget for October 2026
  Budget:     $1,000.00
  Spent:        $587.50 (58.8%)
  Remaining:    $412.50
  Days left:  17 (including today)
  You can spend about $24.26 per day.
```

If no budget has been set:

```
You have not set a budget for October 2026. Use: budget set a/AMOUNT
```

---

### Budget warnings

After you `add`, `split` or `edit` an expense, TripJio automatically checks the budget of the month the expense belongs to:

* When you reach **80%** of the budget:
  ```
  Warning: You have used 85.0% of your budget for October 2026 ($850.00 / $1,000.00).
  ```
* When you go **over** the budget:
  ```
  Warning: You have exceeded your budget for October 2026 by $45.00.
  ```

No warning is shown if that month has no budget.

---

## Trip dates and statistics

### Setting trip dates: `trip set`

Sets the start and end dates of your current trip.

Format: `trip set s/START_DATE e/END_DATE`

* `END_DATE` must be on or after `START_DATE`.
* TripJio tracks one trip at a time. Setting the dates again replaces the old trip.

Example: `trip set s/10-12-2026 e/24-12-2026`

Expected output:

```
Trip set: 10-12-2026 to 24-12-2026 (15 days).
```

---

### Viewing trip status: `trip status`

Shows your progress through the trip and how much you have spent during it.

Format: `trip status`

Expected output (on 18-12-2026):

```
Trip: 10-12-2026 to 24-12-2026 (15 days)
  Days passed:     9
  Days remaining:  6
  Total spent:     $540.00
  Average per day: $60.00
```

* `Total spent` counts expenses dated within the trip, including your share of split expenses.
* If the trip has not started yet, TripJio shows how many days are left until it starts. If the trip has ended, it shows the total and average spending for the whole trip.
* If no trip has been set, TripJio asks you to set one with `trip set`.

---

### Viewing monthly statistics: `stats`

Shows how your spending in a month is divided across categories.

Format: `stats [m/MONTH]`

* If `MONTH` is not given, the current month is shown.
* Categories with no spending are still shown, with $0.00.

Example: `stats m/09-2026`

Expected output:

```
Spending for September 2026
  Food              $420.00  (40.0%)
  Transport         $210.00  (20.0%)
  Accommodation     $315.00  (30.0%)
  Entertainment      $63.00   (6.0%)
  Shopping           $21.00   (2.0%)
  Other              $21.00   (2.0%)
  Total           $1,050.00
```

---

### Comparing two months: `stats compare`

Compares your spending in two months, category by category.

Format: `stats compare m/MONTH m/MONTH`

* Exactly two months must be given, and they must be different.

Example: `stats compare m/09-2026 m/10-2026`

Expected output:

```
Category          Sep 2026    Oct 2026    Change
Food               $420.00     $380.00   -$40.00
Transport          $210.00     $150.00   -$60.00
Accommodation      $315.00     $315.00     $0.00
Entertainment       $63.00     $120.00   +$57.00
Shopping            $21.00      $80.00   +$59.00
Other               $21.00      $25.00    +$4.00
Total            $1,050.00   $1,070.00   +$20.00
```

---

### Exiting the program: `exit`

Saves your data and exits TripJio.

Format: `exit`

Expected output:

```
Bye! Your data has been saved. Safe travels!
```

---

### Saving the data

TripJio saves your data to `data/tripjio.txt` automatically after every command that changes your data. There is no need to save manually.

### Editing the data file

The data file `data/tripjio.txt` is a plain text file. Advanced users can edit it directly.

The file is divided into blocks. For example:

```
[expense]
E|Lunch|12.50|FOOD|15-10-2026
S|Group dinner|90.00|3|30.00|FOOD|14-10-2026

[budget]
default=1000.00
12-2026=1500.00

[trip]
start=10-12-2026
end=24-12-2026
```

* In the `[expense]` block, each line is one expense. `E` lines are normal expenses: `E|DESCRIPTION|AMOUNT|CATEGORY|DATE`. `S` lines are split expenses: `S|DESCRIPTION|TOTAL_AMOUNT|PEOPLE|MY_SHARE|CATEGORY|DATE`.
* Amounts are written without a dollar sign or thousands separators, e.g. `1234.50`.
* Dates use `DD-MM-YYYY` and months use `MM-YYYY`, the same as in commands.
* Categories are written in capital letters, e.g. `FOOD`.

> **Caution:** if a line in the file is in an invalid format, TripJio skips that line when starting and shows a warning. Make a backup of the file before editing it.

---

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install TripJio on the other computer, then copy your `data/tripjio.txt` into the `data` folder next to `tripjio.jar` there, replacing the empty file that TripJio creates.

**Q**: Can I record expenses in other currencies (e.g. EUR, SEK)?<br>
**A**: Not in v1.0. All amounts are in SGD, so convert the amount before recording it. Multi-currency support is planned for a future version.

**Q**: My friends also want to use TripJio. Can we share data?<br>
**A**: TripJio is a personal, single-user app. Each person keeps their own data. Use `split` to record your share of a group bill.

**Q**: Can I create my own categories?<br>
**A**: Not in v1.0. Use `other` for expenses that do not fit the six built-in categories.

---

## Known Issues

* None at the moment.

---

## Command Summary

| Action | Format, Examples |
|---|---|
| **Help** | `help` |
| **Add expense** | `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE]`<br>e.g. `add d/Lunch a/12.50 c/food` |
| **List expenses** | `list [c/CATEGORY] [m/MONTH]`<br>e.g. `list c/food m/10-2026` |
| **Edit expense** | `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE]`<br>e.g. `edit 1 a/13.20` |
| **Delete expense** | `delete INDEX`<br>e.g. `delete 3` |
| **Split group expense** | `split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE]`<br>e.g. `split d/Group dinner a/90 n/3 c/food` |
| **List split expenses** | `split list [m/MONTH]`<br>e.g. `split list m/10-2026` |
| **Set monthly budget** | `budget set a/AMOUNT [m/MONTH]`<br>e.g. `budget set a/1000`, `budget set a/1500 m/12-2026` |
| **View budget status** | `budget status [m/MONTH]`<br>e.g. `budget status` |
| **Set trip dates** | `trip set s/START_DATE e/END_DATE`<br>e.g. `trip set s/10-12-2026 e/24-12-2026` |
| **View trip status** | `trip status` |
| **Monthly statistics** | `stats [m/MONTH]`<br>e.g. `stats m/09-2026` |
| **Compare two months** | `stats compare m/MONTH m/MONTH`<br>e.g. `stats compare m/09-2026 m/10-2026` |
| **Exit** | `exit` |
