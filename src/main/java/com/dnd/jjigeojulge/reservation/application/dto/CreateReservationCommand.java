package com.dnd.jjigeojulge.reservation.application.dto;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

public record CreateReservationCommand(
        Long userId,
        String region,
        String specificPlace,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledAt,
        ShootingDurationOption shootingDuration,
        String requestMessage
) {
}
