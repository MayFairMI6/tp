package seedu.spendswift.command;

//import seedu.spendswift.Format;
//import seedu.spendswift.parser.InputParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class CurrencyConverter {

    private static final String API_KEY = "04a6ee8bec44c7f011340564e098b97e"; // Replace with your actual API key
    private static final String BASE_URL = "https://api.exchangeratesapi.io/latest";

    private Map<String, Double> exchangeRates;

    public CurrencyConverter(String baseCurrency) throws IOException {
        fetchExchangeRates(baseCurrency);
    }

    private void fetchExchangeRates(String baseCurrency) throws IOException {
        String urlString = BASE_URL + "?access_key=" + API_KEY + "&base=" + baseCurrency;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
        exchangeRates = new Gson().fromJson(jsonObject.get("rates"), Map.class);
    }

 public double convert(double amount, String fromCurrency, String toCurrency) {
        if (amount<=0) {
            throw new IllegalArgumentException("Amount has to be greater than zero.");
        }
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        double rate = exchangeRates.get(toCurrency) / exchangeRates.get(fromCurrency);
        return amount * rate;
    }
}
