package com.dnd.jjigeojulge.reservation.domain.event;

public record ReservationAcceptedEvent(
		Long reservationId,
		Long applicantId) {
}
