package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;

public record ReservationSummaryDto(
        Long reservationId,
        String title,
        LocalDateTime scheduledAt,
        Region1Depth region1Depth,
        String specificPlace,
        List<String> photoStyleSnapshot,
        Integer trustScore,
        ReservationStatus status,
        Long ownerId,
        String ownerNickname,
        Gender ownerGender,
        String ownerProfileImageUrl) {
}
