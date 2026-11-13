package com.cts.booking.modules.catalog.infrastructure.persistence.performance;

// JSON representation of the venue details captured when configuring a performance.
public record VenueSnapshot(String name, String address, String city, String timezone) {
}
