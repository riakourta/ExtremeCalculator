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

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // Τυπώνουμε την απάντηση για να βλέπουμε τι στέλνει το site
            System.out.println("Απάντηση API: " + responseBody);

            String searchKey = "\"" + to + "\":";
            int keyIndex = responseBody.indexOf(searchKey);

            // Αν το ποσό ήταν 0.0 ή υπήρξε λάθος, σταματάμε εδώ με ασφάλεια
            if (keyIndex == -1) {
                System.out.println("Προειδοποίηση: Το νόμισμα δεν βρέθηκε στην απάντηση. Μήπως το ποσό είναι 0;");
                return 0.0;
            }

            int startIndex = keyIndex + searchKey.length();
            int endIndex = responseBody.indexOf("}", startIndex);
            int commaIndex = responseBody.indexOf(",", startIndex);

            if (commaIndex != -1 && commaIndex < endIndex) {
                endIndex = commaIndex;
            }

            String resultString = responseBody.substring(startIndex, endIndex);
            return Double.parseDouble(resultString);

        } catch (Exception e) {
            System.out.println("Σφάλμα συστήματος/δικτύου: " + e.getMessage());
            return 0.0;
        }
    }
}