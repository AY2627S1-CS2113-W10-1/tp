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
  * [**Expenses**](#expenses): `add`, `list`, `edit`, `delete`, sorting
  * [**Exchange rates**](#exchange-rates): `rate set`, `rate list`
  * [**Split expenses**](#split-expenses): `split`, `split list`, `owe`, `settle`
  * [**Monthly budget**](#monthly-budget): `budget set`, `budget status`, budget warnings
  * [**Trips**](#trips): `trip set`, `trip status`, `trip add`, `trip list`, `trip select`, `trip edit`, `trip delete`, `trip/` filtering
  * [**Statistics**](#statistics): `stats`, `stats compare`
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
| `NAMES` (`p/` in `split`) | Exactly `n - 1` distinct other people, comma-separated; no empty names, `/`, `\|` or line breaks | `Alice,Bob` |
| `CURRENCY` | Three-letter currency code (case-insensitive) | `EUR`, `JPY` |
| `RATE` | How many SGD one unit of the currency is worth. Positive, up to 8 decimal places, at most 1,000,000 | `1.45` |
| `sort/` | `amount` or `date` (lowercase) | `sort/amount` |
| `order/` | `asc` or `desc` (lowercase), only together with `sort/`. Default: `desc` | `order/asc` |

---

### Viewing help: `help`

Shows the list of all commands and their formats.

Format: `help`

Expected output:

```
Here are the commands you can use:
help
add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE] [cur/CURRENCY]
list [c/CATEGORY] [m/MONTH] [sort/amount|date] [order/asc|desc] [trip/]
edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE] [my/MY_SHARE]
delete INDEX
split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE] [p/NAME,NAME] [paid/NAME] [cur/CURRENCY rate/RATE]
split list [m/MONTH] [trip/]
owe [p/NAME]
settle p/NAME [a/AMOUNT]
budget set a/AMOUNT [m/MONTH]
budget status [m/MONTH]
trip set s/START_DATE e/END_DATE
trip status
trip add n/NAME s/START_DATE e/END_DATE
trip list
trip select INDEX
trip edit INDEX [n/NAME] [s/START_DATE] [e/END_DATE]
trip delete INDEX
rate set from/CURRENCY to/SGD r/RATE
rate list
Amounts and reports use SGD; rates are manual, not live.
stats [m/MONTH] [trip/]
stats compare m/MONTH m/MONTH
exit
```

---

## Expenses

### Adding an expense: `add`

Adds an expense that you paid for yourself.

Format: `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE] [cur/CURRENCY]`

* If `CATEGORY` is not given, the expense is put under `other`.
* If `DATE` is not given, today's date is used. `DATE` cannot be in the future.
* With `cur/CURRENCY`, `AMOUNT` is in that currency and is converted to SGD using the rate you set with [`rate set`](#exchange-rates).
  * The rate is saved with the expense, so changing the rate later does not change expenses you already added.
  * The original amount may be more than 1,000,000, but the converted SGD amount must be at most $1,000,000.00.
  * `cur/SGD` needs no rate.

Examples:

* `add d/Lunch a/12.50 c/food dt/15-10-2026`
* `add d/Metro card a/45 c/transport dt/02-10-2026`
* `add d/Ramen a/12 c/food dt/10-10-2026 cur/EUR` (after `rate set from/EUR to/SGD r/1.45`)

Expected output (second example):

```
Added expense:
  [Transport] Metro card - $45.00 (02-10-2026)
You now have 2 expenses.
```

Expected output (foreign-currency example, EUR 12 × 1.45 = SGD 17.40):

```
Added expense (SGD):
  [Food] Ramen - $17.40 (10-10-2026) [EUR 12.00 at 1.45 SGD/EUR]
You now have 4 expenses.
```

If no rate has been set for the currency:

```
Error: No rate for EUR. Use: rate set from/EUR to/SGD r/RATE.
```

> After adding an expense, TripJio checks your budget for that month and may show a [budget warning](#budget-warnings).

---

### Listing expenses: `list`

Shows your expenses, optionally filtered and sorted.

Format: `list [c/CATEGORY] [m/MONTH] [trip/] [sort/amount|date] [order/asc|desc]`

* Without `sort/`, expenses are shown in the order they were added.
* `trip/` shows only expenses dated within the **selected trip** (both end dates included). A trip must be selected first, see [Trips](#trips).
* Filters can be combined: an expense is shown only if it matches all of them.
* `sort/amount` sorts by the SGD amount (your share for split expenses). `sort/date` sorts by the expense date.
* `order/` needs `sort/`. If `order/` is not given, the order is `desc` (largest or latest first).
* Split expenses show your share as the amount, followed by the number of people. Foreign-currency expenses show the SGD amount, followed by the original amount and rate.
* The total at the bottom is the sum of the expenses shown.

Examples:

* `list` shows all expenses.
* `list c/food m/10-2026` shows food expenses in October 2026.
* `list sort/amount` shows all expenses, largest first.
* `list c/food sort/date order/asc` shows food expenses, earliest first.
* `list trip/ sort/amount` shows the expenses of the selected trip, largest first.

Expected output for `list`:

```
Here are your expenses:
1. [Food] Lunch - $12.50 (15-10-2026)
2. [Transport] Metro card - $45.00 (02-10-2026)
3. [Food] Group dinner - $30.00 (14-10-2026) (Split among 3)
4. [Food] Ramen - $17.40 (10-10-2026) [EUR 12.00 at 1.45 SGD/EUR]
Total: $104.90
```

Expected output for `list c/food sort/date order/asc`:

```
Here are your expenses:
4. [Food] Ramen - $17.40 (10-10-2026) [EUR 12.00 at 1.45 SGD/EUR]
3. [Food] Group dinner - $30.00 (14-10-2026) (Split among 3)
1. [Food] Lunch - $12.50 (15-10-2026)
Total: $59.90
```

> The number in front of each expense is always its original index, even when the list is filtered or sorted, so you can use it directly with `edit` and `delete`. Sorting only changes what is shown; it does not change the saved order.

If you have no expenses, or none match the filters:

```
You have no expenses yet.
```

```
No expenses match the given filter.
```

---

### Editing an expense: `edit`

Edits an existing expense.

Format: `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE] [my/MY_SHARE]`

* At least one of the optional fields must be given.
* Fields that are given replace the existing values. Fields not given stay the same.
* `DATE` cannot be in the future.
* For a **foreign-currency expense**, `a/AMOUNT` is in the original currency and is converted with the expense's saved rate. The currency and rate cannot be changed; delete the expense and add it again instead.
* For a **split expense**, `a/AMOUNT` changes the **full bill**:
  * If the bill was split equally, your share is recalculated as the new bill divided by the number of people.
  * If you gave your own share with `my/`, your share stays the same and only the full bill changes. The new bill cannot be less than your share.
  * Use `my/MY_SHARE` to change your own SGD share; it must be at least $0.01 and no more than the full bill. You can supply `a/` and `my/` together; invalid edits leave the whole record unchanged.
  * The full bill entered through `edit` is always in SGD, including for a foreign split. To change the number of people, delete the split expense and add it again.

Examples:

* `edit 1 a/13.20` changes the amount of expense 1 to $13.20.
* `edit 2 c/transport dt/03-10-2026` changes the category and date of expense 2.
* `edit 4 a/10` changes the Ramen to EUR 10, converted at its saved rate of 1.45.

Expected output (first and third examples):

```
Edited expense:
  [Food] Lunch - $13.20 (15-10-2026)
```

```
Edited expense:
  [Food] Ramen - $14.50 (10-10-2026) [EUR 10.00 at 1.45 SGD/EUR]
```

> After editing, TripJio checks the budget of the expense's month and may show a [budget warning](#budget-warnings).

---

### Deleting an expense: `delete`

Deletes an expense, including a split or foreign-currency expense.

Format: `delete INDEX`

* Expenses after the deleted one move up by one index. Use `list` again before the next `edit` or `delete`.

Example: `delete 3`

Expected output:

```
Deleted expense:
  [Food] Group dinner - $30.00 (14-10-2026) (Split among 3)
You now have 3 expenses.
```

If `INDEX` does not exist (e.g. `delete 9` when you have 3 expenses):

```
Error: Invalid index: 9. You have 3 expenses.
```

---

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

---

## Exchange rates

All existing amounts and budgets are interpreted as **SGD**. Reports continue to
use the dollar symbol for SGD. This version does not fetch live exchange rates
or support changing the reporting currency.

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
`cur/SGD` uses rate 1 automatically for personal expenses. Foreign splits require
both `cur/CURRENCY` and `rate/RATE`; see [Named splits and settling debts](#named-splits-and-settling-debts).

---

## Split expenses

Use this when you share a bill with friends, e.g. a group dinner or a shared taxi. TripJio records **only your share** as your expense, so your budget and statistics show what **you** actually spent.

### Splitting a group expense: `split`

Adds a group expense and records your share of it.

Format: `split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE] [p/NAMES] [paid/NAME] [cur/CURRENCY rate/RATE]`

* `TOTAL_AMOUNT` is the **full bill** for the whole group.
* `PEOPLE` is the number of people sharing the bill, **including you**.
* By default, the bill is split equally: your share is `TOTAL_AMOUNT / PEOPLE`, rounded to 2 decimal places.
  Your share must be at least $0.01, so a very small bill cannot be split among many people.
* If you did not pay an equal share (e.g. you ordered more), use `my/MY_SHARE` to give your actual share. `MY_SHARE` is in SGD and must be at least $0.01 and no more than the converted full bill.
* `CATEGORY` and `DATE` work the same way as in [`add`](#adding-an-expense-add).
* A split expense appears in `list` like a normal expense and can be edited or deleted with `edit` and `delete`.

Examples:

* `split d/Group dinner a/90 n/3 c/food dt/14-10-2026`
* `split d/Hotpot a/120 n/4 my/40 c/food`

Expected output:

```
Got it. I've added this split expense:
  [Food] Group dinner - $30.00 (14-10-2026) (Split among 3)
Now you have 3 expenses in the list.
```

---

### Listing split expenses: `split list`

Shows only your split expenses, with your SGD share and the number of people.

Format: `split list [m/MONTH] [trip/]`

Example: `split list m/10-2026`

Expected output:

```
Here are your split expenses:
3. [Food] Group dinner - $30.00 (14-10-2026) (Split among 3)
5. [Food] Hotpot - $40.00 (20-10-2026) (Split among 4)
```

> The numbers shown are the same indexes as in `list`.

---

### Named splits and settling debts

```text
split d/Dinner a/100 n/3 p/Alice,Bob
split d/Taxi a/60 n/2 p/Alice paid/Alice
split d/Hotel a/100 n/3 p/Alice,Bob cur/EUR rate/1.45 my/45
owe
owe p/Alice
settle p/Alice a/10
settle p/Alice
```

`n/` includes you. Optional `p/` lists exactly `n - 1` other participants,
separated by commas. Names are case-insensitive and must be distinct and non-empty;
they cannot contain commas, `/`, `|`, or line breaks. Omit `paid/` when you paid
the bill. Otherwise, `paid/` must name someone in `p/`. Unnamed splits still count
your spending but cannot assign debts to individual people.

`owe` shows each person's net balance across all splits and settlements, in SGD,
with names in alphabetical order and amounts formatted with thousands separators;
`owe p/NAME` filters it to one person. `settle p/NAME a/AMOUNT` records a partial
payment; omitting `a/` settles the full outstanding balance. Payments must be positive
SGD amounts with at most two decimal places and cannot exceed the outstanding balance.
Settlement direction is determined from that balance. Settlements do not change expenses
or budget spending. If there is no balance, settlement reports an error.

Settlements are stored independently of split expenses. Editing or deleting a split
keeps its recorded payments, so the recalculated balance can reverse direction.
For example, if Alice paid you $30 and you then delete the split that created her
$30 debt, `owe` shows that you owe Alice $30 because her payment is still recorded.
There is currently no command to edit or delete a settlement.

For foreign splits, `a/` is the original-currency bill and `rate/` is SGD per unit
of that currency. Both `cur/` and `rate/` are required together; configured `rate set`
values are not used for splits. The full bill is converted and rounded to SGD cents
once, then split. `my/` is always your custom share in **SGD**. For example, the hotel
bill above is SGD 145, with your share fixed at SGD 45. Rates follow the manual-rate
rules above; an explicit SGD rate must be 1. The converted bill is capped at SGD
1,000,000 and your share must be at least SGD 0.01 and no more than the bill.
Remaining cents are distributed to named participants in list order.

`edit INDEX a/AMOUNT` on any split changes the full bill in **SGD**, including for a
foreign split; its saved currency and rate remain as historical input metadata.
An equal share is recalculated, while a custom share stays fixed. Use
`edit INDEX my/AMOUNT` to set your custom SGD share, or supply `a/` and `my/` together.
The final bill and share are validated together; rejected edits leave all fields unchanged.

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

## Trips

### Setting Trip Dates: `trip set`
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

---

### Viewing Trip Status: `trip status`
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

---

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

---

### Selected-trip filtering

```text
list trip/
list trip/ c/food m/10-2026 sort/amount order/desc
stats trip/
stats m/10-2026 trip/
split list trip/ m/10-2026
```

`trip/` is a flag with no value, allowed once on list, stats and split list.
It uses the currently selected trip's inclusive start/end dates. Select a trip
with `trip select INDEX` first. If none is selected, the command reports an error,
even if there are no expenses. `trip/1`, repeated flags and extra text are invalid.

Filters intersect: an expense must match the trip dates and any supplied month
or category. List sorting still works and keeps original expense indexes.
Split list shows only split bills. Reports use SGD amounts and your split share.
`stats trip/` covers the whole trip, including trips spanning several months;
`stats m/MM-YYYY trip/` covers only that month's portion. Unfiltered `stats`
still defaults to the current month. `stats compare` does not accept `trip/`.

Trip statistics starts with `Spending for trip NAME:`, or
`Spending for trip NAME in MONTH YEAR:` when a month is supplied. All six category
rows and TOTAL SPENT follow the existing statistics format. An empty intersection
shows zeros and `No expenses match the selected trip filter.`
List and split list use their existing empty-filter messages.
Filtering is read-only, does not assign expenses to trips, and does not change
budgets, saved data or the selected trip. Overlapping trips can include the same expense.

---

## Statistics

### Monthly Spending by Category: `stats`
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

---

### Comparing two months: `stats compare`

Compares your spending in two months, category by category, in a table.

Format: `stats compare m/MONTH m/MONTH`

* Exactly two months must be given. The first month is the base; the second is compared against it, even if it is earlier.
* Comparing a month with itself is allowed and shows no change.
* All six categories are shown, followed by the total. Categories with no spending show `$0.00`.
* `Change` is the second month minus the first: `+` means you spent more, `-` means you spent less.
* Amounts are in SGD and count only your share of split expenses.
* `stats compare` does not accept `trip/`.

Example: `stats compare m/09-2026 m/10-2026`

Expected output:

```
Spending comparison: September 2026 -> October 2026
Category               Sep 2026        Oct 2026          Change
---------------------------------------------------------------
Food                     $90.00         $100.00         +$10.00
Transport                $45.00          $30.00         -$15.00
Accommodation             $0.00           $0.00           $0.00
Entertainment             $0.00           $0.00           $0.00
Shopping                  $0.00          $20.00         +$20.00
Other                     $0.00           $0.00           $0.00
---------------------------------------------------------------
Total                   $135.00         $150.00         +$15.00
```

If neither month has any expenses, the table shows `$0.00` everywhere, followed by:

```
No expenses recorded for either month. :(
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

TripJio saves your data to `data/tripjio.txt` automatically after every command that changes your data, and again when you exit. There is no need to save manually.

### Editing the data file

The data file `data/tripjio.txt` is a plain text file. Advanced users can edit it directly.

The file is divided into blocks. For example:

```
[expense]
E|Lunch|12.50|FOOD|15-10-2026
S|Group dinner|90.00|3|30.00|FOOD|14-10-2026
F|Ramen|12.00|FOOD|10-10-2026|EUR|1.45

[budget]
default=1000.00
12-2026=1500.00

[trips]
T|Japan|10-12-2026|24-12-2026
A|Japan

[rates]
EUR=1.45
```

* In the `[expense]` block, each line is one expense:
  * `E` lines are normal expenses: `E|DESCRIPTION|AMOUNT|CATEGORY|DATE`.
  * `S` lines retain `S|DESCRIPTION|SGD_BILL|PEOPLE|SGD_SHARE|CATEGORY|DATE`, followed by payer, names, currency, rate and the custom-share flag; see [Saved-data compatibility](#saved-data-compatibility).
  * `F` lines are foreign-currency expenses: `F|DESCRIPTION|ORIGINAL_AMOUNT|CATEGORY|DATE|CURRENCY|RATE`.
* Amounts are written without a dollar sign or thousands separators, e.g. `1234.50`.
* Dates use `DD-MM-YYYY` and months use `MM-YYYY`, the same as in commands.
* Categories are written in capital letters, e.g. `FOOD`.
* The file is saved in UTF-8, so descriptions such as `Phở` or `Café` are kept correctly.

> **Caution:** if a line in the file is in an invalid format, TripJio skips that line when starting and shows a warning such as `Error: Skipped invalid line 4 in data file: ...`. The other lines are still loaded. Make a backup of the file before editing it.

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
Split expenses retain the legacy fields
`S|description|sgdBill|people|sgdShare|category|date` and append
`|payer|participants|currency|rate|hasCustomShare`. Optional metadata uses empty fields;
participants are comma-separated and the final flag is `true` or `false`.
The flag preserves custom shares even when they happen to equal an equal split.
Legacy seven-field records still load; their custom-share status is inferred by comparing
the saved share with the equal share. Saved split bills and shares are already in SGD,
so loading does not apply the rate again. `[owe]` records use `person|signedAmount`,
with two decimal places: positive means money received and negative means money paid.
Named trips and selection are stored under `[trips]`; manual rates under `[rates]`.
Keep a backup before using this format with an older application release: older
releases do not understand the new records.

---

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install TripJio on the other computer, then copy your `data/tripjio.txt` into the `data` folder next to `tripjio.jar` there, replacing the file that TripJio creates.

**Q**: Can I manually edit `data/tripjio.txt`?<br>
**A**: Yes, but back up the file first. Supported blocks include `[expense]`, `[budget]`, `[trips]`, `[rates]` and `[owe]`. See [Editing the data file](#editing-the-data-file). Invalid supported records are skipped with warnings; a later save omits them.

**Q**: Can I record expenses in other currencies (e.g. EUR, JPY)?<br>
**A**: Yes. Set a rate first with `rate set from/EUR to/SGD r/1.45`, then add the expense with `cur/EUR`. Rates are entered by you, not fetched online, and all reports are in SGD. See [Exchange rates](#exchange-rates).

**Q**: Does selecting a trip filter my expenses automatically?<br>
**A**: No. `list`, `split list` and `stats` show only the selected trip's expenses when you add `trip/`. Budgets are always monthly.

**Q**: My friends also want to use TripJio. Can we share data?<br>
**A**: TripJio is a personal, single-user app. Each person keeps their own data. Use `split` to record your share of a group bill.

**Q**: Can I create my own categories?<br>
**A**: Not at the moment. Use `other` for expenses that do not fit the six built-in categories.

---

## Known Issues

* In the data file, categories must be in capital letters (e.g. `FOOD`); lowercase categories are skipped as invalid lines.
* Older versions of TripJio cannot read files saved by this version. Keep a backup before going back to an older release.

---

## Command Summary

| Action | Format, Examples |
|---|---|
| **Help** | `help` |
| **Add expense** | `add d/DESCRIPTION a/AMOUNT [c/CATEGORY] [dt/DATE] [cur/CURRENCY]`<br>e.g. `add d/Lunch a/12.50 c/food`, `add d/Ramen a/12 c/food cur/EUR` |
| **List expenses** | `list [c/CATEGORY] [m/MONTH] [trip/] [sort/amount\|date] [order/asc\|desc]`<br>e.g. `list c/food m/10-2026`, `list trip/ sort/amount` |
| **Edit expense** | `edit INDEX [d/DESCRIPTION] [a/AMOUNT] [c/CATEGORY] [dt/DATE] [my/MY_SHARE]`<br>e.g. `edit 1 a/13.20` |
| **Delete expense** | `delete INDEX`<br>e.g. `delete 3` |
| **Set exchange rate** | `rate set from/CURRENCY to/SGD r/RATE`<br>e.g. `rate set from/EUR to/SGD r/1.45` |
| **List exchange rates** | `rate list` |
| **Split group expense** | `split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE] [p/NAMES] [paid/NAME] [cur/CURRENCY rate/RATE]`<br>e.g. `split d/Group dinner a/90 n/3 c/food` |
| **List split expenses** | `split list [m/MONTH] [trip/]`<br>e.g. `split list m/10-2026` |
| **View debts** | `owe [p/NAME]`<br>e.g. `owe p/Alice` |
| **Settle debt** | `settle p/NAME [a/AMOUNT]`<br>e.g. `settle p/Alice a/10` |
| **Set monthly budget** | `budget set a/AMOUNT [m/MONTH]`<br>e.g. `budget set a/1000`, `budget set a/1500 m/12-2026` |
| **View budget status** | `budget status [m/MONTH]`<br>e.g. `budget status` |
| **Add trip** | `trip add n/NAME s/START_DATE e/END_DATE`<br>e.g. `trip add n/Japan s/01-12-2026 e/14-12-2026` |
| **List trips** | `trip list` |
| **Select trip** | `trip select INDEX`<br>e.g. `trip select 1` |
| **Set trip dates** | `trip set s/START_DATE e/END_DATE`<br>e.g. `trip set s/10-12-2026 e/24-12-2026` |
| **Edit trip** | `trip edit INDEX [n/NAME] [s/START_DATE] [e/END_DATE]`<br>e.g. `trip edit 1 n/Tokyo` |
| **Delete trip** | `trip delete INDEX`<br>e.g. `trip delete 1` |
| **View trip status** | `trip status` |
| **Monthly statistics** | `stats [m/MONTH] [trip/]`<br>e.g. `stats m/09-2026`, `stats trip/` |
| **Compare two months** | `stats compare m/MONTH m/MONTH`<br>e.g. `stats compare m/09-2026 m/10-2026` |
| **Exit** | `exit` |
