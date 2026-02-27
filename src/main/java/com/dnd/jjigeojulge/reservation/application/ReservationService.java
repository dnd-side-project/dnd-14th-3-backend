package com.dnd.jjigeojulge.reservation.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.application.dto.CreateReservationCommand;
import com.dnd.jjigeojulge.reservation.application.dto.UpdateReservationCommand;
import com.dnd.jjigeojulge.reservation.domain.Applicant;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.exception.ReservationNotFoundException;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.domain.UserPhotoStyle;
import com.dnd.jjigeojulge.user.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final UserRepository userRepository;

        public Long createReservation(CreateReservationCommand command) {
                User user = userRepository.findByIdWithPhotoStyles(command.userId())
                                .orElseThrow(UserNotFoundException::new);

                OwnerInfo ownerInfo = OwnerInfo.of(
                                user.getId(),
                                user.getPhotoStyles().stream()
                                                .map(UserPhotoStyle::getPhotoStyle)
                                                .map(PhotoStyle::getName)
                                                .map(com.dnd.jjigeojulge.user.domain.StyleName::getLabel)
                                                .toList());

                ReservationTitle title = ReservationTitle.from(command.title());
                ScheduledTime scheduledTime = ScheduledTime.of(command.scheduledAt(), LocalDateTime.now());
                PlaceInfo placeInfo = PlaceInfo.of(
                                command.region1Depth(),
                                command.specificPlace(),
                                command.latitude(),
                                command.longitude());
                RequestMessage requestMessage = RequestMessage.from(command.requestMessage());

                Reservation reservation = Reservation.create(
                                ownerInfo,
                                title,
                                scheduledTime,
                                placeInfo,
                                command.shootingDuration(),
                                requestMessage);

                return reservationRepository.save(reservation).getId();
        }

        public void updateReservation(UpdateReservationCommand command) {
                Reservation reservation = findReservationById(command.reservationId());

                LocalDateTime now = LocalDateTime.now();

                // 기존 값 또는 새로운 값 채택 (PATCH 부분 업데이트 지원)
                String newTitle = command.title() != null ? command.title()
                                : reservation.getTitle().getValue();

                String newRegion1Depth = command.region1Depth() != null ? command.region1Depth()
                                : reservation.getPlaceInfo().getRegion1Depth().getLabel();
                String newSpecificPlace = command.specificPlace() != null ? command.specificPlace()
                                : reservation.getPlaceInfo().getSpecificPlace();
                Double newLatitude = command.latitude() != null ? command.latitude()
                                : reservation.getPlaceInfo().getLatitude().doubleValue();
                Double newLongitude = command.longitude() != null ? command.longitude()
                                : reservation.getPlaceInfo().getLongitude().doubleValue();

                LocalDateTime newScheduledAt = command.scheduledAt() != null ? command.scheduledAt()
                                : reservation.getScheduledTime().getTime();
                ShootingDurationOption newShootingDuration = command.shootingDuration() != null
                                ? command.shootingDuration()
                                : reservation.getShootingDuration();

                String newRequestMessage = command.requestMessage() != null ? command.requestMessage()
                                : reservation.getRequestMessage().getValue();

                ReservationTitle title = ReservationTitle.from(newTitle);
                ScheduledTime scheduledTime = ScheduledTime.of(newScheduledAt, now);
                PlaceInfo placeInfo = PlaceInfo.of(newRegion1Depth, newSpecificPlace, newLatitude, newLongitude);
                RequestMessage requestMessage = RequestMessage.from(newRequestMessage);

                reservation.update(
                                command.userId(),
                                title,
                                scheduledTime,
                                placeInfo,
                                newShootingDuration,
                                requestMessage,
                                now);
        }

        public void applyToReservation(Long reservationId, Long userId) {
                Reservation reservation = findReservationById(reservationId);

                if (!userRepository.existsById(userId)) {
                        throw new UserNotFoundException();
                }

                Applicant applicant = Applicant.create(reservation, userId);
                reservation.apply(applicant, LocalDateTime.now());
                // TODO: 예약 작성자에게 지원 알림 발송 (Event Publisher 활용 권장)
        }

        public void cancelApplicationToReservation(Long reservationId, Long userId) {
                Reservation reservation = findReservationById(reservationId);
                reservation.cancelApplication(userId, LocalDateTime.now());
        }

        public void acceptApplicant(Long reservationId, Long ownerId, Long applicantId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.acceptApplicant(ownerId, applicantId, LocalDateTime.now());
                // TODO: 양쪽 사용자에게 매칭 확정 푸시 알림 발송 (명세서 2번)
        }

        public void rejectApplicant(Long reservationId, Long ownerId, Long applicantId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.rejectApplicant(ownerId, applicantId, LocalDateTime.now());
                // TODO: 거절된 지원자에게 알림 발송 필요 여부 검토
        }

        public void cancelReservation(Long reservationId, Long userId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.cancel(userId, LocalDateTime.now());
                // TODO: 매칭이 확정된 상태였다면 상대방에게 취소 알림 발송
        }

        /**
         * scheduler-only; called by ScheduledTask
         */
        void completeReservation(Long reservationId) {
                Reservation reservation = findReservationById(reservationId);

                reservation.complete(LocalDateTime.now());
        }

        private Reservation findReservationById(Long reservationId) {
                return reservationRepository.findById(reservationId)
                                .orElseThrow(ReservationNotFoundException::new);
        }
}
