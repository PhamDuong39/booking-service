package com.cts.booking.modules.catalog.domain.error;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final ErrorCode code;

    protected DomainException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
}
