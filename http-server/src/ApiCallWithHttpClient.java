import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCallWithHttpClient {

    public static void main(String[] args) {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();
            
            // Create an HttpRequest for a GET method
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                    .build();
            
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Output the response code and body
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
