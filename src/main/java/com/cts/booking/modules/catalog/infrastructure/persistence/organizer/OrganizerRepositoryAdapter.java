package com.cts.booking.modules.catalog.infrastructure.persistence.organizer;

import com.cts.booking.modules.catalog.domain.model.organizer.Organizer;
import com.cts.booking.modules.catalog.domain.port.organizer.OrganizerRepository;
import com.cts.booking.modules.catalog.infrastructure.persistence.organizer.mapper.OrganizerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizerRepositoryAdapter implements OrganizerRepository {
    private final OrganizerJpaRepository jpa;

    @Override
    public Organizer save(Organizer organizer) {
        OrganizerJpaEntity saved = jpa.save(OrganizerMapper.toJpa(organizer));
        return OrganizerMapper.toDomain(saved);
    }
}
