// src/SimpleRestServer.java

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.codec.UrnCodec;
import com.github.f4b6a3.uuid.util.UuidValidator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class SimpleRestServer {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        server.createContext("/isUuidUrn", new IsUuidUrnHandler());
        server.createContext("/isValid", new IsValidHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server is running on http://localhost:8081");
    }

    static class IsUuidUrnHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> body = mapper.readValue(exchange.getRequestBody(), Map.class);
            boolean result = UrnCodec.isUuidUrn((String) body.get("string"));
            sendJsonResponse(exchange, Map.of("result", result));
        }
    }

    static class IsValidHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> body = mapper.readValue(exchange.getRequestBody(), Map.class);
            boolean result = UuidValidator.isValid((String) body.get("uuid"), (int) body.get("version"));
            sendJsonResponse(exchange, Map.of("result", result));
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, Map<String, Object> responseBody) throws IOException {
        String response = mapper.writeValueAsString(responseBody);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}