//@@author glenda-1506
package seedu.spendswift.parser;

public class InputParser {
    private String parseComponent(String input, String prefix) {
        int startIndex = input.indexOf(prefix);
        if (startIndex == -1) {
            return "";
        }

        startIndex += prefix.length();
        int endIndex = input.length();
        String[] prefixes = {"n/", "a/", "c/", "e/", "l/","cur/"};

        for (String otherPrefix : prefixes) {
            if (!otherPrefix.equals(prefix)) {
                int prefixIndex = input.indexOf(otherPrefix, startIndex);
                if (prefixIndex != -1 && prefixIndex < endIndex) {
                    endIndex = prefixIndex;
                }
            }
        }

        return input.substring(startIndex, endIndex).trim();
    }

    public int parseIndex(String input) {
        String indexStr = parseComponent(input, "e/");
        try {
            return Integer.parseInt(indexStr) - 1; // Convert to 0-based index
        } catch (NumberFormatException e) {
            System.out.println("Invalid expense index format. Please enter a valid number after 'e/'.");
            return -1;
        }
    }

    public double parseLimit(String input) {
        String limitStr = parseComponent(input, "l/");
        try {
            return Double.parseDouble(limitStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid limit format. Please enter a valid number after 'l/'.");
            return -1;
        }
    }

    public double parseAmount(String input) {
        String amountStr = parseComponent(input, "a/");
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid number after 'a/'.");
            return -1;
        }
    }

    public String parseName(String input) {
        return parseComponent(input, "n/");
    }

    public String parseCategory(String input) {
        return parseComponent(input, "c/");
    }
    //@@author MayFairMI6
    /**
     * Parses the currency from the given input string if it meets the format requirements.
     * @param input the command input string.
     * @return the normalized currency code if valid.
     * @throws IllegalArgumentException if the currency code does not meet the required format.
     */
    public String parseCurrency(String input) throws IllegalArgumentException {
        // First, check if there's a potential currency format in the input
        if (!input.matches(".*cur/[a-zA-Z]{3}.*")) {
            throw new IllegalArgumentException("Error: A 3-letter currency code is needed after 'cur/'.");
        }

        // Extract the currency using parseComponent from the Parser class
        String currency = Parser.parseComponent(input, "cur/");
        if (currency.isEmpty() || !currency.matches("[a-zA-Z]{3}")) {
            throw new IllegalArgumentException("Error: A valid 3-letter currency code is needed after 'cur/'.");
        }

        return currency.toUpperCase();  // Return the currency code in uppercase
    }

    
}

}
