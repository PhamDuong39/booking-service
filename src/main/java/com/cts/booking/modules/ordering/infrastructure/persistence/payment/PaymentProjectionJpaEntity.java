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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_projection", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class PaymentProjectionJpaEntity {
    @Id
    // The Payment service supplies this ID; Booking must preserve it.
    @Column(name = "attempt_id", nullable = false, updatable = false)
    private UUID attemptId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount_vnd", nullable = false)
    private Long amountVnd;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

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
