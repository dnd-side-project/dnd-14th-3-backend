package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;

public record ReservationListResponseDto(
        Long reservationId,
        String title,
        LocalDateTime scheduledAt,
        String specificPlace,
        List<String> photoStyleSnapshot,
        Integer trustScore,
        ReservationStatus status,
        boolean isImminent,
        Long ownerId,
        String ownerNickname,
        com.dnd.jjigeojulge.user.domain.Gender ownerGender,
        String ownerProfileImageUrl) {

    public static ReservationListResponseDto of(ReservationSummaryDto dto, LocalDateTime now) {
        boolean imminent = false;
        if (dto.status() == ReservationStatus.RECRUITING && dto.scheduledAt() != null) {
            // 24시간 이내면 일정임박으로 판단
            imminent = dto.scheduledAt().isBefore(now.plusHours(24)) && dto.scheduledAt().isAfter(now);
        }

        return new ReservationListResponseDto(
                dto.reservationId(),
                dto.title(),
                dto.scheduledAt(),
                dto.specificPlace(),
                dto.photoStyleSnapshot(),
                dto.trustScore(),
                dto.status(),
                imminent,
                dto.ownerId(),
                dto.ownerNickname(),
                dto.ownerGender(),
                dto.ownerProfileImageUrl()
        );
    }
}
