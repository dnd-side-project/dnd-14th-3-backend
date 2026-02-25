package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;

public record ReservationDetailDto(
        Long reservationId,
        int viewCount,
        int applicantCount,
        int commentCount,
        Long ownerId,
        String ownerNickname,
        String ownerProfileImageUrl,
        Integer ownerTrustScore,
        Gender ownerGender,
        String title,
        LocalDateTime scheduledAt,
        Region1Depth region1Depth,
        String specificPlace,
        List<String> photoStyleSnapshot,
        ShootingDurationOption shootingDuration,
        String requestMessage,
        ReservationStatus status) {
}
