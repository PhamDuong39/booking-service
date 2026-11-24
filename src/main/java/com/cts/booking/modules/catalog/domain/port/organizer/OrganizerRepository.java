package com.cts.booking.modules.catalog.domain.port.organizer;


import com.cts.booking.modules.catalog.domain.model.organizer.Organizer;

public interface OrganizerRepository {
    Organizer save(Organizer organizer);
}
