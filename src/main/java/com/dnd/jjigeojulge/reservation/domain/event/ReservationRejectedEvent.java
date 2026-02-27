package com.dnd.jjigeojulge.reservation.domain.event;

public record ReservationRejectedEvent(
		Long reservationId,
		Long applicantId) {
}
