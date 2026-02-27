package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ApplicantStatus;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내가 지원한 동행 예약 리스트 조회 항목 응답 객체")
public record AppliedReservationListDto(
        @Schema(description = "예약 ID (다음 cursor 값)", example = "105") Long reservationId,
        @Schema(description = "나의 게스트 가상 매칭 상태 (WAITING, MATCHED, COMPLETED, REJECTED, CANCELED)", example = "WAITING") AppliedReservationStatus status, // 가상
        // 지원
        // 상태
        @Schema(description = "예약된 방의 제목", example = "홍대입구역 카페 스냅") String title,
        @Schema(description = "약속 예약 날짜 및 시간", example = "2026-03-05T15:00:00") LocalDateTime scheduledAt,
        @Schema(description = "큰 지역 (대분류) 한글 명칭 반환", example = "서울특별시") Region1Depth region1Depth,
        @Schema(description = "구체적 장소명", example = "홍대입구역") String specificPlace,
        @Schema(description = "예상 촬영 소요 시간 (TEN_MINUTES, TWENTY_MINUTES, THIRTY_PLUS_MINUTES)", example = "TWENTY_MINUTES") ShootingDurationOption shootingDuration,
        @Schema(description = "이 방의 누적 총 지원자(경쟁자) 수", example = "1") Long applicantCount) {

    public static AppliedReservationListDto of(
            Long reservationId,
            ReservationStatus reservationStatus,
            ApplicantStatus applicantStatus,
            String title,
            LocalDateTime scheduledAt,
            Region1Depth region1Depth,
            String specificPlace,
            ShootingDurationOption shootingDuration,
            Long applicantCount,
            LocalDateTime now) {

        AppliedReservationStatus virtualStatus = mapToVirtualStatus(reservationStatus, applicantStatus, scheduledAt,
                now);

        return new AppliedReservationListDto(
                reservationId,
                virtualStatus,
                title,
                scheduledAt,
                region1Depth,
                specificPlace,
                shootingDuration,
                applicantCount);
    }

    private static AppliedReservationStatus mapToVirtualStatus(
            ReservationStatus reservationStatus,
            ApplicantStatus applicantStatus,
            LocalDateTime scheduledAt,
            LocalDateTime now) {

        boolean isExpired = scheduledAt.isBefore(now);

        if (applicantStatus == ApplicantStatus.CANCELED) {
            return AppliedReservationStatus.CANCELED;
        }

        if (applicantStatus == ApplicantStatus.REJECTED) {
            return AppliedReservationStatus.REJECTED;
        }

        if (applicantStatus == ApplicantStatus.APPLIED) {
            if (reservationStatus == ReservationStatus.RECRUITING && !isExpired) {
                return AppliedReservationStatus.WAITING;
            }
            if (reservationStatus == ReservationStatus.CONFIRMED || reservationStatus == ReservationStatus.COMPLETED
                    || isExpired) {
                // 타인이 선택되어 확정되었거나 지났으면 매칭 실패 처리
                return AppliedReservationStatus.REJECTED;
            }
        }

        if (applicantStatus == ApplicantStatus.SELECTED) {
            if (isExpired) {
                return AppliedReservationStatus.COMPLETED;
            }
            return AppliedReservationStatus.MATCHED;
        }

        // 기본 안전망 (발생할 일 없겠지만 CANCELED로 폴백)
        return AppliedReservationStatus.CANCELED;
    }
}
