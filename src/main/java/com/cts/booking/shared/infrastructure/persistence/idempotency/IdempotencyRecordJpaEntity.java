package com.cts.booking.shared.infrastructure.persistence.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name = "idempotency_record", schema = "booking")
@IdClass(IdempotencyRecordId.class)
@Getter
@Setter
@NoArgsConstructor
public class IdempotencyRecordJpaEntity {
    @Id
    @Column(name = "actor", nullable = false, updatable = false, length = 160)
    private String actor;

    @Id
    @Column(name = "operation", nullable = false, updatable = false, length = 64)
    private String operation;

    @Id
    @Column(name = "request_key", nullable = false, updatable = false, length = 128)
    private String requestKey;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "request_hash", nullable = false, updatable = false, length = 64)
    private String requestHash;

    @Column(name = "resource_id", nullable = false, updatable = false)
    private UUID resourceId;

    @Column(name = "http_status", nullable = false, updatable = false)
    private Short httpStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response", nullable = false, updatable = false, columnDefinition = "jsonb")
    private Map<String, Object> response;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
