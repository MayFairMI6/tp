//@@author glenda-1506
package seedu.spendswift;
import java.util.Calendar;
import org.junit.jupiter.api.Test;
import seedu.spendswift.command.CategoryManager;
import seedu.spendswift.command.ExpenseManager;
import seedu.spendswift.command.TrackerData;
import seedu.spendswift.command.Budget;
import seedu.spendswift.command.Category;
import seedu.spendswift.command.Expense;
import seedu.spendswift.command.BudgetManager;
//import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;


class ExpenseTest {
    @Test
    public void expenseConstructor() {
        Category category = new Category("Food");
        Expense expense = new Expense("Lunch", 12.50, category);
        assertEquals("Lunch", expense.getName());
        assertEquals(12.50, expense.getAmount());
        assertEquals(category, expense.getCategory());
    }

    @Test
    public void inputWithCategory() {
        Category category = new Category("Entertainment");
        Expense expense = new Expense("Movie", 12.5, category);
        assertEquals(" Item: Movie, Amount: $12.50, Category: Entertainment", expense.toString());
    }

    @Test
    public void inputWithoutCategory() {
        Expense expense = new Expense("Movie", 12.5, null);
        assertEquals(" Item: Movie, Amount: $12.50, Category: null", expense.toString());
    }

    @Test
    public void setCategory() {
        Category category = new Category("Entertainment");
        Expense expense = new Expense("Movie", 12.5, null);
        expense.setCategory(category);
        assertEquals("Entertainment", expense.getCategory().getName());
    }
}

class CategoryTest {
    @Test
    public void categoryConstructor() {
        Category category = new Category("Entertainment");
        assertEquals("Entertainment", category.getName());
    }

    @Test
    public void categoryName() {
        Category category = new Category("Groceries");
        assertEquals("Groceries", category.toString());
    }

    @Test
    public void getName() {
        Category category = new Category("Transport");
        assertNotNull(category.getName());
        assertEquals("Transport", category.getName());
    }
}

class BudgetTest {
    @Test
    public void budgetConstructor() {
        Category category = new Category("Food");
        Budget budget = new Budget(category, 100);
        assertEquals(100, budget.getLimit());
        assertEquals(category, budget.getCategory());
    }

    @Test
    public void setPositiveLimit() {
        Category category = new Category("Transport");
        Budget budget = new Budget(category, 50);
        budget.setLimit(75);
        assertEquals(75, budget.getLimit());
    }

    @Test
    public void setNegativeLimit() {
        Category category = new Category("Utilities");
        Budget budget = new Budget(category, 100);
        assertThrows(IllegalArgumentException.class, () -> budget.setLimit(-50));
    }

    @Test
    public void budgetOutput() {
        Category category = new Category("Entertainment");
        Budget budget = new Budget(category, 200);
        assertEquals("Budget for category 'Entertainment' is $200", budget.toString());
    }
}

//@@author AdiMangalam
class CategoryManagerTest {
    @Test
    void addCategoryNewCategory() {
        CategoryManager categoryManager = new CategoryManager();
        TrackerData trackerData = new TrackerData();

        categoryManager.addCategory(trackerData, "add-category Food");

        assertEquals(1, trackerData.getCategories().size());
        assertEquals("Food", trackerData.getCategories().get(0).getName());
    }

    @Test
    void addCategoryExistingCategory() {
        CategoryManager categoryManager = new CategoryManager();
        TrackerData trackerData = new TrackerData();

        trackerData.getCategories().add(new Category("Food"));

        categoryManager.addCategory(trackerData, "add-category Food");

        assertEquals(1, trackerData.getCategories().size());
    }

    @Test
    void addCategory_doesNotAddIfNameIsEmpty() {
        CategoryManager categoryManager = new CategoryManager();
        TrackerData trackerData = new TrackerData();

        categoryManager.addCategory(trackerData, "add-category  ");

        assertTrue(trackerData.getCategories().isEmpty());
    }

