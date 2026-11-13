package com.cts.booking.shared.infrastructure.persistence.messaging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SourceType;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "outbox_event", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEventJpaEntity {
    // Supply the event ID from the application event so it matches the envelope.
    // Business event fields are immutable; only delivery metadata may be updated.
    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "aggregate_type", nullable = false, updatable = false, length = 40)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_version", nullable = false, updatable = false)
    private Long aggregateVersion;

    @Column(name = "sequence_in_version", nullable = false, updatable = false)
    private Short sequenceInVersion;

    @Column(name = "event_type", nullable = false, updatable = false, length = 64)
    private String eventType;

    @Column(name = "schema_version", nullable = false, updatable = false)
    private Integer schemaVersion = 1;

    @Column(name = "topic", nullable = false, updatable = false, length = 120)
    private String topic;

    @Column(name = "partition_key", nullable = false, updatable = false, length = 128)
    private String partitionKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "envelope", nullable = false, updatable = false, columnDefinition = "jsonb")
    private Map<String, Object> envelope;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "state", nullable = false, length = 16)
    private String state;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "lease_token")
    private UUID leaseToken;

    @Column(name = "lease_until")
    private Instant leaseUntil;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", length = 100)
    private String lastError;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
