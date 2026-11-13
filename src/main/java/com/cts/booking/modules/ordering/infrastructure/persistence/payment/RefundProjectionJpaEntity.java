package com.cts.booking.modules.ordering.infrastructure.persistence.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "refund_projection", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class RefundProjectionJpaEntity {
    @Id
    // The Payment service supplies this ID; Booking must preserve it.
    @Column(name = "refund_id", nullable = false, updatable = false)
    private UUID refundId;

    @Column(name = "attempt_id", nullable = false)
    private UUID attemptId;

    @Column(name = "status", nullable = false, length = 24)
    private String status;

    @Column(name = "amount_vnd", nullable = false)
    private Long amountVnd;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "source_version", nullable = false)
    private Long sourceVersion;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "last_event_id", nullable = false)
    private UUID lastEventId;

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
