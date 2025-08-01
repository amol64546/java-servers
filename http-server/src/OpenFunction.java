import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfunction.functions.HttpFunction;
import dev.openfunction.functions.HttpRequest;
import dev.openfunction.functions.HttpResponse;
import dev.openfunction.functions.Routable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.f4b6a3.uuid.codec.UrnCodec;
import com.github.f4b6a3.uuid.util.UuidValidator;

public class OpenFunction extends Routable implements HttpFunction {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Map<String, Function<Map<String, Object>, Object>> pathHandlers = new HashMap<>();

    static {
        pathHandlers.put("/isUuidUrn", OpenFunction::isUuidUrn);
        pathHandlers.put("/isValid", OpenFunction::isValid);
    }

    private static Object isUuidUrn(Map<String, Object> body) {
        return UrnCodec.isUuidUrn((String) body.get("string"));
    }

    private static Object isValid(Map<String, Object> body) {
        return UuidValidator.isValid((String) body.get("uuid"), (int) body.get("version"));
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getReader().lines()
                .collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> body = mapper.readValue(requestBody, new TypeReference<>() {
        });
        Object result = pathHandlers.get(request.getPath())
                .apply(body);
        sendResponse(response, result);
    }

    @Override
    public String getPath() {
        return "/*";
    }

    private static void sendResponse(HttpResponse response, Object result) throws IOException {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }
}
