package com.cts.booking.modules.catalog.infrastructure.persistence.organizer.mapper;

import com.cts.booking.modules.catalog.domain.model.organizer.Organizer;
import com.cts.booking.modules.catalog.infrastructure.persistence.organizer.OrganizerJpaEntity;

public final class OrganizerMapper {
    public OrganizerMapper() {}

    public static OrganizerJpaEntity toJpa(Organizer organizer) {
        var e = new OrganizerJpaEntity();
        return e;
    }

    public static Organizer toDomain(OrganizerJpaEntity e) {
        return Organizer.builder()
                .build();
    }
}
