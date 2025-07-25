import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SimpleRestServer {

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
            String response = "Hello, World!";

            // Set response headers (HTTP 200 OK, Content-Type as text/plain)
            exchange.sendResponseHeaders(200, response.getBytes().length);

            // Write the response to the output stream
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
