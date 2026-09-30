package seedu.spendswift.command;

import seedu.spendswift.Format;
import seedu.spendswift.CurrencyConverter;




//@@author MayFairMI6
public class Budget {
    private String homeCurrency;
    private Category category; // Private to prevent unauthorized access or changes
    private double limit; // Private to control modifications to the budget
    private TrackerData trackerData;
    private CurrencyConverter currencyConverter;
    public Budget(Category category, double limit) {
        this(category, limit, "", null);
    }

    public Budget(Category category, double limit,String homeCurrency, CurrencyConverter currencyConverter) {
        this.category = category;
        setLimit(limit);
        this.homeCurrency = homeCurrency.isEmpty() ? "" : CurrencyConverter.currencyCode(homeCurrency);
        this.currencyConverter = currencyConverter;
    }

    public void attachTracker(TrackerData trackerData) {
        this.trackerData = trackerData;
    }

    public String getHomeCurrency() {
        return homeCurrency;
    }

    public Category getCategory() {
        return category;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        if (!Double.isFinite(limit) || limit < 0) {
            throw new IllegalArgumentException("Budget limit cannot be negative.");
        }
        this.limit = limit;
    }

    public double getRemainingLimit() {
        if (trackerData == null) {
            throw new IllegalStateException("Budget must be attached to tracker data "
                    + "before computing remaining funds.");
        }
        double totalExpenses = trackerData.getExpenses().stream()
            .filter(e -> e.getCategory().equals(category))
            .mapToDouble(Expense::getConvertedAmount)
            .sum();
        return limit - totalExpenses;
    }

    @Override
 public String toString() {
        return "Budget for category '" + category + "' is " + Format.formatAmount(limit, homeCurrency);
    }
}
