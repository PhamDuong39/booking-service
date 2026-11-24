package com.cts.booking.shared.infrastructure.web.error;

import java.util.Map;

public record ApiError(String code, String message, boolean retryable, Map<String, Object> details) {
    public ApiError {
        details = details == null ? Map.of() : Map.copyOf(details);
    }

    public record FieldViolation(String field, String code, String message) {
    }
}
