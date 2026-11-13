package com.cts.booking.shared.infrastructure.persistence.idempotency;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IdempotencyRecordId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String actor;

    private String operation;

    private String requestKey;
}
