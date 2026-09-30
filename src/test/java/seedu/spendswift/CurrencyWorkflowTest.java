package seedu.spendswift;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.spendswift.command.Budget;
import seedu.spendswift.command.BudgetManager;
import seedu.spendswift.command.Category;
import seedu.spendswift.command.CategoryManager;
import seedu.spendswift.command.Expense;
import seedu.spendswift.command.ExpenseManager;
import seedu.spendswift.command.TrackerData;
import seedu.spendswift.parser.Parser;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CurrencyWorkflowTest {
    @TempDir Path directory;

    private Parser parser(CurrencyConverter converter) {
        return new Parser(new ExpenseManager(converter), new CategoryManager(), new BudgetManager(), new UI());
    }

    @Test void crossCurrencyBudgetUsesConvertedAmountAfterReload() throws IOException {
        TrackerData data = new TrackerData();
        Parser parser = parser(new CurrencyConverter((from, to) -> {
            assertEquals("USD", from);
            assertEquals("EUR", to);
            return .8;
        }));
        parser.parseCommand("add-category Food", data);
        parser.parseCommand("set-budget c/Food l/100 hcur/EUR", data);
        parser.parseCommand("add-expense n/Lunch a/50 c/Food ocur/USD hcur/EUR", data);
        Expense expense = data.getExpenses().get(0);
        assertEquals(40, expense.getConvertedAmount());
        assertEquals(60, data.getBudgets().get(expense.getCategory()).getRemainingLimit());
        Storage storage = new Storage(directory.resolve("spendswift.txt").toString());
        storage.saveData(data);
        TrackerData restored = new TrackerData();
        storage.loadData(restored);
        Budget budget = restored.getBudgets().values().iterator().next();
        assertEquals(60, budget.getRemainingLimit());
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            new BudgetManager().viewBudget(restored);
        } finally {
            System.setOut(original);
        }
        assertTrue(output.toString().contains("40.00 EUR spent, 60.00 EUR remaining"));
    }

    @Test void sameCurrencyAndUnlabelledExpenseUseNoNetwork() throws IOException {
        CurrencyConverter converter = new CurrencyConverter((from, to) -> {
            fail("Network is unnecessary");
            return 0;
        });
        assertEquals(12.34, converter.convert(12.34, "eur", "EUR"));
        TrackerData data = new TrackerData();
        Parser parser = parser(converter);
        parser.parseCommand("add-category Food", data);
        parser.parseCommand("set-budget c/Food l/100 hcur/EUR", data);
        parser.parseCommand("add-expense n/Lunch a/10 c/Food", data);
        assertEquals("EUR", data.getExpenses().get(0).gethomeCurrency());
        assertEquals(90, data.getBudgets().values().iterator().next().getRemainingLimit());
    }

    @Test void conversionRoundsToTargetCurrencyPrecision() throws IOException {
        CurrencyConverter converter = new CurrencyConverter((from, to) -> 1.23456);
        assertEquals(12, converter.convert(10, "USD", "JPY"));
        assertEquals(12.346, converter.convert(10, "USD", "KWD"));
    }

    @Test void rejectsInvalidAmountsCurrenciesAndRates() {
        CurrencyConverter converter = new CurrencyConverter((from, to) -> Double.NaN);
        assertThrows(IOException.class, () -> converter.convert(10, "USD", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert(Double.POSITIVE_INFINITY, "USD", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert(-1, "USD", "EUR"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert(10, "BAD", "EUR"));
        assertThrows(IOException.class, () -> new CurrencyConverter((f,t) -> 0).convert(10, "USD", "EUR"));
    }

    @Test void providerFailureDoesNotAddExpense() {
        TrackerData data = new TrackerData();
        parser(new CurrencyConverter((f,t) -> {
            throw new IOException("Unavailable");
        }))
                .parseCommand("add-expense n/Lunch a/10 c/Food ocur/USD hcur/EUR", data);
        assertTrue(data.getExpenses().isEmpty());
        assertTrue(data.getCategories().isEmpty());
    }

    @Test void budgetCurrencyCannotChangeUnderRecordedExpenses() {
        TrackerData data = new TrackerData();
        Parser parser = parser(new CurrencyConverter((f,t) -> 1));
        parser.parseCommand("add-category Food", data);
        parser.parseCommand("set-budget c/Food l/100 hcur/EUR", data);
        parser.parseCommand("add-expense n/Lunch a/10 c/Food ocur/USD hcur/EUR", data);
        parser.parseCommand("set-budget c/Food l/200 hcur/USD", data);
        Budget budget = data.getBudgets().values().iterator().next();
        assertEquals("EUR", budget.getHomeCurrency());
        assertEquals(100, budget.getLimit());
    }

    @Test void mixedHomeCurrencyAndRetaggingAreRejected() {
        TrackerData data = new TrackerData();
        Parser parser = parser(new CurrencyConverter((f,t) -> 1));
        parser.parseCommand("add-category Food", data);
        parser.parseCommand("set-budget c/Food l/100 hcur/EUR", data);
        parser.parseCommand("add-expense n/Lunch a/10 c/Food ocur/USD hcur/USD", data);
        assertTrue(data.getExpenses().isEmpty());
        parser.parseCommand("add-expense n/Lunch a/10 c/Travel ocur/USD hcur/USD", data);
        parser.parseCommand("tag-expense e/1 c/Food", data);
        assertEquals("Travel", data.getExpenses().get(0).getCategory().getName());
    }

    @Test void failedSavePreservesExistingFile() throws IOException {
        Path file = directory.resolve("spendswift.txt");
        String original = "Budgets\nExpenses\n";
        Files.writeString(file, original);
        TrackerData data = new TrackerData();
        data.getExpenses().add(new Expense("Lunch, tea", 10, new Category("Food")));
        assertThrows(IOException.class, () -> new Storage(file.toString()).saveData(data));
        assertEquals(original, Files.readString(file));
    }
}
