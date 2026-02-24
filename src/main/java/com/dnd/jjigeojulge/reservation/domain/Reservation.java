package com.dnd.jjigeojulge.reservation.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseUpdatableEntity {

        @Embedded
        private OwnerInfo ownerInfo;

        @Embedded
        private ScheduledTime scheduledTime;

        @Embedded
        private PlaceInfo placeInfo;

        @Enumerated(EnumType.STRING)
        @Column(name = "shooting_duration", nullable = false)
        private ShootingDurationOption shootingDuration;

        @Embedded
        private RequestMessage requestMessage;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private ReservationStatus status;

        @OneToMany(mappedBy = "reservation", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
        private List<Applicant> applicants = new ArrayList<>();

        private Reservation(
                        OwnerInfo ownerInfo,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage,
                        ReservationStatus status) {
                this.ownerInfo = ownerInfo;
                this.scheduledTime = scheduledTime;
                this.placeInfo = placeInfo;
                this.shootingDuration = shootingDuration;
                this.requestMessage = requestMessage;
                this.status = status;
        }

        public static Reservation create(
                        OwnerInfo ownerInfo,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage) {
                validateCreationData(ownerInfo, scheduledTime, placeInfo, shootingDuration);

                return new Reservation(
                                ownerInfo,
                                scheduledTime,
                                placeInfo,
                                shootingDuration,
                                requestMessage,
                                ReservationStatus.RECRUITING);
        }

        private static void validateCreationData(
                        OwnerInfo ownerInfo,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration) {
                if (ownerInfo == null) {
                        throw new IllegalArgumentException("작성자(Owner) 정보는 필수입니다.");
                }
                validateReservationData(scheduledTime, placeInfo, shootingDuration);
        }

        private static void validateReservationData(
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration) {
                if (scheduledTime == null) {
                        throw new IllegalArgumentException("예약 시간 정보는 필수입니다.");
                }
                if (placeInfo == null) {
                        throw new IllegalArgumentException("장소 정보는 필수입니다.");
                }
                if (shootingDuration == null) {
                        throw new IllegalArgumentException("촬영 소요 시간 옵션은 필수입니다.");
                }
        }

        public void apply(Applicant applicant, LocalDateTime now) {
                validateApply(applicant, now);
                this.applicants.add(applicant);
        }

        private void validateApply(Applicant applicant, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new IllegalStateException("모집 기간이 지난 예약에는 지원할 수 없습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new IllegalStateException("모집 중(RECRUITING)인 예약에만 지원할 수 있습니다.");
                }
                if (this.ownerInfo.isOwner(applicant.getUserId())) {
                        throw new IllegalArgumentException("자신의 예약에는 지원할 수 없습니다.");
                }
                if (hasAlreadyApplied(applicant.getUserId())) {
                        throw new IllegalStateException("이미 지원한 예약입니다.");
                }
        }

        private boolean hasAlreadyApplied(Long userId) {
                return this.applicants.stream()
                                .anyMatch(a -> a.getUserId().equals(userId)
                                                && a.getStatus() == ApplicantStatus.APPLIED);
        }

        public void acceptApplicant(Long ownerId, Long applicantId, LocalDateTime now) {
                validateAcceptApplicant(ownerId, now);

                Applicant selectedApplicant = findApplicantById(applicantId);
                selectedApplicant.markAsSelected();

                rejectAllExcept(selectedApplicant);
                this.status = ReservationStatus.CONFIRMED;
        }

        private void validateAcceptApplicant(Long ownerId, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new IllegalStateException("모집 기간이 지난 예약은 지원자를 수락할 수 없습니다.");
                }
                if (!this.ownerInfo.isOwner(ownerId)) {
                        throw new IllegalArgumentException("예약 작성자 본인만 지원자를 수락할 수 있습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new IllegalStateException("모집 중(RECRUITING)인 상태에서만 지원자를 수락할 수 있습니다.");
                }
        }

        private Applicant findApplicantById(Long applicantId) {
                return this.applicants.stream()
                                .filter(a -> a.getId() != null && a.getId().equals(applicantId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("해당 지원자를 찾을 수 없습니다."));
        }

        private void rejectAllExcept(Applicant selectedApplicant) {
                this.applicants.stream()
                                .filter(a -> a.getStatus() == ApplicantStatus.APPLIED && !a.equals(selectedApplicant))
                                .forEach(Applicant::markAsRejected);
        }

        public void update(
                        Long requesterId,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage,
                        LocalDateTime now) {
                validateUpdate(requesterId, scheduledTime, placeInfo, shootingDuration, now);
                this.scheduledTime = scheduledTime;
                this.placeInfo = placeInfo;
                this.shootingDuration = shootingDuration;
                this.requestMessage = requestMessage;
        }

        private void validateUpdate(
                        Long requesterId,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new IllegalStateException("모집 기간이 지난 예약 정보는 수정할 수 없습니다.");
                }
                if (!this.ownerInfo.isOwner(requesterId)) {
                        throw new IllegalArgumentException("예약 작성자 본인만 예약 정보를 수정할 수 있습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new IllegalStateException("모집 중(RECRUITING)인 상태에서만 예약 정보를 수정할 수 있습니다.");
                }
                validateReservationData(scheduledTime, placeInfo, shootingDuration);
        }

        public void cancel(Long requesterId, LocalDateTime now) {
                validateCancel(requesterId, now);
                this.status = ReservationStatus.CANCELED;
        }

        private void validateCancel(Long requesterId, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new IllegalStateException("모집 기간이 지난 예약은 취소할 수 없습니다.");
                }

                if (this.status == ReservationStatus.RECRUITING) {
                        if (!this.ownerInfo.isOwner(requesterId)) {
                                throw new IllegalArgumentException("모집 중인 예약은 작성자 본인만 취소할 수 있습니다.");
                        }
                } else if (this.status == ReservationStatus.CONFIRMED) {
                        boolean isOwner = this.ownerInfo.isOwner(requesterId);
                        boolean isSelectedApplicant = this.applicants.stream()
                                        .anyMatch(a -> a.getUserId().equals(requesterId)
                                                        && a.getStatus() == ApplicantStatus.SELECTED);

                        if (!isOwner && !isSelectedApplicant) {
                                throw new IllegalArgumentException("확정된 예약은 작성자나 매칭된 동행자만 취소할 수 있습니다.");
                        }
                } else {
                        throw new IllegalStateException("현재 상태에서는 예약을 취소할 수 없습니다.");
                }
        }

        public void complete(LocalDateTime now) {
                if (this.status != ReservationStatus.CONFIRMED) {
                        throw new IllegalStateException("확정됨(CONFIRMED) 상태인 예약만 완료 처리할 수 있습니다.");
                }
                if (!this.scheduledTime.isExpired(now)) {
                        throw new IllegalStateException("약속 시간이 지나기 전에는 완료 처리할 수 없습니다.");
                }
                this.status = ReservationStatus.COMPLETED;
        }

        public void closeRecruitmentIfExpired(LocalDateTime now) {
                if (this.status.isRecruiting() && this.scheduledTime.isExpired(now)) {
                        this.status = ReservationStatus.RECRUITMENT_CLOSED;
                }
        }
}
