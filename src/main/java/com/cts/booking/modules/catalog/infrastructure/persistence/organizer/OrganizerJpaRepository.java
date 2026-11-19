package com.cts.booking.modules.catalog.infrastructure.persistence.organizer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizerJpaRepository extends JpaRepository<OrganizerJpaEntity, UUID> {
    // implement query here
}
