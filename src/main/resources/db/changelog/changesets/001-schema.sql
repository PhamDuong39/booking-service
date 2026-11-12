--liquibase formatted sql
--changeset 00001-initial-booking:1.0_create_tables

CREATE TABLE booking.organizer (
    id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(200) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_organizer PRIMARY KEY (id),
    CONSTRAINT uk_organizer_code UNIQUE (code),
    CONSTRAINT ck_organizer_version CHECK (version >= 0)
);

CREATE TABLE booking.organizer_membership (
    organizer_id UUID NOT NULL,
    subject VARCHAR(128) NOT NULL,
    permission VARCHAR(24) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    granted_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_organizer_membership PRIMARY KEY (organizer_id, subject),
    CONSTRAINT fk_organizer_membership_organizer FOREIGN KEY (organizer_id)
        REFERENCES booking.organizer (id) ON DELETE RESTRICT,
    CONSTRAINT ck_organizer_membership_permission CHECK (permission IN ('MANAGE', 'SUPPORT_READ')),
    CONSTRAINT ck_organizer_membership_version CHECK (version >= 0)
);

CREATE TABLE booking.venue (
    id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    timezone VARCHAR(64) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_venue PRIMARY KEY (id)
);

CREATE TABLE booking.venue_gate (
    id UUID NOT NULL,
    venue_id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_venue_gate PRIMARY KEY (id),
    CONSTRAINT uk_venue_gate_fk_target_1 UNIQUE (id, venue_id),
    CONSTRAINT fk_venue_gate_1 FOREIGN KEY (venue_id)
        REFERENCES booking.venue (id) ON DELETE RESTRICT
);

CREATE TABLE booking.concert (
    id UUID NOT NULL,
    organizer_id UUID NOT NULL,
    slug VARCHAR(180) NOT NULL,
    title VARCHAR(250) NOT NULL,
    artists JSONB NOT NULL,
    description TEXT NOT NULL,
    purchase_terms TEXT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    first_published_at TIMESTAMP WITH TIME ZONE,
    catalog_revision BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_concert PRIMARY KEY (id),
    CONSTRAINT uk_concert_fk_target_1 UNIQUE (id, organizer_id),
    CONSTRAINT fk_concert_1 FOREIGN KEY (organizer_id)
        REFERENCES booking.organizer (id) ON DELETE RESTRICT
);

CREATE TABLE booking.performance (
    id UUID NOT NULL,
    concert_id UUID NOT NULL,
    venue_id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    starts_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_open_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_close_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    checkin_open_at TIMESTAMP WITH TIME ZONE NOT NULL,
    checkin_close_at TIMESTAMP WITH TIME ZONE NOT NULL,
    venue_snapshot JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_performance PRIMARY KEY (id),
    CONSTRAINT uk_performance_fk_target_1 UNIQUE (id, concert_id),
    CONSTRAINT uk_performance_fk_target_2 UNIQUE (id, venue_id),
    CONSTRAINT fk_performance_1 FOREIGN KEY (concert_id)
        REFERENCES booking.concert (id) ON DELETE RESTRICT,
    CONSTRAINT fk_performance_2 FOREIGN KEY (venue_id)
        REFERENCES booking.venue (id) ON DELETE RESTRICT
);

CREATE TABLE booking.performance_gate (
    performance_id UUID NOT NULL,
    venue_id UUID NOT NULL,
    gate_id UUID NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_performance_gate PRIMARY KEY (performance_id, gate_id),
    CONSTRAINT fk_performance_gate_1 FOREIGN KEY (performance_id, venue_id)
        REFERENCES booking.performance (id, venue_id) ON DELETE RESTRICT,
    CONSTRAINT fk_performance_gate_2 FOREIGN KEY (gate_id, venue_id)
        REFERENCES booking.venue_gate (id, venue_id) ON DELETE RESTRICT
);

