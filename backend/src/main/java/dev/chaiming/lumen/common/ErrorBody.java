package dev.chaiming.lumen.common;

import java.time.Instant;
import java.util.Map;

public record ErrorBody(
        String code,
        String message,
        Instant timestamp,
        Map<String, String> fields
) {
    public static ErrorBody of(String code, String message) {
        return new ErrorBody(code, message, Instant.now(), Map.of());
    }

    public static ErrorBody of(String code, String message, Map<String, String> fields) {
        return new ErrorBody(code, message, Instant.now(), fields == null ? Map.of() : fields);
    }
}
