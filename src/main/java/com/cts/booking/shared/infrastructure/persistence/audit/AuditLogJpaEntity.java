package com.cts.booking.shared.infrastructure.persistence.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "audit_log", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogJpaEntity {
    // Append-only audit data; no extra common timestamps or version column.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "actor_type", nullable = false, updatable = false, length = 16)
    private String actorType;

    @Column(name = "actor_subject", nullable = false, updatable = false, length = 160)
    private String actorSubject;

    @Column(name = "action", nullable = false, updatable = false, length = 80)
    private String action;

    @Column(name = "entity_type", nullable = false, updatable = false, length = 40)
    private String entityType;

    @Column(name = "entity_id", nullable = false, updatable = false, length = 160)
    private String entityId;

    @Column(name = "organizer_id", updatable = false)
    private UUID organizerId;

    @Column(name = "order_id", updatable = false)
    private UUID orderId;

    @Column(name = "outcome", nullable = false, updatable = false, length = 16)
    private String outcome;

    @Column(name = "reason_code", updatable = false, length = 80)
    private String reasonCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_data", updatable = false, columnDefinition = "jsonb")
    private Map<String, Object> beforeData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_data", updatable = false, columnDefinition = "jsonb")
    private Map<String, Object> afterData;

    @Column(name = "correlation_id", nullable = false, updatable = false)
    private UUID correlationId;

    @Column(name = "operation_id", updatable = false)
    private UUID operationId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;
}
