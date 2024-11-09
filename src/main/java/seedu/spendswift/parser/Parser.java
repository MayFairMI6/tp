//@@author glenda-1506
package seedu.spendswift.parser;

import seedu.spendswift.command.BudgetManager;
import seedu.spendswift.command.CategoryManager;
import seedu.spendswift.command.ExpenseManager;
import seedu.spendswift.command.TrackerData;
import seedu.spendswift.UI;

public class Parser {
    private final ExpenseManager expenseManager;
    private final CategoryManager categoryManager;
    private final BudgetManager budgetManager;
    private final UI ui;

    public Parser(ExpenseManager expenseManager, CategoryManager categoryManager, BudgetManager budgetManager, UI ui) {
        this.expenseManager = expenseManager;
        this.categoryManager = categoryManager;
        this.budgetManager = budgetManager;
        this.ui = ui;
    }

    public boolean parseCommand(String input, TrackerData trackerData) {
        input = input.trim();

        if (input.startsWith("add-expense")) {
            expenseManager.addExpenseRequest(input, expenseManager, trackerData);
        } else if (input.startsWith("add-category")) {
            categoryManager.addCategory(trackerData, input);
        } else if (input.startsWith("delete-expense")) {
            expenseManager.deleteExpenseRequest(input, expenseManager, trackerData);
        } else if (input.startsWith("tag-expense")) {
            expenseManager.tagExpense(trackerData, input);
        } else if (input.startsWith("view-budget")) {
            budgetManager.viewBudget(trackerData);
        } else if (input.startsWith("set-budget")) {
            budgetManager.setBudgetLimitRequest(input, budgetManager, trackerData);
        } else if (input.startsWith("view-expenses")) {
            expenseManager.viewExpensesByCategory(trackerData);
        } else if (input.startsWith("toggle-reset")) {
            budgetManager.toggleAutoReset();
        } else if (input.startsWith("help")) {
            ui.printHelpMessage();
        } else if (input.startsWith("bye")) {
            ui.printExitMessage();
            return true;
        } else {
            ui.printParserInvalidInput();
        }

        return false;
    }

    private String parseAmount(String input) {
    String[] parts = input.split("\\s+");
    for (String part : parts) {
        try {
            Double.parseDouble(part);
            return part; // Return the first numeric value which represents the amount
        } catch (NumberFormatException e) {
            // Continue if the part isn't numeric, ensuring only the amount is parsed
        }
    }
    return ""; // Return empty if no numeric amount is found
    }
    
    private String parseCurrency(String input) {
    String[] parts = input.split("\\s+");
    for (String part : parts) {
        // Check if the part matches exactly three letters, which is typical for currency codes
        if (part.matches("[a-zA-Z]{3}")) {
            return part.toUpperCase(); // Return the currency code in uppercase to standardize
        }
    }
    return ""; // Return empty if no currency code is found
}

}
