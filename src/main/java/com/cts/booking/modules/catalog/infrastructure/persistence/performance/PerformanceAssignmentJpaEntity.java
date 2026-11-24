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
@Table(name = "performance_assignment", schema = "booking")
@IdClass(PerformanceAssignmentId.class)
@Getter
@Setter
@NoArgsConstructor
public class PerformanceAssignmentJpaEntity {
    // References an existing performance; the database enforces the foreign key.
    @Id
    @Column(name = "performance_id", nullable = false, updatable = false)
    private UUID performanceId;

    @Id
    @Column(name = "subject", nullable = false, updatable = false, length = 128)
    private String subject;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "granted_by", nullable = false, length = 128)
    private String grantedBy;

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
