package com.dnd.jjigeojulge.reservation.application.dto;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

public record UpdateReservationCommand(
        Long reservationId,
        Long userId,
        String title,
        String region1Depth,
        String specificPlace,
        Double latitude,
        Double longitude,
        LocalDateTime scheduledAt,
        ShootingDurationOption shootingDuration,
        String requestMessage) {

    public static UpdateReservationCommand of(Long reservationId, Long userId,
            com.dnd.jjigeojulge.reservation.presentation.request.ReservationUpdateRequest request) {
        return new UpdateReservationCommand(
                reservationId,
                userId,
                request.title(),
                request.region1Depth(),
                request.specificPlace(),
                request.latitude(),
                request.longitude(),
                request.scheduledAt(),
                request.shootingDuration(),
                request.requestMessage());
    }
}
