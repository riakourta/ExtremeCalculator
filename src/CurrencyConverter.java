import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverter {

    public double convert(double amount, String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return amount;
        }

        try {
            String urlString = String.format("https://api.frankfurter.app/latest?amount=%s&from=%s&to=%s", amount, from, to);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Εντοπισμός του νομίσματος-στόχου μέσα στο επιστρεφόμενο JSON
            String searchKey = "\"" + to + "\":";
            int startIndex = responseBody.indexOf(searchKey) + searchKey.length();
            int endIndex = responseBody.indexOf("}", startIndex);

            String resultString = responseBody.substring(startIndex, endIndex);
            return Double.parseDouble(resultString);

        } catch (Exception e) {
            // Επιστροφή 0.0 σε περίπτωση αποτυχίας δικτύου (πιάσιμο από το Task 5)
            return 0.0;
        }
    }
}
