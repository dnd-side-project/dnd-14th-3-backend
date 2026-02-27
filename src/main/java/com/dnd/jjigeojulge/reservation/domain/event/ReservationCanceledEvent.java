package com.dnd.jjigeojulge.reservation.domain.event;

import java.util.List;

public record ReservationCanceledEvent(
        Long reservationId,
        List<Long> applicantIds) {
}