    //@@glenda-1506
    @Test
    public void add2NonDuplicateCategory() {
        CategoryManager categoryManager = new CategoryManager();
        TrackerData trackerData = new TrackerData();
        categoryManager.addCategory(trackerData, "add category Food");
        assertEquals(1, trackerData.getCategories().size(), "Should have 1 category");
        categoryManager.addCategory(trackerData, "add category Drinks");
        assertEquals(2, trackerData.getCategories().size(), "Should have 2 categories");
    }
}

//@@author AdiMangalam
class ExpenseManagerTest {
    @Test
    public void addExpenseNewCategory() {
        ExpenseManager expenseManager = new ExpenseManager();
        TrackerData trackerData = new TrackerData();

        expenseManager.addExpense(trackerData, "Coffee", 2.0, "Food");

        assertEquals(1, trackerData.getExpenses().size());
        assertEquals("Coffee", trackerData.getExpenses().get(0).getName());
        assertEquals(2.00, trackerData.getExpenses().get(0).getAmount());
        assertEquals("Food", trackerData.getExpenses().get(0).getCategory().getName());

        assertEquals(1, trackerData.getCategories().size());
        assertEquals("Food", trackerData.getCategories().get(0).getName());
    }

    @Test
    void addExpenseExistingCategory() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        Category existingCategory = new Category("Food");
        trackerData.getCategories().add(existingCategory);

        expenseManager.addExpense(trackerData, "Dinner", 20.00, "Food");

        assertEquals(1, trackerData.getCategories().size());
        assertEquals("Food", trackerData.getExpenses().get(0).getCategory().getName());
    }

    @Test
    void deleteExpenseValidIndex() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        expenseManager.addExpense(trackerData, "Coffee", 3.5, "Food");
        expenseManager.addExpense(trackerData, "Bus", 1.2, "Transport");

        assertEquals(2, trackerData.getCategories().size());

        expenseManager.deleteExpense(trackerData, 0);
        assertEquals(1, trackerData.getExpenses().size());
        assertEquals("Bus", trackerData.getExpenses().get(0).getName());
        assertEquals(1.2, trackerData.getExpenses().get(0).getAmount());
        assertEquals("Transport", trackerData.getExpenses().get(0).getCategory().getName());

        expenseManager.deleteExpense(trackerData, 0);
        assertTrue(trackerData.getExpenses().isEmpty());
    }

    @Test
    void deleteExpenseInvalidIndex() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        expenseManager.addExpense(trackerData, "Chocolates", 2.5, "Snacks");

        expenseManager.deleteExpense(trackerData, 1);

        assertEquals(1, trackerData.getExpenses().size());
        assertEquals("Chocolates", trackerData.getExpenses().get(0).getName());
    }

    @Test
    void tagExpenseValidInput() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        expenseManager.addExpense(trackerData, "Train", 4, "Transport");

        Category existingCategory = new Category("Travel");
        trackerData.getCategories().add(existingCategory);

        expenseManager.tagExpense(trackerData, "e/1 c/Travel");

        assertEquals("Travel", trackerData.getExpenses().get(0).getCategory().getName());
        assertEquals(2, trackerData.getCategories().size());
    }

    @Test
    void tagExpenseInvalidIndex() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        Category category = new Category("Food");
        trackerData.getCategories().add(category);

        expenseManager.addExpense(trackerData, "Waffle", 11.5, "Food");
        expenseManager.tagExpense(trackerData, "e/2 c/Snack");

        assertEquals("Food", trackerData.getExpenses().get(0).getCategory().getName());
    }

    @Test
    void tagExpenseInvalidCategory() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();

        Expense expense = new Expense("Bus Fare", 2.50, new Category("Transport"));
        trackerData.getExpenses().add(expense);

        expenseManager.addExpense(trackerData, "Bus", 1.25, "Transport");
        expenseManager.tagExpense(trackerData, "e/1 c/Travel");

        assertEquals("Transport", trackerData.getExpenses().get(0).getCategory().getName());
    }

    //@@author glenda-1506
    @Test
    void tagExpenseInvalidNegativeIndex() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addExpense(trackerData, "Lunch", 10.00, "Food");
        expenseManager.tagExpense(trackerData, "e/-1 c/Transport");
        assertEquals("Food", trackerData.getExpenses().get(0).getCategory().getName());
    }

    @Test
    void tagInvalidFormatExpense() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addExpense(trackerData, "Train", 4, "Transport");
        expenseManager.tagExpense(trackerData, "e/x c/");
    }

    @Test
    void viewNoExpenses() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.viewExpensesByCategory(trackerData);
    }

    @Test
    void viewMultipleExpensesOneCategory() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addExpense(trackerData, "Coffee", 5.0, "Food");
        expenseManager.addExpense(trackerData, "Sandwich", 3.0, "Food");
        expenseManager.viewExpensesByCategory(trackerData);
    }

    @Test
    void viewExpensesMultipleCategory() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addExpense(trackerData, "Train", 4.5, "Transport");
        expenseManager.addExpense(trackerData, "Pizza", 12.0, "Food");
        expenseManager.viewExpensesByCategory(trackerData);
    }

    @Test
    void deleteExpenseEmptyList() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.deleteExpense(trackerData, 0);
        assertTrue(trackerData.getExpenses().isEmpty());
    }

    @Test
    void deleteExpenseIndexOutOfBounds() {
        TrackerData trackerData = new TrackerData();
        ExpenseManager expenseManager = new ExpenseManager();
        expenseManager.addExpense(trackerData, "Dinner", 20.00, "Food");
        expenseManager.deleteExpense(trackerData, -1);
        assertEquals(1, trackerData.getExpenses().size());
    }
}

