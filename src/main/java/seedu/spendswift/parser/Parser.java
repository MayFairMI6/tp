//@@author glenda-1506
package seedu.spendswift.parser;

import seedu.spendswift.Storage;
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
    private Storage storage;

    public Parser(ExpenseManager expenseManager, CategoryManager categoryManager, BudgetManager budgetManager, UI ui,
                  Storage storage) {
        this.expenseManager = expenseManager;
        this.categoryManager = categoryManager;
        this.budgetManager = budgetManager;
        this.ui = ui;
        this.storage = storage;
    }

    public boolean parseCommand(String input, TrackerData trackerData) {
        String filePath = "spendswift.txt";
        storage = new Storage(filePath);
        input = input.trim().toLowerCase();

        if (input.startsWith("add-expense")) {
            expenseManager.addExpenseRequest(input, expenseManager, trackerData);
            storage.savedData(trackerData, storage, ui);
        } else if (input.startsWith("add-category")) {
            CategoryManager.addCategory(trackerData, input);
            storage.savedData(trackerData, storage, ui);
        } else if (input.startsWith("delete-expense")) {
            expenseManager.deleteExpenseRequest(input, expenseManager, trackerData);
            storage.savedData(trackerData, storage, ui);
        } else if (input.startsWith("tag-expense")) {
            expenseManager.tagExpense(trackerData, input);
            storage.savedData(trackerData, storage, ui);
        } else if (input.startsWith("view-budget")) {
            budgetManager.viewBudget(trackerData);
        } else if (input.startsWith("view-category")) {
            CategoryManager.viewAllCategories(trackerData);
        } else if (input.startsWith("set-budget")) {
            budgetManager.setBudgetLimitRequest(input, budgetManager, trackerData);
            storage.savedData(trackerData, storage, ui);
        } else if (input.startsWith("view-expenses")) {
            expenseManager.viewExpensesByCategory(trackerData);
        } else if (input.startsWith("toggle-reset")) {
            budgetManager.toggleAutoReset();
            storage.savedData(trackerData, storage, ui);
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
}
