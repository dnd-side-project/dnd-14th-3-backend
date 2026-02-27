package com.dnd.jjigeojulge.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.domain.NotificationType;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Test
    @DisplayName("알림 목록을 커서 기반 페이징으로 정상적으로 조회한다.")
    void getNotifications_returnsPageResponse() {
        // given
        Long receiverId = 1L;
        Long cursor = 15L;
        int limit = 10;

        List<NotificationResponseDto> mockContent = List.of(
                new NotificationResponseDto(14L, NotificationType.RESERVATION_APPLIED, "메시지1", "/url1", false,
                        LocalDateTime.now()),
                new NotificationResponseDto(13L, NotificationType.RESERVATION_ACCEPTED, "메시지2", "/url2", true,
                        LocalDateTime.now()));
        PageImpl<NotificationResponseDto> mockPage = new PageImpl<>(mockContent, PageRequest.of(0, limit), 20L);

        // when
        when(notificationRepository.getNotifications(eq(receiverId), eq(cursor), anyInt()))
                .thenReturn(mockPage);

        PageResponse<NotificationResponseDto> result = notificationQueryService.getNotifications(receiverId, cursor,
                limit);

        // then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).notificationId()).isEqualTo(14L);
        assertThat(result.content().get(1).notificationId()).isEqualTo(13L);

        // 커서 추출 검증: 마지막 요소의 ID가 커서로 설정되어야 함
        assertThat(result.nextCursor()).isEqualTo(13L);
        assertThat(result.totalElements()).isEqualTo(20L);
        assertThat(result.size()).isEqualTo(limit);
    }
}
