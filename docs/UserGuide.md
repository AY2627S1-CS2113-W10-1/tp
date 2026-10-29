# TripJio User Guide

TripJio is a **budget tracker and travel app for students**. Whether you are on a weekend getaway, a recess-week trip, a graduation trip or a semester abroad, TripJio helps you keep track of what you spend and stay within your budget.

TripJio is a **desktop app operated through a Command Line Interface (CLI)**: you type short commands instead of clicking through menus, so if you type fast, you can record an expense in a few seconds.

TripJio is a **single-user app** with **no paywalls or friction**. All your data stays on your own computer in a plain text file. There are no accounts and no internet connection is needed.

With TripJio you can:

* record your daily expenses and sort them into categories,
* record expenses in other currencies using exchange rates you enter yourself (counted in SGD),
* split a group bill (e.g. a dinner with friends) and record only **your share**,
* set a **monthly budget** and get warned before you overspend,
* plan several **trips**, see how much you spent on each, and view spending statistics,
* **export** your expenses to a CSV file and open them in Excel, Numbers or Google Sheets.

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
  * [Exporting expenses: `export`](#exporting-expenses-export)
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
| `MY_SHARE` | Your SGD share, at least 0.01, at most two decimals and no more than the converted full bill | `40` |
| `NAMES` (`p/` in `split`) | Exactly n - 1 other names, comma-separated and unique ignoring case; no empty names, `/`, `\|` or line breaks | `Alice,Bob` |
| `NAME` (`n/` in `trip`) | Trip name, same rules as `DESCRIPTION`, unique (case-insensitive) | `Japan` |
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
export [c/CATEGORY] [m/MONTH] [trip/]
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
  * Change your own SGD share with `my/`, alone or with `a/`; the final pair is validated before any field changes. This is available only for splits.
  * On any split, including foreign splits, `a/` is the full SGD bill. To change the people/names/payer, delete and re-add; prior settlements remain.

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

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

## Exchange rates

<!-- @@author teetoozs -->
All existing amounts and budgets are interpreted as **SGD**. Reports continue to
use the dollar symbol for SGD. This version does not fetch live exchange rates
or support changing the reporting currency.

### Manual exchange rates

```text
rate set from/EUR to/SGD r/1.45
rate list
add d/Lunch a/12 c/food dt/10-10-2026 cur/EUR
```
<!-- @@author -->

The example rate means **1 EUR = 1.45 SGD**, not the reverse.
<!-- @@author teetoozs -->
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
<!-- @@author -->
`cur/SGD` uses rate 1 automatically for personal expenses. Foreign splits instead
require explicit `cur/` and `rate/`; see [Foreign splits](#splitting-a-foreign-currency-bill).

---

## Split expenses

Use splits for a shared dinner, taxi or other group purchase. TripJio keeps the
full bill but counts **only your own SGD share** in budgets and reports. Named
participants also let you track repayments; those payments do not count as spending.

### Splitting a group expense: `split`

Format: `split d/DESCRIPTION a/TOTAL_AMOUNT n/PEOPLE [my/MY_SHARE] [c/CATEGORY] [dt/DATE] [p/NAMES] [paid/NAME] [cur/CURRENCY rate/RATE]`

* `TOTAL_AMOUNT` is the full bill. Without currency options it is in SGD, with at
  most two decimal places and a maximum of SGD 1,000,000.
* `PEOPLE` is a whole number from 2 to 50, **including you**.
* Without `my/`, your share is the bill divided by the number of people, rounded
  to cents. Your share must be at least SGD 0.01.
* `my/` sets a fixed custom share in **SGD**. It must be at least SGD 0.01, have at
  most two decimal places and not exceed the converted full bill.
* Category defaults to `other`; date defaults to today. An expense date cannot be
  in the future. Category/date rules match [`add`](#adding-an-expense-add).
* `p/` and `paid/` are optional; their rules are explained below. Foreign bills
  require both `cur/` and `rate/`, as described in [Foreign splits](#splitting-a-foreign-currency-bill).
* After a successful split, TripJio checks that month's budget using **your share**
  and saves. A warning does not reject the purchase.

Example, starting with no expenses:

```text
split d/Dinner a/90 n/3 c/food dt/14-10-2026
```

Expected output, before any budget warning:

```text
Got it. I've added this split expense:
  [Food] Dinner - $30.00 (14-10-2026) (Split among 3)
Now you have 1 expense in the list.
```

For a custom share, use `split d/Taxi a/60 n/3 my/10 dt/15-10-2026`.
The bill is SGD 60 and your spending is SGD 10. An equal split of the same bill
would count SGD 20. Split creation does not add a second ordinary expense.

### Listing split expenses: `split list`

Format: `split list [m/MONTH] [trip/]`

This shows split expenses only. The displayed amount is your SGD share, followed
by the number of people; it does not display the full bill. Without filters, it
includes every month. `m/` narrows the month and `trip/` requires a selected trip;
combined filters intersect, including both trip boundary dates.

After adding the Dinner and Taxi examples above, run `split list m/10-2026`:

```text
Here are your split expenses:
1. [Food] Dinner - $30.00 (14-10-2026) (Split among 3)
2. [Other] Taxi - $10.00 (15-10-2026) (Split among 3)
```

Numbers remain **global expense indexes**, including when ordinary expenses are
interspersed or filters hide rows. Use those indexes for edit/delete; list again
after deletion because later indexes shift. Sorting/filtering other views does
not change saved order.

With no splits, output is `You have no split expenses yet.` With a month/trip
filter matching none, it is `No split expenses match the given filter.` If a trip
filter is requested with no selection, an error is shown instead of an empty list.

### Naming participants and the payer

`p/Alice,Bob` lists exactly `n - 1` **other people**, excluding you. Names must be
non-empty, distinct ignoring case, and separated by commas. They cannot contain
`/`, `|`, commas within a name, or line breaks. Surrounding whitespace is trimmed;
names are compared case-insensitively and shown in lowercase.

* Without `paid/`, **you paid the whole bill** and the named people owe you.
* With `paid/Alice`, **Alice paid the bill** and you owe Alice your own share.
  The payer must appear in `p/`. TripJio does not track debts between other people.
* A split without names still counts your spending, but has no individual debtor
  or creditor in `owe`. You cannot add/change its names or payer with edit; delete
  and re-add it, taking care of any existing payments as explained below.

Example in a fresh instance:

```text
split d/Dinner a/100 n/3 p/Alice,Bob dt/14-10-2026
```

Your equal share is SGD 33.33. Remaining cents are distributed in **participant
list order**, so the shares add up to the full bill:

| Person | SGD share |
| --- | --- |
| You | 33.33 |
| Alice (first name) | 33.34 |
| Bob (second name) | 33.33 |
| Total | 100.00 |

Reversing the names changes who receives the extra cent. With `my/`, the same
rule divides the remainder after your fixed share among the other people.

### Viewing debts: `owe`

Format: `owe [p/NAME]`

Shows net balances across **all** named splits and settlements. It does not apply
month or trip filters. Names appear alphabetically, with amounts in SGD and two
decimal places; large amounts use thousands separators, e.g. `$1,234.50`.

For the named SGD 100 Dinner above, `owe` prints:

```text
=== Debt Ledger ===
alice owes you: $33.34
bob owes you: $33.33
```

Use `owe p/ALICE` to show only Alice, ignoring case. If another person paid, a
negative net is shown as `You owe alice: $30.00`, for example. A person may owe you
on one bill and be owed on another; only the net is shown, not a per-bill history.

If there is no outstanding balance in the selected view, including an unknown
name filter, the message is `All debts are settled!`. `owe` does not change data.

### Recording repayments: `settle`

Format: `settle p/NAME [a/AMOUNT]`

Records a payment against that person's current net balance. Omit `a/` to settle
it fully. Otherwise give a positive **SGD** payment with at most two decimals,
no larger than the outstanding balance. The direction is automatic: a person who
owes you pays you; if you owe them, you pay them. Do not enter a negative payment.

Continuing the named Dinner example:

```text
settle p/Alice a/10
owe p/Alice
```

Expected output (separators omitted):

```text
Recorded settlement of $10.00 with alice.
=== Debt Ledger ===
alice owes you: $23.34
```

Then `settle p/alice` records SGD 23.34 and `owe p/alice` shows that she is settled.
Bob still owes SGD 33.33. Your spending remains SGD 33.33 throughout. Restarting
restores both the split and payments, so the balances do not reset.

For the other direction, start fresh with
`split d/Taxi a/60 n/2 p/Alice paid/Alice`. You owe Alice SGD 30; `settle p/alice`
records that outgoing payment and clears the balance. Settlement never creates
an expense or changes your monthly/trip spending.

**Payments survive bill changes.** Editing or deleting a split does not remove
recorded settlements. The balance is recalculated and can reverse direction.
For example, if Alice repays a SGD 30 debt and you delete the only split that
created it, `owe` shows that you owe Alice SGD 30 because her payment remains.
There is currently no command to edit/delete a settlement or undo a payment.

### Editing your share: `edit INDEX my/AMOUNT`

`my/` is available only on split expenses. It sets a fixed custom SGD share,
including when it happens to equal the current equal share. The share must be at
least one cent and no more than the full SGD bill. To change participant count,
names, payer or currency/rate, delete and re-add the split; this retains any prior
settlements, as explained above.

Example in a fresh instance:

```text
split d/Custom a/90 n/3 my/40 dt/14-10-2026
edit 1 a/30 my/10
```

The final bill becomes SGD 30 and own share SGD 10. The proposed pair is checked
together, so lowering both is valid. `edit 1 a/5 d/Changed` would then fail without
changing bill, share or description. A full-bill edit recalculates an equal share
but retains a custom share unless you also supply `my/`.

### Splitting a foreign-currency bill

Supply **both** `cur/CURRENCY` and `rate/RATE`. The rate means SGD per original
currency unit. It is entered explicitly for the split; configured `rate set`
values are not used. Rates must be positive plain decimals, up to eight decimal
places and at most 1,000,000; an explicit SGD rate must be 1.

Example in a fresh instance:

```text
split d/Hotel a/100 n/3 p/Alice,Bob paid/Alice cur/EUR rate/1.45 my/45 dt/14-10-2026
owe p/Alice
```

The full EUR 100 bill becomes SGD 145. Your `my/45` is **SGD 45**, not EUR 45,
and `owe` shows `You owe alice: $45.00`. Without `my/`, own share would be SGD 48.33.
The original full amount can exceed 1,000,000, but must be positive, finite and
cent-precision; the **converted full SGD bill** is capped at SGD 1,000,000.
Conversions rounding your share below a cent are rejected.

The saved bill/share are already in SGD; restarting does not convert them again.
Changing a configured rate does not revalue an existing split. Unlike a personal
foreign expense, **`edit INDEX a/150` on a split means a full SGD bill of 150**,
retaining its saved currency/rate as historical metadata and its SGD 45 custom
share. Personal foreign-expense edits still use the original currency and saved
rate as described under [Editing an expense](#editing-an-expense-edit).

### Split and repayment input errors

Rejected commands leave existing records unchanged. Typical examples:

| Input | Result |
| --- | --- |
| `split d/Bad a/10 n/1` | Number of people must be between 2 and 50. |
| `split d/Bad a/90 n/3 my/100` | Your custom share cannot be more than the total bill. |
| `split d/Bad a/90 n/3 p/Alice,ALICE` | Participant names must be unique ignoring case. |
| `split d/Bad a/90 n/3 p/Alice,Bob paid/Charlie` | Payer must appear in the participant list. |
| `split d/Bad a/90 n/3 cur/EUR` | Provide cur/ and rate/ together. |
| `settle p/Nobody` with no balance | No outstanding balance; no payment is recorded. |

Repeated/empty prefixes are rejected. A payment above the current balance is
also rejected, with the allowable balance shown in the error message.

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

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

<!-- @@author teetoozs -->
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
<!-- @@author -->

---

## Statistics

<!-- @@author teetoozs -->
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
<!-- @@author -->

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

### Exporting expenses: `export`

Saves your expenses to a CSV file, so you can open them in a spreadsheet (e.g. Excel, Numbers or Google Sheets) to make your own charts or share a trip's costs with friends.

Format: `export [c/CATEGORY] [m/MONTH] [trip/]`

* The filters work exactly like in [`list`](#listing-expenses-list): an expense is exported only if it matches all of them. Without filters, all expenses are exported.
* `trip/` exports only expenses dated within the **selected trip** (both end dates included). A trip must be selected first, see [Selected-trip filtering](#selected-trip-filtering).
* Sorting (`sort/`, `order/`) is not supported; rows are always in the order the expenses were added.
* The file is always `data/export.csv`, in the same folder as your data file. **Each export replaces the previous one**, so rename or move the file first if you want to keep it.
* If no expense matches, no file is written (an earlier export is kept unchanged).
* Exporting does not change your data, and TripJio never reads `export.csv` back. Editing it does not affect your expenses.

Each row is one expense, after a header row:

| Column | Meaning |
|---|---|
| `Index` | The expense's number in `list`, so you can use it with `edit` and `delete`. |
| `Date` | The date as `YYYY-MM-DD` (e.g. `2026-10-15`), which spreadsheets read as a date in every region. |
| `Description`, `Category` | As shown in `list`. |
| `Amount (SGD)` | **Your own spending** in SGD: your share for split expenses, the converted amount for foreign-currency expenses. Summing this column gives the same total as `list`. |
| `Type` | `Personal`, `Split` or `Foreign`. |
| `Full bill (SGD)`, `People` | Split expenses only: the whole bill in SGD and the number of people. |
| `Original amount` | Foreign-currency expenses only: the amount in the original currency. |
| `Currency`, `Rate` | Foreign-currency expenses and foreign-currency splits: the currency code and the SGD rate used. |

Columns that do not apply to an expense are left empty. Amounts have two decimal places and no `$` or thousands separator, so spreadsheets read them as numbers.

Examples:

* `export` exports all expenses.
* `export c/food m/10-2026` exports food expenses in October 2026.
* `export trip/` exports the expenses of the selected trip.

Expected output for `export` (with the four expenses from the `list` example):

```
Exported 4 expenses to data/export.csv.
```

Contents of `data/export.csv`:

```
Index,Date,Description,Category,Amount (SGD),Type,Full bill (SGD),People,Original amount,Currency,Rate
1,2026-10-15,Lunch,Food,12.50,Personal,,,,,
2,2026-10-02,Metro card,Transport,45.00,Personal,,,,,
3,2026-10-14,Group dinner,Food,30.00,Split,90.00,3,,,
4,2026-10-10,Ramen,Food,17.40,Foreign,,,12.00,EUR,1.45
```

A description that contains a comma or a double quote is wrapped in double quotes in the file (e.g. `"Ramen, large"`), so it stays in one column.

If no expense matches the filters (or you have no expenses):

```
No expenses match the given filter. Nothing was exported.
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
  * `S` lines store a split's full SGD bill and own share, followed by payer, names, currency, rate and a custom-share flag; see [Saved-data compatibility](#saved-data-compatibility).
  * `F` lines are foreign-currency expenses: `F|DESCRIPTION|ORIGINAL_AMOUNT|CATEGORY|DATE|CURRENCY|RATE`.
* `[owe]` contains `person|signedAmount` payments: positive means money you received, negative means money you paid. Payments are not expenses.
* Amounts are written without a dollar sign or thousands separators, e.g. `1234.50`.
* Dates use `DD-MM-YYYY` and months use `MM-YYYY`, the same as in commands.
* Categories are written in capital letters, e.g. `FOOD`.
* The file is saved in UTF-8, so descriptions such as `Phở` or `Café` are kept correctly.

> **Caution:** if a line in the file is in an invalid format, TripJio skips that line when starting and shows a warning such as `Error: Skipped invalid line 4 in data file: ...`. The other lines are still loaded. Make a backup of the file before editing it.

<!-- @@author teetoozs -->
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
<!-- @@author -->
Split records keep the legacy first seven fields and add metadata:

```text
S|description|sgdBill|people|sgdShare|category|date|payer|participants|currency|rate|hasCustomShare
S|Dinner|100.00|3|33.33|FOOD|14-10-2026||alice,bob|||false
```

Empty optional metadata fields are retained. Names are comma-separated. The final
true/false flag preserves a custom share even if it equals an equal share.
Legacy seven-field records still load; their custom status is inferred from the
saved amounts because the old file cannot express that distinction. Bills/shares
in split records are already SGD, so rates are not applied again on load.
Settlements are saved separately in `[owe]` with signed two-decimal amounts.
<!-- @@author teetoozs -->
Named trips and selection are stored under `[trips]`; manual rates under `[rates]`.
Keep a backup before using this format with an older application release: older
releases do not understand the new records.
<!-- @@author -->

---

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install TripJio on the other computer, then copy your `data/tripjio.txt` into the `data` folder next to `tripjio.jar` there, replacing the file that TripJio creates.

**Q**: Can I record expenses in other currencies (e.g. EUR, JPY)?<br>
**A**: Yes. Set a rate first with `rate set from/EUR to/SGD r/1.45`, then add the expense with `cur/EUR`. Rates are entered by you, not fetched online, and all reports are in SGD. See [Exchange rates](#exchange-rates).

**Q**: Does selecting a trip filter my expenses automatically?<br>
**A**: No. `list`, `split list` and `stats` show only the selected trip's expenses when you add `trip/`. Budgets are always monthly.

**Q**: Does a repayment change my budget, and what if I delete its bill?<br>
**A**: A repayment changes only debt balances, not spending. Deleting a split keeps its payments and can reverse who owes whom. See [Recording repayments](#recording-repayments-settle).

**Q**: My friends also want to use TripJio. Can we share data?<br>
**A**: TripJio is a personal, single-user app. Each person keeps their own data. Use `split` to record your share of a group bill.

**Q**: Can I create my own categories?<br>
**A**: Not at the moment. Use `other` for expenses that do not fit the six built-in categories.

**Q**: How do I open my expenses in Excel or Google Sheets?<br>
**A**: Run `export`, then open `data/export.csv` in Excel or Numbers, or import it in Google Sheets with **File → Import**. See [Exporting expenses](#exporting-expenses-export).

**Q**: Can I manually edit `data/tripjio.txt`?<br>
**A**: Yes, but back up the file first. Supported blocks include `[expense]`, `[budget]`, `[trips]`, `[rates]` and `[owe]`. See [Editing the data file](#editing-the-data-file). Invalid supported records are skipped with warnings; a later save omits them.

---

## Known Issues

* There is no settlement edit/delete or undo command. Payments remain when a split is edited/deleted; the resulting net balance can reverse direction.
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
| **Record repayment** | `settle p/NAME [a/AMOUNT]`<br>e.g. `settle p/Alice a/10` |
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
| **Export to CSV** | `export [c/CATEGORY] [m/MONTH] [trip/]`<br>e.g. `export c/food m/10-2026`, `export trip/` |
| **Exit** | `exit` |
