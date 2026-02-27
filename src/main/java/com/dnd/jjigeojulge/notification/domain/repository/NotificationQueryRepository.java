package com.dnd.jjigeojulge.notification.domain.repository;

import org.springframework.data.domain.Page;

import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;

public interface NotificationQueryRepository {

    /**
     * 사용자의 알림 목록을 커서 기반 페이징으로 조회합니다.
     * 
     * @param receiverId 알림 수신자 ID
     * @param cursor     마지막으로 조회한 알림 ID (해당 ID보다 작은 값들을 조회, null일 경우 가장 최근 값부터)
     * @param limit      조회할 최대 알림 수
     * @return 알림 목록 (PageResponse 변환용 Page 객체)
     */
    Page<NotificationResponseDto> getNotifications(Long receiverId, Long cursor, int limit);
}
