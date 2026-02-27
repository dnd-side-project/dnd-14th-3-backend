package com.dnd.jjigeojulge.reservation.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.reservation.domain.exception.ReservationValidationException;
import com.dnd.jjigeojulge.reservation.domain.event.ApplicantAddedEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationAcceptedEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationCanceledEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationRejectedEvent;

import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseUpdatableEntity {

        @Transient
        private final List<Object> domainEvents = new ArrayList<>();

        @Embedded
        private OwnerInfo ownerInfo;

        @Embedded
        private ReservationTitle title;

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

        @Column(name = "view_count", nullable = false)
        private int viewCount = 0;

        @OneToMany(mappedBy = "reservation", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
        private List<Applicant> applicants = new ArrayList<>();

        private Reservation(
                        OwnerInfo ownerInfo,
                        ReservationTitle title,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage,
                        ReservationStatus status) {
                this.ownerInfo = ownerInfo;
                this.title = title;
                this.scheduledTime = scheduledTime;
                this.placeInfo = placeInfo;
                this.shootingDuration = shootingDuration;
                this.requestMessage = requestMessage;
                this.status = status;
        }

        public static Reservation create(
                        OwnerInfo ownerInfo,
                        ReservationTitle title,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage) {
                validateCreationData(ownerInfo, title, scheduledTime, placeInfo, shootingDuration);

                return new Reservation(
                                ownerInfo,
                                title,
                                scheduledTime,
                                placeInfo,
                                shootingDuration,
                                requestMessage,
                                ReservationStatus.RECRUITING);
        }

        private static void validateCreationData(
                        OwnerInfo ownerInfo,
                        ReservationTitle title,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration) {
                if (ownerInfo == null) {
                        throw new ReservationValidationException("작성자(Owner) 정보는 필수입니다.");
                }
                if (title == null) {
                        throw new ReservationValidationException("예약 제목 정보는 필수입니다.");
                }
                validateReservationData(scheduledTime, placeInfo, shootingDuration);
        }

        private static void validateReservationData(
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration) {
                if (scheduledTime == null) {
                        throw new ReservationValidationException("예약 시간 정보는 필수입니다.");
                }
                if (placeInfo == null) {
                        throw new ReservationValidationException("장소 정보는 필수입니다.");
                }
                if (shootingDuration == null) {
                        throw new ReservationValidationException("촬영 소요 시간 옵션은 필수입니다.");
                }
        }

        public void apply(Applicant applicant, LocalDateTime now) {
                validateApply(applicant, now);
                this.applicants.add(applicant);
                registerEvent(new ApplicantAddedEvent(this.getId(), this.ownerInfo.getUserId(), applicant.getUserId()));
        }

        public ReservationStatus getVirtualStatus(LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        if (this.status == ReservationStatus.RECRUITING) {
                                return ReservationStatus.RECRUITMENT_CLOSED;
                        } else if (this.status == ReservationStatus.CONFIRMED) {
                                return ReservationStatus.COMPLETED;
                        }
                }
                return this.status;
        }

        private void validateApply(Applicant applicant, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("모집 기간이 지난 예약에는 지원할 수 없습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new ReservationValidationException("모집 중(RECRUITING)인 예약에만 지원할 수 있습니다.");
                }
                if (this.ownerInfo.isOwner(applicant.getUserId())) {
                        throw new ReservationValidationException("자신의 예약에는 지원할 수 없습니다.");
                }
                if (hasAlreadyApplied(applicant.getUserId())) {
                        throw new ReservationValidationException("이미 지원한 예약입니다.");
                }
        }

        private boolean hasAlreadyApplied(Long userId) {
                return this.applicants.stream()
                                .anyMatch(a -> a.getUserId().equals(userId)
                                                && a.getStatus() == ApplicantStatus.APPLIED);
        }

        public void cancelApplication(Long applicantUserId, LocalDateTime now) {
                if (!this.status.isRecruiting()) {
                        throw new ReservationValidationException("모집 중(RECRUITING)인 상태에서만 지원을 취소할 수 있습니다.");
                }
                if (this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("모집 기간이 지난 예약의 지원은 취소할 수 없습니다.");
                }
                Applicant applicant = this.applicants.stream()
                                .filter(a -> a.getUserId().equals(applicantUserId)
                                                && (a.getStatus() == ApplicantStatus.APPLIED
                                                                || a.getStatus() == ApplicantStatus.SELECTED))
                                .findFirst()
                                .orElseThrow(() -> new ReservationValidationException("취소할 지원 내역이 없습니다."));

                applicant.cancelApplication();
        }

        public void acceptApplicant(Long ownerId, Long applicantId, LocalDateTime now) {
                validateAcceptApplicant(ownerId, now);

                Applicant selectedApplicant = findApplicantById(applicantId);
                selectedApplicant.markAsSelected();

                rejectAllExcept(selectedApplicant);
                this.status = ReservationStatus.CONFIRMED;

                registerEvent(new ReservationAcceptedEvent(this.getId(), selectedApplicant.getUserId()));
        }

        private void validateAcceptApplicant(Long ownerId, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("모집 기간이 지난 예약은 지원자를 수락할 수 없습니다.");
                }
                if (!this.ownerInfo.isOwner(ownerId)) {
                        throw new ReservationValidationException("예약 작성자 본인만 지원자를 수락할 수 있습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new ReservationValidationException("모집 중(RECRUITING)인 상태에서만 지원자를 수락할 수 있습니다.");
                }
        }

        private Applicant findApplicantById(Long applicantId) {
                return this.applicants.stream()
                                .filter(a -> a.getId() != null && a.getId().equals(applicantId))
                                .findFirst()
                                .orElseThrow(() -> new ReservationValidationException("해당 지원자를 찾을 수 없습니다."));
        }

        private void rejectAllExcept(Applicant selectedApplicant) {
                this.applicants.stream()
                                .filter(a -> a.getStatus() == ApplicantStatus.APPLIED && !a.equals(selectedApplicant))
                                .forEach(Applicant::markAsRejected);
        }

        public void update(
                        Long requesterId,
                        ReservationTitle title,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        RequestMessage requestMessage,
                        LocalDateTime now) {
                validateUpdate(requesterId, title, scheduledTime, placeInfo, shootingDuration, now);
                this.title = title;
                this.scheduledTime = scheduledTime;
                this.placeInfo = placeInfo;
                this.shootingDuration = shootingDuration;
                this.requestMessage = requestMessage;
        }

        private void validateUpdate(
                        Long requesterId,
                        ReservationTitle title,
                        ScheduledTime scheduledTime,
                        PlaceInfo placeInfo,
                        ShootingDurationOption shootingDuration,
                        LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("모집 기간이 지난 예약 정보는 수정할 수 없습니다.");
                }
                if (!this.ownerInfo.isOwner(requesterId)) {
                        throw new ReservationValidationException("예약 작성자 본인만 예약 정보를 수정할 수 있습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new ReservationValidationException("모집 중(RECRUITING)인 상태에서만 예약 정보를 수정할 수 있습니다.");
                }
                if (title == null) {
                        throw new ReservationValidationException("예약 제목 정보는 필수입니다.");
                }
                validateReservationData(scheduledTime, placeInfo, shootingDuration);
        }

        public void cancel(Long requesterId, LocalDateTime now) {
                validateCancel(requesterId, now);

                List<Long> appliedUserIds = this.applicants.stream()
                                .filter(a -> a.getStatus() == ApplicantStatus.APPLIED)
                                .map(Applicant::getUserId)
                                .toList();

                cancelApplicants();
                this.status = ReservationStatus.CANCELED;

                if (!appliedUserIds.isEmpty()) {
                        registerEvent(new ReservationCanceledEvent(this.getId(), appliedUserIds));
                }
        }

        private void validateCancel(Long requesterId, LocalDateTime now) {
                if (this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("모집 기간이 지난 예약은 취소할 수 없습니다.");
                }

                if (!this.ownerInfo.isOwner(requesterId)) {
                        throw new ReservationValidationException("예약은 작성자 본인만 취소할 수 있습니다.");
                }

                if (this.status != ReservationStatus.RECRUITING) {
                        throw new ReservationValidationException("모집 중(RECRUITING) 상태에서만 예약을 취소할 수 있습니다.");
                }
        }

        private void cancelApplicants() {
                this.applicants.stream()
                                .filter(a -> a.getStatus() == ApplicantStatus.APPLIED)
                                .forEach(Applicant::cancelApplication);
        }

        public void complete(LocalDateTime now) {
                if (this.status != ReservationStatus.CONFIRMED) {
                        throw new ReservationValidationException("확정됨(CONFIRMED) 상태인 예약만 완료 처리할 수 있습니다.");
                }
                if (!this.scheduledTime.isExpired(now)) {
                        throw new ReservationValidationException("약속 시간이 지나기 전에는 완료 처리할 수 없습니다.");
                }
                this.status = ReservationStatus.COMPLETED;
        }

        public void rejectApplicant(Long ownerId, Long applicantId, LocalDateTime now) {
                validateRejectApplicant(ownerId, now);

                Applicant rejectedApplicant = findApplicantById(applicantId);
                rejectedApplicant.markAsRejected();

                registerEvent(new ReservationRejectedEvent(this.getId(), rejectedApplicant.getUserId()));
        }

        private void validateRejectApplicant(Long ownerId, LocalDateTime now) {
                if (!this.ownerInfo.isOwner(ownerId)) {
                        throw new ReservationValidationException("예약 작성자 본인만 지원자를 거절할 수 있습니다.");
                }
                if (!this.status.isRecruiting()) {
                        throw new ReservationValidationException("모집 중(RECRUITING)인 상태에서만 지원자를 거절할 수 있습니다.");
                }
        }

        @DomainEvents
        public List<Object> domainEvents() {
                return Collections.unmodifiableList(domainEvents);
        }

        @AfterDomainEventPublication
        public void clearDomainEvents() {
                domainEvents.clear();
        }

        protected void registerEvent(Object event) {
                this.domainEvents.add(event);
        }
}
