package com.cts.booking.modules.ticketing.infrastructure.persistence.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ticket_checkin", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class TicketCheckinJpaEntity {
    // The primary key is the existing ticket ID; it must not be generated here.
    @Id
    @Column(name = "ticket_id", nullable = false, updatable = false)
    private UUID ticketId;

    @Column(name = "performance_id", nullable = false, updatable = false)
    private UUID performanceId;

    @Column(name = "gate_id", nullable = false, updatable = false)
    private UUID gateId;

    @Column(name = "staff_subject", nullable = false, updatable = false, length = 128)
    private String staffSubject;

    @Column(name = "used_at", nullable = false, updatable = false)
    private Instant usedAt;

    @Column(name = "request_id", nullable = false, updatable = false)
    private UUID requestId;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
