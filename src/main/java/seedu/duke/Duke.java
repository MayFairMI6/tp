package seedu.duke;

import java.util.Scanner;

public class Duke {
    public static void main(String[] args) {
        ExpenseTracker expenseTracker = new ExpenseTracker();
        Scanner in = new Scanner(System.in);
        System.out.println("Hiya! How can I assist?");
        while (true) {
            if (in.hasNextLine()) {
                String input = in.nextLine().trim();
                if (input.startWith("add-expense")) {
                    addExpenseRequest(input, expenseTracker);
                } else if (input.startsWith("add category")) {
                    expenseTracker.addCategory(input);
                } else if (input.equalsIgnoreCase("bye")) {
                    System.out.println("Goodbye! (｡•‿•｡) Hope to see you again soon!");
                    break;
                } else {
                    System.out.println("Invalid input! Try again."); // Provide feedback for invalid input
                }
            } else {
                System.out.println("No input received.");
                break;
            }
        }
    }


    public static void addExpenseRequest(String input, ExpenseTracker expenseTracker) {
        try {
            String[] parts = inputs.split(" ");

            String name = null;
            double amount = 0;
            String category = null;


            for (String part: parts) {
                if (parts.startsWith("n/")) {
                    name = part.substring(2).trim();
                } else if (parts.startsWith("a/")) {
                    amount = Double.parseDouble(part.substring(2).trim());
                } else if (parts.startsWith("c/")) {
                    category = part.substring(2).trim();
                }
            }

            if (name == null || amount == 0) {
                System.out.println("Invalid input! Please provide name and amount.")
            }


        } catch (Exception e) {
            System.out.println("Error parsing the input. Please use correct format for add-expense commands.")
        }
    }
}