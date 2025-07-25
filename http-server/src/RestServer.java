import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class RestServer {

    public static void main(String[] args) throws IOException {
        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Create a context for the /hello endpoint
        server.createContext("/hello", new HelloHandler());
        
        // Set a default executor (optional, but recommended for production use)
        server.setExecutor(null);
        
        // Start the server
        server.start();
        
        System.out.println("Server is running on http://localhost:8080");
    }

    // Handler class for the /hello endpoint
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the HTTP request method (GET, POST, etc.)
            String method = exchange.getRequestMethod();
            
            // Handle POST method
            if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
            }
            // Handle GET method (optional)
            else if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
            } else {
                // Return Method Not Allowed (405) for unsupported methods
                String response = "Method Not Allowed";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        // Handle GET requests (optional)
        private void handleGet(HttpExchange exchange) throws IOException {
            String response = "Hello, World! This is a GET request!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        // Handle POST requests
        private void handlePost(HttpExchange exchange) throws IOException {
            // Read the POST request body (data sent by the client)
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            
            // For example, we assume the body contains a simple text message
            String response = "Received POST data: " + requestBody;
            System.out.println(response);
            
            // Set response headers (HTTP 200 OK)
            exchange.sendResponseHeaders(200, response.getBytes().length);
            
            // Write the response to the output stream
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
