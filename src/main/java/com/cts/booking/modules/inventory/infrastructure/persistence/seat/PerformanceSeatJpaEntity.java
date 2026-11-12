package com.cts.booking.modules.inventory.infrastructure.persistence.seat;

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
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "performance_seat", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class PerformanceSeatJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "performance_id", nullable = false)
    private UUID performanceId;

    @Column(name = "section_id", nullable = false)
    private UUID sectionId;

    @Column(name = "row_label", nullable = false, length = 20)
    private String rowLabel;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    @Column(name = "x", nullable = false, precision = 10, scale = 2)
    private BigDecimal xCoordinate;

    @Column(name = "y", nullable = false, precision = 10, scale = 2)
    private BigDecimal yCoordinate;

    @Column(name = "price_vnd", nullable = false)
    private Long priceVnd;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "status", nullable = false, length = 16)
    private String status = "AVAILABLE";

    @Column(name = "current_order_id")
    private UUID currentOrderId;

    @Column(name = "hold_id")
    private UUID holdId;

    @Column(name = "hold_expires_at")
    private Instant holdExpiresAt;

    @Column(name = "sold_at")
    private Instant soldAt;

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
