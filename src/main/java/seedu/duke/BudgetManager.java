package seedu.duke;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BudgetManager {
    private int lastResetMonth;
    private boolean isAutoResetEnabled;

    public BudgetManager() {
        this.lastResetMonth = -1;
        this.isAutoResetEnabled = false;
    }

    public void toggleAutoReset() {
        isAutoResetEnabled = !isAutoResetEnabled;
        System.out.println("Automatic budget reset is now " + (isAutoResetEnabled ? "ON" : "OFF") + ".");
    }

    //@@author AdiMangalam
    /**
     * Checks if it is a new month and resets budgets if auto-reset is enabled.
     *
     * This method uses the current month to determine if a monthly budget reset
     * should occur. If auto-reset is enabled and the month has changed since the
     * last reset, it triggers a reset of all budgets and updates the last reset month.
     */
    public void checkAndResetBudgets(TrackerData trackerData) {

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        if (isAutoResetEnabled && currentMonth != lastResetMonth) {
            resetBudgets(trackerData);
            lastResetMonth = currentMonth;
        }
    }

    /**
     * Manages the monthly budget reset process.
     *
     * This method is intended to be called periodically to ensure budgets are reset
     * at the start of a new month if necessary. It delegates the actual reset logic
     * to the checkAndResetBudgets method, which handles auto-reset checks.
     */
    public void manageMonthlyReset(TrackerData trackerData) {
        checkAndResetBudgets(trackerData);
    }

    /**
     * Resets the budget limits for all categories.
     *
     * This method iterates over all budgets in the tracker and resets each budget's
     * limit as per the current configuration. By default, it maintains the same limit
     * for each budget, but the reset logic can be adjusted if needed.
     */
    private void resetBudgets(TrackerData trackerData) {
        Map<Category, Budget> budgets = trackerData.getBudgets();

        for (Budget budget : budgets.values()) {
            // Resetting the budget logic can be adjusted as needed
            budget.setLimit(budget.getLimit()); // For now, just maintaining the same limit
        }

        trackerData.setBudgets(budgets);
        System.out.println("Budgets have been reset for all categories.");
    }

    //@@author MayFairMI6
    /**
     * Sets a budget limit for a specific category.
     *
     * If the category already has a budget, this method updates the budget limit.
     * If the category does not have a budget set, it creates a new budget for the category.
     *
     * This method is used to track and control spending limits for different categories.
     * After setting the budget, a message is displayed to confirm the action.
     *
     * @param categoryName The name of the category to set the budget for
     * @param limit The budget limit to be set for the category (in dollars)
     */
    public void setBudgetLimit(TrackerData trackerData, String categoryName, double limit) {
        List<Category> categories = trackerData.getCategories();
        Map<Category, Budget> budgets = trackerData.getBudgets();
        String formattedCategoryName = formatInput(categoryName.trim());

        Category existingCategory = null;
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(formattedCategoryName)) {
                existingCategory = category;
                break;
            }
        }

        if (existingCategory == null) {
            System.out.println("Category '" + formattedCategoryName + "' not found. Please add the category first.");
            return;
        }

        if (budgets.containsKey(existingCategory)) {
            budgets.get(existingCategory).setLimit(limit);
            System.out.println("Updated budget for category '" + existingCategory + "' to " + formatDecimal(limit));
        } else {
            Budget newBudget = new Budget(existingCategory, limit);
            budgets.put(existingCategory, newBudget);
            System.out.println("Set budget for category '" + existingCategory + "' to " + formatDecimal(limit));
        }

        trackerData.setBudgets(budgets);
    }

    //@@author kq2003
    public void setBudgetLimitRequest(String input, BudgetManager budgetManager, TrackerData trackerData) {
        try {
            String[] parts = input.split(" ");
            double limit = 0;
            String category = null;

            for (String part : parts) {
                if (part.startsWith("c/")) {
                    category = part.substring(2).trim();
                } else if (part.startsWith("l/")) {
                    limit = Double.parseDouble(part.substring(2).trim());
                }
            }

            if (category == null || limit == 0) {
                System.out.println("Invalid input! Please provide category name and limit.");
                return;
            }

            budgetManager.setBudgetLimit(trackerData, category, limit);
        } catch (Exception e) {
            System.out.println("Error parsing the input. Please use the correct format for set-budget commands.");
        }
    }

    /**
     * Displays the current budget status for each category.
     *
     * This method checks if each category has a budget set, calculates the total expenses for that category,
     * and shows the remaining budget. If the total expenses exceed the budget limit, it displays the amount
     * over budget. Categories with expenses but no budget set are also displayed.
     * If no budgets are set, a message is shown indicating the absence of budgets.
     */
    public void viewBudget(TrackerData trackerData) {
        List<Expense> expenses = trackerData.getExpenses();
        Map<Category, Budget> budgets = trackerData.getBudgets();

        if (budgets.isEmpty()) {
            System.out.println("No budgets set for any category.");
            return;
        }

        // mapping total expenses for a category to each category
        Map<Category, Double> totalExpensesToCategory = new HashMap<>();
        for (Expense expense: expenses) {
            Category category = expense.getCategory();
            if (totalExpensesToCategory.containsKey(category)) {
                totalExpensesToCategory.put(category, totalExpensesToCategory.get(category) + expense.getAmount());
            } else {
                totalExpensesToCategory.put(category, expense.getAmount());
            }
        }

        // Calculate remaining budget, and display as needed
        for (Category category: budgets.keySet()) {
            Budget budget = budgets.get(category);
            double totalExpense = totalExpensesToCategory.getOrDefault(category, 0.0);
            double remainingBudget = budget.getLimit() - totalExpense;

            if (remainingBudget >= 0) {
                System.out.println(category + ": " + formatDecimal(totalExpense) + " spent, " +
                        formatDecimal(remainingBudget) + " remaining");
            } else {
                Double positive = Math.abs(remainingBudget);
                System.out.println(category + ": " + formatDecimal(totalExpense) + " spent, " +
                        "Over budget by " + formatDecimal(positive));
            }
        }

        // if no budget set for certain category
        for (Category category: totalExpensesToCategory.keySet()) {
            if (!budgets.containsKey(category)) {
                System.out.println(category + ": No budget set");
            }
        }
    }

    //@@glenda-1506
    private String formatDecimal(double value) {
        BigDecimal roundedValue = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
        DecimalFormat decimalFormat = new DecimalFormat("$#.00");
        return decimalFormat.format(roundedValue);
    }

    private String formatInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
