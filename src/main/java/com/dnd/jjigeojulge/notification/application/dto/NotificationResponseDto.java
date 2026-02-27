package com.dnd.jjigeojulge.notification.application.dto;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.notification.domain.Notification;
import com.dnd.jjigeojulge.notification.domain.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 응답 모델")
public record NotificationResponseDto(
        @Schema(description = "알림 식별자", example = "1") Long notificationId,

        @Schema(description = "알림 타입", example = "RESERVATION_APPLIED") NotificationType type,

        @Schema(description = "알림 내용", example = "김유저님이 동행을 신청했습니다.") String message,

        @Schema(description = "알림 클릭 시 이동할 URL", example = "/reservations/123") String relatedUrl,

        @Schema(description = "읽음 여부", example = "false") boolean isRead,

        @Schema(description = "알림 발생 시각") LocalDateTime createdAt) {
    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getType(),
                notification.getMessage().getValue(),
                notification.getRelatedUrl().getValue(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