class BudgetManagerTest {

    
    BudgetManager budgetManager = new BudgetManager();
    TrackerData trackerData = new TrackerData();
  

    private Category findCategory(TrackerData trackerData, String categoryName) {
        for (Category category : trackerData.getCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null; // Return null if no category matches the given name
    }


    /* @Test
    void testSetBudgetValidCategory() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Utilities";
        double limit = 500.0;
        CategoryManager.addCategory(trackerData, categoryName);
        budgetManager.setBudgetLimit(trackerData, categoryName, limit);
        Category category = findCategory(trackerData, categoryName);
        assertNotNull(category);
        assertTrue(trackerData.getBudgets().containsKey(category));
        assertEquals(limit, trackerData.getBudgets().get(category).getLimit());
    } 

    @Test
    void testSetBudgetForNonExistingCategory() {
    BudgetManager budgetManager = new BudgetManager();
    TrackerData trackerData = new TrackerData();
    Random random = new Random();

    // Generate a unique random category name
    String uniqueCategoryName;
    do {
        uniqueCategoryName = generateRandomWord(random, 10); // Generate a random word of length 10
    } while (findCategory(trackerData, uniqueCategoryName) != null); // Ensure the name is not already in use

    double limit = 300.0;

    // Confirm that the unique category does not exist initially
    Category category = findCategory(trackerData, uniqueCategoryName);
    assertNull(category, "Category should initially be null, indicating it doesn't exist.");

    // Set the budget for the non-existing unique category
    budgetManager.setBudgetLimit(trackerData, uniqueCategoryName, limit);

    // Verify that the category now exists
    category = findCategory(trackerData, uniqueCategoryName);
    assertNotNull(category, "Category should not be null after setting budget limit.");
    
    // Verify the budget limit was set correctly
    assertTrue(trackerData.getBudgets().containsKey(category), "Budget should contain the new category.");
    assertEquals(limit, trackerData.getBudgets().get(category).getLimit(), "Budget limit should match the set limit.");
}

// Method to generate a random word of specified length
private String generateRandomWord(Random random, int wordLength) {
    return random.ints('a', 'z' + 1)
                 .limit(wordLength)
                 .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                 .toString();
} */

    /*@Test
    void testSetBudgetWithInvalidLimit() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Groceries";
        double invalidLimit = -100.0;
        CategoryManager.addCategory(trackerData, categoryName);
        budgetManager.setBudgetLimit(trackerData, categoryName, invalidLimit);
        Category category = findCategory(trackerData, categoryName);
        assertNotNull(category);
        assertEquals(invalidLimit, trackerData.getBudgets().get(category).getLimit());
    } */

    @Test
    void testAddExpenseWithinBudget() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Food";
        double budgetLimit = 200.0;
        double expenseAmount = 150.0;
        CategoryManager.addCategory(trackerData, categoryName);
        budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
        ExpenseManager.addExpense(trackerData, "Lunch", expenseAmount, categoryName);
        Category category = findCategory(trackerData, categoryName);
        assertNotNull(category);
        assertEquals(expenseAmount, trackerData.getExpenses().stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum());
    }

