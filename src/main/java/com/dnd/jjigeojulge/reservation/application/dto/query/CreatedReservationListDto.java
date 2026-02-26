package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내가 올린 동행 예약 리스트 조회 항목 응답 객체")
public record CreatedReservationListDto(
                @Schema(description = "예약 ID (다음 cursor 값)", example = "101") Long reservationId,
                @Schema(description = "방 자체의 상태 (RECRUITING, CONFIRMED, RECRUITMENT_CLOSED, COMPLETED, CANCELED)", example = "RECRUITING") ReservationStatus status,
                @Schema(description = "예약된 방의 제목", example = "이번 주말 서울숲 출사 가실 분") String title,
                @Schema(description = "약속 예약 날짜 및 시간", example = "2026-03-01T14:00:00") LocalDateTime scheduledAt,
                @Schema(description = "큰 지역 (대분류) 한글 명칭 반환", example = "서울특별시") Region1Depth region1Depth,
                @Schema(description = "구체적 장소명", example = "서울숲") String specificPlace,
                @Schema(description = "예상 촬영 소요 시간 (TEN_MINUTES, TWENTY_MINUTES, THIRTY_PLUS_MINUTES 등)", example = "THIRTY_PLUS_MINUTES") ShootingDurationOption shootingDuration,
                @Schema(description = "현재 이 방에 지원한 대기자 수", example = "3") long applicantCount) {
}
