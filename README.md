# SpendSwift — team expense-tracking project

SpendSwift is a Java command-line budgeting application developed as a CS2113 team project. It uses text commands to record expenses, manage categories and budget limits, and persist application state.

## My contribution

My [project portfolio page](docs/team/ppp-2.md) records work on the Budget class and BudgetManager, category-grouped expense viewing, budget input limits, tests, and developer-guide diagrams. The application is team work built on the course project template; the other application components and inherited template are not presented as my sole work.

I also worked on a currency-conversion extension and related input handling. The portfolio page records that this extension was not integrated into the final team submission. Converter source exists in this fork, but that is not evidence of a completed, tested multicurrency release.

## Structure and methods

The Java source under `src/main/java/seedu/spendswift/` separates command/input handling, expense and budget management, formatting, and persistence. JUnit tests are under `src/test/`. Gradle configures Java 17, application execution, test dependencies, and JAR packaging.

See the [user guide](docs/README.md), [developer guide](docs/DeveloperGuide.md), and [individual contribution record](docs/team/ppp-2.md) for commands, design diagrams, and the historical team-project scope. The original template setup text is preserved [here](docs/LEGACY_TEMPLATE_SETUP.md).

## Build and run

With JDK 17 installed, from the repository root:

```sh
bash gradlew test
bash gradlew run
```

On September 30, 2026, the main Java source compiled, but `gradlew test` failed while compiling tests (29 errors, including constructor/API mismatches). The test suite therefore did not run. This fork needs test/source alignment before it should be treated as a reproducible release. There is no operational-performance or user-study claim.

## Currency extension and credentials

The optional converter reads `EXCHANGERATES_API_KEY` from the environment and reports a missing-configuration error before making a request. Keep credentials outside the repository. The converter still needs integration and API-behavior testing before use; setting a key alone does not complete that work.
