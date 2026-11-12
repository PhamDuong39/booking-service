package com.cts.booking.modules.ordering.infrastructure.persistence.order;

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
@Table(name = "booking_order", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class BookingOrderJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "performance_id", nullable = false)
    private UUID performanceId;

    @Column(name = "hold_id", nullable = false)
    private UUID holdId;

    @Column(name = "order_code", nullable = false, length = 40)
    private String orderCode;

    @Column(name = "buyer_subject", nullable = false, length = 128)
    private String buyerSubject;

    @Column(name = "buyer_email", nullable = false, length = 320)
    private String buyerEmail;

    @Column(name = "buyer_email_normalized", nullable = false, length = 320)
    private String buyerEmailNormalized;

    @Column(name = "status", nullable = false, length = 24)
    private String status;

    @Column(name = "seat_count", nullable = false)
    private Short seatCount;

    @Column(name = "subtotal_vnd", nullable = false)
    private Long subtotalVnd;

    @Column(name = "fee_vnd", nullable = false)
    private Long feeVnd = 0L;

    @Column(name = "total_vnd", nullable = false)
    private Long totalVnd;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "VND";

    @Column(name = "held_at", nullable = false)
    private Instant heldAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "close_reason", length = 64)
    private String closeReason;

    @Column(name = "confirmed_attempt_id")
    private UUID confirmedAttemptId;

    @Column(name = "tickets_completed_at")
    private Instant ticketsCompletedAt;

    @Column(name = "needs_attention", nullable = false)
    private boolean needsAttention = false;

    @Column(name = "attention_reason", length = 100)
    private String attentionReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "concert_snapshot", nullable = false, columnDefinition = "jsonb")
    private ConcertSnapshot concertSnapshot;

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
