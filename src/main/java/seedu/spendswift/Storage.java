//@@author glenda-1506
package seedu.spendswift;

import seedu.spendswift.command.Budget;
import seedu.spendswift.command.Category;
import seedu.spendswift.command.Expense;
import seedu.spendswift.command.TrackerData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.IOException;
import java.util.Map;

public class Storage {
    private final String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public void saveData(TrackerData trackerData) throws IOException {
        java.io.StringWriter buffer = new java.io.StringWriter();
        try (BufferedWriter writer = new BufferedWriter(buffer)) {
            writer.write("Budgets\n");
            for (Map.Entry<Category, Budget> entry : trackerData.getBudgets().entrySet()) {
                String categoryName = entry.getKey().getName();
                double budgetLimit = entry.getValue().getLimit();
                String currency = entry.getValue().getHomeCurrency();
                writer.write(categoryName + ", " + budgetLimit
                        + (currency.isEmpty() ? "" : ", " + currency) + "\n");
            }

            writer.write("Expenses\n");
            for (Expense expense : trackerData.getExpenses()) {
                String expenseName = expense.getName();
                double amount = expense.getAmount();
                String categoryName = expense.getCategory().getName();
                String originalCurrency = expense.getoriginalCurrency();
                String gethomeCurrency = expense.gethomeCurrency();
                double getConvertedAmount = expense.getConvertedAmount();
                String currencyFields = originalCurrency.isEmpty() && gethomeCurrency.isEmpty() ? ""
                        : ", " + originalCurrency + ", " + gethomeCurrency + ", " + getConvertedAmount;
                writer.write(expenseName + ", " + amount + ", " + categoryName + currencyFields + "\n");
            }
        }
        java.nio.file.Path target = java.nio.file.Path.of(filePath).toAbsolutePath();
        java.nio.file.Path temporary = java.nio.file.Files.createTempFile(target.getParent(), "spendswift-", ".tmp");
        try {
            java.nio.file.Files.writeString(temporary, buffer.toString());
            // Validate the complete record before replacing any existing saved data.
            new Storage(temporary.toString()).loadData(new TrackerData(), false);
            try {
                java.nio.file.Files.move(temporary, target, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(temporary, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            java.nio.file.Files.deleteIfExists(temporary);
        }
    }

    private Category loadCategory(TrackerData trackerData, String categoryName) {
        for (Category category : trackerData.getCategories()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        for (Category category : trackerData.getBudgets().keySet()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }

        Category newCategory = new Category(categoryName);
        trackerData.getCategories().add(newCategory);
        return newCategory;
    }

    public void loadData(TrackerData trackerData) throws IOException {
        loadData(trackerData, true);
    }

    private void loadData(TrackerData trackerData, boolean announce) throws IOException {
        UI ui = new UI();
        File file = new File(filePath);
        if (!file.exists()) {
            ui.printFileNotFound();
            return;
        }

        TrackerData destination = trackerData;
        trackerData = new TrackerData();
        try (BufferedReader reader = java.nio.file.Files.newBufferedReader(
                file.toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
            String line;
            boolean isBudgetSection = true;

            while ((line = reader.readLine()) != null) {
                if (line.equals("Budgets")) {
                    isBudgetSection = true;
                    continue;
                } else if (line.equals("Expenses")) {
                    isBudgetSection = false;
                    continue;
                }

                String[] parts = line.split(", ");
                if (isBudgetSection ? (parts.length != 2 && parts.length != 3)
                        : (parts.length != 3 && parts.length != 6)) {
                    throw new IOException("Unsupported record in the SpendSwift file format.");
                }
                if (isBudgetSection) {
                    String categoryName = parts[0];
                    double limit = Double.parseDouble(parts[1]);
                    Category category = loadCategory(trackerData, categoryName);
                    Budget budget = new Budget(category, limit, parts.length == 3 ? parts[2] : "", null);
                    budget.attachTracker(trackerData);
                    trackerData.getBudgets().put(category, budget);
                } else {
                    String expenseName = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String categoryName = parts[2];
                    Category category = loadCategory(trackerData, categoryName);
                    Expense expense = parts.length == 3
                            ? new Expense(expenseName, amount, category)
                            : new Expense(expenseName, amount, category, parts[3], parts[4],
                                    Double.parseDouble(parts[5]));
                    Budget budget = trackerData.getBudgets().get(category);
                    if (budget != null && !budget.getHomeCurrency().equals(expense.gethomeCurrency())) {
                        throw new IOException("Saved expense and budget currencies do not match.");
                    }
                    for (Expense other : trackerData.getExpenses()) {
                        if (other.getCategory().equals(category)
                                && !other.gethomeCurrency().equals(expense.gethomeCurrency())) {
                            throw new IOException("Saved category contains inconsistent home currencies.");
                        }
                    }
                    trackerData.getExpenses().add(expense);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid amount or currency in the SpendSwift file.", e);
        }
        destination.setCategories(trackerData.getCategories());
        destination.setExpenses(trackerData.getExpenses());
        destination.setBudgets(trackerData.getBudgets());
        for (Budget budget : destination.getBudgets().values()) {
            budget.attachTracker(destination);
        }
        if (announce) {
            ui.printDataLoaded();
        }
    }
}