CREATE TABLE booking.performance_assignment (
    performance_id UUID NOT NULL,
    subject VARCHAR(128) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    granted_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_performance_assignment PRIMARY KEY (performance_id, subject),
    CONSTRAINT fk_performance_assignment_1 FOREIGN KEY (performance_id)
        REFERENCES booking.performance (id) ON DELETE RESTRICT
);

CREATE TABLE booking.seat_section (
    id UUID NOT NULL,
    performance_id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(120) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_seat_section PRIMARY KEY (id),
    CONSTRAINT uk_seat_section_fk_target_1 UNIQUE (id, performance_id),
    CONSTRAINT fk_seat_section_1 FOREIGN KEY (performance_id)
        REFERENCES booking.performance (id) ON DELETE RESTRICT
);

CREATE TABLE booking.booking_order (
    id UUID NOT NULL,
    order_code VARCHAR(40) NOT NULL,
    buyer_subject VARCHAR(128) NOT NULL,
    buyer_email VARCHAR(320) NOT NULL,
    buyer_email_normalized VARCHAR(320) NOT NULL,
    organizer_id UUID NOT NULL,
    concert_id UUID NOT NULL,
    performance_id UUID NOT NULL,
    hold_id UUID NOT NULL,
    status VARCHAR(24) NOT NULL,
    seat_count SMALLINT NOT NULL,
    subtotal_vnd BIGINT NOT NULL,
    fee_vnd BIGINT NOT NULL DEFAULT 0,
    total_vnd BIGINT NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'VND',
    held_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    confirmed_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    close_reason VARCHAR(64),
    confirmed_attempt_id UUID,
    tickets_completed_at TIMESTAMP WITH TIME ZONE,
    needs_attention BOOLEAN NOT NULL DEFAULT FALSE,
    attention_reason VARCHAR(100),
    concert_snapshot JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_booking_order PRIMARY KEY (id),
    CONSTRAINT uk_booking_order_fk_target_1 UNIQUE (id, performance_id),
    CONSTRAINT uk_booking_order_fk_target_2 UNIQUE (id, performance_id, hold_id),
    CONSTRAINT fk_booking_order_1 FOREIGN KEY (concert_id, organizer_id)
        REFERENCES booking.concert (id, organizer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_order_2 FOREIGN KEY (performance_id, concert_id)
        REFERENCES booking.performance (id, concert_id) ON DELETE RESTRICT
);

CREATE TABLE booking.performance_seat (
    id UUID NOT NULL,
    performance_id UUID NOT NULL,
    section_id UUID NOT NULL,
    row_label VARCHAR(20) NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    x NUMERIC(10,2) NOT NULL,
    y NUMERIC(10,2) NOT NULL,
    price_vnd BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(16) NOT NULL DEFAULT 'AVAILABLE',
    current_order_id UUID,
    hold_id UUID,
    hold_expires_at TIMESTAMP WITH TIME ZONE,
    sold_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_performance_seat PRIMARY KEY (id),
    CONSTRAINT uk_performance_seat_fk_target_1 UNIQUE (id, performance_id),
    CONSTRAINT fk_performance_seat_1 FOREIGN KEY (performance_id)
        REFERENCES booking.performance (id) ON DELETE RESTRICT,
    CONSTRAINT fk_performance_seat_2 FOREIGN KEY (section_id, performance_id)
        REFERENCES booking.seat_section (id, performance_id) ON DELETE RESTRICT,
    CONSTRAINT fk_performance_seat_3 FOREIGN KEY (current_order_id, performance_id, hold_id)
        REFERENCES booking.booking_order (id, performance_id, hold_id) ON DELETE RESTRICT
);

CREATE TABLE booking.order_item (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    performance_id UUID NOT NULL,
    seat_id UUID NOT NULL,
    section_name VARCHAR(120) NOT NULL,
    row_label VARCHAR(20) NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    price_vnd BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT pk_order_item PRIMARY KEY (id),
    CONSTRAINT uk_order_item_fk_target_1 UNIQUE (id, order_id, performance_id, seat_id),
    CONSTRAINT fk_order_item_1 FOREIGN KEY (order_id, performance_id)
        REFERENCES booking.booking_order (id, performance_id) ON DELETE RESTRICT,
    CONSTRAINT fk_order_item_2 FOREIGN KEY (seat_id, performance_id)
        REFERENCES booking.performance_seat (id, performance_id) ON DELETE RESTRICT
);

CREATE TABLE booking.payment_projection (
    attempt_id UUID NOT NULL,
    order_id UUID NOT NULL,
    amount_vnd BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE,
    verified_at TIMESTAMP WITH TIME ZONE,
    source_version BIGINT NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_event_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_payment_projection PRIMARY KEY (attempt_id),
    CONSTRAINT fk_payment_projection_1 FOREIGN KEY (order_id)
        REFERENCES booking.booking_order (id) ON DELETE RESTRICT
);

CREATE TABLE booking.refund_projection (
    refund_id UUID NOT NULL,
    attempt_id UUID NOT NULL,
    status VARCHAR(24) NOT NULL,
    amount_vnd BIGINT NOT NULL,
    refunded_at TIMESTAMP WITH TIME ZONE,
    source_version BIGINT NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_event_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_refund_projection PRIMARY KEY (refund_id),
    CONSTRAINT fk_refund_projection_1 FOREIGN KEY (attempt_id)
        REFERENCES booking.payment_projection (attempt_id) ON DELETE RESTRICT
);

CREATE TABLE booking.ticket (
    id UUID NOT NULL,
    ticket_code VARCHAR(40) NOT NULL,
    order_item_id UUID NOT NULL,
    order_id UUID NOT NULL,
    performance_id UUID NOT NULL,
    seat_id UUID NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ISSUED',
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    qr_digest BYTEA NOT NULL,
    qr_ciphertext BYTEA NOT NULL,
    qr_nonce BYTEA NOT NULL,
    qr_key_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ticket PRIMARY KEY (id),
    CONSTRAINT uk_ticket_fk_target_1 UNIQUE (id, performance_id),
    CONSTRAINT fk_ticket_1 FOREIGN KEY (order_item_id, order_id, performance_id, seat_id)
        REFERENCES booking.order_item (id, order_id, performance_id, seat_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_2 FOREIGN KEY (seat_id, performance_id)
        REFERENCES booking.performance_seat (id, performance_id) ON DELETE RESTRICT
);

CREATE TABLE booking.ticket_checkin (
    ticket_id UUID NOT NULL,
    performance_id UUID NOT NULL,
    gate_id UUID NOT NULL,
    staff_subject VARCHAR(128) NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE NOT NULL,
    request_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT pk_ticket_checkin PRIMARY KEY (ticket_id),
    CONSTRAINT fk_ticket_checkin_1 FOREIGN KEY (ticket_id, performance_id)
        REFERENCES booking.ticket (id, performance_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_checkin_2 FOREIGN KEY (performance_id, gate_id)
        REFERENCES booking.performance_gate (performance_id, gate_id) ON DELETE RESTRICT
);

CREATE TABLE booking.ticket_email_grant (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    business_key VARCHAR(160) NOT NULL,
    owner_subject VARCHAR(128) NOT NULL,
    recipient_email VARCHAR(320) NOT NULL,
    purpose VARCHAR(24) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    requested_by VARCHAR(128),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ticket_email_grant PRIMARY KEY (id),
    CONSTRAINT fk_ticket_email_grant_1 FOREIGN KEY (order_id)
        REFERENCES booking.booking_order (id) ON DELETE RESTRICT
);

CREATE TABLE booking.support_operation (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    action VARCHAR(32) NOT NULL,
    target_reference VARCHAR(160) NOT NULL,
    actor_subject VARCHAR(128) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(24) NOT NULL,
    result_reference VARCHAR(160),
    error_code VARCHAR(80),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_support_operation PRIMARY KEY (id),
    CONSTRAINT fk_support_operation_1 FOREIGN KEY (order_id)
        REFERENCES booking.booking_order (id) ON DELETE RESTRICT
);

CREATE TABLE booking.idempotency_record (
    actor VARCHAR(160) NOT NULL,
    operation VARCHAR(64) NOT NULL,
    request_key VARCHAR(128) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    resource_id UUID NOT NULL,
    http_status SMALLINT NOT NULL,
    response JSONB NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT pk_idempotency_record PRIMARY KEY (actor, operation, request_key)
);

CREATE TABLE booking.workflow_dispatch (
    id UUID NOT NULL,
    order_id UUID NOT NULL,
    business_key VARCHAR(180) NOT NULL,
    kind VARCHAR(32) NOT NULL,
    workflow_id VARCHAR(160) NOT NULL,
    payload JSONB NOT NULL,
    state VARCHAR(16) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    lease_token UUID,
    lease_until TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(100),
    delivered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_workflow_dispatch PRIMARY KEY (id),
    CONSTRAINT fk_workflow_dispatch_1 FOREIGN KEY (order_id)
        REFERENCES booking.booking_order (id) ON DELETE RESTRICT
);

CREATE TABLE booking.outbox_event (
    event_id UUID NOT NULL,
    aggregate_type VARCHAR(40) NOT NULL,
    aggregate_id UUID NOT NULL,
    aggregate_version BIGINT NOT NULL,
    sequence_in_version SMALLINT NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    schema_version INTEGER NOT NULL DEFAULT 1,
    topic VARCHAR(120) NOT NULL,
    partition_key VARCHAR(128) NOT NULL,
    envelope JSONB NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    state VARCHAR(16) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    lease_token UUID,
    lease_until TIMESTAMP WITH TIME ZONE,
    published_at TIMESTAMP WITH TIME ZONE,
    last_error VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT pk_outbox_event PRIMARY KEY (event_id)
);

CREATE TABLE booking.inbox_event (
    consumer_name VARCHAR(80) NOT NULL,
    event_id UUID NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    envelope JSONB NOT NULL,
    state VARCHAR(16) NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    next_attempt_at TIMESTAMP WITH TIME ZONE,
    error_code VARCHAR(80),
    CONSTRAINT pk_inbox_event PRIMARY KEY (consumer_name, event_id)
);

CREATE TABLE booking.audit_log (
    id UUID NOT NULL,
    actor_type VARCHAR(16) NOT NULL,
    actor_subject VARCHAR(160) NOT NULL,
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id VARCHAR(160) NOT NULL,
    organizer_id UUID,
    order_id UUID,
    outcome VARCHAR(16) NOT NULL,
    reason_code VARCHAR(80),
    before_data JSONB,
    after_data JSONB,
    correlation_id UUID NOT NULL,
    operation_id UUID,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_audit_log PRIMARY KEY (id)
);

-- Rollback: drop dependent tables before their referenced tables.
--rollback DROP TABLE booking.audit_log;
--rollback DROP TABLE booking.inbox_event;
--rollback DROP TABLE booking.outbox_event;
--rollback DROP TABLE booking.workflow_dispatch;
--rollback DROP TABLE booking.idempotency_record;
--rollback DROP TABLE booking.support_operation;
--rollback DROP TABLE booking.ticket_email_grant;
--rollback DROP TABLE booking.ticket_checkin;
--rollback DROP TABLE booking.ticket;
--rollback DROP TABLE booking.refund_projection;
--rollback DROP TABLE booking.payment_projection;
--rollback DROP TABLE booking.order_item;
--rollback DROP TABLE booking.performance_seat;
--rollback DROP TABLE booking.booking_order;
--rollback DROP TABLE booking.seat_section;
--rollback DROP TABLE booking.performance_assignment;
--rollback DROP TABLE booking.performance_gate;
--rollback DROP TABLE booking.performance;
--rollback DROP TABLE booking.concert;
--rollback DROP TABLE booking.venue_gate;
--rollback DROP TABLE booking.venue;
--rollback DROP TABLE booking.organizer_membership;
--rollback DROP TABLE booking.organizer;
