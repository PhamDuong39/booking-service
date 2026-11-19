package com.cts.booking.modules.catalog.domain.error;

public class BadRequestException  extends DomainException {
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
