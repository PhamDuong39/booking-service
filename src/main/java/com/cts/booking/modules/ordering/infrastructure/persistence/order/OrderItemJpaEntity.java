package com.cts.booking.modules.ordering.infrastructure.persistence.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_item", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class OrderItemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "performance_id", nullable = false, updatable = false)
    private UUID performanceId;

    @Column(name = "seat_id", nullable = false, updatable = false)
    private UUID seatId;

    @Column(name = "section_name", nullable = false, updatable = false, length = 120)
    private String sectionName;

    @Column(name = "row_label", nullable = false, updatable = false, length = 20)
    private String rowLabel;

    @Column(name = "seat_number", nullable = false, updatable = false, length = 20)
    private String seatNumber;

    @Column(name = "price_vnd", nullable = false, updatable = false)
    private Long priceVnd;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
