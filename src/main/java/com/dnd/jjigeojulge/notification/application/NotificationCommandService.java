package com.dnd.jjigeojulge.notification.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;
import com.dnd.jjigeojulge.notification.domain.Notification;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;

    /**
     * 특정 알림을 읽음 처리합니다.
     * 
     * @param receiverId     로그인한 사용자 ID (알림 소유자 검증용)
     * @param notificationId 대상 알림 ID
     */
    public void readNotification(Long receiverId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)); // TODO: 구체적인 예외 코드로 변경 가능

        // 소유권 검증 로직
        if (!notification.getReceiverId().equals(receiverId)) {
            // 본인의 알림이 아닌 경우 예외 발생
            throw new BusinessException(ErrorCode.UNAUTHORIZED); // TODO: 구체적인 예외 코드로 변경
        }

        notification.read();
    }
}
