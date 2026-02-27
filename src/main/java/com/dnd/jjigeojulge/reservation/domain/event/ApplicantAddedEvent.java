package com.dnd.jjigeojulge.reservation.domain.event;

public record ApplicantAddedEvent(
                Long reservationId,
                Long ownerId,
                Long applicantId) {
}
