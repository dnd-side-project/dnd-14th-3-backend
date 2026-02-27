package com.dnd.jjigeojulge.notification.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.domain.Notification;
import com.dnd.jjigeojulge.notification.domain.NotificationType;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationRepository;
import com.dnd.jjigeojulge.notification.domain.vo.NotificationMessage;
import com.dnd.jjigeojulge.notification.domain.vo.RelatedUrl;
import com.dnd.jjigeojulge.reservation.domain.event.ApplicantAddedEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationAcceptedEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationCanceledEvent;
import com.dnd.jjigeojulge.reservation.domain.event.ReservationRejectedEvent;
import com.dnd.jjigeojulge.sse.SseMessage;
import com.dnd.jjigeojulge.sse.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    private static final String NOTIFICATION_EVENT_NAME = "notification";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleApplicantAdded(ApplicantAddedEvent event) {
        NotificationMessage message = NotificationMessage.from("회원님의 동행 모집에 새로운 신청이 들어왔습니다.");
        RelatedUrl url = RelatedUrl.from("/reservations/" + event.reservationId());

        Notification notification = Notification.create(
                event.ownerId(),
                NotificationType.RESERVATION_APPLIED,
                message,
                url);
        notificationRepository.save(notification);
        sendSse(event.ownerId(), notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationAccepted(ReservationAcceptedEvent event) {
        NotificationMessage message = NotificationMessage.from("신청하신 동행이 확정되었습니다!");
        RelatedUrl url = RelatedUrl.from("/reservations/" + event.reservationId());

        Notification notification = Notification.create(
                event.applicantId(),
                NotificationType.RESERVATION_ACCEPTED,
                message,
                url);
        notificationRepository.save(notification);
        sendSse(event.applicantId(), notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationRejected(ReservationRejectedEvent event) {
        NotificationMessage message = NotificationMessage.from("신청하신 동행이 아쉽게도 거절되었습니다.");
        RelatedUrl url = RelatedUrl.from("/reservations/" + event.reservationId());

        Notification notification = Notification.create(
                event.applicantId(),
                NotificationType.RESERVATION_REJECTED,
                message,
                url);
        notificationRepository.save(notification);
        sendSse(event.applicantId(), notification);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCanceled(ReservationCanceledEvent event) {
        NotificationMessage message = NotificationMessage.from("신청하신 동행 모집이 취소되었습니다.");
        RelatedUrl url = RelatedUrl.from("/reservations/" + event.reservationId());

        event.applicantIds().forEach(applicantId -> {
            Notification notification = Notification.create(
                    applicantId,
                    NotificationType.RESERVATION_CANCELED,
                    message,
                    url);
            notificationRepository.save(notification);
            sendSse(applicantId, notification);
        });
    }

    private void sendSse(Long receiverId, Notification notification) {
        try {
            NotificationResponseDto dto = NotificationResponseDto.from(notification);
            SseMessage sseMessage = SseMessage.create(receiverId, NOTIFICATION_EVENT_NAME, dto);
            sseService.send(sseMessage);
        } catch (Exception e) {
            log.error("SSE 전송 실패 (receiverId: {})", receiverId, e);
        }
    }
}
