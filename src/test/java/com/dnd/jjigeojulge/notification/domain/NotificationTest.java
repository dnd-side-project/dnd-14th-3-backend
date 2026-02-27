package com.dnd.jjigeojulge.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.notification.domain.vo.NotificationMessage;
import com.dnd.jjigeojulge.notification.domain.vo.RelatedUrl;
import com.dnd.jjigeojulge.notification.domain.exception.NotificationValidationException;

class NotificationTest {

    @Test
    @DisplayName("알림 객체를 처음 생성하면 읽음 상태는 false여야 한다.")
    void createNotification_isReadShouldBeFalse() {
        // given
        Long receiverId = 1L;
        NotificationType type = NotificationType.RESERVATION_APPLIED;
        NotificationMessage message = NotificationMessage.from("동행 신청이 왔습니다.");
        RelatedUrl relatedUrl = RelatedUrl.from("/reservations/1");

        // when
        Notification notification = Notification.create(receiverId, type, message, relatedUrl);

        // then
        assertThat(notification.getReceiverId()).isEqualTo(receiverId);
        assertThat(notification.getType()).isEqualTo(type);
        assertThat(notification.getMessage().getValue()).isEqualTo(message.getValue());
        assertThat(notification.getRelatedUrl().getValue()).isEqualTo(relatedUrl.getValue());
        assertThat(notification.isRead()).isFalse(); // 중요: 초기 상태 검증
    }

    @Test
    @DisplayName("알림 객체 생성 시 필수값이 누락되면 예외가 발생한다.")
    void createNotification_nullValues_throwsException() {
        // given
        Long receiverId = 1L;
        NotificationType type = NotificationType.RESERVATION_APPLIED;
        NotificationMessage message = NotificationMessage.from("동행 신청이 왔습니다.");
        RelatedUrl relatedUrl = RelatedUrl.from("/reservations/1");

        // when & then
        assertThatThrownBy(() -> Notification.create(null, type, message, relatedUrl))
                .isInstanceOf(NotificationValidationException.class);

        assertThatThrownBy(() -> Notification.create(receiverId, null, message, relatedUrl))
                .isInstanceOf(NotificationValidationException.class);

        assertThatThrownBy(() -> Notification.create(receiverId, type, null, relatedUrl))
                .isInstanceOf(NotificationValidationException.class);

        assertThatThrownBy(() -> Notification.create(receiverId, type, message, null))
                .isInstanceOf(NotificationValidationException.class);
    }

    @Test
    @DisplayName("알림 객체의 read 메서드를 호출하면 읽음 상태가 true로 변경된다.")
    void readNotification_isReadShouldBeTrue() {
        // given
        Notification notification = Notification.create(1L, NotificationType.RESERVATION_ACCEPTED,
                NotificationMessage.from("동행이 확정되었습니다."),
                RelatedUrl.from("/reservations/2"));
        assertThat(notification.isRead()).isFalse();

        // when
        notification.read();

        // then
        assertThat(notification.isRead()).isTrue();
    }
}
