package com.cts.booking.modules.catalog.infrastructure.persistence.performance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "performance", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class PerformanceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "code", nullable = false, length = 40)
    private String code;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "sales_open_at", nullable = false)
    private Instant salesOpenAt;

    @Column(name = "sales_close_at", nullable = false)
    private Instant salesCloseAt;

    @Column(name = "sales_enabled", nullable = false)
    private boolean salesEnabled = false;

    @Column(name = "checkin_open_at", nullable = false)
    private Instant checkinOpenAt;

    @Column(name = "checkin_close_at", nullable = false)
    private Instant checkinCloseAt;

    // Preserve venue details as a JSON object for this performance.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "venue_snapshot", nullable = false, columnDefinition = "jsonb")
    private VenueSnapshot venueSnapshot;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
