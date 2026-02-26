package com.dnd.jjigeojulge.reservation.application.dto.query;

import java.time.LocalDateTime;

import com.dnd.jjigeojulge.user.domain.AgeGroup;
import com.dnd.jjigeojulge.user.domain.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "해당 동행 예약에 지원한 지원자 정보 DTO")
@Builder
public record ApplicantDto(
                @Schema(description = "지원 내역(Application) ID 저장용 (수락/거절 시 활용)", example = "301") Long applicantId,

                @Schema(description = "지원자 유저 ID", example = "15") Long userId,

                @Schema(description = "지원자 닉네임", example = "감자튀김") String nickname,

                @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg") String profileImageUrl,

                @Schema(description = "성별 (MALE, FEMALE)", example = "MALE") Gender gender,

                @Schema(description = "지원자 연령대 (기존 가입자의 경우 null일 수 있음)", nullable = true, example = "TWENTIES") AgeGroup ageGroup,

                @Schema(description = "지원자 한줄 소개 (선택 항목)", nullable = true, example = "사진 찍는 걸 좋아합니다.") String introduction,

                @Schema(description = "지원한 일시", example = "2024-05-12T15:30:00") LocalDateTime appliedAt) {
}