    @Test
    void testAddExpenseExceedingBudget() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Travel";
        double budgetLimit = 300.0;
        double expenseAmount = 350.0;
        CategoryManager.addCategory(trackerData, categoryName);
        budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
        ExpenseManager.addExpense(trackerData, "Train Ticket", expenseAmount, categoryName);
        Category category = findCategory(trackerData, categoryName);
        assertNotNull(category);
        assertTrue(expenseAmount > budgetLimit);
        assertEquals(expenseAmount, trackerData.getExpenses().stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum());
    }

    @Test
    void testToggleAutoReset() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        budgetManager.toggleAutoReset();
        assertTrue(budgetManager.getAutoResetStatus());
        budgetManager.toggleAutoReset();
        assertFalse(budgetManager.getAutoResetStatus());
    }

    @Test
    void testSimulateMonthChangeWithAutoReset() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        budgetManager.toggleAutoReset();
        budgetManager.simulateMonthChange();
        budgetManager.checkAndResetBudgets(trackerData);
        assertEquals(Calendar.getInstance().get(Calendar.MONTH), budgetManager.getLastResetMonth());
    }

    @Test
    void testSimulateMonthChangeWithoutAutoReset() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        if (budgetManager.getAutoResetStatus()) {
            budgetManager.toggleAutoReset();
        }
        budgetManager.simulateMonthChange();
        budgetManager.checkAndResetBudgets(trackerData);
        assertNotEquals(Calendar.getInstance().get(Calendar.MONTH), budgetManager.getLastResetMonth());
    }

    /*    @Test
    void testViewBudgetsWithBudgetsSet() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Category_ViewBudgetSet";
        double budgetLimit = 500.0;
        double expenseAmount = 250.0;
        CategoryManager.addCategory(trackerData, categoryName);
        budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
        ExpenseManager.addExpense(trackerData, "Expense", expenseAmount, categoryName);
        Category category = findCategory(trackerData, categoryName);
        Budget budget = trackerData.getBudgets().get(category);
        assertNotNull(budget);
        double totalSpent = trackerData.getExpenses().stream()
                .filter(e -> e.getCategory().equals(category))
                .mapToDouble(Expense::getAmount)
                .sum();
        assertEquals(expenseAmount, totalSpent);
        assertEquals(budgetLimit - expenseAmount, budget.getRemainingLimit());
    }*/

    @Test
    void testViewBudgetsWithNoBudgetsSet() {
        BudgetManager budgetManager = new BudgetManager();
        TrackerData trackerData = new TrackerData();
        String categoryName = "Category_NoBudget";
        ExpenseManager.addExpense(trackerData, "RandomExpense", 100.0, categoryName);
        Category category = findCategory(trackerData, categoryName);
        assertFalse(trackerData.getBudgets().containsKey(category));
    }
}


