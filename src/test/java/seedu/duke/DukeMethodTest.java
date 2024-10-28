//@@author {AdiMangalam}
package seedu.duke;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DukeMethodTest {

    @Test
    void addExpenseRequestValidInput() {
        ExpenseTracker tracker = new ExpenseTracker();

        String input = "add-expense n/Lunch a/15.50 c/Food";
        Duke.addExpenseRequest(input, tracker);

        assertEquals(1, tracker.getCategories().size());
        assertEquals("Food", tracker.getCategories().get(0).getName());
        assertEquals(1,tracker.getExpenses().size());
    }

    @Test
    void addExpenseMissingCategory() {
        ExpenseTracker tracker = new ExpenseTracker();

        String input = "add-expense n/Lunch a/15.50 ";
        Duke.addExpenseRequest(input, tracker);

        assertEquals(0, tracker.getCategories().size());
        assertEquals(0, tracker.getExpenses().size());
    }

    @Test
    void addExpenseMissingAmount() {
        ExpenseTracker tracker = new ExpenseTracker();

        String input = "add-expense n/lunch a/ c/Food ";
        Duke.addExpenseRequest(input, tracker);

        assertEquals(0, tracker.getExpenses().size());
    }

    @Test
    void setBudgetLimitRequestValidInput() {
        ExpenseTracker tracker = new ExpenseTracker();

        tracker.addCategory("add-category Food");
        String input = "set-budget c/Food l/100.0";
        Duke.setBudgetLimitRequest(input, tracker);

        Category foodCategory = tracker.getCategories().get(0);
        Budget foodBudget = tracker.getBudgets().get(foodCategory);
        assertEquals(100, foodBudget.getLimit(), 0.01, "Budget should be 100.");
    }

    @Test
    void setBudgetLimitRequestInValidInput() {
        ExpenseTracker tracker = new ExpenseTracker();

        tracker.addCategory("add-category Food");
        String input1 = "set-budget c/Food l/100.0";
        Duke.setBudgetLimitRequest(input1, tracker);
        String input2 = "set-budget n/Food l/150.0";
        Duke.setBudgetLimitRequest(input2, tracker);

        Category foodCategory = tracker.getCategories().get(0);
        Budget foodBudget = tracker.getBudgets().get(foodCategory);
        assertEquals(100, foodBudget.getLimit(), 0.01, "Budget should still be 100 and not updated to 150.");
    }

    @Test
    public void deleteExpenseValidInput() {
        ExpenseTracker tracker = new ExpenseTracker();
        String input1 = "add-expense n/Lunch a/15.50 c/Food";
        String input2 = "add-expense n/Dinner a/20 c/Food";
        Duke.addExpenseRequest(input1, tracker);
        Duke.addExpenseRequest(input2, tracker);

        String delete1 = "delete-expense e/2";
        Duke.deleteExpenseRequest(delete1, tracker);

        assertEquals(1, tracker.getExpenses().size());
        assertEquals("Lunch", tracker.getExpenses().get(0).getName());

        String delete2 = "delete-expense e/1";
        Duke.deleteExpenseRequest(delete2, tracker);

        assertEquals(0, tracker.getExpenses().size());
    }

    @Test
    public void deleteExpenseInValidInput() {
        ExpenseTracker tracker = new ExpenseTracker();
        String input1 = "add-expense n/Lunch a/15.50 c/Food";
        String input2 = "add-expense n/Dinner a/20 c/Food";
        Duke.addExpenseRequest(input1, tracker);
        Duke.addExpenseRequest(input2, tracker);

        String delete1 = "delete-expense 1";
        Duke.deleteExpenseRequest(delete1, tracker);

        assertEquals(2, tracker.getExpenses().size());
        assertEquals("Lunch", tracker.getExpenses().get(0).getName());
        assertEquals("Dinner", tracker.getExpenses().get(1).getName());
    }


}


