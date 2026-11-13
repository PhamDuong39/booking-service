package com.cts.booking.modules.ordering.infrastructure.persistence.order;

import java.time.Instant;
import java.util.List;

// Ordering owns this historical JSON snapshot independently of Catalog persistence.
public record ConcertSnapshot(
        String title,
        List<String> artists,
        VenueSnapshot venue,
        Instant startsAt,
        String terms
) {
    public ConcertSnapshot {
        artists = artists == null ? null : List.copyOf(artists);
    }

    public record VenueSnapshot(String name, String address, String city, String timezone) {
    }
}
