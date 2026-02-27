package com.dnd.jjigeojulge.notification.application;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    /**
     * 로그인한 유저의 알림 목록을 커서 기반 페이징으로 조회합니다.
     * 
     * @param receiverId 로그인한 유저 ID
     * @param cursor     페이징 커서 (알림 ID)
     * @param limit      조회할 개수
     * @return PageResponse 형태의 알림 목록
     */
    public PageResponse<NotificationResponseDto> getNotifications(Long receiverId, Long cursor, int limit) {
        Page<NotificationResponseDto> page = notificationRepository.getNotifications(receiverId, cursor, limit);
        // NotificationResponseDto의 notificationId를 커서로 사용
        return PageResponse.fromCursor(page, NotificationResponseDto::notificationId);
    }
}
