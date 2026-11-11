package com.cts.booking.modules.catalog.infrastructure.persistence.concert;

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
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "concert", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class ConcertJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(name = "slug", nullable = false, length = 180)
    private String slug;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    // Store artist names as a JSON array in the existing PostgreSQL JSONB column.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "artists", nullable = false, columnDefinition = "jsonb")
    private List<String> artists;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "purchase_terms", nullable = false, columnDefinition = "text")
    private String purchaseTerms;

    @Column(name = "status", nullable = false, length = 16)
    private String status = "DRAFT";

    @Column(name = "first_published_at")
    private Instant firstPublishedAt;

    // Snapshot revision is distinct from Hibernate's optimistic-lock version.
    @Column(name = "catalog_revision", nullable = false)
    private long catalogRevision = 0L;

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
