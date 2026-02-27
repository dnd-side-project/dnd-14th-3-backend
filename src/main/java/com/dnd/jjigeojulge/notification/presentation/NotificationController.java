package com.dnd.jjigeojulge.notification.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.annotation.CurrentUserId;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.notification.application.NotificationCommandService;
import com.dnd.jjigeojulge.notification.application.NotificationQueryService;
import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.presentation.api.NotificationApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    @Override
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponseDto>>> getNotifications(
            @CurrentUserId Long currentUserId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit) {

        PageResponse<NotificationResponseDto> response = notificationQueryService
                .getNotifications(currentUserId, cursor, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> readNotification(
            @CurrentUserId Long currentUserId,
            @org.springframework.web.bind.annotation.PathVariable Long id) {

        notificationCommandService.readNotification(currentUserId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
