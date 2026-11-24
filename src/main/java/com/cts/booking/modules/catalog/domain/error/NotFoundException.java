package com.cts.booking.modules.catalog.domain.error;

public class NotFoundException extends DomainException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
