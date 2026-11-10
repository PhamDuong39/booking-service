package com.cts.booking.modules.catalog.infrastructure.persistence.organizermembership;

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
public class OrganizerMembershipId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID organizerId;
    private String subject;
}
