package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "동행 예약 상세 탐색 응답 객체")
public record ReservationDetailDto(
        @Schema(description = "예약 ID", example = "100") Long reservationId,
        @Schema(description = "게시물 조회수", example = "42") int viewCount,
        @Schema(description = "현재 지원자 수", example = "3") int applicantCount,
        @Schema(description = "현재 달린 댓글 수", example = "5") int commentCount,
        @Schema(description = "작성자(호스트) ID", example = "1") Long ownerId,
        @Schema(description = "작성자 닉네임", example = "춘식이짱") String ownerNickname,
        @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg") String ownerProfileImageUrl,
        @Schema(description = "작성자 성별 (MALE, FEMALE)", example = "MALE") Gender ownerGender,
        @Schema(description = "작성자 연령대 (기존 가입자의 경우 null일 수 있음)", nullable = true, example = "TWENTIES") AgeGroup ownerAgeGroup,
        @Schema(description = "작성자 한줄 소개 (선택 항목)", nullable = true, example = "사진 찍는 걸 좋아합니다.") String ownerIntroduction,
        @Schema(description = "예약 게시글 제목", example = "이번 주 일요일 카페 창가자리 스냅") String title,
        @Schema(description = "약속 예약 날짜 및 시간", example = "2026-03-01T14:30:00") LocalDateTime scheduledAt,
        @Schema(description = "지역 1Depth (예: 서울특별시, 경기도, 인천광역시 등)", example = "서울특별시") Region1Depth region1Depth,
        @Schema(description = "구체적 장소명", example = "천안 신불당동 OOO 카페") String specificPlace,
        @Schema(description = "요청자의 선호 촬영 유형(StyleName) 스냅샷 목록", example = "[\"UPPER_BODY_FOCUS\", \"INDOOR_SHOOT\"]") List<String> photoStyleSnapshot,
        @Schema(description = "예상 소요 시간 (TEN_MINUTES, TWENTY_MINUTES, THIRTY_PLUS_MINUTES 등)", example = "THIRTY_PLUS_MINUTES") ShootingDurationOption shootingDuration,
        @Schema(description = "호스트가 남긴 상세 요청 메시지 본문", example = "자연스러운 분위기 원해요. 렌즈는 50mm 가져오시면 좋을 것 같아요!") String requestMessage,
        @Schema(description = "예약 모집 상태 (RECRUITING, CONFIRMED, RECRUITMENT_CLOSED, COMPLETED, CANCELED)", example = "RECRUITING") ReservationStatus status) {
}
