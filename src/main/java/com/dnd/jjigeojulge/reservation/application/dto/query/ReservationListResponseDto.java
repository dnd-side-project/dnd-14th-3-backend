package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약 탐색 리스트 응답 (피드 요약 카드, 임박 여부 포함)")
public record ReservationListResponseDto(
        @Schema(description = "예약 ID", example = "100") Long reservationId,
        @Schema(description = "게시글 제목", example = "상암 평화의 공원에서 스냅 찍어요") String title,
        @Schema(description = "약속 예약 날짜/시간", example = "2026-04-05T13:00:00") LocalDateTime scheduledAt,
        @Schema(description = "지역 1Depth", example = "서울특별시") Region1Depth region1Depth,
        @Schema(description = "구체적 장소명", example = "상암 평화의 공원") String specificPlace,
        @Schema(description = "예상 소요 시간", example = "TWENTY_MINUTES") ShootingDurationOption shootingDuration,
        @Schema(description = "예약 상태 배지", example = "RECRUITING") ReservationStatus status,
        @Schema(description = "예약 모집 임박 여부 (24시간 이내 남음)", example = "true") boolean isImminent,
        @Schema(description = "작성자 ID", example = "5") Long ownerId,
        @Schema(description = "작성자 닉네임", example = "포토맨") String ownerNickname,
        @Schema(description = "작성자 성별", example = "MALE") com.dnd.jjigeojulge.user.domain.Gender ownerGender,
        @Schema(description = "작성자 프로필 섬네일", example = "https://image.url/1.png") String ownerProfileImageUrl) {

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
                dto.region1Depth(),
                dto.specificPlace(),
                dto.shootingDuration(),
                dto.status(),
                imminent,
                dto.ownerId(),
                dto.ownerNickname(),
                dto.ownerGender(),
                dto.ownerProfileImageUrl());
    }
}
