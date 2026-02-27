package com.dnd.jjigeojulge.notification.domain;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;
import com.dnd.jjigeojulge.notification.domain.vo.NotificationMessage;
import com.dnd.jjigeojulge.notification.domain.vo.RelatedUrl;
import com.dnd.jjigeojulge.notification.domain.exception.NotificationValidationException;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseUpdatableEntity {

    @Column(nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Embedded
    private NotificationMessage message;

    @Embedded
    private RelatedUrl relatedUrl;

    @Column(nullable = false)
    private boolean isRead;

    private Notification(Long receiverId, NotificationType type, NotificationMessage message, RelatedUrl relatedUrl) {
        this.receiverId = receiverId;
        this.type = type;
        this.message = message;
        this.relatedUrl = relatedUrl;
        this.isRead = false;
    }

    public static Notification create(Long receiverId, NotificationType type, NotificationMessage message,
            RelatedUrl relatedUrl) {
        validateCreate(receiverId, type, message, relatedUrl);
        return new Notification(receiverId, type, message, relatedUrl);
    }

    private static void validateCreate(Long receiverId, NotificationType type, NotificationMessage message,
            RelatedUrl relatedUrl) {
        if (receiverId == null) {
            throw new NotificationValidationException("수신자 ID는 필수입니다.");
        }
        if (type == null) {
            throw new NotificationValidationException("알림 타입은 필수입니다.");
        }
        if (message == null) {
            throw new NotificationValidationException("알림 메시지는 필수입니다.");
        }
        if (relatedUrl == null) {
            throw new NotificationValidationException("관련 URL은 필수입니다.");
        }
    }

    public void read() {
        this.isRead = true;
    }
}
