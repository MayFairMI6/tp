package seedu.duke;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;


class BudgetManagerTest {

    private BudgetManager budgetManager;
    private TrackerData trackerData;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        budgetManager = new BudgetManager();
        trackerData = new TrackerData();

        // Redirect System.out to capture outputs for verification
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    /**
     * Provides one test case per equivalence class with randomized inputs.
     */
    static Stream<Arguments> provideTestCases() {
        List<Arguments> testCases = new ArrayList<>();
        Random random = new Random();

        // EC1: Add a new category successfully.
        testCases.add(Arguments.of("EC1_AddNewCategory", (Executable) () -> {
            String categoryName = "Category_" + UUID.randomUUID().toString().substring(0, 8);
            trackerData.addCategory(new Category(categoryName));
            System.out.println("Added category '" + categoryName + "'.");
            // Assert category is added
            assertTrue(trackerData.getCategories().stream()
                            .anyMatch(c -> c.getName().equalsIgnoreCase(categoryName)),
                    "Category should be added successfully.");
        }));

        // EC2: Attempt to add a duplicate category.
        testCases.add(Arguments.of("EC2_AddDuplicateCategory", (Executable) () -> {
            String categoryName = "Category_Duplicate";
            trackerData.addCategory(new Category(categoryName));
            System.out.println("Added category '" + categoryName + "'.");
            // Attempt to add the same category again
            trackerData.addCategory(new Category(categoryName));
            System.out.println("Attempted to add duplicate category '" + categoryName + "'.");
            // Depending on implementation, duplicates might be allowed or ignored
            // Here, we assume duplicates are allowed; adjust assertion as per actual behavior
            long count = trackerData.getCategories().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .count();
            assertEquals(2, count, "Duplicate category should be added if allowed.");
        }));

        // EC3: Set budget for an existing category with a valid limit.
        testCases.add(Arguments.of("EC3_SetBudgetValid", (Executable) () -> {
            String categoryName = "Category_ValidBudget";
            trackerData.addCategory(new Category(categoryName));
            double limit = 100 + (1000 - 100) * random.nextDouble(); // $100 to $1000
            budgetManager.setBudgetLimit(trackerData, categoryName, limit);
            // Assert budget is set
            Category category = findCategory(trackerData, categoryName);
            assertNotNull(category, "Category should exist.");
            assertTrue(trackerData.getBudgets().containsKey(category),
                    "Budget should be set for the existing category.");
            assertEquals(limit, trackerData.getBudgets().get(category).getLimit(),
                    0.001, "Budget limit should be set correctly.");
        }));

        // EC4: Attempt to set budget for a non-existing category.
        testCases.add(Arguments.of("EC4_SetBudgetNonExisting", (Executable) () -> {
            String categoryName = "Category_NonExisting";
            double limit = 100 + (1000 - 100) * random.nextDouble(); // $100 to $1000
            budgetManager.setBudgetLimit(trackerData, categoryName, limit);
            // Assert budget is not set
            Category category = findCategory(trackerData, categoryName);
            assertNull(category, "Category should not exist.");
            assertFalse(trackerData.getBudgets().containsKey(new Category(categoryName)),
                    "Budget should not be set for non-existing category.");
        }));

        // EC5: Set budget with an invalid limit.
        testCases.add(Arguments.of("EC5_SetBudgetInvalidLimit", (Executable) () -> {
            String categoryName = "Category_InvalidLimit";
            trackerData.addCategory(new Category(categoryName));
            double invalidLimit = -50.0; // Negative limit
            budgetManager.setBudgetLimit(trackerData, categoryName, invalidLimit);
            // Depending on implementation, negative limits might be rejected or accepted
            // Here, we check if the limit is set as provided
            Category category = findCategory(trackerData, categoryName);
            assertNotNull(category, "Category should exist.");
            if (trackerData.getBudgets().containsKey(category)) {
                assertEquals(invalidLimit, trackerData.getBudgets().get(category).getLimit(),
                        0.001, "Budget limit should be set even if invalid.");
            } else {
                fail("Budget should be set even with invalid limit.");
            }
        }));

        // EC6: Add expense within the budget.
        testCases.add(Arguments.of("EC6_AddExpenseWithinBudget", (Executable) () -> {
            String categoryName = "Category_ExpenseWithin";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 200 + (800 - 200) * random.nextDouble(); // $200 to $800
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            double expense = 50 + (budgetLimit - 50) * random.nextDouble(); // $50 to (limit - $50)
            Category category = findCategory(trackerData, categoryName);
            assertNotNull(category, "Category should exist.");
            trackerData.addExpense(new Expense(category, expense));
            System.out.println("Added expense of $" + String.format("%.2f", expense) + " to '" + categoryName + "'.");
            // Assert expense is added
            assertTrue(trackerData.getExpenses().stream()
                            .anyMatch(e -> e.getCategory().equals(category) && e.getAmount() == expense),
                    "Expense should be added within the budget.");
        }));

        // EC7: Add expense exceeding the budget.
        testCases.add(Arguments.of("EC7_AddExpenseExceedingBudget", (Executable) () -> {
            String categoryName = "Category_ExpenseExceeding";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 100 + (500 - 100) * random.nextDouble(); // $100 to $500
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            double expense = budgetLimit + (500 - budgetLimit) * random.nextDouble(); // Above limit
            Category category = findCategory(trackerData, categoryName);
            assertNotNull(category, "Category should exist.");
            trackerData.addExpense(new Expense(category, expense));
            System.out.println("Added expense of $" + String.format("%.2f", expense) + " to '" + categoryName + "'.");
            // Assert expense is added
            assertTrue(trackerData.getExpenses().stream()
                            .anyMatch(e -> e.getCategory().equals(category) && e.getAmount() == expense),
                    "Expense should be added exceeding the budget.");
        }));

        // EC8: Attempt to add expense to a non-existing category.
        testCases.add(Arguments.of("EC8_AddExpenseNonExistingCategory", (Executable) () -> {
            String categoryName = "Category_NonExistingExpense";
            double expense = 100 + (300 - 100) * random.nextDouble(); // $100 to $300
            Category category = findCategory(trackerData, categoryName);
            if (category != null) {
                trackerData.addExpense(new Expense(category, expense));
                System.out.println("Added expense of $" + String.format("%.2f", expense) + " to '" + categoryName + "'.");
            } else {
                System.out.println("Category '" + categoryName + "' not found. Cannot add expense.");
            }
            // Assert expense is not added
            assertNull(category, "Category should not exist.");
        }));

        // EC9: Toggle auto-reset functionality (Enable).
        testCases.add(Arguments.of("EC9_ToggleAutoReset_Enable", (Executable) () -> {
            budgetManager.toggleAutoReset(); // Enable
            // Assert auto-reset is enabled
            assertTrue(getAutoResetStatus(budgetManager), "Auto-reset should be enabled.");
        }));

        // EC10: Simulate month change with auto-reset enabled.
        testCases.add(Arguments.of("EC10_SimulateMonthChange_AutoResetEnabled", (Executable) () -> {
            // Enable auto-reset
            budgetManager.toggleAutoReset();
            // Add a category and set budget
            String categoryName = "Category_AutoResetEnabled";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 300 + (700 - 300) * random.nextDouble(); // $300 to $700
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            // Add an expense
            Category category = findCategory(trackerData, categoryName);
            double expense = 100 + (budgetLimit - 100) * random.nextDouble(); // Within budget
            trackerData.addExpense(new Expense(category, expense));
            // Simulate month change
            simulateMonthChange(budgetManager);
            // Perform budget check and reset
            budgetManager.checkAndResetBudgets(trackerData);
            // Assert that budgets are reset (currently, resetBudgets maintains the same limits)
            // Depending on reset logic, adjust assertions accordingly
            assertEquals(Calendar.getInstance().get(Calendar.MONTH),
                    getLastResetMonth(budgetManager),
                    "Last reset month should be updated to current month.");
            System.out.println("Simulated month change with auto-reset enabled.");
        }));

        // EC11: Simulate month change with auto-reset disabled.
        testCases.add(Arguments.of("EC11_SimulateMonthChange_AutoResetDisabled", (Executable) () -> {
            // Ensure auto-reset is disabled
            if (getAutoResetStatus(budgetManager)) {
                budgetManager.toggleAutoReset();
            }
            // Add a category and set budget
            String categoryName = "Category_AutoResetDisabled";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 300 + (8000 - 300) * random.nextDouble(); // $300 to $8000
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            // Add an expense
            Category category = findCategory(trackerData, categoryName);
            double expense = 100 + (budgetLimit - 100) * random.nextDouble(); // Within budget
            trackerData.addExpense(new Expense(category, expense));
            // Simulate month change
            simulateMonthChange(budgetManager);
            // Perform budget check and reset
            budgetManager.checkAndResetBudgets(trackerData);
            // Assert that budgets are NOT reset (lastResetMonth should not be updated)
            assertNotEquals(Calendar.getInstance().get(Calendar.MONTH),
                    getLastResetMonth(budgetManager),
                    "Last reset month should not be updated when auto-reset is disabled.");
            System.out.println("Simulated month change with auto-reset disabled.");
        }));

        // EC12: View budget when budgets are set.
        testCases.add(Arguments.of("EC12_ViewBudget_BudgetsSet", (Executable) () -> {
            String categoryName = "Category_ViewBudgetSet";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 300 + (8000 - 300) * random.nextDouble(); // $300 to $8000
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            double expense = 200 + (budgetLimit - 200) * random.nextDouble(); // Within budget
            Category category = findCategory(trackerData, categoryName);
            trackerData.addExpense(new Expense(category, expense));
            // Capture output
            budgetManager.viewBudget(trackerData);
            String output = outContent.toString();
            // Assert output contains expected budget information
            assertTrue(output.contains(categoryName + ": $" + String.format("%.2f", expense) +
                            " spent, $" + String.format("%.2f", (budgetLimit - expense)) + " remaining"),
                    "View budget should display correct spent and remaining amounts.");
        }));

        // EC13: View budget when no budgets are set.
        testCases.add(Arguments.of("EC13_ViewBudget_NoBudgetsSet", (Executable) () -> {
            // Add some expenses without setting budgets
            String categoryName1 = "Category_NoBudget1";
            String categoryName2 = "Category_NoBudget2";
            trackerData.addCategory(new Category(categoryName1));
            trackerData.addCategory(new Category(categoryName2));
            Category category1 = findCategory(trackerData, categoryName1);
            Category category2 = findCategory(trackerData, categoryName2);
            trackerData.addExpense(new Expense(category1, (6-0.75)*random.nextDouble()));
            trackerData.addExpense(new Expense(category2, (8001-1300)*random.nextDouble()));
            // Capture output
            budgetManager.viewBudget(trackerData);
            String output = outContent.toString();
            // Assert output contains "No budgets set" messages
            assertTrue(output.contains("No budgets set for any category."),
                    "View budget should indicate that no budgets are set.");
            assertTrue(output.contains(categoryName1 + ": No budget set"),
                    "View budget should indicate no budget set for " + categoryName1 + ".");
            assertTrue(output.contains(categoryName2 + ": No budget set"),
                    "View budget should indicate no budget set for " + categoryName2 + ".");
        }));

        // EC14: Add expense with an invalid amount.
        testCases.add(Arguments.of("EC14_AddExpense_InvalidAmount", (Executable) () -> {
            String categoryName = "Category_InvalidExpenseAmount";
            trackerData.addCategory(new Category(categoryName));
            double budgetLimit = 350 + (8999 - 350) * random.nextDouble(); // $350 to $8999
            budgetManager.setBudgetLimit(trackerData, categoryName, budgetLimit);
            double invalidExpense = -100.0; // Negative amount
            Category category = findCategory(trackerData, categoryName);
            trackerData.addExpense(new Expense(category, invalidExpense));
            System.out.println("Added invalid expense of $" + String.format("%.2f", invalidExpense) + " to '" + categoryName + "'.");
            // Assert expense is added (assuming the system allows negative expenses)
            assertTrue(trackerData.getExpenses().stream()
                            .anyMatch(e -> e.getCategory().equals(category) && e.getAmount() == invalidExpense),
                    "Expense with invalid amount should be added if allowed.");
        }));

        return testCases.stream();
    }

