package seedu.spendswift;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Locale;

/** Fetches reference rates; expense amounts and names are never sent to a provider. */
public class CurrencyConverter {
    @FunctionalInterface
    public interface RateProvider {
        double rate(String from, String to) throws IOException;
    }

    private final RateProvider provider;

    public CurrencyConverter() {
        this(CurrencyConverter::fetchRate);
    }

    // Retained for callers of the original extension. Construction performs no network request.
    public CurrencyConverter(String baseCurrency) throws IOException {
        this();
        currencyCode(baseCurrency);
    }

    public CurrencyConverter(RateProvider provider) {
        this.provider = java.util.Objects.requireNonNull(provider);
    }

    public static String currencyCode(String code) {
        if (code == null || !code.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Use a three-letter currency code.");
        }
        String normalized = code.toUpperCase(Locale.ROOT);
        Currency currency = Currency.getInstance(normalized);
        if (currency.getDefaultFractionDigits() < 0) {
            throw new IllegalArgumentException("Unsupported currency code.");
        }
        return normalized;
    }

    public double convert(double amount, String fromCurrency, String toCurrency) throws IOException {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and non-negative.");
        }
        String from = currencyCode(fromCurrency);
        String to = currencyCode(toCurrency);
        double rate = from.equals(to) ? 1 : provider.rate(from, to);
        if (!Double.isFinite(rate) || rate <= 0) {
            throw new IOException("The rate provider returned an invalid exchange rate.");
        }
        double result = BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(rate))
                .setScale(Currency.getInstance(to).getDefaultFractionDigits(), RoundingMode.HALF_UP)
                .doubleValue();
        if (!Double.isFinite(result)) {
            throw new IllegalArgumentException("Converted amount is too large.");
        }
        return result;
    }

    private static double fetchRate(String from, String to) throws IOException {
        String providerName = System.getenv().getOrDefault("SPENDSWIFT_RATE_PROVIDER", "frankfurter");
        if (providerName.equals("frankfurter")) {
            JsonObject response = readJson("https://api.frankfurter.dev/v2/rate/" + from + "/" + to);
            try {
                if (!from.equalsIgnoreCase(response.get("base").getAsString())
                        || !to.equalsIgnoreCase(response.get("quote").getAsString())) {
                    throw new IOException("Rate response does not match the requested currency pair.");
                }
                return response.get("rate").getAsDouble();
            } catch (RuntimeException e) {
                throw new IOException("Malformed exchange-rate response.");
            }
        }
        if (!providerName.equals("exchangeratesapi")) {
            throw new IOException("Set SPENDSWIFT_RATE_PROVIDER to frankfurter or exchangeratesapi.");
        }
        String key = System.getenv("EXCHANGERATES_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IOException("Set EXCHANGERATES_API_KEY for the exchangeratesapi provider.");
        }
        // Cross-convert from the provider's default base, avoiding a paid base-switch requirement.
        JsonObject response = readJson("https://api.exchangeratesapi.io/v1/latest?access_key="
                + URLEncoder.encode(key, StandardCharsets.UTF_8) + "&symbols=" + from + "," + to);
        try {
            if (!response.has("success") || !response.get("success").getAsBoolean()) {
                throw new IOException("Exchange Rates API rejected the request; check credentials and plan access.");
            }
            JsonObject rates = response.getAsJsonObject("rates");
            String base = response.get("base").getAsString();
            double source = from.equals(base) ? 1 : rates.get(from).getAsDouble();
            double target = to.equals(base) ? 1 : rates.get(to).getAsDouble();
            if (!Double.isFinite(source) || source <= 0 || !Double.isFinite(target) || target <= 0) {
                throw new IOException("Invalid exchange rates in provider response.");
            }
            return target / source;
        } catch (RuntimeException e) {
            throw new IOException("Malformed exchange-rate response or unsupported currency.");
        }
    }

    private static JsonObject readJson(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setInstanceFollowRedirects(false);
        try {
            if (connection.getResponseCode() != 200) {
                throw new IOException("Exchange-rate service returned HTTP " + connection.getResponseCode() + ".");
            }
            try (var input = connection.getInputStream()) {
                return JsonParser.parseString(new String(input.readNBytes(262144), StandardCharsets.UTF_8))
                        .getAsJsonObject();
            }
        } catch (IOException | RuntimeException e) {
            // Never include a credential-bearing URL or raw provider body in console output.
            throw new IOException("Unable to obtain exchange rates. "
                    + "Check the network, currency, and provider settings.");
        } finally {
            connection.disconnect();
        }
    }
}
