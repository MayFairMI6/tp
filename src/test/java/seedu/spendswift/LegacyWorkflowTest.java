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
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegacyWorkflowTest {
    @TempDir Path directory;

    @Test
    void originalCommandsSurviveSaveAndReloadWithoutCredentials() throws IOException {
        TrackerData data = new TrackerData();
        Parser parser = new Parser(new ExpenseManager(), new CategoryManager(), new BudgetManager(), new UI());
        parser.parseCommand("add-category Food", data);
        parser.parseCommand("set-budget c/Food l/100", data);
        parser.parseCommand("add-expense n/Lunch a/12.50 c/Food", data);
        assertEquals(1, data.getExpenses().size());
        assertEquals(1, data.getBudgets().size());
        Storage storage = new Storage(directory.resolve("spendswift.txt").toString());
        storage.saveData(data);
        TrackerData restored = new TrackerData();
        storage.loadData(restored);
        assertEquals(12.50, restored.getExpenses().get(0).getAmount());
        assertEquals(100, restored.getBudgets().get(restored.getExpenses().get(0).getCategory()).getLimit());
        assertEquals(1, restored.getCategories().size());
    }

    @Test
    void currencyMetadataRoundTripsWithoutFetchingRates() throws IOException {
        TrackerData data = new TrackerData();
        Category category = new Category("Food");
        data.getCategories().add(category);
        data.getBudgets().put(category, new Budget(category, 100, "EUR", null));
        data.getExpenses().add(new Expense("Lunch", 12, category, "USD", "EUR", 11));
        Storage storage = new Storage(directory.resolve("spendswift.txt").toString());
        storage.saveData(data);
        TrackerData restored = new TrackerData();
        storage.loadData(restored);
        Expense expense = restored.getExpenses().get(0);
        assertEquals("USD", expense.getoriginalCurrency());
        assertEquals("EUR", expense.gethomeCurrency());
        assertEquals(11, expense.getConvertedAmount());
        assertEquals("EUR", restored.getBudgets().get(expense.getCategory()).getHomeCurrency());
    }

    @Test
    void malformedFileIsReportedWithoutChangingItsContents() throws IOException {
        Path file = directory.resolve("spendswift.txt");
        String original = "Budgets\nFood, not-a-number\nExpenses\n";
        Files.writeString(file, original);
        assertThrows(IOException.class, () -> new Storage(file.toString()).loadData(new TrackerData()));
        assertEquals(original, Files.readString(file));
    }
}
