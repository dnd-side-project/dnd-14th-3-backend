package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ApplicantStatus;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

public record AppliedReservationListDto(
        Long reservationId,
        AppliedReservationStatus status, // 가상 지원 상태
        String title,
        LocalDateTime scheduledAt,
        Region1Depth region1Depth,
        String specificPlace,
        ShootingDurationOption shootingDuration,
        Long applicantCount) {

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
