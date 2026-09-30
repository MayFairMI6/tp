# SpendSwift — expenses and budgets across currencies

SpendSwift is a Java command-line budgeting application developed as a CS2113 team project. It records expenses by category, compares spending against category budgets, and saves the results locally. The currency extension lets a traveler record an expense in its original currency while tracking the budget in a chosen home currency.

## My contribution and project history

My [original contribution record](docs/team/ppp-2.md) covers Budget and BudgetManager, category-grouped expense viewing, budget input limits, tests, and design diagrams. SpendSwift is team work built on the course template; the other components retain their contributor attribution.

I also worked on the original currency extension. It was not integrated into the final team submission. **The September 30, 2026 repair in this personal fork** connects conversion to expense entry, uses converted amounts in budget calculations, preserves currency fields through save/load, and restores compatibility with the original tests. This later repair used coding-assistant support and does not change the historical contribution record.

## Design

- `Parser` and `InputParser` dispatch text commands and parse amounts/currency codes.
- `CurrencyConverter` obtains a rate for the requested pair and rounds to the target currency's minor-unit precision. Identical currencies need no network request. Only currency codes are sent to the default provider; expense names and amounts stay local.
- `ExpenseManager` records original amount/currency and converted amount/home currency. A category uses one home currency; inconsistent expenses or retagging requests are rejected.
- `BudgetManager` sums the saved converted amounts. Reopening a file does not revalue earlier expenses or fetch new rates.
- `Storage` reads the original file format and extended currency records. It validates a temporary save before replacing the previous file. An unreadable file stops startup before it can be overwritten.

**Technologies:** Java 17, Gradle, JUnit 5, Gson, HTTP/JSON, and GitHub Actions.

## Build and run

```sh
bash gradlew check shadowJar
java -jar build/libs/spendswift.jar
```

Example:

```text
add-category Food
set-budget c/Food l/100 hcur/EUR
add-expense n/Lunch a/10 c/Food ocur/USD hcur/EUR
view-budget
bye
```

The first cross-currency expense requests a current reference rate. Each subsequent cross-currency expense makes its own request. `spendswift.txt` is saved in the current working directory. Run in an empty directory for a fresh demo, or back up an existing file first.

Commands without currency fields remain supported. In a category with a currency-tagged budget, an expense without currency fields is interpreted in that budget's currency. See the [currency guide](docs/CURRENCY_GUIDE.md) for provider setup and constraints.

## Verification

On September 30, 2026, **46 JUnit tests**, Checkstyle, JAR packaging, and the Unix CLI smoke test passed locally. Tests include conversion and rounding, provider failure, currency mismatch rejection, budget totals, old-format loading, currency persistence, and preserving an existing file after a failed save.

A live Frankfurter USD→EUR conversion was exercised through the packaged application; the same converted amount and remaining budget were verified after restarting. This is a functional smoke test, not a benchmark. The optional credential-backed Exchange Rates API path has not been live-tested with a replacement key.

## Limits

Rates are reference rates, not card settlement amounts including bank fees. Stored converted amounts remain fixed; historical transaction-date lookup and rate timestamps are not implemented. Monetary fields still use the project's original `double` representation, with decimal rounding at conversion. The text format does not escape commas followed by spaces or embedded newlines in names; an incompatible record causes saving to fail without replacing the previous file. The inherited automatic-reset behavior is unchanged and is not a transaction-history feature.

The [historical user guide](docs/UserGuide.md), [developer guide](docs/DeveloperGuide.md), and [team contribution record](docs/team/ppp-2.md) describe the original coursework. Current currency behavior is documented separately so later work is not attributed to the original submission.
