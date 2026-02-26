package com.dnd.jjigeojulge.reservation.presentation.request;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약 부분 수정(PATCH) 요청 데이터")
public record ReservationUpdateRequest(
        @Schema(description = "예약 게시글 제목 (수정하지 않을 시 null 가능)", example = "서울숲 피크닉 스냅 구해요", nullable = true) String title,

        @Schema(description = "큰 지역 대분류 수정값", example = "서울특별시", nullable = true) String region1Depth,

        @Schema(description = "구체적인 만남 장소 이름", example = "서울숲 정문 앞", nullable = true) String specificPlace,

        @Schema(description = "수정할 약속 장소 위도", example = "37.5445", nullable = true) Double latitude,

        @Schema(description = "수정할 약속 장소 경도", example = "127.0374", nullable = true) Double longitude,

        @Schema(description = "수정할 약속 날짜 및 시간", example = "2026-04-10T14:00:00", type = "string", nullable = true) LocalDateTime scheduledAt,

        @Schema(description = "수정 예상 촬영 시간 옵션", example = "ONE_HOUR", nullable = true) ShootingDurationOption shootingDuration,

        @Schema(description = "요청(요청 사항) 메시지 본문", example = "봄 느낌 나게 찍고 싶어요!", nullable = true) String requestMessage) {
}
