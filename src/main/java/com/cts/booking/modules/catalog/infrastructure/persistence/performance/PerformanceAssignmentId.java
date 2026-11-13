package com.cts.booking.modules.catalog.infrastructure.persistence.performance;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PerformanceAssignmentId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID performanceId;
    private String subject;
}
