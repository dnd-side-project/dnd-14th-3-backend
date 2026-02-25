package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;

public record MyReservationDetailDto(
        Long reservationId,
        String title,
        LocalDateTime scheduledAt,
        String specificPlace,
        List<String> photoStyleSnapshot,
        ShootingDurationOption shootingDuration,
        String requestMessage,
        ReservationStatus status,
        int applicantCount,
        List<ApplicantInfoDto> applicants) {
}
