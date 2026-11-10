package com.cts.booking.modules.catalog.infrastructure.persistence.venuegate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "venue_gate", schema = "booking")
@Getter
@Setter
@NoArgsConstructor
public class VenueGateJpaEntity {

//    id UUID NOT NULL,
//    venue_id UUID NOT NULL,
//    code VARCHAR(40) NOT NULL,
//    name VARCHAR(120) NOT NULL,
//    active BOOLEAN NOT NULL DEFAULT TRUE,
//    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
//    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp(),
//    version BIGINT NOT NULL DEFAULT 0,
//    CONSTRAINT pk_venue_gate PRIMARY KEY (id),
//    CONSTRAINT uk_venue_gate_fk_target_1 UNIQUE (id, venue_id),
//    CONSTRAINT fk_venue_gate_1 FOREIGN KEY (venue_id)
//    REFERENCES booking.venue (id) ON DELETE RESTRICT
}
