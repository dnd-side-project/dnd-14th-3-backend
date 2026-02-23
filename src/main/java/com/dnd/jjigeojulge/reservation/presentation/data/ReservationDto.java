package com.dnd.jjigeojulge.reservation.presentation.data;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.global.common.dto.AuthorDto;
import com.dnd.jjigeojulge.global.common.dto.GeoPoint;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약 상세 응답 DTO")
public record ReservationDto(
        @Schema(description = "예약 ID", example = "101")
        Long reservationId,

        @Schema(description = "조회수", example = "7")
        Long viewCount,

        @Schema(description = "동행 요청 수", example = "3")
        Long requestCount,

        @Schema(description = "요청 메시지", example = "사진 찍는 걸 좋아하지만 혼자 서울 살다 보니 사진이 없어서 늘 아쉬워요.")
        String requestMessage,

        @Schema(description = "예상 촬영 소요 시간", example = "TWENTY_MINUTES")
        ShootingDurationOption expectedDuration,

        @Schema(description = "촬영 예약 일시 (KST, ISO-8601 형식)", example = "2026-02-20T14:30:00")
        LocalDateTime scheduledAt,

        @Schema(description = "1Depth 지역", example = "서울특별시")
        String region1Depth,

        @Schema(description = "구체적인 촬영 장소", example = "강남역 10번 출구 앞")
        String specificPlace,

        @Schema(description = "촬영 장소의 위치 좌표")
        GeoPoint location,

        @Schema(description = "예약 작성자 정보")
        AuthorDto author
) {
}
