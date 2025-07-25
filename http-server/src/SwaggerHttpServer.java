import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SwaggerHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve Swagger UI
        server.createContext("/swagger-ui", new SwaggerUIHandler());
        
        // Serve the Swagger/OpenAPI specification
        server.createContext("/swagger.json", new SwaggerJsonHandler());
        
        // Example API endpoint
        server.createContext("/hello", new HelloHandler());

        server.setExecutor(null); // Use default executor
        server.start();
        
        System.out.println("Server started at http://localhost:8080");
    }

    // Handler to serve Swagger UI static files
    static class SwaggerUIHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String filePath = "static/swagger-ui" + path.substring("/swagger-ui".length());
            if (Files.exists(Paths.get(filePath))) {
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                exchange.sendResponseHeaders(200, fileContent.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } else {
                String response = "File not found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    // Swagger JSON handler (serves the OpenAPI specification)
    static class SwaggerJsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Example OpenAPI spec in JSON format (swagger.json)
            String openApiSpec = """
                {
                  "openapi": "3.0.1",
                  "info": {
                    "title": "My API",
                    "version": "1.0.0"
                  },
                  "paths": {
                    "/hello": {
                      "get": {
                        "summary": "Returns a greeting",
                        "responses": {
                          "200": {
                            "description": "A greeting message"
                          }
                        }
                      }
                    }
                  }
                }
            """;
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, openApiSpec.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(openApiSpec.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    // Example handler for your API endpoint
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, World!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
