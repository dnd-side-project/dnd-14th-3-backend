package com.dnd.jjigeojulge.reservation.application.dto.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ApplicantStatus;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;

class AppliedReservationListDtoTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime futureScheduledAt = now.plusDays(1);
    private final LocalDateTime pastScheduledAt = now.minusDays(1);

    private AppliedReservationListDto createDto(ReservationStatus resStatus, ApplicantStatus appStatus,
            LocalDateTime scheduledAt) {
        return AppliedReservationListDto.of(
                1L,
                resStatus,
                appStatus,
                "Test Title",
                scheduledAt,
                Region1Depth.SEOUL,
                "Hongdae",
                ShootingDurationOption.TEN_MINUTES,
                3L,
                now);
    }

    @Test
    @DisplayName("내가 취소(CANCELED)한 경우 상태는 CANCELED가 반환된다.")
    void test_Canceled() {
        AppliedReservationListDto dto = createDto(ReservationStatus.RECRUITING, ApplicantStatus.CANCELED,
                futureScheduledAt);
        assertThat(dto.status()).isEqualTo(AppliedReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("내가 거절(REJECTED)당한 경우 상태는 REJECTED가 반환된다.")
    void test_Rejected() {
        AppliedReservationListDto dto = createDto(ReservationStatus.RECRUITING, ApplicantStatus.REJECTED,
                futureScheduledAt);
        assertThat(dto.status()).isEqualTo(AppliedReservationStatus.REJECTED);
    }

    @Test
    @DisplayName("내가 대기중(APPLIED)이고 아직 모집중(RECRUITING)이며 예약 시간이 안 지났으면 WAITING이 반환된다.")
    void test_Waiting() {
        AppliedReservationListDto dto = createDto(ReservationStatus.RECRUITING, ApplicantStatus.APPLIED,
                futureScheduledAt);
        assertThat(dto.status()).isEqualTo(AppliedReservationStatus.WAITING);
    }

    @Test
    @DisplayName("내가 대기중(APPLIED)인데, 방이 확정(CONFIRMED)되거나 기한이 지났으면(RECRUITMENT_CLOSED) 타인이 선택된 것이므로 REJECTED가 반환된다.")
    void test_AppliedButClosed_ThrowsRejected() {
        AppliedReservationListDto dtoConfirmed = createDto(ReservationStatus.CONFIRMED, ApplicantStatus.APPLIED,
                futureScheduledAt);
        assertThat(dtoConfirmed.status()).isEqualTo(AppliedReservationStatus.REJECTED);

        AppliedReservationListDto dtoExpired = createDto(ReservationStatus.RECRUITING, ApplicantStatus.APPLIED,
                pastScheduledAt);
        assertThat(dtoExpired.status()).isEqualTo(AppliedReservationStatus.REJECTED);
    }

    @Test
    @DisplayName("내가 선택(SELECTED)받았고 예약 시간이 아직 지나지 않았으면 MATCHED가 반환된다.")
    void test_Matched() {
        AppliedReservationListDto dto = createDto(ReservationStatus.CONFIRMED, ApplicantStatus.SELECTED,
                futureScheduledAt);
        assertThat(dto.status()).isEqualTo(AppliedReservationStatus.MATCHED);
    }

    @Test
    @DisplayName("내가 선택(SELECTED)받았고 예약 시간이 이미 지났으면 COMPLETED가 반환된다.")
    void test_Completed() {
        AppliedReservationListDto dto = createDto(ReservationStatus.CONFIRMED, ApplicantStatus.SELECTED,
                pastScheduledAt);
        assertThat(dto.status()).isEqualTo(AppliedReservationStatus.COMPLETED);
    }
}
