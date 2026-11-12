package com.cts.booking.modules.catalog.infrastructure.persistence.performance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "performance_gate", schema = "booking")
@IdClass(PerformanceGateId.class)
@Getter
@Setter
@NoArgsConstructor
public class PerformanceGateJpaEntity {
    // IDs refer to an existing performance and venue gate; neither is generated here.
    @Id
    @Column(name = "performance_id", nullable = false, updatable = false)
    private UUID performanceId;

    @Id
    @Column(name = "gate_id", nullable = false, updatable = false)
    private UUID gateId;

    // Composite database foreign keys enforce that the performance and gate share this venue.
    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

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
