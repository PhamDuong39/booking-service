package com.cts.booking.shared.infrastructure.web.error;

import com.cts.booking.shared.domain.error.DomainValidationException;
import com.cts.booking.shared.domain.error.NotFoundException;
import com.cts.booking.shared.infrastructure.web.error.ApiError.FieldViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DomainValidationException.class)
    ResponseEntity<Object> handleDomainValidation(DomainValidationException exception) {
        return response(HttpStatus.UNPROCESSABLE_CONTENT, HttpHeaders.EMPTY,
                new ApiError(exception.getCode(), exception.getMessage(), false, Map.of()));
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Object> handleNotFound(NotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, HttpHeaders.EMPTY,
                new ApiError(exception.getCode(), exception.getMessage(), false, Map.of()));
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    void propagateSecurityException(RuntimeException exception) {
        throw exception;
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> handleUnexpected(Exception exception) {
        log.error("Unexpected REST request failure", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY,
                error("INTERNAL_ERROR", "An unexpected error occurred."));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (exception instanceof HttpMessageNotReadableException unreadable) {
            Throwable cause = unreadable.getMostSpecificCause();
            if (cause instanceof UnrecognizedPropertyException unknown) {
                return super.handleExceptionInternal(exception,
                        new ApiError("UNKNOWN_FIELD", "The request contains an unknown field.", false, Map.of(
                                "fields", List.of(new FieldViolation(unknown.getPropertyName(),
                                        "UNKNOWN_FIELD", "Unknown field.")))),
                        jsonHeaders(headers), HttpStatus.UNPROCESSABLE_CONTENT, request);
            }
            if (cause instanceof MismatchedInputException) {
                return super.handleExceptionInternal(exception,
                        error("VALIDATION_FAILED", "The request body does not match the expected format."),
                        jsonHeaders(headers), HttpStatus.UNPROCESSABLE_CONTENT, request);
            }
            return super.handleExceptionInternal(exception,
                    error("MALFORMED_JSON", "The request body must contain valid JSON."),
                    jsonHeaders(headers), HttpStatus.BAD_REQUEST, request);
        }

        if (exception instanceof MethodArgumentNotValidException invalid) {
            List<FieldViolation> fields = invalid.getBindingResult().getFieldErrors().stream()
                    .map(ApiExceptionHandler::fieldViolation)
                    .toList();
            return super.handleExceptionInternal(exception,
                    new ApiError("VALIDATION_FAILED", "Request validation failed.", false, Map.of("fields", fields)),
                    jsonHeaders(headers), HttpStatus.UNPROCESSABLE_CONTENT, request);
        }

        if (exception instanceof TypeMismatchException
                || exception instanceof ServletRequestBindingException && status.value() == 400
                || exception instanceof HandlerMethodValidationException && status.value() == 400) {
            status = HttpStatus.UNPROCESSABLE_CONTENT;
        }
        if (status.is5xxServerError() && status.value() != 503) {
            log.error("REST request failed with status {}", status.value(), exception);
        }
        return super.handleExceptionInternal(exception, errorForStatus(status), jsonHeaders(headers), status, request);
    }

    private static FieldViolation fieldViolation(FieldError error) {
        return new FieldViolation(error.getField(), "INVALID_FIELD", "Invalid value.");
    }

    private static ApiError errorForStatus(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> error("MALFORMED_JSON", "The request body must contain valid JSON.");
            case 401 -> error("AUTH_REQUIRED", "Authentication is required.");
            case 403 -> error("FORBIDDEN", "Access is forbidden.");
            case 404 -> error("RESOURCE_NOT_FOUND", "Resource not found.");
            case 405 -> error("METHOD_NOT_ALLOWED", "The HTTP method is not supported for this resource.");
            case 406 -> error("NOT_ACCEPTABLE", "The requested response format is not available.");
            case 409 -> error("CONFLICT", "The request conflicts with the current resource state.");
            case 413 -> error("PAYLOAD_TOO_LARGE", "The request body exceeds the allowed size.");
            case 415 -> error("UNSUPPORTED_MEDIA_TYPE", "The request media type is not supported.");
            case 422 -> error("VALIDATION_FAILED", "Request validation failed.");
            case 429 -> error("RATE_LIMITED", "Too many requests.");
            case 503 -> error("SERVICE_UNAVAILABLE", "The service is temporarily unavailable.");
            default -> status.is5xxServerError()
                    ? error("INTERNAL_ERROR", "An unexpected error occurred.")
                    : error("REQUEST_FAILED", "The request could not be processed.");
        };
    }

    private static ApiError error(String code, String message) {
        return new ApiError(code, message, false, Map.of());
    }

    private static ResponseEntity<Object> response(HttpStatus status, HttpHeaders headers, ApiError error) {
        return ResponseEntity.status(status).headers(jsonHeaders(headers)).body(error);
    }

    private static HttpHeaders jsonHeaders(HttpHeaders headers) {
        HttpHeaders result = new HttpHeaders();
        result.addAll(headers);
        result.setContentType(MediaType.APPLICATION_JSON);
        return result;
    }
}
