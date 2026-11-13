package com.cts.booking.shared.infrastructure.persistence.messaging;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InboxEventId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String consumerName;

    private UUID eventId;
}
