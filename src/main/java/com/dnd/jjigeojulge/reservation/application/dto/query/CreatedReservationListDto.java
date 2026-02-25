package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

public record CreatedReservationListDto(
                Long reservationId,
                ReservationStatus status,
                String title,
                LocalDateTime scheduledAt,
                Region1Depth region1Depth,
                String specificPlace,
                ShootingDurationOption shootingDuration,
                long applicantCount) {
}
