package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방장이 작성한 동행 예약 글의 모집 현황 및 지원자 목록 응답 DTO")
public record ApplicantListResponseDto(
        @Schema(description = "현재까지 해당 예약에 지원한 총 인원 수", example = "5") int totalCount,
        @Schema(description = "개별 지원자 정보 목록") List<ApplicantDto> applicants) {
}
