package com.cts.booking.modules.ticketing.infrastructure.persistence.ticket;

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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ticket", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class TicketJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "ticket_code", nullable = false, length = 40)
    private String ticketCode;

    @Column(name = "order_item_id", nullable = false)
    private UUID orderItemId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "performance_id", nullable = false)
    private UUID performanceId;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Column(name = "status", nullable = false, length = 16)
    private String status = "ISSUED";

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "qr_digest", nullable = false)
    private byte[] qrDigest;

    @Column(name = "qr_ciphertext", nullable = false)
    private byte[] qrCiphertext;

    @Column(name = "qr_nonce", nullable = false)
    private byte[] qrNonce;

    @Column(name = "qr_key_id", nullable = false, length = 64)
    private String qrKeyId;

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
