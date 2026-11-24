package com.cts.booking.shared.domain.error;

public class NotFoundException extends DomainException {
    public NotFoundException() {
        this("RESOURCE_NOT_FOUND");
    }

    public NotFoundException(String code) {
        super(code, "Resource not found.");
    }
}
