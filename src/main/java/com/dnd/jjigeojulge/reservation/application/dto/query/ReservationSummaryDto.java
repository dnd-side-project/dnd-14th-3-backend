package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;

public record ReservationSummaryDto(
        Long reservationId,
        String title,
        LocalDateTime scheduledAt,
        String specificPlace,
        List<String> photoStyleSnapshot,
        Integer trustScore,
        ReservationStatus status,
        Long ownerId,
        String ownerNickname) {
}
