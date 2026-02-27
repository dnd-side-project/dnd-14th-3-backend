package com.dnd.jjigeojulge.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;
import com.dnd.jjigeojulge.notification.domain.Notification;
import com.dnd.jjigeojulge.notification.domain.NotificationMessage;
import com.dnd.jjigeojulge.notification.domain.NotificationType;
import com.dnd.jjigeojulge.notification.domain.RelatedUrl;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationCommandService notificationCommandService;

    @Test
    @DisplayName("알림을 성공적으로 읽음 처리한다.")
    void readNotification_success() {
        // given
        Long receiverId = 1L;
        Long notificationId = 100L;
        Notification notification = createNotification(receiverId);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when
        notificationCommandService.readNotification(receiverId, notificationId);

        // then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 알림을 읽음 처리하려고 하면 예외가 발생한다.")
    void readNotification_notFound() {
        // given
        Long receiverId = 1L;
        Long notificationId = 100L;

        given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationCommandService.readNotification(receiverId, notificationId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("본인의 알림이 아닌 것을 읽음 처리하려고 하면 예외가 발생한다.")
    void readNotification_unauthorized() {
        // given
        Long receiverId = 1L;
        Long unauthorizedUserId = 2L;
        Long notificationId = 100L;
        Notification notification = createNotification(receiverId);
        ReflectionTestUtils.setField(notification, "id", notificationId);

        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when & then
        assertThatThrownBy(() -> notificationCommandService.readNotification(unauthorizedUserId, notificationId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED.getMessage());
    }

    private Notification createNotification(Long receiverId) {
        return Notification.builder()
                .receiverId(receiverId)
                .type(NotificationType.RESERVATION_APPLIED)
                .message(NotificationMessage.from("메시지"))
                .relatedUrl(RelatedUrl.from("/test"))
                .build();
    }
}
