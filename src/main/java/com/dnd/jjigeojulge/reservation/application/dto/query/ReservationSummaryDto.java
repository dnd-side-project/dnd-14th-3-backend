package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약 탐색 리스트 응답 (피드 요약 카드)")
public record ReservationSummaryDto(
        @Schema(description = "예약 ID", example = "100") Long reservationId,
        @Schema(description = "게시글 제목", example = "상암 평화의 공원에서 스냅 찍어요") String title,
        @Schema(description = "약속 예약 날짜/시간", example = "2026-04-05T13:00:00") LocalDateTime scheduledAt,
        @Schema(description = "지역 1Depth (예: 서울특별시, 경기도, 인천광역시 등)", example = "서울특별시") Region1Depth region1Depth,
        @Schema(description = "구체적 장소명", example = "상암 평화의 공원") String specificPlace,
        @Schema(description = "예상 소요 시간 (TEN_MINUTES, TWENTY_MINUTES, THIRTY_PLUS_MINUTES 등)", example = "TWENTY_MINUTES") ShootingDurationOption shootingDuration,
        @Schema(description = "예약 상태 배지 (RECRUITING, CONFIRMED, RECRUITMENT_CLOSED, COMPLETED, CANCELED)", example = "RECRUITING") ReservationStatus status,
        @Schema(description = "작성자 ID", example = "5") Long ownerId,
        @Schema(description = "작성자 닉네임", example = "포토맨") String ownerNickname,
        @Schema(description = "작성자 성별 (MALE, FEMALE)", example = "MALE") Gender ownerGender,
        @Schema(description = "작성자 프로필 섬네일", example = "https://image.url/1.png") String ownerProfileImageUrl) {

    @QueryProjection
    public ReservationSummaryDto {
    }
}
