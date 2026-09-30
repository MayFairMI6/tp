# Currency extension

This guide describes the September 30, 2026 implementation in this personal fork. The original course submission and individual contribution record remain historical documents.

## Commands

```text
add-category Travel
set-budget c/Travel l/500 hcur/EUR
add-expense n/Train a/40 c/Travel ocur/USD hcur/EUR
view-expenses
view-budget
bye
```

Supply both `ocur/` (original currency) and `hcur/` (home currency) for conversion. Codes are case-insensitive ISO currencies supported by Java; the rate provider must also support the requested pair. The home currency must match the category's existing expenses and budget. To track a different home currency, use a separate category. Setting a budget needs no network access.

Without currency fields, an expense in a currency-tagged budget category is treated as already denominated in that budget currency. Categories without a currency-tagged budget retain the original unlabelled-dollar display; the app does not infer a currency for historical records.

A failed rate request adds no expense. The application does not substitute a made-up rate. Reopening saved expenses uses their stored converted amounts rather than requesting current rates.

## Providers

The default is [Frankfurter](https://frankfurter.dev/), using its public `/v2/rate/{base}/{quote}` endpoint. It requires no API key. Conversion occurs locally after fetching the rate. Requests have five-second connection/read timeouts, and redirects are rejected.

The original [Exchange Rates API](https://exchangeratesapi.io/documentation/) remains optional:

```sh
export SPENDSWIFT_RATE_PROVIDER=exchangeratesapi
# Set EXCHANGERATES_API_KEY privately in your shell or secret manager.
java -jar build/libs/spendswift.jar
```

That adapter requests rates relative to the provider's default base and calculates the pair's ratio. It does not require changing the provider's base currency. Missing credentials, rejected requests, missing rates, and malformed responses are errors. Credentials and raw provider responses are not printed. To return to the keyless provider, unset `SPENDSWIFT_RATE_PROVIDER` or set it to `frankfurter`.

A credential previously committed to this fork must be revoked or rotated before the optional provider is used. Removing it from current source does not remove it from historical commits.

## Saved records

Original budget and expense records remain readable. Currency-tagged records add these fields:

```text
Budgets
Travel, 500.0, EUR
Expenses
train, 40.0, Travel, USD, EUR, 35.2
```

The `35.2` above is an illustrative saved amount, not a current exchange-rate quote. Original amount, original currency, home currency, and converted amount survive restart. No provider access is needed to load the file. Files with mismatched currencies or malformed records are rejected; startup exits before overwriting them.

## Reproduce the checks

```sh
bash gradlew check shadowJar
bash text-ui-test/runtest.sh
```

On Windows, use `gradlew.bat check shadowJar` and `text-ui-test\runtest.bat`. Automated currency tests inject controlled rates, so they do not need credentials or external API access. The CLI smoke test uses a temporary working directory and does not alter the repository's saved sample data.
