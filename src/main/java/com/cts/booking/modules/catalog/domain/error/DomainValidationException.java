package com.cts.booking.modules.catalog.domain.error;

public class DomainValidationException extends DomainException {
    public DomainValidationException(String message) {
        super(ErrorCode.DOMAIN_VALIDATION_FAILED, message);
    }

    public DomainValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