    /**
     * Executes each test case corresponding to an equivalence class.
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestCases")
    void testEquivalenceClasses(String testCaseName, Executable testCase) {
        try {
            testCase.execute();
        } catch (Exception e) {
            fail("Test case '" + testCaseName + "' failed with exception: " + e.getMessage());
        }
    }

    /**
     * Helper method to find a category by name (case-insensitive).
     */
    private static Category findCategory(TrackerData trackerData, String categoryName) {
        for (Category category : trackerData.getCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Helper method to simulate a month change by manipulating the lastResetMonth field.
     */
    private static void simulateMonthChange(BudgetManager budgetManager) {
        try {
            Field lastResetMonthField = BudgetManager.class.getDeclaredField("lastResetMonth");
            lastResetMonthField.setAccessible(true);
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            // Set lastResetMonth to previous month to simulate a month change
            int previousMonth = (currentMonth + 11) % 12;
            lastResetMonthField.set(budgetManager, previousMonth);
            System.out.println("Simulated month change. Current month set to " + currentMonth + ", last reset month set to " + previousMonth + ".");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to simulate month change: " + e.getMessage());
        }
    }

    /**
     * Helper method to get the current status of auto-reset.
     */
    private static boolean getAutoResetStatus(BudgetManager budgetManager) {
        try {
            Field autoResetField = BudgetManager.class.getDeclaredField("isAutoResetEnabled");
            autoResetField.setAccessible(true);
            return autoResetField.getBoolean(budgetManager);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to access isAutoResetEnabled field: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to get the last reset month.
     */
    private static int getLastResetMonth(BudgetManager budgetManager) {
        try {
            Field lastResetMonthField = BudgetManager.class.getDeclaredField("lastResetMonth");
            lastResetMonthField.setAccessible(true);
            return lastResetMonthField.getInt(budgetManager);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to access lastResetMonth field: " + e.getMessage());
            return -1;
        }
    }
}
