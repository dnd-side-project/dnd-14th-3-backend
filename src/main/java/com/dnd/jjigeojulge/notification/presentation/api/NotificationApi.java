package com.dnd.jjigeojulge.notification.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.dnd.jjigeojulge.global.annotation.CurrentUserId;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "알림 API", description = "알림 조회 및 상태 변경 API")
public interface NotificationApi {

    @Operation(summary = "알림 목록 페이징 조회", description = "로그인한 사용자의 알림 목록을 커서 기반 페이징으로 조회합니다.")
    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<NotificationResponseDto>>> getNotifications(
            @Parameter(hidden = true) @CurrentUserId Long currentUserId,
            @Parameter(description = "마지막으로 조회한 알림 ID (커서), 입력하지 않을 경우 최근 알림부터 조회합니다.") @RequestParam(required = false) Long cursor,
            @Parameter(description = "조회할 알림 개수 (기본 20개)") @RequestParam(defaultValue = "20") int limit);

    @Operation(summary = "알림 단건 읽음 처리", description = "단건 알림을 클릭하여 읽음(isRead=true) 처리합니다.")
    @PatchMapping("/{id}/read")
    ResponseEntity<ApiResponse<Void>> readNotification(
            @Parameter(hidden = true) @CurrentUserId Long currentUserId,
            @Parameter(description = "알림 ID") @PathVariable("id") Long id);
}
