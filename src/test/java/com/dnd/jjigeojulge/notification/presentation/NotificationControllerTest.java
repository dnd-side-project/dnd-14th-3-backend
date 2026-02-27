package com.dnd.jjigeojulge.notification.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.notification.application.NotificationCommandService;
import com.dnd.jjigeojulge.notification.application.NotificationQueryService;
import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.domain.NotificationType;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private NotificationQueryService notificationQueryService;

        @MockBean
        private NotificationCommandService notificationCommandService;

        @MockBean
        private com.dnd.jjigeojulge.auth.infra.jwt.JwtTokenProvider jwtTokenProvider;

        @Test
        @DisplayName("알림 목록을 커서 기반 페이징으로 성공적으로 조회한다.")
        @WithMockUser
        void getNotifications_success() throws Exception {
                // given
                List<NotificationResponseDto> mockContent = List.of(
                                new NotificationResponseDto(14L, NotificationType.RESERVATION_APPLIED, "메시지1", "/url1",
                                                false,
                                                LocalDateTime.now()),
                                new NotificationResponseDto(13L, NotificationType.RESERVATION_ACCEPTED, "메시지2", "/url2",
                                                true,
                                                LocalDateTime.now()));
                PageResponse<NotificationResponseDto> mockResponse = new PageResponse<>(
                                mockContent,
                                13L, // nextCursor
                                20, // size
                                true, // hasNext
                                100L // totalElements
                );

                given(notificationQueryService.getNotifications(any(Long.class), any(), anyInt()))
                                .willReturn(mockResponse);

                // when & then
                com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails mockUserDetails = new com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails(
                                1L, java.util.List.of());

                mockMvc.perform(get("/api/v1/notifications")
                                .param("limit", "20")
                                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                                .user(mockUserDetails)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SUCCESS"))
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content.length()").value(2))
                                .andExpect(jsonPath("$.data.content[0].notificationId").value(14))
                                .andExpect(jsonPath("$.data.nextCursor").value(13))
                                .andExpect(jsonPath("$.data.hasNext").value(true));
        }

        @Test
        @DisplayName("알림 단건을 성공적으로 읽음 처리한다.")
        @WithMockUser
        void readNotification_success() throws Exception {
                // given
                Long notificationId = 100L;
                com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails mockUserDetails = new com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails(
                                1L, java.util.List.of());

                // when & then
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/notifications/{id}/read", notificationId)
                                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                                .user(mockUserDetails)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SUCCESS"));
        }
}
