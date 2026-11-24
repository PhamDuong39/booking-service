package com.cts.booking.shared.infrastructure.persistence.messaging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "inbox_event", schema = "booking")
@IdClass(InboxEventId.class)
@Getter
@Setter
@NoArgsConstructor
public class InboxEventJpaEntity {
    @Id
    @Column(name = "consumer_name", nullable = false, updatable = false, length = 80)
    private String consumerName;

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "payload_hash", nullable = false, updatable = false, length = 64)
    private String payloadHash;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "envelope", nullable = false, updatable = false, columnDefinition = "jsonb")
    private Map<String, Object> envelope;

    @Column(name = "state", nullable = false, length = 16)
    private String state;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "error_code", length = 80)
    private String errorCode;
}
