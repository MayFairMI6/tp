package seedu.spendswift.command;

import seedu.spendswift.Format;
//import seedu.spendswift.CurrencyConverter;

//@@author kq2003
/**
 * Represents an expense with a name, amount, and category.
 */
public class Expense {


    private String name;
    private double amount;
    private Category category;
    //@@author MayFairMI6
    private String originalCurrency; //Amount in original currency before conversion
    private String homeCurrency;
    private double convertedAmount; // amount in home currency
    //@@author kq2003
    /**
     * Constructs an Expense object with the specified name, amount, and category.
     *
     * @param name The name of the expense item.
     * @param name The amount of the expense.
     * @param category The category associated with the expense.
     */
    // Compatibility with the original single-currency coursework API.
    public Expense(String name, double amount, Category category) {
        this(name, amount, category, "", "", amount);
    }

    public Expense(String name, double amount, Category category, String originalCurrency,
                   String homeCurrency, double convertedAmount) {
        if (!Double.isFinite(amount) || amount < 0 || !Double.isFinite(convertedAmount) || convertedAmount < 0) {
            throw new IllegalArgumentException("Expense amounts must be finite and non-negative.");
        }
        if (originalCurrency.isEmpty() != homeCurrency.isEmpty()) {
            throw new IllegalArgumentException("Provide both original and home currency.");
        }
        if (!originalCurrency.isEmpty()) {
            originalCurrency = seedu.spendswift.CurrencyConverter.currencyCode(originalCurrency);
            homeCurrency = seedu.spendswift.CurrencyConverter.currencyCode(homeCurrency);
        }
        this.name = name;
        this.amount = amount;
        this.category = category;
        //@@author MayFairMI6
        this.originalCurrency = originalCurrency;
        this.homeCurrency = homeCurrency;
        this.convertedAmount= convertedAmount;
    }


    /**
     * Returns the name of the expense.
     *
     * @return The name of the expense.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the amount of the expense.
     *
     * @Return the amount of the expense.
     */
    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category != null ? category : null;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getoriginalCurrency(){
        return originalCurrency;
    }
    public String gethomeCurrency(){
        return homeCurrency;
    }
    public double getConvertedAmount(){
        return convertedAmount;
    }


    /**
     * Returns a string representation of the expense, including name, formatted amount, and category.
     *
     * @return a string describing the expense.
     */
    @Override
    public String toString() {
        if (originalCurrency.isEmpty() && homeCurrency.isEmpty()) {
            return " Item: " + name + ", Amount: " + Format.formatAmount(amount)
                + ", Category: " + getCategory();
        }
        return "Item: " + name +
            ",  Amount: " + Format.formatAmount(amount, originalCurrency) +
            ", Converted Amount: " + Format.formatAmount(convertedAmount, homeCurrency) +
            ", Category: " + getCategory();
    }

}
