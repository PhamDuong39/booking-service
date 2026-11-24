package com.cts.booking.shared.domain.error;

public class DomainValidationException extends DomainException {
    public DomainValidationException(String message) {
        this("VALIDATION_FAILED", message);
    }

    public DomainValidationException(String code, String message) {
        super(code, message);
    }
}
