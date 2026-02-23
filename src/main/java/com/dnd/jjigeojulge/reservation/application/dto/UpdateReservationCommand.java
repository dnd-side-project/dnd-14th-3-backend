package com.dnd.jjigeojulge.reservation.application.dto;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

public record UpdateReservationCommand(
        Long reservationId,
        Long userId,
        String region1Depth,
        String specificPlace,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledAt,
        ShootingDurationOption shootingDuration,
        String requestMessage
) {
}
