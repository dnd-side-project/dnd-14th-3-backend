package com.dnd.jjigeojulge.notification.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMessage {

    private static final int MAX_LENGTH = 100;

    @Column(name = "message", length = MAX_LENGTH, nullable = false)
    private String value;

    private NotificationMessage(String value) {
        this.value = value;
    }

    public static NotificationMessage from(String value) {
        String normalized = value == null ? null : value.trim();
        validate(normalized);
        return new NotificationMessage(normalized);
    }

    private static void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("알림 메시지는 필수입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("알림 메시지는 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
        }
    }
}
