//@@author glenda-1506
package seedu.spendswift;

import seedu.spendswift.command.BudgetManager;
import seedu.spendswift.command.CategoryManager;
import seedu.spendswift.command.ExpenseManager;
import seedu.spendswift.command.TrackerData;
import seedu.spendswift.parser.Parser;

import java.io.IOException;
import java.util.Scanner;

public class SpendSwift {
    public static void main(String[] args) {
        String filePath = "spendswift.txt";
        Storage storage = new Storage(filePath);
        TrackerData trackerData = new TrackerData();
        UI ui = new UI();

        try {
            storage.loadData(trackerData);
        } catch (IOException e) {
            ui.printLoadingError(e.getMessage());
        }

        CategoryManager categoryManager = new CategoryManager();
        BudgetManager budgetManager = new BudgetManager();
        ExpenseManager expenseManager = new ExpenseManager();
        Parser parser = new Parser(expenseManager, categoryManager, budgetManager, ui);

        Scanner in = new Scanner(System.in);
        ui.printWelcomeMessage();
        boolean isExit = false;

        while (!isExit && in.hasNextLine()) {
            String input = in.nextLine();
            isExit = parser.parseCommand(input, trackerData);
        }

        try {
            storage.saveData(trackerData);
            ui.printDataSaved();
        } catch (IOException e) {
            ui.printSavingError(e.getMessage());
        }
    }
}
